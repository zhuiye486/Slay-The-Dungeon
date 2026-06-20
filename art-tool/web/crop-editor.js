export const OUTPUT_WIDTH = 500;
export const OUTPUT_HEIGHT = 380;
export const CROP_ASPECT = OUTPUT_WIDTH / OUTPUT_HEIGHT;

export function defaultCropRect(width, height) {
  const imgAspect = width / height;
  let cropW;
  let cropH;
  if (imgAspect > CROP_ASPECT) {
    cropH = height;
    cropW = Math.round(cropH * CROP_ASPECT);
  } else {
    cropW = width;
    cropH = Math.round(cropW / CROP_ASPECT);
  }
  cropW = Math.min(cropW, width);
  cropH = Math.min(cropH, height);
  return {
    x: Math.max(0, Math.floor((width - cropW) / 2)),
    y: Math.max(0, Math.floor((height - cropH) / 2)),
    width: cropW,
    height: cropH,
    applied: false,
    sourceWidth: width,
    sourceHeight: height,
  };
}

export function clampCrop(crop, width, height) {
  let { x, y, width: w, height: h } = crop;
  w = Math.max(20, Math.min(w, width));
  h = Math.max(20, Math.min(h, height));
  x = Math.max(0, Math.min(x, width - w));
  y = Math.max(0, Math.min(y, height - h));
  return {
    ...crop,
    x: Math.round(x),
    y: Math.round(y),
    width: Math.round(w),
    height: Math.round(h),
    sourceWidth: width,
    sourceHeight: height,
  };
}

/**
 * 从源图生成 500×380 预览用 Canvas
 */
export function renderArtCanvas(sourceCanvas, crop) {
  const out = document.createElement("canvas");
  out.width = OUTPUT_WIDTH;
  out.height = OUTPUT_HEIGHT;
  const ctx = out.getContext("2d");
  const sw = sourceCanvas.width;
  const sh = sourceCanvas.height;

  if (crop?.applied) {
    ctx.drawImage(
      sourceCanvas,
      crop.x,
      crop.y,
      crop.width,
      crop.height,
      0,
      0,
      OUTPUT_WIDTH,
      OUTPUT_HEIGHT
    );
  } else {
    const scale = Math.max(OUTPUT_WIDTH / sw, OUTPUT_HEIGHT / sh);
    const dw = sw * scale;
    const dh = sh * scale;
    const dx = (OUTPUT_WIDTH - dw) / 2;
    const dy = (OUTPUT_HEIGHT - dh) / 2;
    ctx.drawImage(sourceCanvas, 0, 0, sw, sh, dx, dy, dw, dh);
  }
  return out;
}

export class CropEditor {
  constructor(canvas, { onChange } = {}) {
    this.canvas = canvas;
    this.ctx = canvas.getContext("2d");
    this.onChange = onChange;
    this.image = null;
    this.crop = null;
    this.scale = 1;
    this.offsetX = 0;
    this.offsetY = 0;
    this.dragging = null;
    this._lastPointer = null;

    canvas.addEventListener("mousedown", (e) => this.onPointerDown(e));
    canvas.addEventListener("mousemove", (e) => this.onPointerMove(e));
    window.addEventListener("mouseup", () => this.onPointerUp());
    canvas.addEventListener("wheel", (e) => this.onWheel(e), { passive: false });
  }

  async loadImage(url) {
    const img = new Image();
    img.crossOrigin = "anonymous";
    await new Promise((resolve, reject) => {
      img.onload = resolve;
      img.onerror = reject;
      img.src = url;
    });
    this.image = img;
    this.crop = defaultCropRect(img.naturalWidth, img.naturalHeight);
    this.fitView();
    this.draw();
  }

  setCrop(crop) {
    if (!this.image) return;
    this.crop = clampCrop(
      { ...defaultCropRect(this.image.naturalWidth, this.image.naturalHeight), ...crop },
      this.image.naturalWidth,
      this.image.naturalHeight
    );
    this.draw();
  }

  resetCrop() {
    if (!this.image) return;
    this.crop = defaultCropRect(this.image.naturalWidth, this.image.naturalHeight);
    this.draw();
    this.emitChange();
  }

  getSourceCanvas() {
    if (!this.image) return null;
    const c = document.createElement("canvas");
    c.width = this.image.naturalWidth;
    c.height = this.image.naturalHeight;
    c.getContext("2d").drawImage(this.image, 0, 0);
    return c;
  }

  resizeCanvas(width, height) {
    this.canvas.width = Math.max(320, Math.round(width));
    this.canvas.height = Math.max(360, Math.round(height));
    if (this.image) {
      this.fitView();
      this.draw();
    }
  }

  fitView() {
    if (!this.image) return;
    const pad = 12;
    const cw = this.canvas.width;
    const ch = this.canvas.height;
    this.scale = Math.min(
      (cw - pad * 2) / this.image.naturalWidth,
      (ch - pad * 2) / this.image.naturalHeight
    );
    this.offsetX = (cw - this.image.naturalWidth * this.scale) / 2;
    this.offsetY = (ch - this.image.naturalHeight * this.scale) / 2;
  }

  imageToScreen(ix, iy) {
    return [ix * this.scale + this.offsetX, iy * this.scale + this.offsetY];
  }

  screenToImage(sx, sy) {
    return [(sx - this.offsetX) / this.scale, (sy - this.offsetY) / this.scale];
  }

  getHandleAt(sx, sy) {
    const [x, y] = this.imageToScreen(this.crop.x, this.crop.y);
    const [x2, y2] = this.imageToScreen(
      this.crop.x + this.crop.width,
      this.crop.y + this.crop.height
    );
    const r = 8;
    const handles = {
      nw: [x, y],
      ne: [x2, y],
      sw: [x, y2],
      se: [x2, y2],
      n: [(x + x2) / 2, y],
      s: [(x + x2) / 2, y2],
      w: [x, (y + y2) / 2],
      e: [x2, (y + y2) / 2],
      move: [(x + x2) / 2, (y + y2) / 2],
    };
    for (const [name, [hx, hy]] of Object.entries(handles)) {
      if (Math.hypot(sx - hx, sy - hy) <= r + 2) return name;
    }
    if (sx >= x && sx <= x2 && sy >= y && sy <= y2) return "move";
    return null;
  }

  onPointerDown(e) {
    if (!this.image) return;
    const rect = this.canvas.getBoundingClientRect();
    const sx = e.clientX - rect.left;
    const sy = e.clientY - rect.top;
    const handle = this.getHandleAt(sx, sy);
    if (!handle) return;
    this.dragging = handle;
    this._lastPointer = { sx, sy };
    e.preventDefault();
  }

  onPointerMove(e) {
    const rect = this.canvas.getBoundingClientRect();
    const sx = e.clientX - rect.left;
    const sy = e.clientY - rect.top;

    if (!this.dragging) {
      const h = this.getHandleAt(sx, sy);
      this.canvas.style.cursor = h
        ? h === "move"
          ? "move"
          : `${h}-resize`
        : "default";
      return;
    }

    const [dx, dy] = [
      (sx - this._lastPointer.sx) / this.scale,
      (sy - this._lastPointer.sy) / this.scale,
    ];
    this._lastPointer = { sx, sy };

    let { x, y, width, height } = this.crop;
    const minW = 40;
    const minH = minW / CROP_ASPECT;

    if (this.dragging === "move") {
      x += dx;
      y += dy;
    } else {
      if (this.dragging.includes("e")) width += dx;
      if (this.dragging.includes("w")) {
        x += dx;
        width -= dx;
      }
      if (this.dragging.includes("s")) height += dy;
      if (this.dragging.includes("n")) {
        y += dy;
        height -= dy;
      }
      height = width / CROP_ASPECT;
      if (this.dragging.includes("n")) {
        y = this.crop.y + this.crop.height - height;
      }
      if (this.dragging.includes("w")) {
        x = this.crop.x + this.crop.width - width;
      }
      width = Math.max(minW, width);
      height = width / CROP_ASPECT;
    }

    this.crop = clampCrop(
      { ...this.crop, x, y, width, height, applied: true },
      this.image.naturalWidth,
      this.image.naturalHeight
    );
    this.draw();
    this.emitChange();
  }

  onPointerUp() {
    this.dragging = null;
  }

  onWheel(e) {
    if (!this.image || !this.getHandleAt(e.offsetX, e.offsetY)) return;
    e.preventDefault();
    const factor = e.deltaY > 0 ? 0.95 : 1.05;
    const cx = this.crop.x + this.crop.width / 2;
    const cy = this.crop.y + this.crop.height / 2;
    let nw = this.crop.width * factor;
    nw = Math.max(40, Math.min(nw, this.image.naturalWidth));
    const nh = nw / CROP_ASPECT;
    this.crop = clampCrop(
      {
        ...this.crop,
        x: cx - nw / 2,
        y: cy - nh / 2,
        width: nw,
        height: nh,
        applied: true,
      },
      this.image.naturalWidth,
      this.image.naturalHeight
    );
    this.draw();
    this.emitChange();
  }

  draw() {
    if (!this.image || !this.crop) return;
    const ctx = this.ctx;
    const cw = this.canvas.width;
    const ch = this.canvas.height;
    ctx.clearRect(0, 0, cw, ch);
    ctx.fillStyle = "#111";
    ctx.fillRect(0, 0, cw, ch);

    ctx.drawImage(
      this.image,
      this.offsetX,
      this.offsetY,
      this.image.naturalWidth * this.scale,
      this.image.naturalHeight * this.scale
    );

    const [x, y] = this.imageToScreen(this.crop.x, this.crop.y);
    const [x2, y2] = this.imageToScreen(
      this.crop.x + this.crop.width,
      this.crop.y + this.crop.height
    );
    const w = x2 - x;
    const h = y2 - y;

    const imgW = this.image.naturalWidth * this.scale;
    const imgH = this.image.naturalHeight * this.scale;
    const imgX = this.offsetX;
    const imgY = this.offsetY;

    ctx.fillStyle = "rgba(0,0,0,0.55)";
    // 仅在裁剪框外侧绘制遮罩
    ctx.fillRect(imgX, imgY, imgW, y - imgY);
    ctx.fillRect(imgX, y + h, imgW, imgY + imgH - (y + h));
    ctx.fillRect(imgX, y, x - imgX, h);
    ctx.fillRect(x + w, y, imgX + imgW - (x + w), h);

    ctx.strokeStyle = "#4dabf7";
    ctx.lineWidth = 2;
    ctx.strokeRect(x, y, w, h);

    const hs = 7;
    ctx.fillStyle = "#4dabf7";
    [
      [x, y],
      [x2, y],
      [x, y2],
      [x2, y2],
    ].forEach(([hx, hy]) => {
      ctx.fillRect(hx - hs / 2, hy - hs / 2, hs, hs);
    });
  }

  emitChange() {
    if (this.onChange && this.crop) {
      this.onChange({ ...this.crop });
    }
  }
}
