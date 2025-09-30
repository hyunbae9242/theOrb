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
    val baseDamageMul: Float,
    val castEffectType: EffectType? = null,
    val flyEffectType: EffectType? = null,
    val hitEffectType: EffectType,
    val isInstant: Boolean = false, // 즉발 스킬 여부
    val isAOE: Boolean = false, // AOE 스킬 여부
    var rank: SkillRank = SkillRank.C // 기본 등급은 C
) {

    // 스킬별 등급 배율 정의 (서브클래스에서 오버라이드)
    open fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.3f,
        SkillRank.A to 1.7f,
        SkillRank.S to 2.2f,
        SkillRank.SS to 2.8f,
        SkillRank.SSS to 3.5f
    )

    // 등급을 적용한 최종 데미지 배율
    val damageMul: Float
        get() = baseDamageMul * (getRankMultipliers()[rank] ?: 1.0f)
    var cooldownTimer: Float = 0f
        private set

    fun canUse(): Boolean = cooldownTimer <= 0f
    fun resetCooldown() { cooldownTimer = baseCooldown }
    fun updateCooldown(delta: Float) { cooldownTimer -= delta }

    abstract fun createProjectile(x: Float, y: Float, target: Enemy, caster: Player, preCalculatedDamage: Int, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, com.example.theorb.balance.Element, String) -> Unit)? = null): Projectile

    // AOE 스킬용 메소드
    open fun createAOEProjectiles(x: Float, y: Float, targets: List<Enemy>, caster: Player, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, com.example.theorb.balance.Element, String) -> Unit)? = null): List<Projectile> {
        // 시전 효과가 있으면 시전자 위치에서 한 번만 발동
        if (castEffectType != null) {
            effects.add(
                Effect(
                    com.example.theorb.effects.EffectManager.load(castEffectType),
                    x,
                    y,
                    castEffectType.scale,
                    0f,
                    com.example.theorb.effects.Anchor.CENTER
                )
            )
        }

        // 기본적으로는 각 타겟에 대해 개별 프로젝타일 생성
        return targets.map { target ->
            val damage = com.example.theorb.util.calcDamage(target, caster, this)
            target.vhp -= damage // 가상 hp 감소
            createProjectile(x, y, target, caster, damage, effects, onDamage)
        }
    }
}

object SkillRegistry {
    fun createSkill(id: String): Skill {
        return when (id) {
            "LightningStrike" -> LightningStrike()
            "Fireball" -> Fireball()
            "IceLance" -> IceLance()
            "DivineNova" -> DivineNova()
            else -> throw IllegalArgumentException("Unknown skill id: $id")
        }
    }
}
