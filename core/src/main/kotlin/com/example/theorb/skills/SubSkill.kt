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
 * 보조스킬 타입
 */
enum class SubSkillType(
    val displayName: String,
    val descriptionTemplate: String, // {value}를 포함한 템플릿
    val requiredTags: List<SkillTag>,
    val effectType: SubSkillEffectType,
    val levelBreakpoints: Map<Int, Int> // 레벨 -> 값 (예: 1->1, 3->2, 5->3 등)
) {
    // 투사체 관련
    PROJECTILE_COUNT(
        "투사체 증가",
        "투사체 개수를 {value}개 증가시킵니다.",
        listOf(SkillTag.PROJECTILE),
        SubSkillEffectType.PROJECTILE_COUNT,
        mapOf(
            1 to 1,   // 1레벨: +1개
            3 to 2,   // 3레벨: +2개
            5 to 3,   // 5레벨: +3개
            7 to 4,   // 7레벨: +4개
            10 to 5   // 10레벨: +5개
        )
    ),

    PROJECTILE_CHAIN(
        "투사체 연쇄",
        "투사체가 적 처치 시 {value}회 연쇄하여 근처 적을 공격합니다.",
        listOf(SkillTag.PROJECTILE),
        SubSkillEffectType.PROJECTILE_CHAIN,
        mapOf(1 to 1, 3 to 2, 5 to 3, 7 to 4, 10 to 5)
    ),

    PROJECTILE_FORK(
        "투사체 갈래",
        "투사체가 적 적중 시 {value}개로 갈라져 다른 적을 공격합니다.",
        listOf(SkillTag.PROJECTILE),
        SubSkillEffectType.PROJECTILE_FORK,
        mapOf(1 to 2, 4 to 3, 7 to 4, 10 to 5)
    ),

    // 화염 관련
    FIRE_DAMAGE(
        "화염 피해 증가",
        "화염 피해를 {value}% 증가시킵니다.",
        listOf(SkillTag.FIRE),
        SubSkillEffectType.FIRE_DAMAGE,
        mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 50)
    ),

    IGNITE_CHANCE(
        "점화 확률",
        "적을 점화시킬 확률 {value}%를 부여합니다.",
        listOf(SkillTag.FIRE),
        SubSkillEffectType.IGNITE_CHANCE,
        mapOf(1 to 5, 3 to 10, 5 to 15, 7 to 20, 10 to 30)
    ),

    // 얼음 관련
    ICE_DAMAGE(
        "냉기 피해 증가",
        "냉기 피해를 {value}% 증가시킵니다.",
        listOf(SkillTag.ICE),
        SubSkillEffectType.ICE_DAMAGE,
        mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 50)
    ),

    FREEZE_CHANCE(
        "빙결 확률",
        "적을 빙결시킬 확률 {value}%를 부여합니다.",
        listOf(SkillTag.ICE),
        SubSkillEffectType.FREEZE_CHANCE,
        mapOf(1 to 5, 3 to 10, 5 to 15, 7 to 20, 10 to 30)
    ),

    // 번개 관련
    LIGHTNING_DAMAGE(
        "번개 피해 증가",
        "번개 피해를 {value}% 증가시킵니다.",
        listOf(SkillTag.LIGHTNING),
        SubSkillEffectType.LIGHTNING_DAMAGE,
        mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 50)
    ),

    SHOCK_CHANCE(
        "감전 확률",
        "적을 감전시킬 확률 {value}%를 부여합니다.",
        listOf(SkillTag.LIGHTNING),
        SubSkillEffectType.SHOCK_CHANCE,
        mapOf(1 to 5, 3 to 10, 5 to 15, 7 to 20, 10 to 30)
    ),

    // 범용
    DAMAGE_INCREASE(
        "피해 증가",
        "모든 피해를 {value}% 증가시킵니다.",
        emptyList(), // 모든 스킬에 적용 가능
        SubSkillEffectType.DAMAGE_INCREASE,
        mapOf(1 to 10, 3 to 15, 5 to 20, 7 to 30, 10 to 50)
    ),

    COOLDOWN_REDUCTION(
        "재사용 대기시간 단축",
        "스킬 재사용 대기시간을 {value}% 단축시킵니다.",
        emptyList(), // 모든 스킬에 적용 가능
        SubSkillEffectType.COOLDOWN_REDUCTION,
        mapOf(1 to 5, 3 to 10, 5 to 15, 7 to 20, 10 to 30)
    );

    /**
     * 레벨에 따른 효과 값 계산
     */
    fun getValueForLevel(level: Int): Int {
        // 레벨이 1~10 범위를 벗어나면 보정
        val clampedLevel = level.coerceIn(1, 10)

        // 해당 레벨 이하의 가장 큰 breakpoint 찾기
        val applicableLevel = levelBreakpoints.keys
            .filter { it <= clampedLevel }
            .maxOrNull() ?: 1

        return levelBreakpoints[applicableLevel] ?: levelBreakpoints[1] ?: 1
    }

    /**
     * 값에 따라 설명 생성
     */
    fun getDescription(value: Int): String {
        return descriptionTemplate.replace("{value}", value.toString())
    }

    /**
     * 레벨에 따른 설명 생성
     */
    fun getDescriptionForLevel(level: Int): String {
        return getDescription(getValueForLevel(level))
    }
}

/**
 * 보조스킬 효과 타입 (실제 게임플레이 효과)
 */
enum class SubSkillEffectType {
    PROJECTILE_COUNT,
    PROJECTILE_CHAIN,
    PROJECTILE_FORK,
    FIRE_DAMAGE,
    IGNITE_CHANCE,
    ICE_DAMAGE,
    FREEZE_CHANCE,
    LIGHTNING_DAMAGE,
    SHOCK_CHANCE,
    DAMAGE_INCREASE,
    COOLDOWN_REDUCTION
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
    val value: Int get() = type.getValueForLevel(level)
    val description: String get() = type.getDescriptionForLevel(level)
    val requiredTags: List<SkillTag> get() = type.requiredTags
    val effectType: SubSkillEffectType get() = type.effectType

    /**
     * 메인 스킬과 호환되는지 확인
     */
    fun isCompatibleWith(mainSkillTags: List<SkillTag>): Boolean {
        if (requiredTags.isEmpty()) return true // 범용 보조스킬
        return requiredTags.any { it in mainSkillTags }
    }
}