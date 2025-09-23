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
    damageMul = 1.6f,
    hitEffectType = EffectType.ICE_LANCE_HIT,
    flyEffectType = EffectType.ICE_LANCE_FLY
) {
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
