package com.example.theorb.util

import com.example.theorb.balance.Element
import com.example.theorb.data.DamageCalculationType
import com.example.theorb.data.OrbAbilityType
import com.example.theorb.data.OrbRegistry
import com.example.theorb.data.SaveData

object OrbManager {

    /**
     * 오브 능력에 따른 데미지 배율 계산
     */
    fun getDamageMultiplier(saveData: SaveData, element: Element? = null): Float {
        // 이 함수는 기존 시스템과의 호환성을 위해 유지하지만
        // 새로운 시스템에서는 DamageCalculator를 사용하는 것을 권장
        val damageModifiers = DamageCalculator.getOrbDamageModifiers(saveData)

        // 간단한 증가 타입 계산만 수행 (기존 호환성)
        var multiplier = 1.0f

        damageModifiers.forEach { modifier ->
            if (modifier.type.calculationType == DamageCalculationType.INCREASE) {
                when {
                    modifier.type == OrbAbilityType.DAMAGE_INCREASE -> {
                        multiplier *= modifier.value
                    }
                    element != null && modifier.element == element -> {
                        multiplier *= modifier.value
                    }
                }
            }
        }

        return multiplier
    }

    /**
     * 오브 능력에 따른 쿨다운 배율 계산
     */
    fun getCooldownMultiplier(saveData: SaveData): Float {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb) ?: return 1.0f

        var multiplier = 1.0f

        if (orb.hasAbility(OrbAbilityType.COOLDOWN_REDUCTION)) {
            multiplier *= orb.getAbilityValue(OrbAbilityType.COOLDOWN_REDUCTION)
        }

        return multiplier
    }

    /**
     * 오브 능력에 따른 사정거리 배율 계산
     */
    fun getRangeMultiplier(saveData: SaveData): Float {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb) ?: return 1.0f

        var multiplier = 1.0f

        if (orb.hasAbility(OrbAbilityType.RANGE_INCREASE)) {
            multiplier *= orb.getAbilityValue(OrbAbilityType.RANGE_INCREASE)
        }

        return multiplier
    }

    /**
     * 오브 능력에 따른 치명타 확률 추가
     */
    fun getCritChanceBonus(saveData: SaveData): Float {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb) ?: return 0.0f

        var bonus = 0.0f

        if (orb.hasAbility(OrbAbilityType.CRIT_CHANCE)) {
            bonus += orb.getAbilityValue(OrbAbilityType.CRIT_CHANCE)
        }

        return bonus
    }

    /**
     * 오브 능력에 따른 체력 배율 계산
     */
    fun getHealthMultiplier(saveData: SaveData): Float {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb) ?: return 1.0f

        var multiplier = 1.0f

        if (orb.hasAbility(OrbAbilityType.HEALTH_BOOST)) {
            multiplier *= orb.getAbilityValue(OrbAbilityType.HEALTH_BOOST)
        }

        return multiplier
    }

    /**
     * 현재 선택된 오브의 능력 설명 반환
     */
    fun getOrbAbilityDescription(saveData: SaveData): String {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb)
            ?: return "선택된 오브가 없습니다."

        return orb.description
    }

    /**
     * 오브 능력이 적용된 최종 값들을 로그로 출력 (디버깅용)
     */
    fun logOrbEffects(saveData: SaveData) {
        val orb = OrbRegistry.getOrbById(saveData.selectedOrb) ?: return

        println("=== 오브 효과 ===")
        println("선택된 오브: ${orb.name}")
        println("보유 능력: ${orb.abilities.size}개")
        orb.abilities.forEach { ability ->
            println("  ${ability.type}: ${ability.value}")
        }
        println("데미지 배율: ${getDamageMultiplier(saveData)}")
        println("쿨다운 배율: ${getCooldownMultiplier(saveData)}")
        println("사정거리 배율: ${getRangeMultiplier(saveData)}")
        println("치명타 확률 보너스: ${getCritChanceBonus(saveData)}")
        println("체력 배율: ${getHealthMultiplier(saveData)}")
    }
}
