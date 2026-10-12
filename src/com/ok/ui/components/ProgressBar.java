package com.ok.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * 进度条组件
 * 用于显示关卡进度、波次进度等
 */
public class ProgressBar extends JPanel {

    public ProgressBar(int i, int width, int height, int i1) {
    }

    /** 进度类型 */
    public enum ProgressType {
        LEVEL,      // 关卡进度
        WAVE,       // 波次进度
        HEALTH,     // 生命值进度
        COOLDOWN    // 冷却进度
    }

    /** 进度条类型 */
    private ProgressType type;

    /** 当前进度（0-1） */
    private float progress;

    /** 目标进度（用于动画） */
    private float targetProgress;

    /** 动画计时器 */
    private int animationTimer;

    /** 动画持续时间（毫秒） */
    private static final int ANIMATION_DURATION = 300;

    /** 进度条宽度 */
    private int barWidth;

    /** 进度条高度 */
    private int barHeight;

    /** 进度条颜色 */
    private Color progressColor;

    /** 进度条背景颜色 */
    private Color bgColor;

    /** 边框颜色 */
    private Color borderColor;

    /** 文字颜色 */
    private Color textColor;

    /** 是否显示百分比文字 */
    private boolean showPercentage;

    /** 是否显示标签文字 */
    private boolean showLabel;

    /** 标签文字 */
    private String labelText;

    /** 当前数值（用于显示） */
    private int currentValue;

    /** 最大值（用于显示） */
    private int maxValue;

    /** 是否显示数值文字 */
    private boolean showValue;

    /** 是否闪烁（进度达到100%时） */
    private boolean isBlinking;

    /** 闪烁计时器 */
    private int blinkTimer;

    /** 闪烁持续时间（毫秒） */
    private static final int BLINK_DURATION = 1000;

    /** 闪烁间隔（毫秒） */
    private static final int BLINK_INTERVAL = 200;

    /**
     * 构造函数
     * @param type 进度条类型
     * @param width 宽度
     * @param height 高度
     */
    public ProgressBar(ProgressType type, int width, int height) {
        this.type = type;
        this.barWidth = width;
        this.barHeight = height;
        this.progress = 0f;
        this.targetProgress = 0f;
        this.animationTimer = 0;
        this.isBlinking = false;
        this.blinkTimer = 0;
        this.showPercentage = true;
        this.showLabel = true;
        this.showValue = false;
        this.currentValue = 0;
        this.maxValue = 100;

        setPreferredSize(new Dimension(barWidth, barHeight));
        setOpaque(false);

        initColors();
        initLabel();
    }

    /**
     * 初始化颜色
     */
    private void initColors() {
        switch (type) {
            case LEVEL:
                progressColor = new Color(100, 200, 255);
                bgColor = new Color(60, 60, 80);
                borderColor = new Color(80, 80, 100);
                textColor = Color.WHITE;
                break;
            case WAVE:
                progressColor = new Color(255, 150, 50);
                bgColor = new Color(60, 60, 80);
                borderColor = new Color(100, 80, 60);
                textColor = Color.WHITE;
                break;
            case HEALTH:
                progressColor = new Color(100, 255, 100);
                bgColor = new Color(60, 60, 80);
                borderColor = new Color(80, 100, 80);
                textColor = Color.WHITE;
                break;
            case COOLDOWN:
                progressColor = new Color(150, 150, 150);
                bgColor = new Color(80, 80, 80);
                borderColor = new Color(100, 100, 100);
                textColor = new Color(200, 200, 200);
                break;
            default:
                progressColor = new Color(100, 200, 100);
                bgColor = new Color(60, 60, 60);
                borderColor = new Color(80, 80, 80);
                textColor = Color.WHITE;
                break;
        }
    }

    /**
     * 初始化标签文字
     */
    private void initLabel() {
        switch (type) {
            case LEVEL:
                labelText = "关卡进度";
                break;
            case WAVE:
                labelText = "波次进度";
                break;
            case HEALTH:
                labelText = "生命值";
                break;
            case COOLDOWN:
                labelText = "冷却中";
                break;
            default:
                labelText = "进度";
                break;
        }
    }

    /**
     * 设置进度
     * @param progress 进度值（0-1）
     */
    public void setProgress(float progress) {
        this.targetProgress = Math.max(0, Math.min(1, progress));
        this.animationTimer = ANIMATION_DURATION;

        // 更新数值显示
        this.currentValue = (int)(targetProgress * maxValue);

        // 检查是否完成（进度达到100%）
        if (targetProgress >= 0.99f && !isBlinking) {
            startBlink();
        } else if (targetProgress < 0.99f && isBlinking) {
            stopBlink();
        }

        repaint();
    }

    /**
     * 设置进度（数值）
     * @param current 当前值
     * @param max 最大值
     */
    public void setProgress(int current, int max) {
        this.currentValue = current;
        this.maxValue = max;
        setProgress((float) current / max);
    }

    /**
     * 开始闪烁（进度完成时）
     */
    public void startBlink() {
        isBlinking = true;
        blinkTimer = BLINK_DURATION;
        repaint();
    }

    /**
     * 停止闪烁
     */
    public void stopBlink() {
        isBlinking = false;
        blinkTimer = 0;
        repaint();
    }

    /**
     * 设置是否显示百分比文字
     */
    public void setShowPercentage(boolean show) {
        this.showPercentage = show;
        repaint();
    }

    /**
     * 设置是否显示标签文字
     */
    public void setShowLabel(boolean show) {
        this.showLabel = show;
        repaint();
    }

    /**
     * 设置是否显示数值文字
     */
    public void setShowValue(boolean show) {
        this.showValue = show;
        repaint();
    }

    /**
     * 设置标签文字
     */
    public void setLabelText(String text) {
        this.labelText = text;
        repaint();
    }

    /**
     * 获取当前进度
     */
    public float getProgress() {
        return progress;
    }

    /**
     * 更新动画（每帧调用）
     * @param deltaTime 帧间隔时间（秒）
     */
    public void update(float deltaTime) {
        // 更新数值动画
        if (animationTimer > 0) {
            animationTimer -= deltaTime * 1000;
            if (animationTimer <= 0) {
                animationTimer = 0;
                progress = targetProgress;
            } else {
                float t = 1.0f - (float) animationTimer / ANIMATION_DURATION;
                // 缓动效果（ease out）
                float eased = 1 - (float)Math.pow(1 - t, 2);
                progress = this.progress + (targetProgress - this.progress) * eased;
                if (Math.abs(progress - targetProgress) < 0.01f) {
                    progress = targetProgress;
                }
            }
            repaint();
        }

        // 更新闪烁
        if (isBlinking) {
            blinkTimer -= deltaTime * 1000;
            if (blinkTimer <= 0) {
                isBlinking = false;
                repaint();
            } else {
                // 每隔 BLINK_INTERVAL 切换一次
                int blinkCycle = (int)(blinkTimer / BLINK_INTERVAL);
                if (blinkCycle % 2 == 0) {
                    repaint();
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算绘图区域
        int labelWidth = 0;
        int valueWidth = 0;
        int barStartX = 0;

        // 绘制标签
        if (showLabel && labelText != null && !labelText.isEmpty()) {
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            labelWidth = fm.stringWidth(labelText) + 10;
            g2d.setColor(textColor);
            g2d.drawString(labelText, 5, (barHeight + fm.getAscent() - fm.getDescent()) / 2);
            barStartX = labelWidth;
        }

        // 绘制数值文字
        if (showValue && maxValue > 0) {
            String valueText = currentValue + "/" + maxValue;
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            FontMetrics fm = g2d.getFontMetrics();
            valueWidth = fm.stringWidth(valueText) + 5;
            g2d.setColor(textColor);
            g2d.drawString(valueText, barWidth - valueWidth - 5, (barHeight + fm.getAscent() - fm.getDescent()) / 2);
        }

        // 计算进度条区域
        int progressBarWidth = barWidth - labelWidth - valueWidth - 10;
        int progressBarX = labelWidth + 5;
        int progressBarY = (barHeight - 12) / 2;
        int progressBarHeight = 12;

        // 绘制进度条背景
        g2d.setColor(bgColor);
        g2d.fillRoundRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight, 6, 6);

        // 绘制边框
        g2d.setColor(borderColor);
        g2d.drawRoundRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight, 6, 6);

        // 绘制进度
        int filledWidth = (int)(progressBarWidth * progress);
        if (filledWidth > 0) {
            // 根据闪烁状态决定颜色
            Color currentColor = progressColor;
            if (isBlinking) {
                int blinkCycle = (int)(blinkTimer / BLINK_INTERVAL);
                if (blinkCycle % 2 == 0) {
                    currentColor = new Color(255, 100, 100);
                }
            }

            g2d.setColor(currentColor);
            g2d.fillRoundRect(progressBarX, progressBarY, filledWidth, progressBarHeight, 6, 6);

            // 添加渐变效果
            GradientPaint gradient = new GradientPaint(
                    progressBarX, progressBarY, currentColor,
                    progressBarX + filledWidth, progressBarY, currentColor.brighter()
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(progressBarX, progressBarY, filledWidth, progressBarHeight, 6, 6);
        }

        // 绘制百分比文字
        if (showPercentage && progressBarWidth > 60) {
            String percentText = String.format("%.0f%%", progress * 100);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = progressBarX + (progressBarWidth - fm.stringWidth(percentText)) / 2;
            int textY = progressBarY + (progressBarHeight + fm.getAscent() - fm.getDescent()) / 2;

            // 文字阴影
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(percentText, textX + 1, textY + 1);
            g2d.setColor(Color.WHITE);
            g2d.drawString(percentText, textX, textY);
        }

        // 绘制完成时的光晕效果
        if (progress >= 0.99f && !isBlinking) {
            g2d.setColor(new Color(255, 255, 100, 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(progressBarX - 1, progressBarY - 1,
                    progressBarWidth + 2, progressBarHeight + 2, 8, 8);
        }
    }

    /**
     * 获取悬停提示文本
     */
    public String getTooltipText() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(labelText).append(": ");

        if (maxValue > 0) {
            sb.append(currentValue).append("/").append(maxValue);
        } else {
            sb.append(String.format("%.0f%%", progress * 100));
        }

        if (progress >= 0.99f) {
            sb.append("<br><font color='green'>已完成！</font>");
        }

        sb.append("</html>");
        return sb.toString();
    }
}