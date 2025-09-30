package com.example.theorb.skills

data class SkillItem(
    val skillType: String, // 스킬 타입 (예: "Fireball", "IceLance" 등)
    val rank: SkillRank,
    val id: String = java.util.UUID.randomUUID().toString() // 고유 ID
) {
    fun getDisplayName(): String = "${rank.displayName} ${skillType}"

    fun canUpgrade(): Boolean = rank.canUpgrade()

    fun getUpgradeRequirement(): Int = rank.upgradeRequirement
}
