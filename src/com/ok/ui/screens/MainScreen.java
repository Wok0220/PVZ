package com.ok.ui.screens;

import com.ok.resource.ResourceManager;
import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.ui.components.AccountAvatar;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * 主界面
 * 显示主菜单（冒险模式、无限模式、物品栏、拼图系统）
 * 左下角显示账号头像
 */
public class MainScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 账号头像组件 */
    private AccountAvatar accountAvatar;

    // ==================== 菜单按钮区域 ====================

    /** 冒险模式按钮区域 */
    private Rectangle adventureRect;

    /** 无限模式按钮区域 */
    private Rectangle endlessRect;

    /** 图鉴按钮区域 */
    private Rectangle inventoryRect;

    /** 拼图游戏按钮区域 */
    private Rectangle puzzleRect;

    /** 当前悬停的按钮 */
    private String hoverButton;

    // ==================== 音效/音乐开关 ====================

    /** 音效开关按钮区域 */
    private Rectangle soundRect;

    /** 音乐开关按钮区域 */
    private Rectangle musicRect;

    /** 音效是否开启 */
    private boolean soundEnabled;

    /** 音乐是否开启 */
    private boolean musicEnabled;

    // ==================== 背景和图片 ====================

    /** 背景图片 */
    private BufferedImage backgroundImage;

    /** 房屋图片 */
    private BufferedImage houseImage;

    /** 墓碑图片 */
    private BufferedImage tombstoneImage;

    /** 冒险模式按钮图片 */
    private BufferedImage adventureModeImage;

    /** 无限模式按钮图片 */
    private BufferedImage endlessModeImage;

    /** 图鉴按钮图片 */
    private BufferedImage inventoryImage;

    /** 拼图游戏按钮图片 */
    private BufferedImage puzzleImage;

    // 树木图片
    private BufferedImage treeImage;

    // 木牌图片
    private BufferedImage woodSign1Image;
    private BufferedImage woodSign2Image;
    private BufferedImage woodSign3Image;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public MainScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.hoverButton = null;
        this.soundEnabled = Constants.DEFAULT_SOUND_ENABLED;
        this.musicEnabled = Constants.DEFAULT_MUSIC_ENABLED;

        // 加载背景图片
        backgroundImage = ResourceManager.getInstance().getBackgroundImage("主菜单背景");

        // 加载房屋图片
        houseImage = ResourceManager.getInstance().getBackgroundImage("房屋");

        // 加载墓碑图片
        tombstoneImage = ResourceManager.getInstance().getBackgroundImage("墓碑");

        // 加载按钮图片
        adventureModeImage = ResourceManager.getInstance().getBackgroundImage("冒险模式");
        endlessModeImage = ResourceManager.getInstance().getBackgroundImage("无限模式");
        inventoryImage = ResourceManager.getInstance().getBackgroundImage("图鉴");
        puzzleImage = ResourceManager.getInstance().getBackgroundImage("拼图游戏");

        // 加载树木图片
        treeImage = ResourceManager.getInstance().getBackgroundImage("树木");

        // 加载木牌图片
        woodSign1Image = ResourceManager.getInstance().getBackgroundImage("WoodSign1");
        woodSign2Image = ResourceManager.getInstance().getBackgroundImage("WoodSign2");
        woodSign3Image = ResourceManager.getInstance().getBackgroundImage("WoodSign3");

        setLayout(null);
        setBackground(new Color(30, 30, 40));

        initUI();
        initListeners();
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        // 创建账号头像
        accountAvatar = new AccountAvatar(Constants.AVATAR_SIZE);
        accountAvatar.setBounds(Constants.AVATAR_X, Constants.AVATAR_Y,
                Constants.AVATAR_SIZE + 20, Constants.AVATAR_SIZE + 30);
        accountAvatar.setListener(() -> {
            gameFrame.showAccountSelectionScreen();
        });
        add(accountAvatar);

        // 初始化按钮区域
        int centerX = (int) (Constants.WINDOW_WIDTH *3.0/5.0);
        int startY = 30;
        int buttonWidth = 390;
        int buttonHeight = 170;
        int spacing = 0;

        adventureRect = new Rectangle(centerX - buttonWidth / 2+130, startY, buttonWidth, buttonHeight);
        endlessRect = new Rectangle(centerX - buttonWidth / 2+130, startY-20 + buttonHeight + spacing, buttonWidth-40, buttonHeight-10);
        inventoryRect = new Rectangle(centerX - buttonWidth / 2+130, startY-50 + (buttonHeight + spacing) * 3, buttonWidth-250, buttonHeight-20);
        puzzleRect = new Rectangle(centerX - buttonWidth / 2+135, startY -60+ (buttonHeight + spacing) * 2, buttonWidth-80, buttonHeight-25);

        // 音效/音乐开关按钮
        soundRect = new Rectangle(Constants.WINDOW_WIDTH - 120, Constants.WINDOW_HEIGHT - 80, 50, 30);
        musicRect = new Rectangle(Constants.WINDOW_WIDTH - 60, Constants.WINDOW_HEIGHT - 80, 50, 30);
    }

    /**
     * 初始化鼠标监听器
     */
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handlePress(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleRelease();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });
    }

    /**
     * 处理点击事件
     */
    private void handleClick(int x, int y) {
        // 检查菜单按钮
        if (adventureRect.contains(x, y)) {
            // 冒险模式 - 先进入选关界面
            gameFrame.showLevelSelectScreen();
            return;
        }

        if (endlessRect.contains(x, y)) {
            // 无限模式
            gameFrame.showGameScreen("endless", 0);
            return;
        }

        if (inventoryRect.contains(x, y)) {
            // 物品栏
            gameFrame.showInventoryScreen("plants");
            return;
        }

        if (puzzleRect.contains(x, y)) {
            // 拼图系统
            gameFrame.showPuzzleScreen();
            return;
        }

        // 检查音效/音乐开关
        if (soundRect.contains(x, y)) {
            soundEnabled = !soundEnabled;
            // TODO: 应用音效开关
            repaint();
            return;
        }

        if (musicRect.contains(x, y)) {
            musicEnabled = !musicEnabled;
            // TODO: 应用音乐开关
            repaint();
            return;
        }
    }

    /**
     * 处理按下事件
     */
    private void handlePress(int x, int y) {
        // 预留
    }

    /**
     * 处理释放事件
     */
    private void handleRelease() {
        // 预留
    }

    /**
     * 处理悬停事件
     */
    private void handleHover(int x, int y) {
        String oldHover = hoverButton;

        if (adventureRect.contains(x, y)) {
            hoverButton = "adventure";
        } else if (endlessRect.contains(x, y)) {
            hoverButton = "endless";
        } else if (inventoryRect.contains(x, y)) {
            hoverButton = "inventory";
        } else if (puzzleRect.contains(x, y)) {
            hoverButton = "puzzle";
        } else {
            hoverButton = null;
        }

        if (oldHover != hoverButton) {
            repaint();
        }
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制背景
        drawBackground(g2d);

        // 绘制游戏标题
        drawTitle(g2d);

        // 绘制菜单按钮
        drawMenuButtons(g2d);

        // 绘制音效/音乐开关
        drawAudioButtons(g2d);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics2D g) {
        // 绘制背景图片
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // 绘制房屋图片（左侧和下侧与界面边边重合，占高度的一半，保持原始比例）
        if (houseImage != null) {
            int houseOriginalWidth = houseImage.getWidth();
            int houseOriginalHeight = houseImage.getHeight();
            double houseAspectRatio = (double) houseOriginalWidth / houseOriginalHeight; // 宽度/高度

            // 计算房屋图片高度（占屏幕高度的一半）
            int houseHeight = (int) (getHeight() *3.0/5.0);
            int houseWidth = (int) (houseHeight * houseAspectRatio+500);

            // 计算位置（左侧对齐，下侧对齐）
            int houseX = 0; // 左侧与界面边边重合
            int houseY = getHeight() - houseHeight; // 下侧与界面边边重合

            g.drawImage(houseImage, houseX, houseY, houseWidth, houseHeight, null);
        }

        // 绘制墓碑图片
        if (tombstoneImage != null) {
            int tombstoneOriginalWidth = tombstoneImage.getWidth();
            int tombstoneOriginalHeight = tombstoneImage.getHeight();
            double tombstoneAspectRatio = (double) tombstoneOriginalHeight / tombstoneOriginalWidth; // 高度/宽度

            // 计算墓碑图片宽度
            int tombstoneWidth = (int) (getWidth() *5.0/6.0);
            int tombstoneHeight = (int) (tombstoneWidth * tombstoneAspectRatio+90);

            // 计算位置（右侧对齐，下侧对齐）
            int tombstoneX = getWidth() - tombstoneWidth;
            int tombstoneY = getHeight() - tombstoneHeight;

            g.drawImage(tombstoneImage, tombstoneX, tombstoneY, tombstoneWidth, tombstoneHeight, null);
        }

        // 绘制树木
        if (treeImage != null) {
            int treeWidth = 450;
            int treeHeight = (int) (treeWidth * (double) treeImage.getHeight() / treeImage.getWidth()+650);
            int treeY = getHeight() - treeHeight;
            g.drawImage(treeImage, 0, treeY, treeWidth, treeHeight, null);
        }
    }

    /**
     * 绘制木牌标题
     */
    private void drawTitle(Graphics2D g) {
        // 三张木牌占整个界面的左2/5和上1/3
        int totalWidth = (Constants.WINDOW_WIDTH * 2) / 5;
        int totalHeight = (Constants.WINDOW_HEIGHT * 3) / 7;
        int signX = 0; // 从最左边开始
        int signY = 0; // 从最上边开始

        // 按照 2:1.5:1.5 的比例分配高度
        int height1 = (totalHeight * 3) / 5;  // 木牌1占2份
        int height2 = (totalHeight * 3) / 10; // 木牌2占1.5份
        int height3 = (totalHeight * 3) / 10; // 木牌3占1.5份

        // 绘制第一张木牌
        if (woodSign1Image != null) {
            g.drawImage(woodSign1Image, signX, signY, totalWidth, height1, null);
        }

        // 如果有第二张木牌，接着绘制
        if (woodSign2Image != null) {
            int sign2Y = signY + height1;
            g.drawImage(woodSign2Image, signX, sign2Y-8, totalWidth, height2, null);
        }

        // 如果有第三张木牌，接着绘制
        if (woodSign3Image != null) {
            int sign3Y = signY + height1 + height2;
            g.drawImage(woodSign3Image, signX, sign3Y-16, totalWidth, height3, null);
        }
    }

    /**
     * 绘制菜单按钮
     */
    private void drawMenuButtons(Graphics2D g) {
        // 冒险模式按钮（使用图片）
        drawImageButton(g, adventureRect, adventureModeImage, hoverButton == "adventure");

        // 无限模式按钮（使用图片）
        drawImageButton(g, endlessRect, endlessModeImage, hoverButton == "endless");

        // 图鉴按钮（使用图片）
        drawImageButton(g, inventoryRect, inventoryImage, hoverButton == "inventory");

        // 拼图游戏按钮（使用图片）
        drawImageButton(g, puzzleRect, puzzleImage, hoverButton == "puzzle");
    }

    /**
     * 绘制图片按钮
     */
    private void drawImageButton(Graphics2D g, Rectangle rect, BufferedImage image, boolean hover) {
        // 绘制按钮图片
        if (image != null) {
            // 悬停时调整透明度
            if (hover) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 完全不透明
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); // 稍微透明
            }
            g.drawImage(image, rect.x, rect.y, rect.width, rect.height, null);
            // 恢复透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    /**
     * 绘制音效/音乐开关
     */
    private void drawAudioButtons(Graphics2D g) {
        // 音效按钮
        g.setColor(soundEnabled ? new Color(80, 120, 80) : new Color(80, 80, 80));
        g.fillRoundRect(soundRect.x, soundRect.y, soundRect.width, soundRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("🔊", soundRect.x + 15, soundRect.y + 22);

        // 音乐按钮
        g.setColor(musicEnabled ? new Color(80, 120, 80) : new Color(80, 80, 80));
        g.fillRoundRect(musicRect.x, musicRect.y, musicRect.width, musicRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("🎵", musicRect.x + 15, musicRect.y + 22);
    }
}