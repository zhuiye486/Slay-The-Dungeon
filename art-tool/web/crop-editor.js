export const OUTPUT_WIDTH = 500;
export const OUTPUT_HEIGHT = 380;
export const CROP_ASPECT = OUTPUT_WIDTH / OUTPUT_HEIGHT;

function midpoint(a, b) {
  return [(a[0] + b[0]) / 2, (a[1] + b[1]) / 2];
}

function pointInQuad(x, y, corners) {
  let inside = false;
  for (let i = 0, j = corners.length - 1; i < corners.length; j = i++) {
    const [xi, yi] = corners[i];
    const [xj, yj] = corners[j];
    const crosses = (yi > y) !== (yj > y)
      && x < ((xj - xi) * (y - yi)) / (yj - yi) + xi;
    if (crosses) inside = !inside;
  }
  return inside;
}

export function defaultCropRect(width, height, aspectRatio = CROP_ASPECT) {
  const imgAspect = width / height;
  let cropW;
  let cropH;
  if (imgAspect > aspectRatio) {
    cropH = height;
    cropW = Math.round(cropH * aspectRatio);
  } else {
    cropW = width;
    cropH = Math.round(cropW / aspectRatio);
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
export function renderArtCanvas(sourceCanvas, crop, outputWidth = OUTPUT_WIDTH, outputHeight = OUTPUT_HEIGHT) {
  const out = document.createElement("canvas");
  out.width = outputWidth;
  out.height = outputHeight;
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
      outputWidth,
      outputHeight
    );
  } else {
    const scale = Math.max(outputWidth / sw, outputHeight / sh);
    const dw = sw * scale;
    const dh = sh * scale;
    const dx = (outputWidth - dw) / 2;
    const dy = (outputHeight - dh) / 2;
    ctx.drawImage(sourceCanvas, 0, 0, sw, sh, dx, dy, dw, dh);
  }
  return out;
}

export class CropEditor {
  constructor(canvas, { onChange, onPaint, onColorPick, aspectRatio = CROP_ASPECT } = {}) {
    this.canvas = canvas;
    this.ctx = canvas.getContext("2d");
    this.onChange = onChange;
    this.onPaint = onPaint;
    this.onColorPick = onColorPick;
    this.aspectRatio = aspectRatio;
    this.image = null;
    this.crop = null;
    this.scale = 1;
    this.offsetX = 0;
    this.offsetY = 0;
    this.dragging = null;
    this._lastPointer = null;
    this.rotation = 0;
    this.tool = "crop";
    this.brushColor = "#ff3b30";
    this.brushSize = 6;
    this.painting = false;
    this.lastPaintPoint = null;
    this.paintCanvas = document.createElement("canvas");
    this.paintCtx = this.paintCanvas.getContext("2d");

    canvas.addEventListener("mousedown", (e) => this.onPointerDown(e));
    canvas.addEventListener("mousemove", (e) => this.onPointerMove(e));
    window.addEventListener("mouseup", () => this.onPointerUp());
    canvas.addEventListener("touchstart", (e) => this.onTouchStart(e), { passive: false });
    canvas.addEventListener("touchmove", (e) => this.onTouchMove(e), { passive: false });
    window.addEventListener("touchend", () => this.onPointerUp());
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
    this.paintCanvas.width = img.naturalWidth;
    this.paintCanvas.height = img.naturalHeight;
    this.paintCtx.clearRect(0, 0, this.paintCanvas.width, this.paintCanvas.height);
    this.crop = defaultCropRect(img.naturalWidth, img.naturalHeight, this.aspectRatio);
    this.rotation = 0;
    this.fitView();
    this.draw();
  }

  setRotation(degrees) {
    this.rotation = ((Number(degrees) % 360) + 360) % 360;
    this.fitView();
    this.draw();
    this.emitChange();
  }

  rotateBy(degrees) {
    this.setRotation(this.rotation + degrees);
  }

  setTool(tool) {
    this.tool = tool;
    this.painting = false;
    this.draw();
  }

  setBrushColor(color) {
    this.brushColor = color;
  }

  setBrushSize(size) {
    this.brushSize = Math.max(1, Number(size) || 1);
  }

  pickColorAt(clientX, clientY) {
    const point = this.screenToImagePoint(clientX, clientY);
    if (!point) return null;
    const [x, y] = point;
    const pixel = this.getSourceCanvas().getContext("2d").getImageData(
      Math.max(0, Math.min(this.image.naturalWidth - 1, Math.floor(x))),
      Math.max(0, Math.min(this.image.naturalHeight - 1, Math.floor(y))),
      1,
      1,
    ).data;
    return `#${[...pixel].slice(0, 3).map(value => value.toString(16).padStart(2, "0")).join("")}`; 
  }

  screenToImagePoint(clientX, clientY) {
    if (!this.image) return null;
    const rect = this.canvas.getBoundingClientRect();
    const [sx, sy] = [
      (clientX - rect.left) * this.canvas.width / rect.width,
      (clientY - rect.top) * this.canvas.height / rect.height,
    ];
    const [ux, uy] = this.screenToImage(sx, sy);
    const radians = -this.rotation * Math.PI / 180;
    const viewWidth = this.image.naturalWidth * Math.abs(Math.cos(radians))
      + this.image.naturalHeight * Math.abs(Math.sin(radians));
    const viewHeight = this.image.naturalWidth * Math.abs(Math.sin(radians))
      + this.image.naturalHeight * Math.abs(Math.cos(radians));
    const cx = this.image.naturalWidth / 2;
    const cy = this.image.naturalHeight / 2;
    const dx = ux - viewWidth / 2;
    const dy = uy - viewHeight / 2;
    return [cx + dx * Math.cos(radians) - dy * Math.sin(radians),
      cy + dx * Math.sin(radians) + dy * Math.cos(radians)];
  }

  imageToScreenPoint(ix, iy) {
    const cx = this.image.naturalWidth / 2;
    const cy = this.image.naturalHeight / 2;
    const radians = this.rotation * Math.PI / 180;
    const viewWidth = this.image.naturalWidth * Math.abs(Math.cos(radians))
      + this.image.naturalHeight * Math.abs(Math.sin(radians));
    const viewHeight = this.image.naturalWidth * Math.abs(Math.sin(radians))
      + this.image.naturalHeight * Math.abs(Math.cos(radians));
    const dx = ix - cx;
    const dy = iy - cy;
    return [this.offsetX + (viewWidth / 2 + dx * Math.cos(radians) - dy * Math.sin(radians)) * this.scale,
      this.offsetY + (viewHeight / 2 + dx * Math.sin(radians) + dy * Math.cos(radians)) * this.scale];
  }

  getCropCorners() {
    return [
      this.imageToScreenPoint(this.crop.x, this.crop.y),
      this.imageToScreenPoint(this.crop.x + this.crop.width, this.crop.y),
      this.imageToScreenPoint(this.crop.x + this.crop.width, this.crop.y + this.crop.height),
      this.imageToScreenPoint(this.crop.x, this.crop.y + this.crop.height),
    ];
  }

  cropScreenBounds() {
    const corners = [
      this.imageToScreenPoint(this.crop.x, this.crop.y),
      this.imageToScreenPoint(this.crop.x + this.crop.width, this.crop.y),
      this.imageToScreenPoint(this.crop.x + this.crop.width, this.crop.y + this.crop.height),
      this.imageToScreenPoint(this.crop.x, this.crop.y + this.crop.height),
    ];
    const xs = corners.map(point => point[0]);
    const ys = corners.map(point => point[1]);
    return { left: Math.min(...xs), top: Math.min(...ys), right: Math.max(...xs), bottom: Math.max(...ys) };
  }

  paintAt(clientX, clientY) {
    const point = this.screenToImagePoint(clientX, clientY);
    if (!point) return;
    const ctx = this.paintCtx;
    const [x, y] = point;
    ctx.beginPath();
    ctx.arc(x, y, this.brushSize / 2, 0, Math.PI * 2); 
    ctx.fillStyle = this.brushColor; 
    ctx.globalCompositeOperation = this.tool === "eraser" ? "destination-out" : "source-over";
    ctx.fill();
    ctx.globalCompositeOperation = "source-over";
    this.draw();
    this.onPaint?.();
  }

  paintLine(from, to) {
    const ctx = this.paintCtx;
    const dx = to[0] - from[0];
    const dy = to[1] - from[1];
    ctx.beginPath();
    ctx.moveTo(from[0], from[1]); 
    ctx.lineTo(to[0], to[1]); 
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.lineWidth = this.brushSize;
    ctx.strokeStyle = this.brushColor;
    ctx.globalCompositeOperation = this.tool === "eraser" ? "destination-out" : "source-over";
    ctx.stroke();
    ctx.globalCompositeOperation = "source-over";
    this.draw();
    this.onPaint?.();
  }

  async persistImage() {
    const source = this.getSourceCanvas();
    const blob = await new Promise(resolve => source.toBlob(resolve, "image/png"));
    if (!blob) throw new Error("无法生成编辑后的 PNG");
    return new File([blob], "edited-power.png", { type: "image/png" });
  }

  async acceptDrawing() {
    const merged = this.getSourceCanvas();
    const image = new Image();
    image.src = merged.toDataURL("image/png");
    await image.decode();
    this.image = image;
    this.paintCtx.clearRect(0, 0, this.paintCanvas.width, this.paintCanvas.height);
    this.draw();
  }

  setCrop(crop) {
    if (!this.image) return;
    this.crop = clampCrop(
      { ...defaultCropRect(this.image.naturalWidth, this.image.naturalHeight, this.aspectRatio), ...crop },
      this.image.naturalWidth,
      this.image.naturalHeight
    );
    this.draw();
  }

  resetCrop() {
    if (!this.image) return;
    this.crop = defaultCropRect(this.image.naturalWidth, this.image.naturalHeight, this.aspectRatio);
    this.draw();
    this.emitChange();
  }

  getSourceCanvas() {
    if (!this.image) return null;
    const c = document.createElement("canvas");
    c.width = this.image.naturalWidth;
    c.height = this.image.naturalHeight;
    const ctx = c.getContext("2d");
    ctx.drawImage(this.image, 0, 0);
    ctx.drawImage(this.paintCanvas, 0, 0);
    return c;
  }

  resizeCanvas(width, height) {
    this.canvas.width = Math.max(1, Math.round(width));
    this.canvas.height = Math.max(1, Math.round(height));
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
    const radians = this.rotation * Math.PI / 180;
    const cos = Math.abs(Math.cos(radians));
    const sin = Math.abs(Math.sin(radians));
    const viewWidth = this.image.naturalWidth * cos + this.image.naturalHeight * sin;
    const viewHeight = this.image.naturalWidth * sin + this.image.naturalHeight * cos;
    this.scale = Math.min(
      (cw - pad * 2) / viewWidth,
      (ch - pad * 2) / viewHeight
    );
    this.offsetX = (cw - viewWidth * this.scale) / 2;
    this.offsetY = (ch - viewHeight * this.scale) / 2;
  }

  imageToScreen(ix, iy) {
    return [ix * this.scale + this.offsetX, iy * this.scale + this.offsetY];
  }

  screenToImage(sx, sy) {
    return [(sx - this.offsetX) / this.scale, (sy - this.offsetY) / this.scale];
  }

  getHandleAt(sx, sy) {
    const corners = this.getCropCorners();
    const r = 8;
    const handles = {
      nw: corners[0], ne: corners[1], sw: corners[3], se: corners[2],
      n: midpoint(corners[0], corners[1]), s: midpoint(corners[2], corners[3]),
      w: midpoint(corners[0], corners[3]), e: midpoint(corners[1], corners[2]),
      move: midpoint(corners[0], corners[2]),
    };
    for (const [name, [hx, hy]] of Object.entries(handles)) {
      if (Math.hypot(sx - hx, sy - hy) <= r + 2) return name;
    }
    if (pointInQuad(sx, sy, corners)) return "move";
    return null;
  }

  onPointerDown(e) {
    if (!this.image) return;
    if (this.tool === "eyedropper") {
      const color = this.pickColorAt(e.clientX, e.clientY);
      if (color) this.onColorPick?.(color);
      e.preventDefault();
      return;
    }
    if (["brush", "eraser"].includes(this.tool)) {
      this.painting = true;
      this.lastPaintPoint = this.screenToImagePoint(e.clientX, e.clientY);
      if (this.tool === "brush") this.paintAt(e.clientX, e.clientY);
      e.preventDefault();
      return;
    }
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
    if (this.painting && ["brush", "eraser"].includes(this.tool)) {
      const point = this.screenToImagePoint(e.clientX, e.clientY);
      if (point && this.lastPaintPoint) this.paintLine(this.lastPaintPoint, point);
      this.lastPaintPoint = point;
      e.preventDefault();
      return;
    }
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

    let [dx, dy] = [
      (sx - this._lastPointer.sx) / this.scale,
      (sy - this._lastPointer.sy) / this.scale,
    ];
    const radians = -this.rotation * Math.PI / 180;
    [dx, dy] = [dx * Math.cos(radians) - dy * Math.sin(radians),
      dx * Math.sin(radians) + dy * Math.cos(radians)];
    this._lastPointer = { sx, sy };

    let { x, y, width, height } = this.crop;
    const minW = 40;
    const minH = minW / this.aspectRatio;

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
      height = width / this.aspectRatio;
      if (this.dragging.includes("n")) {
        y = this.crop.y + this.crop.height - height;
      }
      if (this.dragging.includes("w")) {
        x = this.crop.x + this.crop.width - width;
      }
      width = Math.max(minW, width);
      height = width / this.aspectRatio;
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
    this.painting = false;
    this.lastPaintPoint = null;
  }

  onTouchStart(e) {
    if (!e.touches.length) return;
    const touch = e.touches[0];
    this.onPointerDown({ clientX: touch.clientX, clientY: touch.clientY, preventDefault: () => e.preventDefault() });
  }

  onTouchMove(e) {
    if (!e.touches.length) return;
    const touch = e.touches[0];
    this.onPointerMove({ clientX: touch.clientX, clientY: touch.clientY, preventDefault: () => e.preventDefault() });
  }

  onWheel(e) {
    if (!this.image || !this.getHandleAt(e.offsetX, e.offsetY)) return;
    e.preventDefault();
    const factor = e.deltaY > 0 ? 0.95 : 1.05;
    const cx = this.crop.x + this.crop.width / 2;
    const cy = this.crop.y + this.crop.height / 2;
    let nw = this.crop.width * factor;
    nw = Math.max(40, Math.min(nw, this.image.naturalWidth));
    const nh = nw / this.aspectRatio;
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

    const radians = this.rotation * Math.PI / 180;
    ctx.save();
    ctx.translate(this.offsetX + (this.image.naturalWidth * Math.abs(Math.cos(radians)) + this.image.naturalHeight * Math.abs(Math.sin(radians))) * this.scale / 2,
      this.offsetY + (this.image.naturalHeight * Math.abs(Math.cos(radians)) + this.image.naturalWidth * Math.abs(Math.sin(radians))) * this.scale / 2);
    ctx.rotate(this.rotation * Math.PI / 180);
    ctx.drawImage(this.image, -this.image.naturalWidth * this.scale / 2,
      -this.image.naturalHeight * this.scale / 2,
      this.image.naturalWidth * this.scale,
      this.image.naturalHeight * this.scale);
    ctx.drawImage(this.paintCanvas, -this.image.naturalWidth * this.scale / 2,
      -this.image.naturalHeight * this.scale / 2,
      this.image.naturalWidth * this.scale,
      this.image.naturalHeight * this.scale);
    ctx.restore();

    const corners = this.getCropCorners();
    ctx.save();
    ctx.beginPath();
    ctx.rect(this.offsetX, this.offsetY,
      this.image.naturalWidth * Math.abs(Math.cos(radians)) * this.scale
        + this.image.naturalHeight * Math.abs(Math.sin(radians)) * this.scale,
      this.image.naturalWidth * Math.abs(Math.sin(radians)) * this.scale
        + this.image.naturalHeight * Math.abs(Math.cos(radians)) * this.scale);
    ctx.moveTo(...corners[0]);
    ctx.lineTo(...corners[1]);
    ctx.lineTo(...corners[2]);
    ctx.lineTo(...corners[3]);
    ctx.closePath();
    ctx.fillStyle = "rgba(0,0,0,0.55)";
    ctx.fill("evenodd");
    ctx.restore();

    ctx.strokeStyle = "#4dabf7";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(...corners[0]);
    ctx.lineTo(...corners[1]);
    ctx.lineTo(...corners[2]);
    ctx.lineTo(...corners[3]);
    ctx.closePath();
    ctx.stroke();

    const hs = 7;
    ctx.fillStyle = "#4dabf7";
    corners.forEach(([hx, hy]) => {
      ctx.fillRect(hx - hs / 2, hy - hs / 2, hs, hs);
    });
  }

  emitChange() {
    if (this.onChange && this.crop) {
      this.onChange({ ...this.crop });
    }
  }
}
