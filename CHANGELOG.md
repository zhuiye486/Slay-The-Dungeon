# 更新日志 | Changelog

## 0.2.6

### 平衡与修正 | Balance & Fixes
- 修正「敏捷药剂」的额外抽牌判定：现在按玩家的敏捷层数增加抽牌数，而不是读取中毒层数。<br>
  Fixed Toxin Trickery's bonus draw calculation: it now draws cards based on the player's Dexterity instead of Poison.
- 调整「破损钥匙」的费用循环：每回合打出的奇数张牌消耗为 0，偶数张牌消耗增加 1；刷新手牌费用时跳过已排入出牌队列的牌。<br>
  Updated Broken Key's cost cycle: odd-numbered cards played each turn cost 0, while even-numbered cards cost 1 more; queued cards are skipped when refreshing hand costs.
- 修正亮度与压力指示器在数值变化时的遗物说明同步更新。<br>
  Fixed Brightness Indicator and Pressure Indicator tooltips so their descriptions stay in sync when values change.
- 修正亮度概率修正中的浮点计算精度。<br>
  Fixed floating-point precision in the Brightness chance modifier.

### 资源与本地化 | Assets & Localization
- 更新「诱捕」卡牌图片与升级图片，并修正简体中文描述中「靠近点！」和「诱捕」的关键词格式。<br>
  Updated the Captivate card art and upgraded art, and corrected the keyword formatting in the Simplified Chinese descriptions for Come Hither and Captivate.

## 0.2.5

### 新内容与机制 | New Content & Mechanics
- 正式加入18种职业专属遗物；遗物会根据当前队伍自动获得或移除。<br>
  Added 18 class-specific relics, automatically granted or removed to match the current party.
- 调整眩晕机制：目标获得的人工制品层数现在与眩晕层数相同；相关卡牌和效果均已同步。<br>
  Updated the Stun mechanic: targets now gain Artifact equal to their Stun, with related cards and effects updated accordingly.
- 「惩戒」现在会在回合结束造成伤害后降低亮度；「亵渎魔瓶」改变瘟疫的伤害与衰减方式。<br>
  Smite now reduces Brightness after dealing its end-of-turn damage; Blasphemous Vial changes how Plague deals damage and decays.

### 平衡与修正 | Balance & Fixes
- 调整多张卡牌的费用、伤害、格挡、概率、升级效果及目标选择，涉及折磨牌、猎犬、十字军、小丑、火枪手等多种卡牌。<br>
  Rebalanced the costs, damage, Block, chances, upgrades, and targeting of many cards, including Afflictions, Hounds, and cards from the Crusader, Jester, and Musketeer.
- 修正「责罚」的流血数值显示与对已死亡敌人的施加判定；修正自动打出的「圣矛」的队列处理。<br>
  Fixed Punish's displayed Bleed amount and prevented it from applying Bleed to defeated enemies; corrected how automatically played Holy Lances are queued.
- 调整亮度对受到伤害、压力、金币及正面效果触发概率的影响；相关概率效果会随亮度、幸运及遗物修正实时更新。<br>
  Updated how Brightness affects incoming damage, Stress, gold, and positive-effect chances; affected chance-based effects now reflect Brightness, Luck, and relic modifiers.
- 为多张卡牌和持续效果补充对应的攻击动画与音效表现。<br>
  Added appropriate attack animations and sound effects to multiple cards and ongoing effects.

### 资源与本地化 | Assets & Localization
- 补充18种职业专属遗物的中英文名称与说明，并更新亮度、压力及眩晕相关文本。<br>
  Added Chinese and English names and descriptions for the 18 class-specific relics, and updated Brightness, Stress, and Stun text.
- 更新「守护犬」卡牌图片。<br>
  Updated the Guard Dog card art.

## 0.2.4

### 游戏内容与修正 | Gameplay & Fixes
- 修正火枪手卡牌效果：「猎兽铅弹」升级后增加每回合触发次数；「呼叫射击」现在正确选择敌人作为目标。<br>
  Fixed Musketeer card behavior: upgrading Buckshot now increases the number of Attacks it affects each turn, and Call the Shot now correctly targets an enemy.
- 改进组队事件的选择流程，逐次显示选择进度，并在队伍组建完成后提供离开选项。<br>
  Improved the party selection event with step-by-step progress text and a Leave option after the party is assembled.

### 本地化 | Localization
- 新增英文卡牌、角色、关键词、能力和遗物本地化，并补齐组队事件的英文文本。<br>
  Added English localization for cards, the character, keywords, Powers, and Relics, and completed the English text for the party selection event.
- 更新组队事件的简体中文提示与选项；角色名称现在从本地化文本读取。<br>
  Updated the Simplified Chinese party selection prompts and options; the character title is now read from localization.
- 统一关键词别名的小写注册形式，提升卡牌文本中关键词匹配的一致性。<br>
  Normalize keyword aliases to lowercase when registering them, improving keyword matching consistency in card text.
- 修正「猎兽铅弹」的简体中文说明，使其与实际效果一致。<br>
  Corrected the Simplified Chinese description of Buckshot to match its behavior.

## 0.2.3

### 游戏内容与平衡 | Gameplay & Balance
- 调整多个职业卡牌的稀有度、费用、伤害、格挡及升级效果。<br>
  Rebalanced the rarity, costs, damage, Block, and upgrade effects of cards across many classes, including Antiquarian, Arbalest, Bounty Hunter, Crusader, Flagellant, Grave Robber, Hellion, Highwayman, Houndmaster, Jester, Leper, Man-at-Arms, Musketeer, Occultist, Plague Doctor, and Shieldbreaker.
- 更新多张卡牌的机制：「诱捕」根据目标的流血和中毒层数额外施加瘟疫；「靠近点！」改为基于概率的效果；部分眩晕或控场牌现在也会给予目标1层人工制品。<br>
  Updated several card mechanics: Captivate now adds Plague based on the target's Bleed and Poison; Come Hither now uses a chance-based effect; and some stun or control cards now also apply 1 Artifact to their target.
- 调整猎犬卡牌与支援效果：「猎犬骚扰」现在按使用的猎犬逐次消耗，并在回合结束时移除；猎犬支援效果与猎犬生成数量的联动得到修正，部分猎犬牌现在会抽牌。<br>
  Updated Hound cards and support effects: Hounds Hurry now counts down as Hounds are played and expires at the end of the turn; Hound support effects now interact correctly with Hound generation, and some Hound cards now draw cards.
- 修正「自虐」与「偏执」的手牌限制逻辑；「偏执」现在会随机限制另一张手牌，限制在回合结束或该牌弃置时解除。<br>
  Fixed the hand restrictions for Masochistic and Paranoid; Paranoid now randomly locks another card in hand, and the lock ends at turn end or when that card is discarded.
- 修正「压力指示器」的伤害判定与存档状态；即使牌组已有折磨牌，压力达到阈值时仍会生成折磨牌，且压力相关的德行与折磨牌不会被净化。<br>
  Fixed Pressure Indicator damage checks and save persistence; reaching the pressure threshold now adds an Affliction even if the deck already contains one, and pressure-related Virtue and Affliction cards can no longer be purged.
- 移除自定义「打击」牌，并将翻牌事件提供的职业牌改为「铁锹」；修正组合角色使用对应死亡图像的问题。<br>
  Removed the custom Strike card and changed the class card offered by the card-flip event to Shovel; fixed the composed character's death image.

### 资源与本地化 | Assets & Localization
- 替换 Victory1、Victory2、Victory3 占位图为正式胜利过场插画，并校正、提亮篝火休息处的两张角色立绘。<br>
  Replaced the Victory1, Victory2, and Victory3 placeholders with finished victory cutscene artwork, and corrected and brightened both campfire character images.
- 更新多张卡牌图片；修正 Highwayman 与 Houndmaster 的角色图片对应关系，并统一瘟疫医生死亡图像的资源文件名。<br>
  Updated several card images; corrected the Highwayman and Houndmaster character art mappings; and standardized the Plague Doctor death-image filename.
- 更新简体中文卡牌与能力说明，以匹配卡牌机制和数值调整。<br>
  Updated Simplified Chinese card and Power descriptions to match the gameplay and balance changes.

### 工具 | Tools
- 修正美术工具中 Highwayman 与 Houndmaster 的角色资源映射，并更新卡牌图片暂存记录。<br>
  Fixed the Highwayman and Houndmaster asset mappings in the art tool and updated the staged card-art record.

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
