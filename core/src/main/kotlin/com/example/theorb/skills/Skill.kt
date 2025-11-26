package com.example.theorb.skills

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectType
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile
import com.example.theorb.util.calcDamage
import com.example.theorb.util.dist2

abstract class Skill(
    val name: String,
    val baseCooldown: Float,
    val baseDamageMul: Float,
    val castEffectType: EffectType? = null,
    val flyEffectType: EffectType? = null,
    val hitEffectType: EffectType,
    val isInstant: Boolean = false, // 즉발 스킬 여부
    val isAOE: Boolean = false, // AOE 스킬 여부
    var rank: SkillRank = SkillRank.C, // 기본 등급은 C
    val baseDescription: String? = null,
    val baseIcon: TextureRegionDrawable? = null,
    val eventIcon: TextureRegionDrawable? = null,
    val splashRadius: Float = 0f, // 스플래시 범위 (0이면 스플래시 없음)
    val splashDamageRatio: Float = 0f // 스플래시 데미지 비율 (0~1)
) {

    // 각 스킬이 가진 태그들 (하위 클래스에서 구현)
    abstract val tags: List<SkillTag>

    // 장착된 보조스킬 리스트
    var equippedSubSkills: List<SubSkill> = emptyList()

    // 장착된 보조스킬 효과 통합 맵 (effectType -> 합산된 value)
    private var _aggregatedEffects: Map<SubSkillEffectType, Int> = emptyMap()

    /**
     * 보조스킬 장착 시 효과 집계
     */
    fun updateSubSkillEffects(subSkills: List<SubSkill>) {
        equippedSubSkills = subSkills

        // 모든 보조스킬의 효과를 합산
        val effectsMap = mutableMapOf<SubSkillEffectType, Int>()
        subSkills.forEach { subSkill ->
            subSkill.getAllEffects().forEach { (effectType, value) ->
                effectsMap[effectType] = (effectsMap[effectType] ?: 0) + value
            }
        }
        _aggregatedEffects = effectsMap
    }

    /**
     * 특정 효과 값 가져오기
     */
    fun getEffectValue(effectType: SubSkillEffectType): Int {
        return _aggregatedEffects[effectType] ?: 0
    }

    // 스킬설명
    fun getDescription(): String {
        return "쿨타임: $baseCooldown 초\n$baseDescription"
    }

    /**
     * 보조스킬 효과가 적용된 투사체 개수 계산
     */
    fun getProjectileCount(): Int {
        return 1 + getEffectValue(SubSkillEffectType.PROJECTILE_COUNT)
    }

    /**
     * 보조스킬 효과가 적용된 투사체 연쇄 횟수
     */
    fun getChainCount(): Int {
        return getEffectValue(SubSkillEffectType.PROJECTILE_CHAIN)
    }

    /**
     * 보조스킬 효과가 적용된 투사체 갈래 개수
     */
    fun getForkCount(): Int {
        return getEffectValue(SubSkillEffectType.PROJECTILE_FORK)
    }

    /**
     * 보조스킬 효과가 적용된 쿨다운 계산
     */
    fun getModifiedCooldown(): Float {
        val cooldownPercent = getEffectValue(SubSkillEffectType.COOLDOWN) // -35% ~ +15%
        return baseCooldown * (1f + cooldownPercent / 100f)
    }

    /**
     * 보조스킬 효과가 적용된 AOE/스플래시 범위 계산
     */
    fun getModifiedSplashRadius(): Float {
        if (splashRadius <= 0f) return 0f
        val aoeIncrease = getEffectValue(SubSkillEffectType.AOE_INCREASE) // +20% ~ +80%
        return splashRadius * (1f + aoeIncrease / 100f)
    }

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
    fun resetCooldown() {
        cooldownTimer = getModifiedCooldown() // 보조스킬 효과 적용된 쿨다운
    }
    fun updateCooldown(delta: Float) { cooldownTimer -= delta }

    abstract fun createProjectile(
        x: Float,
        y: Float,
        target: Enemy,
        caster: Player,
        preCalculatedDamage: Int,
        effects: MutableList<Effect>,
        chainCnt: Int = 0,
        beforeEnemies: MutableList<Enemy> = mutableListOf(),
        onDamage: ((Int, Float, Float, String) -> Unit)? = null
    ): Projectile

    // AOE 스킬용 메소드
    open fun createAOEProjectiles(
        x: Float,
        y: Float,
        targets: List<Enemy>,
        caster: Player,
        effects: MutableList<Effect>,
        onDamage: ((Int, Float, Float, String) -> Unit)? = null
    ): List<Projectile> {
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
            createProjectile(x, y, target, caster, damage, effects, onDamage = onDamage)
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
