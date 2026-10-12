# 卡牌美术工具

面向美术设计师的本地网页工具，用于浏览、替换和提交 Slay The Dungeon 模组的卡牌、能力与遗物图片。

## 快速开始

1. 安装 [Python 3.10+](https://www.python.org/downloads/)
2. 双击 `start.bat`
3. 浏览器会自动打开 `http://127.0.0.1:8765`

对于 macOS 用户：

- 双击 `start.command`（如果无法运行，请先在终端内执行 `chmod +x start.command`）
- 或在终端运行：

```bash
cd art-tool
./start.command
```

## 工作流程

1. **浏览**：网格查看所有卡牌的美术、中文名、效果、职业
2. **筛选**：按职业、类型、占位图、暂存状态过滤
3. **替换**：点击卡牌 → 拖拽或选择 PNG → 进入暂存区（不改动主项目）
4. **提交**：确认暂存列表 → 点击「提交到主项目」
    - 按卡牌类型 mask 裁剪并生成 `500 × 380` 和 `250 × 190` 两个 PNG 到 `src/main/resources/SlayTheDungeonResources/img/cards/`
    - 小图使用 `CardName.png`，大图使用 `CardName_p.png`
    - 更新对应 Java 文件的 `IMG_PATH` 指向小图

遗物页使用与能力图标一致的流程：选择遗物后可拖拽、粘贴或选择 PNG，暂存后可裁剪、旋转、翻转和绘制，提交时会输出 `128 × 128` PNG 到 `src/main/resources/SlayTheDungeonResources/img/relics/`，并更新对应遗物类的 `IMG_PATH`。所有图片变更都可通过「撤销上次提交」恢复。

## 命名规则

提交后每张卡使用独立文件名：

```
SlayTheDungeon:SniperShot  →  SniperShot.png
```

未单独提交过的卡牌仍继续使用占位图的小图 `ItsukiA.png` / `ItsukiS.png` / `ItsukiP.png`，以及对应大图 `ItsukiA_p.png` / `ItsukiS_p.png` / `ItsukiP_p.png`。

## 图片要求

- 格式：**PNG**
- 输入图建议尺寸：**500 × 380**（与杀戮尖塔卡牌比例一致）
- 尺寸或格式不符时**仅提醒**，不阻止暂存或提交
- 提交时按卡牌类型应用 `attack_mask.png`、`skill_mask.png` 或 `power_mask.png`
- 能力和遗物图标建议为 **128 × 128**；尺寸不符时同样仅提醒

## 目录说明

```
art-tool/
├── start.bat           # 一键启动
├── config.json         # 主项目路径与规则配置
├── server/             # Python 后端
├── web/                # 前端界面
└── staging/            # 暂存区
    ├── images/         # 待提交 PNG
    └── manifest.json   # 变更记录
```

## 手动启动（可选）

```bash
cd art-tool
pip install -r requirements.txt
python -m uvicorn server.main:app --host 127.0.0.1 --port 8765
```

## AI 提示词

侧边栏可编辑**基础提示词**（全局风格，自动保存到浏览器）。点击卡牌后在详情中查看：

```
合并提示词 = 基础提示词 + 卡牌信息（按模板生成）
```

- **复制**：一键复制到剪贴板，粘贴到 Midjourney / Stable Diffusion 等
- **重新生成**：按当前模板覆盖手动修改
- 详情中的微调会按卡牌单独保存（浏览器 localStorage）

- 详情页可对照 **角色立绘**（仅职业技能卡）
- **裁剪框**（500×380）可拖动/缩放，与套框预览实时联动
- **mask 合成**后叠加 STS 原版卡框预览
- 提交时始终按 mask 导出精确 **500×380** 和 **250×190** PNG
