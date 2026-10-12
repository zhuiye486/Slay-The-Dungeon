# 更新日志 | Changelog

## 0.2.2

### 游戏内容与平衡 | Gameplay & Balance
- 调整多张卡牌的费用、伤害、格挡、治疗及升级效果，包括惩戒、忍耐、圣水和腺素飙升等卡牌。  
  Adjusted the costs, damage, Block, healing, and upgrade effects of several cards, including Smite, Endure, Holy Water, and Adrenaline Rush.
- 调整卡牌效果：「撕扯」额外施加瘟疫；升级后的「责罚」额外施加流血；「怒吼」和「指挥」的升级效果有所调整。  
  Updated several card effects: Rake now also applies Plague; upgraded Punish applies additional Bleed; and the upgrade effects of Bellow and Command have been adjusted.
- 修正「从冥界而来」与主宰现实的交互：生成的非诅咒、非状态牌会升级，且费用为 0。  
  Fixed the interaction between From Beyond and Master Reality: generated cards that are not Curses or Statuses are upgraded and cost 0.
- 「吼叫」效果改为在回合开始时触发，并修正相关说明。  
  Roar effects now trigger at the start of the turn; related descriptions have been updated.
- 修正「逃跑」结束回合的处理方式。  
  Fixed how Escape ends the turn.

### 资源与本地化 | Assets & Localization
- 更新多张卡牌图片，并修正卡牌图片路径大小写不一致的问题。  
  Updated several card images and corrected mismatches in card image path capitalization.
- 为多种能力和遗物使用对应图标；新增亮度指示器与压力指示器的遗物图片。  
  Added the appropriate icons for several Powers and Relics, including new images for Brightness Indicator and Pressure Indicator.
- 修正并补充简体中文卡牌、事件、关键词、能力和遗物说明。  
  Corrected and expanded Simplified Chinese descriptions for cards, events, keywords, Powers, and Relics.

### 工具与构建 | Tools & Build
- 扩展卡牌美术工具，支持遗物图片的暂存、裁剪、变换、绘制、提交与撤销。  
  Extended the card art tool with staging, cropping, transforming, drawing, committing, and undo support for Relic images.
- 改进 Maven 构建配置，支持 Windows 和 macOS 的 Steam 游戏目录，并更新相关构建说明。  
  Improved the Maven build configuration to support Steam game directories on Windows and macOS, and updated the related build instructions.
