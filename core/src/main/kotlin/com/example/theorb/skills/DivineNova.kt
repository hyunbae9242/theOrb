package com.example.theorb.skills

import com.example.theorb.balance.Element
import com.example.theorb.effects.Anchor
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile

class DivineNova : Skill(
    name = "성스러운파동",
    baseCooldown = 2.0f,
    baseElement = Element.ANGEL,
    damageMul = 2.5f, // AOE이므로 단일 타겟 대비 데미지 조정
    castEffectType = EffectType.DIVINE_NOVA_FLY, // 시전자 중심에서 한 번만 발동
    hitEffectType = EffectType.FIREBALL_HIT,
    flyEffectType = null, // AOE 스킬이므로 개별 projectile fly 효과 없음
    isInstant = true, // 즉발 스킬
    isAOE = true // AOE 스킬
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

    override fun createAOEProjectiles(x: Float, y: Float, targets: List<Enemy>, caster: Player, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, com.example.theorb.balance.Element, String) -> Unit)?): List<Projectile> {
        // 시전자 위치에서 시전 효과 생성 (한 번만 발동)
        if (castEffectType != null) {
            effects.add(
                Effect(
                    EffectManager.load(castEffectType),
                    x,
                    y,
                    castEffectType.scale,
                    0f,
                    Anchor.CENTER
                )
            )
        }

        // 각 타겟에 대해 즉시 피해 적용 (즉발 AOE)
        return targets.map { target ->
            val damage = com.example.theorb.util.calcDamage(target, caster, this)
            target.vhp -= damage // 가상 hp 감소

            // 즉시 피해 적용 및 효과 생성
            target.hp -= damage
            onDamage?.invoke(damage, target.x, target.y, baseElement, name)

            // 타겟 위치에 히트 효과 생성
            effects.add(
                Effect(
                    EffectManager.load(hitEffectType),
                    target.x,
                    target.y,
                    hitEffectType.scale,
                    0f,
                    Anchor.CENTER
                )
            )

            // 더미 프로젝타일 반환 (즉시 비활성화됨)
            Projectile(target.x, target.y, target, caster, this, damage, onDamage = null).apply {
                alive = false
            }
        }
    }


}
