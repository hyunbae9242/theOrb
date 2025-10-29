package com.example.theorb.skills

import com.example.theorb.data.SaveManager

class SkillInventory {
    private val skills = mutableListOf<SkillItem>()

    fun addSkill(skillType: String, rank: SkillRank = SkillRank.C, exp: Int = 1): SkillItem {
        val skillItem = SkillItem(skillType, rank, exp)
        var isInventorySkill = false
        for (skill in skills) {
            if (skill.isEqualSkill(skillItem)) {
                isInventorySkill = true
                skill.exp += skillItem.exp
                skill.updateRank()
                break
            }
        }
        if (!isInventorySkill) skills.add(skillItem)
        return skillItem
    }

    fun removeSkill(skillItem: SkillItem): Boolean {
        return skills.remove(skillItem)
    }

    fun removeSkills(skillItems: List<SkillItem>): Boolean {
        return skills.removeAll(skillItems.toSet())
    }

    fun getAllSkills(): List<SkillItem> = skills.toList()

    fun getSkillsByType(skillType: String): List<SkillItem> {
        return skills.filter { it.skillType == skillType }
    }

    fun getRankByType(skillType: String): SkillRank {
        return skills.find {it.skillType == skillType}?.rank ?: SkillRank.C
    }

    fun clear() {
        skills.clear()
    }

    // 저장/로드용 데이터 변환
    fun toSaveData(): List<Map<String, Any>> {
        return skills.map { skill ->
            mapOf(
                "skillType" to skill.skillType,
                "rank" to skill.rank.name,
                "exp" to skill.exp
            )
        }
    }

    fun fromSaveData(data: List<Map<String, Any>>) {
        skills.clear()
        data.forEach { skillData ->
            val skillType = skillData["skillType"] as String
            val rankName = skillData["rank"] as String
            val exp = skillData["exp"] as Int
            val rank = SkillRank.valueOf(rankName)
            skills.add(SkillItem(skillType, rank, exp))
        }
    }
}
