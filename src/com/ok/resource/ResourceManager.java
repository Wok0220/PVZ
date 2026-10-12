package com.ok.resource;

import com.ok.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 资源管理器
 * 单例模式，统一加载和管理游戏中的所有图片资源
 * 优化版本：支持预加载、软引用缓存、异步加载、Sprite Sheet等
 */
public class ResourceManager {

    /** 单例实例 */
    private static ResourceManager instance;

    /** 图片缓存 - 强引用缓存，用于常用图片 */
    private Map<String, BufferedImage> strongCache;

    /** 图片缓存 - 软引用缓存，用于不常用图片 */
    private Map<String, SoftReference<BufferedImage>> softCache;

    /** 缩放图片缓存 */
    private Map<String, BufferedImage> scaledCache;

    /** 动画帧缓存 */
    private Map<String, BufferedImage[]> animationCache;

    /** 异步加载线程池 */
    private ExecutorService executorService;

    /** 预加载状态 */
    private boolean preloadComplete;

    /** 加载中图片的占位符 */
    private BufferedImage loadingPlaceholder;

    /**
     * 私有构造函数
     */
    private ResourceManager() {
        strongCache = new ConcurrentHashMap<>();
        softCache = new ConcurrentHashMap<>();
        scaledCache = new ConcurrentHashMap<>();
        animationCache = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(4);
        preloadComplete = false;

        // 创建加载占位符
        loadingPlaceholder = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = loadingPlaceholder.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Loading...", 20, 50);
        g2d.dispose();
    }

    /**
     * 获取单例实例
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                if (instance == null) {
                    instance = new ResourceManager();
                }
            }
        }
        return instance;
    }

    // ==================== 预加载功能 ====================

    /**
     * 预加载所有图片资源
     */
    public void preloadAllResources() {
        System.out.println("开始预加载资源...");

        // 预加载背景图片
        preloadBackgrounds();

        // 预加载植物图片
        preloadPlants();

        // 预加载僵尸图片
        preloadZombies();

        // 预加载子弹图片
        preloadBullets();

        // 预加载UI图片
        preloadUI();

        // 预加载工具图片
        preloadTools();

        // 预加载卡片图片
        preloadCards();

        preloadComplete = true;
        System.out.println("资源预加载完成！");
        System.out.println("缓存大小: " + getTotalCacheSize());
    }

    /**
     * 预加载背景图片
     */
    private void preloadBackgrounds() {
        String[] backgrounds = {
                "主菜单背景", "介绍僵尸", "介绍植物", "僵尸介绍", "冒险模式",
                "图鉴", "图鉴亮", "土地", "地板", "墓碑", "房屋",
                "拼图游戏", "选关背景", "无限模式", "树木", "植物介绍",
                "游戏界面", "白天", "白天草坪", "草进度条", "进度条土地",
                "选择查看的僵尸","菜单","拼图背景","WoodSign1","WoodSign2","WoodSign3",
                "拼图","拼图计数","拼图图片",
                "植物全图鉴","僵尸全图鉴","豌豆射手介绍","向日葵介绍","普通僵尸介绍","路障僵尸介绍","介绍植物","介绍僵尸"
        };

        for (String bg : backgrounds) {
            getBackgroundImage(bg);
        }
    }

    /**
     * 预加载植物图片
     */
    private void preloadPlants() {
        String[] plants = {
                "向日葵", "土豆雷", "坚果墙", "寒冰射手", "樱桃炸弹", "豌豆射手"
        };

        for (String plant : plants) {
            // 预加载静态图片
            getPlantImage(plant, false);
            // 预加载动画图片
            getPlantImage(plant, true);
        }
    }

    /**
     * 预加载僵尸图片
     */
    private void preloadZombies() {
        String[] zombies = {
                "旗帜僵尸/旗帜僵尸", "旗帜僵尸/旗帜僵尸啃食",
                "普通僵尸/普通僵尸", "普通僵尸/普通僵尸啃食", "普通僵尸/普通僵尸走路",
                "路障僵尸/路障僵尸", "路障僵尸/路障僵尸啃食",
                "铁桶僵尸/铁桶僵尸", "铁桶僵尸/铁桶僵尸啃食"
        };

        for (String zombie : zombies) {
            String[] parts = zombie.split("/");
            if (parts.length == 2) {
                getZombieImage(parts[0]);
            }
        }
    }

    /**
     * 预加载子弹图片
     */
    private void preloadBullets() {
        String[] bullets = {
                "冰豆", "豆"
        };

        for (String bullet : bullets) {
            getBulletImage(bullet);
        }
    }

    /**
     * 预加载UI图片
     */
    private void preloadUI() {
        String[] ui = {
                "back", "menu亮", "quit", "不勾选", "准备",
                "勾选", "大波僵尸", "好", "开始", "最后一波",
                "箭头", "箭头左", "菜单","返回亮","脑吃僵","滑竿","僵吃脑"
        };

        for (String uiItem : ui) {
            getUIImage(uiItem);
        }
    }

    /**
     * 预加载工具图片
     */
    private void preloadTools() {
        getSunImage();
        getCartImage();
        getShovelImage();
    }

    /**
     * 预加载卡片图片
     */
    private void preloadCards() {
        String[] cards = {
                "1-1", "1-2", "1-3", "卡片槽", "卡片槽1",
                "向日葵", "土豆雷", "坚果墙", "寒冰射手", "樱桃炸弹", "豌豆射手",
        };

        for (String card : cards) {
            getCardImage(card);
        }
    }

    // ==================== 图片加载 ====================

    /**
     * 加载图片
     * @param path 图片路径（相对于 resources 目录）
     * @return 图片对象，加载失败返回 null
     */
    public BufferedImage loadImage(String path) {
        // 先检查强引用缓存
        if (strongCache.containsKey(path)) {
            return strongCache.get(path);
        }

        // 检查软引用缓存
        if (softCache.containsKey(path)) {
            SoftReference<BufferedImage> ref = softCache.get(path);
            BufferedImage image = ref.get();
            if (image != null) {
                // 将常用图片移到强引用缓存
                strongCache.put(path, image);
                softCache.remove(path);
                return image;
            }
        }

        // 从资源路径加载
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("图片资源不存在: " + path);
                return loadingPlaceholder;
            }
            BufferedImage image = ImageIO.read(is);
            if (image != null) {
                // 放入强引用缓存
                strongCache.put(path, image);
                // 如果缓存过大，清理一些不常用的图片到软引用缓存
                if (strongCache.size() > 100) {
                    cleanupCache();
                }
            }
            return image;
        } catch (IOException e) {
            System.err.println("加载图片失败: " + path);
            e.printStackTrace();
            return loadingPlaceholder;
        }
    }

    /**
     * 加载图片并缩放到指定尺寸
     * @param path 图片路径
     * @param width 目标宽度
     * @param height 目标高度
     * @return 缩放后的图片
     */
    public BufferedImage loadImage(String path, int width, int height) {
        // 检查缩放缓存
        String scaledKey = path + "_" + width + "x" + height;
        if (scaledCache.containsKey(scaledKey)) {
            return scaledCache.get(scaledKey);
        }

        // 尝试加载GIF
        if (path.endsWith(".gif")) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    ImageIcon icon = new ImageIcon(bytes);
                    Image image = icon.getImage();

                    // 等待图片加载完成
                    MediaTracker tracker = new MediaTracker(new Component() {});
                    tracker.addImage(image, 0);
                    tracker.waitForAll(2000); // 增加等待时间

                    // 创建BufferedImage并绘制
                    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, width, height, null);
                    g2d.dispose();

                    scaledCache.put(scaledKey, bufferedImage);
                    return bufferedImage;
                }
            } catch (Exception e) {
                System.err.println("加载GIF图片失败: " + path);
                e.printStackTrace();
            }
        }

        // 加载普通图片
        BufferedImage original = loadImage(path);
        if (original == null) {
            return loadingPlaceholder;
        }

        BufferedImage scaled = scaleImage(original, width, height);
        if (scaled != null) {
            scaledCache.put(scaledKey, scaled);
        }
        return scaled;
    }

    /**
     * 缩放图片
     * @param original 原图
     * @param width 目标宽度
     * @param height 目标高度
     * @return 缩放后的图片
     */
    public BufferedImage scaleImage(BufferedImage original, int width, int height) {
        if (original == null) {
            return loadingPlaceholder;
        }
        if (original.getWidth() == width && original.getHeight() == height) {
            return original;
        }
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return result;
    }

    /**
     * 异步加载图片
     * @param path 图片路径
     * @param callback 加载完成回调
     */
    public void loadImageAsync(String path, ImageLoadCallback callback) {
        executorService.submit(() -> {
            BufferedImage image = loadImage(path);
            SwingUtilities.invokeLater(() -> callback.onImageLoaded(image));
        });
    }

    /**
     * 图片加载回调接口
     */
    public interface ImageLoadCallback {
        void onImageLoaded(BufferedImage image);
    }

    // ==================== 获取图片（自动加载） ====================

    // 获取僵尸图片
    public BufferedImage getZombieImage(String zombieType) {
        String path = Constants.IMAGE_PATH + "zombies/" + zombieType + "/" + zombieType + ".png";
        return loadImage(path, 80, 100); // 调整尺寸
    }

    /**
     * 获取僵尸动画图片
     * @param zombieType 僵尸类型
     * @param animationType 动画类型（walking/eating/dying）
     * @return 动画图片
     */
    public Image getZombieAnimationImage(String zombieType, String animationType) {
        String path = "images/zombies/";

        switch (animationType) {
            case "walking":
                if (zombieType.equals("普通僵尸")) {
                    path += "普通僵尸/普通僵尸走路.gif";
                } else if (zombieType.equals("路障僵尸")) {
                    path += "路障僵尸/路障僵尸走路.gif";
                } else if (zombieType.equals("铁桶僵尸")) {
                    path += "铁桶僵尸/铁桶僵尸.gif";
                }
                break;
            case "eating":
                path += zombieType + "/" + zombieType + "啃食.gif";
                break;
            case "dying":
                path += "僵尸死.gif";
                break;
        }

        // 使用URL加载图片，保留动画效果
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }

        // 加载失败时返回null
        System.err.println("无法加载僵尸动画: " + path);
        return null;
    }

    // 获取子弹图片
    public BufferedImage getBulletImage(String bulletType) {
        String path = Constants.IMAGE_PATH + "bullets/" + bulletType + ".png";
        return loadImage(path, 20, 20); // 调整尺寸
    }

    /**
     * 获取植物图片
     * @param name 植物名称
     * @param isAnimated 是否加载动画图片
     * @return 植物图片
     */
    public BufferedImage getPlantImage(String name, boolean isAnimated) {
        String path;
        if (isAnimated) {
            // 检查名称是否已经包含"1"后缀
            if (name.endsWith("1")) {
                // 如果已经包含"1"，直接使用
                path = "images/plants/" + name + ".gif";
            } else {
                // 否则添加"1"
                path = "images/plants/" + name + "1.gif";
            }
        } else {
            // 静态图片：植物名字.png
            path = "images/plants/" + name + ".png";
        }

        // 尝试加载指定格式
        BufferedImage image = loadImage(path, 70, 70);

        // 检查是否加载成功
        if (image != loadingPlaceholder) {
            return image;
        }

        // 如果指定格式加载失败，尝试另一种格式
        if (isAnimated) {
            path = "images/plants/" + name + ".png";
        } else {
            if (name.endsWith("1")) {
                path = "images/plants/" + name + ".gif";
            } else {
                path = "images/plants/" + name + "1.gif";
            }
        }

        image = loadImage(path, 70, 70);

        if (image != loadingPlaceholder) {
            return image;
        }

        System.err.println("无法加载植物图片: " + name);
        return loadingPlaceholder;
    }

    /**
     * 获取植物图片（默认尝试动画图片）
     * @param name 植物名称
     * @return 植物图片
     */
    public BufferedImage getPlantImage(String name) {
        return getPlantImage(name, true);
    }

    /**
     * 获取植物图片作为Image对象（保留动画效果）
     * @param name 植物名称
     * @param isAnimated 是否加载动画图片
     * @return Image对象
     */
    public Image getPlantImageAsImage(String name, boolean isAnimated) {
        String path;
        if (isAnimated) {
            // 检查名称是否已经包含"1"后缀
            if (name.endsWith("1")) {
                // 如果已经包含"1"，直接使用
                path = "images/plants/" + name + ".gif";
            } else {
                // 否则添加"1"
                path = "images/plants/" + name + "1.gif";
            }
        } else {
            // 静态图片：植物名字.png
            path = "images/plants/" + name + ".png";
        }

        // 使用URL加载图片，保留动画效果
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }

        // 如果指定格式加载失败，尝试另一种格式
        if (isAnimated) {
            path = "images/plants/" + name + ".png";
        } else {
            if (name.endsWith("1")) {
                path = "images/plants/" + name + ".gif";
            } else {
                path = "images/plants/" + name + "1.gif";
            }
        }

        url = getClass().getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }

        System.err.println("无法加载植物图片: " + name);
        return null;
    }

    // 获取阳光图片
    public BufferedImage getSunImage() {
        String path = Constants.IMAGE_PATH + "tools/太阳.png";
        return loadImage(path, 40, 40);
    }

    // 获取小推车图片
    public BufferedImage getCartImage() {
        String path = Constants.IMAGE_PATH + "tools/小推车.png";
        return loadImage(path, 100, 100); // 调整尺寸
    }

    // 获取铲子图片
    public BufferedImage getShovelImage() {
        String path = Constants.IMAGE_PATH + "tools/铲子.png";
        return loadImage(path, 200, 200);
    }

    // 获取植物卡片图片
    public BufferedImage getPlantCardImage(String plantId) {
        String path = Constants.IMAGE_PATH + "cards/" + plantId + ".png";
        return loadImage(path, 60, 80); // 调整尺寸
    }

    //获取卡片栏图片
    public BufferedImage getCardImage(String card) {
        String path = Constants.IMAGE_PATH + "cards/" + card + ".png";
        return loadImage(path, 600, 50); // 调整尺寸
    }

    //获取ui图片
    public BufferedImage getUIImage(String uiName) {
        String path = Constants.IMAGE_PATH + "ui/" + uiName + ".png";
        return loadImage(path, 1000, 1000);
    }

    /**
     * 获取鸽子图标
     * @return 鸽子图标
     */
    public BufferedImage getPigeonIcon() {
        String path = Constants.IMAGE_PATH + "ui/pigeon.png";
        return loadImage(path, 30, 30);
    }

    /**
     * 获取拼图块图片
     * @param pieceIndex 拼图块索引（0-3）
     * @return 拼图块图片
     */
    public BufferedImage getPuzzlePieceImage(int pieceIndex) {
        String path = Constants.IMAGE_PATH + "puzzle/piece_" + pieceIndex + ".png";
        return loadImage(path, Constants.PUZZLE_PIECE_WIDTH, Constants.PUZZLE_PIECE_HEIGHT);
    }

    /**
     * 获取完整拼图背景（淡色）
     * @return 完整拼图背景
     */
    public BufferedImage getPuzzleBackgroundImage() {
        String path = Constants.IMAGE_PATH + "puzzle/background.png";
        return loadImage(path, Constants.PUZZLE_PIECE_WIDTH * Constants.PUZZLE_COLS,
                Constants.PUZZLE_PIECE_HEIGHT * Constants.PUZZLE_ROWS);
    }

    /**
     * 获取物品栏物品放大图
     * @param itemId 物品ID
     * @param type 类型（plant/zombie/tool）
     * @return 放大图
     */
    public BufferedImage getLargeItemImage(String itemId, String type) {
        String path = Constants.IMAGE_PATH + type + "/large/" + itemId + ".png";
        return loadImage(path, 200, 200);
    }

    /**
     * 获取背景图片
     * @param backgroundName 背景名称
     * @return 背景图片
     */
    public BufferedImage getBackgroundImage(String backgroundName) {
        String path = Constants.IMAGE_PATH + "backgrounds/" + backgroundName + ".png";
        return loadImage(path, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    // ==================== 图片工具方法 ====================

    /**
     * 获取图片的多个帧（用于动画）
     * @param basePath 基础路径（如 "plants/PeaShooter_"）
     * @param frameCount 帧数
     * @param width 每帧宽度
     * @param height 每帧高度
     * @return 帧数组
     */
    public BufferedImage[] getAnimationFrames(String basePath, int frameCount, int width, int height) {
        String cacheKey = basePath + "_" + frameCount + "_" + width + "x" + height;
        if (animationCache.containsKey(cacheKey)) {
            return animationCache.get(cacheKey);
        }

        BufferedImage[] frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            String path = Constants.IMAGE_PATH + basePath + i + ".png";
            frames[i] = loadImage(path, width, height);
        }

        animationCache.put(cacheKey, frames);
        return frames;
    }

    /**
     * 预加载一组图片
     * @param paths 图片路径列表
     */
    public void preloadImages(String[] paths) {
        for (String path : paths) {
            loadImage(path);
        }
    }

    /**
     * 清除图片缓存（用于释放内存）
     */
    public void clearCache() {
        strongCache.clear();
        softCache.clear();
        scaledCache.clear();
        animationCache.clear();
        System.out.println("缓存已清除");
    }

    /**
     * 清理缓存，将不常用的图片移到软引用缓存
     */
    private void cleanupCache() {
        List<String> keys = new ArrayList<>(strongCache.keySet());
        // 保留前50个常用图片，其余移到软引用缓存
        for (int i = 50; i < keys.size(); i++) {
            String key = keys.get(i);
            BufferedImage image = strongCache.remove(key);
            if (image != null) {
                softCache.put(key, new SoftReference<>(image));
            }
        }
    }

    /**
     * 检查图片是否已缓存
     */
    public boolean isCached(String path) {
        return strongCache.containsKey(path) || softCache.containsKey(path);
    }

    /**
     * 获取缓存大小
     */
    public int getCacheSize() {
        return strongCache.size();
    }

    /**
     * 获取总缓存大小（包括软引用缓存）
     */
    public int getTotalCacheSize() {
        return strongCache.size() + softCache.size() + scaledCache.size() + animationCache.size();
    }

    /**
     * 检查预加载是否完成
     */
    public boolean isPreloadComplete() {
        return preloadComplete;
    }

    /**
     * 关闭资源管理器
     */
    public void shutdown() {
        executorService.shutdown();
        clearCache();
    }
}