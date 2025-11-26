package com.example.theorb.skills

import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

class DivineNova : Skill(
    name = "성스러운파동",
    baseCooldown = 1.0f, // 2.0f → 1.0f (50% 감소)
    baseDamageMul = 2.5f, // AOE이므로 단일 타겟 대비 데미지 조정
    castEffectType = EffectType.DIVINE_NOVA_CAST, // 시전자 중심에서 한 번만 발동
    hitEffectType = EffectType.FIREBALL_HIT,
    flyEffectType = null, // AOE 스킬이므로 개별 projectile fly 효과 없음
    isInstant = true, // 즉발 스킬
    isAOE = true, // AOE 스킬
    baseDescription = "오브 주변의 모든 적에게 피해를 줍니다.",
    splashRadius = 150f, // DivineNova의 기본 범위
    splashDamageRatio = 1.0f // 범위 내 모든 적에게 100% 데미지
) {

    override val tags: List<SkillTag> = listOf(SkillTag.DIVINE, SkillTag.AOE, SkillTag.INSTANT)

    // DivineNova 전용 등급 배율 (AOE 스킬 특성)
    override fun getRankMultipliers(): Map<SkillRank, Float> = mapOf(
        SkillRank.C to 1.0f,
        SkillRank.B to 1.5f,
        SkillRank.A to 2.2f,
        SkillRank.S to 3.2f,
        SkillRank.SS to 4.5f,
        SkillRank.SSS to 6.0f
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
        // 시전자 위치에서 시전 효과 생성
        if (castEffectType != null) {
            effects.add(
                Effect(
                    EffectManager.load(castEffectType),
                    x,
                    y,
                    castEffectType.scale,
                    0f,
                    Anchor.CENTER,
                    0.5f // 50% 불투명도
                )
            )
        }

        // DivineNova는 즉발 AOE이지만, Projectile의 splash 시스템을 사용
        // 더미 타겟에 명중 즉시 splash 데미지 발동
        return Projectile(
            x = x,
            y = y,
            target = target,
            caster = caster,
            skill = this,
            preCalculatedDamage = preCalculatedDamage,
            speed = 999999f, // 즉시 명중
            chainCnt = chainCnt,
            beforeEnemies = beforeEnemies,
            onHit = { hitEnemy ->
                // 타겟 위치에 히트 효과 (중심 폭발)
                effects.add(
                    Effect(
                        EffectManager.load(hitEffectType),
                        hitEnemy.x,
                        hitEnemy.y,
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
