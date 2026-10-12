# 植物大战僵尸（PVZ）

一个基于 **Java Swing** 实现的植物大战僵尸同人游戏，纯 JDK 开发，无第三方依赖。

## 功能特性

- **多账号系统**：支持创建/选择/删除多个账号，账号可设置密码，存档相互独立
- **两种游戏模式**
  - 闯关模式（campaign）：按关卡配置分波次出怪，全部消灭即胜利，通关解锁新植物
  - 无限模式（endless）：僵尸持续生成，难度随波次递增，挑战生存极限
- **经典玩法还原**
  - 种植植物抵御僵尸，收集阳光作为资源（天上掉落 + 向日葵生产）
  - 植物卡片 5 秒冷却机制，阳光不足时置灰提示
  - 铲子铲除植物可返还 50% 阳光
  - 每行一辆小推车，僵尸突破防线时触发清场
  - 开场 "好 → 准备 → 开始" 提示动画
- **养成与收集**
  - 击杀僵尸获得"鸽子"货币
  - 物品栏/图鉴：查看植物、僵尸、道具的图鉴与放大图
  - 拼图小游戏：独立的小游戏玩法
- **本地存档**：玩家进度、货币、解锁状态自动保存到本地

## 植物与僵尸

| 类型 | 已实现 | 配置中（待实现） |
|---|---|---|
| 植物 | 豌豆射手、向日葵 | 坚果墙、寒冰射手、土豆雷、樱桃炸弹 |
| 僵尸 | 普通僵尸、路障僵尸 | 铁桶僵尸 |

> 数值配置集中在 `com.ok.data.GameConfig`，新关卡和波次也在此处添加（同时预留了 `config/*.json` 的配置文件入口，后续可切换为 JSON 加载）。

## 技术栈

- JDK 9+（推荐 JDK 17）
- Java Swing（界面）+ Java2D（渲染）
- 无第三方依赖，无需 Maven/Gradle

## 项目结构

```
src/com/ok/
├── Launcher.java          # 程序入口：初始化数据管理器 → 预加载资源 → 启动主窗口
├── account/               # 多账号：Account、AccountManager、MultiAccountDataProvider
├── data/                  # 数据层：DataManager(IDataProvider)、存档、GameConfig 关卡/数值配置
├── resource/              # ResourceManager(图片预加载/缓存)、AudioManager、images/ 图片资源
├── game/
│   ├── core/              # GameManager(总协调)、GameLoop(独立线程定帧主循环)、
│   │                      WaveManager(波次出怪)、CollisionManager(碰撞)
│   ├── entities/          # GameObject 基类及 Plant/Zombie/Bullet/Sun/Cart 实体
│   ├── plants/            # 具体植物：PeaShooter、Sunflower
│   ├── zombies/           # 具体僵尸：NormalZombie、ConeheadZombie
│   ├── grid/              # GridManager：草坪网格的种植/查询
│   └── systems/           # SunSystem、BulletManager、CartSystem、ShovelSystem 等子系统
├── ui/
│   ├── GameFrame.java     # 主窗口，CardLayout 管理全部界面切换
│   ├── screens/           # 加载页/主菜单/选关/战斗/物品栏/拼图/账号选择
│   ├── components/        # PlantCard、SunCounter、ProgressBar 等组件
│   └── dialogs/           # 胜利/失败/解锁植物/菜单弹窗
└── utils/                 # Constants(全部数值常量)、FileUtil、ImageUtil
```

### 架构要点

- **主循环**：`GameLoop` 独立线程按目标帧率运行，每帧计算 `deltaTime` → `GameManager.update()` 更新逻辑 → 投递到 EDT 重绘
- **每帧更新顺序**：波次出怪 → 天降阳光 → 植物 → 僵尸 → 子弹 → 阳光 → 小推车 → 碰撞检测 → 清理死亡实体 → 判定胜负
- **单例协调**：GameManager / WaveManager / SunSystem 等核心管理器均为单例，实体采用"待移除列表"延迟清理，避免遍历中修改

## 运行方式

1. 用 IntelliJ IDEA 打开 `PVZ` 项目目录（已包含 `PVZ.iml`）
2. 确保 `src` 被标记为 Sources Root（图片资源在 `src/com/ok/resource/images/` 下，随源码一起进入 classpath）
3. 运行 `com.ok.Launcher` 的 `main` 方法

启动流程：账号选择 → 加载页（点击开始）→ 主菜单 → 选关 / 无限模式。

## 操作说明

| 操作 | 说明 |
|---|---|
| 点击阳光 | 收集阳光 |
| 点击植物卡片 → 点击草坪格子 | 种植植物（需阳光足够且卡片冷却完毕） |
| 点击铲子 → 点击植物 | 铲除植物，返还 50% 阳光 |
| 右上角菜单按钮 | 暂停菜单（继续 / 返回主界面） |
| 胜利/失败界面点击任意处 | 返回选关界面（闯关）或主菜单（无限） |

## 已知事项

- 窗口为固定大小、不可缩放
- 存档保存在本地文件，删除账号会同时删除其存档且不可恢复
- 游戏逻辑与界面渲染分属两个线程，向共享实体列表（阳光/僵尸/子弹）添加数据时请注意线程安全，建议使用 `CopyOnWriteArrayList`
