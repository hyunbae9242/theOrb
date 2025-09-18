package com.example.theorb.balance

import kotlin.math.pow

object InGameUpgrades {
    // 업그레이드 타입 정의
    enum class UpgradeType {
        // 공격 탭
        DAMAGE_INCREASE,
        COOLDOWN_REDUCTION,
        CRITICAL_CHANCE,
        CRITICAL_DAMAGE,

        // 방어 탭 (미구현)

        // 유틸 탭
        GOLD_INCREASE,
        SILVER_INCREASE,
        GEM_INCREASE,
        ENEMY_SPAWN_SPEED,
        ENEMY_SPAWN_COUNT
    }

    // 업그레이드 정보
    data class UpgradeInfo(
        val name: String,
        val description: String,
        val maxLevel: Int,
        val baseCost: Int,
        val costMultiplier: Float,
        val bonusPerLevel: Float,
        val maxBonus: Float,
        val tab: UpgradeTab
    )

    enum class UpgradeTab {
        ATTACK, DEFENSE, UTILITY
    }

    // 업그레이드 정의
    val UPGRADE_DATA = mapOf(
        // 공격 탭
        UpgradeType.DAMAGE_INCREASE to UpgradeInfo(
            name = "데미지 증가",
            description = "모든 데미지가 %만큼 증가합니다",
            maxLevel = 20,
            baseCost = 10,
            costMultiplier = 1.15f,
            bonusPerLevel = 10f, // +10% per level
            maxBonus = 200f, // +200% max
            tab = UpgradeTab.ATTACK
        ),

        UpgradeType.COOLDOWN_REDUCTION to UpgradeInfo(
            name = "쿨다운 감소",
            description = "스킬 쿨다운이 %만큼 감소합니다",
            maxLevel = 10,
            baseCost = 15,
            costMultiplier = 1.2f,
            bonusPerLevel = 5f, // +5% per level
            maxBonus = 50f, // +50% max
            tab = UpgradeTab.ATTACK
        ),

        UpgradeType.CRITICAL_CHANCE to UpgradeInfo(
            name = "치명타 확률",
            description = "치명타 확률이 %만큼 증가합니다",
            maxLevel = 19, // 5% + 95% = 100%
            baseCost = 20,
            costMultiplier = 1.25f,
            bonusPerLevel = 5f, // +5% per level
            maxBonus = 95f, // +95% max (기본 5% + 95% = 100%)
            tab = UpgradeTab.ATTACK
        ),

        UpgradeType.CRITICAL_DAMAGE to UpgradeInfo(
            name = "치명타 데미지",
            description = "치명타 데미지가 %만큼 증가합니다",
            maxLevel = 35, // 150% + 350% = 500%
            baseCost = 12,
            costMultiplier = 1.1f,
            bonusPerLevel = 10f, // +10% per level
            maxBonus = 350f, // +350% max (기본 150% + 350% = 500%)
            tab = UpgradeTab.ATTACK
        ),

        // 유틸 탭
        UpgradeType.GOLD_INCREASE to UpgradeInfo(
            name = "골드 획득량",
            description = "골드 획득량이 %만큼 증가합니다",
            maxLevel = 30,
            baseCost = 8,
            costMultiplier = 1.12f,
            bonusPerLevel = 10f, // +10% per level
            maxBonus = 300f, // +300% max
            tab = UpgradeTab.UTILITY
        ),

        UpgradeType.SILVER_INCREASE to UpgradeInfo(
            name = "실버 획득량",
            description = "실버 획득량이 %만큼 증가합니다",
            maxLevel = 20,
            baseCost = 10,
            costMultiplier = 1.15f,
            bonusPerLevel = 10f, // +10% per level
            maxBonus = 200f, // +200% max
            tab = UpgradeTab.UTILITY
        ),

        UpgradeType.GEM_INCREASE to UpgradeInfo(
            name = "젬 획득량",
            description = "젬 획득량이 개씩 증가합니다",
            maxLevel = 5,
            baseCost = 100,
            costMultiplier = 2.0f,
            bonusPerLevel = 1f, // +1 per level
            maxBonus = 5f, // +5 max
            tab = UpgradeTab.UTILITY
        ),

        UpgradeType.ENEMY_SPAWN_SPEED to UpgradeInfo(
            name = "적 스폰 속도",
            description = "적이 %만큼 빠르게 스폰됩니다",
            maxLevel = 20,
            baseCost = 25,
            costMultiplier = 1.3f,
            bonusPerLevel = 5f, // +5% per level
            maxBonus = 100f, // +100% max (2배 빠르게)
            tab = UpgradeTab.UTILITY
        ),

        UpgradeType.ENEMY_SPAWN_COUNT to UpgradeInfo(
            name = "적 스폰 수",
            description = "적 스폰 시 마리가 추가로 스폰됩니다",
            maxLevel = 3,
            baseCost = 150,
            costMultiplier = 2.5f,
            bonusPerLevel = 1f, // +1 per level
            maxBonus = 3f, // +3 max
            tab = UpgradeTab.UTILITY
        )
    )

    // 업그레이드 비용 계산
    fun getUpgradeCost(upgradeType: UpgradeType, currentLevel: Int): Int {
        val info = UPGRADE_DATA[upgradeType] ?: return 0
        if (currentLevel >= info.maxLevel) return Int.MAX_VALUE

        return (info.baseCost * info.costMultiplier.toDouble().pow(currentLevel.toDouble())).toInt()
    }

    // 현재 업그레이드 보너스 계산
    fun getCurrentBonus(upgradeType: UpgradeType, currentLevel: Int): Float {
        val info = UPGRADE_DATA[upgradeType] ?: return 0f
        return kotlin.math.min(info.bonusPerLevel * currentLevel, info.maxBonus)
    }
}
