package com.example.theorb.skills

import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

class IceLance : Skill(
    name = "얼음창",
    baseCooldown = 0.4f, // 0.75f → 0.4f (약 47% 감소)
    baseDamageMul = 1.6f,
    hitEffectType = EffectType.ICE_LANCE_HIT,
    flyEffectType = EffectType.ICE_LANCE_FLY,
    baseDescription = "얼음창을 적에게 발사합니다. 명중한 적은 2초간 빙결되어 움직이지 못합니다."
) {

    override val tags: List<SkillTag> = listOf(SkillTag.ICE, SkillTag.PROJECTILE, SkillTag.INSTANT)

    // IceLance 전용 등급 배율 (균형잡힌 성장)
    override fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.35f,
        SkillRank.A to 1.8f,
        SkillRank.S to 2.4f,
        SkillRank.SS to 3.1f,
        SkillRank.SSS to 4.0f
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
            x = x,
            y = y,
            target = target,
            caster = caster,
            skill = this,
            preCalculatedDamage = preCalculatedDamage,
            chainCnt = chainCnt,
            beforeEnemies = beforeEnemies,
            onHit = { enemy ->
                // 히트 이펙트
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
                // 빙결 효과 적용 (2초)
                enemy.applyFreeze(2.0f)
            },
            onDamage = onDamage
        )
    }


}
