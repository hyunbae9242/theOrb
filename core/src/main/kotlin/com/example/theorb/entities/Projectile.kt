package com.example.theorb.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.skills.Skill
import com.example.theorb.skills.SubSkillEffectType
import com.example.theorb.skills.SubSkillType
import kotlin.math.atan2
import kotlin.math.sqrt

class Projectile(
    var x: Float,
    var y: Float,
    val target: Enemy,
    val caster: Player,
    val skill: Skill,
    val preCalculatedDamage: Int, // 미리 계산된 데미지
    val speed: Float = 500f,
    var chainCnt: Int = 0,
    val beforeEnemies: MutableList<Enemy> = mutableListOf(),
    val onHit: ((Enemy) -> Unit)? = null,
    val onDamage: ((Int, Float, Float, String) -> Unit)? = null
) {
    var alive = true
    private var effect: Effect? = null

    init {
        if (skill.flyEffectType != null) {
            effect = Effect(
                EffectManager.load(skill.flyEffectType),
                x, y, scale = skill.flyEffectType.scale
            )
        }
    }

    fun update(delta: Float, enemies: MutableList<Enemy>, projectiles: MutableList<Projectile>, effects: MutableList<Effect>) {
        if (!alive || target.isDead()) {
            alive = false
            return
        }

        val dx = target.x - x
        val dy = target.y - y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist < target.type.radius) {
            // 스플래시 데미지 처리
            if (skill.splashRadius > 0f && skill.splashDamageRatio > 0f) {
                // splashDamageRatio가 1.0이면 순수 AOE (메인 타겟 제외 없음)
                // splashDamageRatio가 1.0 미만이면 메인 타겟 100%, 주변 감소
                if (skill.splashDamageRatio >= 1.0f) {
                    // 순수 AOE: 범위 내 모든 적에게 동일 데미지
                    applySplashDamage(enemies, target, includeMainTarget = true)
                } else {
                    // 메인 타겟에게 100% 데미지
                    target.hp -= preCalculatedDamage
                    onDamage?.invoke(preCalculatedDamage, target.x, target.y, skill.name)

                    // 주변에 감소된 데미지
                    applySplashDamage(enemies, target, includeMainTarget = false)
                }
            } else {
                // 스플래시 없는 단일 타겟 공격
                target.hp -= preCalculatedDamage
                onDamage?.invoke(preCalculatedDamage, target.x, target.y, skill.name)
            }

            onHit?.invoke(target) // 히트 이펙트 실행
            alive = false

            // 투사체 보조 기능 실행
            for(subSkill in skill.equippedSubSkills) {
                val subSkillType = subSkill.type
                if (subSkillType == SubSkillType.PROJECTILE_FORK && beforeEnemies.size < 2) {
                    // 갈래는 첫 투사체만 적용
                    val maxForkCnt = subSkill.getEffectValue(SubSkillEffectType.PROJECTILE_FORK)
                    repeat(maxForkCnt) {
                        ActionFactory.castProjectileToTarget(skill, enemies, x, y, 300f, projectiles, caster, effects, chainCnt + 1, beforeEnemies = beforeEnemies, onDamage)
                    }
                    break
                } else if (subSkillType == SubSkillType.PROJECTILE_CHAIN) {
                    val maxChainCnt = subSkill.getEffectValue(SubSkillEffectType.PROJECTILE_CHAIN)
                    if (maxChainCnt > chainCnt) {
                        ActionFactory.castProjectileToTarget(skill, enemies, x, y, 300f, projectiles, caster, effects, chainCnt + 1, beforeEnemies = beforeEnemies, onDamage)
                    }
                }
            }
        } else {
            x += dx / dist * speed * delta
            y += dy / dist * speed * delta
        }

        effect?.apply {
            update(delta)
            setPosition(this@Projectile.x, this@Projectile.y)
            setRotation(Math.toDegrees(atan2(dy, dx).toDouble()).toFloat())
        }
    }

    fun draw(batch: SpriteBatch) {
        effect?.draw(batch)
    }

    /**
     * 스플래시 데미지 적용 (명중한 적 주변의 다른 적들에게)
     * @param includeMainTarget true면 메인 타겟도 splash 범위로 계산 (DivineNova 등 순수 AOE)
     */
    private fun applySplashDamage(enemies: MutableList<Enemy>, hitEnemy: Enemy, includeMainTarget: Boolean) {
        val splashDamage = (preCalculatedDamage * skill.splashDamageRatio).toInt()
        val effectiveSplashRadius = skill.getModifiedSplashRadius() // 보조스킬 효과 적용된 범위

        enemies.forEach { enemy ->
            // 메인 타겟 처리
            if (enemy == hitEnemy) {
                if (includeMainTarget) {
                    // 순수 AOE: 메인 타겟도 splash 데미지 적용
                    enemy.hp -= splashDamage
                    onDamage?.invoke(splashDamage, enemy.x, enemy.y, skill.name)
                }
                return@forEach
            }

            // 이미 죽은 적은 제외
            if (enemy.isDead()) return@forEach

            // 거리 계산
            val dx = enemy.x - hitEnemy.x
            val dy = enemy.y - hitEnemy.y
            val distance = sqrt(dx * dx + dy * dy)

            // 스플래시 범위 내에 있으면 데미지 적용 (보조스킬 효과 반영)
            if (distance <= effectiveSplashRadius) {
                enemy.hp -= splashDamage
                onDamage?.invoke(splashDamage, enemy.x, enemy.y, skill.name)
            }
        }
    }
}
