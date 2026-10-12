package com.ok.ui.components;

import java.awt.Rectangle;

/**
 * 植物卡片组件
 * 用于在游戏界面中显示和选择植物
 */
public class PlantCard {

    /** 植物ID */
    private String plantId;

    /** 卡片位置X */
    private int x;

    /** 卡片位置Y */
    private int y;

    /** 卡片宽度 */
    private int width;

    /** 卡片高度 */
    private int height;

    /**
     * 构造函数
     * @param plantId 植物ID
     * @param x 位置X
     * @param y 位置Y
     * @param width 宽度
     * @param height 高度
     */
    public PlantCard(String plantId, int x, int y, int width, int height) {
        this.plantId = plantId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * 获取植物ID
     * @return 植物ID
     */
    public String getPlantId() {
        return plantId;
    }

    /**
     * 获取卡片边界
     * @return 卡片边界矩形
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * 获取卡片X坐标
     * @return X坐标
     */
    public int getX() {
        return x;
    }

    /**
     * 获取卡片Y坐标
     * @return Y坐标
     */
    public int getY() {
        return y;
    }

    /**
     * 获取卡片宽度
     * @return 宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取卡片高度
     * @return 高度
     */
    public int getHeight() {
        return height;
    }
}