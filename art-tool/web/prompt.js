/** 提示词模板与合并逻辑 */

export const STORAGE_KEYS = {
  basePrompt: "art-tool-base-prompt",
  cardTemplate: "art-tool-card-template",
  promptOverrides: "art-tool-prompt-overrides",
};

export const DEFAULT_BASE_PROMPT = `Slay the Spire style card illustration, dark fantasy, Darkest Dungeon inspired, dramatic lighting, painterly brushwork, vertical card composition, detailed foreground, moody atmosphere, no text, no UI frame, no watermark`;

export const DEFAULT_CARD_TEMPLATE = `Card: {{name}} ({{class_name}})
Hero/Theme: {{hero}} · {{card_type_zhs}} · Cost {{cost}}
Scene: {{description_clean}}`;

export const PLACEHOLDER_HINT = "{{name}} {{class_name}} {{hero}} {{card_type}} {{card_type_zhs}} {{description}} {{description_clean}} {{cost}} {{id}}";

export function cleanDescription(text) {
  return (text || "")
    .replace(/\s*NL\s*/g, " ")
    .replace(/\[[^\]]*\]/g, "")
    .replace(/\*([^*]+)\*/g, "$1")
    .replace(/![A-Za-z0-9]+!/g, "")
    .replace(/slaythedungeon:\S+/g, "")
    .replace(/\s+/g, " ")
    .trim();
}

export function cardVariables(card) {
  return {
    name: card.name || "",
    class_name: card.class_name || "",
    hero: card.hero || "",
    card_type: card.card_type || "",
    card_type_zhs: card.card_type_zhs || "",
    description: card.description || "",
    description_clean: cleanDescription(card.description),
    cost: card.cost ?? "—",
    id: card.id || "",
  };
}

export function applyTemplate(template, vars) {
  return (template || "").replace(/\{\{(\w+)\}\}/g, (_, key) => vars[key] ?? "");
}

export function buildCardPrompt(card, cardTemplate) {
  return applyTemplate(cardTemplate || DEFAULT_CARD_TEMPLATE, cardVariables(card)).trim();
}

export function mergePrompts(basePrompt, cardPrompt) {
  const base = (basePrompt || "").trim();
  const card = (cardPrompt || "").trim();
  if (!base) return card;
  if (!card) return base;
  return `${base}\n\n${card}`;
}

export function buildMergedPrompt(card, basePrompt, cardTemplate) {
  return mergePrompts(basePrompt, buildCardPrompt(card, cardTemplate));
}

export function loadPromptSettings() {
  return {
    basePrompt: localStorage.getItem(STORAGE_KEYS.basePrompt) ?? DEFAULT_BASE_PROMPT,
    cardTemplate: localStorage.getItem(STORAGE_KEYS.cardTemplate) ?? DEFAULT_CARD_TEMPLATE,
  };
}

export function savePromptSettings(basePrompt, cardTemplate) {
  localStorage.setItem(STORAGE_KEYS.basePrompt, basePrompt);
  localStorage.setItem(STORAGE_KEYS.cardTemplate, cardTemplate);
}

export function loadPromptOverrides() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEYS.promptOverrides) || "{}");
  } catch {
    return {};
  }
}

export function savePromptOverride(safeId, text) {
  const overrides = loadPromptOverrides();
  if (!text || !text.trim()) {
    delete overrides[safeId];
  } else {
    overrides[safeId] = text;
  }
  localStorage.setItem(STORAGE_KEYS.promptOverrides, JSON.stringify(overrides));
}

export function clearPromptOverride(safeId) {
  const overrides = loadPromptOverrides();
  delete overrides[safeId];
  localStorage.setItem(STORAGE_KEYS.promptOverrides, JSON.stringify(overrides));
}

export function getPromptOverride(safeId) {
  return loadPromptOverrides()[safeId];
}
