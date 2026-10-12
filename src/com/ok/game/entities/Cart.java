package com.ok.game.entities;

import com.ok.resource.ResourceManager;
import com.ok.utils.Constants;

import java.awt.*;

/**
 * 小车实体类
 * 管理单个小车的状态、位置和行为
 */
public class Cart extends GameObject {

    // 小车状态
    public enum CartState {
        IDLE,       // 静止
        MOVING,     // 移动中
        TRIGGERED   // 已触发
    }

    private int row;
    private CartState state;
    private int speed;
    private int targetX;

    public Cart(int row) {
        super(Constants.CART_START_X, Constants.ROW_Y_POSITIONS[row],
                Constants.CART_WIDTH, Constants.CART_HEIGHT);
        this.row = row;
        this.state = CartState.IDLE;
        this.speed = Constants.CART_SPEED;
        this.targetX = Constants.CART_END_X;
        // 设置小车图片
        setImage(ResourceManager.getInstance().getCartImage());
    }

    @Override
    public void update(float deltaTime) {
        if (state == CartState.MOVING) {
            // 使用浮点数计算移动距离，避免精度丢失
            float moveDistance = speed * deltaTime;
            // 确保移动距离至少为1，避免卡住
            if (moveDistance < 1) {
                moveDistance = 3;
            }
            setX(getX() + (int)moveDistance);

            // 检查是否到达目标位置
            if (getX() >= targetX) {
                setX(targetX);
                state = CartState.TRIGGERED;
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (isVisible() && (state == CartState.IDLE || state == CartState.MOVING)) {
            renderImage(g);
        }
    }

    public void trigger() {
        if (state == CartState.IDLE) {
            state = CartState.MOVING;
        }
    }

    public boolean checkCollision(Zombie zombie) {
        if (state != CartState.MOVING || !zombie.isAlive()) return false;

        int zombieX = zombie.getX();
        int zombieWidth = zombie.getWidth();

        return zombie.getRow() == row &&
                getX() < zombieX + zombieWidth &&
                getX() + getWidth() > zombieX;
    }

    // Getter 方法
    public int getRow() { return row; }
    public CartState getState() { return state; }
    public boolean isActive() { return state == CartState.IDLE; }
    public boolean isMoving() { return state == CartState.MOVING; }
}