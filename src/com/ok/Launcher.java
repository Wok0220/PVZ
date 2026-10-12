import com.ok.data.DataManager;
import com.ok.data.LocalDataProvider;
import com.ok.resource.ResourceManager;
import com.ok.ui.GameFrame;

import javax.swing.*;

public static void main(String[] args) {
    // 1. 初始化数据管理器（暂时使用默认，稍后账号选择后会切换）
    System.out.println("初始化数据管理器...");
    DataManager.getInstance().setProvider(new LocalDataProvider());
    System.out.println("数据管理器初始化完成！");

    // 2. 预加载所有资源
    System.out.println("正在预加载游戏资源...");
    ResourceManager.getInstance().preloadAllResources();
    System.out.println("资源预加载完成！");

    // 3. 启动游戏（先显示账号选择界面）
    SwingUtilities.invokeLater(() -> {
        GameFrame gameFrame = new GameFrame();
        gameFrame.setVisible(true);

        // 显示账号选择界面
        gameFrame.showAccountSelectionScreen();
    });
}