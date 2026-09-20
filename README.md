# Slay The Dungeon

一个基于 **ModTheSpire**、**BaseMod** 和 **STSLib** 的《杀戮尖塔》大型模组。项目将《暗黑地牢》的英雄、职业卡牌和压力机制带入《杀戮尖塔》，并提供一个配套的本地卡牌美术管理工具。

## 项目内容

- 自定义角色“英雄们”：一次选择 4 名《暗黑地牢》英雄组成队伍。
- 每名英雄拥有专属初始卡组，并共享同一张自定义卡牌颜色。
- 注册约 90 张自定义卡牌，覆盖普通卡牌、英雄专属卡牌、Virtue 和 Affliction 卡牌。
- 自定义遗物 `PressureIndicator`：受到伤害时积累压力，并在压力达到阈值时加入 Virtue 或 Affliction 卡牌。
- 自定义事件和补丁：在 Neow 事件后触发英雄选择流程。
- 自定义 Action、Power、DynamicVariable 和 Patch，用于实现卡牌与压力系统的特殊效果。
- 支持中文和英文资源目录；当前代码根据游戏语言加载 `ZHS` 或 `ENG` 文本。
- 配套卡牌美术工具可浏览卡牌、上传和裁剪图片，并将图片提交回主项目。

## 运行环境

### 游戏运行环境

- macOS 或 Windows
- Steam 版《Slay the Spire》
- ModTheSpire 3.30.3
- BaseMod 5.56.0
- STSLib 2.12.0
- 游戏版本：2022-12-20

模组元数据见 [ModTheSpire.json](src/main/resources/ModTheSpire.json)。

### 开发环境

- JDK 8 或兼容 Java 8 的 JDK
- Maven
- Python 3.10+（仅运行卡牌美术工具时需要）

## 构建 Java 模组

Maven 依赖直接引用本机 Steam 和 Steam Workshop 中的游戏及 ModTheSpire 组件。默认路径配置在 [pom.xml](pom.xml) 的 `Steam.path` 属性中：

```xml
<Steam.path>/Users/zhuiye/Library/Application Support/Steam/steamapps</Steam.path>
```

如果 Steam 安装位置不同，请先修改该属性，并确认以下文件存在：

```text
common/SlayTheSpire/SlayTheSpire.app/Contents/Resources/desktop-1.0.jar
workshop/content/646570/1605833019/BaseMod.jar
workshop/content/646570/1605060445/ModTheSpire.jar
workshop/content/646570/1609158507/STSLib.jar
```

构建并运行测试：

```bash
mvn test
```

打包模组：

```bash
mvn package
```

`package` 阶段会生成 `target/Slay-The-Dungeon.jar`，并通过 Maven AntRun 插件自动复制到：

```text
<Steam.path>/common/SlayTheSpire/SlayTheSpire.app/Contents/Resources/mods/
```

构建完成后，启动 ModTheSpire，勾选 `BaseMod`、`STSLib` 和 `Slay The Dungeon` 即可加载模组。

## 游戏内容结构

游戏中的核心流程如下：

1. 在角色选择界面选择“英雄们”。
2. 在 Neow 事件后进入英雄选择事件，连续选择 4 名英雄。
3. 根据选择的英雄追加对应的 3 张专属初始卡牌，并合成队伍立绘和死亡立绘。
4. 通过 `PressureIndicator` 记录战斗中受到的伤害，压力达到阈值后向牌组加入特殊卡牌。

主要 Java 包：

```text
src/main/java/io/github/zhuiye486/
├── actions/       自定义游戏行动
├── cards/         基础卡牌、英雄卡牌、Virtue、Affliction
├── characters/    自定义角色和英雄队伍逻辑
├── events/        英雄选择事件及相关补丁
├── helpers/       ID 和通用辅助方法
├── patches/       ModTheSpire 补丁与卡牌标签
├── powers/        自定义能力
├── relics/        自定义遗物
└── variables/     卡牌动态数值
```

模组入口是 [SlayTheDungeon.java](src/main/java/io/github/zhuiye486/SlayTheDungeon.java)。它负责注册卡牌、遗物、角色、动态变量、关键字和本地化文本。

## 资源与本地化

资源位于 `src/main/resources/SlayTheDungeonResources/`：

```text
img/                       卡牌、角色、事件、遗物和 UI 图片
localization/ZHS/          中文卡牌、角色、事件、关键字、能力和遗物文本
localization/ENG/          英文本地化目录
```

卡牌图片路径由各卡牌类中的 `IMG_PATH` 指定。图片修改后重新执行 `mvn package` 即可复制到游戏的 `mods` 目录。

## 卡牌美术工具

`art-tool/` 是一个本地 FastAPI + Uvicorn 网页工具，用于维护卡牌图片：

- 浏览卡牌名称、描述、职业和卡牌类型。
- 按职业、类型、占位图和暂存状态筛选。
- 上传 PNG 到独立暂存区，不会立即修改主项目。
- 预览角色立绘、拖动裁剪框并保存裁剪位置。
- 按卡牌类型套用攻击、技能或能力卡框。
- 提交后生成 `500 × 380` 和 `250 × 190` 两种尺寸，并更新卡牌 Java 文件中的图片路径。

启动方式：

```bash
cd art-tool
chmod +x start.command
./start.command
```

启动脚本会创建 `art-tool/.venv`、安装 [requirements.txt](art-tool/requirements.txt) 中的依赖，并打开 `http://127.0.0.1:8765`。

也可以手动启动：

```bash
cd art-tool
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
python -m uvicorn server.main:app --host 127.0.0.1 --port 8765
```

美术工具的路径、尺寸、占位图和端口配置位于 [art-tool/config.json](art-tool/config.json)。更完整的操作说明见 [art-tool/README.md](art-tool/README.md)。

## 测试

Java 测试位于 `src/test/java/`，当前包含 Maven/JUnit 5 的基础测试。运行：

```bash
mvn test
```

实际游戏行为需要在安装了 ModTheSpire、BaseMod 和 STSLib 的《杀戮尖塔》环境中进行验证。

## 项目状态

项目版本为 `0.1.2`，仍处于持续开发阶段。部分资源和实现仍可能使用占位内容；提交卡牌美术前建议先在美术工具中预览裁剪和卡框效果，再执行提交。

