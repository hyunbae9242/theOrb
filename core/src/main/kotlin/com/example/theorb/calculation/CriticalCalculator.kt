package com.example.theorb.calculation

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.data.SaveData
import com.example.theorb.util.OrbManager

/**
 * 치명타 관련 계산을 담당하는 객체
 */
object CriticalCalculator {

    /**
     * 치명타 확률 계산 (합연산, 상한선 50%)
     * 인게임 레벨업 + 영구 업그레이드 + 오브 보너스
     */
    fun getCriticalChance(saveData: SaveData): Float {
        var totalBonus = 0f

        // 인게임 레벨업 시스템
        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.CRIT_CHANCE_NORMAL.name -> totalBonus += 5f
                LevelUpOptionType.CRIT_CHANCE_RARE.name -> totalBonus += 10f
                LevelUpOptionType.CRIT_CHANCE_UNIQUE.name -> totalBonus += 20f
            }
        }

        // 영구 업그레이드 (pBaseCriChance)
        totalBonus += saveData.pBaseCriChance.toFloat()

        // 오브 보너스
        val orbCritBonus = OrbManager.getCritChanceBonus(saveData) * 100f // 0.15 -> 15%
        totalBonus += orbCritBonus

        // 상한선 50%
        return totalBonus.coerceAtMost(50f)
    }

    /**
     * 치명타 데미지 계산 (합연산)
     * 인게임 레벨업만 적용 (기본 150%에서 시작)
     */
    fun getCriticalDamageBonus(saveData: SaveData): Float {
        var totalBonus = 0f

        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.CRIT_DAMAGE_NORMAL.name -> totalBonus += 10f
                LevelUpOptionType.CRIT_DAMAGE_RARE.name -> totalBonus += 30f
                LevelUpOptionType.CRIT_DAMAGE_UNIQUE.name -> totalBonus += 100f
            }
        }

        return totalBonus
    }
}
