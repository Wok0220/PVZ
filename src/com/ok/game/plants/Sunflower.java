package com.ok.game.plants;

import com.ok.game.entities.Plant;
import com.ok.game.entities.Sun;

import static com.ok.utils.Constants.SUNFLOWER_PRODUCE_INTERVAL;

/**
 * 向日葵
 * 生产型植物，定期产生阳光
 */
public class Sunflower extends Plant {

    /** 生产阳光的冷却时间（毫秒） */
    private long produceCooldown;

    /** 上次生产阳光的时间 */
    private long lastProduceTime;

    /**
     * 构造函数
     * @param row 网格行
     * @param col 网格列
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     */
    public Sunflower(int row, int col, int x, int y) {
        super("向日葵", row, col, x, y, 70, 70, 50); // 阳光消耗50
        this.produceCooldown = SUNFLOWER_PRODUCE_INTERVAL;
        this.lastProduceTime = 0;
    }

    /**
     * 更新逻辑
     * @param deltaTime 帧间隔时间（秒）
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // 检查是否可以生产阳光
        if (canProduce()) {
            produceSun();
        }
    }

    /**
     * 检查是否可以生产阳光
     * @return 是否可以生产
     */
    protected boolean canProduce() {
        return System.currentTimeMillis() - lastProduceTime > produceCooldown;
    }

    /**
     * 生产阳光
     */
    protected void produceSun() {
        lastProduceTime = System.currentTimeMillis();

        // 创建阳光
        Sun sun = new Sun(x, y);

        // 添加到游戏中
        if (gameManager != null) {
            gameManager.addSun(sun);
        }
    }

    /**
     * 攻击方法（向日葵不攻击）
     */
    @Override
    protected void attack() {
        // 向日葵不攻击，空实现
    }
}