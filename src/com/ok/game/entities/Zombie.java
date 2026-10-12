package com.ok.game.entities;

import com.ok.game.core.GameManager;
import com.ok.utils.Constants;

import java.awt.*;

/**
 * 僵尸基类
 * 所有僵尸的父类
 */
public abstract class Zombie extends GameObject implements Damageable {

    // ==================== 状态定义 ====================

    public enum ZombieState {
        WALKING,   // 行走
        EATING,    // 啃食
        DYING,     // 死亡
        DEAD       // 已死亡
    }

    // ==================== 基础属性 ====================

    /** 生命值 */
    protected int health;

    /** 伤害值 */
    protected int damage;

    /** 移动速度（像素/秒） */
    protected int speed;

    /** 攻击间隔（毫秒） */
    protected long attackCooldown;

    /** 上次攻击时间 */
    protected long lastAttackTime;

    /** 所在行 */
    protected int row;

    /** 原始速度（用于减速效果恢复） */
    protected int originalSpeed;

    /** 游戏管理器引用 */
    protected GameManager gameManager;

    /** 当前状态 */
    protected ZombieState currentState;

    /** 死亡动画计时器 */
    protected long deathTimer;

    /** 死亡动画持续时间（毫秒） */
    protected static final long DEATH_ANIMATION_DURATION = 1000;

    /** 行走动画 */
    protected Image walkingImage;

    /** 啃食动画 */
    protected Image eatingImage;

    /** 死亡动画 */
    protected Image dyingImage;

    // ==================== 构造函数 ====================

    /**
     * 构造函数
     */
    public Zombie(int row, int x, int y, int width, int height, int health, int damage, int speed) {
        super(x, y, width, height);
        this.row = row;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.originalSpeed = speed;
        this.attackCooldown = Constants.ZOMBIE_ATTACK_INTERVAL;
        this.lastAttackTime = 0;
        this.currentState = ZombieState.WALKING;
        this.deathTimer = 0;
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
        if (currentState == ZombieState.DEAD) return;

        switch (currentState) {
            case WALKING:
                updateWalking(deltaTime);
                break;
            case EATING:
                updateEating(deltaTime);
                break;
            case DYING:
                updateDying(deltaTime);
                break;
        }
    }

    /**
     * 更新行走状态
     */
    protected void updateWalking(float deltaTime) {
        // 移动
        moveLeft(deltaTime);

        // 检查是否到达房子
        checkReachHouse();

        // 检查是否有植物可攻击
        checkForPlants();
    }

    /**
     * 更新啃食状态
     */
    protected void updateEating(float deltaTime) {
        Plant targetPlant = getTargetPlant();

        // 如果目标植物不存在或已死亡，切换回行走状态
        if (targetPlant == null || !targetPlant.isAlive()) {
            currentState = ZombieState.WALKING;
            return;
        }

        // 检查是否在攻击范围内
        int distance = targetPlant.getX() + targetPlant.getWidth() - this.x;
        if (distance > 0 && distance < 10) {
            attack(targetPlant);
        }
    }

    /**
     * 更新死亡状态
     */
    protected void updateDying(float deltaTime) {
        deathTimer += deltaTime * 1000;
        if (deathTimer >= DEATH_ANIMATION_DURATION) {
            currentState = ZombieState.DEAD;
            visible = false;
            // 确保生命值为0
            health = 0;
            // 通知游戏管理器移除僵尸
            if (gameManager != null) {
                gameManager.removeZombie(this);
            }
        }
    }

    /**
     * 向左移动
     */
    protected void moveLeft(float deltaTime) {
        x -= speed * deltaTime;
    }

    /**
     * 检查是否到达房子
     */
    protected void checkReachHouse() {
        // 检查是否到达界面左侧边界
        if (x == 0) { // 左侧边界
            // 通知游戏管理器僵尸到达边界
            if (gameManager != null) {
                gameManager.onZombieReachHouse();
            }
            // 立即设置为死亡状态，不播放死亡动画
            currentState = ZombieState.DEAD;
            visible = false;
            // 通知游戏管理器移除僵尸
            if (gameManager != null) {
                gameManager.removeZombie(this);
            }
        }
    }

    /**
     * 检查是否有植物可攻击
     */
    protected void checkForPlants() {
        Plant targetPlant = getTargetPlant();
        if (targetPlant != null && targetPlant.isAlive()) {
            int distance = this.x - targetPlant.getX();

            if (distance > 0 && distance < 30) {
                currentState = ZombieState.EATING;
            }
        }
    }

    /**
     * 获取目标植物
     */
    protected Plant getTargetPlant() {
        if (gameManager == null) return null;
        // 这里需要实现获取当前行最前面的植物
        // 假设GameManager有方法获取指定行的植物
        return gameManager.getPlantInRow(row, x);
    }

    /**
     * 攻击植物
     */
    public void attack(Plant plant) {
        if (System.currentTimeMillis() - lastAttackTime > attackCooldown) {
            plant.takeDamage(damage);
            lastAttackTime = System.currentTimeMillis();
        }
    }

    // ==================== 伤害处理 ====================

    protected abstract void loadImage();

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0 && currentState != ZombieState.DYING && currentState != ZombieState.DEAD) {
            die();
        }
    }

    /**
     * 死亡处理
     */
    public void die() {
        currentState = ZombieState.DYING;
        deathTimer = 0;
        health = 0;
    }

    // ==================== 渲染 ====================

    @Override
    public void render(Graphics2D g) {
        if (visible && currentState != ZombieState.DEAD) {
            renderImage(g);
        }
    }

    /**
     * 渲染图片
     */
    protected void renderImage(Graphics2D g) {
        Image currentImage = null;
        switch (currentState) {
            case WALKING:
                currentImage = walkingImage;
                break;
            case EATING:
                currentImage = eatingImage;
                break;
            case DYING:
                currentImage = dyingImage;
                break;
        }

        if (currentImage != null) {
            g.drawImage(currentImage, x, y, width, height, null);
        }
    }

    // ==================== 重置方法 ====================

    @Override
    public void reset() {
        super.reset();
        this.health = 100; // 默认生命值
        this.speed = originalSpeed;
        this.lastAttackTime = 0;
        this.currentState = ZombieState.WALKING;
        this.deathTimer = 0;
        loadImage();
    }

    // ==================== Getters ====================

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getSpeed() {
        return speed;
    }

    public int getRow() {
        return row;
    }

    public int getOriginalSpeed() {
        return originalSpeed;
    }

    public ZombieState getCurrentState() {
        return currentState;
    }

    // ==================== Setters ====================

    public void setHealth(int health) {
        this.health = health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查僵尸是否存活
     * @return 是否存活
     */
    public boolean isAlive() {
        return health > 0 && currentState != ZombieState.DYING && currentState != ZombieState.DEAD;
    }

    /**
     * 检查僵尸是否可见
     * @return 是否可见
     */
    public boolean isVisible() {
        return visible && currentState != ZombieState.DEAD;
    }
}