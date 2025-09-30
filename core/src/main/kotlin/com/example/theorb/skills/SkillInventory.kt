package com.example.theorb.skills

import com.example.theorb.data.SaveManager

class SkillInventory {
    private val skills = mutableListOf<SkillItem>()

    fun addSkill(skillType: String, rank: SkillRank = SkillRank.C): SkillItem {
        val skillItem = SkillItem(skillType, rank)
        skills.add(skillItem)
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

    fun getSkillsByTypeAndRank(skillType: String, rank: SkillRank): List<SkillItem> {
        return skills.filter { it.skillType == skillType && it.rank == rank }
    }

    fun canUpgrade(skillType: String, rank: SkillRank): Boolean {
        if (!rank.canUpgrade()) return false
        val sameRankSkills = getSkillsByTypeAndRank(skillType, rank)
        return sameRankSkills.size >= rank.upgradeRequirement
    }

    fun upgradeSkill(skillType: String, rank: SkillRank): SkillItem? {
        if (!canUpgrade(skillType, rank)) return null

        val nextRank = rank.getNextRank() ?: return null
        val sameRankSkills = getSkillsByTypeAndRank(skillType, rank)
        val requiredSkills = sameRankSkills.take(rank.upgradeRequirement)

        // 필요한 스킬들을 제거
        removeSkills(requiredSkills)

        // 업그레이드된 스킬 추가
        return addSkill(skillType, nextRank)
    }

    fun getSkillCount(skillType: String, rank: SkillRank): Int {
        return getSkillsByTypeAndRank(skillType, rank).size
    }

    fun getUniqueSkillTypes(): Set<String> {
        return skills.map { it.skillType }.toSet()
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
                "id" to skill.id
            )
        }
    }

    fun fromSaveData(data: List<Map<String, Any>>) {
        skills.clear()
        data.forEach { skillData ->
            val skillType = skillData["skillType"] as String
            val rankName = skillData["rank"] as String
            val id = skillData["id"] as String
            val rank = SkillRank.valueOf(rankName)
            skills.add(SkillItem(skillType, rank, id))
        }
    }
}