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

    // 기본 데미지 계산
    val baseDamage = player.baseDamage * skill.damageMul

    // 영구 업그레이드 데미지 보너스 적용
    val permanentDamageMultiplier = UpgradeManager.getEffectiveDamage(saveData, 1) // 1을 곱해서 배수만 가져옴

    // 인게임 업그레이드 데미지 보너스 적용
    val inGameDamageMultiplier = InGameUpgradeManager.getDamageMultiplier(saveData)

    // 저항 계산
    val resist = enemy.resistAgainst(skill.baseElement).coerceIn(0f, 0.75f)

    // 기본 데미지 (저항 적용)
    var finalDamage = baseDamage * permanentDamageMultiplier * inGameDamageMultiplier * (1f - resist)

    // 치명타 확률 체크
    val critChance = InGameUpgradeManager.getCriticalChance(saveData)
    val isCritical = Random.nextFloat() * 100f < critChance

    if (isCritical) {
        val critDamage = InGameUpgradeManager.getCriticalDamage(saveData)
        finalDamage *= (critDamage / 100f) // 150% -> 1.5x
    }

    return maxOf(1, finalDamage.toInt())
}

fun isCriticalHit(player: Player): Boolean {
    val critChance = InGameUpgradeManager.getCriticalChance(player.saveData)
    return Random.nextFloat() * 100f < critChance
}
