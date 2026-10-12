package com.ok.account;

import java.io.Serializable;
import java.util.Date;

/**
 * 账号类
 * 存储账号的基本信息
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 账号ID（唯一标识） */
    private String accountId;

    /** 玩家名称 */
    private String playerName;

    /** 密码（可选，不加密仅作简单验证） */
    private String password;

    /** 创建时间 */
    private Date createTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    /** 头像索引（0-9，预留扩展） */
    private int avatarIndex;

    /**
     * 默认构造函数（反序列化用）
     */
    public Account() {
        this.createTime = new Date();
        this.lastLoginTime = new Date();
        this.avatarIndex = 0;
    }

    /**
     * 构造函数（创建新账号）
     * @param accountId 账号ID
     * @param playerName 玩家名称
     */
    public Account(String accountId, String playerName) {
        this();
        this.accountId = accountId;
        this.playerName = playerName;
    }

    /**
     * 构造函数（带密码）
     */
    public Account(String accountId, String playerName, String password) {
        this(accountId, playerName);
        this.password = password;
    }

    // ==================== Getter 和 Setter ====================

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginTime = new Date();
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword) {
        if (password == null || password.isEmpty()) {
            return true;
        }
        return password.equals(inputPassword);
    }

    /**
     * 获取存档文件名
     */
    public String getSaveFileName() {
        return accountId + ".json";
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}