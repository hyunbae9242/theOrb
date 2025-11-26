package com.example.theorb.calculation

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.data.SaveData
import com.example.theorb.upgrades.UpgradeManager

/**
 * 쿨다운 관련 계산을 담당하는 객체
 */
object CooldownCalculator {

    /**
     * 쿨다운 감소 배율 계산
     * - 선택지 상한선: -50%
     * - 최종 상한선 (영구 업그레이드 포함): -80%
     * 인게임 레벨업 + 영구 업그레이드 합연산
     */
    fun getCooldownMultiplier(saveData: SaveData): Float {
        var levelUpReduction = 0f

        // 인게임 레벨업 시스템 (선택지 상한선 50%)
        saveData.selectedLevelUpOptions.forEach { optionName ->
            when (optionName) {
                LevelUpOptionType.COOLDOWN_NORMAL.name -> levelUpReduction += 3f
                LevelUpOptionType.COOLDOWN_RARE.name -> levelUpReduction += 5f
                LevelUpOptionType.COOLDOWN_UNIQUE.name -> levelUpReduction += 10f
            }
        }
        levelUpReduction = levelUpReduction.coerceAtMost(50f) // 선택지 상한선 50%

        // 영구 업그레이드에서 쿨다운 감소 추출
        // UpgradeManager.getCooldownMultiplier는 배율을 반환하므로 감소율로 변환
        val permanentMultiplier = UpgradeManager.getCooldownMultiplier(saveData)
        val permanentReduction = (1f - permanentMultiplier) * 100f // 예: 0.9 -> 10%

        var totalReduction = levelUpReduction + permanentReduction

        // 최종 상한선 -80%
        totalReduction = totalReduction.coerceAtMost(80f)

        return 1f - (totalReduction / 100f)
    }
}
