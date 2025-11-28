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

    // 빙결 상태
    var isFrozen: Boolean = false
    var freezeTimeRemaining: Float = 0f

    // 넉백 상태
    var isKnockback: Boolean = false
    var knockbackVelocityX: Float = 0f
    var knockbackVelocityY: Float = 0f
    var knockbackDistance: Float = 0f // 남은 넉백 거리
    var knockbackMaxDistance: Float = 0f // 최대 넉백 거리

    fun update(delta: Float, player: Player) {
        animationTime += delta
        lastAttackTime += delta

        // 빙결 상태 업데이트
        if (isFrozen) {
            freezeTimeRemaining -= delta
            if (freezeTimeRemaining <= 0f) {
                isFrozen = false
                freezeTimeRemaining = 0f
            }
        }

        // 넉백 상태 업데이트
        if (isKnockback) {
            val knockbackSpeed = 300f // 넉백 속도 (픽셀/초)
            val moveDistance = knockbackSpeed * delta

            if (knockbackDistance > 0f) {
                val actualMove = minOf(moveDistance, knockbackDistance)
                x += knockbackVelocityX * actualMove
                y += knockbackVelocityY * actualMove
                knockbackDistance -= actualMove
            } else {
                isKnockback = false
                knockbackVelocityX = 0f
                knockbackVelocityY = 0f
            }
        }

        val dx = player.x - x
        val dy = player.y - y
        val dist = sqrt(dx * dx + dy * dy)

        // 빙결이나 넉백 상태가 아닐 때만 이동
        if (!isFrozen && !isKnockback && dist > stopDistance) {
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

    /**
     * 빙결 효과 적용
     */
    fun applyFreeze(duration: Float) {
        isFrozen = true
        freezeTimeRemaining = duration
    }

    /**
     * 넉백 효과 적용
     * @param sourceX 넉백 원점 X (플레이어 위치)
     * @param sourceY 넉백 원점 Y (플레이어 위치)
     * @param knockbackDistance 넉백 거리
     * @param maxDistance 최대 밀려날 수 있는 거리 (사정거리 등)
     */
    fun applyKnockback(sourceX: Float, sourceY: Float, knockbackDistance: Float, maxDistance: Float) {
        // 넉백 방향 계산 (플레이어 -> 적)
        val dx = x - sourceX
        val dy = y - sourceY
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 0f) {
            // 정규화된 방향 벡터
            val dirX = dx / distance
            val dirY = dy / distance

            // 실제 넉백 거리 (최대 거리 제한)
            val actualKnockbackDistance = minOf(knockbackDistance, maxDistance - distance).coerceAtLeast(0f)

            if (actualKnockbackDistance > 0f) {
                isKnockback = true
                knockbackVelocityX = dirX
                knockbackVelocityY = dirY
                this.knockbackDistance = actualKnockbackDistance
                this.knockbackMaxDistance = maxDistance
            }
        }
    }

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
