package com.example.theorb.balance

import com.badlogic.gdx.graphics.Color


enum class EnemyType(
    val color: Color,
    val radius: Float,
) {
    NORMAL(Color.RED, 15f),
    SPEED(Color.YELLOW, 12f),
    TANK(Color.GREEN, 18f),
    BOSS(Color.PURPLE, 20f)
}
enum class Element { FIRE, COLD, LIGHTNING, ANGEL, DEMON }
enum class DamageType { PROJECTILE } // 추후 근접/지속도 추가 가능

object Balance {
    // === 저항 관련 ===
    const val MAX_RESIST = 0.75f
    const val BASE_RESIST = 0.10f      // 기본 저항력
    const val MATCH_RESIST = 0.25f     // 해당 속성 매칭 저항력

    // === 베이스 스탯 === (원래 쓰던 숫자와 매칭되도록 설정)
    const val BASE_HP = 20
    const val BASE_SPEED = 40f
    const val BASE_CONTACT_DAMAGE = 5
    const val BASE_REWARD_GOLD = 1

    // 타입별 배수
    data class TypeMultipliers(
        val hpMul: Float = 1f,
        val speedMul: Float = 1f,
        val dmgMul: Float = 1f,
        val goldMul: Float = 1f
    )

    val TYPE_MULTIPLIERS: Map<EnemyType, TypeMultipliers> = mapOf(
        EnemyType.NORMAL to TypeMultipliers(1f, 1f, 1f, 1f),
        EnemyType.SPEED  to TypeMultipliers(0.7f, 1.8f, 1f, 1.3f),
        EnemyType.TANK   to TypeMultipliers(3f, 0.8f, 1f, 2f),
        EnemyType.BOSS   to TypeMultipliers(7f, 0.5f, 3f, 5f),
    )

    // 타입 스폰 가중치 (보스 포함)
    val TYPE_WEIGHTS: Map<EnemyType, Int> = mapOf(
        EnemyType.NORMAL to 80,
        EnemyType.SPEED  to 15,
        EnemyType.TANK   to 4,
        EnemyType.BOSS   to 1
    )

    // 일반 스폰용 가중치 (보스 제외)
    val TYPE_WEIGHTS_NO_BOSS: Map<EnemyType, Int> = mapOf(
        EnemyType.NORMAL to 80,
        EnemyType.SPEED  to 15,
        EnemyType.TANK   to 45
    )

    // 속성 스폰 가중치 (엔젤/데몬 낮게)
    val ELEMENT_WEIGHTS: Map<Element, Int> = mapOf(
        Element.FIRE to 30,
        Element.COLD to 30,
        Element.LIGHTNING to 30,
        Element.ANGEL to 5,
        Element.DEMON to 5
    )
}
