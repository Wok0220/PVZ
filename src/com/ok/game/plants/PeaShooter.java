package com.ok.game.plants;

import com.ok.game.entities.Bullet;
import com.ok.game.entities.Plant;

/**
 * 豌豆射手
 * 基础攻击植物，发射豌豆子弹
 */
public class PeaShooter extends Plant {

    /**
     * 构造函数
     */
    public PeaShooter(int row, int col, int x, int y) {
        super("豌豆射手", row, col, x, y, 70, 70, 100); // 阳光消耗100
    }

    /**
     * 攻击方法
     */
    @Override
    protected void attack() {
        super.attack();

        // 检查是否有僵尸在攻击范围内
        if (gameManager != null && gameManager.hasZombieInFront(row, x)) {
            // 创建子弹
            Bullet bullet = new Bullet(row, x + width, y + height / 2, 20, 20, 20);
            // 发射子弹
            gameManager.shootBullet(bullet);
        }
    }
}