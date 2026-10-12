package com.ok.ui.screens;

import com.ok.game.core.GameLoop;
import com.ok.game.core.GameManager;
import com.ok.game.entities.*;
import com.ok.game.grid.GridManager;
import com.ok.game.plants.PeaShooter;
import com.ok.game.plants.Sunflower;
import com.ok.game.systems.CartSystem;
import com.ok.game.systems.SunSystem;
import com.ok.resource.ResourceManager;
import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.ui.components.PlantCard;
import com.ok.ui.components.SunCounter;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * 游戏战斗界面
 * 显示背景、植物栏、小推车、阳光和植物
 * 优化版本：添加双缓冲、减少重绘、优化绘制性能
 */
public class GameScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 植物卡片列表 */
    private List<PlantCard> plantCards;

    /** 背景图片 */
    private BufferedImage backgroundImage;

    //卡片栏图片
    private BufferedImage cardImage;

    //植物卡片图片
    private BufferedImage PeaShooterImage;
    private BufferedImage SunFlowerImage;

    //植物图片
    private Image peaShooterPlantImage;
    private Image sunflowerPlantImage;

    /** 铲子图片 */
    private BufferedImage shovelImage;

    //菜单图片
    private BufferedImage menuImage;

    /** 是否显示菜单界面 */
    private boolean showMenuScreen = false;

    /** 小推车图片 */
    private BufferedImage cartImage;

    /** 菜单按钮区域 */
    private Rectangle menuButtonRect;

    /** 铲子按钮区域 */
    private Rectangle shovelButtonRect;

    /** 卡片栏区域 */
    private Rectangle cardBarRect;

    /** 游戏模式 */
    private String gameMode;

    /** 关卡索引 */
    private int levelIndex;

    /** 鼠标位置 */
    private Point mousePos;

    /** 上一次鼠标位置 */
    private Point lastMousePos;

    //网格管理器
    private GridManager gridManager;

    /** 铲子模式 */
    private boolean shovelMode;

    /** 选中的植物 */
    private String selectedPlant;

    /** 阳光计数器 */
    private SunCounter sunCounter;

    /** 阳光系统 */
    private SunSystem sunSystem;

    /** 进入战斗界面的时间（毫秒） */
    private long startTime;

    /** 提示图片 */
    private BufferedImage readyImage;
    private BufferedImage setImage;
    private BufferedImage goImage;

    /** 是否显示失败界面 */
    private boolean showDefeatScreen = false;

    /** 失败图片 */
    private BufferedImage defeatImage;

    /** 是否显示胜利界面 */
    private boolean showVictoryScreen = false;

    /** 胜利图片 */
    private BufferedImage victoryImage;

    /** 植物卡片冷却时间（毫秒） */
    private static final int PLANT_COOLDOWN = 5000; // 5秒

    /** 植物冷却计时器（植物ID -> 剩余冷却时间） */
    private Map<String, Long> plantCooldowns;

    /** 植物卡片图片缓存 */
    private Map<String, BufferedImage> plantCardImages;

    /** 双缓冲缓冲区 */
    private BufferedImage buffer;

    /** 上次重绘时间 */
    private long lastRepaintTime;

    /** 重绘间隔（毫秒） */
    private static final long REPAINT_INTERVAL = 16; // 约60fps

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public GameScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.gameMode = "campaign";
        this.levelIndex = 0;
        this.mousePos = new Point(0, 0);
        this.lastMousePos = new Point(0, 0);

        plantCooldowns = new HashMap<>();
        plantCardImages = new HashMap<>();

        setLayout(null);
        setBackground(new Color(70, 110, 70));
        setDoubleBuffered(false); // 禁用默认双缓冲，使用自定义双缓冲

        loadImages();
        initUI();
        loadCardImages();
        initSunSystem();
        initListeners();
    }

    /**
     * 加载图片
     */
    private void loadImages() {
        // 加载背景图片
        backgroundImage = ResourceManager.getInstance().getBackgroundImage("白天草坪");

        //加载卡片栏图片
        cardImage = ResourceManager.getInstance().getCardImage("卡片槽1");

        //加载植物卡片
        PeaShooterImage = ResourceManager.getInstance().getCardImage("豌豆射手");
        SunFlowerImage = ResourceManager.getInstance().getCardImage("向日葵");

        //加载植物静态图片（PNG）用于鼠标跟随
        peaShooterPlantImage = ResourceManager.getInstance().getPlantImage("豌豆射手", false);
        sunflowerPlantImage = ResourceManager.getInstance().getPlantImage("向日葵", false);

        //加载菜单图片
        menuImage = ResourceManager.getInstance().getUIImage("菜单");

        // 加载小推车图片
        cartImage = ResourceManager.getInstance().getCartImage();

        // 加载铲子图片
        shovelImage = ResourceManager.getInstance().getShovelImage();

        // 加载提示图片
        readyImage = ResourceManager.getInstance().getUIImage("好");
        setImage = ResourceManager.getInstance().getUIImage("准备");
        goImage = ResourceManager.getInstance().getUIImage("开始");
    }

    /**
     * 加载植物卡片图片
     */
    private void loadCardImages() {
        // 遍历所有植物卡片
        for (PlantCard card : plantCards) {
            String plantId = card.getPlantId();
            BufferedImage cardImage = ResourceManager.getInstance().getCardImage(plantId);
            if (cardImage != null) {
                plantCardImages.put(plantId, cardImage);
            }
        }
    }

    /**
     * 初始化阳光系统
     */
    private void initSunSystem() {
        sunSystem = SunSystem.getInstance();
        // 初始化阳光系统，传入GameManager实例
        sunSystem.init(GameManager.getInstance());
        sunSystem.setSunCounterPosition(95, 60); // 阳光计数器位置
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        // 初始化卡片栏区域（左上角，与界面边缘重合）
        int cardBarWidth = 300; // 放大后的宽度
        int cardBarHeight = 120; // 放大后的高度
        cardBarRect = new Rectangle(0, 0, cardBarWidth, cardBarHeight);

        // 初始化铲子按钮区域（卡片栏右侧，高度与卡片栏相同）
        shovelButtonRect = new Rectangle(cardBarWidth+423, 17, 35, cardBarHeight-65);

        // 初始化菜单按钮区域（右上角，与界面边缘重合）
        int menuWidth = 100; // 放大后的宽度
        int menuHeight = 50; // 放大后的高度
        menuButtonRect = new Rectangle(Constants.WINDOW_WIDTH - menuWidth-10, 0, menuWidth, menuHeight);

        // 初始化植物卡片
        plantCards = new ArrayList<>();
        int cardX = 210; // 卡片X位置
        int cardY = 10; // 卡片Y位置
        int cardWidth = 65; // 卡片宽度
        int cardHeight = 85; // 卡片高度
        int cardSpacing = 0; // 卡片间距

        // 创建并添加豌豆射手卡片
        PlantCard peaShooterCard = new PlantCard("豌豆射手", cardX, cardY, cardWidth, cardHeight);
        plantCards.add(peaShooterCard);

        // 创建并添加向日葵卡片
        PlantCard sunflowerCard = new PlantCard("向日葵", cardX + (cardWidth + cardSpacing), cardY, cardWidth, cardHeight);
        plantCards.add(sunflowerCard);

        // 初始化阳光计数器
        sunCounter = new SunCounter(95, 60); // 左上角位置
        add(sunCounter);
    }

    /**
     * 创建植物对象
     * @param plantId 植物ID
     * @param row 网格行
     * @param col 网格列
     * @return 植物对象
     */
    private Plant createPlant(String plantId, int row, int col) {
        if (gridManager == null) {
            gridManager = GameManager.getInstance().getGridManager();
        }
        int screenX = gridManager.gridToScreenX(col);
        int screenY = gridManager.gridToScreenY(row);

        // 获取 GameManager 实例
        GameManager gameManager = GameManager.getInstance();

        // 检查阳光数量
        int cost = 0;
        if (plantId.equals("豌豆射手")) {
            cost = 100; // 豌豆射手成本
        } else if (plantId.equals("向日葵")) {
            cost = 50; // 向日葵成本
        }

        // 使用 GameManager 检查并消耗阳光
        if (!gameManager.spendSun(cost)) {
            // 阳光不足，无法种植
            return null;
        }

        // 创建植物
        if (plantId.equals("豌豆射手")) {
            // 创建豌豆射手
            PeaShooter peaShooter = new PeaShooter(row, col, screenX, screenY);
            peaShooter.setGameManager(gameManager);
            return peaShooter;
        } else if (plantId.equals("向日葵")) {
            // 创建向日葵
            Sunflower sunflower = new Sunflower(row, col, screenX, screenY);
            sunflower.setGameManager(gameManager);
            return sunflower;
        }

        return null;
    }

    /**
     * 获取植物的阳光消耗
     * @param plantId 植物ID
     * @return 阳光消耗值
     */
    private int getPlantSunCost(String plantId) {
        if (plantId.equals("豌豆射手")) {
            return 100; // 豌豆射手成本
        } else if (plantId.equals("向日葵")) {
            return 50; // 向日葵成本
        }
        return 0;
    }

    /**
     * 开始植物冷却
     */
    private void startPlantCooldown(String plantId) {
        plantCooldowns.put(plantId, System.currentTimeMillis() + PLANT_COOLDOWN);
    }

    /**
     * 检查植物是否在冷却中
     */
    private boolean isPlantOnCooldown(String plantId) {
        Long cooldownEnd = plantCooldowns.get(plantId);
        if (cooldownEnd == null) {
            return false;
        }
        return System.currentTimeMillis() < cooldownEnd;
    }

    /**
     * 获取植物冷却进度（0-1，0表示刚开始冷却，1表示冷却结束）
     */
    private float getPlantCooldownProgress(String plantId) {
        Long cooldownEnd = plantCooldowns.get(plantId);
        if (cooldownEnd == null) {
            return 1.0f; // 没有冷却，进度为1
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = cooldownEnd - currentTime;
        if (elapsedTime <= 0) {
            return 1.0f; // 冷却结束
        }

        float progress = (float) elapsedTime / PLANT_COOLDOWN;
        return Math.max(0.0f, Math.min(1.0f, progress));
    }

    /**
     * 初始化鼠标监听器
     */
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseRelease();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });
    }

    /**
     * 初始化游戏
     * @param mode 游戏模式
     * @param levelIndex 关卡索引
     */
    public void initGame(String mode, int levelIndex) {
        this.gameMode = mode;
        this.levelIndex = levelIndex;

        // 初始化 GameManager
        GameManager gameManager = GameManager.getInstance();
        gameManager.initGame(mode, levelIndex, this);

        // 获取 GridManager 实例
        this.gridManager = gameManager.getGridManager();

        // 初始化开始时间
        startTime = System.currentTimeMillis();
        lastRepaintTime = System.currentTimeMillis();

        // 从 GameManager 获取初始阳光并更新显示
        sunCounter.setSunAmount(gameManager.getCurrentSun());

        showMenuScreen = false;

        repaint();
    }

    /**
     * 显示失败界面
     */
    public void showDefeatScreen() {
        showDefeatScreen = true;
        // 加载失败图片
        defeatImage = ResourceManager.getInstance().getUIImage("僵吃脑");
        repaint();
    }

    /**
     * 显示胜利界面
     */
    public void showVictoryScreen() {
        showVictoryScreen = true;
        // 加载胜利图片
        victoryImage = ResourceManager.getInstance().getUIImage("脑吃僵");
        repaint();
    }

    public void init(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * 处理鼠标点击
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleMouseClick(int x, int y) {
        // 检查失败界面的点击
        if (showDefeatScreen) {
            showDefeatScreen = false;  // 重置标志
            if ("campaign".equals(gameMode)) {
                gameFrame.showLevelSelectScreen();  // 闯关模式返回选关界面
            } else {
                gameFrame.showMainScreen();  // 无限模式返回主菜单
            }
            return;
        }

        // 检查胜利界面的点击
        if (showVictoryScreen) {
            showVictoryScreen = false;  // 重置标志
            if ("campaign".equals(gameMode)) {
                gameFrame.showLevelSelectScreen();  // 闯关模式返回选关界面
            } else {
                gameFrame.showMainScreen();  // 无限模式返回主菜单
            }
            return;
        }

        // 检查阳光
        if (sunSystem.tryCollectSun(x, y)) {
            // 阳光收集后更新显示
            GameManager gameManager = GameManager.getInstance();
            sunCounter.setSunAmount(gameManager.getCurrentSun());
            repaint();
            return;
        }

        // 检查菜单按钮
        if (menuButtonRect.contains(x, y)) {
            showMenuDialog();
            return;
        }

        // 检查铲子按钮
        if (shovelButtonRect.contains(x, y)) {
            // 铲子模式切换
            shovelMode = !shovelMode;
            selectedPlant = null;
            repaint();
            return;
        }

        // 检查植物卡片
        for (PlantCard card : plantCards) {
            if (card.getBounds().contains(x, y)) {
                String plantId = card.getPlantId();

                // 检查是否在冷却中
                if (isPlantOnCooldown(plantId)) {
                    // 冷却中，不允许选择
                    return;
                }

                // 检查阳光是否足够
                if (getSunAmount() < getPlantSunCost(plantId)) {
                    // 阳光不足，不允许选择
                    return;
                }

                // 如果点击的是已选中的植物卡片，则取消选择
                if (selectedPlant != null && selectedPlant.equals(plantId)) {
                    selectedPlant = null;
                } else {
                    // 选择新植物
                    selectedPlant = plantId;
                    shovelMode = false;
                }
                repaint();
                return;
            }
        }

        // 检查网格
        if (gridManager == null) {
            gridManager = GameManager.getInstance().getGridManager();
        }
        Point gridPos = gridManager.screenToGrid(x, y);
        if (gridPos != null) {
            if (shovelMode) {
                // 铲子模式：铲除植物
                Plant removedPlant = gridManager.removePlant(gridPos.x, gridPos.y);
                if (removedPlant != null) {
                    // 铲除成功，通过 GameManager 返还阳光
                    GameManager gameManager = GameManager.getInstance();
                    int refund = removedPlant.getSunCost() * Constants.SHOVEL_REFUND_RATIO / 100;
                    gameManager.addSunAmount(refund);
                }
            } else if (selectedPlant != null) {
                // 种植模式：种植植物

                // 先检查该格子是否已有植物
                if (gridManager.hasPlant(gridPos.x, gridPos.y)) {
                    // 该格子已有植物，不做任何反应
                    return;
                }

                // 检查植物是否在冷却中
                if (isPlantOnCooldown(selectedPlant)) {
                    // 冷却中，不允许种植
                    return;
                }

                // 创建并种植植物
                Plant plant = createPlant(selectedPlant, gridPos.x, gridPos.y);
                if (plant != null) {
                    boolean success = gridManager.plant(plant, gridPos.x, gridPos.y);
                    if (success) {
                        // 种植成功，开始冷却
                        startPlantCooldown(selectedPlant);
                        // 清除选择状态（跟随鼠标的PNG消失）
                        selectedPlant = null;
                        // 强制重绘
                        repaint();
                    }
                }
            }
            repaint();
            return;
        }
    }

    /**
     * 处理鼠标按下
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleMousePress(int x, int y) {
        // 预留
    }

    /**
     * 处理鼠标释放
     */
    private void handleMouseRelease() {
        // 预留
    }

    /**
     * 处理鼠标移动
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleMouseMove(int x, int y) {
        // 更新鼠标位置
        mousePos.setLocation(x, y);

        // 只有当鼠标位置发生变化且达到重绘间隔时才重绘
        if (!mousePos.equals(lastMousePos)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRepaintTime > REPAINT_INTERVAL) {
                repaint();
                lastRepaintTime = currentTime;
                lastMousePos.setLocation(mousePos);
            }
        }
    }

    /**
     * 显示菜单弹窗
     */
    private void showMenuDialog() {
        GameLoop.getInstance().pause();  // 直接用单例
        MenuDialog dialog = new MenuDialog(gameFrame, this);
        dialog.setVisible(true);
        GameLoop.getInstance().resume();
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 初始化缓冲区
        if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        // 获取缓冲区的Graphics
        Graphics2D bufferG = buffer.createGraphics();
        bufferG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bufferG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 绘制到缓冲区
        drawEverything(bufferG);

        // 一次性绘制到屏幕
        g.drawImage(buffer, 0, 0, null);

        bufferG.dispose();
    }

    /**
     * 绘制所有内容到缓冲区
     */
    private void drawEverything(Graphics2D g) {
        // 绘制背景
        drawBackground(g);

        // 绘制网格
        drawGrid(g);

        // 绘制小推车
        drawCarts(g);

        // 绘制植物
        drawPlants(g);

        //绘制子弹
        drawBullets(g);

        // 绘制僵尸
        drawZombies(g);

        // 绘制阳光
        drawSuns(g);

        // 绘制UI
        drawUI(g);

        // 绘制提示图片
        drawStartHint(g);

        // 绘制跟随鼠标的植物或铲子
        drawFollowMouse(g);

        // 绘制失败界面
        if (showDefeatScreen) {
            drawDefeatScreen(g);
        }

        // 绘制胜利界面
        if (showVictoryScreen) {
            drawVictoryScreen(g);
        }
    }

    /**
     * 绘制开始提示
     */
    private void drawStartHint(Graphics2D g) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double n=0.1;
        int m=450,p=270;

        // 根据时间显示不同的提示图片
        if (elapsedTime < 1000) {
            // 第一秒：显示"好"
            if (readyImage != null) {
                int width = (int) (readyImage.getWidth() * n);
                int height = (int) (readyImage.getHeight() * n);
                g.drawImage(readyImage, m, p, width, height, null);
            }
        } else if (elapsedTime < 2500) {
            // 第二秒：显示"准备"
            if (setImage != null) {
                int width = (int) (setImage.getWidth() * n);
                int height = (int) (setImage.getHeight() * n);
                g.drawImage(setImage, m, p, width, height, null);
            }
        } else if (elapsedTime < 3500) {
            // 第三秒：显示"开始"
            if (goImage != null) {
                int width = (int) (goImage.getWidth() * n);
                int height = (int) (goImage.getHeight() * n);
                g.drawImage(goImage, m, p, width, height, null);
            }
        }
        // 第四秒及以后：不显示提示图片，开始战斗
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics2D g) {
        // 绘制背景图片
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * 绘制小推车
     */
    private void drawCarts(Graphics2D g) {
        int cartY = 150;
        int cartWidth = 80;
        int cartHeight = 80;
        int cartSpacing = 100;

        CartSystem cartSystem = GameManager.getInstance().getCartSystem();

        if (cartImage != null) {
            for (int i = 0; i < 5; i++) {
                Cart cart = cartSystem.getCart(i);
                if (cart != null) {
                    if (cart.isActive()) {
                        // 绘制静止的小车
                        g.drawImage(cartImage, 10 + 25, cartY - 35 + i * cartSpacing, cartWidth, cartHeight, null);
                    } else if (cart.isMoving()) {
                        // 绘制移动中的小车
                        g.drawImage(cartImage, cart.getX(), cartY - 35 + i * cartSpacing, cartWidth, cartHeight, null);
                    }
                    // 已触发并停止的小车不绘制
                }
            }
        }
    }

    /**
     * 绘制UI
     */
    private void drawUI(Graphics2D g) {
        // 绘制卡片栏
        if (cardImage != null) {
            g.drawImage(cardImage, cardBarRect.x+115, cardBarRect.y, cardBarRect.width+370, cardBarRect.height-20, null);
        }

        // 绘制植物卡片
        int cardX = 210; // 与卡片栏对齐
        int cardY = 10; // 与卡片栏对齐
        int cardWidth = 65; // 放大后的卡片宽度
        int cardHeight = 85; // 放大后的卡片高度
        int cardSpacing = 0; // 放大后的间距

        // 遍历所有植物卡片
        for (int i = 0; i < plantCards.size(); i++) {
            PlantCard card = plantCards.get(i);
            String plantId = card.getPlantId();
            int currentCardX = cardX + i * (cardWidth + cardSpacing);

            // 绘制卡片
            if (plantCardImages.containsKey(plantId)) {
                BufferedImage cardImage = plantCardImages.get(plantId);
                if (cardImage != null) {
                    // 绘制原始卡片
                    g.drawImage(cardImage, currentCardX, cardY, cardWidth, cardHeight, null);

                    // 检查是否在冷却中
                    if (isPlantOnCooldown(plantId)) {
                        // 计算冷却进度
                        float progress = getPlantCooldownProgress(plantId);

                        // 绘制底层半透明（25%）
                        g.setColor(new Color(0, 0, 0, 64)); // 25% 透明度
                        g.fillRect(currentCardX, cardY, cardWidth, cardHeight);

                        // 绘制上层半透明（50%），高度随进度变化
                        int overlayHeight = (int) (cardHeight * progress);
                        g.setColor(new Color(0, 0, 0, 128)); // 50% 透明度
                        g.fillRect(currentCardX, cardY, cardWidth, overlayHeight);
                    }
                    // 检查阳光是否足够
                    else if (getSunAmount() < getPlantSunCost(plantId)) {
                        // 阳光不足，绘制灰色覆盖层（与冷却时的底层效果相同）
                        g.setColor(new Color(0, 0, 0, 64)); // 25% 透明度
                        g.fillRect(currentCardX, cardY, cardWidth, cardHeight);
                    }
                }
            }
        }

        // 绘制铲子按钮
        if (shovelImage != null) {
            g.drawImage(shovelImage, shovelButtonRect.x, shovelButtonRect.y, shovelButtonRect.width, shovelButtonRect.height, null);
        }

        // 绘制菜单按钮
        if (menuImage != null) {
            g.drawImage(menuImage, menuButtonRect.x, menuButtonRect.y, menuButtonRect.width, menuButtonRect.height, null);
        }
    }

    /**
     * 绘制网格
     */
    private void drawGrid(Graphics2D g) {
        if (gridManager == null) {
            gridManager = GameManager.getInstance().getGridManager();
        }
        int rows = gridManager.getRows();
        int cols = gridManager.getCols();
        int cellWidth = gridManager.getCellWidth();
        int cellHeight = gridManager.getCellHeight();
        int offsetX = gridManager.getOffsetX();
        int offsetY = gridManager.getOffsetY();

        // 绘制网格线
        g.setColor(new Color(0, 0, 0, 50));
        for (int i = 0; i <= rows; i++) {
            int y = offsetY + i * cellHeight;
            g.drawLine(offsetX, y, offsetX + cols * cellWidth, y);
        }
        for (int j = 0; j <= cols; j++) {
            int x = offsetX + j * cellWidth;
            g.drawLine(x, offsetY, x, offsetY + rows * cellHeight);
        }
    }

    /**
     * 绘制阳光
     */
    private void drawSuns(Graphics2D g) {
        for (Sun sun : sunSystem.getSuns()) {
            sun.render(g);
        }
    }

    /**
     * 绘制跟随鼠标的植物或铲子
     */
    private void drawFollowMouse(Graphics2D g) {
        int x = mousePos.x - 30; // 鼠标中心
        int y = mousePos.y - 30; // 鼠标中心

        if (shovelMode) {
            // 绘制铲子
            if (shovelImage != null) {
                g.drawImage(shovelImage, x, y, 60, 60, null);
            }
        } else if (selectedPlant != null) {
            // 绘制选中的植物（使用 PNG 图片）
            if (selectedPlant.equals("豌豆射手") && peaShooterPlantImage != null) {
                g.drawImage(peaShooterPlantImage, x, y, 60, 60, null);
            } else if (selectedPlant.equals("向日葵") && sunflowerPlantImage != null) {
                g.drawImage(sunflowerPlantImage, x, y, 60, 60, null);
            }
        }
    }

    /**
     * 绘制植物
     */
    private void drawPlants(Graphics2D g) {
        if (gridManager == null) {
            gridManager = GameManager.getInstance().getGridManager();
        }
        java.util.List<Plant> plants = gridManager.getAllPlants();

        for (Plant plant : plants) {
            // 绘制植物（使用植物的种植后图片）
            int x = plant.getX();
            int y = plant.getY();
            int width = plant.getWidth();
            int height = plant.getHeight();

            Image plantedImage = plant.getPlantedImage();
            if (plantedImage != null) {
                g.drawImage(plantedImage, x, y, width, height, null);
            }
        }
    }

    /**
     * 绘制僵尸
     */
    private void drawZombies(Graphics2D g) {
        GameManager gameManager = GameManager.getInstance();
        for (Zombie zombie : gameManager.getZombies()) {
            zombie.render(g);
        }
    }

    /**
     * 绘制子弹
     */
    private void drawBullets(Graphics2D g) {
        GameManager gameManager = GameManager.getInstance();
        for (Bullet bullet : gameManager.getBullets()) {
            bullet.render(g);
        }
    }

    /**
     * 绘制失败界面
     */
    private void drawDefeatScreen(Graphics2D g) {
        // 背景遮罩
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        // 显示失败图片（缩小到 50%）
        if (defeatImage != null) {
            // 缩放比例：0.5 = 缩小到 50%，0.3 = 30%，0.7 = 70%
            double scale = 0.5;

            int originalWidth = defeatImage.getWidth();
            int originalHeight = defeatImage.getHeight();
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // 居中显示
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;

            g.drawImage(defeatImage, x, y, scaledWidth, scaledHeight, null);
        }
    }

    /**
     * 绘制胜利界面
     */
    private void drawVictoryScreen(Graphics2D g) {
        // 背景遮罩
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        // 显示胜利图片（缩小到 50%）
        if (victoryImage != null) {
            // 缩放比例：0.5 = 缩小到 50%，0.3 = 30%，0.7 = 70%
            double scale = 0.5;

            int originalWidth = victoryImage.getWidth();
            int originalHeight = victoryImage.getHeight();
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // 居中显示
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;

            g.drawImage(victoryImage, x, y, scaledWidth, scaledHeight, null);
        }
    }

    /**
     * 更新游戏状态
     */
    public void update() {
        // 获取GameManager实例
        GameManager gameManager = GameManager.getInstance();

        // 更新植物冷却
        updatePlantCooldowns();

        // 更新阳光计数器显示
        sunCounter.setSunAmount(gameManager.getCurrentSun());

        // 控制重绘频率
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRepaintTime > REPAINT_INTERVAL) {
            repaint();
            lastRepaintTime = currentTime;
        }
    }

    /**
     * 更新植物冷却状态
     */
    private void updatePlantCooldowns() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = plantCooldowns.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime >= entry.getValue()) {
                // 冷却结束，移除
                iterator.remove();
            }
        }
    }

    /**
     * 设置阳光数量
     * @param amount 阳光数量
     */
    public void setSunAmount(int amount) {
        sunCounter.setSunAmount(amount);
    }

    /**
     * 获取阳光数量
     * @return 阳光数量
     */
    public int getSunAmount() {
        return GameManager.getInstance().getCurrentSun();
    }

    /**
     * 获取游戏模式
     * @return 游戏模式
     */
    public String getGameMode() {
        return gameMode;
    }
}