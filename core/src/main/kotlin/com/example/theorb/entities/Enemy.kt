package com.example.theorb.entities

import com.example.theorb.balance.EnemyType
import com.example.theorb.effects.EffectType
import kotlin.math.sqrt

/**
 * 적 속성 타입 (5가지 속성)
 */
enum class EnemyElement {
    FIRE,      // 화염
    COLD,      // 냉기
    LIGHTNING, // 번개
    ANGEL,     // 신성 (Divine)
    DEMON;     // 어둠 (Dark)

    companion object {
        fun random(): EnemyElement = values().random()
    }
}

class Enemy(
    val type: EnemyType,
    var hp: Int,
    var contactDamage: Int,
    var x: Float,
    var y: Float,
    val speed: Float = 60f,
    val rewardGold: Int = 1,
    val element: EnemyElement = EnemyElement.random() // 랜덤 속성 할당
) {
    val maxHp: Int = hp // 최대 체력 저장
    var vhp: Int = hp // 가상 hp (투사체 발사 시점에 즉시 감소)
    var animationTime: Float = 0f
    var lastAttackTime: Float = 0f // 마지막 공격 시간
    private val attackCooldown: Float = 2f // 공격 쿨다운 (2초)
    private val stopDistance: Float = 20f // 플레이어로부터 멈추는 거리

    fun update(delta: Float, player: Player) {
        animationTime += delta
        lastAttackTime += delta

        val dx = player.x - x
        val dy = player.y - y
        val dist = sqrt(dx * dx + dy * dy)

        // 일정 거리 이상일 때만 이동
        if (dist > stopDistance) {
            x += dx / dist * speed * delta
            y += dy / dist * speed * delta
        }
    }

    /**
     * 플레이어를 공격할 수 있는지 확인
     */
    fun canAttack(): Boolean {
        return lastAttackTime >= attackCooldown
    }

    /**
     * 공격 쿨다운 리셋
     */
    fun resetAttackCooldown() {
        lastAttackTime = 0f
    }

    fun isDead() = hp <= 0

    fun getDeathEffectType(): EffectType {
        // 속성에 맞는 죽음 이펙트 (DIE_02와 DIE_03 중 랜덤)
        val dieEffects = when (element) {
            EnemyElement.FIRE -> listOf(EffectType.ENEMY_DIE_02_FIRE, EffectType.ENEMY_DIE_03_FIRE)
            EnemyElement.COLD -> listOf(EffectType.ENEMY_DIE_02_COLD, EffectType.ENEMY_DIE_03_COLD)
            EnemyElement.LIGHTNING -> listOf(EffectType.ENEMY_DIE_02_LIGHTNING, EffectType.ENEMY_DIE_03_LIGHTNING)
            EnemyElement.ANGEL -> listOf(EffectType.ENEMY_DIE_02_ANGEL, EffectType.ENEMY_DIE_03_ANGEL)
            EnemyElement.DEMON -> listOf(EffectType.ENEMY_DIE_02_DEMON, EffectType.ENEMY_DIE_03_DEMON)
        }
        return dieEffects.random()
    }

    fun getSpriteEffectType(): EffectType = when (type) {
        EnemyType.NORMAL -> when (element) {
            EnemyElement.FIRE -> EffectType.ENEMY_NORMAL_FIRE
            EnemyElement.COLD -> EffectType.ENEMY_NORMAL_COLD
            EnemyElement.LIGHTNING -> EffectType.ENEMY_NORMAL_LIGHTNING
            EnemyElement.ANGEL -> EffectType.ENEMY_NORMAL_ANGEL
            EnemyElement.DEMON -> EffectType.ENEMY_NORMAL_DEMON
        }
        EnemyType.SPEED -> when (element) {
            EnemyElement.FIRE -> EffectType.ENEMY_SPEED_FIRE
            EnemyElement.COLD -> EffectType.ENEMY_SPEED_COLD
            EnemyElement.LIGHTNING -> EffectType.ENEMY_SPEED_LIGHTNING
            EnemyElement.ANGEL -> EffectType.ENEMY_SPEED_ANGEL
            EnemyElement.DEMON -> EffectType.ENEMY_SPEED_DEMON
        }
        EnemyType.TANK -> when (element) {
            EnemyElement.FIRE -> EffectType.ENEMY_TANK_FIRE
            EnemyElement.COLD -> EffectType.ENEMY_TANK_COLD
            EnemyElement.LIGHTNING -> EffectType.ENEMY_TANK_LIGHTNING
            EnemyElement.ANGEL -> EffectType.ENEMY_TANK_ANGEL
            EnemyElement.DEMON -> EffectType.ENEMY_TANK_DEMON
        }
        EnemyType.BOSS -> when (element) {
            EnemyElement.FIRE -> EffectType.ENEMY_BOSS_FIRE
            EnemyElement.COLD -> EffectType.ENEMY_BOSS_COLD
            EnemyElement.LIGHTNING -> EffectType.ENEMY_BOSS_LIGHTNING
            EnemyElement.ANGEL -> EffectType.ENEMY_BOSS_ANGEL
            EnemyElement.DEMON -> EffectType.ENEMY_BOSS_DEMON
        }
    }
}
