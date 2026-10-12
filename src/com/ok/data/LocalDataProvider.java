package com.ok.data;

import com.ok.utils.FileUtil;
import com.ok.utils.Constants;

/**
 * 本地数据提供者（单账号版本）
 * 实现 IDataProvider 接口，提供单账号的本地文件存储
 *
 * 前期使用此实现，后期加账号系统时替换为 MultiAccountDataProvider
 */
public class LocalDataProvider implements IDataProvider {

    /** 当前玩家进度 */
    private PlayerProgress progress;

    /** 存档文件路径 */
    private final String savePath;

    /**
     * 构造函数
     * 自动加载已有存档或创建新存档
     */
    public LocalDataProvider() {
        this.savePath = Constants.SAVE_FILE_PATH;
        loadOrCreateProgress();
    }

    /**
     * 构造函数（自定义存档路径）
     * @param savePath 自定义存档路径
     */
    public LocalDataProvider(String savePath) {
        this.savePath = savePath;
        loadOrCreateProgress();
    }

    /**
     * 加载已有存档或创建新存档
     */
    private void loadOrCreateProgress() {
        // 尝试从文件加载
        Object obj = FileUtil.loadObject(savePath);
        if (obj instanceof PlayerProgress) {
            progress = (PlayerProgress) obj;
        } else {
            // 没有存档或加载失败，创建新进度
            progress = new PlayerProgress();
            saveProgress(progress);
        }
    }

    // ==================== IDataProvider 接口实现 ====================

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
            // 清除后创建新的空进度
            progress = new PlayerProgress();
            saveProgress(progress);
        }
        return deleted;
    }

    @Override
    public String getSaveFilePath() {
        return savePath;
    }
}