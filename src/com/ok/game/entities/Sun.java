package com.ok.game.entities;

import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import java.awt.*;

/**
 * 阳光类
 * 游戏中收集的资源，用于种植植物
 */
public class Sun extends GameObject {
    @Override
    public void reset() {

    }

    @Override
    public boolean isActive() {
        return alive && visible && state != SunState.TIMEOUT;
    }

    @Override
    public Point getPosition() {
        return new Point(x, y);
    }

// ==================== 阳光状态枚举 ====================

    /**
     * 阳光状态
     */
    public enum SunState {
        FALLING,    // 正在掉落
        FLOATING,   // 停留在地面附近飘浮
        COLLECTING, // 正在被收集（飞向计数器）
        TIMEOUT     // 超时消失
    }

    // ==================== 基础属性 ====================

    /** 阳光价值（默认25） */
    protected int value;

    /** 当前状态 */
    protected SunState state;

    /** 存在时间（秒），超时后消失 */
    protected float lifeTimer;

    /** 最大存在时间（秒） */
    protected float maxLifeTime;

    /** 阳光创建时间（毫秒） */
    private long createTime;

    // ==================== 掉落相关 ====================

    /** 掉落速度（像素/秒） */
    protected float fallSpeed;

    /** 起始掉落Y坐标 */
    protected int startY;

    /** 目标Y坐标（掉落停止位置） */
    protected int targetY;

    /** 累积下落距离（用于处理小数部分） */
    private float accumulatedFallDistance = 0;

    // ==================== 动画效果 ====================

    /** 飘浮动画相位 */
    protected float floatPhase;

    /** 飘浮幅度 */
    protected float floatAmplitude;

    // ==================== 构造函数 ====================

    /**
     * 构造函数（天上掉落）
     * @param x X坐标
     * @param y 起始Y坐标
     * @param targetY 掉落目标Y坐标
     */
    public Sun(int x, int y, int targetY) {
        this(x, y, targetY, Constants.SUN_PRODUCE_AMOUNT);
    }

    /**
     * 构造函数（天上掉落，指定价值）
     * @param x X坐标
     * @param y 起始Y坐标
     * @param targetY 掉落目标Y坐标
     * @param value 阳光价值
     */
    public Sun(int x, int y, int targetY, int value) {
        super(x, y, 40, 40);  // 阳光尺寸40x40

        this.value = value;
        this.state = SunState.FALLING;
        this.startY = y;
        this.targetY = targetY;
        this.fallSpeed = Constants.SUN_FALL_SPEED;
        this.maxLifeTime = Constants.SUN_LIFESPAN;
        this.lifeTimer = 0.0f;
        this.accumulatedFallDistance = 0;  // 初始化累积距离
        this.createTime = System.currentTimeMillis(); // 初始化创建时间

        // 动画效果初始化
        this.floatPhase = 0;
        this.floatAmplitude = 3;

        // 加载阳光图片
        loadImage();

        // 设置初始位置
        this.y = y;
    }

    /**
     * 构造函数（向日葵生产）
     * @param x X坐标（植物位置）
     * @param y Y坐标（植物位置）
     */
    public Sun(int x, int y) {
        this(x+50, y+50, y+50, Constants.SUN_PRODUCE_AMOUNT);
        this.maxLifeTime = 10.0f;  // 向日葵产生的阳光设置为10秒
    }

    /**
     * 加载阳光图片
     */
    protected void loadImage() {
        this.image = ResourceManager.getInstance().getSunImage();
    }

    // ==================== 更新逻辑 ====================

    @Override
    public void update(float deltaTime) {
        // 检查存在时间
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - createTime;
        float lifeSeconds = elapsedTime / 1000.0f;

        if (lifeSeconds >= maxLifeTime) {
            this.state = SunState.TIMEOUT;
            die();
            return;
        }

        // 更新状态
        switch (state) {
            case FALLING:
                updateFalling(deltaTime);
                break;
            case FLOATING:
                updateFloating(deltaTime);
                break;
            case COLLECTING:
            case TIMEOUT: // 收集和超时状态都直接标记为死亡
                die();
                return;
        }

        // 更新飘浮动画
        floatPhase += deltaTime * 3;
        if (floatPhase > Math.PI * 2) {
            floatPhase -= Math.PI * 2;
        }
    }

    /**
     * 更新掉落状态
     */
    protected void updateFalling(float deltaTime) {
        // 计算本次移动距离
        float moveDistance = fallSpeed * deltaTime;

        // 累积移动距离
        accumulatedFallDistance += moveDistance;

        // 当累积距离达到或超过 1 时，更新 y 坐标
        if (accumulatedFallDistance >= 1.0f) {
            // 计算实际移动的整数距离
            int intDistance = (int) accumulatedFallDistance;
            // 更新 y 坐标
            y += intDistance;
            // 减去已使用的距离，保留小数部分
            accumulatedFallDistance -= intDistance;
        }

        // 到达目标位置，转为飘浮状态
        if (y >= targetY) {
            y = targetY;
            state = SunState.FLOATING;
            // 重置累积距离
            accumulatedFallDistance = 0;
        }
    }

    /**
     * 更新飘浮状态
     */
    protected void updateFloating(float deltaTime) {
        // 飘浮效果（上下轻微移动）
        float offset = (float)(Math.sin(floatPhase) * floatAmplitude);
    }

    // ==================== 收集逻辑 ====================

    public void collect(int targetX, int targetY) {
        if (state == SunState.COLLECTING) return;
        this.state = SunState.COLLECTING;
        die();
    }

    /**
     * 检查点是否在阳光范围内
     * @param px 点X坐标
     * @param py 点Y坐标
     * @return 是否在范围内
     */
    @Override
    public boolean contains(int px, int py) {
        // 扩大点击范围，便于收集
        int expandedSize = 10;
        return px >= x - expandedSize && px <= x + width + expandedSize &&
                py >= y - expandedSize && py <= y + height + expandedSize;
    }

    // ==================== 渲染 ====================

    @Override
    public void render(Graphics2D g) {
        if (!visible || image == null) return;

        // 计算飘浮偏移
        int drawY = y;
        if (state == SunState.FLOATING) {
            float offset = (float)(Math.sin(floatPhase) * floatAmplitude);
            drawY = y + (int)offset;
        }

        // 超时状态的淡出效果
        if (state == SunState.TIMEOUT) {
            // 计算透明度（根据超时时间逐渐降低）
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - createTime;
            float lifeSeconds = elapsedTime / 1000.0f;
            float alpha = 1.0f - (lifeSeconds - maxLifeTime) / 2.0f; // 2秒内淡出
            alpha = Math.max(0, alpha);

            // 设置透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        // 绘制阳光
        g.drawImage(image, x, drawY, width, height, null);

        // 重置透明度
        if (state == SunState.TIMEOUT) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    // ==================== Getters ====================

    public int getValue() {
        return value;
    }

    public SunState getState() {
        return state;
    }

    public boolean isCollecting() {
        return state == SunState.COLLECTING;
    }

    public float getLifePercent() {
        return 1 - (float) lifeTimer / maxLifeTime;
    }
}