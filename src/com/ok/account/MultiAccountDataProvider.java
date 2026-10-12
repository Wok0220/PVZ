package com.ok.account;

import com.ok.data.IDataProvider;
import com.ok.data.PlayerProgress;
import com.ok.utils.FileUtil;

/**
 * 多账号数据提供者
 * 每个账号对应一个独立的存档文件
 */
public class MultiAccountDataProvider implements IDataProvider {

    /** 对应的账号 */
    private Account account;

    /** 存档文件路径 */
    private String savePath;

    /** 当前玩家进度 */
    private PlayerProgress progress;

    /**
     * 构造函数
     * @param account 对应的账号
     * @param savePath 存档文件路径
     */
    public MultiAccountDataProvider(Account account, String savePath) {
        this.account = account;
        this.savePath = savePath;
        loadOrCreateProgress();
    }

    /**
     * 加载已有存档或创建新存档
     */
    private void loadOrCreateProgress() {
        Object obj = FileUtil.loadObject(savePath);
        if (obj instanceof PlayerProgress) {
            progress = (PlayerProgress) obj;
        } else {
            progress = new PlayerProgress();
            saveProgress(progress);
        }
    }

    @Override
    public PlayerProgress getProgress() {
        return progress;
    }

    @Override
    public boolean saveProgress(PlayerProgress progress) {
        this.progress = progress;
        return FileUtil.saveObject(progress, savePath);
    }

    @Override
    public boolean hasSaveData() {
        return FileUtil.fileExists(savePath);
    }

    @Override
    public boolean clearSaveData() {
        boolean deleted = FileUtil.deleteFile(savePath);
        if (deleted) {
            progress = new PlayerProgress();
            saveProgress(progress);
        }
        return deleted;
    }

    @Override
    public String getSaveFilePath() {
        return savePath;
    }

    /**
     * 获取对应的账号
     */
    public Account getAccount() {
        return account;
    }
}