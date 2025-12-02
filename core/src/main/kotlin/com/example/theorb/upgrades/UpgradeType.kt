package com.example.theorb.upgrades

import kotlin.math.pow

enum class UpgradeCategory(val displayName: String) {
    ATTACK("ATTACK"),
    DEFENSE("DEFENSE"),
    UTILITY("UTILITY")
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
    DAMAGE("데미지", "기본 공격력 증가", 10, 1.3f, 5f, 50, UpgradeCategory.ATTACK),
    CRITICAL_CHANCE("치명타 확률", "치명타 발생 확률 증가", 50, 1.8f, 1f, 30, UpgradeCategory.ATTACK),
    CRITICAL_DAMAGE("치명타 데미지", "치명타 데미지 배율 증가", 50, 1.6f, 3f, 25, UpgradeCategory.ATTACK),

    // 방어 업그레이드
    HEALTH("체력", "최대 체력 증가", 15, 1.4f, 20f, 40, UpgradeCategory.DEFENSE),
    ARMOR("방어력", "받는 데미지를 고정 수치만큼 감소", 20, 1.4f, 1f, 50, UpgradeCategory.DEFENSE),
    ARMOR_PERCENTAGE("방어율", "받는 데미지를 %만큼 감소", 5000, 1.4f, 1f, 50, UpgradeCategory.DEFENSE),

    // 유틸 업그레이드
    RANGE("사정거리", "공격 사정거리 증가", 15, 1.5f, 0.05f, 14, UpgradeCategory.UTILITY), // 5%씩 증가, 최대 70% (14레벨)
    COOLDOWN_REDUCTION("쿨다운 감소", "스킬 쿨다운 시간 감소", 20, 1.7f, 0.03f, 25, UpgradeCategory.UTILITY), // 3%씩 감소, 최대 75%
    GOLD_BONUS("골드 보너스", "획득 골드량 증가", 22, 1.4f, 0.05f, 20, UpgradeCategory.UTILITY),
    REROLL_COUNT("리롤 기회", "레벨업 선택지 리롤 횟수 증가 (최대 5회)", 100, 2.0f, 1f, 5, UpgradeCategory.UTILITY), // 매우 비싸게, 최대 5레벨
    RARITY_BONUS("선택지 희귀도", "레벨업 선택지의 레어/유니크 확률 증가", 3000, 2.0f, 1f, 10, UpgradeCategory.UTILITY); // 초기 3000골드, 2배씩 증가, 10레벨 max

    fun getCostForLevel(level: Int): Int {
        if (level >= maxLevel) return Int.MAX_VALUE
        return (baseCost * costMultiplier.toDouble().pow(level.toDouble())).toInt()
    }

    fun getValueAtLevel(level: Int): Float {
        // 방어력은 특별한 성장 곡선 사용
        if (this == ARMOR) {
            return getArmorValueAtLevel(level)
        }
        // 방어율은 특별한 성장 곡선 사용
        if (this == ARMOR_PERCENTAGE) {
            return getArmorPercentageValueAtLevel(level)
        }
        // 희귀도 보너스는 특별한 성장 곡선 사용
        if (this == RARITY_BONUS) {
            return getRarityBonusValueAtLevel(level)
        }
        return baseIncrease * level
    }

    /**
     * 방어력 성장 곡선
     * 1~10: +1씩 (총 10)
     * 11~20: +2씩 (총 20)
     * 21~35: +3씩 (총 45)
     * 36~50: +5씩 (총 75)
     * 최종 50레벨: 150 방어력
     */
    private fun getArmorValueAtLevel(level: Int): Float {
        return when (level) {
            in 1..10 -> level.toFloat()                           // 1~10
            in 11..20 -> 10f + (level - 10) * 2f                  // 10 + 20
            in 21..35 -> 30f + (level - 20) * 3f                  // 30 + 45
            in 36..50 -> 75f + (level - 35) * 5f                  // 75 + 75
            else -> 0f
        }
    }

    /**
     * 방어율 성장 곡선 (초반 적게, 후반 많이)
     * 1~15: +0.5%씩 (총 7.5%)
     * 16~30: +1%씩 (총 15%)
     * 31~40: +2%씩 (총 20%)
     * 41~50: +3.25%씩 (총 32.5%)
     * 최종 50레벨: 75%
     */
    private fun getArmorPercentageValueAtLevel(level: Int): Float {
        return when (level) {
            in 1..15 -> level * 0.5f                              // 0.5~7.5
            in 16..30 -> 7.5f + (level - 15) * 1f                 // 7.5 + 15
            in 31..40 -> 22.5f + (level - 30) * 2f                // 22.5 + 20
            in 41..50 -> 42.5f + (level - 40) * 3.25f             // 42.5 + 32.5
            else -> 0f
        }
    }

    /**
     * 희귀도 보너스 성장 곡선
     * 레벨당 유니크 +0.5%, 레어 +1%씩 증가
     * 이 함수는 내부적으로는 사용되지 않고, getRarityBonusUnique/Rare 함수에서 직접 계산
     */
    private fun getRarityBonusValueAtLevel(level: Int): Float {
        return level.toFloat() // 더미 값, 실제로는 아래 함수들 사용
    }

    /**
     * 희귀도 보너스 - 유니크 확률 증가 (레벨당 +0.5%)
     */
    fun getRarityBonusUnique(level: Int): Float {
        return level * 0.5f
    }

    /**
     * 희귀도 보너스 - 레어 확률 증가 (레벨당 +1%)
     */
    fun getRarityBonusRare(level: Int): Float {
        return level * 1f
    }
}
