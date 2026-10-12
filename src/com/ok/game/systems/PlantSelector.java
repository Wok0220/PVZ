package com.ok.game.systems;

import com.ok.data.DataManager;
import com.ok.data.GameConfig;
import com.ok.data.PlayerProgress;
import com.ok.game.entities.Plant;
import com.ok.game.plants.PeaShooter;
import com.ok.game.plants.Sunflower;
import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 植物选择器
 * 管理植物选择状态（种植模式），包括选中的植物、跟随鼠标的图标等
 */
public class PlantSelector {

    /** 单例实例 */
    private static PlantSelector instance;

    /** 是否处于选择状态（种植模式） */
    private boolean isSelecting;

    /** 当前选中的植物ID */
    private String selectedPlantId;

    /** 当前选中的植物配置 */
    private GameConfig.PlantConfig selectedPlantConfig;

    /** 鼠标当前位置（用于绘制跟随图标） */
    private int mouseX;
    private int mouseY;

    /** 植物图标缓存（用于快速获取图标） */
    private Map<String, Image> plantIconCache;

    /** 植物卡片冷却管理器 */
    private Map<String, Integer> plantCooldowns;  // 植物ID -> 剩余冷却时间（毫秒）

    /** 游戏管理器引用 */
    private com.ok.game.core.GameManager gameManager;

    /**
     * 私有构造函数
     */
    private PlantSelector() {
        this.isSelecting = false;
        this.selectedPlantId = null;
        this.selectedPlantConfig = null;
        this.mouseX = 0;
        this.mouseY = 0;
        this.plantIconCache = new HashMap<>();
        this.plantCooldowns = new HashMap<>();
    }

    /**
     * 获取单例实例
     */
    public static PlantSelector getInstance() {
        if (instance == null) {
            instance = new PlantSelector();
        }
        return instance;
    }

    /**
     * 初始化
     * @param gameManager 游戏管理器
     */
    public void init(com.ok.game.core.GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // ==================== 植物选择 ====================

    /**
     * 选择植物
     * @param plantId 植物ID
     * @param currentSun 当前阳光数量
     * @return 是否选择成功
     */
    public boolean selectPlant(String plantId, int currentSun) {
        // 获取植物配置
        GameConfig.PlantConfig config = GameConfig.getInstance().getPlantConfig(plantId);
        if (config == null) {
            return false;
        }

        // 检查植物是否已解锁
        PlayerProgress progress = DataManager.getInstance().getProvider().getProgress();
        if (!progress.isPlantUnlocked(plantId)) {
            return false;
        }

        // 检查阳光是否足够
        if (currentSun < config.getSunCost()) {
            return false;
        }

        // 检查是否在冷却中
        if (isOnCooldown(plantId)) {
            return false;
        }

        // 选择植物
        this.isSelecting = true;
        this.selectedPlantId = plantId;
        this.selectedPlantConfig = config;

        return true;
    }

    /**
     * 取消选择
     */
    public void deselect() {
        this.isSelecting = false;
        this.selectedPlantId = null;
        this.selectedPlantConfig = null;
    }

    /**
     * 尝试种植当前选中的植物
     * @param row 网格行
     * @param col 网格列
     * @return 是否种植成功
     */
    public boolean tryPlantAt(int row, int col) {
        if (!isSelecting || selectedPlantId == null || selectedPlantConfig == null) {
            return false;
        }

        if (gameManager == null) {
            return false;
        }

        // 计算屏幕坐标
        int x = Constants.GRID_OFFSET_X + col * Constants.GRID_WIDTH;
        int y = Constants.GRID_OFFSET_Y + row * Constants.GRID_HEIGHT;

        // 创建植物实例
        Plant plant = createPlant(selectedPlantId, row, col, x, y);
        if (plant == null) {
            return false;
        }

        // 尝试种植
        boolean success = gameManager.tryPlant(plant, row, col);

        if (success) {
            // 种植成功，开始冷却
            startCooldown(selectedPlantId, selectedPlantConfig.getAttackCooldown());
            // 取消选择状态
            deselect();
        }

        return success;
    }

    /**
     * 创建植物实例
     */
    private Plant createPlant(String plantId, int row, int col, int x, int y) {
        switch (plantId) {
            case "PeaShooter":
                return new PeaShooter(row, col, x, y);
            case "Sunflower":
                return new Sunflower(row, col, x, y);
            default:
                return null;
        }
    }

    // ==================== 冷却系统 ====================

    /**
     * 开始冷却
     * @param plantId 植物ID
     * @param cooldownTime 冷却时间（毫秒）
     */
    public void startCooldown(String plantId, int cooldownTime) {
        plantCooldowns.put(plantId, cooldownTime);
    }

    /**
     * 更新所有冷却计时
     * @param deltaTime 帧间隔时间（秒）
     */
    public void updateCooldowns(float deltaTime) {
        int deltaMs = (int)(deltaTime * 1000);

        // 使用迭代器，避免并发修改
        for (Map.Entry<String, Integer> entry : plantCooldowns.entrySet()) {
            int remaining = entry.getValue() - deltaMs;
            if (remaining <= 0) {
                // 冷却完成，移除
                plantCooldowns.remove(entry.getKey());
            } else {
                plantCooldowns.put(entry.getKey(), remaining);
            }
        }
    }

    /**
     * 检查植物是否在冷却中
     * @param plantId 植物ID
     * @return 是否在冷却中
     */
    public boolean isOnCooldown(String plantId) {
        return plantCooldowns.containsKey(plantId);
    }

    /**
     * 获取植物剩余冷却时间（毫秒）
     * @param plantId 植物ID
     * @return 剩余时间，如果没有冷却返回0
     */
    public int getRemainingCooldown(String plantId) {
        return plantCooldowns.getOrDefault(plantId, 0);
    }

    /**
     * 获取冷却进度（0-1）
     * @param plantId 植物ID
     * @param totalCooldown 总冷却时间
     * @return 冷却进度（1表示冷却完成）
     */
    public float getCooldownProgress(String plantId, int totalCooldown) {
        if (totalCooldown <= 0) return 1.0f;
        int remaining = getRemainingCooldown(plantId);
        return 1.0f - (float) remaining / totalCooldown;
    }

    // ==================== 鼠标位置 ====================

    /**
     * 更新鼠标位置
     */
    public void updateMousePosition(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    /**
     * 获取鼠标X坐标
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * 获取鼠标Y坐标
     */
    public int getMouseY() {
        return mouseY;
    }

    // ==================== 状态查询 ====================

    /**
     * 是否处于选择状态
     */
    public boolean isSelecting() {
        return isSelecting;
    }

    /**
     * 获取选中的植物ID
     */
    public String getSelectedPlantId() {
        return selectedPlantId;
    }

    /**
     * 获取选中的植物配置
     */
    public GameConfig.PlantConfig getSelectedPlantConfig() {
        return selectedPlantConfig;
    }

    /**
     * 获取选中的植物阳光消耗
     */
    public int getSelectedPlantSunCost() {
        if (selectedPlantConfig != null) {
            return selectedPlantConfig.getSunCost();
        }
        return 0;
    }

    // 修改 com.ok.game.systems.PlantSelector.java
    public Image getSelectedPlantIcon() {
        if (selectedPlantId == null) return null;

        // 从缓存获取
        if (plantIconCache.containsKey(selectedPlantId)) {
            return plantIconCache.get(selectedPlantId);
        }

        // 加载图标（从ResourceManager）
        Image icon = ResourceManager.getInstance().getPlantImage(selectedPlantId);
        plantIconCache.put(selectedPlantId, icon);
        return icon;
    }

    // ==================== 重置 ====================

    /**
     * 重置所有状态（新关卡时调用）
     */
    public void reset() {
        deselect();
        plantCooldowns.clear();
    }
}