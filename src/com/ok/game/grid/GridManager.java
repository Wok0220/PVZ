package com.ok.game.grid;

import com.ok.game.core.GameManager;
import com.ok.game.entities.Plant;
import com.ok.utils.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 网格管理器
 * 管理植物种植网格（5行 x 9列）
 * 负责坐标转换、植物种植、铲除、查询等操作
 */
public class GridManager {

    /** 植物网格（行 x 列） */
    private Plant[][] grid;

    /** 网格行数 */
    private int rows;

    /** 网格列数 */
    private int cols;

    /** 格子宽度（像素） */
    private int cellWidth;

    /** 格子高度（像素） */
    private int cellHeight;

    /** 网格起始X坐标（像素） */
    private int offsetX;

    /** 网格起始Y坐标（像素） */
    private int offsetY;

    private GameManager gameManager;

    public void init(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    /**
     * 构造函数
     */
    public GridManager() {
        this.rows = Constants.GRID_ROWS;
        this.cols = Constants.GRID_COLS;
        this.cellWidth = Constants.GRID_WIDTH - 8;
        this.cellHeight = Constants.GRID_HEIGHT + 9;
        this.offsetX = Constants.GRID_OFFSET_X - 80;
        this.offsetY = Constants.GRID_OFFSET_Y - 60;
        this.grid = new Plant[rows][cols];
    }

    // ==================== 坐标转换 ====================

    /**
     * 屏幕坐标转网格行
     * @param screenY 屏幕Y坐标
     * @return 网格行（0-4），-1表示不在网格内
     */
    public int screenToRow(int screenY) {
        if (screenY < offsetY || screenY > offsetY + rows * cellHeight) {
            return -1;
        }
        return (screenY - offsetY) / cellHeight;
    }

    /**
     * 屏幕坐标转网格列
     * @param screenX 屏幕X坐标
     * @return 网格列（0-8），-1表示不在网格内
     */
    public int screenToCol(int screenX) {
        if (screenX < offsetX || screenX > offsetX + cols * cellWidth) {
            return -1;
        }
        return (screenX - offsetX) / cellWidth;
    }

    /**
     * 屏幕坐标转网格位置
     * @param screenX 屏幕X坐标
     * @param screenY 屏幕Y坐标
     * @return Point(x=行, y=列)，如果不在网格内返回null
     */
    public Point screenToGrid(int screenX, int screenY) {
        int row = screenToRow(screenY);
        int col = screenToCol(screenX);
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return new Point(row, col);
        }
        return null;
    }

    /**
     * 网格坐标转屏幕X坐标（格子左上角）
     * @param col 网格列
     * @return 屏幕X坐标
     */
    public int gridToScreenX(int col) {
        return offsetX + col * cellWidth;
    }

    /**
     * 网格坐标转屏幕Y坐标（格子左上角）
     * @param row 网格行
     * @return 屏幕Y坐标
     */
    public int gridToScreenY(int row) {
        return offsetY + row * cellHeight;
    }

    /**
     * 获取格子的矩形区域
     * @param row 网格行
     * @param col 网格列
     * @return 格子矩形
     */
    public Rectangle getCellBounds(int row, int col) {
        return new Rectangle(
                gridToScreenX(col),
                gridToScreenY(row),
                cellWidth,
                cellHeight
        );
    }

    // ==================== 植物操作 ====================

    /**
     * 种植植物
     * @param plant 植物对象
     * @param row 网格行
     * @param col 网格列
     * @return 是否种植成功
     */
    public boolean plant(Plant plant, int row, int col) {
        // 检查边界
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        // 检查格子是否为空
        if (grid[row][col] != null) {
            return false;
        }

        // 设置植物的行列
        plant.setRow(row);
        plant.setCol(col);

        // 设置植物的屏幕位置
        plant.setPosition(gridToScreenX(col), gridToScreenY(row));

        // 放入网格
        grid[row][col] = plant;

        // 新增：设置 gameManager 引用
        plant.setGameManager(gameManager);

        return true;
    }

    /**
     * 铲除植物
     * @param row 网格行
     * @param col 网格列
     * @return 被铲除的植物，如果格子为空返回null
     */
    public Plant removePlant(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }

        Plant plant = grid[row][col];
        if (plant != null) {
            grid[row][col] = null;
        }
        return plant;
    }

    /**
     * 铲除植物（通过屏幕坐标）
     * @param screenX 屏幕X坐标
     * @param screenY 屏幕Y坐标
     * @return 被铲除的植物，如果格子为空返回null
     */
    public Plant removePlantAt(int screenX, int screenY) {
        Point gridPos = screenToGrid(screenX, screenY);
        if (gridPos != null) {
            return removePlant(gridPos.x, gridPos.y);
        }
        return null;
    }

    /**
     * 获取指定位置的植物
     * @param row 网格行
     * @param col 网格列
     * @return 植物对象，如果没有返回null
     */
    public Plant getPlant(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return grid[row][col];
    }

    /**
     * 获取指定位置的植物（通过屏幕坐标）
     * @param screenX 屏幕X坐标
     * @param screenY 屏幕Y坐标
     * @return 植物对象，如果没有返回null
     */
    public Plant getPlantAt(int screenX, int screenY) {
        Point gridPos = screenToGrid(screenX, screenY);
        if (gridPos != null) {
            return getPlant(gridPos.x, gridPos.y);
        }
        return null;
    }

    /**
     * 检查指定格子是否有植物
     * @param row 网格行
     * @param col 网格列
     * @return 是否有植物
     */
    public boolean hasPlant(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        return grid[row][col] != null;
    }

    /**
     * 获取指定行的所有植物
     * @param row 网格行
     * @return 该行的植物列表（按列排序）
     */
    public Plant[] getPlantsInRow(int row) {
        if (row < 0 || row >= rows) {
            return new Plant[0];
        }
        return grid[row].clone();
    }

    /**
     * 获取指定列的所有植物
     * @param col 网格列
     * @return 该列的植物列表（按行排序）
     */
    public Plant[] getPlantsInCol(int col) {
        if (col < 0 || col >= cols) {
            return new Plant[0];
        }
        Plant[] plants = new Plant[rows];
        for (int i = 0; i < rows; i++) {
            plants[i] = grid[i][col];
        }
        return plants;
    }

    /**
     * 获取所有植物
     * @return 所有植物的列表（不包括空位）
     */
    public List<Plant> getAllPlants() {
        List<Plant> plants = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != null) {
                    plants.add(grid[i][j]);
                }
            }
        }
        return plants;
    }

    // ==================== 前方植物查询 ====================

    /**
     * 获取指定位置前方的第一个植物
     * 用于僵尸碰撞检测
     * @param row 僵尸所在行
     * @param zombieX 僵尸X坐标（像素）
     * @return 前方最近的植物，如果没有返回null
     */
    public Plant getPlantInFront(int row, int zombieX) {
        if (row < 0 || row >= rows) {
            return null;
        }

        // 找到僵尸当前所在的列（基于X坐标）
        int currentCol = (zombieX - offsetX) / cellWidth;
        if (currentCol < 0) {
            currentCol = 0;
        }
        if (currentCol >= cols) {
            currentCol = cols - 1;
        }

        // 从当前列开始向左查找第一个植物（僵尸从右向左移动）
        for (int col = currentCol; col >= 0; col--) {
            if (grid[row][col] != null) {
                return grid[row][col];
            }
        }

        return null;
    }

    /**
     * 获取指定列右侧第一个植物
     * @param row 行
     * @param startCol 起始列
     * @return 右侧第一个植物，如果没有返回null
     */
    public Plant getPlantToTheRight(int row, int startCol) {
        if (row < 0 || row >= rows) {
            return null;
        }

        for (int col = startCol + 1; col < cols; col++) {
            if (grid[row][col] != null) {
                return grid[row][col];
            }
        }

        return null;
    }

    /**
     * 获取指定列左侧第一个植物
     * @param row 行
     * @param startCol 起始列
     * @return 左侧第一个植物，如果没有返回null
     */
    public Plant getPlantToTheLeft(int row, int startCol) {
        if (row < 0 || row >= rows) {
            return null;
        }

        for (int col = startCol - 1; col >= 0; col--) {
            if (grid[row][col] != null) {
                return grid[row][col];
            }
        }

        return null;
    }

    // ==================== 网格操作 ====================

    /**
     * 清空所有植物
     */
    public void clear() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != null) {
                    grid[i][j].die();
                    grid[i][j] = null;
                }
            }
        }
    }

    /**
     * 获取网格行数
     */
    public int getRows() {
        return rows;
    }

    /**
     * 获取网格列数
     */
    public int getCols() {
        return cols;
    }

    /**
     * 获取格子宽度
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * 获取格子高度
     */
    public int getCellHeight() {
        return cellHeight;
    }

    /**
     * 获取网格起始X坐标
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * 获取网格起始Y坐标
     */
    public int getOffsetY() {
        return offsetY;
    }
}