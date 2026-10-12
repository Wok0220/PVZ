package com.ok.data;

import java.util.List;

/**
 * 数据提供者接口
 * 定义所有数据读写操作，用于隔离游戏逻辑与数据存储
 *
 * 前期：使用 LocalDataProvider 实现单账号本地存储
 * 后期：实现 MultiAccountDataProvider 支持多账号切换
 *
 * 游戏逻辑只依赖此接口，不依赖具体实现
 */
public interface IDataProvider {

    // ==================== 进度数据操作 ====================

    /**
     * 获取当前玩家的进度数据
     * @return 玩家进度对象
     */
    PlayerProgress getProgress();

    /**
     * 保存当前玩家的进度数据
     * @param progress 玩家进度对象
     * @return 是否保存成功
     */
    boolean saveProgress(PlayerProgress progress);

    // ==================== 便捷方法（可选，减少直接操作Progress的代码） ====================

    /**
     * 获取当前关卡
     */
    default String getCurrentLevel() {
        return getProgress().getCurrentLevel();
    }

    /**
     * 设置当前关卡
     */
    default void setCurrentLevel(String level) {
        PlayerProgress progress = getProgress();
        progress.setCurrentLevel(level);
        saveProgress(progress);
    }

    /**
     * 获取当前关卡索引
     */
    default int getCurrentLevelIndex() {
        return getProgress().getCurrentLevelIndex();
    }

    /**
     * 设置当前关卡索引
     */
    default void setCurrentLevelIndex(int index) {
        PlayerProgress progress = getProgress();
        progress.setCurrentLevelIndex(index);
        saveProgress(progress);
    }

    /**
     * 通关后进入下一关
     * @param totalLevels 总关卡数
     * @return 是否还有下一关
     */
    default boolean advanceToNextLevel(int totalLevels) {
        PlayerProgress progress = getProgress();
        boolean hasNext = progress.advanceToNextLevel(totalLevels);
        saveProgress(progress);
        return hasNext;
    }

    // ==================== 植物解锁 ====================

    /**
     * 检查植物是否已解锁
     * @param plantId 植物ID
     * @return 是否已解锁
     */
    default boolean isPlantUnlocked(String plantId) {
        return getProgress().isPlantUnlocked(plantId);
    }

    /**
     * 解锁新植物
     * @param plantId 植物ID
     * @return 是否解锁成功（已解锁返回false）
     */
    default boolean unlockPlant(String plantId) {
        PlayerProgress progress = getProgress();
        boolean unlocked = progress.unlockPlant(plantId);
        if (unlocked) {
            saveProgress(progress);
        }
        return unlocked;
    }

    /**
     * 获取已解锁植物列表
     */
    default List<String> getUnlockedPlants() {
        return getProgress().getUnlockedPlants();
    }

    // ==================== 鸽子系统 ====================

    /**
     * 获取鸽子数量
     */
    default int getPigeonCount() {
        return getProgress().getPigeonCount();
    }

    /**
     * 增加鸽子
     * @param amount 增加数量
     */
    default void addPigeons(int amount) {
        PlayerProgress progress = getProgress();
        progress.addPigeons(amount);
        saveProgress(progress);
    }

    /**
     * 消耗鸽子
     * @param amount 消耗数量
     * @return 是否消耗成功（鸽子不足返回false）
     */
    default boolean spendPigeons(int amount) {
        PlayerProgress progress = getProgress();
        boolean success = progress.spendPigeons(amount);
        if (success) {
            saveProgress(progress);
        }
        return success;
    }

    // ==================== 拼图系统 ====================

    /**
     * 检查拼图是否已解锁
     * @param index 拼图索引（0-3）
     * @return 是否已解锁
     */
    default boolean isPuzzleUnlocked(int index) {
        return getProgress().isPuzzleUnlocked(index);
    }

    /**
     * 解锁拼图
     * @param index 拼图索引（0-3）
     * @return 是否解锁成功
     */
    default boolean unlockPuzzlePiece(int index) {
        PlayerProgress progress = getProgress();
        boolean unlocked = progress.unlockPuzzlePiece(index);
        if (unlocked) {
            saveProgress(progress);
        }
        return unlocked;
    }

    /**
     * 获取已解锁拼图数量
     */
    default int getUnlockedPuzzleCount() {
        return getProgress().getUnlockedPuzzleCount();
    }

    /**
     * 检查拼图是否全部解锁
     */
    default boolean isPuzzleComplete() {
        return getProgress().isPuzzleComplete();
    }

    // ==================== 游戏设置 ====================

    /**
     * 获取音效开关状态
     */
    default boolean isSoundEnabled() {
        return getProgress().isSoundEnabled();
    }

    /**
     * 设置音效开关
     */
    default void setSoundEnabled(boolean enabled) {
        PlayerProgress progress = getProgress();
        progress.setSoundEnabled(enabled);
        saveProgress(progress);
    }

    /**
     * 获取音乐开关状态
     */
    default boolean isMusicEnabled() {
        return getProgress().isMusicEnabled();
    }

    /**
     * 设置音乐开关
     */
    default void setMusicEnabled(boolean enabled) {
        PlayerProgress progress = getProgress();
        progress.setMusicEnabled(enabled);
        saveProgress(progress);
    }

    // ==================== 无限模式 ====================

    /**
     * 获取无限模式最高波次
     */
    default int getEndlessHighScore() {
        return getProgress().getEndlessHighScore();
    }

    /**
     * 更新无限模式最高波次
     * @param wave 当前波次
     * @return 是否刷新了记录
     */
    default boolean updateEndlessHighScore(int wave) {
        PlayerProgress progress = getProgress();
        boolean updated = progress.updateEndlessHighScore(wave);
        if (updated) {
            saveProgress(progress);
        }
        return updated;
    }

    // ==================== 统计信息 ====================

    /**
     * 增加游戏场数
     */
    default void incrementGamesPlayed() {
        PlayerProgress progress = getProgress();
        progress.incrementGamesPlayed();
        saveProgress(progress);
    }

    /**
     * 增加胜利场数
     */
    default void incrementWins() {
        PlayerProgress progress = getProgress();
        progress.incrementWins();
        saveProgress(progress);
    }

    /**
     * 增加击杀僵尸数
     */
    default void addZombieKills(int count) {
        PlayerProgress progress = getProgress();
        progress.addZombieKills(count);
        saveProgress(progress);
    }

    /**
     * 增加种植植物数
     */
    default void incrementPlantsPlanted() {
        PlayerProgress progress = getProgress();
        progress.incrementPlantsPlanted();
        saveProgress(progress);
    }

    // ==================== 重置功能 ====================

    /**
     * 重置所有进度（清空数据）
     */
    default void resetProgress() {
        PlayerProgress progress = getProgress();
        progress.reset();
        saveProgress(progress);
    }

    // ==================== 数据管理 ====================

    /**
     * 检查是否有存档数据
     * @return 是否有存档
     */
    boolean hasSaveData();

    /**
     * 清除所有存档数据
     * @return 是否清除成功
     */
    boolean clearSaveData();

    /**
     * 获取存档文件路径（用于调试）
     * @return 存档路径
     */
    String getSaveFilePath();
}