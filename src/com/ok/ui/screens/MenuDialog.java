package com.ok.ui.screens;

import com.ok.ui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * 暂停菜单弹窗
 * 游戏暂停时显示，包含重新开始、结束游戏、返回游戏按钮
 */
public class MenuDialog extends JDialog {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 游戏界面引用 */
    private GameScreen gameScreen;

    /** 对话框宽度 */
    private static final int DIALOG_WIDTH = 300;

    /** 对话框高度 */
    private static final int DIALOG_HEIGHT = 250;

    /** 按钮区域 */
    private Rectangle resumeRect;
    private Rectangle restartRect;
    private Rectangle exitRect;

    /** 当前悬停的按钮 */
    private String hoverButton;

    /**
     * 构造函数
     * @param gameFrame 主窗口
     * @param gameScreen 游戏界面
     */
    public MenuDialog(GameFrame gameFrame, GameScreen gameScreen) {
        super(gameFrame, "游戏菜单", true);
        this.gameFrame = gameFrame;
        this.gameScreen = gameScreen;
        this.hoverButton = null;

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(gameFrame);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        initButtons();
        initListeners();
    }

    /**
     * 初始化按钮区域
     */
    private void initButtons() {
        int centerX = DIALOG_WIDTH / 2;
        int buttonWidth = 180;
        int buttonHeight = 45;
        int startY = 70;
        int spacing = 15;

        resumeRect = new Rectangle(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight);
        restartRect = new Rectangle(centerX - buttonWidth / 2, startY + buttonHeight + spacing, buttonWidth, buttonHeight);
        exitRect = new Rectangle(centerX - buttonWidth / 2, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight);
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
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });
    }

    /**
     * 处理点击事件
     */
    private void handleClick(int x, int y) {
        if (resumeRect.contains(x, y)) {
            dispose();
        } else if (restartRect.contains(x, y)) {
            // 重新开始
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "确定要重新开始当前关卡吗？",
                    "重新开始",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else if (exitRect.contains(x, y)) {
            // 根据游戏模式决定返回哪里
            String mode = gameScreen.getGameMode();
            String message;
            if ("campaign".equals(mode)) {
                message = "确定要退出游戏返回选关界面吗？\n当前进度将不会保存。";
            } else {
                message = "确定要退出游戏返回主界面吗？\n当前进度将不会保存。";
            }
            
            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "退出游戏",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                if ("campaign".equals(mode)) {
                    gameFrame.showLevelSelectScreen();
                } else {
                    gameFrame.showMainScreen();
                }
                dispose();
            }
        }
    }

    /**
     * 处理悬停事件
     */
    private void handleHover(int x, int y) {
        String oldHover = hoverButton;

        if (resumeRect.contains(x, y)) {
            hoverButton = "resume";
        } else if (restartRect.contains(x, y)) {
            hoverButton = "restart";
        } else if (exitRect.contains(x, y)) {
            hoverButton = "exit";
        } else {
            hoverButton = null;
        }

        if (oldHover != hoverButton) {
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制半透明背景遮罩
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 绘制对话框面板
        drawPanel(g2d);

        // 绘制标题
        drawTitle(g2d);

        // 绘制按钮
        drawButton(g2d, resumeRect, "继续游戏", hoverButton == "resume");
        drawButton(g2d, restartRect, "重新开始", hoverButton == "restart");
        drawButton(g2d, exitRect, "结束游戏", hoverButton == "exit");
    }

    /**
     * 绘制对话框面板
     */
    private void drawPanel(Graphics2D g) {
        // 圆角背景
        g.setColor(new Color(50, 45, 40));
        g.fillRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 边框
        g.setColor(new Color(150, 120, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 装饰线条
        g.setColor(new Color(100, 80, 60));
        g.drawLine(20, 55, DIALOG_WIDTH - 20, 55);
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        String title = "游戏暂停";
        g.setFont(new Font("微软雅黑", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (DIALOG_WIDTH - fm.stringWidth(title)) / 2;

        g.setColor(new Color(255, 215, 0));
        g.drawString(title, titleX, 45);
    }

    /**
     * 绘制按钮
     */
    private void drawButton(Graphics2D g, Rectangle rect, String text, boolean hover) {
        // 按钮背景
        if (hover) {
            g.setColor(new Color(100, 80, 50));
        } else {
            g.setColor(new Color(70, 60, 45));
        }
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // 边框
        if (hover) {
            g.setColor(new Color(255, 200, 100));
            g.setStroke(new BasicStroke(2));
        } else {
            g.setColor(new Color(100, 80, 60));
            g.setStroke(new BasicStroke(1));
        }
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // 文字
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height + fm.getAscent() - fm.getDescent()) / 2;

        if (hover) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(text, textX, textY);
    }
}