package com.ok.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * 阳光计数器组件
 * 显示当前阳光数量
 */
public class SunCounter extends JPanel {

    /** 阳光数量 */
    private int sunAmount;

    /** 位置X */
    private int x;

    /** 位置Y */
    private int y;

    /**
     * 构造函数
     * @param x 位置X
     * @param y 位置Y
     */
    public SunCounter(int x, int y) {
        this.x = x;
        this.y = y;
        this.sunAmount = 100; // 初始阳光数量

        setBounds(x, y, 100, 50);
        setOpaque(false);

    }

    /**
     * 设置阳光数量
     * @param sunAmount 阳光数量
     */
    public void setSunAmount(int sunAmount) {
        this.sunAmount = sunAmount;
        repaint();
    }

    /**
     * 获取阳光数量
     * @return 阳光数量
     */
    public int getSunAmount() {
        return sunAmount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制阳光数量
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(sunAmount), 45, 30);
    }
}