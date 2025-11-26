package com.example.theorb.calculation

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.data.SaveData
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.UpgradeType

/**
 * 플레이어 스탯(체력, 흡혈, 에너지 쉴드) 관련 계산을 담당하는 객체
 */
object PlayerStatsCalculator {

    /**
     * 최대 HP 계산 (baseHp + 영구 업그레이드 + 인게임 레벨업)
     */
    fun calculateMaxHp(saveData: SaveData): Int {
        val baseHp = saveData.pBaseHp

        // 영구 업그레이드 보너스
        val permanentBonus = UpgradeManager.getUpgradeValue(saveData, UpgradeType.HEALTH).toInt()

        // 인게임 레벨업 배율
        val inGameMultiplier = getHpMultiplierFromLevelUp(saveData)

        return ((baseHp + permanentBonus) * inGameMultiplier).toInt()
    }

    /**
     * 체력 배율 계산 (인게임 레벨업만)
     */
    private fun getHpMultiplierFromLevelUp(saveData: SaveData): Float {
        var totalBonus = 0f

        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.HP_NORMAL.name -> totalBonus += 10f
                LevelUpOptionType.HP_RARE.name -> totalBonus += 30f
                LevelUpOptionType.HP_UNIQUE.name -> totalBonus += 100f
            }
        }

        return 1f + (totalBonus / 100f)
    }

    /**
     * 흡혈 비율 계산
     * 유니크 우선, 중복 불가
     */
    fun getLifestealRate(saveData: SaveData): Float {
        var hasRare = false
        var hasUnique = false

        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.LIFESTEAL_RARE.name -> hasRare = true
                LevelUpOptionType.LIFESTEAL_UNIQUE.name -> hasUnique = true
            }
        }

        // 둘 중 하나만 적용 (유니크 우선)
        return when {
            hasUnique -> 0.04f // 4%
            hasRare -> 0.02f // 2%
            else -> 0f
        }
    }

    /**
     * 에너지 쉴드 획득량 (스킬 시전당)
     * 유니크 우선, 중복 불가
     */
    fun getEnergyShieldPerCast(saveData: SaveData): Int {
        var hasRare = false
        var hasUnique = false

        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.ENERGY_SHIELD_RARE.name -> hasRare = true
                LevelUpOptionType.ENERGY_SHIELD_UNIQUE.name -> hasUnique = true
            }
        }

        // 둘 중 하나만 적용 (유니크 우선)
        return when {
            hasUnique -> 5
            hasRare -> 3
            else -> 0
        }
    }
}
