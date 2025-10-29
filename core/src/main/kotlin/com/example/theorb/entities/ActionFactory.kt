package com.example.theorb.entities

import com.example.theorb.effects.Effect
import com.example.theorb.skills.Skill
import com.example.theorb.util.calcDamage
import com.example.theorb.util.dist2

object ActionFactory {

    fun playerCastSkill(skill: Skill,
                        enemies: MutableList<Enemy>,
                        x: Float,
                        y: Float,
                        effectiveRange: Float,
                        projectiles: MutableList<Projectile>,
                        caster: Player,
                        effects: MutableList<Effect>,
                        onDamage: ((Int, Float, Float, com.example.theorb.balance.Element, String) -> Unit)? = null
                        ): Boolean
    {
        if (skill.canUse()) {
            if (skill.isAOE) {
                // AOE 스킬: 사정거리 내 모든 적을 타겟으로 함
                val targetsInRange = enemies
                    .filter { !it.isDead() && dist2(it.x, it.y, x, y) <= effectiveRange * effectiveRange }

                if (targetsInRange.isNotEmpty()) {
                    val aoeProjectiles = skill.createAOEProjectiles(x, y, targetsInRange, caster, effects, onDamage)
                    projectiles.addAll(aoeProjectiles)
                    skill.resetCooldown()
                    return true
                }
            } else {
                return castProjectileToTarget(skill, enemies, x, y, effectiveRange, projectiles, caster, effects, onDamage = onDamage)
            }
        }
        return false
    }

    fun castProjectileToTarget(skill: Skill,
                               enemies: MutableList<Enemy>,
                               x: Float,
                               y: Float,
                               effectiveRange: Float,
                               projectiles: MutableList<Projectile>,
                               caster: Player,
                               effects: MutableList<Effect>,
                               chainCnt: Int = 0,
                               beforeEnemies: MutableList<Enemy> = mutableListOf(),
                               onDamage: ((Int, Float, Float, com.example.theorb.balance.Element, String) -> Unit)? = null
    ): Boolean {
        val projectileCount = skill.getProjectileCount()
        val nearbyTargets = enemies
            .filter { !it.isDead() && !beforeEnemies.contains(it) && dist2(it.x, it.y, x, y) <= effectiveRange * effectiveRange }
            .sortedBy { dist2(it.x, it.y, x, y) }
            .take(projectileCount)

        if (nearbyTargets.isNotEmpty()) {
            for (enemy in nearbyTargets) {
                // 오버킬 방지
                if (enemy.vhp <= 0) { continue }
                val finDamage = calcDamage(enemy, caster, skill)
                enemy.vhp -= finDamage
                beforeEnemies.add(enemy)
                projectiles.add(skill.createProjectile(x, y, enemy, caster, finDamage, effects, chainCnt, beforeEnemies, onDamage))
            }
            if (chainCnt == 0) skill.resetCooldown()
            return true
        }
        return false
    }
}
