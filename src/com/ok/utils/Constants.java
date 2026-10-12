package com.ok.utils;

/**
 * 游戏常量配置类
 * 只存放全局通用的配置参数
 * 植物/僵尸的特有属性（伤害、生命值、阳光消耗等）不放在这里，放在各自的类中
 */
public class Constants {

    // ==================== 窗口配置 ====================
    /** 窗口宽度 */
    public static final int WINDOW_WIDTH = 1000;

    /** 窗口高度 */
    public static final int WINDOW_HEIGHT = 700;

    // ==================== 网格配置 ====================
    /** 网格行数（5行草坪） */
    public static final int GRID_ROWS = 5;

    /** 网格列数 */
    public static final int GRID_COLS = 9;

    /** 格子宽度（像素） */
    public static final int GRID_WIDTH = 100;

    /** 格子高度（像素） */
    public static final int GRID_HEIGHT = 100;

    /** 网格起始X坐标（相对于窗口左侧） */
    public static final int GRID_OFFSET_X = 200;

    /** 网格起始Y坐标（相对于窗口顶部） */
    public static final int GRID_OFFSET_Y = 150;

    // ==================== 阳光系统全局参数 ====================
    /** 初始阳光数量 */
    public static final int START_SUN = 500;

    /** 阳光产生数量（向日葵生产和天上掉落） */
    public static final int SUN_PRODUCE_AMOUNT = 25;

    /** 向日葵产生阳光间隔（毫秒） */
    public static final int SUNFLOWER_PRODUCE_INTERVAL = 8000;

    /** 阳光存在时间（秒），超时后消失 */
    public static final float SUN_LIFESPAN = 10.0f;

    /** 阳光掉落速度（像素/秒） */
    public static final int SUN_FALL_SPEED = 100;

    /** 铲除植物返还阳光比例（返还阳光消耗的百分比，50表示返还50%） */
    public static final int SHOVEL_REFUND_RATIO = 50;

    /** 随机阳光生成间隔（毫秒） */
    public static final int FIRST_SUN_INTERVAL = 5000;
    public static final int RANDOM_SUN_INTERVAL = 15000;

    /** 阳光生成的X坐标范围（从网格左边界到右边界） */
    public static final int SUN_SPAWN_X_MIN = 200;
    public static final int SUN_SPAWN_X_MAX = 900;

    /** 阳光生成的起始Y坐标（屏幕上方） */
    public static final int SUN_SPAWN_Y_START = -1;

    /** 阳光掉落的目标Y坐标范围（草坪区域） */
    public static final int SUN_TARGET_Y_MIN = 150;
    public static final int SUN_TARGET_Y_MAX = 600;

    // ==================== 帧率配置 ====================
    /** 目标帧率 */
    public static final int TARGET_FPS = 60;

    /** 每帧时间（毫秒） */
    public static final int FRAME_TIME = 1000 / TARGET_FPS;

    // ==================== 游戏流程配置 ====================

    /** 波次内僵尸生成间隔（毫秒） */
    public static final int ZOMBIE_SPAWN_INTERVAL = 3000;

    /** 波次间隔时间（毫秒） */
    public static final int WAVE_INTERVAL = 5000;

    // ==================== 子弹系统全局参数 ====================
    /** 子弹速度（像素/秒） */
    public static final int BULLET_SPEED = 300;

    /** 子弹宽度（像素） */
    public static final int BULLET_WIDTH = 20;

    /** 子弹高度（像素） */
    public static final int BULLET_HEIGHT = 20;

    // ==================== 僵尸系统全局参数 ====================
    /** 僵尸移动速度（像素/秒） */
    public static final int ZOMBIE_WALK_SPEED = 2;

    /** 僵尸攻击间隔（毫秒） */
    public static final int ZOMBIE_ATTACK_INTERVAL = 1000;

    /** 僵尸宽度（像素） */
    public static final int ZOMBIE_WIDTH = 80;

    /** 僵尸高度（像素） */
    public static final int ZOMBIE_HEIGHT = 100;

    // ==================== 植物系统全局参数 ====================
    /** 植物默认生命值（如果植物类没有指定，使用此值） */
    public static final int PLANT_DEFAULT_HEALTH = 100;

    /** 植物默认攻击冷却时间（毫秒） */
    public static final int PLANT_ATTACK_COOLDOWN = 1500;

    /** 植物攻击范围（像素），Integer.MAX_VALUE表示全行 */
    public static final int PLANT_ATTACK_RANGE = Integer.MAX_VALUE;

    /** 植物宽度（像素） */
    public static final int PLANT_WIDTH = 80;

    /** 植物高度（像素） */
    public static final int PLANT_HEIGHT = 80;

    // ==================== 小车系统配置 ====================
    // 小车相关常量
    public static final int CART_START_X = 35; // 小车初始位置
    public static final int CART_END_X = 1100; // 小车结束位置（屏幕外右侧）
    public static final int CART_SPEED = 200; // 小车移动速度（像素/秒）
    public static final int CART_WIDTH = 80; // 小车宽度
    public static final int CART_HEIGHT = 80; // 小车高度
    public static final int CART_TRIGGER_X = 100; // 触发小车的X位置
    public static final int[] ROW_Y_POSITIONS = {150, 250, 350, 450, 550}; // 各行Y位置

    // ==================== 账号系统配置 ====================
    /** 最大账号数量 */
    public static final int MAX_ACCOUNT_COUNT = 5;

    /** 默认账号名称前缀 */
    public static final String DEFAULT_ACCOUNT_NAME_PREFIX = "玩家";

    // ==================== 拼图系统配置 ====================
    /** 拼图行数 */
    public static final int PUZZLE_ROWS = 2;

    /** 拼图列数 */
    public static final int PUZZLE_COLS = 2;

    /** 拼图块宽度（像素） */
    public static final int PUZZLE_PIECE_WIDTH = 150;

    /** 拼图块高度（像素） */
    public static final int PUZZLE_PIECE_HEIGHT = 150;


    // ==================== UI布局配置 ====================

    /** 左下角头像位置X */
    public static final int AVATAR_X = 20;

    /** 左下角头像位置Y */
    public static final int AVATAR_Y = WINDOW_HEIGHT - 80;

    /** 头像大小 */
    public static final int AVATAR_SIZE = 60;

    // ==================== 字体配置 ====================
    /** 标题字体大小 */
    public static final int TITLE_FONT_SIZE = 48;

    // ==================== 音效配置 ====================
    /** 音效开关默认状态 */
    public static final boolean DEFAULT_SOUND_ENABLED = true;

    /** 音乐开关默认状态 */
    public static final boolean DEFAULT_MUSIC_ENABLED = true;

    /** 默认音效音量（0-100） */
    public static final int DEFAULT_SOUND_VOLUME = 80;

    /** 默认音乐音量（0-100） */
    public static final int DEFAULT_MUSIC_VOLUME = 70;

    // ==================== 动画配置 ====================
    /** 僵尸死亡动画持续时间（毫秒） */
    public static final int ZOMBIE_DEATH_ANIMATION = 500;

    // ==================== 路径配置 ====================
    /** 图片资源根路径 */
    public static final String IMAGE_PATH = "images/";

    /** 音效资源根路径 */
    public static final String SOUND_PATH = "/sounds/";

    /** 配置文件根路径 */
    public static final String CONFIG_PATH = "/config/";

    /** 存档文件路径（相对于程序运行目录） */
    public static final String SAVE_FILE_PATH = "saves/save_data.json";

    /** 多账号存档根目录 */
    public static final String ACCOUNTS_SAVE_DIR = "saves/accounts/";

    /** 账号列表文件 */
    public static final String ACCOUNTS_LIST_FILE = "saves/accounts_list.json";

    // ==================== 无限模式配置 ====================
    /** 无限模式初始难度系数 */
    public static final int ENDLESS_START_DIFFICULTY = 1;

    /** 每波次难度增加系数 */
    public static final double ENDLESS_DIFFICULTY_INCREMENT = 0.1;

    /** 无限模式僵尸最大数量限制（防止过多僵尸卡顿） */
    public static final int ENDLESS_MAX_ZOMBIES = 50;
    public static final int HOUSE_POSITION = 10;

    // ==================== 私有构造函数 ====================
    /** 私有构造函数，防止实例化 */
    private Constants() {
        // 工具类不应被实例化
    }
}