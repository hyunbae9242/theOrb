package com.example.theorb.util

import com.example.theorb.data.DamageCalculationType
import com.example.theorb.data.OrbAbilityType
import com.example.theorb.data.SaveData

/**
 * 데미지 계산 정보
 */
data class DamageModifier(
    val type: OrbAbilityType,
    val value: Float
)

/**
 * 확장 가능한 데미지 계산 시스템
 */
object DamageCalculator {

    /**
     * 최종 데미지 계산
     * 계산 순서: 베이스 데미지 → 추가 → 증가 → 증폭 → 크리티컬 → 속성 저항
     */
    fun calculateFinalDamage(
        baseDamage: Float,
        modifiers: List<DamageModifier>,
        skillElement: Any? = null,
        criticalChance: Float = 0f,
        criticalDamageMultiplier: Float = 1.5f,
        elementalResistance: Float = 0f
    ): Float {
        // 1단계: 추가 데미지 (베이스에 더하기)
        var finalDamage = baseDamage + calculateAdditionDamage(modifiers, skillElement)

        // 2단계: 증가 데미지 (퍼센트 증가, 합연산 후 곱연산)
        val increaseMultiplier = 1f + calculateIncreaseDamage(modifiers, skillElement)
        finalDamage *= increaseMultiplier

        // 3단계: 증폭 데미지 (최종 곱연산)
        val amplifyMultiplier = 1f + calculateAmplifyDamage(modifiers, skillElement)
        finalDamage *= amplifyMultiplier

        // 4단계: 크리티컬 계산
        val isCritical = Math.random() < criticalChance
        if (isCritical) {
            finalDamage *= criticalDamageMultiplier
        }

        // 5단계: 속성 저항 적용
        finalDamage *= (1f - elementalResistance)

        return finalDamage.coerceAtLeast(0f)
    }

    /**
     * 추가 데미지 계산 (정수 합연산)
     */
    private fun calculateAdditionDamage(modifiers: List<DamageModifier>, skillElement: Any?): Float {
        var additionDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.ADDITION }.forEach { modifier ->
            // 전체 데미지 추가만 계산 (속성별 데미지 추가는 더 이상 사용되지 않음)
            if (modifier.type == OrbAbilityType.DAMAGE_ADDITION) {
                additionDamage += modifier.value
            }
        }

        return additionDamage
    }

    /**
     * 증가 데미지 계산 (퍼센트 합연산)
     */
    private fun calculateIncreaseDamage(modifiers: List<DamageModifier>, skillElement: Any?): Float {
        var increaseDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.INCREASE }.forEach { modifier ->
            // 전체 데미지 증가만 계산 (속성별 데미지 증가는 더 이상 사용되지 않음)
            if (modifier.type == OrbAbilityType.DAMAGE_INCREASE) {
                increaseDamage += (modifier.value - 1f) // 1.15f -> 0.15f (15% 증가)
            }
        }

        return increaseDamage
    }

    /**
     * 증폭 데미지 계산 (최종 곱연산)
     */
    private fun calculateAmplifyDamage(modifiers: List<DamageModifier>, skillElement: Any?): Float {
        var amplifyDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.AMPLIFY }.forEach { modifier ->
            // 전체 데미지 증폭만 계산 (속성별 데미지 증폭은 더 이상 사용되지 않음)
            if (modifier.type == OrbAbilityType.DAMAGE_AMPLIFY) {
                amplifyDamage += (modifier.value - 1f)
            }
        }

        return amplifyDamage
    }


    /**
     * 오브에서 데미지 모디파이어 추출
     */
    fun getOrbDamageModifiers(saveData: SaveData): List<DamageModifier> {
        val orb = com.example.theorb.data.OrbRegistry.getOrbById(saveData.selectedOrb) ?: return emptyList()

        return orb.abilities.mapNotNull { ability ->
            when (ability.type) {
                OrbAbilityType.DAMAGE_ADDITION,
                OrbAbilityType.DAMAGE_INCREASE,
                OrbAbilityType.DAMAGE_AMPLIFY -> {
                    DamageModifier(ability.type, ability.value)
                }
                else -> null
            }
        }
    }
}
