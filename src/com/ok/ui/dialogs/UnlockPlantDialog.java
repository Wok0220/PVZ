package com.ok.ui.dialogs;

import com.ok.ui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * 新植物解锁弹窗
 * 通关解锁新植物时显示，展示新植物的信息和能力
 */
public class UnlockPlantDialog extends JDialog {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 解锁的植物ID */
    private String plantId;

    /** 植物名称 */
    private String plantName;

    /** 植物描述 */
    private String plantDescription;

    /** 植物阳光消耗 */
    private int sunCost;

    /** 植物伤害 */
    private int damage;

    /** 植物生命值 */
    private int health;

    /** 植物攻击速度（毫秒） */
    private int attackCooldown;

    /** 对话框宽度 */
    private static final int DIALOG_WIDTH = 450;

    /** 对话框高度 */
    private static final int DIALOG_HEIGHT = 400;

    /** 按钮区域 */
    private Rectangle continueRect;

    /** 当前悬停 */
    private boolean hoverContinue;

    /**
     * 构造函数
     * @param gameFrame 主窗口
     * @param plantId 解锁的植物ID
     */
    public UnlockPlantDialog(GameFrame gameFrame, String plantId) {
        super(gameFrame, "新植物解锁", true);
        this.gameFrame = gameFrame;
        this.plantId = plantId;
        this.hoverContinue = false;

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(gameFrame);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        loadPlantInfo();
        initButton();
        initListeners();
    }

    /**
     * 加载植物信息
     */
    private void loadPlantInfo() {
        switch (plantId) {
            case "PeaShooter":
                plantName = "豌豆射手";
                plantDescription = "基础攻击型植物，向前方发射豌豆子弹。\n\n" +
                        "能力：\n" +
                        "• 攻击方式：发射豌豆子弹\n" +
                        "• 伤害：25\n" +
                        "• 攻击速度：1.5秒/次\n" +
                        "• 阳光消耗：100\n\n" +
                        "适合作为主力输出，放置在中后排。";
                sunCost = 100;
                damage = 25;
                health = 100;
                attackCooldown = 1500;
                break;

            case "Sunflower":
                plantName = "向日葵";
                plantDescription = "生产型植物，定期产生阳光资源。\n\n" +
                        "能力：\n" +
                        "• 生产阳光：25阳光/次\n" +
                        "• 生产间隔：24秒\n" +
                        "• 阳光消耗：50\n\n" +
                        "是游戏前期重要的阳光来源，建议优先种植。";
                sunCost = 50;
                damage = 0;
                health = 100;
                attackCooldown = 24000;
                break;

            case "WallNut":
                plantName = "坚果墙";
                plantDescription = "防御型植物，拥有高生命值，可阻挡僵尸前进。\n\n" +
                        "能力：\n" +
                        "• 生命值：400\n" +
                        "• 阳光消耗：50\n\n" +
                        "适合放置在最前方，为后排植物争取时间。";
                sunCost = 50;
                damage = 0;
                health = 400;
                attackCooldown = 0;
                break;

            case "SnowPea":
                plantName = "寒冰射手";
                plantDescription = "攻击型植物，发射寒冰子弹，命中后减速僵尸。\n\n" +
                        "能力：\n" +
                        "• 攻击方式：发射寒冰子弹\n" +
                        "• 伤害：20\n" +
                        "• 减速效果：减速50%，持续3秒\n" +
                        "• 攻击速度：1.5秒/次\n" +
                        "• 阳光消耗：175\n\n" +
                        "适合配合高伤害植物使用，延缓僵尸进攻速度。";
                sunCost = 175;
                damage = 20;
                health = 100;
                attackCooldown = 1500;
                break;

            case "PotatoMine":
                plantName = "土豆雷";
                plantDescription = "陷阱型植物，需要时间激活，僵尸触碰后爆炸。\n\n" +
                        "能力：\n" +
                        "• 激活时间：10秒\n" +
                        "• 爆炸伤害：1800（秒杀）\n" +
                        "• 爆炸范围：单格\n" +
                        "• 阳光消耗：25\n\n" +
                        "适合提前种植在僵尸必经之路，性价比高。";
                sunCost = 25;
                damage = 1800;
                health = 100;
                attackCooldown = 10000;
                break;

            case "CherryBomb":
                plantName = "樱桃炸弹";
                plantDescription = "一次性爆炸植物，种植后立即爆炸，大范围秒杀僵尸。\n\n" +
                        "能力：\n" +
                        "• 爆炸范围：半径2格\n" +
                        "• 爆炸伤害：1800（秒杀）\n" +
                        "• 阳光消耗：150\n\n" +
                        "适合应对僵尸密集的紧急情况，可扭转战局。";
                sunCost = 150;
                damage = 1800;
                health = 100;
                attackCooldown = 0;
                break;

            case "Repeater":
                plantName = "双发射手";
                plantDescription = "攻击型植物，每次攻击发射两颗豌豆子弹。\n\n" +
                        "能力：\n" +
                        "• 攻击方式：发射2颗豌豆\n" +
                        "• 每颗伤害：25\n" +
                        "• 子弹间隔：0.1秒\n" +
                        "• 攻击速度：1.5秒/轮\n" +
                        "• 阳光消耗：200\n\n" +
                        "火力是豌豆射手的2倍，适合后期强力输出。";
                sunCost = 200;
                damage = 25;
                health = 100;
                attackCooldown = 1500;
                break;

            case "TallNut":
                plantName = "高坚果";
                plantDescription = "防御型植物，极高生命值，可阻挡跳跃僵尸。\n\n" +
                        "能力：\n" +
                        "• 生命值：800\n" +
                        "• 特殊能力：阻挡撑杆跳僵尸\n" +
                        "• 阳光消耗：125\n\n" +
                        "是终极防线，可承受大量伤害。";
                sunCost = 125;
                damage = 0;
                health = 800;
                attackCooldown = 0;
                break;

            default:
                plantName = "新植物";
                plantDescription = "已解锁新植物！\n\n在物品栏中可以查看详细信息。";
                sunCost = 100;
                damage = 25;
                health = 100;
                attackCooldown = 1500;
                break;
        }
    }

    /**
     * 初始化按钮区域
     */
    private void initButton() {
        int buttonWidth = 120;
        int buttonHeight = 40;
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

        // 绘制植物图标
        drawPlantIcon(g2d);

        // 绘制植物信息
        drawPlantInfo(g2d);

        // 绘制属性标签
        drawAttributes(g2d);

        // 绘制按钮
        drawButton(g2d);
    }

    /**
     * 绘制对话框面板
     */
    private void drawPanel(Graphics2D g) {
        // 圆角背景
        g.setColor(new Color(50, 45, 40));
        g.fillRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 边框（金色）
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(10, 10, DIALOG_WIDTH - 20, DIALOG_HEIGHT - 20, 20, 20);

        // 装饰光效
        g.setColor(new Color(255, 200, 100, 50));
        for (int i = 0; i < 3; i++) {
            g.drawRoundRect(10 + i, 10 + i, DIALOG_WIDTH - 20 - i * 2,
                    DIALOG_HEIGHT - 20 - i * 2, 20, 20);
        }

        // 星光效果
        long time = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI * 2 / 12 + time * 0.005;
            int radius = 15 + (int)(Math.sin(time * 0.01) * 3);
            int x = DIALOG_WIDTH / 2 + (int)(Math.cos(angle) * radius);
            int y = 55 + (int)(Math.sin(angle) * radius);

            g.setColor(new Color(255, 255, 100, 150));
            g.fillOval(x - 2, y - 2, 4, 4);
        }
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        String title = "新植物解锁！";
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (DIALOG_WIDTH - fm.stringWidth(title)) / 2;

        // 阴影
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 2, 62);

        // 主文字
        g.setColor(new Color(255, 215, 0));
        g.drawString(title, titleX, 60);
    }

    /**
     * 绘制植物图标
     */
    private void drawPlantIcon(Graphics2D g) {
        int iconSize = 100;
        int iconX = DIALOG_WIDTH / 2 - iconSize / 2;
        int iconY = 85;

        // 图标背景光晕
        g.setColor(new Color(100, 200, 100, 80));
        g.fillOval(iconX - 5, iconY - 5, iconSize + 10, iconSize + 10);

        // 图标背景
        g.setColor(new Color(80, 120, 80));
        g.fillRoundRect(iconX, iconY, iconSize, iconSize, 20, 20);

        // 绘制植物图标
        drawPlantIconDetail(g, iconX + 15, iconY + 15, iconSize - 30);

        // 植物名称
        g.setFont(new Font("微软雅黑", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int nameX = (DIALOG_WIDTH - fm.stringWidth(plantName)) / 2;
        int nameY = iconY + iconSize + 20;
        g.setColor(Color.WHITE);
        g.drawString(plantName, nameX, nameY);
    }

    /**
     * 绘制植物图标细节
     */
    private void drawPlantIconDetail(Graphics2D g, int x, int y, int size) {
        // 植物身体
        g.setColor(new Color(100, 150, 100));
        g.fillOval(x, y, size, size);

        // 叶子
        g.setColor(new Color(50, 100, 50));
        g.fillOval(x - 8, y + size / 3, 12, 10);
        g.fillOval(x + size - 4, y + size / 3, 12, 10);

        // 眼睛
        g.setColor(Color.WHITE);
        g.fillOval(x + size / 3, y + size / 3, 10, 10);
        g.fillOval(x + size * 2 / 3 - 10, y + size / 3, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(x + size / 3 + 3, y + size / 3 + 3, 4, 4);
        g.fillOval(x + size * 2 / 3 - 7, y + size / 3 + 3, 4, 4);

        // 微笑
        g.drawArc(x + size / 4, y + size / 2, size / 2, size / 3, 0, -180);

        // 如果是攻击型植物，添加攻击特效
        if (damage > 0 && !plantId.equals("PotatoMine") && !plantId.equals("CherryBomb")) {
            g.setColor(new Color(255, 200, 100));
            g.fillOval(x + size - 5, y + size / 2 - 3, 8, 8);
        }

        // 如果是生产型植物，添加阳光特效
        if (plantId.equals("Sunflower")) {
            g.setColor(new Color(255, 215, 0));
            g.fillOval(x + size / 2 - 3, y - 5, 6, 6);
        }

        // 如果是防御型植物，添加盾牌特效
        if (plantId.equals("WallNut") || plantId.equals("TallNut")) {
            g.setColor(new Color(100, 150, 200, 100));
            g.fillRoundRect(x + size / 4, y + size / 2, size / 2, size / 3, 5, 5);
        }
    }

    /**
     * 绘制植物信息
     */
    private void drawPlantInfo(Graphics2D g) {
        int infoY = 210;

        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 分割线
        g.setColor(new Color(100, 80, 60));
        g.drawLine(30, infoY - 5, DIALOG_WIDTH - 30, infoY - 5);

        // 描述文字（支持多行）
        String[] lines = plantDescription.split("\n");
        int lineY = infoY;
        for (String line : lines) {
            FontMetrics fm = g.getFontMetrics();
            int lineX = (DIALOG_WIDTH - fm.stringWidth(line)) / 2;
            g.setColor(new Color(200, 180, 150));
            g.drawString(line, lineX, lineY);
            lineY += 22;
        }
    }

    /**
     * 绘制属性标签
     */
    private void drawAttributes(Graphics2D g) {
        int startY = DIALOG_HEIGHT - 130;
        int attrWidth = 80;
        int spacing = 20;
        int centerX = DIALOG_WIDTH / 2;

        // 阳光消耗
        drawAttributeBadge(g, centerX - attrWidth - spacing, startY,
                "🌞", String.valueOf(sunCost), new Color(255, 200, 50));

        // 伤害
        drawAttributeBadge(g, centerX - attrWidth / 2, startY,
                "⚔️", damage > 0 ? String.valueOf(damage) : "无",
                damage > 0 ? new Color(255, 100, 100) : new Color(150, 150, 150));

        // 生命值
        drawAttributeBadge(g, centerX + spacing, startY,
                "❤️", String.valueOf(health), new Color(100, 200, 100));

        // 攻击速度（如果有）
        if (attackCooldown > 0 && !plantId.equals("PotatoMine")) {
            drawAttributeBadge(g, centerX + attrWidth + spacing, startY,
                    "⏱️", (attackCooldown / 1000) + "s", new Color(100, 150, 200));
        }
    }

    /**
     * 绘制属性徽章
     */
    private void drawAttributeBadge(Graphics2D g, int x, int y, String icon, String value, Color color) {
        int width = 70;
        int height = 50;

        // 背景
        g.setColor(new Color(60, 50, 45));
        g.fillRoundRect(x, y, width, height, 10, 10);

        // 边框
        g.setColor(color);
        g.setStroke(new BasicStroke(1));
        g.drawRoundRect(x, y, width, height, 10, 10);

        // 图标
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        int iconX = x + (width - fm.stringWidth(icon)) / 2;
        g.setColor(color);
        g.drawString(icon, iconX, y + 22);

        // 数值
        g.setFont(new Font("Arial", Font.BOLD, 14));
        fm = g.getFontMetrics();
        int valueX = x + (width - fm.stringWidth(value)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(value, valueX, y + 42);
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
        g.setFont(new Font("微软雅黑", Font.BOLD, 14));
        String text = "继续游戏";
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