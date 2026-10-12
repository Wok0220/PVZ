package com.ok.ui.screens;

import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * 选关界面
 * 显示所有可选择的关卡
 */
public class LevelSelectScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 背景图片 */
    private BufferedImage backgroundImage;

    /** 返回按钮区域 */
    private Rectangle backButtonRect;

    /** 当前悬停的按钮 */
    private String hoverButton;

    /** 关卡按钮区域 */
    private Rectangle[] levelRects;

    /** 关卡图片 */
    private BufferedImage levelImage;

    /** 第2关图片 */
    private BufferedImage levelImage2;

    /** 关卡数量 */
    private int levelCount = 2;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public LevelSelectScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.hoverButton = null;

        // 加载背景图片
        backgroundImage = ResourceManager.getInstance().getBackgroundImage("选关背景");

        // 加载关卡图片
        levelImage = ResourceManager.getInstance().getCardImage("1-1");
        levelImage2 = ResourceManager.getInstance().getCardImage("1-2");

        setLayout(null);
        setBackground(new Color(30, 30, 40));

        initUI();
        initListeners();
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        // 返回按钮区域（移到中间下方）
        backButtonRect = new Rectangle(
                (Constants.WINDOW_WIDTH - 120) / 2 - 100,
                Constants.WINDOW_HEIGHT - 120,
                260,
                55
        );

        // 初始化关卡按钮区域（两关并排显示）
        levelRects = new Rectangle[levelCount];
        int levelWidth = 150;
        int levelHeight = 150;
        int centerX = Constants.WINDOW_WIDTH / 2;
        int startY = (Constants.WINDOW_HEIGHT - levelHeight) / 2 - 50;
        int spacing = 50;

        // 第1关（左）
        levelRects[0] = new Rectangle(centerX - levelWidth - spacing / 2, startY, levelWidth, levelHeight);

        // 第2关（右）
        if (levelCount > 1) {
            levelRects[1] = new Rectangle(centerX + spacing / 2, startY, levelWidth, levelHeight);
        }
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
        // 检查返回按钮
        if (backButtonRect.contains(x, y)) {
            gameFrame.showMainScreen();
            return;
        }

        // 检查关卡按钮
        for (int i = 0; i < levelRects.length; i++) {
            if (levelRects[i] != null && levelRects[i].contains(x, y)) {
                // 进入对应关卡（闯关模式，关卡索引为i）
                gameFrame.showGameScreen("campaign", i);
                return;
            }
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

        if (backButtonRect.contains(x, y)) {
            hoverButton = "back";
        } else {
            hoverButton = null;
            for (int i = 0; i < levelRects.length; i++) {
                if (levelRects[i] != null && levelRects[i].contains(x, y)) {
                    hoverButton = "level" + i;
                    break;
                }
            }
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

        // 绘制标题
        drawTitle(g2d);

        // 绘制关卡按钮
        drawLevelButtons(g2d);

        // 绘制返回按钮
        drawBackButton(g2d);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics2D g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        String title = "选择关卡";

        // 阴影
        g.setFont(new Font("微软雅黑", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (Constants.WINDOW_WIDTH - titleWidth) / 2;

        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 3, 83);

        // 主标题
        GradientPaint gradient = new GradientPaint(
                titleX, 80, new Color(255, 215, 0),
                titleX + titleWidth, 80, new Color(255, 100, 0)
        );
        g.setPaint(gradient);
        g.drawString(title, titleX, 80);
    }

    /**
     * 绘制关卡按钮
     */
    private void drawLevelButtons(Graphics2D g) {
        for (int i = 0; i < levelRects.length; i++) {
            if (levelRects[i] == null) continue;

            boolean isHover = hoverButton != null && hoverButton.equals("level" + i);

            // 选择对应的图片
            BufferedImage img = null;
            if (i == 0) {
                img = levelImage;
            } else if (i == 1) {
                img = levelImage2;
            }

            // 绘制关卡图片
            if (img != null) {
                if (isHover) {
                    // 悬停时放大效果
                    int x = levelRects[i].x - 5;
                    int y = levelRects[i].y - 5;
                    int w = levelRects[i].width + 10;
                    int h = levelRects[i].height + 10;
                    g.drawImage(img, x, y, w, h, null);
                } else {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                    g.drawImage(img, levelRects[i].x, levelRects[i].y,
                            levelRects[i].width, levelRects[i].height, null);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            }
        }
    }

    /**
     * 绘制返回按钮
     */
    private void drawBackButton(Graphics2D g) {
        boolean isHover = hoverButton != null && hoverButton.equals("back");

        // 加载返回图片
        BufferedImage backImage = ResourceManager.getInstance().getUIImage("返回亮");

        if (isHover) {
            // 悬停时：显示图片
            if (backImage != null) {
                g.drawImage(backImage, backButtonRect.x, backButtonRect.y,
                        backButtonRect.width, backButtonRect.height, null);
            }
        }
    }
}