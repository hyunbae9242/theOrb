package com.example.theorb.entities

import com.example.theorb.balance.Element
import com.example.theorb.balance.EnemyType
import com.example.theorb.effects.EffectType
import kotlin.math.sqrt

class Enemy(
    val type: EnemyType,
    val element: Element,
    var hp: Int,
    var contactDamage: Int,
    var x: Float,
    var y: Float,
    val speed: Float = 60f,
    val rewardGold: Int = 1,
    private val resist: Map<Element, Float> // 0.0 ~ 0.75
) {
    val maxHp: Int = hp // 최대 체력 저장
    var vhp: Int = hp // 가상 hp (투사체 발사 시점에 즉시 감소)
    var animationTime: Float = 0f

    fun update(delta: Float, player: Player) {
        animationTime += delta

        val dx = player.x - x
        val dy = player.y - y
        val dist = sqrt(dx * dx + dy * dy)
        if (dist > 1f) {
            x += dx / dist * speed * delta
            y += dy / dist * speed * delta
        }
    }

    fun resistAgainst(element: Element): Float = resist[element] ?: 0f

    fun isDead() = hp <= 0

    fun getDeathEffectType(): EffectType {
        val availableDeathEffects = when (element) {
            Element.FIRE -> listOf(EffectType.ENEMY_DIE_02_FIRE, EffectType.ENEMY_DIE_03_FIRE)
            Element.COLD -> listOf(EffectType.ENEMY_DIE_02_COLD, EffectType.ENEMY_DIE_03_COLD)
            Element.LIGHTNING -> listOf(EffectType.ENEMY_DIE_02_LIGHTNING, EffectType.ENEMY_DIE_03_LIGHTNING)
            Element.ANGEL -> listOf(EffectType.ENEMY_DIE_02_ANGEL, EffectType.ENEMY_DIE_03_ANGEL)
            Element.DEMON -> listOf(EffectType.ENEMY_DIE_02_DEMON, EffectType.ENEMY_DIE_03_DEMON)
        }
        return availableDeathEffects.random()
    }

    fun getSpriteEffectType(): EffectType = when (type) {
        EnemyType.NORMAL -> when (element) {
            Element.FIRE -> EffectType.ENEMY_NORMAL_FIRE
            Element.COLD -> EffectType.ENEMY_NORMAL_COLD
            Element.LIGHTNING -> EffectType.ENEMY_NORMAL_LIGHTNING
            Element.ANGEL -> EffectType.ENEMY_NORMAL_ANGEL
            Element.DEMON -> EffectType.ENEMY_NORMAL_DEMON
        }
        EnemyType.SPEED -> when (element) {
            Element.FIRE -> EffectType.ENEMY_SPEED_FIRE
            Element.COLD -> EffectType.ENEMY_SPEED_COLD
            Element.LIGHTNING -> EffectType.ENEMY_SPEED_LIGHTNING
            Element.ANGEL -> EffectType.ENEMY_SPEED_ANGEL
            Element.DEMON -> EffectType.ENEMY_SPEED_DEMON
        }
        EnemyType.TANK -> when (element) {
            Element.FIRE -> EffectType.ENEMY_TANK_FIRE
            Element.COLD -> EffectType.ENEMY_TANK_COLD
            Element.LIGHTNING -> EffectType.ENEMY_TANK_LIGHTNING
            Element.ANGEL -> EffectType.ENEMY_TANK_ANGEL
            Element.DEMON -> EffectType.ENEMY_TANK_DEMON
        }
        EnemyType.BOSS -> when (element) {
            Element.FIRE -> EffectType.ENEMY_BOSS_FIRE
            Element.COLD -> EffectType.ENEMY_BOSS_COLD
            Element.LIGHTNING -> EffectType.ENEMY_BOSS_LIGHTNING
            Element.ANGEL -> EffectType.ENEMY_BOSS_ANGEL
            Element.DEMON -> EffectType.ENEMY_BOSS_DEMON
        }
    }
}
