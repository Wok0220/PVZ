package com.ok.game.core;

import com.ok.data.DataManager;
import com.ok.game.entities.Bullet;
import com.ok.game.entities.Plant;
import com.ok.game.entities.Sun;
import com.ok.game.entities.Zombie;
import com.ok.game.grid.GridManager;
import com.ok.game.systems.BulletManager;
import com.ok.game.systems.CartSystem;
import com.ok.game.systems.SunSystem;
import com.ok.ui.screens.GameScreen;
import com.ok.utils.Constants;

import javax.swing.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 游戏管理器
 * 游戏核心管理类，协调所有子系统
 */
public class GameManager implements WaveManager.ZombieSpawnListener {

    /** 单例实例 */
    private static GameManager instance;

    // ==================== 核心组件 ====================

    /** 网格管理器 */
    private GridManager gridManager;

    /** 碰撞检测管理器 */
    private CollisionManager collisionManager;

    /** 波次管理器 */
    private WaveManager waveManager;

    /** 阳光系统 */
    private SunSystem sunSystem;

    /** 子弹管理器 */
    private BulletManager bulletManager;

    /** 小车系统 */
    private CartSystem cartSystem;

    /** 游戏界面引用 */
    private GameScreen gameScreen;

    /** 游戏循环 */
    private GameLoop gameLoop;

    // ==================== 游戏实体列表 ====================

    /** 所有僵尸列表 */
    private List<Zombie> zombies;

    /** 待移除的僵尸列表（防止并发修改） */
    private List<Zombie> zombiesToRemove;

    /** 随机阳光生成计时器 */
    private float randomSunTimer;

    /** 随机数生成器 */
    private Random random;

    private long lastSunSpawnTime = 0;

    /** 是否是第一次生成阳光 */
    private boolean isFirstSunSpawn = true;

    // ==================== 游戏状态 ====================

    /** 游戏模式（"campaign" 闯关 / "endless" 无限） */
    private String gameMode;

    /** 当前关卡索引（闯关模式使用） */
    private int currentLevelIndex;

    /** 游戏是否活跃（未胜利/未失败） */
    private boolean gameActive;

    /** 游戏是否胜利 */
    private boolean gameVictory;

    /** 游戏是否失败 */
    private boolean gameDefeat;

    /** 游戏是否暂停 */
    private boolean isPaused;

    /** 当前阳光数量 */
    private int currentSun;

    // ==================== 统计信息 ====================

    /** 本关击杀僵尸数 */
    private int killsInLevel;

    /** 本关种植植物数 */
    private int plantsPlantedInLevel;

    /**
     * 私有构造函数
     */
    private GameManager() {
        this.gridManager = new GridManager();
        this.collisionManager = CollisionManager.getInstance();
        this.waveManager = WaveManager.getInstance();
        this.sunSystem = SunSystem.getInstance();
        this.bulletManager = BulletManager.getInstance();
        this.cartSystem = CartSystem.getInstance();
        this.gameLoop = GameLoop.getInstance();

        // 设置僵尸生成监听器
        this.waveManager.setZombieSpawnListener(this);

        this.zombies = new CopyOnWriteArrayList<>();
        this.zombiesToRemove = new CopyOnWriteArrayList<>();

        this.gameActive = false;
        this.gameVictory = false;
        this.gameDefeat = false;
        this.isPaused = false;
        this.currentSun = Constants.START_SUN;
        this.killsInLevel = 0;
        this.plantsPlantedInLevel = 0;

        // 初始化随机阳光生成
        this.randomSunTimer = 0;
        this.random = new Random();

        // 初始化游戏循环
        this.gameLoop.init(this);
    }

    /**
     * 获取单例实例
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // ==================== 初始化 ====================

    /**
     * 初始化游戏
     * @param mode 游戏模式（"campaign" / "endless"）
     * @param levelIndex 关卡索引（闯关模式使用）
     * @param gameScreen 游戏界面引用
     */
    public void initGame(String mode, int levelIndex, GameScreen gameScreen) {
        this.gameMode = mode;
        this.currentLevelIndex = levelIndex;
        this.gameScreen = gameScreen;

        // 清空所有数据
        clearAll();

        // 初始化阳光
        this.currentSun = Constants.START_SUN;

        // 重置随机阳光计时器
        this.randomSunTimer = 0;
        // 重置第一次生成标志
        this.isFirstSunSpawn = true;

        // 初始化子系统
        sunSystem.init(this);
        bulletManager.init(this);
        // 新增：初始化网格管理器
        gridManager.init(this);
        // 在 GameManager.initGame 中
        cartSystem.init(gridManager);
        System.out.println("小车系统初始化完成");

        // 初始化波次
        if ("campaign".equals(mode)) {
            waveManager.initCampaign(levelIndex);
        } else {
            waveManager.initEndless();
        }

        // 启动游戏
        this.gameActive = true;
        this.gameVictory = false;
        this.gameDefeat = false;
        this.isPaused = false;
        this.killsInLevel = 0;
        this.plantsPlantedInLevel = 0;

        // 启动游戏循环
        gameLoop.start();
    }

    /**
     * 清空所有游戏数据
     */
    private void clearAll() {
        gridManager.clear();
        zombies.clear();
        zombiesToRemove.clear();
        bulletManager.clear();
        sunSystem.clear();
    }

    // ==================== 更新逻辑 ====================

    /**
     * 更新游戏逻辑
     * @param deltaTime 帧间隔时间（秒）
     */
    public void update(float deltaTime) {
        if (!gameActive || isPaused || gameDefeat) {
            return;
        }

        // 1. 更新波次系统
        waveManager.update(deltaTime);

        // 2. 更新随机阳光生成
        updateRandomSunGeneration(deltaTime);

        // 3. 更新所有植物（通过 GridManager 获取所有植物）
        for (Plant plant : gridManager.getAllPlants()) {
            plant.update(deltaTime);
        }

        // 4. 更新所有僵尸
        for (Zombie zombie : zombies) {
            zombie.update(deltaTime);
        }

        // 5. 更新所有子弹
        bulletManager.update(deltaTime);

        // 6. 更新所有阳光
        sunSystem.update(deltaTime);

        // 7. 更新小车系统
        cartSystem.update(deltaTime, zombies);

        // 8. 碰撞检测
        collisionManager.checkCollisions(this);

        // 9. 处理待移除的实体
        processRemovals();

        // 10. 检查胜负条件
        checkVictory();
        checkDefeat();
    }

    /**
     * 获取指定行和位置的植物
     * @param row 行号
     * @param zombieX 僵尸的x坐标
     * @return 最前面的植物
     */
    public Plant getPlantInRow(int row, int zombieX) {
        return gridManager.getPlantInFront(row, zombieX);
    }

    /**
     * 更新随机阳光生成
     */
    private void updateRandomSunGeneration(float deltaTime) {
        long currentTime = System.currentTimeMillis();

        if (lastSunSpawnTime == 0) {
            lastSunSpawnTime = currentTime;
        }

        // 第一次生成使用 FIRST_SUN_INTERVAL，之后使用 RANDOM_SUN_INTERVAL
        int interval;
        if (isFirstSunSpawn) {
            interval = Constants.FIRST_SUN_INTERVAL;
        } else {
            interval = Constants.RANDOM_SUN_INTERVAL;
        }

        if (currentTime - lastSunSpawnTime >= interval) {
            spawnRandomSun();
            lastSunSpawnTime = currentTime;
            // 第一次生成后，设置标志为false
            isFirstSunSpawn = false;
        }
    }

    /**
     * 生成随机阳光
     */
    private void spawnRandomSun() {
        int x = Constants.SUN_SPAWN_X_MIN + random.nextInt(Constants.SUN_SPAWN_X_MAX - Constants.SUN_SPAWN_X_MIN);
        int startY = Constants.SUN_SPAWN_Y_START;
        int targetY = Constants.SUN_TARGET_Y_MIN + random.nextInt(Constants.SUN_TARGET_Y_MAX - Constants.SUN_TARGET_Y_MIN);

        Sun sun = new Sun(x, startY, targetY,25);

        addSun(sun);
    }

    /**
     * 处理待移除的实体
     */
    private void processRemovals() {
        // 移除僵尸
        zombies.removeAll(zombiesToRemove);
        zombiesToRemove.clear();
    }

    /**
     * 检查胜利条件
     */
    private void checkVictory() {
        if (!gameActive || gameVictory) return;

        // 闯关模式：所有波次完成且没有僵尸
        if ("campaign".equals(gameMode)) {
            if (waveManager.isAllWavesCompleted() && zombies.isEmpty()) {
                onVictory();
            }
        }
        // 无限模式：没有胜利条件，继续游戏
    }

    /**
     * 检查失败条件
     */
    private void checkDefeat() {
        if (!gameActive || gameDefeat) return;

        // 僵尸到达房子
        // 由 Zombie 在到达时调用 onZombieReachHouse()
    }

    /**
     * 胜利处理
     */
    private void onVictory() {
        gameActive = false;
        gameVictory = true;
        gameLoop.stop();

        // 确保在UI线程中显示胜利界面
        SwingUtilities.invokeLater(() -> {
            if (gameScreen != null) {
                gameScreen.showVictoryScreen();
            }
        });
    }

    /**
     * 僵尸到达房子时调用
     */
    public void onZombieReachHouse() {
        onDefeat();
    }

    /**
     * 失败处理
     */
    public void onDefeat() {
        if (!gameActive || gameDefeat) return;

        gameActive = false;
        gameDefeat = true;
        gameLoop.stop();

        // 确保在UI线程中显示失败界面
        SwingUtilities.invokeLater(() -> {
            if (gameScreen != null) {
                gameScreen.showDefeatScreen();
            }
        });
    }

    // ==================== 实体添加 ====================

    /**
     * 添加阳光
     */
    public void addSun(Sun sun) {
        sunSystem.addSun(sun);
    }

    /**
     * 移除僵尸（标记待移除）
     */
    public void removeZombie(Zombie zombie) {
        if (!zombiesToRemove.contains(zombie)) {
            zombiesToRemove.add(zombie);
            killsInLevel++;

            // 每杀死一只僵尸获得一只鸽子 - 使用接口方法自动保存
            DataManager.getInstance().getProvider().addPigeons(1);
        }
    }

    /**
     * 移除子弹
     */
    public void removeBullet(Bullet bullet) {
        bulletManager.removeBullet(bullet);
    }

    /**
     * 移除植物
     * @param plant 要移除的植物
     */
    public void removePlant(Plant plant) {
        if (plant != null && gridManager != null) {
            gridManager.removePlant(plant.getRow(), plant.getCol());
        }
    }

    // ==================== 阳光系统 ====================

    /**
     * 增加阳光
     */
    public void addSunAmount(int amount) {
        currentSun += amount;
        // 通知游戏界面更新阳光显示
        if (gameScreen != null) {
            gameScreen.setSunAmount(currentSun);
        }
    }

    /**
     * 减少阳光（种植消耗）
     * @return 是否成功（阳光不足返回false）
     */
    public boolean spendSun(int amount) {
        if (currentSun >= amount) {
            currentSun -= amount;
            // 通知游戏界面更新阳光显示
            if (gameScreen != null) {
                gameScreen.setSunAmount(currentSun);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取当前阳光数量
     */
    public int getCurrentSun() {
        return currentSun;
    }

    // ==================== 植物种植 ====================

    /**
     * 尝试种植植物
     * @param plant 植物对象
     * @param row 网格行
     * @param col 网格列
     * @return 是否种植成功
     */
    public boolean tryPlant(Plant plant, int row, int col) {
        // 检查阳光是否足够
        if (currentSun < plant.getSunCost()) {
            return false;
        }

        // 尝试种植到网格
        if (gridManager.plant(plant, row, col)) {
            // 扣除阳光
            spendSun(plant.getSunCost());
            plantsPlantedInLevel++;
            return true;
        }

        return false;
    }

    /**
     * 铲除植物
     * @param row 网格行
     * @param col 网格列
     * @return 是否铲除成功
     */
    public boolean shovelPlant(int row, int col) {
        Plant plant = gridManager.removePlant(row, col);
        if (plant != null) {
            // 返还阳光（消耗的50%）
            int refund = plant.getSunCost() * Constants.SHOVEL_REFUND_RATIO / 100;
            addSunAmount(refund);
            return true;
        }
        return false;
    }

    // ==================== 子弹系统 ====================

    /**
     * 发射子弹
     */
    public void shootBullet(Bullet bullet) {
        bulletManager.addBullet(bullet);
    }

    /**
     * 获取所有子弹
     */
    public List<Bullet> getBullets() {
        return bulletManager.getBullets();
    }

    /**
     * 获取所有僵尸
     */
    public List<Zombie> getZombies() {
        return zombies;
    }

    // ==================== 请求重绘 ====================

    /**
     * 请求重绘界面
     */
    public void requestRepaint() {
        if (gameScreen != null) {
            gameScreen.repaint();
        }
    }

    // ==================== Getters ====================

    public boolean isGameActive() {
        return gameActive && !isPaused && !gameVictory && !gameDefeat;
    }
    public GridManager getGridManager() {
        return gridManager;
    }
    public CartSystem getCartSystem() {
        return cartSystem;
    }
    public Plant[] getPlants() {
        return null;
    }

    // ==================== 僵尸生成监听器 ====================

    @Override
    public void onZombieSpawned(Zombie zombie) {
        // 添加僵尸到游戏中
        zombies.add(zombie);
    }
    /**
     * 检查指定行是否有僵尸在植物前方
     */
    public boolean hasZombieInFront(int row, int plantX) {
        for (Zombie zombie : zombies) {
            if (zombie.getRow() == row && zombie.isAlive() && zombie.getX() > plantX) {
                return true;
            }
        }
        return false;
    }
}