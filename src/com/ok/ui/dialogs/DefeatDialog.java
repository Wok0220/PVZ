package com.ok.ui.dialogs;

import com.ok.ui.GameFrame;
import com.ok.ui.screens.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * 失败弹窗
 * 游戏失败后显示，提供重新开始和返回主界面选项
 */
public class DefeatDialog extends JDialog {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 游戏界面引用 */
    private GameScreen gameScreen;

    /** 对话框宽度 */
    private static final int DIALOG_WIDTH = 350;

    /** 对话框高度 */
    private static final int DIALOG_HEIGHT = 280;

    /** 按钮区域 */
    private Rectangle restartRect;
    private Rectangle exitRect;

    /** 当前悬停的按钮 */
    private String hoverButton;

    /**
     * 构造函数
     * @param gameFrame 主窗口
     * @param gameScreen 游戏界面
     */
    public DefeatDialog(GameFrame gameFrame, GameScreen gameScreen) {
        super(gameFrame, "失败", true);
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
        int buttonWidth = 140;
        int buttonHeight = 45;
        int startY = DIALOG_HEIGHT - 100;
        int spacing = 20;

        restartRect = new Rectangle(centerX - buttonWidth - spacing / 2, startY, buttonWidth, buttonHeight);
        exitRect = new Rectangle(centerX + spacing / 2, startY, buttonWidth, buttonHeight);
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
        if (restartRect.contains(x, y)) {
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
            // 返回主界面
            dispose();
            gameFrame.showMainScreen();
        }
    }

    /**
     * 处理悬停事件
     */
    private void handleHover(int x, int y) {
        String oldHover = hoverButton;

        if (restartRect.contains(x, y)) {
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

        // 绘制失败信息
        drawDefeatInfo(g2d);

        // 绘制按钮
        drawButton(g2d, restartRect, "重新开始", hoverButton == "restart");
        drawButton(g2d, exitRect, "返回主界面", hoverButton == "exit");
    }

    /**
     * 绘制对话框面板
     */
    private void drawPanel(Graphics2D g) {
        // 圆角背景
        g.setColor(new Color(50, 45, 40));
        g.fillRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 边框（红色调）
        g.setColor(new Color(150, 50, 50));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 装饰裂纹效果
        g.setColor(new Color(100, 40, 40, 100));
        g.drawLine(30, 50, 50, 70);
        g.drawLine(60, 45, 80, 65);
        g.drawLine(DIALOG_WIDTH - 50, 80, DIALOG_WIDTH - 30, 100);
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        String title = "失败！";
        g.setFont(new Font("微软雅黑", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (DIALOG_WIDTH - fm.stringWidth(title)) / 2;

        // 阴影
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 2, 62);

        // 主文字（暗红色）
        g.setColor(new Color(180, 80, 80));
        g.drawString(title, titleX, 60);
    }

    /**
     * 绘制失败信息
     */
    private void drawDefeatInfo(Graphics2D g) {
        int centerY = 130;

        // 僵尸头像
        int iconSize = 60;
        int iconX = DIALOG_WIDTH / 2 - iconSize / 2;
        int iconY = centerY - 20;

        // 僵尸图标
        g.setColor(new Color(80, 100, 70));
        g.fillRoundRect(iconX, iconY, iconSize, iconSize, 15, 15);

        // 僵尸脸
        g.setColor(new Color(100, 120, 80));
        g.fillOval(iconX + 10, iconY + 10, iconSize - 20, iconSize - 20);

        // 眼睛（X形）
        g.setColor(Color.WHITE);
        g.fillOval(iconX + 18, iconY + 22, 10, 10);
        g.fillOval(iconX + 32, iconY + 22, 10, 10);
        g.setColor(Color.RED);
        g.drawLine(iconX + 20, iconY + 24, iconX + 26, iconY + 30);
        g.drawLine(iconX + 26, iconY + 24, iconX + 20, iconY + 30);
        g.drawLine(iconX + 34, iconY + 24, iconX + 40, iconY + 30);
        g.drawLine(iconX + 40, iconY + 24, iconX + 34, iconY + 30);

        // 嘴巴（向下）
        g.setColor(Color.BLACK);
        g.drawArc(iconX + 20, iconY + 40, 20, 12, 0, 180);

        // 失败文字
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        String message = "僵尸进入了房子！";
        FontMetrics fm = g.getFontMetrics();
        int msgX = (DIALOG_WIDTH - fm.stringWidth(message)) / 2;
        g.setColor(new Color(200, 150, 150));
        g.drawString(message, msgX, iconY + iconSize + 25);

        // 提示
        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        String tip = "再接再厉，重新挑战吧！";
        fm = g.getFontMetrics();
        int tipX = (DIALOG_WIDTH - fm.stringWidth(tip)) / 2;
        g.setColor(new Color(150, 120, 120));
        g.drawString(tip, tipX, iconY + iconSize + 50);
    }

    /**
     * 绘制按钮
     */
    private void drawButton(Graphics2D g, Rectangle rect, String text, boolean hover) {
        // 按钮背景
        if (hover) {
            g.setColor(new Color(100, 70, 60));
        } else {
            g.setColor(new Color(70, 50, 45));
        }
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // 边框
        if (hover) {
            g.setColor(new Color(200, 100, 100));
            g.setStroke(new BasicStroke(2));
        } else {
            g.setColor(new Color(100, 70, 60));
            g.setStroke(new BasicStroke(1));
        }
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // 文字
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height + fm.getAscent() - fm.getDescent()) / 2;

        if (hover) {
            g.setColor(new Color(255, 150, 150));
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(text, textX, textY);
    }
}