package com.example.theorb.skills

import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

class LightningStrike : Skill(
    name = "낙뢰",
    baseCooldown = 0.25f, // 0.5f → 0.25f (50% 감소)
    baseDamageMul = 1.3f,
    hitEffectType = EffectType.LIGHTNING_STRIKE,
    isInstant = true, // 즉발 스킬
    baseDescription = "즉시 적에게 번개를 내리칩니다."
) {

    override val tags: List<SkillTag> = listOf(SkillTag.LIGHTNING, SkillTag.INSTANT, SkillTag.AOE)

    // LightningStrike 전용 등급 배율 (즉발 스킬 특성)
    override fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.4f,
        SkillRank.A to 1.9f,
        SkillRank.S to 2.7f,
        SkillRank.SS to 3.6f,
        SkillRank.SSS to 4.8f
    )
    override fun createProjectile(
        x: Float,
        y: Float,
        target: Enemy,
        caster: Player,
        preCalculatedDamage: Int,
        effects: MutableList<Effect>,
        chainCnt: Int,
        beforeEnemies: MutableList<Enemy>,
        onDamage: ((Int, Float, Float, String) -> Unit)?
    ): Projectile {
        return Projectile(
            x = target.x,
            y = target.y,
            target = target,
            caster = caster,
            skill = this,
            preCalculatedDamage = preCalculatedDamage,
            onHit = { enemy ->
                effects.add(
                    Effect(
                        EffectManager.load(hitEffectType),
                        enemy.x + 8,
                        enemy.y - enemy.type.radius - 10,
                        hitEffectType.scale,
                        0f,
                        Anchor.BOTTOM
                    )
                )
            },
            onDamage = onDamage,
        )
    }


}
