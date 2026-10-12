package com.ok.game.entities;

/**
 * 可被攻击接口
 * 实现此接口的对象可以受到伤害
 */
public interface Damageable {
    /**
     * 受到伤害
     * @param damage 伤害值
     */
    void takeDamage(int damage);
}