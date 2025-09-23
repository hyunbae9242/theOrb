package com.example.theorb.util

import com.example.theorb.balance.Element
import com.example.theorb.data.DamageCalculationType
import com.example.theorb.data.OrbAbilityType
import com.example.theorb.data.SaveData

/**
 * 데미지 계산 정보
 */
data class DamageModifier(
    val type: OrbAbilityType,
    val value: Float,
    val element: Element? = null // 속성별 데미지인 경우
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
        skillElement: Element? = null,
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
    private fun calculateAdditionDamage(modifiers: List<DamageModifier>, skillElement: Element?): Float {
        var additionDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.ADDITION }.forEach { modifier ->
            when {
                // 전체 데미지 추가
                modifier.type == OrbAbilityType.DAMAGE_ADDITION -> {
                    additionDamage += modifier.value
                }
                // 속성별 데미지 추가
                modifier.element == skillElement -> {
                    additionDamage += modifier.value
                }
            }
        }

        return additionDamage
    }

    /**
     * 증가 데미지 계산 (퍼센트 합연산)
     */
    private fun calculateIncreaseDamage(modifiers: List<DamageModifier>, skillElement: Element?): Float {
        var increaseDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.INCREASE }.forEach { modifier ->
            when {
                // 전체 데미지 증가
                modifier.type == OrbAbilityType.DAMAGE_INCREASE -> {
                    increaseDamage += (modifier.value - 1f) // 1.15f -> 0.15f (15% 증가)
                }
                // 속성별 데미지 증가
                skillElement != null && isElementalDamageIncrease(modifier.type, skillElement) -> {
                    increaseDamage += (modifier.value - 1f)
                }
            }
        }

        return increaseDamage
    }

    /**
     * 증폭 데미지 계산 (최종 곱연산)
     */
    private fun calculateAmplifyDamage(modifiers: List<DamageModifier>, skillElement: Element?): Float {
        var amplifyDamage = 0f

        modifiers.filter { it.type.calculationType == DamageCalculationType.AMPLIFY }.forEach { modifier ->
            when {
                // 전체 데미지 증폭
                modifier.type == OrbAbilityType.DAMAGE_AMPLIFY -> {
                    amplifyDamage += (modifier.value - 1f)
                }
                // 속성별 데미지 증폭
                skillElement != null && isElementalDamageAmplify(modifier.type, skillElement) -> {
                    amplifyDamage += (modifier.value - 1f)
                }
            }
        }

        return amplifyDamage
    }

    /**
     * 속성별 데미지 증가 타입 확인
     */
    private fun isElementalDamageIncrease(abilityType: OrbAbilityType, skillElement: Element): Boolean {
        return when (skillElement) {
            Element.FIRE -> abilityType == OrbAbilityType.FIRE_DAMAGE_INCREASE
            Element.LIGHTNING -> abilityType == OrbAbilityType.LIGHTNING_DAMAGE_INCREASE
            Element.COLD -> abilityType == OrbAbilityType.COLD_DAMAGE_INCREASE
            Element.ANGEL -> abilityType == OrbAbilityType.ANGEL_DAMAGE_INCREASE
            Element.DEMON -> abilityType == OrbAbilityType.DEMON_DAMAGE_INCREASE
        }
    }

    /**
     * 속성별 데미지 증폭 타입 확인
     */
    private fun isElementalDamageAmplify(abilityType: OrbAbilityType, skillElement: Element): Boolean {
        return when (skillElement) {
            Element.FIRE -> abilityType == OrbAbilityType.FIRE_DAMAGE_AMPLIFY
            Element.LIGHTNING -> abilityType == OrbAbilityType.LIGHTNING_DAMAGE_AMPLIFY
            Element.COLD -> abilityType == OrbAbilityType.COLD_DAMAGE_AMPLIFY
            Element.ANGEL -> abilityType == OrbAbilityType.ANGEL_DAMAGE_AMPLIFY
            Element.DEMON -> abilityType == OrbAbilityType.DEMON_DAMAGE_AMPLIFY
        }
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
                OrbAbilityType.DAMAGE_AMPLIFY,
                OrbAbilityType.FIRE_DAMAGE_ADDITION,
                OrbAbilityType.FIRE_DAMAGE_INCREASE,
                OrbAbilityType.FIRE_DAMAGE_AMPLIFY,
                OrbAbilityType.LIGHTNING_DAMAGE_ADDITION,
                OrbAbilityType.LIGHTNING_DAMAGE_INCREASE,
                OrbAbilityType.LIGHTNING_DAMAGE_AMPLIFY,
                OrbAbilityType.COLD_DAMAGE_ADDITION,
                OrbAbilityType.COLD_DAMAGE_INCREASE,
                OrbAbilityType.COLD_DAMAGE_AMPLIFY,
                OrbAbilityType.ANGEL_DAMAGE_ADDITION,
                OrbAbilityType.ANGEL_DAMAGE_INCREASE,
                OrbAbilityType.ANGEL_DAMAGE_AMPLIFY,
                OrbAbilityType.DEMON_DAMAGE_ADDITION,
                OrbAbilityType.DEMON_DAMAGE_INCREASE,
                OrbAbilityType.DEMON_DAMAGE_AMPLIFY -> {
                    DamageModifier(ability.type, ability.value, getElementFromAbility(ability.type))
                }
                else -> null
            }
        }
    }

    /**
     * 능력 타입에서 속성 추출
     */
    private fun getElementFromAbility(abilityType: OrbAbilityType): Element? {
        return when (abilityType) {
            OrbAbilityType.FIRE_DAMAGE_ADDITION,
            OrbAbilityType.FIRE_DAMAGE_INCREASE,
            OrbAbilityType.FIRE_DAMAGE_AMPLIFY -> Element.FIRE

            OrbAbilityType.LIGHTNING_DAMAGE_ADDITION,
            OrbAbilityType.LIGHTNING_DAMAGE_INCREASE,
            OrbAbilityType.LIGHTNING_DAMAGE_AMPLIFY -> Element.LIGHTNING

            OrbAbilityType.COLD_DAMAGE_ADDITION,
            OrbAbilityType.COLD_DAMAGE_INCREASE,
            OrbAbilityType.COLD_DAMAGE_AMPLIFY -> Element.COLD

            OrbAbilityType.ANGEL_DAMAGE_ADDITION,
            OrbAbilityType.ANGEL_DAMAGE_INCREASE,
            OrbAbilityType.ANGEL_DAMAGE_AMPLIFY -> Element.ANGEL

            OrbAbilityType.DEMON_DAMAGE_ADDITION,
            OrbAbilityType.DEMON_DAMAGE_INCREASE,
            OrbAbilityType.DEMON_DAMAGE_AMPLIFY -> Element.DEMON

            else -> null
        }
    }
}
