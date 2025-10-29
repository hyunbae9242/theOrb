package com.example.theorb.skills

/**
 * 스킬 등급별 보조스킬 장착 가능 개수 관리
 */
object SubSkillSlots {

    /**
     * 메인 스킬 등급에 따른 보조스킬 장착 가능 개수
     */
    fun getMaxSubSkillSlots(skillRank: SkillRank): Int {
        return when (skillRank) {
            SkillRank.C -> 0      // C등급: 보조스킬 장착 불가
            SkillRank.B -> 1      // B등급: 1개
            SkillRank.A -> 2      // A등급: 2개
            SkillRank.S -> 3      // S등급: 3개
            SkillRank.SS -> 3     // SS등급: 3개
            SkillRank.SSS -> 4    // SSS등급: 4개 (최대)
        }
    }
}
