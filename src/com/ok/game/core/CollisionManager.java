package com.ok.game.core;

import com.ok.game.entities.Bullet;
import com.ok.game.entities.Plant;
import com.ok.game.entities.Zombie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 碰撞检测管理器
 * 处理游戏中的碰撞检测
 */
public class CollisionManager {

    /** 单例实例 */
    private static CollisionManager instance;

    /**
     * 私有构造函数
     */
    private CollisionManager() {
    }

    /**
     * 获取单例实例
     */
    public static CollisionManager getInstance() {
        if (instance == null) {
            instance = new CollisionManager();
        }
        return instance;
    }

    /**
     * 检查所有碰撞
     * @param gameManager 游戏管理器
     */
    public void checkCollisions(GameManager gameManager) {
        // 检查子弹与僵尸的碰撞
        checkBulletZombieCollisions(gameManager);

        // 检查僵尸与植物的碰撞
        checkZombiePlantCollisions(gameManager);
    }

    /**
     * 检查子弹与僵尸的碰撞
     */
    private void checkBulletZombieCollisions(GameManager gameManager) {
        List<Bullet> bullets = gameManager.getBullets();
        List<Zombie> zombies = gameManager.getZombies();

        // 按行分组，减少碰撞检测次数
        Map<Integer, List<Zombie>> zombiesByRow = new HashMap<>();
        for (Zombie zombie : zombies) {
            if (zombie.isAlive()) {
                int row = zombie.getRow();
                zombiesByRow.computeIfAbsent(row, k -> new ArrayList<>()).add(zombie);
            }
        }

        // 只检测同一行的子弹和僵尸
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                int row = bullet.getRow();
                List<Zombie> rowZombies = zombiesByRow.get(row);
                if (rowZombies != null) {
                    for (Zombie zombie : rowZombies) {
                        if (zombie.isAlive() && bullet.collidesWith(zombie)) {
                            if (bullet.onHit(zombie)) {
                                // 子弹需要被移除
                                gameManager.removeBullet(bullet);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查僵尸与植物的碰撞
     */
    private void checkZombiePlantCollisions(GameManager gameManager) {
        List<Zombie> zombies = gameManager.getZombies();
        List<Plant> plants = gameManager.getGridManager().getAllPlants();

        for (Zombie zombie : zombies) {
            if (zombie.isAlive()) {
                for (Plant plant : plants) {
                    if (plant.isAlive() && zombie.collidesWith(plant)) {
                        // 僵尸攻击植物
                        zombie.attack(plant);
                    }
                }
            }
        }
    }
}