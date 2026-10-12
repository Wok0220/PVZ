package com.ok.game.core;

import com.ok.data.GameConfig;
import com.ok.game.entities.Zombie;
import com.ok.game.zombies.*;
import com.ok.utils.Constants;

import java.util.*;

/**
 * 波次管理器
 * 负责僵尸波次的生成和管理
 */
public class WaveManager {

    /** 单例实例 */
    private static WaveManager instance;

    /** 僵尸生成监听器 */
    private ZombieSpawnListener zombieSpawnListener;

    /** 游戏配置引用 */
    private GameConfig gameConfig;

    /** 当前关卡配置（闯关模式） */
    private GameConfig.LevelConfig currentLevelConfig;

    /** 当前波次索引 */
    private int currentWaveIndex;

    /** 当前波次内已生成的僵尸数量 */
    private int spawnedInWave;

    /** 当前波次的僵尸生成列表 */
    private List<GameConfig.ZombieSpawn> currentWaveSpawns;

    /** 下一个僵尸生成的延迟计时器（毫秒） */
    private int nextSpawnTimer;

    /** 波次间隔计时器（毫秒） */
    private int waveIntervalTimer;

    /** 是否在波次间隔中 */
    private boolean isBetweenWaves;

    /** 所有波次是否已完成 */
    private boolean allWavesCompleted;

    /** 开场延迟计时器（毫秒），开场动画播放期间不出怪 */
    private int openingDelayTimer;

    /** 开场动画时长（与 GameScreen.drawStartHint 的 3.5 秒对应） */
    private static final int OPENING_DELAY = 3500;

    // ==================== 无限模式相关 ====================

    /** 是否无限模式 */
    private boolean isEndlessMode;

    /** 当前波次编号（无限模式） */
    private int endlessWaveNumber;

    /** 当前难度系数 */
    private double currentDifficulty;

    /** 无限模式基础僵尸生成间隔 */
    private int endlessBaseSpawnInterval;

    /** 无限模式每波僵尸数量基数 */
    private int endlessBaseZombieCount;

    /** 无限模式僵尸生成计时器（毫秒） */
    private long endlessSpawnTimer;

    /** 无限模式僵尸生成间隔（毫秒） */
    private static final long ENDLESS_SPAWN_INTERVAL = 10000;

    /** 无限模式下一个生成时间（毫秒） */
    private long nextEndlessSpawnTime;

    // ==================== 随机数 ====================

    /** 随机数生成器 */
    private Random random;

    /**
     * 僵尸生成监听器接口
     */
    public interface ZombieSpawnListener {
        void onZombieSpawned(Zombie zombie);
    }

    /**
     * 私有构造函数
     */
    private WaveManager() {
        this.random = new Random();
        this.isBetweenWaves = false;
        this.allWavesCompleted = false;
        this.endlessWaveNumber = 0;
        this.currentDifficulty = Constants.ENDLESS_START_DIFFICULTY;
        this.endlessBaseSpawnInterval = Constants.ZOMBIE_SPAWN_INTERVAL;
        this.endlessBaseZombieCount = 3;
    }

    /**
     * 获取单例实例
     */
    public static WaveManager getInstance() {
        if (instance == null) {
            instance = new WaveManager();
        }
        return instance;
    }

    /**
     * 设置僵尸生成监听器
     */
    public void setZombieSpawnListener(ZombieSpawnListener listener) {
        this.zombieSpawnListener = listener;
    }

    /**
     * 初始化闯关模式
     * @param levelIndex 关卡索引
     */
    public void initCampaign(int levelIndex) {
        this.isEndlessMode = false;
        this.currentWaveIndex = 0;
        this.spawnedInWave = 0;
        this.allWavesCompleted = false;

        // ✅ 加载关卡配置！
        this.gameConfig = GameConfig.getInstance();
        this.currentLevelConfig = gameConfig.getLevelConfig(levelIndex);

        // 加载第一波
        this.openingDelayTimer = OPENING_DELAY;
        loadWave(0);
    }

    /**
     * 初始化无限模式
     */
    public void initEndless() {
        this.isEndlessMode = true;
        this.endlessWaveNumber = 0;
        this.currentWaveIndex = 0;
        this.spawnedInWave = 0;
        this.allWavesCompleted = false;

        // 设置第一次生成时间
        this.openingDelayTimer = OPENING_DELAY;
        this.nextEndlessSpawnTime = System.currentTimeMillis() + 5000; // 5秒后开始

        // 清空波次
        this.currentWaveSpawns = null;
    }

    /**
     * 加载指定波次
     * @param waveIndex 波次索引
     */
    private void loadWave(int waveIndex) {
        if (currentLevelConfig == null) return;

        List<GameConfig.WaveConfig> waves = currentLevelConfig.getWaves();
        if (waveIndex >= waves.size()) {
            // 所有波次完成
            allWavesCompleted = true;
            return;
        }

        GameConfig.WaveConfig wave = waves.get(waveIndex);
        this.currentWaveSpawns = new ArrayList<>();
        for (GameConfig.ZombieSpawn spawn : wave.getSpawns()) {
            int randomRow = random.nextInt(Constants.GRID_ROWS); // 随机行，可重复
            this.currentWaveSpawns.add(new GameConfig.ZombieSpawn(
                    spawn.getZombieType(), randomRow, spawn.getDelay()));
        }
        this.spawnedInWave = 0;
        this.nextSpawnTimer = 0;
        this.isBetweenWaves = false;

        // 如果有立即生成的僵尸（delay=0），立即生成
        spawnImmediateZombies();
    }

    /**
     * 生成立即生成的僵尸（delay=0）
     */
    private void spawnImmediateZombies() {
        if (currentWaveSpawns == null) return;

        Iterator<GameConfig.ZombieSpawn> iterator = currentWaveSpawns.iterator();
        while (iterator.hasNext()) {
            GameConfig.ZombieSpawn spawn = iterator.next();
            if (spawn.getDelay() <= 0) {
                spawnZombie(spawn);
                iterator.remove();
                spawnedInWave++;
            }
        }
    }

    /**
     * 生成无限模式波次
     */
    private void generateEndlessWave() {
        endlessWaveNumber++;

        // 计算难度
        currentDifficulty = Constants.ENDLESS_START_DIFFICULTY +
                (endlessWaveNumber - 1) * Constants.ENDLESS_DIFFICULTY_INCREMENT;

        // 计算本波僵尸数量（随难度增加）
        int zombieCount = (int)(endlessBaseZombieCount + endlessWaveNumber * 0.5 * currentDifficulty);
        if (zombieCount > Constants.ENDLESS_MAX_ZOMBIES) {
            zombieCount = Constants.ENDLESS_MAX_ZOMBIES;
        }

        // 生成僵尸列表
        currentWaveSpawns = new ArrayList<>();
        for (int i = 0; i < zombieCount; i++) {
            String zombieType = getRandomZombieTypeForEndless();
            int row = random.nextInt(Constants.GRID_ROWS);
            int delay = i * (int)(endlessBaseSpawnInterval / currentDifficulty);
            currentWaveSpawns.add(new GameConfig.ZombieSpawn(zombieType, row, delay));
        }

        this.spawnedInWave = 0;
        this.nextSpawnTimer = 0;
        this.isBetweenWaves = false;

        // 生成立即生成的僵尸
        spawnImmediateZombies();
    }

    /**
     * 获取无限模式的随机僵尸类型
     */
    private String getRandomZombieTypeForEndless() {
        // 根据难度选择僵尸类型
        List<GameConfig.ZombieConfig> availableZombies = gameConfig.getZombiesByLevel(endlessWaveNumber);

        if (availableZombies.isEmpty()) {
            return "NormalZombie";
        }

        // 根据难度权重随机选择
        double totalWeight = 0;
        for (GameConfig.ZombieConfig zombie : availableZombies) {
            totalWeight += zombie.getDifficultyWeight();
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (GameConfig.ZombieConfig zombie : availableZombies) {
            cumulative += zombie.getDifficultyWeight();
            if (randomValue <= cumulative) {
                return zombie.getId();
            }
        }

        return "NormalZombie";
    }

    /**
     * 生成单个僵尸
     * @param spawn 僵尸生成配置
     */
    private void spawnZombie(GameConfig.ZombieSpawn spawn) {
        // 计算僵尸起始X坐标（屏幕右侧）
        int startX = Constants.WINDOW_WIDTH;

        // 计算僵尸Y坐标
        int startY = Constants.GRID_OFFSET_Y + spawn.getRow() * Constants.GRID_HEIGHT - 50;

        // 根据类型创建僵尸
        Zombie zombie = createZombieByType(spawn.getZombieType(), spawn.getRow(), startX, startY);

        if (zombie != null && zombieSpawnListener != null) {
            // 设置游戏管理器
            zombie.setGameManager(GameManager.getInstance());
            zombieSpawnListener.onZombieSpawned(zombie);
        }
    }

    /**
     * 根据类型创建僵尸
     */
    private Zombie createZombieByType(String type, int row, int x, int y) {
        switch (type) {
            case "NormalZombie":
                return new NormalZombie(row, x, y);
            case "ConeheadZombie":
                return new ConeheadZombie(row, x, y);
            default:
                return new NormalZombie(row, x, y);
        }
    }

    // ==================== 更新逻辑 ====================

    /**
     * 更新波次管理器
     * @param deltaTime 帧间隔时间（秒）
     */
    public void update(float deltaTime) {
        // 开场动画播放期间不出怪
        if (openingDelayTimer > 0 ) {
            openingDelayTimer -= deltaTime * 1000 ;
            return ;
        }
        if (allWavesCompleted && !isEndlessMode) return;

        // ==================== 无限模式 ====================
        if (isEndlessMode) {
            long currentTime = System.currentTimeMillis();

            if (currentTime >= nextEndlessSpawnTime) {
                // 随机选一行生成普通僵尸
                int row = random.nextInt(Constants.GRID_ROWS);

                // 生成僵尸
                GameConfig.ZombieSpawn spawn = new GameConfig.ZombieSpawn("NormalZombie", row, 0);
                spawnZombie(spawn);

                // 设置下一个生成时间
                nextEndlessSpawnTime = currentTime + ENDLESS_SPAWN_INTERVAL;
            }
            return;
        }

        // ==================== 闯关模式 ====================
        if (isBetweenWaves) {
            waveIntervalTimer -= deltaTime * 1000;
            if (waveIntervalTimer <= 0) {
                isBetweenWaves = false;
                currentWaveIndex++;
                loadWave(currentWaveIndex); // 加载下一波
            }
            return;
        }

        if (currentWaveSpawns != null && !currentWaveSpawns.isEmpty()) {
            nextSpawnTimer -= deltaTime * 1000;

            if (nextSpawnTimer <= 0) {
                GameConfig.ZombieSpawn nextSpawn = currentWaveSpawns.get(0);
                spawnZombie(nextSpawn);
                currentWaveSpawns.remove(0);
                spawnedInWave++;

                if (!currentWaveSpawns.isEmpty()) {
                    // 下一只延迟设置为配置中的 delay
                    nextSpawnTimer = currentWaveSpawns.get(0).getDelay();
                } else {
                    // 波次完成！
                    if (currentLevelConfig != null &&
                            currentWaveIndex < currentLevelConfig.getWaves().size() - 1) {
                        // 还有下一波
                        isBetweenWaves = true;
                        GameConfig.WaveConfig currentWave =
                                currentLevelConfig.getWaves().get(currentWaveIndex);
                        waveIntervalTimer = currentWave.getNextWaveDelay();
                    } else {
                        // 所有波次完成
                        allWavesCompleted = true;
                    }
                }
            }
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 所有波次是否已完成
     */
    public boolean isAllWavesCompleted() {
        return allWavesCompleted;
    }

    /**
     * 获取当前进度（0-1）
     */
    public float getProgress() {
        if (isEndlessMode) {
            return 0; // 无限模式无进度
        }

        if (currentLevelConfig == null) return 0;

        int totalWaves = currentLevelConfig.getWaves().size();
        if (totalWaves == 0) return 0;

        return (float) currentWaveIndex / totalWaves;
    }

    /**
     * 获取当前波次编号
     */
    public int getCurrentWave() {
        if (isEndlessMode) {
            return endlessWaveNumber;
        }
        return currentWaveIndex + 1;
    }

    /**
     * 获取总波次（闯关模式）
     */
    public int getTotalWaves() {
        if (isEndlessMode) {
            return -1; // 无限模式无上限
        }
        if (currentLevelConfig == null) return 0;
        return currentLevelConfig.getWaves().size();
    }

    /**
     * 获取当前难度系数（无限模式）
     */
    public double getCurrentDifficulty() {
        return currentDifficulty;
    }

    /**
     * 获取关卡通关奖励植物
     */
    public String getLevelReward() {
        if (currentLevelConfig != null) {
            return currentLevelConfig.getUnlockReward();
        }
        return null;
    }

    /**
     * 重置波次管理器
     */
    public void reset() {
        this.currentWaveIndex = 0;
        this.spawnedInWave = 0;
        this.nextSpawnTimer = 0;
        this.waveIntervalTimer = 0;
        this.isBetweenWaves = false;
        this.allWavesCompleted = false;
        this.currentWaveSpawns = null;

        if (isEndlessMode) {
            this.endlessWaveNumber = 0;
            this.currentDifficulty = Constants.ENDLESS_START_DIFFICULTY;
        }
    }
}