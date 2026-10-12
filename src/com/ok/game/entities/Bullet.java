package com.ok.game.entities;

import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import java.awt.*;

/**
 * 子弹类
 * 由攻击型植物发射，对僵尸造成伤害
 */
public class Bullet extends GameObject implements Attackable {

    // ==================== 子弹类型枚举 ====================

    /**
     * 子弹类型
     */
    public enum BulletType {
        NORMAL,     // 普通豌豆
        SNOW        // 寒冰豌豆（减速）
    }

    // ==================== 基础属性 ====================

    /** 子弹类型 */
    protected BulletType type;

    /** 伤害值 */
    protected int damage;

    /** 移动速度（像素/秒） */
    protected int speed;

    /** 所在行（0-5） */
    protected int row;

    /** 发射者植物ID（用于统计） */
    protected String sourcePlantId;

    // ==================== 特殊效果相关 ====================

    /** 是否带减速效果（寒冰豌豆） */
    protected boolean hasSlowEffect;

    /** 减速持续时间（毫秒） */
    protected int slowDuration;

    /** 减速幅度（0-1，0.5表示速度减半） */
    protected float slowAmount;

    /** 是否穿透（穿透多个僵尸） */
    protected boolean isPiercing;

    /** 已穿透次数 */
    protected int pierceCount;

    /** 最大穿透次数 */
    protected int maxPierce;

    // ==================== 构造函数 ====================

    /**
     * 构造函数（普通子弹）
     */
    public Bullet(int row, int x, int y, int width, int height, int damage) {
        super(x, y, width, height);
        this.row = row;
        this.damage = damage;
        this.type = BulletType.NORMAL;
        this.speed = Constants.BULLET_SPEED;

        // 根据类型设置特殊效果
        initByType();

        // 加载子弹图片
        loadImage();
    }

    /**
     * 构造函数（指定参数）
     */
    public Bullet(int x, int y, int row, int damage, BulletType type) {
        super(x, y, Constants.BULLET_WIDTH, Constants.BULLET_HEIGHT);
        this.row = row;
        this.damage = damage;
        this.type = type;
        this.speed = Constants.BULLET_SPEED;

        // 根据类型设置特殊效果
        initByType();

        // 加载子弹图片
        loadImage();
    }

    /**
     * 根据子弹类型初始化特殊效果
     */
    protected void initByType() {
        switch (type) {
            case NORMAL:
                this.hasSlowEffect = false;
                this.isPiercing = false;
                break;

            case SNOW:
                this.hasSlowEffect = true;
                this.slowDuration = 3000;      // 减速3秒
                this.slowAmount = 0.5f;         // 速度减半
                this.isPiercing = false;
                break;
        }
    }

    /**
     * 加载子弹图片
     */
    protected void loadImage() {
        // 根据类型加载不同图片
        String imageName;
        switch (type) {
            case SNOW:
                imageName = "冰豆";
                break;
            default:
                imageName = "豆";
                break;
        }
        // 尝试从资源管理器加载图片
        this.image = ResourceManager.getInstance().getBulletImage(imageName);
    }

    // ==================== 更新逻辑 ====================

    @Override
    public void update(float deltaTime) {
        if (!isAlive()) return;

        x += speed * deltaTime;

        if (x > Constants.WINDOW_WIDTH) {
            die();
        }
    }

    // ==================== 碰撞处理 ====================

    @Override
    public boolean onHit(Zombie zombie) {
        if (!isAlive()) return true;

        // 造成伤害
        zombie.takeDamage(damage);

        // 应用减速效果
        if (hasSlowEffect && zombie.isAlive()) {
            applySlowEffect(zombie);
        }

        // 穿透子弹处理
        if (isPiercing) {
            pierceCount++;
            if (pierceCount >= maxPierce) {
                return true;  // 达到最大穿透次数，移除子弹
            }
            return false;  // 继续飞行
        }

        return true;  // 普通子弹，命中后移除
    }

    /**
     * 应用减速效果
     * @param zombie 目标僵尸
     */
    protected void applySlowEffect(Zombie zombie) {
        // 保存原速度（如果还没被减速）
        // 实际实现中，僵尸应该有减速状态管理
        // 这里简化处理，直接修改僵尸速度
        int originalSpeed = zombie.getSpeed();
        int slowedSpeed = (int)(originalSpeed * slowAmount);
        zombie.setSpeed(slowedSpeed);

        // 延迟恢复速度
        // 实际实现中可以用定时器或维护一个减速效果列表
        // 这里预留接口，由GameManager管理
        scheduleSpeedRestore(zombie, originalSpeed, slowDuration);
    }

    /**
     * 安排速度恢复（由外部系统实现）
     * @param zombie 僵尸
     * @param originalSpeed 原始速度
     * @param duration 持续时间
     */
    protected void scheduleSpeedRestore(Zombie zombie, int originalSpeed, int duration) {
        // 实际实现中，可以通过 GameManager 或 EffectManager 来管理
        // 例如：EffectManager.getInstance().addSlowEffect(zombie, originalSpeed, duration);
    }

    // ==================== 渲染 ====================

    @Override
    public void render(Graphics2D g) {
        if (!visible || image == null) return;

        // 绘制子弹图片
        g.drawImage(image, x, y, width, height, null);

        // 可选：绘制拖尾效果（寒冰）
        drawTrailEffect(g);
    }

    /**
     * 绘制拖尾效果
     */
    protected void drawTrailEffect(Graphics2D g) {
        if (type == BulletType.SNOW) {
            // 寒冰拖尾效果
            g.setColor(new Color(100, 200, 255, 100));
            g.fillOval(x - 5, y + 2, 8, 8);
        }
    }

    // ==================== 重置方法 ====================

    @Override
    public void reset() {
        super.reset();
        this.row = 0;
        this.damage = 0;
        this.speed = Constants.BULLET_SPEED;
        this.type = BulletType.NORMAL;
        this.sourcePlantId = null;
        this.hasSlowEffect = false;
        this.slowDuration = 0;
        this.slowAmount = 0;
        this.isPiercing = false;
        this.pierceCount = 0;
        this.maxPierce = 0;
    }

    // ==================== Getters ====================

    public BulletType getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public int getRow() {
        return row;
    }

    public String getSourcePlantId() {
        return sourcePlantId;
    }

    public boolean hasSlowEffect() {
        return hasSlowEffect;
    }

    public boolean isPiercing() {
        return isPiercing;
    }

    // ==================== Setters ====================

    public void setSourcePlantId(String sourcePlantId) {
        this.sourcePlantId = sourcePlantId;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}