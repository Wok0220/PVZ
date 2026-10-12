package com.ok.data;

/**
 * 数据管理器
 * 单例模式，持有当前的 IDataProvider 实例
 * 游戏逻辑通过此类访问数据，不直接依赖具体的 IDataProvider 实现
 *
 * 前期：setProvider(new LocalDataProvider())
 * 后期账号切换：setProvider(new MultiAccountDataProvider(account))
 */
// DataManager.java
public class DataManager {
    private static DataManager instance;
    private IDataProvider provider;

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public void setProvider(IDataProvider provider) {
        this.provider = provider;
    }

    public IDataProvider getProvider() {
        if (provider == null) {
            // 添加默认提供者，避免崩溃
            provider = new LocalDataProvider();
            System.out.println("使用默认数据提供者");
        }
        return provider;
    }
}