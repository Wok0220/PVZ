package com.ok.data;

import com.ok.utils.FileUtil;
import com.ok.utils.Constants;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏静态配置类
 * 加载和管理植物、僵尸、关卡的静态属性数据
 * 所有账号共用此配置，不随玩家进度改变
 */
public class GameConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 单例实例 */
    private static GameConfig instance;

    // ==================== 配置数据 ====================

    /** 植物配置映射（plantId -> PlantConfig） */
    private Map<String, PlantConfig> plants;

    /** 僵尸配置映射（zombieId -> ZombieConfig） */
    private Map<String, ZombieConfig> zombies;

    /** 关卡配置列表（按顺序） */
    private List<LevelConfig> levels;

    // ==================== 构造函数 ====================

    /**
     * 私有构造函数，使用单例模式
     */
    private GameConfig() {
        plants = new HashMap<>();
        zombies = new HashMap<>();
        levels = new ArrayList<>();
    }

    /**
     * 获取单例实例
     */
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
            instance.loadDefaultConfig();  // 加载默认配置
        }
        return instance;
    }

    /**
     * 重新加载配置（用于热更新或重置）
     */
    public static void reload() {
        instance = null;
        getInstance();
    }

    // ==================== 加载配置 ====================

    /**
     * 加载默认配置（硬编码，后期可改为从JSON文件加载）
     */
    private void loadDefaultConfig() {
        loadPlantsConfig();
        loadZombiesConfig();
        loadLevelsConfig();
    }

    /**
     * 从JSON文件加载配置（后期实现）
     */
    public void loadFromJson(String jsonPath) {
        String json = FileUtil.readResource(jsonPath);
        if (json != null) {
            // 解析JSON并填充配置
            // 这里使用硬编码作为示例，后期可以接入JSON库如Gson或Jackson
            parseJsonConfig(json);
        } else {
            // JSON加载失败，使用默认配置
            System.err.println("加载JSON配置失败，使用默认配置");
            loadDefaultConfig();
        }
    }

    /**
     * 解析JSON配置（后期实现）
     */
    private void parseJsonConfig(String json) {
        // TODO: 接入JSON库后实现
        // 示例：使用Gson解析
        // Gson gson = new Gson();
        // JsonObject root = gson.fromJson(json, JsonObject.class);
        // 解析plants、zombies、levels
        loadDefaultConfig(); // 暂时使用默认
    }

    // ==================== 植物配置 ====================

    /**
     * 加载植物配置
     */
    private void loadPlantsConfig() {
        // 豌豆射手
        plants.put("PeaShooter", new PlantConfig(
                "PeaShooter",
                "豌豆射手",
                100,      // 阳光消耗
                100,      // 生命值
                25,       // 伤害
                1500,     // 攻击间隔（毫秒）
                1,        // 解锁关卡（第一关解锁）
                true,     // 是否攻击型
                false     // 是否生产型
        ));

        // 向日葵
        plants.put("Sunflower", new PlantConfig(
                "Sunflower",
                "向日葵",
                50,       // 阳光消耗
                100,      // 生命值
                0,        // 伤害（不攻击）
                24000,    // 生产阳光间隔（毫秒）
                1,        // 解锁关卡
                false,    // 是否攻击型
                true      // 是否生产型
        ));

        // 坚果墙
        plants.put("WallNut", new PlantConfig(
                "WallNut",
                "坚果墙",
                50,       // 阳光消耗
                400,      // 生命值（高）
                0,        // 伤害
                0,        // 攻击间隔（不攻击）
                2,        // 解锁关卡
                false,
                false
        ));

        // 寒冰射手
        plants.put("SnowPea", new PlantConfig(
                "SnowPea",
                "寒冰射手",
                175,      // 阳光消耗
                100,      // 生命值
                20,       // 伤害（略低）
                1500,     // 攻击间隔
                3,        // 解锁关卡
                true,
                false
        ));

        // 土豆雷
        plants.put("PotatoMine", new PlantConfig(
                "PotatoMine",
                "土豆雷",
                25,       // 阳光消耗
                100,      // 生命值
                1800,     // 伤害（秒杀）
                10000,    // 准备时间（毫秒）
                4,        // 解锁关卡
                true,
                false
        ));

        // 樱桃炸弹
        plants.put("CherryBomb", new PlantConfig(
                "CherryBomb",
                "樱桃炸弹",
                150,      // 阳光消耗
                100,      // 生命值
                1800,     // 伤害（范围秒杀）
                0,        // 一次性使用
                5,        // 解锁关卡
                true,
                false
        ));
    }

    /**
     * 获取植物配置
     */
    public PlantConfig getPlantConfig(String plantId) {
        return plants.get(plantId);
    }

    /**
     * 获取所有植物配置
     */
    public Map<String, PlantConfig> getAllPlants() {
        return new HashMap<>(plants);
    }

    /**
     * 获取指定解锁关卡前的植物列表
     */
    public List<PlantConfig> getPlantsUnlockedByLevel(int levelIndex) {
        return plants.values().stream()
                .filter(p -> p.getUnlockLevel() <= levelIndex + 1)
                .collect(Collectors.toList());
    }

    // ==================== 僵尸配置 ====================

    /**
     * 加载僵尸配置
     */
    private void loadZombiesConfig() {
        // 普通僵尸
        zombies.put("NormalZombie", new ZombieConfig(
                "NormalZombie",
                "普通僵尸",
                100,      // 生命值
                25,       // 攻击伤害
                1000,     // 攻击间隔（毫秒）
                40,       // 移动速度（像素/秒）
                1,        // 首次出现关卡
                1.0       // 难度系数权重
        ));

        // 路障僵尸
        zombies.put("ConeheadZombie", new ZombieConfig(
                "ConeheadZombie",
                "路障僵尸",
                200,      // 生命值（高）
                25,       // 攻击伤害
                1000,     // 攻击间隔
                40,       // 移动速度
                2,        // 首次出现关卡
                1.2       // 难度系数权重
        ));

        // 铁桶僵尸
        zombies.put("BucketheadZombie", new ZombieConfig(
                "BucketheadZombie",
                "铁桶僵尸",
                400,      // 生命值（非常高）
                25,       // 攻击伤害
                1000,     // 攻击间隔
                40,       // 移动速度
                3,        // 首次出现关卡
                1.5       // 难度系数权重
        ));
    }

    /**
     * 获取僵尸配置
     */
    public ZombieConfig getZombieConfig(String zombieId) {
        return zombies.get(zombieId);
    }

    /**
     * 获取所有僵尸配置
     */
    public Map<String, ZombieConfig> getAllZombies() {
        return new HashMap<>(zombies);
    }

    /**
     * 获取指定关卡前可出现的僵尸列表
     */
    public List<ZombieConfig> getZombiesByLevel(int levelIndex) {
        int levelNumber = levelIndex + 1;
        return zombies.values().stream()
                .filter(z -> z.getFirstAppearLevel() <= levelNumber)
                .collect(Collectors.toList());
    }

    // ==================== 关卡配置 ====================

    /**
     * 加载关卡配置
     */
    private void loadLevelsConfig() {
        // ====== 第1关 ======
        List<WaveConfig> level1Waves = new ArrayList<>();

        System.out.println("====== 初始化第1关 ======"); // 和原来一样
        List<ZombieSpawn> wave1_1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int row = i % Constants.GRID_ROWS;
            int delay = (i == 0) ? 5000 : 10000;
            wave1_1.add(new ZombieSpawn("NormalZombie", row, delay));
            System.out.println("添加第1关僵尸：行=" + row + "，延迟=" + delay); // 和原来一样
        }
        level1Waves.add(new WaveConfig(0, wave1_1, 0));

        levels.add(new LevelConfig(
                0,
                "1-1",
                150,
                level1Waves,
                "PeaShooter",
                "新手教程"
        ));

        // ====== 第2关 ======
        List<WaveConfig> level2Waves = new ArrayList<>();

        System.out.println("====== 初始化第2关 ======"); // 和原来一样

        // 第1波：5只普通僵尸
        List<ZombieSpawn> wave2_1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int row = i % Constants.GRID_ROWS;
            int delay = (i == 0) ? 5000 : 10000;
            wave2_1.add(new ZombieSpawn("NormalZombie", row, delay));
            System.out.println("添加第2关第一波僵尸：行=" + row + "，延迟=" + delay); // 和原来一样
        }
        level2Waves.add(new WaveConfig(0, wave2_1, 3000)); // 和原来一样

        // 第2波：3只路障僵尸
        List<ZombieSpawn> wave2_2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int row = i % Constants.GRID_ROWS;
            int delay = (i == 0) ? 3000 : 10000;
            wave2_2.add(new ZombieSpawn("ConeheadZombie", row, delay));
            System.out.println("添加第2关第二波僵尸：行=" + row + "，延迟=" + delay); // 和原来一样
        }
        level2Waves.add(new WaveConfig(1, wave2_2, 0));

        levels.add(new LevelConfig(
                1,
                "1-2",
                150,
                level2Waves,
                "WallNut",
                "解锁坚果墙"
        ));
    }

    /**
     * 获取关卡配置
     */
    public LevelConfig getLevelConfig(int index) {
        if (index >= 0 && index < levels.size()) {
            return levels.get(index);
        }
        return null;
    }

    /**
     * 获取所有关卡配置
     */
    public List<LevelConfig> getAllLevels() {
        return new ArrayList<>(levels);
    }

    /**
     * 获取关卡总数
     */
    public int getTotalLevels() {
        return levels.size();
    }

    // ==================== 内部配置类 ====================

    /**
     * 植物配置类
     */
    public static class PlantConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String name;
        private final int sunCost;
        private final int health;
        private final int damage;
        private final int attackCooldown;  // 攻击/生产间隔
        private final int unlockLevel;
        private final boolean isAttacker;
        private final boolean isProducer;

        public PlantConfig(String id, String name, int sunCost, int health,
                           int damage, int attackCooldown, int unlockLevel,
                           boolean isAttacker, boolean isProducer) {
            this.id = id;
            this.name = name;
            this.sunCost = sunCost;
            this.health = health;
            this.damage = damage;
            this.attackCooldown = attackCooldown;
            this.unlockLevel = unlockLevel;
            this.isAttacker = isAttacker;
            this.isProducer = isProducer;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getSunCost() { return sunCost; }
        public int getHealth() { return health; }
        public int getDamage() { return damage; }
        public int getAttackCooldown() { return attackCooldown; }
        public int getUnlockLevel() { return unlockLevel; }
        public boolean isAttacker() { return isAttacker; }
        public boolean isProducer() { return isProducer; }

        @Override
        public String toString() {
            return name + "(阳光:" + sunCost + ", 伤害:" + damage + ")";
        }
    }

    /**
     * 僵尸配置类
     */
    public static class ZombieConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String name;
        private final int health;
        private final int attackDamage;
        private final int attackCooldown;
        private final int speed;
        private final int firstAppearLevel;
        private final double difficultyWeight;

        public ZombieConfig(String id, String name, int health, int attackDamage,
                            int attackCooldown, int speed, int firstAppearLevel,
                            double difficultyWeight) {
            this.id = id;
            this.name = name;
            this.health = health;
            this.attackDamage = attackDamage;
            this.attackCooldown = attackCooldown;
            this.speed = speed;
            this.firstAppearLevel = firstAppearLevel;
            this.difficultyWeight = difficultyWeight;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getHealth() { return health; }
        public int getAttackDamage() { return attackDamage; }
        public int getAttackCooldown() { return attackCooldown; }
        public int getSpeed() { return speed; }
        public int getFirstAppearLevel() { return firstAppearLevel; }
        public double getDifficultyWeight() { return difficultyWeight; }

        @Override
        public String toString() {
            return name + "(生命:" + health + ", 伤害:" + attackDamage + ")";
        }
    }

    /**
     * 关卡配置类
     */
    public static class LevelConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int index;
        private final String name;
        private final int startSun;
        private final List<WaveConfig> waves;
        private final String unlockReward;  // 通关解锁的植物ID
        private final String description;

        public LevelConfig(int index, String name, int startSun,
                           List<WaveConfig> waves, String unlockReward,
                           String description) {
            this.index = index;
            this.name = name;
            this.startSun = startSun;
            this.waves = waves;
            this.unlockReward = unlockReward;
            this.description = description;
        }

        // Getters
        public int getIndex() { return index; }
        public String getName() { return name; }
        public int getStartSun() { return startSun; }
        public List<WaveConfig> getWaves() { return waves; }
        public String getUnlockReward() { return unlockReward; }
        public String getDescription() { return description; }

        public boolean hasReward() {
            return unlockReward != null && !unlockReward.isEmpty();
        }
    }

    /**
     * 波次配置类
     */
    public static class WaveConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int waveIndex;
        private final List<ZombieSpawn> spawns;
        private final int nextWaveDelay;  // 下一波延迟（毫秒）

        public WaveConfig(int waveIndex, List<ZombieSpawn> spawns, int nextWaveDelay) {
            this.waveIndex = waveIndex;
            this.spawns = spawns;
            this.nextWaveDelay = nextWaveDelay;
        }

        // Getters
        public int getWaveIndex() { return waveIndex; }
        public List<ZombieSpawn> getSpawns() { return spawns; }
        public int getNextWaveDelay() { return nextWaveDelay; }
    }

    /**
     * 僵尸生成配置类
     */
    public static class ZombieSpawn implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String zombieType;
        private final int row;
        private final int delay;  // 延迟（毫秒）

        public ZombieSpawn(String zombieType, int row, int delay) {
            this.zombieType = zombieType;
            this.row = row;
            this.delay = delay;
        }

        // Getters
        public String getZombieType() { return zombieType; }
        public int getRow() { return row; }
        public int getDelay() { return delay; }
    }
}