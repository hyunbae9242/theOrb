package com.example.theorb.skills

import com.example.theorb.balance.Element
import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

class IceLance : Skill(
    name = "얼음창",
    baseCooldown = 0.75f,
    baseElement = Element.COLD,
    baseDamageMul = 1.6f,
    hitEffectType = EffectType.ICE_LANCE_HIT,
    flyEffectType = EffectType.ICE_LANCE_FLY
) {

    // IceLance 전용 등급 배율 (균형잡힌 성장)
    override fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.35f,
        SkillRank.A to 1.8f,
        SkillRank.S to 2.4f,
        SkillRank.SS to 3.1f,
        SkillRank.SSS to 4.0f
    )

    override fun createProjectile(x: Float, y: Float, target: Enemy, caster: Player, preCalculatedDamage: Int, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, Element, String) -> Unit)?): Projectile {
        return Projectile(x, y, target, caster, this, preCalculatedDamage, onHit = {enemy ->
            effects.add(
                Effect(
                    EffectManager.load(hitEffectType),
                    enemy.x,
                    enemy.y,
                    hitEffectType.scale,
                    0f,
                    Anchor.CENTER
                )
            )
        }, onDamage = onDamage)
    }


}
