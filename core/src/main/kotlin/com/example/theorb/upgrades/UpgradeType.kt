package com.example.theorb.upgrades

import kotlin.math.pow

enum class UpgradeType(
    val displayName: String,
    val description: String,
    val baseCost: Int,
    val costMultiplier: Float = 1.15f,
    val baseIncrease: Float,
    val maxLevel: Int = 50
) {
    DAMAGE("데미지", "기본 공격력 증가", 10, 1.15f, 2f),
    RANGE("사정거리", "공격 사정거리 증가", 15, 1.5f, 0.05f, 14), // 5%씩 증가, 최대 70% (14레벨)
    COOLDOWN_REDUCTION("쿨다운 감소", "스킬 쿨다운 시간 감소", 20, 1.7f, 0.03f, 25); // 3%씩 감소, 최대 75%

    fun getCostForLevel(level: Int): Int {
        if (level >= maxLevel) return Int.MAX_VALUE
        return (baseCost * costMultiplier.toDouble().pow(level.toDouble())).toInt()
    }

    fun getValueAtLevel(level: Int): Float {
        return baseIncrease * level
    }
}
