package com.ok.game.systems;

import com.ok.game.entities.Cart;
import com.ok.game.entities.Zombie;
import com.ok.game.grid.GridManager;
import com.ok.utils.Constants;

import java.awt.*;
import java.util.List;

/**
 * 小车系统
 * 管理游戏中的所有小车
 */
public class CartSystem {

    private static CartSystem instance;
    private GridManager gridManager;
    private Cart[] carts;

    private CartSystem() {
        this.carts = new Cart[Constants.GRID_ROWS];
        reset();
    }

    public static CartSystem getInstance() {
        if (instance == null) {
            instance = new CartSystem();
        }
        return instance;
    }

    public void init(GridManager gridManager) {
        this.gridManager = gridManager;
        reset();
    }

    public void reset() {
        for (int i = 0; i < Constants.GRID_ROWS; i++) {
            carts[i] = new Cart(i);
        }
    }

    public void update(float deltaTime, List<Zombie> zombies) {
        // 1. 检查是否需要触发小车
        for (int row = 0; row < Constants.GRID_ROWS; row++) {
            Cart cart = carts[row];
            if (cart.isActive()) {
                // 检查是否有僵尸进入触发区域
                boolean shouldTrigger = false;
                for (Zombie zombie : zombies) {
                    if (zombie.getRow() == row && zombie.isAlive() && zombie.getX() < Constants.CART_TRIGGER_X) {
                        shouldTrigger = true;
                        break;
                    }
                }
                if (shouldTrigger) {
                    cart.trigger();
                }
            }
        }

        // 2. 更新小车位置
        for (Cart cart : carts) {
            if (cart.isMoving()) {
                cart.update(deltaTime);

                // 3. 检查碰撞
                for (Zombie zombie : zombies) {
                    if (cart.checkCollision(zombie)) {
                        zombie.die();
                    }
                }
            } else {
                cart.update(deltaTime);
            }
        }
    }

    private boolean shouldTriggerCart(int row, List<Zombie> zombies) {
        for (Zombie zombie : zombies) {
            if (zombie.getRow() == row && zombie.getX() < ( Constants.CART_TRIGGER_X - 50 )) {
                return true;
            }
        }
        return false;
    }

    public void triggerCart(int row, List<Zombie> zombies) {
        Cart cart = carts[row];
        if (cart.isActive()) {
            cart.trigger();
        }
    }

    public int getRemainingCartCount() {
        int count = 0;
        for (Cart cart : carts) {
            if (cart.isActive()) {
                count++;
            }
        }
        return count;
    }

    public boolean isCartActive(int row) {
        if (row >= 0 && row < Constants.GRID_ROWS) {
            return carts[row].isActive();
        }
        return false;
    }

    public Cart getCart(int row) {
        if (row >= 0 && row < Constants.GRID_ROWS) {
            return carts[row];
        }
        return null;
    }

    // 新增渲染方法，用于在GameManager中调用
    public void render(Graphics2D g) {
        for (Cart cart : carts) {
            cart.render(g);
        }
    }
}