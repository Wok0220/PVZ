package com.ok.ui;

/**
 * 可刷新接口
 * 所有需要根据玩家进度刷新数据的界面都应实现此接口
 *
 * 使用场景：
 * - 切换账号后，所有界面需要刷新显示
 * - 通关后，主界面的关卡进度需要更新
 * - 兑换拼图后，拼图界面需要更新显示
 */
public interface Refreshable {

    /**
     * 刷新界面数据
     * 实现类应根据当前玩家进度（从 DataManager 获取）更新界面显示
     */
    void refresh();
}