package com.example.theorb.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.skills.Skill
import com.example.theorb.util.calcDamage
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
    val onHit: ((Enemy) -> Unit)? = null,
    val onDamage: ((Int, Float, Float, com.example.theorb.balance.Element) -> Unit)? = null
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

    fun update(delta: Float) {
        if (!alive || target.isDead()) {
            alive = false
            return
        }

        val dx = target.x - x
        val dy = target.y - y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist < target.type.radius) {
            target.hp -= preCalculatedDamage // 미리 계산된 데미지 적용
            onDamage?.invoke(preCalculatedDamage, target.x, target.y, skill.baseElement) // 데미지 텍스트 생성
            onHit?.invoke(target) // 히트 이펙트 실행
            alive = false
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
}
