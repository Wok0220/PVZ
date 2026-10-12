package com.ok.game.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 游戏对象基类
 * 所有游戏中的实体（植物、僵尸、子弹、阳光）都继承此类
 */
public abstract class GameObject {

    /** X坐标（像素） */
    protected int x;

    /** Y坐标（像素） */
    protected int y;

    /** 宽度（像素） */
    protected int width;

    /** 高度（像素） */
    protected int height;

    /** 当前显示的图片 */
    protected Image image;

    /** 是否存活（true表示在游戏中，false表示待移除） */
    protected boolean alive;

    /** 是否可见（用于绘制，死亡动画期间可能设为false） */
    protected boolean visible;

    /**
     * 默认构造函数
     */
    public GameObject() {
        this.alive = true;
        this.visible = true;
        this.width = 0;
        this.height = 0;
    }

    /**
     * 带坐标的构造函数
     * @param x X坐标
     * @param y Y坐标
     */
    public GameObject(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

    /**
     * 带坐标和尺寸的构造函数
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     */
    public GameObject(int x, int y, int width, int height) {
        this(x, y);
        this.width = width;
        this.height = height;
    }

    // ==================== 抽象方法 ====================

    /**
     * 更新对象状态
     * 每帧调用一次，子类实现具体行为
     * @param deltaTime 帧间隔时间（秒）
     */
    public abstract void update(float deltaTime);

    /**
     * 绘制对象
     * @param g Graphics2D对象
     */
    public abstract void render(Graphics2D g);

    // ==================== 通用方法 ====================

    /**
     * 获取对象的碰撞矩形
     * @return 碰撞矩形
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * 检测是否与另一个对象碰撞
     * @param other 另一个游戏对象
     * @return 是否碰撞
     */
    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }

    /**
     * 检测指定点是否在对象内
     * @param px X坐标
     * @param py Y坐标
     * @return 是否在对象内
     */
    public boolean contains(int px, int py) {
        return getBounds().contains(px, py);
    }

    /**
     * 标记为死亡（待移除）
     */
    public void die() {
        this.alive = false;
    }

    // ==================== Getters and Setters ====================

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if (image != null) {
            // 如果图片已设置但宽高未设置，使用图片尺寸
            if (width == 0) {
                this.width = image.getWidth();
            }
            if (height == 0) {
                this.height = image.getHeight();
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // ==================== 位置移动方法 ====================

    /**
     * 移动对象
     * @param dx X方向移动距离
     * @param dy Y方向移动距离
     */
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * 设置位置
     * @param x X坐标
     * @param y Y坐标
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 获取中心点X坐标
     */
    public int getCenterX() {
        return x + width / 2;
    }

    /**
     * 获取中心点Y坐标
     */
    public int getCenterY() {
        return y + height / 2;
    }

    // ==================== 默认渲染方法 ====================

    /**
     * 默认渲染方法，直接绘制图片
     * 子类可重写以实现更复杂的渲染效果
     * @param g Graphics2D对象
     */
    protected void renderImage(Graphics2D g) {
        if (image != null && visible) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    /**
     * 获取位置
     */
    public Point getPosition() {
        return new Point(x, y);
    }

    /**
     * 重置对象状态
     */
    public void reset() {
        this.alive = true;
        this.visible = true;
        this.x = 0;
        this.y = 0;
    }

    /**
     * 检查对象是否活跃
     */
    public boolean isActive() {
        return alive && visible;
    }
}