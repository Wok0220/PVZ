package com.ok.game.zombies;

import com.ok.game.entities.Zombie;
import com.ok.resource.ResourceManager;

import java.awt.*;

/**
 * 路障僵尸
 * 普通僵尸的强化版，头上戴着路障，生命值更高
 */
public class ConeheadZombie extends Zombie {

    /** 路障是否还在（未被打掉） */
    private boolean hasCone;

    /** 路障生命值 */
    private int coneHealth;

    /** 路障最大生命值 */
    private static final int CONE_MAX_HEALTH = 40;

    /**
     * 构造函数
     * @param row 所在行
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     */
    public ConeheadZombie(int row, int x, int y) {
        super(row, x, y, 80, 100, 140, 10, 12); // 宽度80，高度100，生命值140（包含路障），伤害10，速度12

        this.hasCone = true;
        this.coneHealth = CONE_MAX_HEALTH;
        loadImage();
    }

    @Override
    protected void loadImage() {
        ResourceManager rm = ResourceManager.getInstance();
        // 加载行走动画
        this.walkingImage = rm.getZombieAnimationImage("路障僵尸", "walking");
        // 加载啃食动画
        this.eatingImage = rm.getZombieAnimationImage("路障僵尸", "eating");
        // 加载死亡动画
        this.dyingImage = rm.getZombieAnimationImage("路障僵尸", "dying");
    }

    @Override
    public void takeDamage(int damage) {
        if (!isAlive()) return;

        // 先扣路障生命值
        if (hasCone && coneHealth > 0) {
            coneHealth -= damage;
            if (coneHealth <= 0) {
                hasCone = false;
                coneHealth = 0;
                onConeDestroyed();
            }
            return;
        }

        // 路障已损坏，扣本体生命值
        super.takeDamage(damage);
    }

    /**
     * 路障被破坏时的回调
     */
    protected void onConeDestroyed() {
        // 播放路障破碎音效
        // AudioManager.getInstance().playSound("cone_break");
    }

    // ==================== Getters ====================

    /**
     * 是否还有路障
     */
    public boolean hasCone() {
        return hasCone;
    }

    /**
     * 获取路障剩余生命值
     */
    public int getConeHealth() {
        return coneHealth;
    }

    /**
     * 获取路障血量百分比
     */
    public float getConeHealthPercent() {
        return (float) coneHealth / CONE_MAX_HEALTH;
    }
}