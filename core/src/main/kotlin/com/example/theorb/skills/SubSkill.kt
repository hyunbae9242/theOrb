package com.example.theorb.skills

/**
 * 스킬 태그 (메인 스킬과 보조 스킬의 호환성을 결정)
 */
enum class SkillTag(val displayName: String) {
    FIRE("화염"),
    ICE("얼음"),
    LIGHTNING("번개"),
    DIVINE("신성"),
    DEMON("악마"),
    PROJECTILE("투사체"),
    AOE("범위"),
    DOT("지속 피해"),
    INSTANT("즉시 피해")
}

/**
 * 보조스킬 효과 카테고리 (데미지 계산 단계 구분)
 */
enum class EffectCategory {
    ADDITION,           // 추가 (기본 데미지에 더해짐)
    INCREASE,           // 증가 (백분율, 합산 후 곱연산)
    AMPLIFY,            // 증폭 (백분율, 곱연산)
    AILMENT_CHANCE,     // 상태이상 확률
    AILMENT_EFFECT,     // 상태이상 효과
    MECHANIC            // 게임 메카닉 (투사체 개수, 연쇄, 갈래 등)
}

/**
 * 보조스킬 효과 타입 (실제 효과)
 */
enum class SubSkillEffectType(
    val displayName: String,
    val category: EffectCategory,
    val unit: String = "", // 단위 (%, 개, 회 등)
    val targetStat: String = "" // 대상 스탯 (설명용)
) {
    // === 메카닉 ===
    PROJECTILE_COUNT("투사체 개수", EffectCategory.MECHANIC, "개"),
    PROJECTILE_CHAIN("투사체 연쇄", EffectCategory.MECHANIC, "회"),
    PROJECTILE_FORK("투사체 갈래", EffectCategory.MECHANIC, "개"),
    COOLDOWN("재사용 대기시간", EffectCategory.MECHANIC, "%"),

    // === 추가 ===
    DAMAGE_ADDITION("기본 피해 추가", EffectCategory.ADDITION, ""),
    FIRE_DAMAGE_ADDITION("화염 피해 추가", EffectCategory.ADDITION, ""),
    ICE_DAMAGE_ADDITION("냉기 피해 추가", EffectCategory.ADDITION, ""),
    LIGHTNING_DAMAGE_ADDITION("번개 피해 추가", EffectCategory.ADDITION, ""),

    // === 증가 ===
    DAMAGE_INCREASE("피해 증가", EffectCategory.INCREASE, "%"),
    FIRE_DAMAGE_INCREASE("화염 피해 증가", EffectCategory.INCREASE, "%"),
    ICE_DAMAGE_INCREASE("냉기 피해 증가", EffectCategory.INCREASE, "%"),
    LIGHTNING_DAMAGE_INCREASE("번개 피해 증가", EffectCategory.INCREASE, "%"),
    PROJECTILE_SPEED_INCREASE("투사체 속도 증가", EffectCategory.INCREASE, "%"),
    AOE_INCREASE("범위 증가", EffectCategory.INCREASE, "%"),

    // === 증폭 ===
    DAMAGE_AMPLIFY("피해 증폭", EffectCategory.AMPLIFY, "%"),
    CRITICAL_DAMAGE_AMPLIFY("치명타 피해 증폭", EffectCategory.AMPLIFY, "%"),

    // === 상태이상 확률 ===
    IGNITE_CHANCE("점화 확률", EffectCategory.AILMENT_CHANCE, "%"),
    FREEZE_CHANCE("빙결 확률", EffectCategory.AILMENT_CHANCE, "%"),
    SHOCK_CHANCE("감전 확률", EffectCategory.AILMENT_CHANCE, "%"),
    CRITICAL_CHANCE("치명타 확률", EffectCategory.AILMENT_CHANCE, "%"),

    // === 상태이상 효과 ===
    IGNITE_DAMAGE("점화 피해", EffectCategory.AILMENT_EFFECT, "%"),
    FREEZE_DURATION("빙결 지속시간", EffectCategory.AILMENT_EFFECT, "%"),
    SHOCK_EFFECT("감전 효과", EffectCategory.AILMENT_EFFECT, "%");

    /**
     * 값을 포맷팅 (부호 + 값 + 단위)
     */
    fun formatValue(value: Int): String {
        val sign = if (value > 0) "+" else ""
        return "$sign$value$unit"
    }

    /**
     * 설명 생성
     */
    fun getDescription(value: Int): String {
        return "$displayName ${formatValue(value)}"
    }
}

/**
 * 보조스킬 효과 (하나의 보조스킬이 여러 효과를 가질 수 있음)
 */
data class SubSkillEffect(
    val type: SubSkillEffectType,
    val levelBreakpoints: Map<Int, Int> // 레벨 -> 값 (양수/음수 모두 가능)
) {
    /**
     * 레벨에 따른 효과 값 계산
     */
    fun getValueForLevel(level: Int): Int {
        val clampedLevel = level.coerceIn(1, 10)

        // 해당 레벨 이하의 가장 큰 breakpoint 찾기
        val applicableLevel = levelBreakpoints.keys
            .filter { it <= clampedLevel }
            .maxOrNull() ?: 1

        return levelBreakpoints[applicableLevel] ?: 0
    }

    /**
     * 레벨에 따른 설명 생성
     */
    fun getDescriptionForLevel(level: Int): String {
        return type.getDescription(getValueForLevel(level))
    }
}

/**
 * 보조스킬 타입 (각 보조스킬은 여러 효과를 가질 수 있음)
 */
enum class SubSkillType(
    val displayName: String,
    val requiredTags: List<SkillTag>,
    val effects: List<SubSkillEffect>
) {
    // === 투사체 메카닉 보조스킬 ===
    PROJECTILE_COUNT(
        "다중 투사체",
        listOf(SkillTag.PROJECTILE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.PROJECTILE_COUNT,
                mapOf(1 to 1, 3 to 2, 5 to 3, 7 to 4, 10 to 5)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -20, 3 to -18, 5 to -15, 7 to -12, 10 to -10)
            )
        )
    ),

    PROJECTILE_CHAIN(
        "투사체 연쇄",
        listOf(SkillTag.PROJECTILE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.PROJECTILE_CHAIN,
                mapOf(1 to 1, 3 to 2, 5 to 3, 7 to 4, 10 to 5)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -30, 3 to -25, 5 to -20, 7 to -15, 10 to -10)
            )
        )
    ),

    PROJECTILE_FORK(
        "투사체 갈래",
        listOf(SkillTag.PROJECTILE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.PROJECTILE_FORK,
                mapOf(1 to 2, 4 to 3, 7 to 4, 10 to 5)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -25, 4 to -20, 7 to -15, 10 to -10)
            )
        )
    ),

    // === 화염 보조스킬 ===
    FIRE_DAMAGE(
        "화염 피해",
        listOf(SkillTag.FIRE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.FIRE_DAMAGE_INCREASE,
                mapOf(1 to 15, 3 to 25, 5 to 35, 7 to 50, 10 to 70)
            ),
            SubSkillEffect(
                SubSkillEffectType.COOLDOWN,
                mapOf(1 to 10, 3 to 8, 5 to 6, 7 to 4, 10 to 0)
            )
        )
    ),

    IGNITE(
        "점화",
        listOf(SkillTag.FIRE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.IGNITE_CHANCE,
                mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 40)
            ),
            SubSkillEffect(
                SubSkillEffectType.IGNITE_DAMAGE,
                mapOf(1 to 50, 3 to 70, 5 to 100, 7 to 130, 10 to 150)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -15, 3 to -12, 5 to -10, 7 to -5, 10 to 0)
            )
        )
    ),

    // === 냉기 보조스킬 ===
    ICE_DAMAGE(
        "냉기 피해",
        listOf(SkillTag.ICE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.ICE_DAMAGE_INCREASE,
                mapOf(1 to 15, 3 to 25, 5 to 35, 7 to 50, 10 to 70)
            ),
            SubSkillEffect(
                SubSkillEffectType.COOLDOWN,
                mapOf(1 to 10, 3 to 8, 5 to 6, 7 to 4, 10 to 0)
            )
        )
    ),

    FREEZE(
        "빙결",
        listOf(SkillTag.ICE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.FREEZE_CHANCE,
                mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 40)
            ),
            SubSkillEffect(
                SubSkillEffectType.FREEZE_DURATION,
                mapOf(1 to 50, 3 to 70, 5 to 100, 7 to 130, 10 to 150)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -15, 3 to -12, 5 to -10, 7 to -5, 10 to 0)
            )
        )
    ),

    // === 번개 보조스킬 ===
    LIGHTNING_DAMAGE(
        "번개 피해",
        listOf(SkillTag.LIGHTNING),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.LIGHTNING_DAMAGE_INCREASE,
                mapOf(1 to 15, 3 to 25, 5 to 35, 7 to 50, 10 to 70)
            ),
            SubSkillEffect(
                SubSkillEffectType.COOLDOWN,
                mapOf(1 to 10, 3 to 8, 5 to 6, 7 to 4, 10 to 0)
            )
        )
    ),

    SHOCK(
        "감전",
        listOf(SkillTag.LIGHTNING),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.SHOCK_CHANCE,
                mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 40)
            ),
            SubSkillEffect(
                SubSkillEffectType.SHOCK_EFFECT,
                mapOf(1 to 20, 3 to 30, 5 to 40, 7 to 50, 10 to 60)
            )
        )
    ),

    // === 범용 보조스킬 ===
    RAPID_CASTING(
        "신속 시전",
        emptyList(), // 모든 스킬에 적용 가능
        listOf(
            SubSkillEffect(
                SubSkillEffectType.COOLDOWN,
                mapOf(1 to -15, 3 to -20, 5 to -25, 7 to -30, 10 to -35)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -20, 3 to -18, 5 to -15, 7 to -12, 10 to -10)
            )
        )
    ),

    DAMAGE_FOCUS(
        "피해 집중",
        emptyList(),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to 20, 3 to 30, 5 to 40, 7 to 55, 10 to 70)
            ),
            SubSkillEffect(
                SubSkillEffectType.COOLDOWN,
                mapOf(1 to 15, 3 to 12, 5 to 10, 7 to 7, 10 to 5)
            )
        )
    ),

    CRITICAL_SUPPORT(
        "치명타",
        emptyList(),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.CRITICAL_CHANCE,
                mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 25, 10 to 30)
            ),
            SubSkillEffect(
                SubSkillEffectType.CRITICAL_DAMAGE_AMPLIFY,
                mapOf(1 to 20, 3 to 30, 5 to 50, 7 to 70, 10 to 100)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -10, 3 to -8, 5 to -5, 7 to -3, 10 to 0)
            )
        )
    ),

    SWIFT_PROJECTILE(
        "신속한 투사체",
        listOf(SkillTag.PROJECTILE),
        listOf(
            SubSkillEffect(
                SubSkillEffectType.PROJECTILE_SPEED_INCREASE,
                mapOf(1 to 30, 3 to 40, 5 to 50, 7 to 65, 10 to 80)
            ),
            SubSkillEffect(
                SubSkillEffectType.DAMAGE_INCREASE,
                mapOf(1 to -10, 3 to -8, 5 to -5, 7 to -3, 10 to 0)
            )
        )
    );

    /**
     * 레벨에 따른 모든 효과 설명 생성
     */
    fun getFullDescription(level: Int): String {
        return effects.joinToString("\n") { it.getDescriptionForLevel(level) }
    }

    /**
     * 특정 효과 타입의 값 가져오기
     */
    fun getEffectValue(effectType: SubSkillEffectType, level: Int): Int {
        return effects.find { it.type == effectType }?.getValueForLevel(level) ?: 0
    }

    /**
     * 모든 효과를 Map으로 가져오기 (effectType -> value)
     */
    fun getAllEffects(level: Int): Map<SubSkillEffectType, Int> {
        return effects.associate { it.type to it.getValueForLevel(level) }
    }
}

/**
 * 보조스킬 인스턴스 (실제로 보유하고 있는 보조스킬)
 */
data class SubSkill(
    val id: String,
    val type: SubSkillType,
    val level: Int = 1,
    val quality: SkillRank = SkillRank.C
) {
    val name: String get() = type.displayName
    val description: String get() = type.getFullDescription(level)
    val requiredTags: List<SkillTag> get() = type.requiredTags

    /**
     * 모든 효과 가져오기
     */
    fun getAllEffects(): Map<SubSkillEffectType, Int> {
        return type.getAllEffects(level)
    }

    /**
     * 특정 효과 값 가져오기
     */
    fun getEffectValue(effectType: SubSkillEffectType): Int {
        return type.getEffectValue(effectType, level)
    }

    /**
     * 메인 스킬과 호환되는지 확인
     */
    fun isCompatibleWith(mainSkillTags: List<SkillTag>): Boolean {
        if (requiredTags.isEmpty()) return true // 범용 보조스킬
        return requiredTags.any { it in mainSkillTags }
    }
}
