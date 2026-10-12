package com.ok.data;

import java.io.Serializable;
import java.util.*;

/**
 * 玩家进度数据模型
 * 包含所有需要持久化的玩家进度信息
 * 每个账号独立拥有一份此数据
 */
public class PlayerProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础信息 ====================
    /** 当前关卡（格式：大关-小关，如 "1-2"） */
    private String currentLevel;

    /** 当前关卡索引（用于程序读取，0开始） */
    private int currentLevelIndex;

    // ==================== 植物解锁 ====================
    /** 已解锁的植物ID列表 */
    private List<String> unlockedPlants;

    /** 所有植物ID列表（用于重置时恢复） */
    private static final List<String> ALL_PLANTS = Arrays.asList(
            "PeaShooter",      // 豌豆射手
            "Sunflower",       // 向日葵
            "WallNut",         // 坚果墙
            "SnowPea",         // 寒冰射手
            "PotatoMine",      // 土豆雷
            "CherryBomb"       // 樱桃炸弹
    );

    // ==================== 收集系统 ====================
    /** 鸽子数量（用于拼图兑换） */
    private int pigeonCount;

    // ==================== 拼图系统 ====================
    /** 拼图解锁状态，长度为4，true表示已解锁 */
    private boolean[] puzzlePieces;

    // ==================== 游戏设置（可选，也可放在全局） ====================
    /** 音效开关（如果希望每个账号独立） */
    private boolean soundEnabled;

    /** 音乐开关（如果希望每个账号独立） */
    private boolean musicEnabled;

    // ==================== 无限模式数据 ====================
    /** 无限模式最高波次记录 */
    private int endlessHighScore;

    // ==================== 统计信息（可选） ====================
    /** 总游戏场数 */
    private int totalGamesPlayed;

    /** 总胜利场数 */
    private int totalWins;

    /** 总击杀僵尸数 */
    private int totalZombiesKilled;

    /** 总种植植物数 */
    private int totalPlantsPlanted;

    // ==================== 构造函数 ====================

    /**
     * 默认构造函数，创建新玩家进度
     */
    public PlayerProgress() {
        this.currentLevel = "1-1";
        this.currentLevelIndex = 0;
        this.unlockedPlants = new ArrayList<>();
        this.pigeonCount = 0;
        this.puzzlePieces = new boolean[4];
        this.soundEnabled = true;
        this.musicEnabled = true;
        this.endlessHighScore = 0;
        this.totalGamesPlayed = 0;
        this.totalWins = 0;
        this.totalZombiesKilled = 0;
        this.totalPlantsPlanted = 0;

        // 初始解锁基础植物
        unlockInitialPlants();
    }

    /**
     * 初始化基础植物（新手默认解锁）
     */
    private void unlockInitialPlants() {
        // 默认解锁豌豆射手和向日葵
        if (!unlockedPlants.contains("PeaShooter")) {
            unlockedPlants.add("PeaShooter");
        }
        if (!unlockedPlants.contains("Sunflower")) {
            unlockedPlants.add("Sunflower");
        }
    }

    // ==================== 关卡进度相关 ====================

    /**
     * 获取当前关卡
     */
    public String getCurrentLevel() {
        return currentLevel;
    }

    /**
     * 设置当前关卡
     */
    public void setCurrentLevel(String level) {
        this.currentLevel = level;
    }

    /**
     * 获取当前关卡索引
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    /**
     * 设置当前关卡索引
     */
    public void setCurrentLevelIndex(int index) {
        this.currentLevelIndex = index;
    }

    /**
     * 通关后进入下一关
     * @param totalLevels 总关卡数
     * @return 是否还有下一关
     */
    public boolean advanceToNextLevel(int totalLevels) {
        int nextIndex = currentLevelIndex + 1;
        if (nextIndex < totalLevels) {
            currentLevelIndex = nextIndex;
            // 更新显示格式的关卡名，这里简单处理，实际可能需要从配置读取
            currentLevel = formatLevelName(nextIndex);
            return true;
        }
        return false;
    }

    /**
     * 格式化关卡名称（索引转显示名称）
     * @param index 关卡索引（0开始）
     * @return 如 "1-1"
     */
    private String formatLevelName(int index) {
        int world = index / 10 + 1;
        int level = index % 10 + 1;
        return world + "-" + level;
    }

    // ==================== 植物解锁相关 ====================

    /**
     * 获取已解锁植物列表
     */
    public List<String> getUnlockedPlants() {
        return unlockedPlants;
    }

    /**
     * 检查植物是否已解锁
     * @param plantId 植物ID
     * @return 是否已解锁
     */
    public boolean isPlantUnlocked(String plantId) {
        return unlockedPlants.contains(plantId);
    }

    /**
     * 解锁新植物
     * @param plantId 植物ID
     * @return 是否解锁成功（已解锁返回false）
     */
    public boolean unlockPlant(String plantId) {
        if (unlockedPlants.contains(plantId)) {
            return false;
        }
        unlockedPlants.add(plantId);
        return true;
    }

    /**
     * 批量解锁植物（用于测试或特殊奖励）
     */
    public void unlockPlants(List<String> plantIds) {
        for (String plantId : plantIds) {
            if (!unlockedPlants.contains(plantId)) {
                unlockedPlants.add(plantId);
            }
        }
    }

    /**
     * 获取所有植物ID（用于物品栏显示）
     */
    public static List<String> getAllPlants() {
        return new ArrayList<>(ALL_PLANTS);
    }

    /**
     * 获取未解锁植物列表
     */
    public List<String> getLockedPlants() {
        List<String> locked = new ArrayList<>();
        for (String plant : ALL_PLANTS) {
            if (!unlockedPlants.contains(plant)) {
                locked.add(plant);
            }
        }
        return locked;
    }

    // ==================== 鸽子相关 ====================

    /**
     * 获取鸽子数量
     */
    public int getPigeonCount() {
        return pigeonCount;
    }

    /**
     * 设置鸽子数量
     */
    public void setPigeonCount(int count) {
        this.pigeonCount = Math.max(0, count);
    }

    /**
     * 增加鸽子
     * @param amount 增加数量
     */
    public void addPigeons(int amount) {
        if (amount > 0) {
            this.pigeonCount += amount;
        }
    }

    /**
     * 消耗鸽子
     * @param amount 消耗数量
     * @return 是否消耗成功（鸽子不足返回false）
     */
    public boolean spendPigeons(int amount) {
        if (pigeonCount >= amount) {
            pigeonCount -= amount;
            return true;
        }
        return false;
    }

    // ==================== 拼图相关 ====================

    /**
     * 获取拼图解锁状态
     */
    public boolean[] getPuzzlePieces() {
        return puzzlePieces;
    }

    /**
     * 检查指定拼图是否已解锁
     * @param index 拼图索引（0-3）
     * @return 是否已解锁
     */
    public boolean isPuzzleUnlocked(int index) {
        if (index < 0 || index >= puzzlePieces.length) {
            return false;
        }
        return puzzlePieces[index];
    }

    /**
     * 解锁拼图
     * @param index 拼图索引（0-3）
     * @return 是否解锁成功（已解锁或索引无效返回false）
     */
    public boolean unlockPuzzlePiece(int index) {
        if (index < 0 || index >= puzzlePieces.length) {
            return false;
        }
        if (puzzlePieces[index]) {
            return false;
        }
        puzzlePieces[index] = true;
        return true;
    }

    /**
     * 获取已解锁拼图数量
     */
    public int getUnlockedPuzzleCount() {
        int count = 0;
        for (boolean piece : puzzlePieces) {
            if (piece) count++;
        }
        return count;
    }

    /**
     * 检查拼图是否全部解锁
     */
    public boolean isPuzzleComplete() {
        return getUnlockedPuzzleCount() == puzzlePieces.length;
    }

    // ==================== 设置相关 ====================

    /**
     * 获取音效开关状态
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * 设置音效开关
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /**
     * 获取音乐开关状态
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * 设置音乐开关
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
    }

    // ==================== 无限模式相关 ====================

    /**
     * 获取无限模式最高波次
     */
    public int getEndlessHighScore() {
        return endlessHighScore;
    }

    /**
     * 更新无限模式最高波次
     * @param wave 当前波次
     * @return 是否刷新了记录
     */
    public boolean updateEndlessHighScore(int wave) {
        if (wave > endlessHighScore) {
            endlessHighScore = wave;
            return true;
        }
        return false;
    }

    // ==================== 统计信息 ====================

    /**
     * 获取总游戏场数
     */
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /**
     * 增加游戏场数
     */
    public void incrementGamesPlayed() {
        this.totalGamesPlayed++;
    }

    /**
     * 获取总胜利场数
     */
    public int getTotalWins() {
        return totalWins;
    }

    /**
     * 增加胜利场数
     */
    public void incrementWins() {
        this.totalWins++;
    }

    /**
     * 获取总击杀僵尸数
     */
    public int getTotalZombiesKilled() {
        return totalZombiesKilled;
    }

    /**
     * 增加击杀僵尸数
     */
    public void addZombieKills(int count) {
        this.totalZombiesKilled += count;
    }

    /**
     * 获取总种植植物数
     */
    public int getTotalPlantsPlanted() {
        return totalPlantsPlanted;
    }

    /**
     * 增加种植植物数
     */
    public void incrementPlantsPlanted() {
        this.totalPlantsPlanted++;
    }

    // ==================== 重置功能 ====================

    /**
     * 重置所有进度（清空数据）
     */
    public void reset() {
        this.currentLevel = "1-1";
        this.currentLevelIndex = 0;
        this.unlockedPlants.clear();
        this.pigeonCount = 0;
        Arrays.fill(this.puzzlePieces, false);
        this.endlessHighScore = 0;
        this.totalGamesPlayed = 0;
        this.totalWins = 0;
        this.totalZombiesKilled = 0;
        this.totalPlantsPlanted = 0;

        // 重新解锁基础植物
        unlockInitialPlants();
    }

    // ==================== 辅助方法 ====================

    @Override
    public String toString() {
        return "PlayerProgress{" +
                "currentLevel='" + currentLevel + '\'' +
                ", unlockedPlants=" + unlockedPlants.size() +
                ", pigeonCount=" + pigeonCount +
                ", puzzleProgress=" + getUnlockedPuzzleCount() + "/" + puzzlePieces.length +
                ", endlessHighScore=" + endlessHighScore +
                ", totalGamesPlayed=" + totalGamesPlayed +
                '}';
    }
}