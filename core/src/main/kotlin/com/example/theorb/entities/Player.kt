package com.example.theorb.entities

import com.example.theorb.data.SaveData
import com.example.theorb.effects.Effect
import com.example.theorb.skills.Skill
import com.example.theorb.calculation.CooldownCalculator
import com.example.theorb.upgrades.UpgradeManager

class Player(
    var hp: Int = 100,
    var baseDamage: Int = 10,
    var baseRange: Float = 130f, // 적절한 사정거리
    val x: Float = 240f,
    var y: Float = 400f, // var로 변경하여 위치 조정 가능
    val skills: MutableList<Skill>,
    val saveData: SaveData
) {

    fun update(
        delta: Float,
        enemies: MutableList<Enemy>,
        projectiles: MutableList<Projectile>,
        effects: MutableList<Effect>,
        onDamage: ((Int, Float, Float, String) -> Unit)? = null,
        onSkillCast: (() -> Unit)? = null
    ) {
        // 업그레이드 적용된 쿨다운으로 스킬 업데이트 (레벨업 + 영구 업그레이드)
        val cooldownMultiplier = CooldownCalculator.getCooldownMultiplier(saveData)
        skills.forEach { it.updateCooldown(delta / cooldownMultiplier) }

        // 업그레이드 적용된 사정거리 사용
        val effectiveRange = UpgradeManager.getEffectiveRange(saveData, baseRange)

        // 사용 가능한 스킬 찾기
        for (skill in skills) {
            if (ActionFactory.playerCastSkill(skill, enemies, x, y, effectiveRange, projectiles, this, effects, onDamage)) {
                // 스킬 시전 성공 시 콜백 호출
                onSkillCast?.invoke()
                break
            }
        }
    }
}
