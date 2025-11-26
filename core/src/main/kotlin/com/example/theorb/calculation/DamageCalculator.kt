package com.example.theorb.calculation

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.data.SaveData
import com.example.theorb.upgrades.UpgradeManager

/**
 * 데미지 관련 계산을 담당하는 객체
 */
object DamageCalculator {

    /**
     * 공격력 배율 계산 (합연산)
     * 인게임 레벨업 + 영구 업그레이드 + 오브 보너스는 MathUtil.calcDamage에서 통합
     */
    fun getDamageMultiplierFromLevelUp(saveData: SaveData): Float {
        var totalBonus = 0f

        // 인게임 레벨업 시스템
        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.DAMAGE_NORMAL.name -> totalBonus += 10f
                LevelUpOptionType.DAMAGE_RARE.name -> totalBonus += 50f
                LevelUpOptionType.DAMAGE_UNIQUE.name -> totalBonus += 100f
            }
        }

        return 1f + (totalBonus / 100f)
    }

    /**
     * 최종 데미지 배율 계산 (레벨업 + 영구 업그레이드 통합)
     */
    fun getTotalDamageMultiplier(saveData: SaveData): Float {
        val levelUpMultiplier = getDamageMultiplierFromLevelUp(saveData)
        val permanentMultiplier = UpgradeManager.getDamageMultiplier(saveData)

        // 각각 배율을 모디파이어로 변환하여 합산
        return levelUpMultiplier * permanentMultiplier
    }
}
