package com.example.theorb.util

import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.skills.Skill
import com.example.theorb.calculation.CriticalCalculator
import com.example.theorb.calculation.DamageCalculator as LevelUpDamageCalc
import com.example.theorb.upgrades.UpgradeManager
import java.util.Locale
import kotlin.random.Random

fun dist2(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x1 - x2
    val dy = y1 - y2
    return dx * dx + dy * dy
}

fun calcDamage(enemy: Enemy, player: Player, skill: Skill): Int {
    val saveData = player.saveData

    // 1. 베이스 데미지 계산
    val baseDamage = player.baseDamage * skill.damageMul

    // 2. 모든 데미지 모디파이어 수집
    val modifiers = mutableListOf<DamageModifier>()

    // 2-1. 오브에서 데미지 모디파이어 추출
    modifiers.addAll(DamageCalculator.getOrbDamageModifiers(saveData))

    // 2-2. 영구 업그레이드에서 데미지 모디파이어 추출 (기존 시스템을 증가로 변환)
    val permanentDamageMultiplier = UpgradeManager.getDamageMultiplier(saveData)
    if (permanentDamageMultiplier != 1.0f) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_INCREASE,
            value = permanentDamageMultiplier
        ))
    }

    // 2-3. 레벨업 시스템에서 데미지 모디파이어 추출 (증가로 변환)
    val levelUpDamageMultiplier = LevelUpDamageCalc.getDamageMultiplierFromLevelUp(saveData)
    if (levelUpDamageMultiplier != 1.0f) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_INCREASE,
            value = levelUpDamageMultiplier
        ))
    }

    // 2-4. 보조스킬에서 데미지 모디파이어 추출
    modifiers.addAll(getSubSkillDamageModifiers(skill))

    // 3. 크리티컬 정보 계산
    val baseCritChance = CriticalCalculator.getCriticalChance(saveData)
    val subSkillCritChance = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.CRITICAL_CHANCE)
    val critChance = (baseCritChance + subSkillCritChance) / 100f // 퍼센트를 소수로 변환

    // 치명타 데미지: 기본 150% + 보너스
    val baseCritDamage = CriticalCalculator.getCriticalDamageBonus(saveData)
    val subSkillCritDamage = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.CRITICAL_DAMAGE_AMPLIFY)
    val critDamageMultiplier = 1.5f + ((baseCritDamage + subSkillCritDamage) / 100f)

    // 4. 새로운 DamageCalculator로 최종 데미지 계산
    val finalDamage = DamageCalculator.calculateFinalDamage(
        baseDamage = baseDamage,
        modifiers = modifiers,
        skillElement = null,
        criticalChance = critChance,
        criticalDamageMultiplier = critDamageMultiplier,
        elementalResistance = 0f
    )

    return maxOf(1, finalDamage.toInt())
}

fun isCriticalHit(player: Player): Boolean {
    val critChance = CriticalCalculator.getCriticalChance(player.saveData)
    return Random.nextFloat() * 100f < critChance
}

/**
 * 큰 숫자를 압축 표기로 변환 (1000 -> 1K, 1000000 -> 1M 등)
 */
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000_000 -> "${(number / 1_000_000_000f).let { if (it >= 10) it.toInt().toString() else "%.1f".format(it) }}B"
        number >= 1_000_000 -> "${(number / 1_000_000f).let { if (it >= 10) it.toInt().toString() else "%.1f".format(it) }}M"
        number >= 1_000 -> "${(number / 1_000f).let { if (it >= 10) it.toInt().toString() else "%.1f".format(it) }}K"
        else -> number.toString()
    }
}

/**
 * 큰 숫자를 압축 표기로 변환 (Float 버전)
 */
fun formatNumber(number: Float): String {
    return formatNumber(number.toInt())
}


fun getPercent(current: Int, max: Int): String {
    if (max <= 0) return "0.0%"
    val percent = current.toFloat() / max * 100f
    return String.format(Locale.US, "%.1f%%", percent)
}

/**
 * 보조스킬에서 데미지 모디파이어 추출
 */
private fun getSubSkillDamageModifiers(skill: com.example.theorb.skills.Skill): List<DamageModifier> {
    val modifiers = mutableListOf<DamageModifier>()

    // 추가 데미지
    val damageAddition = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.DAMAGE_ADDITION)
    if (damageAddition != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_ADDITION,
            value = damageAddition.toFloat()
        ))
    }

    // 증가 데미지 (백분율을 배율로 변환: +20% -> 1.20, -15% -> 0.85)
    val damageIncrease = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.DAMAGE_INCREASE)
    if (damageIncrease != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_INCREASE,
            value = 1f + damageIncrease / 100f
        ))
    }

    // 증폭 데미지
    val damageAmplify = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.DAMAGE_AMPLIFY)
    if (damageAmplify != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_AMPLIFY,
            value = 1f + damageAmplify / 100f
        ))
    }

    // 크리티컬 데미지 증폭은 크리티컬 배율 계산 시 직접 적용하므로 여기서는 제외

    return modifiers
}
