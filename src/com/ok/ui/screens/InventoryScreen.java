package com.ok.ui.screens;

import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 图鉴界面
 * 显示植物和僵尸的介绍
 */
public class InventoryScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 背景图片（植物介绍） */
    private BufferedImage plantBackground;

    /** 背景图片（僵尸介绍） */
    private BufferedImage zombieBackground;

    /** 当前背景图片 */
    private BufferedImage currentBackground;

    /** 右侧箭头图片 */
    private BufferedImage rightArrowImage;

    /** 左侧箭头图片 */
    private BufferedImage leftArrowImage;

    /** 返回按钮图片 */
    private BufferedImage backButtonImage;

    /** 右侧箭头区域 */
    private Rectangle rightArrowRect;

    /** 左侧箭头区域 */
    private Rectangle leftArrowRect;

    /** 返回按钮区域 */
    private Rectangle backButtonRect;

    /** 植物全图鉴图片 */
    private BufferedImage plantGuideImage;

    /** 僵尸全图鉴图片 */
    private BufferedImage zombieGuideImage;

    /** 当前显示模式（true: 植物介绍, false: 僵尸介绍） */
    private boolean isPlantMode;

    /** 当前选中的介绍图片 */
    private BufferedImage selectedIntroImage;

    /** 豌豆射手介绍图片 */
    private BufferedImage peashooterIntroImage;

    /** 向日葵介绍图片 */
    private BufferedImage sunflowerIntroImage;

    /** 通用植物介绍图片 */
    private BufferedImage defaultPlantIntroImage;

    /** 普通僵尸介绍图片 */
    private BufferedImage normalZombieIntroImage;

    /** 路障僵尸介绍图片 */
    private BufferedImage coneheadZombieIntroImage;

    /** 通用僵尸介绍图片 */
    private BufferedImage defaultZombieIntroImage;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public InventoryScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.isPlantMode = true;

        setLayout(null);
        setBackground(new Color(70, 110, 70));

        loadImages();
        initUI();
        initListeners();
    }

    /**
     * 加载图片
     */
    private void loadImages() {
        // 加载背景图片
        plantBackground = ResourceManager.getInstance().getBackgroundImage("植物介绍");
        zombieBackground = ResourceManager.getInstance().getBackgroundImage("僵尸介绍");
        currentBackground = plantBackground;
        // 加载箭头图片
        rightArrowImage = ResourceManager.getInstance().getUIImage("箭头");
        leftArrowImage = ResourceManager.getInstance().getUIImage("箭头左");
        // 加载返回按钮图片
        backButtonImage = ResourceManager.getInstance().getUIImage("back");
        // 加载植物全图鉴和僵尸全图鉴图片
        plantGuideImage = ResourceManager.getInstance().getBackgroundImage("植物全图鉴");
        zombieGuideImage = ResourceManager.getInstance().getBackgroundImage("僵尸全图鉴");
        // 加载介绍图片
        peashooterIntroImage = ResourceManager.getInstance().getBackgroundImage("豌豆射手介绍");
        sunflowerIntroImage = ResourceManager.getInstance().getBackgroundImage("向日葵介绍");
        defaultPlantIntroImage = ResourceManager.getInstance().getBackgroundImage("介绍植物");
        // 加载僵尸介绍图片
        normalZombieIntroImage = ResourceManager.getInstance().getBackgroundImage("普通僵尸介绍");
        coneheadZombieIntroImage = ResourceManager.getInstance().getBackgroundImage("路障僵尸介绍");
        defaultZombieIntroImage = ResourceManager.getInstance().getBackgroundImage("介绍僵尸");
        // 默认选中通用介绍（根据当前模式）
        selectedIntroImage = isPlantMode ? defaultPlantIntroImage : defaultZombieIntroImage;
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        // 初始化返回按钮区域
        int backButtonWidth = 150;
        int backButtonHeight = 80;
        backButtonRect = new Rectangle(Constants.WINDOW_WIDTH - backButtonWidth - 20, Constants.WINDOW_HEIGHT - backButtonHeight - 35, backButtonWidth, backButtonHeight);

        // 初始化箭头区域
        int arrowWidth = 50;
        int arrowHeight = 50;
        int arrowY = 27; // 放在上面
        // 右侧箭头（显示植物时用）
        rightArrowRect = new Rectangle(Constants.WINDOW_WIDTH - arrowWidth - 50, arrowY, arrowWidth, arrowHeight);
        // 左侧箭头（显示僵尸时用）
        leftArrowRect = new Rectangle(50, arrowY, arrowWidth, arrowHeight);
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
        });
    }

    /**
     * 处理鼠标点击
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleMouseClick(int x, int y) {
        // 检查右侧箭头
        if (isPlantMode && rightArrowRect.contains(x, y)) {
            // 切换到僵尸介绍
            isPlantMode = false;
            currentBackground = zombieBackground;
            selectedIntroImage = defaultZombieIntroImage;  // 第153行添加
            repaint();
            return;
        }

        // 检查左侧箭头
        if (!isPlantMode && leftArrowRect.contains(x, y)) {
            // 切换到植物介绍
            isPlantMode = true;
            currentBackground = plantBackground;
            selectedIntroImage = defaultPlantIntroImage;  // 第162行添加
            repaint();
            return;
        }

        // 检查返回按钮
        if (backButtonRect.contains(x, y)) {
            // 返回主菜单
            gameFrame.showMainScreen();
            return;
        }

        // 检查网格点击（仅植物模式）
        if (isPlantMode) {
            int guideWidth = (Constants.WINDOW_WIDTH * 4) / 7;
            int guideHeight = Constants.WINDOW_HEIGHT - 180;
            int guideX = 60;
            int guideY = 90;

            int rows = 6;
            int cols = 8;
            int cellWidth = guideWidth / cols;
            int cellHeight = guideHeight / rows;

            // 检查点击是否在网格区域内
            if (x >= guideX && x <= guideX + guideWidth && y >= guideY && y <= guideY + guideHeight) {
                // 计算点击的网格位置
                int col = (x - guideX) / cellWidth;
                int row = (y - guideY) / cellHeight;

                // 根据位置选择介绍图片
                if (row == 0 && col == 0) {
                    selectedIntroImage = peashooterIntroImage;
                } else if (row == 0 && col == 1) {
                    selectedIntroImage = sunflowerIntroImage;
                } else {
                    selectedIntroImage = defaultPlantIntroImage;
                }

                repaint();
            }
        }

        // --- 这是第202行，在这后面添加下面的代码 ---

        // 检查网格点击（仅僵尸模式）
        if (!isPlantMode) {
            int guideWidth = (Constants.WINDOW_WIDTH * 4) / 7;
            int guideHeight = Constants.WINDOW_HEIGHT - 180;
            int guideX = 60;
            int guideY = 90;

            int rows = 6;
            int cols = 5;
            int cellWidth = guideWidth / cols;
            int cellHeight = guideHeight / rows;

            // 检查点击是否在网格区域内
            if (x >= guideX && x <= guideX + guideWidth && y >= guideY && y <= guideY + guideHeight) {
                // 计算点击的网格位置
                int col = (x - guideX) / cellWidth;
                int row = (y - guideY) / cellHeight;

                // 根据位置选择介绍图片
                if (row == 0 && col == 0) {
                    selectedIntroImage = normalZombieIntroImage;
                } else if (row == 0 && col == 2) {
                    selectedIntroImage = coneheadZombieIntroImage;
                } else {
                    selectedIntroImage = defaultZombieIntroImage;
                }

                repaint();
            }
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
        if (currentBackground != null) {
            g2d.drawImage(currentBackground, 0, 0, getWidth(), getHeight(), null);
        }
        // 绘制左侧的全图鉴图片
        BufferedImage guideImage = isPlantMode ? plantGuideImage : zombieGuideImage;
        if (guideImage != null) {
            int guideWidth = (Constants.WINDOW_WIDTH * 4) / 7; // 宽度占界面的2/3
            int guideHeight = Constants.WINDOW_HEIGHT - 180; // 高度为界面高度
            int guideX = 60; // 从最左侧开始
            int guideY = 90; // 从最上边开始
            g2d.drawImage(guideImage, guideX, guideY, guideWidth, guideHeight, null);
            // 添加高质量渲染提示
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(guideImage, guideX, guideY, guideWidth, guideHeight, null);
        }
        // 绘制网格
        if (guideImage != null) {
            int guideWidth = (Constants.WINDOW_WIDTH * 4) / 7;
            int guideHeight = Constants.WINDOW_HEIGHT - 180;
            int guideX = 60;
            int guideY = 90;

            int rows, cols;
            if (isPlantMode) {
                rows = 6;
                cols = 8;
            } else {
                rows = 6;
                cols = 5;
            }

            int cellWidth = guideWidth / cols;
            int cellHeight = guideHeight / rows;

            // 设置网格颜色（半透明白色）
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(1));

            // 绘制竖线
            for (int i = 0; i <= cols; i++) {
                int x = guideX + i * cellWidth;
                g2d.drawLine(x, guideY, x, guideY + guideHeight);
            }

            // 绘制横线
            for (int i = 0; i <= rows; i++) {
                int y = guideY + i * cellHeight;
                g2d.drawLine(guideX, y, guideX + guideWidth, y);
            }
        }
        // 绘制右侧介绍图片
        if (selectedIntroImage != null) {
            int introX = (Constants.WINDOW_WIDTH * 4) / 7 + 60 + 15; // 在图鉴图片右侧，留20像素间距
            int introY = 90;
            int introWidth = Constants.WINDOW_WIDTH - introX - 50; // 右侧剩余宽度
            int introHeight = Constants.WINDOW_HEIGHT - 210;
            g2d.drawImage(selectedIntroImage, introX, introY, introWidth, introHeight, null);
        }
        // 绘制标题
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        String title = isPlantMode ? "植物图鉴" : "僵尸图鉴";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 60);

        // 绘制箭头
        if (isPlantMode) {
            // 绘制右侧箭头
            if (rightArrowImage != null) {
                g2d.drawImage(rightArrowImage, rightArrowRect.x, rightArrowRect.y, rightArrowRect.width, rightArrowRect.height, null);
            }
        } else {
            // 绘制左侧箭头
            if (leftArrowImage != null) {
                g2d.drawImage(leftArrowImage, leftArrowRect.x, leftArrowRect.y, leftArrowRect.width, leftArrowRect.height, null);
            }
        }

        // 绘制返回按钮
        if (backButtonImage != null) {
            g2d.drawImage(backButtonImage, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, null);
        }
    }
}