package com.ok.game.systems;

import com.ok.game.core.GameManager;
import com.ok.game.entities.Sun;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 阳光系统
 * 管理阳光的生成、收集和消失
 */
public class SunSystem {

    /** 单例实例 */
    private static SunSystem instance;

    /** 阳光列表 */
    private List<Sun> suns;

    /** 待移除的阳光列表 */
    private List<Sun> sunsToRemove;

    /** 游戏管理器引用 */
    private GameManager gameManager;

    /** 阳光生成计时器 */
    private float sunSpawnTimer;

    /**
     * 私有构造函数
     */
    private SunSystem() {
        this.suns = new CopyOnWriteArrayList<>();
        this.sunsToRemove = new CopyOnWriteArrayList<>();
        this.sunSpawnTimer = 0;
    }

    /**
     * 获取单例实例
     */
    public static SunSystem getInstance() {
        if (instance == null) {
            instance = new SunSystem();
        }
        return instance;
    }

    /**
     * 初始化阳光系统
     * @param gameManager 游戏管理器
     */
    public void init(GameManager gameManager) {
        this.gameManager = gameManager;
        clear();
    }

    /**
     * 清空所有阳光
     */
    public void clear() {
        suns.clear();
        sunsToRemove.clear();
        sunSpawnTimer = 0;
    }

    /**
     * 添加阳光
     * @param sun 阳光对象
     */
    public void addSun(Sun sun) {
        suns.add(sun);
    }

    /**
     * 更新阳光系统
     * @param deltaTime 帧间隔时间（秒）
     */
    public void update(float deltaTime) {
        // 更新所有阳光
        for (Sun sun : suns) {
            sun.update(deltaTime);
            // 检查是否超时或已收集
            if (!sun.isAlive() || sun.getState() == Sun.SunState.COLLECTING || sun.getState() == Sun.SunState.TIMEOUT) {
                sunsToRemove.add(sun);
            }
        }
        // 处理待移除的阳光
        processRemovals();
    }

    /**
     * 处理待移除的阳光
     */
    private void processRemovals() {
        suns.removeAll(sunsToRemove);
        sunsToRemove.clear();
    }

    /**
     * 尝试收集阳光
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     * @return 是否成功收集
     */
    public boolean tryCollectSun(int x, int y) {
        for (Sun sun : suns) {
            if (sun.isVisible() && sun.contains(x, y)) {
                // 收集阳光
                sun.collect(0, 0); // 目标坐标暂时设为(0,0)
                sunsToRemove.add(sun);
                if (gameManager != null) {
                    gameManager.addSunAmount(sun.getValue());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有阳光
     */
    public List<Sun> getSuns() {
        return suns;
    }


    /**
     * 设置阳光计数器位置
     * @param x X坐标
     * @param y Y坐标
     */
    public void setSunCounterPosition(int x, int y) {
        // 预留实现
    }

    /**
     * 移除阳光（标记待移除）
     * @param sun 阳光对象
     */
    public void removeSun(Sun sun) {
        if (!sunsToRemove.contains(sun)) {
            sunsToRemove.add(sun);
        }
    }
}