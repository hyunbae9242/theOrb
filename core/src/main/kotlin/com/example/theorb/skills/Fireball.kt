package com.example.theorb.skills

import com.example.theorb.balance.Element
import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile
import com.example.theorb.util.ResourceManager

class Fireball : Skill(
    name = "화염구",
    baseCooldown = 0.9f,
    baseElement = Element.FIRE,
    baseDamageMul = 1.8f,
    hitEffectType = EffectType.FIREBALL_HIT,
    flyEffectType = EffectType.FIREBALL_FLY,
    baseIcon = ResourceManager.getFireballBase(),
    eventIcon = ResourceManager.getFireballEvent(),
    baseDescription = "화염구를 적에게 발사합니다."
) {

    override val tags: List<SkillTag> = listOf(SkillTag.FIRE, SkillTag.PROJECTILE, SkillTag.INSTANT)

    // Fireball 전용 등급 배율 (더 공격적인 성장)
    override fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.4f,
        SkillRank.A to 1.9f,
        SkillRank.S to 2.6f,
        SkillRank.SS to 3.4f,
        SkillRank.SSS to 4.5f
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
        onDamage: ((Int, Float, Float, Element, String) -> Unit)?
    ): Projectile {
        return Projectile(
            x = x,
            y = y,
            target = target,
            caster = caster,
            skill = this,
            preCalculatedDamage = preCalculatedDamage,
            chainCnt = chainCnt,
            beforeEnemies = beforeEnemies,
            onHit = { enemy ->
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
            },
            onDamage = onDamage
        )
    }


}
