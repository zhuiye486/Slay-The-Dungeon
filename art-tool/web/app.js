import {
  PLACEHOLDER_HINT,
  buildMergedPrompt,
  clearPromptOverride,
  getPromptOverride,
  loadPromptSettings,
  savePromptOverride,
  savePromptSettings,
} from "./prompt.js";
import { CropEditor, renderArtCanvas } from "./crop-editor.js?v=20261009-power-crop-frame-3";
import { FramePreview } from "./frame-preview.js";

const state = {
  mode: localStorage.getItem("art-tool-mode") === "powers" ? "powers" : "cards",
  cards: [],
  powers: [],
  cardsPage: 1,
  powersPage: 1,
  cardsPageSize: 24,
  powersPageSize: 12,
  cardsPageRows: 4,
  powersPageRows: 3,
  meta: null,
  staging: [],
  activeCard: null,
  activeAssetType: "card",
  promptDirty: false,
  drawingDirty: false,
};

const $ = (sel) => document.querySelector(sel);

let promptSaveTimer = null;
let cropSaveTimer = null;
let cropEditor = null;
let framePreview = null;
let artResizeObserver = null;
let initialCropSnapshot = null;
let fieldSaveTimer = null;

function cloneCrop(crop) {
  return crop ? { ...crop } : null;
}

function cropsEqual(a, b) {
  if (!a || !b) return a === b;
  return (
    a.x === b.x &&
    a.y === b.y &&
    a.width === b.width &&
    a.height === b.height &&
    Boolean(a.applied) === Boolean(b.applied)
  );
}

function updateUndoCropButton() {
  const btn = $("#btn-undo-crop");
  if (!btn) return;
  const canUndo =
    initialCropSnapshot &&
    cropEditor?.crop &&
    !cropsEqual(cropEditor.crop, initialCropSnapshot);
  btn.disabled = !canUndo;
}

async function api(path, options = {}) {
  const res = await fetch(path, options);
  if (!res.ok) {
    const err = await res.json().catch(() => ({ detail: res.statusText }));
    throw new Error(err.detail || res.statusText);
  }
  return res.json();
}

function showToast(msg, isError = false) {
  const el = $("#toast");
  el.textContent = msg;
  el.style.borderColor = isError ? "var(--danger)" : "var(--border)";
  el.classList.remove("hidden");
  setTimeout(() => el.classList.add("hidden"), 3200);
}

function setMode(mode) {
  state.mode = mode === "powers" ? "powers" : "cards";
  document.body.dataset.mode = state.mode;
  localStorage.setItem("art-tool-mode", state.mode);
  document.querySelectorAll(".mode-tab").forEach((button) => {
    const active = button.dataset.mode === state.mode;
    button.classList.toggle("is-active", active);
    button.setAttribute("aria-selected", String(active));
  });
  renderStats();
  if (state.mode === "powers") loadPowers();
  else loadCards();
  requestAnimationFrame(() => requestAnimationFrame(recalibrateActiveGrid));
}

function stripNl(text) {
  return (text || "").replace(/\s*NL\s*/g, " ").trim();
}

function imageUrl(safeId) {
  return `/api/image/card/${safeId}?t=${Date.now()}`;
}

function powerImageUrl(safeId) {
  return `/api/image/power/${safeId}?t=${Date.now()}`;
}

function pageSizeForGrid(selector, rows, fallback) {
  const grid = $(selector);
  if (!grid.clientWidth) return fallback;
  const columns = getComputedStyle(grid).gridTemplateColumns.split(" ").length;
  return columns * rows;
}

function recalibrateActiveGrid() {
  if (state.mode === "cards") {
    const nextPageSize = pageSizeForGrid("#card-grid", state.cardsPageRows, state.cardsPageSize);
    if (nextPageSize === state.cardsPageSize) return;
    state.cardsPageSize = nextPageSize;
    state.cardsPage = Math.min(state.cardsPage, Math.max(1, Math.ceil(state.cards.length / nextPageSize)));
    renderGrid();
    return;
  }

  const nextPageSize = pageSizeForGrid("#power-grid", state.powersPageRows, state.powersPageSize);
  if (nextPageSize === state.powersPageSize) return;
  state.powersPageSize = nextPageSize;
  state.powersPage = Math.min(state.powersPage, Math.max(1, Math.ceil(state.powers.length / nextPageSize)));
  renderPowerGrid();
}

function scheduleCropSave(crop) {
  if (!state.activeCard?.is_staged && !state.activePower?.staged) return;
  const isPower = state.activeAssetType === "power";
  const safeId = isPower ? state.activePower.safe_id : state.activeCard.safe_id;
  const cropToSave = isPower
    ? {
        ...crop,
        rotation: state.activePower.crop?.rotation || 0,
        flipX: Boolean(state.activePower.crop?.flipX),
      }
    : crop;
  if (isPower) state.activePower.crop = cropToSave;
  clearTimeout(cropSaveTimer);
  cropSaveTimer = setTimeout(async () => {
    try {
      await api(`/api/staging/${safeId}/crop`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cropToSave),
      });
    } catch (err) {
      showToast(`裁剪保存失败：${err.message}`, true);
    }
  }, 300);
}

function updateHeroRef(card) {
  const col = $("#hero-ref-col");
  const workspace = document.querySelector(".art-workspace");
  const url = card.hero_image_url;
  const show = Boolean(url);
  col.classList.toggle("hidden", !show);
  workspace?.classList.toggle("no-hero", !show);
  if (!show) return;
  $("#hero-ref-image").src = `${url}?t=${Date.now()}`;
  $("#hero-ref-image").alt = card.hero || "角色立绘";
}

async function updateFramePreview() {
  if (state.activeAssetType === "power") {
    if (!cropEditor?.image) return;
    const source = cropEditor.getSourceCanvas();
    const square = renderPowerPreview(source, {
      ...cropEditor.crop,
      rotation: cropEditor.rotation,
      flipX: Boolean(state.activePower.crop?.flipX),
    }, 256);
    const preview = $("#power-preview-image");
    preview.src = square.toDataURL("image/png");
    return;
  }
  if (!state.activeCard || !cropEditor?.image) return;
  const source = cropEditor.getSourceCanvas();
  await framePreview.render({
    card: state.activeCard,
    sourceCanvas: source,
    crop: cropEditor.crop,
  });
}

function renderPowerPreview(source, crop, size) {
  const cropped = renderArtCanvas(source, crop, size, size);
  const radians = ((crop?.rotation || 0) % 360) * Math.PI / 180;
  const bound = Math.abs(Math.cos(radians)) + Math.abs(Math.sin(radians));
  const expanded = document.createElement("canvas");
  expanded.width = Math.ceil(size * bound);
  expanded.height = Math.ceil(size * bound);
  const expandedCtx = expanded.getContext("2d");
  expandedCtx.translate(expanded.width / 2, expanded.height / 2);
  expandedCtx.rotate(radians);
  expandedCtx.scale(crop?.flipX ? -1 : 1, 1);
  expandedCtx.drawImage(cropped, -size / 2, -size / 2, size, size);
  const transformed = document.createElement("canvas");
  transformed.width = size;
  transformed.height = size;
  const ctx = transformed.getContext("2d");
  ctx.drawImage(expanded, 0, 0, size, size);
  return transformed;
}

function updatePowerEditButtons() {
  const enabled = Boolean(state.activePower?.staged);
  ["#power-rotation", "#btn-power-flip", "#btn-power-reset"].forEach((selector) => {
    $(selector).disabled = !enabled;
  });
  $("#btn-power-save-drawing").disabled = !state.drawingDirty;
  $("#btn-power-flip").classList.toggle("is-active", Boolean(state.activePower?.crop?.flipX));
  const rotation = cropEditor?.rotation ?? state.activePower?.crop?.rotation ?? 0;
  $("#power-rotation").value = String(Math.round(rotation));
  $("#power-rotation-value").value = `${Math.round(rotation)}°`;
}

async function savePowerTransform(changes) {
  if (!state.activePower?.staged) {
    showToast("先上传能力图片，再进行调整", true);
    return;
  }
  state.activePower.crop = {
    ...cropEditor.crop,
    ...state.activePower.crop,
    ...changes,
  };
  if (Object.hasOwn(changes, "rotation")) cropEditor.setRotation(changes.rotation);
  updatePowerEditButtons();
  await updateFramePreview();
  scheduleCropSave(state.activePower.crop);
}

async function savePowerDrawing() {
  if (!state.activePower || !cropEditor?.image || !state.drawingDirty) return;
  const button = $("#btn-power-save-drawing");
  button.disabled = true;
  button.textContent = "保存中…";
  let imageSaved = false;
  try {
    const file = await cropEditor.persistImage();
    const crop = {
      ...cropEditor.crop,
      ...state.activePower.crop,
      rotation: cropEditor.rotation,
    };
    const form = new FormData();
    form.append("file", file);
    const result = await api(`/api/staging/${state.activePower.safe_id}`, {
      method: "POST",
      body: form,
    });
    await api(`/api/staging/${state.activePower.safe_id}/crop`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(crop),
    });
    state.activePower.crop = crop;
    state.activePower.staged = true;
    imageSaved = true;
    await cropEditor.loadImage(powerImageUrl(state.activePower.safe_id));
    cropEditor.setCrop(crop);
    cropEditor.setRotation(crop.rotation);
    await updateFramePreview();
    state.drawingDirty = false;
    await loadPowers();
    await loadStaging();
    updatePowerEditButtons();
    showToast(`绘图已保存：${result.entry.name}`);
  } catch (err) {
    showToast(imageSaved ? `图片已保存，但刷新预览失败：${err.message}` : `绘图保存失败：${err.message}`, true);
  } finally {
    button.textContent = "保存绘图";
    button.disabled = !state.drawingDirty;
  }
}

function resizeArtCanvases() {
  const cropWrap = document.querySelector(".crop-canvas-wrap");
  if (cropWrap && cropEditor) {
    const rect = cropWrap.getBoundingClientRect();
    cropEditor.resizeCanvas(rect.width, rect.height);
  }
}

function bindArtResizeObserver() {
  artResizeObserver?.disconnect();
  const workspace = document.querySelector(".art-workspace");
  const cropWrap = document.querySelector(".crop-canvas-wrap");
  if (!workspace || !cropWrap) return;
  artResizeObserver = new ResizeObserver(() => {
    resizeArtCanvases();
    updateFramePreview();
  });
  artResizeObserver.observe(workspace);
  artResizeObserver.observe(cropWrap);
}

async function loadArtWorkspace(card) {
  cropEditor.aspectRatio = 500 / 380;
  updateHeroRef(card);
  await cropEditor.loadImage(imageUrl(card.safe_id));
  if (card.crop) {
    cropEditor.setCrop(card.crop);
  }
  initialCropSnapshot = cloneCrop(cropEditor.crop);
  updateUndoCropButton();
  requestAnimationFrame(() => {
    resizeArtCanvases();
    updateFramePreview();
  });
}

async function loadPowerWorkspace(power) {
  cropEditor.aspectRatio = 1;
  cropEditor.setTool("crop");
  $("#hero-ref-col").classList.add("hidden");
  await cropEditor.loadImage(powerImageUrl(power.safe_id));
  if (power.crop) cropEditor.setCrop(power.crop);
  cropEditor.setRotation(power.crop?.rotation || 0);
  cropEditor.setBrushColor($("#power-brush-color").value);
  cropEditor.setBrushSize($("#power-brush-size").value);
  $("#power-draw-tools").classList.remove("hidden");
  $("#btn-power-save-drawing").disabled = true;
  updatePowerEditButtons();
  initialCropSnapshot = cloneCrop(cropEditor.crop);
  updateUndoCropButton();
  await new Promise(resolve => requestAnimationFrame(() => {
    resizeArtCanvases();
    resolve();
  }));
  await updateFramePreview();
}

function getBasePrompt() {
  return $("#base-prompt").value;
}

function getCardTemplate() {
  return $("#card-template").value;
}

function generateMergedPrompt(card) {
  return buildMergedPrompt(card, getBasePrompt(), getCardTemplate());
}

const editorFields = [
  "name_en", "name", "card_type", "rarity", "cost", "damage", "block", "magic_number",
  "magic_number_2", "chance", "chance_2",
  "heal", "tags", "upgrade_cost", "upgrade_damage", "upgrade_block",
  "upgrade_magic_number", "upgrade_magic_number_2", "upgrade_heal", "upgrade_chance",
  "description", "description_en", "upgrade_description",
];

function setEditorValue(id, value) {
  const el = $(`#edit-${id.replaceAll("_", "-")}`);
  if (!el) return;
  el.value = Array.isArray(value) ? value.join(", ") : value ?? "";
}

function fillCardEditor(card) {
  editorFields.forEach((field) => setEditorValue(field, card[field]));
  const warning = $("#editor-warning");
  warning.textContent = card.edit_warnings?.join("；") || "";
  warning.classList.toggle("hidden", Boolean(card.editable));
  $("#editor-status").textContent = card.is_staged ? "已暂存" : "未修改";
  document.querySelectorAll(".card-editor-panel input, .card-editor-panel textarea, .card-editor-panel select")
    .forEach((el) => { el.disabled = !card.editable; });
}

function readCardEditor() {
  const value = (field) => $(`#edit-${field.replaceAll("_", "-")}`).value;
  const number = (field) => value(field) === "" ? null : Number(value(field));
  return {
    name_en: value("name_en"), name: value("name"), card_type: value("card_type"), rarity: value("rarity"),
    cost: number("cost"), damage: number("damage"), block: number("block"), magic_number: number("magic_number"),
    magic_number_2: number("magic_number_2"), chance: number("chance"), chance_2: number("chance_2"),
    heal: number("heal"), tags: value("tags").split(",").map((tag) => tag.trim()).filter(Boolean),
    upgrade_cost: number("upgrade_cost"), upgrade_damage: number("upgrade_damage"),
    upgrade_block: number("upgrade_block"), upgrade_magic_number: number("upgrade_magic_number"),
    upgrade_magic_number_2: number("upgrade_magic_number_2"), upgrade_heal: number("upgrade_heal"),
    upgrade_chance: number("upgrade_chance"), description: value("description"),
    description_en: value("description_en"), upgrade_description: value("upgrade_description"),
  };
}

function applyEditorPreview(changes) {
  if (!state.activeCard) return;
  Object.assign(state.activeCard, changes);
  $("#modal-title").textContent = state.activeCard.name;
  $("#info-cost").textContent = state.activeCard.cost ?? "—";
  $("#info-type").textContent = state.activeCard.card_type;
  $("#info-description").textContent = stripNl(state.activeCard.description);
  updateFramePreview();
  refreshActivePrompt(true);
}

function scheduleFieldSave() {
  if (!state.activeCard?.editable) return;
  const changes = readCardEditor();
  applyEditorPreview(changes);
  clearTimeout(fieldSaveTimer);
  fieldSaveTimer = setTimeout(async () => {
    try {
      const result = await api(`/api/staging/${state.activeCard.safe_id}/fields`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(changes),
      });
      state.activeCard = result.card;
      fillCardEditor(state.activeCard);
      $("#editor-status").textContent = "已暂存";
      await loadCards();
      await loadStaging();
      await updateFramePreview();
    } catch (err) {
      showToast(`属性暂存失败：${err.message}`, true);
    }
  }, 450);
}

function setupCardEditor() {
  document.querySelectorAll(".card-editor-panel input, .card-editor-panel textarea, .card-editor-panel select")
    .forEach((el) => {
      el.addEventListener("input", scheduleFieldSave);
      el.addEventListener("change", scheduleFieldSave);
    });
}

function refreshActivePrompt(forceRegenerate = false) {
  if (!state.activeCard) return;

  const textarea = $("#merged-prompt");
  const override = getPromptOverride(state.activeCard.safe_id);

  if (!forceRegenerate && override !== undefined) {
    textarea.value = override;
  } else {
    const merged = generateMergedPrompt(state.activeCard);
    textarea.value = merged;
    if (forceRegenerate) {
      clearPromptOverride(state.activeCard.safe_id);
    }
  }
  state.promptDirty = false;
}

function schedulePromptSettingsSave() {
  savePromptSettings(getBasePrompt(), getCardTemplate());
  if (!state.activeCard || state.promptDirty) return;
  if (getPromptOverride(state.activeCard.safe_id) !== undefined) return;
  $("#merged-prompt").value = generateMergedPrompt(state.activeCard);
}

function schedulePromptOverrideSave() {
  if (!state.activeCard) return;
  clearTimeout(promptSaveTimer);
  promptSaveTimer = setTimeout(() => {
    savePromptOverride(state.activeCard.safe_id, $("#merged-prompt").value);
  }, 400);
}

function initPromptSettings() {
  const settings = loadPromptSettings();
  $("#base-prompt").value = settings.basePrompt;
  $("#card-template").value = settings.cardTemplate;
  $("#placeholder-hint").textContent = PLACEHOLDER_HINT;

  $("#base-prompt").addEventListener("input", schedulePromptSettingsSave);
  $("#card-template").addEventListener("input", schedulePromptSettingsSave);
}

async function copyPrompt() {
  const text = $("#merged-prompt").value;
  if (!text.trim()) {
    showToast("提示词为空", true);
    return;
  }
  try {
    await navigator.clipboard.writeText(text);
    showToast("已复制到剪贴板");
  } catch {
    $("#merged-prompt").select();
    document.execCommand("copy");
    showToast("已复制到剪贴板");
  }
}

async function loadMeta() {
  state.meta = await api("/api/meta");
  const heroSel = $("#filter-hero");
  const typeSel = $("#filter-type");
  heroSel.innerHTML = '<option value="">全部</option>';
  typeSel.innerHTML = '<option value="">全部</option>';
  state.meta.heroes.forEach((h) => {
    heroSel.innerHTML += `<option value="${h}">${h}</option>`;
  });
  state.meta.types.forEach((t) => {
    typeSel.innerHTML += `<option value="${t}">${t}</option>`;
  });
  renderStats();
}

function renderStats() {
  if (!state.meta) return;
  const assetStats = state.mode === "powers"
    ? `<span>能力 <strong>${state.powers.length}</strong></span><span>占位图 <strong>${state.powers.filter((power) => power.is_placeholder).length}</strong></span>`
    : `<span>卡牌 <strong>${state.meta.total}</strong></span><span>占位图 <strong>${state.meta.placeholder_count}</strong></span><span>建议尺寸 <strong>${state.meta.recommended_size.width}×${state.meta.recommended_size.height}</strong></span>`;
  $("#stats").innerHTML = `${assetStats}<span>暂存 <strong>${state.meta.staged_count}</strong></span>`;
  $("#staged-badge").textContent = state.meta.staged_count;
  $("#staging-count").textContent = state.meta.staged_count;
}

async function loadCards() {
  const params = new URLSearchParams();
  const q = $("#filter-q").value.trim();
  const hero = $("#filter-hero").value;
  const type = $("#filter-type").value;
  const placeholder = $("#filter-placeholder").value;
  const staged = $("#filter-staged").value;
  if (q) params.set("q", q);
  if (hero) params.set("hero", hero);
  if (type) params.set("type", type);
  if (placeholder) params.set("placeholder", placeholder);
  if (staged) params.set("staged", staged);

  const data = await api(`/api/cards?${params}`);
  state.cards = data.cards;
  state.cardsPageSize = pageSizeForGrid("#card-grid", state.cardsPageRows, state.cardsPageSize);
  state.cardsPage = Math.min(state.cardsPage, Math.max(1, Math.ceil(state.cards.length / state.cardsPageSize)));
  renderGrid();
}

async function loadPowers() {
  const data = await api("/api/powers");
  state.powers = data.powers;
  state.powersPageSize = pageSizeForGrid("#power-grid", state.powersPageRows, state.powersPageSize);
  state.powersPage = Math.min(state.powersPage, Math.max(1, Math.ceil(state.powers.length / state.powersPageSize)));
  renderPowerGrid();
  renderStats();
}

function renderPowerGrid() {
  const grid = $("#power-grid");
  const start = (state.powersPage - 1) * state.powersPageSize;
  grid.innerHTML = state.powers.slice(start, start + state.powersPageSize).map((power) => `
    <button type="button" class="power-item ${power.staged ? "is-staged" : ""}" data-safe-id="${power.safe_id}">
      <img src="${powerImageUrl(power.safe_id)}" alt="${power.name}${power.is_placeholder ? "（占位图）" : ""}">
      <span class="power-item-name">${power.name}</span>
      <span class="tags">
        ${power.is_placeholder ? '<span class="tag placeholder">占位图</span>' : ""}
        ${power.staged ? '<span class="tag staged">已暂存</span>' : ""}
      </span>
    </button>
  `).join("");
  grid.querySelectorAll(".power-item").forEach((el) => {
    if (el.dataset.safeId) el.addEventListener("click", () => openPowerModal(el.dataset.safeId));
  });
  renderPagination("#powers-pagination", state.powers.length, state.powersPage, state.powersPageSize, (page) => {
    state.powersPage = page;
    renderPowerGrid();
  });
  requestAnimationFrame(recalibrateActiveGrid);
}

async function loadStaging() {
  const data = await api("/api/staging");
  state.staging = data.entries;
  renderStagingList();
  await loadMeta();
}

function renderGrid() {
  const grid = $("#card-grid");
  const empty = $("#empty-msg");
  if (!state.cards.length) {
    grid.innerHTML = "";
    empty.classList.remove("hidden");
    $("#cards-pagination").classList.add("hidden");
    $("#cards-pagination").innerHTML = "";
    return;
  }
  empty.classList.add("hidden");

  const start = (state.cardsPage - 1) * state.cardsPageSize;
  grid.innerHTML = state.cards.slice(start, start + state.cardsPageSize)
    .map(
      (c) => `
    <article class="card-item ${c.is_staged ? "is-staged" : ""}" data-safe-id="${c.safe_id}">
      <div class="card-thumb">
        <img src="${imageUrl(c.safe_id)}" alt="${c.name}" loading="lazy">
      </div>
      <div class="card-meta">
        <h3>${c.name}</h3>
        <p>${c.hero} · ${c.card_type_zhs}</p>
        <div class="tags">
          ${c.is_placeholder ? '<span class="tag placeholder">占位图</span>' : ""}
          ${c.is_staged ? '<span class="tag staged">已暂存</span>' : ""}
          ${c.shared_count > 1 ? `<span class="tag">共用×${c.shared_count}</span>` : ""}
        </div>
      </div>
    </article>
  `
    )
    .join("");

  grid.querySelectorAll(".card-item").forEach((el) => {
    el.addEventListener("click", () => openModal(el.dataset.safeId));
  });
  renderPagination("#cards-pagination", state.cards.length, state.cardsPage, state.cardsPageSize, (page) => {
    state.cardsPage = page;
    renderGrid();
  });
  requestAnimationFrame(recalibrateActiveGrid);
}

function renderPagination(selector, total, page, pageSize, onPageChange) {
  const container = $(selector);
  const pageCount = Math.max(1, Math.ceil(total / pageSize));
  container.classList.toggle("hidden", pageCount <= 1);
  container.innerHTML = `
    <button type="button" class="btn btn-secondary btn-sm" data-page="${page - 1}" ${page <= 1 ? "disabled" : ""}>上一页</button>
    <span>第 ${page} / ${pageCount} 页 · 共 ${total} 项</span>
    <button type="button" class="btn btn-secondary btn-sm" data-page="${page + 1}" ${page >= pageCount ? "disabled" : ""}>下一页</button>
  `;
  container.querySelectorAll("button[data-page]").forEach((button) => {
    button.addEventListener("click", () => {
      onPageChange(Number(button.dataset.page));
      document.querySelector(selector === "#cards-pagination" ? "#card-grid" : ".power-library")
        .scrollIntoView({ behavior: "smooth", block: "start" });
    });
  });
}

function renderStagingList() {
  const list = $("#staging-list");
  if (!state.staging.length) {
    list.innerHTML = '<p class="hint">暂无暂存</p>';
    return;
  }
  list.innerHTML = state.staging
    .map(
      (e) => `
    <div class="staging-item" data-safe-id="${e.safe_id}">
      <img src="${e.asset_type === "power" ? powerImageUrl(e.safe_id) : imageUrl(e.safe_id)}" alt="">
      <span>${e.name}${e.asset_type === "power" ? " · 能力" : ""}</span>
    </div>
  `
    )
    .join("");
  list.querySelectorAll(".staging-item").forEach((el) => {
    el.addEventListener("click", () => {
      const safeId = el.dataset.safeId;
      if (safeId.startsWith("power__")) openPowerModal(safeId);
      else openModal(safeId);
    });
  });
}

async function openModal(safeId) {
  let card;
  try {
    card = await api(`/api/cards/${safeId}`);
  } catch {
    showToast("无法加载卡牌详情", true);
    return;
  }
  state.activeCard = card;
  state.activePower = null;
  state.activeAssetType = "card";
  state.promptDirty = false;

  $("#modal-title").textContent = card.name;
  $("#info-id").textContent = card.id;
  $("#info-class").textContent = card.class_name;
  $("#info-hero").textContent = card.hero;
  $("#info-type").textContent = card.card_type_zhs;
  $("#info-cost").textContent = card.cost ?? "—";
  $("#info-image").textContent = card.image_file;
  $("#info-target").textContent = `${card.target_image_file} / ${card.target_image_file.replace(/\.png$/, "_p.png")}`;
  $("#info-shared").textContent =
    card.shared_count > 1
      ? `与 ${card.shared_with.length} 张卡共用 ${card.image_file}`
      : "独立图片";
  $("#info-description").textContent = stripNl(card.description);
  fillCardEditor(card);

  const warnings = $("#modal-warnings");
  warnings.innerHTML = "";
  if (card.is_staged && card.staging_warnings?.length) {
    card.staging_warnings.forEach((w) => {
      const li = document.createElement("li");
      li.textContent = w;
      warnings.appendChild(li);
    });
  }

  refreshActivePrompt(false);
  $("#btn-remove-staging").classList.toggle("hidden", !card.is_staged);
  document.body.classList.add("crop-modal-open");
  $("#modal").classList.remove("hidden");
  $("#modal .modal-content").classList.remove("is-power");
  fillCardEditor(card);
  bindArtResizeObserver();
  await loadArtWorkspace(card);
}

async function openPowerModal(safeId) {
  const power = state.powers.find((item) => item.safe_id === safeId);
  if (!power) return;
  const staging = await api("/api/staging");
  const entry = staging.entries.find((item) => item.safe_id === safeId);
  state.activeCard = null;
  state.activePower = { ...power, crop: entry?.crop || null, staged: Boolean(entry) };
  state.drawingDirty = false;
  state.activeAssetType = "power";
  $("#modal-title").textContent = `${power.name} 能力图片`;
  $(".crop-col h3").innerHTML = '原图裁剪 <span class="crop-badge">1:1</span>';
  $("#info-description").textContent = "能力图标裁剪";
  $("#modal .modal-content").classList.add("is-power");
  $("#modal .modal-meta-grid").classList.add("hidden");
  $("#power-preview-col").classList.remove("hidden");
  $("#power-preview-image").src = powerImageUrl(safeId);
  $("#btn-remove-staging").classList.toggle("hidden", !entry);
  updatePowerEditButtons();
  document.body.classList.add("crop-modal-open");
  $("#modal").classList.remove("hidden");
  bindArtResizeObserver();
  await loadPowerWorkspace(state.activePower);
}

function closeModal() {
  if (state.activeAssetType === "power" && state.drawingDirty
      && !window.confirm("绘图尚未保存，确定关闭并丢弃吗？")) return;
  artResizeObserver?.disconnect();
  artResizeObserver = null;
  if (state.activeCard) {
    savePromptOverride(state.activeCard.safe_id, $("#merged-prompt").value);
  }
  $("#modal .modal-content").classList.remove("is-power");
  $("#modal .modal-meta-grid").classList.remove("hidden");
  $("#modal").classList.add("hidden");
  document.body.classList.remove("crop-modal-open");
  state.activeCard = null;
  state.activePower = null;
  state.activeAssetType = "card";
  state.promptDirty = false;
  state.drawingDirty = false;
  initialCropSnapshot = null;
  updateUndoCropButton();
}

async function uploadForCard(safeId, file) {
  const isPng =
    file.type === "image/png" || file.name.toLowerCase().endsWith(".png");
  if (!isPng) {
    showToast("请使用 PNG 图片", true);
    return;
  }
  const form = new FormData();
  form.append("file", file);
  try {
    const result = await api(`/api/staging/${safeId}`, { method: "POST", body: form });
    showToast(`已暂存：${result.entry.name}`);
    await loadCards();
    await loadStaging();
    if (state.activeCard?.safe_id === safeId) {
      const updated = await api(`/api/cards/${safeId}`);
      state.activeCard = updated;
      await openModal(safeId);
    }
  } catch (err) {
    showToast(err.message, true);
  }
}

async function uploadForPower(safeId, file, { keepEdits = false } = {}) {
  const previousCrop = state.activePower?.crop || {};
  const previousRotation = keepEdits ? (previousCrop.rotation || 0) : 0;
  const previousFlipX = keepEdits && Boolean(previousCrop.flipX);
  if (file.type !== "image/png" && !file.name.toLowerCase().endsWith(".png")) {
    showToast("请使用 PNG 图片", true);
    return;
  }
  const form = new FormData();
  form.append("file", file);
  try {
    await api(`/api/staging/${safeId}`, { method: "POST", body: form });
    await loadPowers();
    await loadStaging();
    const entry = (await api("/api/staging")).entries.find((item) => item.safe_id === safeId);
    const crop = {
      ...(entry?.crop || {}),
      ...(keepEdits ? previousCrop : {}),
      rotation: previousRotation,
      flipX: previousFlipX,
    };
    state.activePower = { ...state.activePower, crop, staged: true };
    if (keepEdits || previousRotation || previousFlipX) {
      await api(`/api/staging/${safeId}/crop`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(state.activePower.crop),
      });
    }
    await loadPowerWorkspace(state.activePower);
    updatePowerEditButtons();
    $("#btn-remove-staging").classList.remove("hidden");
    if (!keepEdits) showToast(`已暂存能力图标：${state.activePower.name}`);
  } catch (err) {
    showToast(err.message, true);
    throw err;
  }
}

function setupDropZone() {
  const zone = $("#modal-drop");
  ["dragenter", "dragover"].forEach((ev) => {
    zone.addEventListener(ev, (e) => {
      e.preventDefault();
      zone.classList.add("dragover");
    });
  });
  ["dragleave", "drop"].forEach((ev) => {
    zone.addEventListener(ev, (e) => {
      e.preventDefault();
      zone.classList.remove("dragover");
    });
  });
  zone.addEventListener("drop", (e) => {
    const file = e.dataTransfer.files[0];
    if (file && state.activeAssetType === "power" && state.activePower) {
      uploadForPower(state.activePower.safe_id, file);
    } else if (file && state.activeCard) {
      uploadForCard(state.activeCard.safe_id, file);
    }
  });

  $("#modal-file").addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file && state.activeAssetType === "power" && state.activePower) {
      uploadForPower(state.activePower.safe_id, file);
    } else if (file && state.activeCard) {
      uploadForCard(state.activeCard.safe_id, file);
    }
    e.target.value = "";
  });
}

async function clipboardItemToPngFile(item) {
  const blob = item.getAsFile();
  if (!blob) return null;

  if (blob.type === "image/png") {
    return new File([blob], "pasted.png", { type: "image/png" });
  }

  if (!blob.type.startsWith("image/")) return null;

  try {
    const bitmap = await createImageBitmap(blob);
    const canvas = document.createElement("canvas");
    canvas.width = bitmap.width;
    canvas.height = bitmap.height;
    canvas.getContext("2d").drawImage(bitmap, 0, 0);
    bitmap.close();
    const pngBlob = await new Promise((resolve) => canvas.toBlob(resolve, "image/png"));
    if (!pngBlob) return null;
    return new File([pngBlob], "pasted.png", { type: "image/png" });
  } catch {
    return null;
  }
}

function setupPasteHandler() {
  document.addEventListener("paste", async (e) => {
    if ($("#modal").classList.contains("hidden") || (!state.activeCard && !state.activePower)) return;

    const target = e.target;
    if (target instanceof HTMLTextAreaElement || target instanceof HTMLInputElement) {
      return;
    }

    const items = e.clipboardData?.items;
    if (!items) return;

    for (const item of items) {
      if (!item.type.startsWith("image/")) continue;

      e.preventDefault();
      const file = await clipboardItemToPngFile(item);
      if (!file) {
        showToast("无法读取剪贴板图片", true);
        return;
      }
      if (state.activeAssetType === "power" && state.activePower) {
        await uploadForPower(state.activePower.safe_id, file);
      } else if (state.activeCard) {
        await uploadForCard(state.activeCard.safe_id, file);
      }
      return;
    }
  });
}

async function openCommitModal() {
  await loadStaging();
  if (!state.staging.length) {
    showToast("暂存区为空", true);
    return;
  }
  const list = $("#commit-list");
  list.innerHTML = state.staging
    .map(
      (e) =>
        `<li>${e.name} ${Object.keys(e.field_changes || {}).length ? "· 属性" : ""} ${e.target_image_file ? `→ ${e.target_image_file} / ${e.target_image_file.replace(/\.png$/, "_p.png")}` : ""}</li>`
    )
    .join("");
  $("#commit-result").classList.add("hidden");
  $("#commit-modal").classList.remove("hidden");
}

function closeCommitModal() {
  $("#commit-modal").classList.add("hidden");
}

async function doCommit() {
  $("#btn-commit-confirm").disabled = true;
  try {
    const result = await api("/api/commit", { method: "POST" });
    const box = $("#commit-result");
    box.classList.remove("hidden", "success", "error");
    if (result.errors?.length) {
      box.classList.add("error");
      box.innerHTML = `<p>${result.message}</p><ul>${result.errors
        .map((e) => `<li>${e.safe_id}: ${e.error}</li>`)
        .join("")}</ul>`;
    } else {
      box.classList.add("success");
      box.textContent = result.message;
    }
    await loadCards();
    await loadPowers();
    await loadStaging();
    showToast(result.message);
  } catch (err) {
    showToast(err.message, true);
  } finally {
    $("#btn-commit-confirm").disabled = false;
  }
}

function bindEvents() {
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !$("#modal").classList.contains("hidden")) {
      closeModal();
    }
  });

  document.querySelectorAll(".mode-tab").forEach((button) => {
    button.addEventListener("click", () => setMode(button.dataset.mode));
  });

  ["filter-q", "filter-hero", "filter-type", "filter-placeholder", "filter-staged"].forEach(
    (id) => {
      const resetAndLoadCards = () => {
        state.cardsPage = 1;
        loadCards();
      };
      $(`#${id}`).addEventListener("input", resetAndLoadCards);
      $(`#${id}`).addEventListener("change", resetAndLoadCards);
    }
  );

  $("#btn-rescan").addEventListener("click", async () => {
    const result = await api("/api/rescan", { method: "POST" });
    await loadMeta();
    await loadCards();
    await loadPowers();
    showToast(`扫描完成：${result.card_count} 张卡牌，${result.power_count} 个能力图标`);
  });

  $("#btn-commit").addEventListener("click", openCommitModal);
  $("#btn-undo-commit").addEventListener("click", async () => {
    if (!window.confirm("确定撤销上次提交的所有卡牌编辑吗？")) return;
    try {
      const result = await api("/api/commit/undo", { method: "POST" });
      showToast(result.message, !result.ok);
      if (result.ok) {
        await loadCards();
        await loadPowers();
        await loadStaging();
      }
    } catch (err) {
      showToast(`撤销失败：${err.message}`, true);
    }
  });
  $("#btn-commit-confirm").addEventListener("click", doCommit);
  $("#btn-copy-prompt").addEventListener("click", copyPrompt);
  $("#btn-regen-prompt").addEventListener("click", () => {
    refreshActivePrompt(true);
    showToast("已按模板重新生成");
  });

  $("#btn-reset-crop").addEventListener("click", async () => {
    if (!cropEditor?.image) return;
    cropEditor.resetCrop();
    await updateFramePreview();
    scheduleCropSave(cropEditor.crop);
    updateUndoCropButton();
    showToast("已重置裁剪框");
  });

  $("#btn-undo-crop").addEventListener("click", async () => {
    if (!initialCropSnapshot || !cropEditor?.image) return;
    cropEditor.setCrop(initialCropSnapshot);
    await updateFramePreview();
    scheduleCropSave(cropEditor.crop);
    updateUndoCropButton();
    showToast("已恢复打开时的裁剪");
  });

  $("#power-rotation").addEventListener("input", (event) => {
    const rotation = Number(event.target.value);
    $("#power-rotation-value").value = `${rotation}°`;
    savePowerTransform({ rotation });
  });
  $("#btn-power-flip").addEventListener("click", () => {
    savePowerTransform({ flipX: !state.activePower?.crop?.flipX });
  });
  $("#btn-power-reset").addEventListener("click", () => {
    cropEditor.setRotation(0);
    savePowerTransform({ rotation: 0, flipX: false });
  });
  document.querySelectorAll("[data-power-tool]").forEach((button) => {
    button.addEventListener("click", () => {
      cropEditor.setTool(button.dataset.powerTool);
      setPowerToolButton(button.dataset.powerTool);
      if (button.dataset.powerTool === "eyedropper") showToast("点击原图取色");
    });
  });
  $("#power-brush-color").addEventListener("input", (event) => cropEditor.setBrushColor(event.target.value));
  $("#power-brush-size").addEventListener("input", (event) => cropEditor.setBrushSize(event.target.value));
  $("#btn-power-save-drawing").addEventListener("click", savePowerDrawing);
  $("#merged-prompt").addEventListener("input", () => {
    state.promptDirty = true;
    schedulePromptOverrideSave();
  });

  document.querySelectorAll("[data-close]").forEach((el) => {
    el.addEventListener("click", closeModal);
  });
  document.querySelectorAll("[data-close-commit]").forEach((el) => {
    el.addEventListener("click", closeCommitModal);
  });

  $("#btn-remove-staging").addEventListener("click", async () => {
    if (!state.activeCard && !state.activePower) return;
    const safeId = state.activeAssetType === "power" ? state.activePower.safe_id : state.activeCard.safe_id;
    try {
      await api(`/api/staging/${safeId}`, { method: "DELETE" });
      showToast("已撤销暂存");
      closeModal();
      await loadCards();
      await loadPowers();
      await loadStaging();
    } catch (err) {
      showToast(err.message, true);
    }
  });
}

function setPowerToolButton(tool) {
  document.querySelectorAll("[data-power-tool]").forEach((button) => {
    button.classList.toggle("is-active", button.dataset.powerTool === tool);
  });
}

async function init() {
  initPromptSettings();
  cropEditor = new CropEditor($("#crop-canvas"), {
    onChange: async (crop) => {
      await updateFramePreview();
      scheduleCropSave(crop);
      updateUndoCropButton();
    },
    onPaint: () => {
      state.drawingDirty = true;
      $("#btn-power-save-drawing").disabled = false;
      updateFramePreview();
    },
    onColorPick: (color) => {
      $("#power-brush-color").value = color;
      cropEditor.setBrushColor(color);
      cropEditor.setTool("brush");
      document.querySelectorAll("[data-power-tool]").forEach(button => {
        button.classList.toggle("is-active", button.dataset.powerTool === "brush");
      });
    },
  });
  framePreview = new FramePreview($("#frame-preview-canvas"));
  setupDropZone();
  setupPasteHandler();
  setupCardEditor();
  bindEvents();
  let gridResizeTimer = null;
  const gridResizeObserver = new ResizeObserver(() => {
    clearTimeout(gridResizeTimer);
    gridResizeTimer = setTimeout(() => {
      recalibrateActiveGrid();
    }, 100);
  });
  gridResizeObserver.observe($("#card-grid"));
  gridResizeObserver.observe($("#power-grid"));
  window.addEventListener("resize", () => {
    requestAnimationFrame(() => requestAnimationFrame(recalibrateActiveGrid));
  });
  await loadMeta();
  framePreview.applyMeta({
    frameSize: state.meta?.frameSize || 1024,
    frameArtPosition: state.meta?.frameArtPosition || { x: 262, y: 286 },
  });
  setMode(state.mode);
  await loadCards();
  await loadPowers();
  await loadStaging();
}

init();
