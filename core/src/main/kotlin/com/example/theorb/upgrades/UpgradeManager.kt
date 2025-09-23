package com.example.theorb.upgrades

import com.example.theorb.data.SaveData
import com.example.theorb.util.OrbManager

object UpgradeManager {

    fun getUpgradeLevel(saveData: SaveData, upgradeType: UpgradeType): Int {
        return saveData.permanentUpgrades[upgradeType.name] ?: 0
    }

    fun canUpgrade(saveData: SaveData, upgradeType: UpgradeType): Boolean {
        val currentLevel = getUpgradeLevel(saveData, upgradeType)
        val cost = upgradeType.getCostForLevel(currentLevel)
        return currentLevel < upgradeType.maxLevel && saveData.gold >= cost
    }

    fun purchaseUpgrade(saveData: SaveData, upgradeType: UpgradeType): Boolean {
        if (!canUpgrade(saveData, upgradeType)) return false

        val currentLevel = getUpgradeLevel(saveData, upgradeType)
        val cost = upgradeType.getCostForLevel(currentLevel)

        saveData.gold -= cost
        saveData.permanentUpgrades[upgradeType.name] = currentLevel + 1

        return true
    }

    fun getUpgradeValue(saveData: SaveData, upgradeType: UpgradeType): Float {
        val level = getUpgradeLevel(saveData, upgradeType)
        return upgradeType.getValueAtLevel(level)
    }

    // 플레이어 스탯에 업그레이드 적용
    fun getEffectiveDamage(saveData: SaveData, baseDamage: Int): Int {
        val damageBonus = getUpgradeValue(saveData, UpgradeType.DAMAGE)
        return (baseDamage + damageBonus).toInt()
    }

    // 데미지 계산용 - 배수 형태로 반환 (오브 효과 포함)
    fun getDamageMultiplier(saveData: SaveData): Float {
        val damageBonus = getUpgradeValue(saveData, UpgradeType.DAMAGE)
        val upgradeMultiplier = (10f + damageBonus) / 10f
        val orbMultiplier = OrbManager.getDamageMultiplier(saveData)
        return upgradeMultiplier * orbMultiplier
    }

    fun getEffectiveRange(saveData: SaveData, baseRange: Float): Float {
        val rangeMultiplier = getUpgradeValue(saveData, UpgradeType.RANGE)
        val upgradeRange = baseRange * (1f + rangeMultiplier)
        val orbRangeMultiplier = OrbManager.getRangeMultiplier(saveData)
        return upgradeRange * orbRangeMultiplier // 업그레이드 사정거리 * 오브 배율
    }

    fun getCooldownMultiplier(saveData: SaveData): Float {
        val reductionPercent = getUpgradeValue(saveData, UpgradeType.COOLDOWN_REDUCTION)
        val upgradeMultiplier = 1f - reductionPercent // 3% 감소면 0.97 반환
        val orbMultiplier = OrbManager.getCooldownMultiplier(saveData)
        return upgradeMultiplier * orbMultiplier // 업그레이드 * 오브 효과
    }

    // 업그레이드 초기화 - 사용한 골드를 모두 환불
    fun resetAllUpgrades(saveData: SaveData): Int {
        var totalRefund = 0

        for (upgradeType in UpgradeType.values()) {
            val currentLevel = getUpgradeLevel(saveData, upgradeType)

            // 레벨 0부터 현재 레벨까지의 총 비용 계산
            for (level in 0 until currentLevel) {
                totalRefund += upgradeType.getCostForLevel(level)
            }
        }

        // 모든 업그레이드 레벨을 0으로 초기화
        saveData.permanentUpgrades.clear()

        // 골드 환불
        saveData.gold += totalRefund

        return totalRefund
    }
}