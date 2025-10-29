package com.example.theorb.skills

data class SkillItem(
    val skillType: String, // 스킬 타입 (예: "Fireball", "IceLance" 등)
    var rank: SkillRank = SkillRank.C,
    var exp: Int = 1
) {
    fun getDisplayName(): String = "${rank.displayName} ${skillType}"

    fun canUpgrade(): Boolean = rank.canUpgrade()

    fun getUpgradeRequirement(): Int = rank.upgradeRequirement
    fun isEqualSkill(other: SkillItem): Boolean = skillType == other.skillType

    fun updateRank() {
        rank = rank.getRankByExp(exp)
    }
}
