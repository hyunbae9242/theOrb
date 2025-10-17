package com.example.theorb.skills

/**
 * 메인 스킬들의 태그를 정의
 */
object SkillTags {

    /**
     * 스킬 이름에 따른 태그 매핑 - 스킬 클래스의 tags 프로퍼티 사용
     */
    fun getSkillTags(skillName: String): List<SkillTag> {
        return try {
            val skill = SkillRegistry.createSkill(skillName)
            skill.tags
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    /**
     * 스킬 태그들을 문자열로 표시
     */
    fun getTagsDisplayString(skillName: String): String {
        val tags = getSkillTags(skillName)
        return if (tags.isNotEmpty()) {
            tags.joinToString(", ") { it.displayName }
        } else {
            "태그 없음"
        }
    }

    /**
     * 보조스킬이 메인스킬에 적용 가능한지 확인
     */
    fun canApplySubSkill(mainSkillName: String, subSkill: SubSkill): Boolean {
        val mainSkillTags = getSkillTags(mainSkillName)
        return subSkill.isCompatibleWith(mainSkillTags)
    }
}