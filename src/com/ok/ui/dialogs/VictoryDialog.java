package com.ok.ui.dialogs;

import com.ok.ui.GameFrame;
import com.ok.ui.screens.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * 胜利弹窗
 * 通关后显示，展示获得鸽子数量和解锁的新植物
 */
public class VictoryDialog extends JDialog {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 游戏界面引用 */
    private GameScreen gameScreen;

    /** 解锁的植物ID */
    private String unlockedPlant;

    /** 获得的鸽子数量 */
    private int pigeonsEarned;

    /** 对话框宽度 */
    private static final int DIALOG_WIDTH = 400;

    /** 对话框高度 */
    private static final int DIALOG_HEIGHT = 350;

    /** 按钮区域 */
    private Rectangle continueRect;

    /** 当前悬停的按钮 */
    private boolean hoverContinue;

    /** 植物名称 */
    private String plantName;

    /** 植物描述 */
    private String plantDescription;

    /**
     * 构造函数
     * @param gameFrame 主窗口
     * @param gameScreen 游戏界面
     * @param unlockedPlant 解锁的植物ID（可为null）
     * @param pigeonsEarned 获得的鸽子数量
     */
    public VictoryDialog(GameFrame gameFrame, GameScreen gameScreen,
                         String unlockedPlant, int pigeonsEarned) {
        super(gameFrame, "胜利", true);
        this.gameFrame = gameFrame;
        this.gameScreen = gameScreen;
        this.unlockedPlant = unlockedPlant;
        this.pigeonsEarned = pigeonsEarned;
        this.hoverContinue = false;

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(gameFrame);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        initButton();
        initListeners();
        loadPlantInfo();
    }

    /**
     * 初始化按钮区域
     */
    private void initButton() {
        int buttonWidth = 150;
        int buttonHeight = 45;
        int centerX = DIALOG_WIDTH / 2;
        int buttonY = DIALOG_HEIGHT - 70;

        continueRect = new Rectangle(centerX - buttonWidth / 2, buttonY, buttonWidth, buttonHeight);
    }

    /**
     * 初始化鼠标监听器
     */
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (continueRect.contains(e.getX(), e.getY())) {
                    dispose();
                    gameFrame.showMainScreen();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                hoverContinue = continueRect.contains(e.getX(), e.getY());
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverContinue = continueRect.contains(e.getX(), e.getY());
                repaint();
            }
        });
    }

    /**
     * 加载植物信息
     */
    private void loadPlantInfo() {
        if (unlockedPlant != null) {
            switch (unlockedPlant) {
                case "PeaShooter":
                    plantName = "豌豆射手";
                    plantDescription = "基础攻击型植物\n向前方发射豌豆子弹";
                    break;
                case "Sunflower":
                    plantName = "向日葵";
                    plantDescription = "生产型植物\n定期产生阳光";
                    break;
                case "WallNut":
                    plantName = "坚果墙";
                    plantDescription = "防御型植物\n高生命值，阻挡僵尸前进";
                    break;
                case "SnowPea":
                    plantName = "寒冰射手";
                    plantDescription = "攻击型植物\n发射寒冰子弹，减速僵尸";
                    break;
                case "PotatoMine":
                    plantName = "土豆雷";
                    plantDescription = "陷阱型植物\n需要时间激活，爆炸秒杀僵尸";
                    break;
                case "CherryBomb":
                    plantName = "樱桃炸弹";
                    plantDescription = "一次性爆炸植物\n大范围秒杀僵尸";
                    break;
                case "Repeater":
                    plantName = "双发射手";
                    plantDescription = "攻击型植物\n每次攻击发射两颗子弹";
                    break;
                case "TallNut":
                    plantName = "高坚果";
                    plantDescription = "防御型植物\n极高生命值，阻挡跳跃僵尸";
                    break;
                default:
                    plantName = "新植物";
                    plantDescription = "已解锁新植物！";
                    break;
            }
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

        // 绘制鸽子奖励
        drawPigeonReward(g2d);

        // 绘制新植物信息（如果有）
        if (unlockedPlant != null) {
            drawNewPlant(g2d);
        }

        // 绘制继续按钮
        drawButton(g2d);
    }

    /**
     * 绘制对话框面板
     */
    private void drawPanel(Graphics2D g) {
        // 圆角背景
        g.setColor(new Color(50, 45, 40));
        g.fillRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 边框
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 装饰光效
        g.setColor(new Color(255, 200, 100, 50));
        for (int i = 0; i < 3; i++) {
            g.drawRoundRect(10 + i, 10 + i, DIALOG_WIDTH - 20 - i * 2,
                    DIALOG_HEIGHT - 20 - i * 2, 20, 20);
        }
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        String title = "胜利！";
        g.setFont(new Font("微软雅黑", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (DIALOG_WIDTH - fm.stringWidth(title)) / 2;

        // 阴影
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 2, 62);

        // 主文字
        g.setColor(new Color(255, 215, 0));
        g.drawString(title, titleX, 60);

        // 烟花效果
        drawFireworks(g);
    }

    /**
     * 绘制烟花效果
     */
    private void drawFireworks(Graphics2D g) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8 + time * 0.005;
            int radius = 20 + (int)(Math.sin(time * 0.01) * 5);
            int x = DIALOG_WIDTH / 2 + (int)(Math.cos(angle) * radius);
            int y = 45 + (int)(Math.sin(angle) * radius);

            g.setColor(new Color(255, 100 + (int)(Math.sin(angle) * 155),
                    100 + (int)(Math.cos(angle) * 155), 200));
            g.fillOval(x - 3, y - 3, 6, 6);
        }
    }

    /**
     * 绘制鸽子奖励
     */
    private void drawPigeonReward(Graphics2D g) {
        int startY = 100;

        // 鸽子图标区域
        int iconSize = 50;
        int iconX = DIALOG_WIDTH / 2 - iconSize / 2;
        int iconY = startY;

        g.setColor(new Color(100, 80, 120));
        g.fillRoundRect(iconX, iconY, iconSize, iconSize, 15, 15);

        // 绘制鸽子
        g.setColor(new Color(220, 220, 240));
        g.fillOval(iconX + 15, iconY + 15, 20, 20);
        g.fillOval(iconX + 20, iconY + 8, 10, 10);

        // 鸽子数量
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(new Color(255, 215, 0));
        String countText = "+" + pigeonsEarned;
        FontMetrics fm = g.getFontMetrics();
        int textX = DIALOG_WIDTH / 2 - fm.stringWidth(countText) / 2;
        int textY = iconY + iconSize + 25;
        g.drawString(countText, textX, textY);

        // 说明文字
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        g.setColor(new Color(200, 200, 150));
        String tip = "获得鸽子 x " + pigeonsEarned;
        fm = g.getFontMetrics();
        textX = DIALOG_WIDTH / 2 - fm.stringWidth(tip) / 2;
        g.drawString(tip, textX, textY + 25);
    }

    /**
     * 绘制新植物信息
     */
    private void drawNewPlant(Graphics2D g) {
        int startY = 180;

        // 植物图标区域
        int iconSize = 60;
        int iconX = DIALOG_WIDTH / 2 - iconSize / 2;
        int iconY = startY;

        // 植物图标背景
        g.setColor(new Color(80, 120, 80));
        g.fillRoundRect(iconX, iconY, iconSize, iconSize, 15, 15);

        // 绘制植物图标
        drawPlantIcon(g, iconX + 10, iconY + 10, iconSize - 20);

        // 植物名称
        g.setFont(new Font("微软雅黑", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int nameX = DIALOG_WIDTH / 2 - fm.stringWidth(plantName) / 2;
        int nameY = iconY + iconSize + 15;
        g.setColor(new Color(100, 200, 100));
        g.drawString(plantName, nameX, nameY);

        // 植物描述
        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        String[] lines = plantDescription.split("\n");
        int descY = nameY + 25;
        for (String line : lines) {
            fm = g.getFontMetrics();
            int descX = DIALOG_WIDTH / 2 - fm.stringWidth(line) / 2;
            g.setColor(new Color(180, 180, 150));
            g.drawString(line, descX, descY);
            descY += 20;
        }
    }

    /**
     * 绘制植物图标
     */
    private void drawPlantIcon(Graphics2D g, int x, int y, int size) {
        // 简单绘制植物轮廓
        g.setColor(new Color(100, 150, 100));
        g.fillOval(x, y, size, size);

        // 叶子
        g.setColor(new Color(50, 100, 50));
        g.fillOval(x - 5, y + size / 3, 10, 8);
        g.fillOval(x + size - 5, y + size / 3, 10, 8);

        // 眼睛
        g.setColor(Color.WHITE);
        g.fillOval(x + size / 3, y + size / 3, 8, 8);
        g.fillOval(x + size * 2 / 3 - 8, y + size / 3, 8, 8);
        g.setColor(Color.BLACK);
        g.fillOval(x + size / 3 + 2, y + size / 3 + 2, 4, 4);
        g.fillOval(x + size * 2 / 3 - 6, y + size / 3 + 2, 4, 4);

        // 微笑
        g.drawArc(x + size / 4, y + size / 2, size / 2, size / 3, 0, -180);
    }

    /**
     * 绘制按钮
     */
    private void drawButton(Graphics2D g) {
        // 按钮背景
        if (hoverContinue) {
            g.setColor(new Color(100, 80, 50));
        } else {
            g.setColor(new Color(70, 60, 45));
        }
        g.fillRoundRect(continueRect.x, continueRect.y, continueRect.width, continueRect.height, 15, 15);

        // 边框
        if (hoverContinue) {
            g.setColor(new Color(255, 200, 100));
            g.setStroke(new BasicStroke(2));
        } else {
            g.setColor(new Color(100, 80, 60));
            g.setStroke(new BasicStroke(1));
        }
        g.drawRoundRect(continueRect.x, continueRect.y, continueRect.width, continueRect.height, 15, 15);

        // 文字
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        String text = "继续";
        FontMetrics fm = g.getFontMetrics();
        int textX = continueRect.x + (continueRect.width - fm.stringWidth(text)) / 2;
        int textY = continueRect.y + (continueRect.height + fm.getAscent() - fm.getDescent()) / 2;

        if (hoverContinue) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(text, textX, textY);
    }
}