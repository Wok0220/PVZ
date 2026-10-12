package com.ok.game.systems;

import com.ok.game.core.GameManager;
import com.ok.game.grid.GridManager;

import java.awt.*;

/**
 * 铲子系统
 * 管理铲子模式状态，铲除植物并返还阳光
 */
public class ShovelSystem {

    /** 单例实例 */
    private static ShovelSystem instance;

    /** 是否处于铲子模式 */
    private boolean isShovelMode;

    /** 鼠标当前位置（用于绘制跟随铲子图标） */
    private int mouseX;
    private int mouseY;

    /** 游戏管理器引用 */
    private GameManager gameManager;

    /** 网格管理器引用 */
    private GridManager gridManager;

    /**
     * 私有构造函数
     */
    private ShovelSystem() {
        this.isShovelMode = false;
        this.mouseX = 0;
        this.mouseY = 0;
    }

    /**
     * 获取单例实例
     */
    public static ShovelSystem getInstance() {
        if (instance == null) {
            instance = new ShovelSystem();
        }
        return instance;
    }

    /**
     * 初始化
     * @param gameManager 游戏管理器
     */
    public void init(GameManager gameManager) {
        this.gameManager = gameManager;
        this.gridManager = gameManager.getGridManager();
    }

    // ==================== 铲子模式控制 ====================

    /**
     * 激活铲子模式
     */
    public void activate() {
        this.isShovelMode = true;
    }

    /**
     * 取消铲子模式
     */
    public void deactivate() {
        this.isShovelMode = false;
    }

    /**
     * 切换铲子模式
     */
    public void toggle() {
        this.isShovelMode = !this.isShovelMode;
    }

    /**
     * 是否处于铲子模式
     */
    public boolean isShovelMode() {
        return isShovelMode;
    }

    // ==================== 铲除操作 ====================

    /**
     * 尝试铲除指定位置的植物
     * @param screenX 屏幕X坐标
     * @param screenY 屏幕Y坐标
     * @return 是否铲除成功
     */
    public boolean tryShovel(int screenX, int screenY) {
        if (!isShovelMode) {
            return false;
        }

        if (gameManager == null) {
            return false;
        }

        // 获取点击位置的植物
        Point gridPos = gridManager.screenToGrid(screenX, screenY);
        if (gridPos == null) {
            return false;
        }

        int row = gridPos.x;
        int col = gridPos.y;

        // 检查是否有植物
        if (!gridManager.hasPlant(row, col)) {
            return false;
        }

        // 铲除植物
        boolean success = gameManager.shovelPlant(row, col);

        if (success) {
            // 铲除成功后自动退出铲子模式
            deactivate();
        }

        return success;
    }

    /**
     * 尝试铲除指定网格位置的植物
     * @param row 网格行
     * @param col 网格列
     * @return 是否铲除成功
     */
    /**
     * 尝试铲除指定屏幕位置的植物
     */
    public boolean tryShovelAtScreen(int screenX, int screenY) {
        if (!isShovelMode) return false;
        if (gameManager == null) return false;

        Point gridPos = gridManager.screenToGrid(screenX, screenY);
        if (gridPos == null) return false;

        return tryShovelAtGrid(gridPos.x, gridPos.y);
    }

    /**
     * 尝试铲除指定网格位置的植物
     */
    public boolean tryShovelAtGrid(int row, int col) {
        if (!isShovelMode) return false;
        if (gameManager == null) return false;

        if (!gridManager.hasPlant(row, col)) return false;

        boolean success = gameManager.shovelPlant(row, col);
        if (success) {
            deactivate();
        }
        return success;
    }

    // ==================== 鼠标位置 ====================

    /**
     * 更新鼠标位置
     * @param x X坐标
     * @param y Y坐标
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

    // ==================== 获取铲子图标 ====================

    /**
     * 获取铲子图标（用于绘制）
     * @return 铲子图标Image对象
     */
    public Image getShovelIcon() {
        // 从ResourceManager加载铲子图标
        // return ResourceManager.getInstance().getShovelImage();
        return null; // 暂时返回null
    }

    // ==================== 重置 ====================

    /**
     * 重置铲子系统（新关卡时调用）
     */
    public void reset() {
        deactivate();
        mouseX = 0;
        mouseY = 0;
    }
}