package com.example.theorb.entities

import com.example.theorb.data.SaveData
import com.example.theorb.effects.Effect
import com.example.theorb.skills.Skill
import com.example.theorb.upgrades.InGameUpgradeManager
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.util.calcDamage
import com.example.theorb.util.dist2

class Player(
    var hp: Int = 100,
    var baseDamage: Int = 10,
    var baseRange: Float = 200f, // 적절한 사정거리
    val x: Float = 240f,
    var y: Float = 400f, // var로 변경하여 위치 조정 가능
    val skills: MutableList<Skill>,
    val saveData: SaveData
) {

    fun update(delta: Float, enemies: MutableList<Enemy>, projectiles: MutableList<Projectile>, effects: MutableList<Effect>, onDamage: ((Int, Float, Float, com.example.theorb.balance.Element) -> Unit)? = null) {
        // 업그레이드 적용된 쿨다운으로 스킬 업데이트 (영구 + 인게임 업그레이드)
        val cooldownMultiplier = InGameUpgradeManager.getCooldownMultiplier(saveData)
        skills.forEach { it.updateCooldown(delta / cooldownMultiplier) }

        val target = enemies
            .filter { !it.isDead() }
            .minByOrNull { dist2(it.x, it.y, x, y) }

        if (target != null) {
            // 업그레이드 적용된 사정거리 사용
            val effectiveRange = UpgradeManager.getEffectiveRange(saveData, baseRange)
            val d2 = dist2(target.x, target.y, x, y)

            // 사용 가능한 스킬 찾기
            for (skill in skills) {
                if (skill.canUse() && d2 <= effectiveRange * effectiveRange) {
                    val finDamage = calcDamage(target, this, skill)

                    // 오버킬 방지: 이미 죽을 예정이면 스킵
                    if (target.vhp <= 0) {
                        continue
                    }

                    target.vhp -= finDamage // 가상 hp 즉시 감소
                    projectiles.add(skill.createProjectile(x, y, target, this, finDamage, effects, onDamage))
                    skill.resetCooldown() // 스킬 개별 쿨다운 초기화
                    break // 한 번에 하나만 발사
                }
            }
        }
    }

    // 업그레이드가 적용된 데미지를 반환하는 함수
    fun getEffectiveDamage(): Int {
        return UpgradeManager.getEffectiveDamage(saveData, baseDamage)
    }
}
