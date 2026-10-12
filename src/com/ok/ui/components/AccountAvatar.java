package com.ok.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 账号头像组件
 * 显示当前账号的头像，点击可打开账号管理界面
 * 前期显示静态占位，预留点击接口
 */
public class AccountAvatar extends JPanel {

    /** 头像大小 */
    private int avatarSize;

    /** 头像图片 */
    private Image avatarImage;

    /** 默认头像图片（未登录/占位） */
    private Image defaultAvatarImage;

    /** 账号名称 */
    private String accountName;

    /** 是否悬停 */
    private boolean isHovering;

    /** 是否可点击 */
    private boolean clickable;

    /** 是否显示边框 */
    private boolean showBorder;

    /** 是否显示名称 */
    private boolean showName;

    /** 头像点击监听器 */
    private AvatarClickListener listener;

    /** 头像背景颜色 */
    private Color bgColor;

    /** 头像边框颜色 */
    private Color borderColor;

    /** 账号名称颜色 */
    private Color nameColor;

    /**
     * 头像点击监听器接口
     */
    public interface AvatarClickListener {
        /**
         * 点击头像时调用
         */
        void onAvatarClick();
    }

    /**
     * 构造函数
     * @param size 头像大小（像素）
     */
    public AccountAvatar(int size) {
        this.avatarSize = size;
        this.accountName = "玩家";
        this.isHovering = false;
        this.clickable = true;
        this.showBorder = true;
        this.showName = true;
        this.bgColor = new Color(100, 100, 120);
        this.borderColor = new Color(200, 180, 100);
        this.nameColor = Color.WHITE;

        setPreferredSize(new Dimension(avatarSize + 20, avatarSize + 30));
        setOpaque(false);

        loadImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickable && listener != null) {
                    listener.onAvatarClick();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                repaint();
            }
        });
    }

    /**
     * 加载头像图片
     */
    private void loadImages() {
        // 从资源管理器加载默认头像
        // defaultAvatarImage = ResourceManager.getInstance().getAvatarImage();

        // 创建默认头像
        if (defaultAvatarImage == null) {
            defaultAvatarImage = createDefaultAvatar();
        }

        // 当前头像默认使用默认头像
        this.avatarImage = defaultAvatarImage;
    }

    /**
     * 创建默认头像图片
     */
    private Image createDefaultAvatar() {
        int size = avatarSize;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 圆形背景
        g.setColor(new Color(80, 100, 120));
        g.fillOval(0, 0, size, size);

        // 脸部
        g.setColor(new Color(255, 220, 180));
        g.fillOval(5, 5, size - 10, size - 10);

        // 眼睛
        g.setColor(Color.WHITE);
        g.fillOval(size / 3 - 5, size / 3, 8, 8);
        g.fillOval(size * 2 / 3 - 3, size / 3, 8, 8);
        g.setColor(Color.BLACK);
        g.fillOval(size / 3 - 3, size / 3 + 2, 4, 4);
        g.fillOval(size * 2 / 3 - 1, size / 3 + 2, 4, 4);

        // 微笑
        g.setColor(new Color(100, 70, 50));
        g.drawArc(size / 4, size / 2, size / 2, size / 3, 0, -180);

        // 腮红
        g.setColor(new Color(255, 150, 150, 100));
        g.fillOval(size / 4 - 5, size * 2 / 3 - 5, 10, 8);
        g.fillOval(size * 3 / 4 - 5, size * 2 / 3 - 5, 10, 8);

        g.dispose();
        return img;
    }

    /**
     * 设置账号信息
     * @param accountName 账号名称
     * @param avatarImage 头像图片（可选，null则使用默认）
     */
    public void setAccountInfo(String accountName, Image avatarImage) {
        this.accountName = accountName;
        if (avatarImage != null) {
            // 缩放头像到指定大小
            this.avatarImage = avatarImage.getScaledInstance(avatarSize, avatarSize, Image.SCALE_SMOOTH);
        } else {
            this.avatarImage = defaultAvatarImage;
        }
        repaint();
    }

    /**
     * 设置账号名称
     */
    public void setAccountName(String name) {
        this.accountName = name;
        repaint();
    }

    /**
     * 获取账号名称
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * 设置头像图片
     */
    public void setAvatarImage(Image image) {
        if (image != null) {
            this.avatarImage = image.getScaledInstance(avatarSize, avatarSize, Image.SCALE_SMOOTH);
        } else {
            this.avatarImage = defaultAvatarImage;
        }
        repaint();
    }

    /**
     * 设置点击监听器
     */
    public void setListener(AvatarClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置是否可点击
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    /**
     * 设置是否显示边框
     */
    public void setShowBorder(boolean show) {
        this.showBorder = show;
        repaint();
    }

    /**
     * 设置是否显示名称
     */
    public void setShowName(boolean show) {
        this.showName = show;
        repaint();
    }

    /**
     * 设置背景颜色
     */
    public void setBgColor(Color color) {
        this.bgColor = color;
        repaint();
    }

    /**
     * 设置边框颜色
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    /**
     * 设置名称颜色
     */
    public void setNameColor(Color color) {
        this.nameColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int avatarX = centerX - avatarSize / 2;
        int avatarY = 5;

        // 绘制阴影
        if (isHovering) {
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(avatarX + 2, avatarY + 2, avatarSize, avatarSize);
        }

        // 绘制背景圆形
        g2d.setColor(bgColor);
        g2d.fillOval(avatarX, avatarY, avatarSize, avatarSize);

        // 绘制头像
        if (avatarImage != null) {
            // 圆形裁剪
            Shape clip = new java.awt.geom.Ellipse2D.Float(avatarX, avatarY, avatarSize, avatarSize);
            g2d.setClip(clip);
            g2d.drawImage(avatarImage, avatarX, avatarY, avatarSize, avatarSize, null);
            g2d.setClip(null);
        }

        // 绘制边框
        if (showBorder) {
            if (isHovering && clickable) {
                // 悬停时边框高亮
                g2d.setColor(new Color(255, 200, 100));
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
            }
            g2d.drawOval(avatarX, avatarY, avatarSize, avatarSize);
        }

        // 绘制悬停光晕
        if (isHovering && clickable) {
            g2d.setColor(new Color(255, 200, 100, 50));
            g2d.fillOval(avatarX - 2, avatarY - 2, avatarSize + 4, avatarSize + 4);
        }

        // 绘制账号名称
        if (showName && accountName != null && !accountName.isEmpty()) {
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int nameWidth = fm.stringWidth(accountName);
            int nameX = centerX - nameWidth / 2;
            int nameY = avatarY + avatarSize + 15;

            // 文字阴影
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(accountName, nameX + 1, nameY + 1);

            // 文字
            g2d.setColor(nameColor);
            g2d.drawString(accountName, nameX, nameY);
        }

        // 绘制提示图标（表示可点击）
        if (clickable && !isHovering) {
            g2d.setColor(new Color(200, 200, 200, 150));
            g2d.fillOval(avatarX + avatarSize - 12, avatarY + avatarSize - 12, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(avatarX + avatarSize - 10, avatarY + avatarSize - 10, 4, 4);
        }
    }

    /**
     * 获取悬停提示文本
     */
    public String getTooltipText() {
        if (!clickable) {
            return null;
        }
        return "<html>账号: " + accountName + "<br>点击切换账号</html>";
    }
}