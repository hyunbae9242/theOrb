package com.example.theorb.skills

/**
 * 보조스킬 레벨업 시스템
 */
object SubSkillLevelSystem {
    /**
     * 레벨별 필요 경험치
     */
    fun getRequiredExp(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 20
            2 -> 50
            3 -> 100
            4 -> 150
            5 -> 200
            6 -> 250
            7 -> 300
            8 -> 400
            9 -> 500
            else -> Int.MAX_VALUE // 10레벨 이상은 레벨업 불가
        }
    }

    /**
     * 경험치 추가 후 레벨업 처리
     * @return 레벨업 여부
     */
    fun addExpAndCheckLevelUp(currentLevel: Int, currentExp: Int, addedExp: Int): Pair<Int, Int> {
        var level = currentLevel
        var exp = currentExp + addedExp

        // 최대 레벨 체크
        if (level >= 10) {
            return Pair(10, 0)
        }

        // 레벨업 처리 (여러 레벨 한번에 올라갈 수 있음)
        while (level < 10) {
            val requiredExp = getRequiredExp(level)
            if (exp >= requiredExp) {
                exp -= requiredExp
                level++
            } else {
                break
            }
        }

        return Pair(level, exp)
    }
}

/**
 * 보조스킬 인벤토리 데이터
 */
data class SubSkillInventoryItem(
    val type: SubSkillType,
    var level: Int = 1,
    var exp: Int = 0
) {
    /**
     * 경험치 추가 및 레벨업
     * @return 레벨업 여부
     */
    fun addExp(amount: Int): Boolean {
        val oldLevel = level
        val result = SubSkillLevelSystem.addExpAndCheckLevelUp(level, exp, amount)
        level = result.first
        exp = result.second
        return level > oldLevel
    }

    /**
     * 다음 레벨까지 필요한 경험치
     */
    fun getRequiredExpForNextLevel(): Int {
        if (level >= 10) return 0
        return SubSkillLevelSystem.getRequiredExp(level)
    }

    /**
     * 진행률 퍼센트 (0~100)
     */
    fun getExpProgress(): Float {
        if (level >= 10) return 100f
        val required = getRequiredExpForNextLevel()
        if (required == 0) return 100f
        return (exp.toFloat() / required.toFloat()) * 100f
    }
}
