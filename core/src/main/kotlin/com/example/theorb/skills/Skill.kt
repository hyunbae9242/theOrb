package com.example.theorb.skills

import com.example.theorb.balance.Element
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

abstract class Skill(
    val name: String,
    val baseCooldown: Float,
    val baseElement: Element,
    val damageMul: Float,
    val castEffectType: EffectType? = null,
    val flyEffectType: EffectType? = null,
    val hitEffectType: EffectType,
    val isInstant: Boolean = false, // 즉발 스킬 여부
) {
    var cooldownTimer: Float = 0f
        private set

    fun canUse(): Boolean = cooldownTimer <= 0f
    fun resetCooldown() { cooldownTimer = baseCooldown }
    fun updateCooldown(delta: Float) { cooldownTimer -= delta }

    abstract fun createProjectile(x: Float, y: Float, target: Enemy, caster: Player, preCalculatedDamage: Int, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, com.example.theorb.balance.Element) -> Unit)? = null): Projectile
}

object SkillRegistry {
    fun createSkill(id: String): Skill {
        return when (id) {
            "LightningStrike" -> LightningStrike()
            "Fireball" -> Fireball()
            else -> throw IllegalArgumentException("Unknown skill id: $id")
        }
    }
}
