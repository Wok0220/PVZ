package com.ok.ui.screens;

import com.ok.data.DataManager;
import com.ok.data.PlayerProgress;
import com.ok.resource.ResourceManager;
import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * 拼图系统界面
 * 显示和处理拼图游戏
 */
public class PuzzleScreen extends JPanel implements Refreshable {

    /** 主窗口引用 */
    private GameFrame gameFrame;

    /** 拼图块数量 */
    private static final int PUZZLE_PIECE_COUNT = 4;

    /** 拼图块区域 */
    private Rectangle[] puzzlePieceRects;

    /** 拼图背景区域 */
    private Rectangle puzzleBackgroundRect;

    /** 返回按钮图片 */
    private BufferedImage backButtonImage;

    /** 返回按钮区域 */
    private Rectangle backButtonRect;

    /** 鸽子计数器区域 */
    private Rectangle pigeonRect;

    /** 选中的拼图块 */
    private int selectedPiece;

    /** 拼图块是否已解锁 */
    private boolean[] puzzlePiecesUnlocked;

    /** 界面背景图片 */
    private BufferedImage backgroundImage;

    /** 拼图图案图片 */
    private BufferedImage puzzleImage;

    /** 鸽子图标图片 */
    private BufferedImage pigeonImage;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public PuzzleScreen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.selectedPiece = -1;
        this.puzzlePiecesUnlocked = new boolean[PUZZLE_PIECE_COUNT];

        setLayout(null);
        setBackground(new Color(30, 30, 40));
        backgroundImage = ResourceManager.getInstance().getBackgroundImage("拼图背景");
        puzzleImage = ResourceManager.getInstance().getBackgroundImage("拼图图片");
        pigeonImage = ResourceManager.getInstance().getBackgroundImage("拼图计数");
        backButtonImage = ResourceManager.getInstance().getUIImage("back");

        loadPuzzleProgress();
        initUI();
        initListeners();
    }

    /**
     * 加载拼图进度
     */
    private void loadPuzzleProgress() {
        PlayerProgress progress = DataManager.getInstance().getProvider().getProgress();
        if (progress != null) {
            boolean[] pieces = progress.getPuzzlePieces();
            if (pieces != null && pieces.length == PUZZLE_PIECE_COUNT) {
                puzzlePiecesUnlocked = pieces;
            }
        }
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        // 拼图背景区域（正方形）
        int puzzleSize = 400; // 正方形大小
        int puzzleX = (Constants.WINDOW_WIDTH - puzzleSize) / 2;
        int puzzleY = 150;
        puzzleBackgroundRect = new Rectangle(puzzleX, puzzleY, puzzleSize, puzzleSize);

        // 拼图块区域
        puzzlePieceRects = new Rectangle[PUZZLE_PIECE_COUNT];
        int pieceSize = puzzleSize / 2; // 每个拼图块也是正方形

        for (int i = 0; i < PUZZLE_PIECE_COUNT; i++) {
            int pieceX = puzzleX + (i % 2) * pieceSize;
            int pieceY = puzzleY + (i / 2) * pieceSize;
            puzzlePieceRects[i] = new Rectangle(pieceX, pieceY, pieceSize, pieceSize);

            // 鸽子计数器（拼图正下方）
            int pigeonSize = pieceSize / 2; // 鸽子图片边长为拼图块的1/2
            int pigeonX = puzzleX + (puzzleSize - pigeonSize) / 2 - 25;
            int pigeonY = puzzleY + puzzleSize - 5;
            pigeonRect = new Rectangle(pigeonX, pigeonY, pigeonSize, pigeonSize);
        }

        // 返回按钮
        int backButtonWidth = 150;
        int backButtonHeight = 80;
        backButtonRect = new Rectangle(Constants.WINDOW_WIDTH - backButtonWidth - 20, Constants.WINDOW_HEIGHT - backButtonHeight - 35, backButtonWidth, backButtonHeight);
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
                handleDrag(e.getX(), e.getY());
            }
        });
    }

    /**
     * 处理点击事件
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleClick(int x, int y) {
        // 检查返回按钮
        if (backButtonRect.contains(x, y)) {
            gameFrame.showMainScreen();
            return;
        }

        // 检查拼图块
        for (int i = 0; i < PUZZLE_PIECE_COUNT; i++) {
            if (puzzlePieceRects[i].contains(x, y)) {
                if (puzzlePiecesUnlocked[i]) {
                    return;
                } else {
                    // 拼图块未解锁，检查鸽子数量
                    PlayerProgress progress = DataManager.getInstance().getProvider().getProgress();
                    if (progress != null && progress.getPigeonCount() >= 5) {
                        // 消耗5只鸽子解锁拼图块 - 使用接口方法自动保存
                        boolean spent = DataManager.getInstance().getProvider().spendPigeons(5);
                        if (spent) {
                            DataManager.getInstance().getProvider().unlockPuzzlePiece(i);
                            puzzlePiecesUnlocked[i] = true;
                            repaint();
                            JOptionPane.showMessageDialog(this,
                                    "拼图块已解锁！",
                                    "恭喜",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        // 鸽子不足
                        JOptionPane.showMessageDialog(this,
                                "古咕固不足！需要5只古咕固才能解锁。\n当前拥有：" +
                                        (progress != null ? progress.getPigeonCount() : 0) + "只",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                return;
            }
        }
    }

    /**
     * 处理按下事件
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handlePress(int x, int y) {
        // 预留：可以添加拖拽开始的逻辑
    }

    /**
     * 处理释放事件
     */
    private void handleRelease() {
        // 预留：可以添加拖拽结束的逻辑
    }

    /**
     * 处理悬停事件
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleHover(int x, int y) {
        // 预留：可以添加悬停效果
    }

    /**
     * 处理拖拽事件
     * @param x 鼠标X坐标
     * @param y 鼠标Y坐标
     */
    private void handleDrag(int x, int y) {
        // 预留：可以添加拖拽逻辑
    }

    @Override
    public void refresh() {
        loadPuzzleProgress();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制背景图片
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // 标题
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        String title = "拼图游戏";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 60);
        // 绘制鸽子计数器
        drawPigeonCounter(g2d);

        // 绘制拼图背景
        drawPuzzleBackground(g2d);

        // 绘制拼图块
        drawPuzzlePieces(g2d);

        // 绘制返回按钮
        if (backButtonImage != null) {
            g2d.drawImage(backButtonImage, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, null);
        }
    }

    /**
     * 绘制鸽子计数器
     */
    private void drawPigeonCounter(Graphics2D g) {
        // 加载玩家进度
        PlayerProgress progress = DataManager.getInstance().getProvider().getProgress();
        int pigeonCount = 0;
        if (progress != null) {
            pigeonCount = progress.getPigeonCount();
        }

        // 绘制鸽子图片
        if (pigeonImage != null && pigeonRect != null) {
            g.drawImage(pigeonImage, pigeonRect.x, pigeonRect.y, pigeonRect.width, pigeonRect.height, null);

            // 绘制 "=数量" 文字
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            String countText = "=" + pigeonCount;
            FontMetrics fm = g.getFontMetrics();
            int textX = pigeonRect.x + pigeonRect.width + 10;
            int textY = pigeonRect.y + pigeonRect.height / 2 + fm.getAscent() / 2;
            g.drawString(countText, textX, textY);
        }
    }

    /**
     * 绘制拼图背景
     */
    private void drawPuzzleBackground(Graphics2D g) {
        // 绘制完整拼图图案作为背景（淡色显示）
        if (puzzleImage != null) {
            g.setColor(new Color(255, 255, 255, 50)); // 半透明遮罩
            g.fillRoundRect(puzzleBackgroundRect.x, puzzleBackgroundRect.y,
                    puzzleBackgroundRect.width, puzzleBackgroundRect.height, 15, 15);
            g.drawImage(puzzleImage, puzzleBackgroundRect.x, puzzleBackgroundRect.y,
                    puzzleBackgroundRect.width, puzzleBackgroundRect.height, null);
        } else {
            g.setColor(new Color(50, 50, 70));
            g.fillRoundRect(puzzleBackgroundRect.x, puzzleBackgroundRect.y,
                    puzzleBackgroundRect.width, puzzleBackgroundRect.height, 15, 15);
        }
        g.setColor(Color.WHITE);
        g.drawRoundRect(puzzleBackgroundRect.x, puzzleBackgroundRect.y,
                puzzleBackgroundRect.width, puzzleBackgroundRect.height, 15, 15);
    }

    /**
     * 绘制拼图块
     */
    private void drawPuzzlePieces(Graphics2D g) {
        for (int i = 0; i < PUZZLE_PIECE_COUNT; i++) {
            Rectangle rect = puzzlePieceRects[i];

            if (puzzleImage != null) {
                // 计算拼图块在原图中的位置
                int pieceX = (i % 2) * (puzzleImage.getWidth() / 2);
                int pieceY = (i / 2) * (puzzleImage.getHeight() / 2);
                int pieceW = puzzleImage.getWidth() / 2;
                int pieceH = puzzleImage.getHeight() / 2;

                if (puzzlePiecesUnlocked[i]) {
                    // 拼图块已解锁，正常显示拼图图案
                    g.drawImage(puzzleImage,
                            rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
                            pieceX, pieceY, pieceX + pieceW, pieceY + pieceH, null);

                    // 选中效果
                    if (selectedPiece == i) {
                        g.setColor(new Color(255, 255, 0, 100));
                        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
                    }
                } else {
                    // 拼图块未解锁，显示变暗的拼图图案（类似植物卡片阳光不足的效果）
                    // 先绘制暗化的图片
                    g.drawImage(puzzleImage,
                            rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
                            pieceX, pieceY, pieceX + pieceW, pieceY + pieceH, null);

                    // 叠加一层暗色遮罩
                    g.setColor(new Color(0, 0, 0, 180)); // 暗色半透明遮罩
                    g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
                }
            }
            g.setColor(Color.WHITE);
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        }
    }
}