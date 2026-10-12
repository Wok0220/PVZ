package com.ok.account;

import com.ok.data.DataManager;
import com.ok.utils.Constants;
import com.ok.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号管理器
 * 管理所有账号的创建、读取、删除、选择
 */
public class AccountManager {
    private static AccountManager instance;

    /** 当前选中的账号 */
    private Account currentAccount;

    /** 账号列表 */
    private List<Account> accountList;

    /**
     * 私有构造函数
     */
    private AccountManager() {
        this.accountList = new ArrayList<>();
        loadAccountList();
    }

    /**
     * 获取单例实例
     */
    public static AccountManager getInstance() {
        if (instance == null) {
            synchronized (AccountManager.class) {
                if (instance == null) {
                    instance = new AccountManager();
                }
            }
        }
        return instance;
    }

    /**
     * 加载账号列表
     */
    @SuppressWarnings("unchecked")
    private void loadAccountList() {
        // 确保目录存在
        File dir = new File(Constants.ACCOUNTS_SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 加载账号列表
        Object obj = FileUtil.loadObject(Constants.ACCOUNTS_LIST_FILE);
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            this.accountList = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Account) {
                    this.accountList.add((Account) item);
                }
            }
        }
    }

    /**
     * 保存账号列表
     */
    public boolean saveAccountList() {
        return FileUtil.saveObject(this.accountList, Constants.ACCOUNTS_LIST_FILE);
    }

    /**
     * 创建新账号
     * @param playerName 玩家名称
     * @return 新创建的账号
     */
    public Account createAccount(String playerName) {
        if (accountList.size() >= Constants.MAX_ACCOUNT_COUNT) {
            return null;
        }

        // 生成唯一账号ID
        String accountId = "account_" + System.currentTimeMillis();
        Account account = new Account(accountId, playerName);
        accountList.add(account);
        saveAccountList();
        return account;
    }

    /**
     * 创建带密码的账号
     */
    public Account createAccount(String playerName, String password) {
        Account account = createAccount(playerName);
        if (account != null && password != null && !password.isEmpty()) {
            account.setPassword(password);
            saveAccountList();
        }
        return account;
    }

    /**
     * 删除账号
     * @param account 要删除的账号
     * @return 是否删除成功
     */
    public boolean deleteAccount(Account account) {
        if (account == null) {
            return false;
        }

        // 删除账号存档文件
        String savePath = Constants.ACCOUNTS_SAVE_DIR + account.getSaveFileName();
        FileUtil.deleteFile(savePath);

        // 从列表移除
        boolean removed = accountList.remove(account);
        if (removed) {
            saveAccountList();

            // 如果删除的是当前账号，清除当前账号
            if (currentAccount != null && currentAccount.getAccountId().equals(account.getAccountId())) {
                currentAccount = null;
            }
        }

        return removed;
    }

    /**
     * 选择账号
     * @param account 要选择的账号
     * @return 是否选择成功
     */
    public boolean selectAccount(Account account) {
        if (account == null || !accountList.contains(account)) {
            return false;
        }

        currentAccount = account;
        currentAccount.updateLastLoginTime();
        saveAccountList();

        // 设置数据提供者
        String savePath = Constants.ACCOUNTS_SAVE_DIR + account.getSaveFileName();
        DataManager.getInstance().setProvider(new MultiAccountDataProvider(account, savePath));

        return true;
    }

    /**
     * 获取当前账号
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * 获取所有账号
     */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountList);
    }

    /**
     * 获取账号数量
     */
    public int getAccountCount() {
        return accountList.size();
    }

    /**
     * 检查是否有选中的账号
     */
    public boolean hasCurrentAccount() {
        return currentAccount != null;
    }

    /**
     * 检查是否有账号
     */
    public boolean hasAccounts() {
        return !accountList.isEmpty();
    }
}