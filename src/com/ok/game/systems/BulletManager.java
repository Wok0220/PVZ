package com.ok.game.systems;

import com.ok.game.core.GameManager;
import com.ok.game.entities.Bullet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 子弹管理器
 * 管理子弹的发射、更新和碰撞检测
 */
public class BulletManager {

    /** 单例实例 */
    private static BulletManager instance;

    /** 子弹列表 */
    private List<Bullet> bullets;

    /** 待移除的子弹列表 */
    private List<Bullet> bulletsToRemove;

    /** 游戏管理器引用 */
    private GameManager gameManager;

    /**
     * 私有构造函数
     */
    private BulletManager() {
        this.bullets = new CopyOnWriteArrayList<>();
        this.bulletsToRemove = new CopyOnWriteArrayList<>();
    }

    /**
     * 获取单例实例
     */
    public static BulletManager getInstance() {
        if (instance == null) {
            instance = new BulletManager();
        }
        return instance;
    }

    /**
     * 初始化子弹管理器
     * @param gameManager 游戏管理器
     */
    public void init(GameManager gameManager) {
        this.gameManager = gameManager;
        clear();
    }

    /**
     * 清空所有子弹
     */
    public void clear() {
        bullets.clear();
        bulletsToRemove.clear();
    }

    /**
     * 添加子弹
     * @param bullet 子弹对象
     */
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    /**
     * 移除子弹
     * @param bullet 子弹对象
     */
    public void removeBullet(Bullet bullet) {
        bulletsToRemove.add(bullet);
    }

    /**
     * 更新子弹管理器
     * @param deltaTime 帧间隔时间（秒）
     */
    public void update(float deltaTime) {
        // 更新所有子弹
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }

        // 处理待移除的子弹
        processRemovals();
    }

    /**
     * 处理待移除的子弹
     */
    private void processRemovals() {
        bullets.removeAll(bulletsToRemove);
        bulletsToRemove.clear();
    }

    /**
     * 获取所有子弹
     */
    public List<Bullet> getBullets() {
        return bullets;
    }
}