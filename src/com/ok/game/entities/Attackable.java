package com.ok.game.entities;

/**
 * 可攻击接口
 * 实现此接口的对象可以攻击僵尸
 */
public interface Attackable {
    /**
     * 攻击僵尸
     * @param zombie 被攻击的僵尸
     * @return 是否应该移除攻击者
     */
    boolean onHit(Zombie zombie);
}