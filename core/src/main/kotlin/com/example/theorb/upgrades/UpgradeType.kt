package com.example.theorb.upgrades

import kotlin.math.pow

enum class UpgradeCategory(val displayName: String) {
    ATTACK("공격"),
    DEFENSE("방어"),
    UTILITY("유틸")
}

enum class UpgradeType(
    val displayName: String,
    val description: String,
    val baseCost: Int,
    val costMultiplier: Float = 1.15f,
    val baseIncrease: Float,
    val maxLevel: Int = 50,
    val category: UpgradeCategory
) {
    // 공격 업그레이드
    DAMAGE("데미지", "기본 공격력 증가", 10, 1.15f, 2f, 50, UpgradeCategory.ATTACK),
    CRITICAL_CHANCE("치명타 확률", "치명타 발생 확률 증가", 25, 1.8f, 1f, 30, UpgradeCategory.ATTACK),
    CRITICAL_DAMAGE("치명타 데미지", "치명타 데미지 배율 증가", 30, 1.6f, 3f, 25, UpgradeCategory.ATTACK),

    // 방어 업그레이드
    HEALTH("체력", "최대 체력 증가", 15, 1.4f, 20f, 40, UpgradeCategory.DEFENSE),
    ARMOR("방어력", "받는 데미지 감소", 20, 1.7f, 1f, 20, UpgradeCategory.DEFENSE),
    REGENERATION("체력 재생", "시간당 체력 회복량 증가", 25, 1.5f, 2f, 35, UpgradeCategory.DEFENSE),

    // 유틸 업그레이드
    RANGE("사정거리", "공격 사정거리 증가", 15, 1.5f, 0.05f, 14, UpgradeCategory.UTILITY), // 5%씩 증가, 최대 70% (14레벨)
    COOLDOWN_REDUCTION("쿨다운 감소", "스킬 쿨다운 시간 감소", 20, 1.7f, 0.03f, 25, UpgradeCategory.UTILITY), // 3%씩 감소, 최대 75%
    MOVEMENT_SPEED("이동속도", "플레이어 이동속도 증가", 18, 1.3f, 0.02f, 30, UpgradeCategory.UTILITY),
    GOLD_BONUS("골드 보너스", "획득 골드량 증가", 22, 1.4f, 0.05f, 20, UpgradeCategory.UTILITY);

    fun getCostForLevel(level: Int): Int {
        if (level >= maxLevel) return Int.MAX_VALUE
        return (baseCost * costMultiplier.toDouble().pow(level.toDouble())).toInt()
    }

    fun getValueAtLevel(level: Int): Float {
        return baseIncrease * level
    }
}
