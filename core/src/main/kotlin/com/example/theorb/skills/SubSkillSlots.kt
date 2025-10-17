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

    /**
     * 스킬 ID에서 등급을 추출하여 보조스킬 슬롯 개수 반환
     */
    fun getMaxSubSkillSlots(skillId: String): Int {
        val parts = skillId.split(":")
        if (parts.size != 2) return 0

        val rankString = parts[1]
        return try {
            val rank = SkillRank.valueOf(rankString)
            getMaxSubSkillSlots(rank)
        } catch (e: IllegalArgumentException) {
            0 // 잘못된 등급일 경우 0개
        }
    }

    /**
     * 특정 메인스킬에 보조스킬을 장착할 수 있는지 확인
     */
    fun canEquipSubSkill(
        mainSkillId: String,
        currentSubSkills: List<String>, // 현재 장착된 보조스킬 ID들
        newSubSkillId: String
    ): Boolean {
        val maxSlots = getMaxSubSkillSlots(mainSkillId)

        // 이미 같은 보조스킬이 장착되어 있는지 확인
        if (newSubSkillId in currentSubSkills) {
            return false
        }

        // 슬롯 여유가 있는지 확인
        return currentSubSkills.size < maxSlots
    }

    /**
     * 등급별 슬롯 개수 정보를 문자열로 반환 (UI 표시용)
     */
    fun getSlotInfoText(skillRank: SkillRank): String {
        val slotCount = getMaxSubSkillSlots(skillRank)
        return when (slotCount) {
            0 -> "보조스킬 장착 불가"
            1 -> "보조스킬 1개 장착 가능"
            else -> "보조스킬 ${slotCount}개 장착 가능"
        }
    }

    /**
     * 현재 장착 상태 텍스트 반환
     */
    fun getCurrentSlotStatusText(skillId: String, equippedSubSkills: List<String>): String {
        val maxSlots = getMaxSubSkillSlots(skillId)
        val currentCount = equippedSubSkills.size

        return if (maxSlots == 0) {
            "보조스킬 장착 불가"
        } else {
            "$currentCount/$maxSlots 보조스킬 장착됨"
        }
    }
}