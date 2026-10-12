package com.ok.ui.screens;

import com.ok.resource.ResourceManager;
import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 加载页面
 * 显示游戏名称和 CLICK TO START
 */
public class LoadScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 按钮文字是否悬停 */
    private boolean isHovering = false;

    /** 按钮区域（用于点击检测） */
    private Rectangle buttonRect;

    /** 背景图片 */
    private BufferedImage backgroundImage;

    /** 进度条土地背景图片 */
    private BufferedImage progressBarLandImage;

    /** 草进度条图片 */
    private BufferedImage grassProgressBarImage;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public LoadScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(null);
        setBackground(Color.BLACK);

        // 加载背景图片
        backgroundImage = ResourceManager.getInstance().getBackgroundImage("游戏界面");

        // 加载进度条土地图片
        progressBarLandImage = ResourceManager.getInstance().getBackgroundImage("进度条土地");

        // 加载草进度条图片
        grassProgressBarImage = ResourceManager.getInstance().getBackgroundImage("草进度条");

        // 计算按钮区域（在paintComponent中绘制，这里先初始化）
        buttonRect = new Rectangle();

        // 添加鼠标监听
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (buttonRect.contains(e.getPoint())) {
                    // 点击点击开始，进入主界面
                    gameFrame.showMainScreen();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean wasHovering = isHovering;
                isHovering = buttonRect.contains(e.getPoint());
                if (wasHovering != isHovering) {
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean wasHovering = isHovering;
                isHovering = buttonRect.contains(e.getPoint());
                if (wasHovering != isHovering) {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 绘制背景图片
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // 绘制草进度条图片（土地进度条上方）
        int grassWidth = 400; // 与土地进度条宽度相同
        int grassHeight = 50; // 调整大小
        int grassX = (getWidth() - grassWidth) / 2;
        int grassY = getHeight() - grassHeight - 132; // 土地进度条上方
        g2d.drawImage(grassProgressBarImage, grassX, grassY, grassWidth, grassHeight, null);

        // 绘制进度条土地图片（下方）
        int landWidth = 400; // 调整大小
        int landHeight = 100; // 调整大小
        int landX = (getWidth() - landWidth) / 2;
        int landY = getHeight() - landHeight - 50; // 下方位置
        g2d.drawImage(progressBarLandImage, landX, landY, landWidth, landHeight, null);

        // 计算按钮位置和大小（在土地进度条内部）
        int buttonWidth = landWidth; // 与土地进度条宽度相同
        int buttonHeight = landHeight; // 与土地进度条高度相同
        int buttonX = landX; // 与土地进度条X坐标相同
        int buttonY = landY; // 与土地进度条Y坐标相同
        buttonRect.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        // 绘制点击开始按钮（使用文字）
        drawStartButton(g2d, buttonX, buttonY, buttonWidth, buttonHeight);
    }

    /**
     * 绘制开始按钮
     */
    private void drawStartButton(Graphics2D g2d, int x, int y, int width, int height) {
        // 绘制"点击开始"文字，不需要边框和背景
        String text = "点击开始";
        Font font = new Font("微软雅黑", Font.BOLD, 24); // 调整字体大小
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + fm.getAscent() - fm.getDescent()) / 2;

        // 悬停时文字变红色
        if (isHovering) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(Color.WHITE);
        }
        g2d.drawString(text, textX, textY);
    }

    @Override
    public void refresh() {
        // 加载页面无需刷新数据，但需要重置悬停状态
        isHovering = false;
        repaint();
    }
}