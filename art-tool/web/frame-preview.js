import { OUTPUT_HEIGHT, OUTPUT_WIDTH, renderArtCanvas } from "./crop-editor.js";

const frameCache = new Map();
const maskCache = new Map();

async function loadImage(url, cache) {
  if (cache.has(url)) return cache.get(url);
  const img = new Image();
  img.crossOrigin = "anonymous";
  await new Promise((resolve, reject) => {
    img.onload = resolve;
    img.onerror = reject;
    img.src = url;
  });
  cache.set(url, img);
  return img;
}

function frameTypeForCard(cardType) {
  const map = {
    ATTACK: "attack",
    SKILL: "skill",
    POWER: "power",
    CURSE: "skill",
    STATUS: "skill",
  };
  return map[cardType] || "skill";
}

function simplifyDescription(text) {
  return (text || "")
    .replace(/\s*NL\s*/g, "\n")
    .replace(/\[[^\]]*\]/g, "")
    .replace(/\*([^*]+)\*/g, "$1")
    .replace(/![A-Za-z0-9]+!/g, "?")
    .replace(/slaythedungeon:\S+/g, "")
    .trim();
}

function formatCost(cost) {
  if (cost === null || cost === undefined) return "";
  if (cost < 0) return "X";
  return String(cost);
}

/**
 * 美术图与 mask 做 alpha 交叉（destination-in）
 */
function applyMask(artCanvas, maskImg) {
  const out = document.createElement("canvas");
  out.width = OUTPUT_WIDTH;
  out.height = OUTPUT_HEIGHT;
  const ctx = out.getContext("2d");
  ctx.drawImage(artCanvas, 0, 0);
  ctx.globalCompositeOperation = "destination-in";
  ctx.drawImage(maskImg, 0, 0, OUTPUT_WIDTH, OUTPUT_HEIGHT);
  ctx.globalCompositeOperation = "source-over";
  return out;
}

export class FramePreview {
  constructor(canvas, meta = {}) {
    this.canvas = canvas;
    this.ctx = canvas.getContext("2d");
    this.displayScale = 0.42;
    this.applyMeta(meta);
  }

  applyMeta(meta) {
    this.meta = meta;
    this.frameSize = meta.frameSize || 1024;
    this.artPos = meta.frameArtPosition || { x: 262, y: 286 };
  }

  resizeToFit() {
    const wrap = this.canvas.parentElement;
    const maxW = wrap?.clientWidth || 480;
    const maxH = wrap?.clientHeight || maxW;
    const size = Math.round(Math.min(maxW, maxH, 560));
    this.displayScale = size / this.frameSize;
    this.canvas.width = size;
    this.canvas.height = size;
  }

  async render({ card, sourceCanvas, crop }) {
    if (!card || !sourceCanvas) return;

    this.resizeToFit();
    const ctx = this.ctx;
    const scale = this.displayScale;
    const frameType = frameTypeForCard(card.card_type);

    const frame = await loadImage(`/assets/frames/${frameType}.png`, frameCache);
    const mask = await loadImage(`/assets/frames/${frameType}_mask.png`, maskCache);

    const artFull = renderArtCanvas(sourceCanvas, crop);
    const artMasked = applyMask(artFull, mask);

    ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    ctx.drawImage(frame, 0, 0, this.canvas.width, this.canvas.height);

    const ax = this.artPos.x * scale;
    const ay = this.artPos.y * scale;
    const aw = OUTPUT_WIDTH * scale;
    const ah = OUTPUT_HEIGHT * scale;
    ctx.drawImage(artMasked, ax, ay, aw, ah);

    this.drawCardText(card, scale);
  }

  drawCardText(card, scale) {
    const ctx = this.ctx;
    const s = scale;

    ctx.fillStyle = "#f1f3f5";
    ctx.textAlign = "center";
    ctx.font = `bold ${Math.round(42 * s)}px serif`;
    ctx.fillText(card.name, 512 * s, 72 * s);

    const cost = formatCost(card.cost);
    if (cost) {
      ctx.beginPath();
      ctx.arc(88 * s, 88 * s, 52 * s, 0, Math.PI * 2);
      ctx.fillStyle = "rgba(20, 24, 32, 0.85)";
      ctx.fill();
      ctx.fillStyle = "#74c0fc";
      ctx.font = `bold ${Math.round(48 * s)}px serif`;
      ctx.fillText(cost, 88 * s, 102 * s);
    }

    const desc = simplifyDescription(card.description);
    const lines = this.wrapText(desc, 880 * s, Math.round(28 * s));
    ctx.fillStyle = "#dee2e6";
    ctx.font = `${Math.round(28 * s)}px "Segoe UI", "Microsoft YaHei", sans-serif`;
    lines.slice(0, 6).forEach((line, i) => {
      ctx.fillText(line, 512 * s, (860 + i * 34) * s);
    });
  }

  wrapText(text, maxWidth, fontSize) {
    const ctx = this.ctx;
    ctx.font = `${fontSize}px "Segoe UI", "Microsoft YaHei", sans-serif`;
    const paragraphs = text.split("\n");
    const lines = [];
    for (const para of paragraphs) {
      let line = "";
      for (const ch of para) {
        const test = line + ch;
        if (ctx.measureText(test).width > maxWidth && line) {
          lines.push(line);
          line = ch;
        } else {
          line = test;
        }
      }
      if (line) lines.push(line);
    }
    return lines;
  }
}
