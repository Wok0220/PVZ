package com.ok.game.core;

import com.ok.utils.Constants;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 游戏循环
 * 使用独立线程实现稳定的帧率更新
 * 每帧调用 update() 更新游戏逻辑，然后触发界面重绘
 */
public class GameLoop implements Runnable {

    /** 单例实例 */
    private static GameLoop instance;

    /** 游戏线程 */
    private Thread gameThread;

    /** 游戏管理器引用 */
    private GameManager gameManager;

    /** 上一帧的时间（纳秒） */
    private long lastTime;

    /** 帧率计算相关 */
    private long lastFpsTime;
    private int frameCount;
    private int currentFps;

    /** 是否正在运行 */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /** 是否暂停 */
    private AtomicBoolean isPaused = new AtomicBoolean(false);

    /** 目标帧率 */
    private final int targetFps = Constants.TARGET_FPS;

    /** 每帧目标时间（纳秒） */
    private final long targetFrameTime = 1_000_000_000 / targetFps;

    /**
     * 私有构造函数
     */
    private GameLoop() {
        this.lastTime = System.nanoTime();
        this.lastFpsTime = System.nanoTime();
        this.frameCount = 0;
        this.currentFps = 0;
    }

    /**
     * 获取单例实例
     */
    public static GameLoop getInstance() {
        if (instance == null) {
            synchronized (GameLoop.class) {
                if (instance == null) {
                    instance = new GameLoop();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化游戏循环
     * @param gameManager 游戏管理器
     */
    public void init(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * 开始游戏循环
     */
    public void start() {
        if (isRunning.get()) {
            return;
        }

        isRunning.set(true);
        isPaused.set(false);
        lastTime = System.nanoTime();
        lastFpsTime = System.nanoTime();
        frameCount = 0;

        // 创建并启动游戏线程
        gameThread = new Thread(this, "GameLoopThread");
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    /**
     * 停止游戏循环
     */
    public void stop() {
        if (!isRunning.get()) {
            return;
        }

        isRunning.set(false);

        // 等待线程结束
        if (gameThread != null) {
            try {
                gameThread.join(1000); // 等待最多1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停游戏循环（逻辑更新暂停，但帧循环继续）
     */
    public void pause() {
        isPaused.set(true);
    }

    /**
     * 恢复游戏循环
     */
    public void resume() {
        isPaused.set(false);
        lastTime = System.nanoTime(); // 重置时间，避免 deltaTime 过大
    }

    /**
     * 游戏循环主方法
     */
    @Override
    public void run() {
        while (isRunning.get()) {
            // 1. 记录当前时间（循环开始时）
            long currentTime = System.nanoTime();

            // 2. 计算时间差
            long elapsedTime = currentTime - lastTime;
            float deltaTime = elapsedTime / 1_000_000_000.0f;

            // 3. 限制最大 deltaTime（避免卡顿跳跃）
            if (deltaTime > 0.1f) {
                deltaTime = 0.1f;
            }

            // 4. 更新游戏逻辑
            if (!isPaused.get() && gameManager != null && gameManager.isGameActive()) {
                gameManager.update(deltaTime);
            }

            // 5. 请求重绘
            if (gameManager != null) {
                SwingUtilities.invokeLater(() -> gameManager.requestRepaint());
            }

            // 6. 帧率控制（可选）
            long sleepTime = targetFrameTime - (System.nanoTime() - currentTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 7. 更新 lastTime（使用循环开始时的时间）
            lastTime = currentTime;
        }
    }

    // ==================== Getters ====================

    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * 是否暂停
     */
    public boolean isPaused() {
        return isPaused.get();
    }

    /**
     * 获取当前帧率（调试用）
     */
    public int getCurrentFPS() {
        return currentFps;
    }
}