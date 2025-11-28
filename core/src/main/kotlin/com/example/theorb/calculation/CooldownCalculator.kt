package com.example.theorb.calculation

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.data.SaveData
import com.example.theorb.upgrades.UpgradeManager

/**
 * 쿨다운 관련 계산을 담당하는 객체
 */
object CooldownCalculator {

    /**
     * 쿨다운 감소율 계산 (퍼센트로 반환)
     * - 선택지 상한선: 50%
     * - 최종 상한선 (영구 업그레이드 포함): 80%
     * 인게임 레벨업 + 영구 업그레이드 합연산
     */
    fun getCooldownReduction(saveData: SaveData): Float {
        var totalReduction = 0f

        // 인게임 레벨업 시스템
        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.COOLDOWN_NORMAL.name -> totalReduction += 3f
                LevelUpOptionType.COOLDOWN_RARE.name -> totalReduction += 5f
                LevelUpOptionType.COOLDOWN_UNIQUE.name -> totalReduction += 10f
            }
        }
        totalReduction = totalReduction.coerceAtMost(50f) // 선택지 상한선 50%

        // 영구 업그레이드에서 쿨다운 감소 추출 (% 단위)
        val permanentReduction = UpgradeManager.getUpgradeValue(saveData, com.example.theorb.upgrades.UpgradeType.COOLDOWN_REDUCTION) * 100f
        totalReduction += permanentReduction

        // 최종 상한선 80%
        return totalReduction.coerceAtMost(80f)
    }

    /**
     * 쿨다운 감소 배율 계산 (실제 스킬에 적용할 배율)
     * - 선택지 상한선: -50%
     * - 최종 상한선 (영구 업그레이드 포함): -80%
     * 인게임 레벨업 + 영구 업그레이드 합연산
     */
    fun getCooldownMultiplier(saveData: SaveData): Float {
        val totalReduction = getCooldownReduction(saveData)
        return 1f - (totalReduction / 100f)
    }
}
