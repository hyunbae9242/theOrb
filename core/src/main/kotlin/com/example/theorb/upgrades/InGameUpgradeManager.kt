package com.example.theorb.upgrades

import com.example.theorb.balance.InGameUpgrades
import com.example.theorb.data.SaveData

object InGameUpgradeManager {

    // 데미지 배수 계산 (인게임 업그레이드)
    fun getDamageMultiplier(saveData: SaveData): Float {
        val level = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.DAMAGE_INCREASE.name] ?: 0
        val bonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.DAMAGE_INCREASE, level)
        return 1f + (bonus / 100f) // +10% per level -> 1.1x multiplier
    }

    // 쿨다운 배수 계산 (인게임 + 영구 업그레이드 곱연산)
    fun getCooldownMultiplier(saveData: SaveData): Float {
        // 인게임 업그레이드
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.COOLDOWN_REDUCTION.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.COOLDOWN_REDUCTION, inGameLevel)
        val inGameMultiplier = 1f + (inGameBonus / 100f)

        // 영구 업그레이드 (기존 UpgradeManager에서 가져옴)
        val permanentMultiplier = UpgradeManager.getCooldownMultiplier(saveData)

        return inGameMultiplier * permanentMultiplier
    }

    // 치명타 확률 (인게임 업그레이드만)
    fun getCriticalChance(saveData: SaveData): Float {
        return saveData.criticalChance // 이미 계산된 값 사용
    }

    // 치명타 데미지 (인게임 업그레이드만)
    fun getCriticalDamage(saveData: SaveData): Float {
        return saveData.criticalDamage // 이미 계산된 값 사용
    }

    // 골드 획득량 배수 (인게임 + 영구 업그레이드 합연산)
    fun getGoldMultiplier(saveData: SaveData): Float {
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.GOLD_INCREASE.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.GOLD_INCREASE, inGameLevel)

        // 영구 업그레이드도 나중에 추가될 예정 (현재는 0)
        val permanentBonus = 0f

        val totalBonus = inGameBonus + permanentBonus
        return 1f + (totalBonus / 100f)
    }

    // 실버 획득량 배수 (인게임 + 영구 업그레이드 합연산)
    fun getSilverMultiplier(saveData: SaveData): Float {
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.SILVER_INCREASE.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.SILVER_INCREASE, inGameLevel)

        // 영구 업그레이드도 나중에 추가될 예정 (현재는 0)
        val permanentBonus = 0f

        val totalBonus = inGameBonus + permanentBonus
        return 1f + (totalBonus / 100f)
    }

    // 젬 획득량 추가 (인게임 + 영구 업그레이드 합연산)
    fun getGemBonus(saveData: SaveData): Int {
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.GEM_INCREASE.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.GEM_INCREASE, inGameLevel).toInt()

        // 영구 업그레이드도 나중에 추가될 예정 (현재는 0)
        val permanentBonus = 0

        return inGameBonus + permanentBonus
    }

    // 적 스폰 속도 배수 (인게임 + 영구 업그레이드 합연산)
    fun getEnemySpawnSpeedMultiplier(saveData: SaveData): Float {
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.ENEMY_SPAWN_SPEED.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.ENEMY_SPAWN_SPEED, inGameLevel)

        // 영구 업그레이드도 나중에 추가될 예정 (현재는 0)
        val permanentBonus = 0f

        val totalBonus = inGameBonus + permanentBonus
        return 1f + (totalBonus / 100f) // +100% = 2배 빠르게
    }

    // 적 스폰 수 추가 (인게임 + 영구 업그레이드 합연산)
    fun getEnemySpawnCountBonus(saveData: SaveData): Int {
        val inGameLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.ENEMY_SPAWN_COUNT.name] ?: 0
        val inGameBonus = InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.ENEMY_SPAWN_COUNT, inGameLevel).toInt()

        // 영구 업그레이드도 나중에 추가될 예정 (현재는 0)
        val permanentBonus = 0

        return inGameBonus + permanentBonus
    }
}