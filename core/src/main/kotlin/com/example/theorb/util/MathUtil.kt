package com.example.theorb.util

import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.skills.Skill
import com.example.theorb.upgrades.InGameUpgradeManager
import com.example.theorb.upgrades.UpgradeManager
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

    // 2-3. 인게임 업그레이드에서 데미지 모디파이어 추출 (기존 시스템을 증가로 변환)
    val inGameDamageMultiplier = InGameUpgradeManager.getDamageMultiplier(saveData)
    if (inGameDamageMultiplier != 1.0f) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.DAMAGE_INCREASE,
            value = inGameDamageMultiplier
        ))
    }

    // 2-4. 보조스킬에서 데미지 모디파이어 추출
    modifiers.addAll(getSubSkillDamageModifiers(skill))

    // 3. 크리티컬 정보 계산
    val baseCritChance = InGameUpgradeManager.getCriticalChance(saveData)
    val subSkillCritChance = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.CRITICAL_CHANCE)
    val critChance = (baseCritChance + subSkillCritChance) / 100f // 퍼센트를 소수로 변환

    val baseCritDamage = InGameUpgradeManager.getCriticalDamage(saveData)
    val subSkillCritDamage = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.CRITICAL_DAMAGE_AMPLIFY)
    val critDamageMultiplier = (baseCritDamage + subSkillCritDamage) / 100f

    // 4. 속성 저항 계산
    val resist = enemy.resistAgainst(skill.baseElement).coerceIn(0f, 0.75f)

    // 5. 새로운 DamageCalculator로 최종 데미지 계산
    val finalDamage = DamageCalculator.calculateFinalDamage(
        baseDamage = baseDamage,
        modifiers = modifiers,
        skillElement = skill.baseElement,
        criticalChance = critChance,
        criticalDamageMultiplier = critDamageMultiplier,
        elementalResistance = resist
    )

    return maxOf(1, finalDamage.toInt())
}

fun isCriticalHit(player: Player): Boolean {
    val critChance = InGameUpgradeManager.getCriticalChance(player.saveData)
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

    val fireAddition = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.FIRE_DAMAGE_ADDITION)
    if (fireAddition != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.FIRE_DAMAGE_ADDITION,
            value = fireAddition.toFloat(),
            element = com.example.theorb.balance.Element.FIRE
        ))
    }

    val iceAddition = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.ICE_DAMAGE_ADDITION)
    if (iceAddition != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.COLD_DAMAGE_ADDITION,
            value = iceAddition.toFloat(),
            element = com.example.theorb.balance.Element.COLD
        ))
    }

    val lightningAddition = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.LIGHTNING_DAMAGE_ADDITION)
    if (lightningAddition != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.LIGHTNING_DAMAGE_ADDITION,
            value = lightningAddition.toFloat(),
            element = com.example.theorb.balance.Element.LIGHTNING
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

    val fireIncrease = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.FIRE_DAMAGE_INCREASE)
    if (fireIncrease != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.FIRE_DAMAGE_INCREASE,
            value = 1f + fireIncrease / 100f,
            element = com.example.theorb.balance.Element.FIRE
        ))
    }

    val iceIncrease = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.ICE_DAMAGE_INCREASE)
    if (iceIncrease != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.COLD_DAMAGE_INCREASE,
            value = 1f + iceIncrease / 100f,
            element = com.example.theorb.balance.Element.COLD
        ))
    }

    val lightningIncrease = skill.getEffectValue(com.example.theorb.skills.SubSkillEffectType.LIGHTNING_DAMAGE_INCREASE)
    if (lightningIncrease != 0) {
        modifiers.add(DamageModifier(
            type = com.example.theorb.data.OrbAbilityType.LIGHTNING_DAMAGE_INCREASE,
            value = 1f + lightningIncrease / 100f,
            element = com.example.theorb.balance.Element.LIGHTNING
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
