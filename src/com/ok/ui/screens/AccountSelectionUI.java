package com.ok.ui.screens;

import com.ok.account.Account;
import com.ok.account.AccountManager;
import com.ok.ui.GameFrame;
import com.ok.ui.Refreshable;
import com.ok.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号选择/管理界面
 * 启动时显示，或点击主菜单头像时显示
 */
public class AccountSelectionUI extends JPanel implements Refreshable {

    private GameFrame gameFrame;

    /** 按钮区域 */
    private List<Rectangle> accountRects;

    /** 删除按钮区域（对应每个账号） */
    private List<Rectangle> deleteButtonRects;

    /** 新建账号按钮 */
    private Rectangle newAccountRect;

    /** 悬停的账号索引 */
    private int hoveredAccountIndex = -1;

    /**
     * 构造函数
     * @param gameFrame 主窗口引用
     */
    public AccountSelectionUI(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.accountRects = new ArrayList<>();
        this.deleteButtonRects = new ArrayList<>();

        setLayout(null);
        setBackground(new Color(30, 30, 40));

        initUI();
        initListeners();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        int buttonWidth = 250;
        int buttonHeight = 80;
        int deleteButtonWidth = 50; // 删除按钮宽度
        int startY = 200;
        int spacing = 20;

        // 初始化账号按钮区域
        accountRects.clear();
        deleteButtonRects.clear();
        int centerX = (Constants.WINDOW_WIDTH - buttonWidth) / 2;

        List<Account> accounts = AccountManager.getInstance().getAllAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Rectangle rect = new Rectangle(centerX, startY + i * (buttonHeight + spacing), buttonWidth, buttonHeight);
            accountRects.add(rect);
            // 删除按钮在账号按钮右边
            Rectangle deleteRect = new Rectangle(centerX + buttonWidth + 10, startY + i * (buttonHeight + spacing) + 15, deleteButtonWidth, 50);
            deleteButtonRects.add(deleteRect);
        }

        // 新建账号按钮
        int newAccountY = startY + accounts.size() * (buttonHeight + spacing) + 40;
        newAccountRect = new Rectangle(centerX, newAccountY, buttonWidth, buttonHeight);
    }

    /**
     * 初始化监听器
     */
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });
    }

    /**
     * 处理点击事件
     */
    private void handleClick(int x, int y) {
        List<Account> accounts = AccountManager.getInstance().getAllAccounts();

        // 检查删除按钮
        for (int i = 0; i < deleteButtonRects.size(); i++) {
            if (deleteButtonRects.get(i).contains(x, y)) {
                Account account = accounts.get(i);
                int option = JOptionPane.showConfirmDialog(this,
                        "确定要删除账号 \"" + account.getPlayerName() + "\" 吗？\n此操作不可恢复！",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    AccountManager.getInstance().deleteAccount(account);
                    initUI();
                    repaint();
                }
                return;
            }
        }

        // 检查账号按钮
        for (int i = 0; i < accountRects.size(); i++) {
            if (accountRects.get(i).contains(x, y)) {
                Account account = accounts.get(i);
                selectAccount(account);
                return;
            }
        }

        // 检查新建账号按钮
        if (newAccountRect.contains(x, y)) {
            createNewAccount();
        }
    }

    /**
     * 处理鼠标移动
     */
    private void handleMouseMove(int x, int y) {
        int newHovered = -1;

        for (int i = 0; i < accountRects.size(); i++) {
            if (accountRects.get(i).contains(x, y)) {
                newHovered = i;
                break;
            }
        }

        if (newHovered != hoveredAccountIndex) {
            hoveredAccountIndex = newHovered;
            repaint();
        }
    }

    /**
     * 选择账号
     */
    private void selectAccount(Account account) {
        // 如果有密码，询问密码
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            String password = JOptionPane.showInputDialog(this, "请输入密码：", "密码验证", JOptionPane.PLAIN_MESSAGE);
            if (password == null || !account.verifyPassword(password)) {
                JOptionPane.showMessageDialog(this, "密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 选择账号
        boolean success = AccountManager.getInstance().selectAccount(account);
        if (success) {
            gameFrame.showLoadScreen();
        }
    }

    /**
     * 创建新账号
     */
    private void createNewAccount() {
        // 检查是否达到最大账号数
        if (AccountManager.getInstance().getAccountCount() >= Constants.MAX_ACCOUNT_COUNT) {
            JOptionPane.showMessageDialog(this,
                    "最多只能创建" + Constants.MAX_ACCOUNT_COUNT + "个账号！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 输入玩家名称
        String playerName = JOptionPane.showInputDialog(this, "请输入玩家名称：", "创建新账号", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            return;
        }

        // 询问是否设置密码
        int option = JOptionPane.showConfirmDialog(this, "是否设置密码？", "安全设置", JOptionPane.YES_NO_OPTION);
        Account account;

        if (option == JOptionPane.YES_OPTION) {
            String password = JOptionPane.showInputDialog(this, "请输入密码：", "设置密码", JOptionPane.PLAIN_MESSAGE);
            if (password != null && !password.trim().isEmpty()) {
                account = AccountManager.getInstance().createAccount(playerName.trim(), password.trim());
            } else {
                account = AccountManager.getInstance().createAccount(playerName.trim());
            }
        } else {
            account = AccountManager.getInstance().createAccount(playerName.trim());
        }

        if (account != null) {
            // 选择新账号
            AccountManager.getInstance().selectAccount(account);
            initUI();
            gameFrame.showLoadScreen();
        } else {
            JOptionPane.showMessageDialog(this, "创建账号失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        initUI();
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

        // 绘制账号按钮
        drawAccountButtons(g2d);

        // 绘制删除按钮
        drawDeleteButtons(g2d);

        // 绘制新建账号按钮
        drawNewAccountButton(g2d);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(30, 30, 40));
        g.fillRect(0, 0, getWidth(), getHeight());

        // 添加一些装饰效果
        g.setColor(new Color(100, 130, 150, 50));
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            g.fillOval(x, y, 3, 3);
        }
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g) {
        g.setFont(new Font("微软雅黑", Font.BOLD, 48));
        g.setColor(new Color(255, 215, 0));
        String title = "选择账号";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 120);
    }

    /**
     * 绘制账号按钮
     */
    private void drawAccountButtons(Graphics2D g) {
        List<Account> accounts = AccountManager.getInstance().getAllAccounts();

        for (int i = 0; i < accountRects.size(); i++) {
            Rectangle rect = accountRects.get(i);
            Account account = accounts.get(i);

            // 绘制按钮背景
            if (i == hoveredAccountIndex) {
                g.setColor(new Color(100, 150, 200));
            } else {
                g.setColor(new Color(80, 120, 160));
            }
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // 绘制边框
            g.setColor(new Color(200, 230, 255));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // 绘制玩家名称
            g.setFont(new Font("微软雅黑", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textX = rect.x + (rect.width - fm.stringWidth(account.getPlayerName())) / 2;
            int textY = rect.y + rect.height / 2 + fm.getAscent() / 2 - 5;
            g.drawString(account.getPlayerName(), textX, textY);

            // 绘制账号ID（小字）
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(new Color(200, 200, 200));
            String idText = "ID: " + account.getAccountId().substring(0, Math.min(account.getAccountId().length(), 10)) + "...";
            int idX = rect.x + 10;
            int idY = rect.y + rect.height - 10;
            g.drawString(idText, idX, idY);
        }
    }

    /**
     * 绘制删除按钮
     */
    private void drawDeleteButtons(Graphics2D g) {
        for (int i = 0; i < deleteButtonRects.size(); i++) {
            Rectangle rect = deleteButtonRects.get(i);

            // 绘制红色背景
            g.setColor(new Color(200, 50, 50));
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

            // 绘制边框
            g.setColor(new Color(255, 100, 100));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

            // 绘制 "X"
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            String text = "X";
            int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
            int textY = rect.y + rect.height / 2 + fm.getAscent() / 2 - 2;
            g.drawString(text, textX, textY);
        }
    }

    /**
     * 绘制新建账号按钮
     */
    private void drawNewAccountButton(Graphics2D g) {
        // 绘制按钮背景
        g.setColor(new Color(60, 140, 100));
        g.fillRoundRect(newAccountRect.x, newAccountRect.y, newAccountRect.width, newAccountRect.height, 15, 15);

        // 绘制边框
        g.setColor(new Color(120, 200, 160));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(newAccountRect.x, newAccountRect.y, newAccountRect.width, newAccountRect.height, 15, 15);

        // 绘制文字
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        String text = "创建新账号";
        FontMetrics fm = g.getFontMetrics();
        int textX = newAccountRect.x + (newAccountRect.width - fm.stringWidth(text)) / 2;
        int textY = newAccountRect.y + newAccountRect.height / 2 + fm.getAscent() / 2 - 5;
        g.drawString(text, textX, textY);
    }
}