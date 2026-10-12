package com.ok.game.entities;

import com.ok.game.core.GameManager;
import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import java.awt.*;

/**
 * 植物基类
 * 所有植物的父类
 */
public class Plant extends GameObject implements Damageable {

    // ==================== 基础属性 ====================

    /** 植物名称 */
    private String plantName;

    /** 阳光消耗 */
    protected int sunCost;

    /** 生命值 */
    protected int health;

    /** 攻击范围（像素） */
    protected int attackRange;

    /** 攻击冷却时间（毫秒） */
    protected long attackCooldown;

    /** 上次攻击时间 */
    protected long lastAttackTime;

    /** 所在网格行 */
    protected int row;

    /** 所在网格列 */
    protected int col;

    /** 游戏管理器引用 */
    protected GameManager gameManager;

    /** 植物图片（使用Image而不是BufferedImage，保留动画效果） */
    protected Image plantImage;

    // ==================== 构造函数 ====================

    /**
     * 构造函数
     * @param plantName 植物名称
     * @param row 网格行
     * @param col 网格列
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @param width 宽度
     * @param height 高度
     * @param sunCost 阳光消耗
     */
    public Plant(String plantName, int row, int col, int x, int y, int width, int height, int sunCost) {
        super(x, y, width, height);
        this.plantName = plantName;
        this.row = row;
        this.col = col;
        this.sunCost = sunCost;
        this.health = Constants.PLANT_DEFAULT_HEALTH;
        this.attackRange = Constants.PLANT_ATTACK_RANGE;
        this.attackCooldown = Constants.PLANT_ATTACK_COOLDOWN;
        this.lastAttackTime = 0;
        loadImage();
    }

    /**
     * 设置游戏管理器
     * @param gameManager 游戏管理器
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // ==================== 更新逻辑 ====================

    @Override
    public void update(float deltaTime) {
        if (!isAlive()) return;

        // 检查是否可以攻击
        if (canAttack()) {
            attack();
        }
    }

    /**
     * 检查是否可以攻击
     */
    protected boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime > attackCooldown;
    }

    /**
     * 攻击方法，子类实现
     */
    protected void attack() {
        // 子类实现具体攻击逻辑
        lastAttackTime = System.currentTimeMillis();
    }

    // ==================== 伤害处理 ====================

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0 && isAlive()) {
            die();
        }
    }

    /**
     * 死亡处理
     */
    @Override
    public void die() {
        super.die();
        if (gameManager != null) {
            gameManager.removePlant(this);
        }
    }

    // ==================== 渲染 ====================

    @Override
    public void render(Graphics2D g) {
        renderImage(g);
    }

    // ==================== 图片管理 ====================

    /**
     * 加载图片
     */
    protected void loadImage() {
        ResourceManager rm = ResourceManager.getInstance();
        // 直接加载动画图片（GIF），返回Image对象
        this.plantImage = rm.getPlantImageAsImage(plantName, true);
        // 设置到父类的image字段
        this.image = plantImage;
    }

    // ==================== 重置方法 ====================

    @Override
    public void reset() {
        super.reset();
        this.health = Constants.PLANT_DEFAULT_HEALTH;
        this.lastAttackTime = 0;
        loadImage();
    }

    // ==================== Getters ====================

    public String getPlantName() {
        return plantName;
    }

    public int getSunCost() {
        return sunCost;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Image getPlantedImage() {
        return plantImage;
    }

    // ==================== Setters ====================

    public void setHealth(int health) {
        this.health = health;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public void setPlantImage(Image plantImage) {
        this.plantImage = plantImage;
        this.image = plantImage;
    }
}