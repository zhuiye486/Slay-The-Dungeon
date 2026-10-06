import {
  PLACEHOLDER_HINT,
  buildMergedPrompt,
  clearPromptOverride,
  getPromptOverride,
  loadPromptSettings,
  savePromptOverride,
  savePromptSettings,
} from "./prompt.js";
import { CropEditor } from "./crop-editor.js";
import { FramePreview } from "./frame-preview.js";

const state = {
  cards: [],
  meta: null,
  staging: [],
  activeCard: null,
  promptDirty: false,
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

function stripNl(text) {
  return (text || "").replace(/\s*NL\s*/g, " ").trim();
}

function imageUrl(safeId) {
  return `/api/image/card/${safeId}?t=${Date.now()}`;
}

function scheduleCropSave(crop) {
  if (!state.activeCard?.is_staged) return;
  clearTimeout(cropSaveTimer);
  cropSaveTimer = setTimeout(async () => {
    try {
      await api(`/api/staging/${state.activeCard.safe_id}/crop`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(crop),
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
  if (!state.activeCard || !cropEditor?.image) return;
  const source = cropEditor.getSourceCanvas();
  await framePreview.render({
    card: state.activeCard,
    sourceCanvas: source,
    crop: cropEditor.crop,
  });
}

function resizeArtCanvases() {
  const cropWrap = document.querySelector(".crop-canvas-wrap");
  if (cropWrap && cropEditor) {
    cropEditor.resizeCanvas(cropWrap.clientWidth, cropWrap.clientHeight);
  }
}

function bindArtResizeObserver() {
  artResizeObserver?.disconnect();
  const workspace = document.querySelector(".art-workspace");
  if (!workspace) return;
  artResizeObserver = new ResizeObserver(() => {
    resizeArtCanvases();
    updateFramePreview();
  });
  artResizeObserver.observe(workspace);
}

async function loadArtWorkspace(card) {
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
  "upgrade_magic_number", "upgrade_heal", "description", "description_en", "upgrade_description",
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
    upgrade_heal: number("upgrade_heal"), description: value("description"),
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
  $("#stats").innerHTML = `
    <span>卡牌 <strong>${state.meta.total}</strong></span>
    <span>占位图 <strong>${state.meta.placeholder_count}</strong></span>
    <span>暂存 <strong>${state.meta.staged_count}</strong></span>
    <span>建议尺寸 <strong>${state.meta.recommended_size.width}×${state.meta.recommended_size.height}</strong></span>
  `;
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
  renderGrid();
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
    return;
  }
  empty.classList.add("hidden");

  grid.innerHTML = state.cards
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
      <img src="/api/image/card/${e.safe_id}?t=${Date.now()}" alt="">
      <span>${e.name}</span>
    </div>
  `
    )
    .join("");
  list.querySelectorAll(".staging-item").forEach((el) => {
    el.addEventListener("click", () => openModal(el.dataset.safeId));
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
  $("#modal").classList.remove("hidden");
  fillCardEditor(card);
  bindArtResizeObserver();
  await loadArtWorkspace(card);
}

function closeModal() {
  artResizeObserver?.disconnect();
  artResizeObserver = null;
  if (state.activeCard) {
    savePromptOverride(state.activeCard.safe_id, $("#merged-prompt").value);
  }
  $("#modal").classList.add("hidden");
  state.activeCard = null;
  state.promptDirty = false;
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
    if (file && state.activeCard) {
      uploadForCard(state.activeCard.safe_id, file);
    }
  });

  $("#modal-file").addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file && state.activeCard) {
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
    if ($("#modal").classList.contains("hidden") || !state.activeCard) return;

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
      await uploadForCard(state.activeCard.safe_id, file);
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
    await loadStaging();
    showToast(result.message);
  } catch (err) {
    showToast(err.message, true);
  } finally {
    $("#btn-commit-confirm").disabled = false;
  }
}

function bindEvents() {
  ["filter-q", "filter-hero", "filter-type", "filter-placeholder", "filter-staged"].forEach(
    (id) => {
      $(`#${id}`).addEventListener("input", () => loadCards());
      $(`#${id}`).addEventListener("change", () => loadCards());
    }
  );

  $("#btn-rescan").addEventListener("click", async () => {
    await api("/api/rescan", { method: "POST" });
    await loadMeta();
    await loadCards();
    showToast("已重新扫描卡牌索引");
  });

  $("#btn-commit").addEventListener("click", openCommitModal);
  $("#btn-undo-commit").addEventListener("click", async () => {
    if (!window.confirm("确定撤销上次提交的所有卡牌编辑吗？")) return;
    try {
      const result = await api("/api/commit/undo", { method: "POST" });
      showToast(result.message, !result.ok);
      if (result.ok) {
        await loadCards();
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
    if (!state.activeCard) return;
    try {
      await api(`/api/staging/${state.activeCard.safe_id}`, { method: "DELETE" });
      showToast("已撤销暂存");
      closeModal();
      await loadCards();
      await loadStaging();
    } catch (err) {
      showToast(err.message, true);
    }
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
  });
  framePreview = new FramePreview($("#frame-preview-canvas"));
  setupDropZone();
  setupPasteHandler();
  setupCardEditor();
  bindEvents();
  await loadMeta();
  framePreview.applyMeta({
    frameSize: state.meta?.frameSize || 1024,
    frameArtPosition: state.meta?.frameArtPosition || { x: 262, y: 286 },
  });
  await loadCards();
  await loadStaging();
}

init();
