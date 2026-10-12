package com.ok.game.zombies;

import com.ok.game.entities.Zombie;
import com.ok.resource.ResourceManager;

import java.awt.*;

/**
 * 普通僵尸
 * 最基础的僵尸类型，移动速度中等，生命值中等
 */
public class NormalZombie extends Zombie {

    /**
     * 构造函数
     * @param row 所在行
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     */
    public NormalZombie(int row, int x, int y) {
        super(row, x, y, 80, 100, 100, 10, 20); // 宽度80，高度100，生命值100，伤害10，速度20
        loadImage();
    }

    @Override
    protected void loadImage() {
        ResourceManager rm = ResourceManager.getInstance();
        // 加载行走动画
        this.walkingImage = rm.getZombieAnimationImage("普通僵尸", "walking");
        // 加载啃食动画
        this.eatingImage = rm.getZombieAnimationImage("普通僵尸", "eating");
        // 加载死亡动画
        this.dyingImage = rm.getZombieAnimationImage("普通僵尸", "dying");
    }
}