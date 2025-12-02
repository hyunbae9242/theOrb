package com.example.theorb.balance

import com.badlogic.gdx.graphics.Color

/**
 * 레벨업 선택지 티어
 */
enum class UpgradeTier(val displayName: String, val color: Color) {
    NORMAL("Normal", Color.WHITE),
    RARE("Rare", Color(0.3f, 0.5f, 1f, 1f)), // 파란색
    UNIQUE("Unique", Color(1f, 0.8f, 0f, 1f)) // 금색
}

/**
 * 레벨업 선택지 타입
 */
enum class LevelUpOptionType(
    val displayName: String,
    val tier: UpgradeTier,
    val maxStack: Int, // 최대 중첩 횟수
    val description: (currentStack: Int) -> String // 현재 중첩에 따른 설명
) {
    // === 1. 공격력 ===
    DAMAGE_NORMAL(
        "공격력 증가",
        UpgradeTier.NORMAL,
        5,
        { stack -> "공격력이 ${10 * (stack + 1)}% 증가합니다 (${stack}/5)" }
    ),
    DAMAGE_RARE(
        "공격력 증가",
        UpgradeTier.RARE,
        3,
        { stack -> "공격력이 ${50 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    DAMAGE_UNIQUE(
        "공격력 증가",
        UpgradeTier.UNIQUE,
        3,
        { stack -> "공격력이 ${100 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),

    // === 2. 쿨다운 감소 (선택지 상한선 -50%, 최종 상한선 -80%) ===
    COOLDOWN_NORMAL(
        "쿨다운 감소",
        UpgradeTier.NORMAL,
        3,
        { stack -> "쿨다운이 ${3 * (stack + 1)}% 감소합니다 (${stack}/3)" }
    ),
    COOLDOWN_RARE(
        "쿨다운 감소",
        UpgradeTier.RARE,
        3,
        { stack -> "쿨다운이 ${5 * (stack + 1)}% 감소합니다 (${stack}/3)" }
    ),
    COOLDOWN_UNIQUE(
        "쿨다운 감소",
        UpgradeTier.UNIQUE,
        2,
        { stack -> "쿨다운이 ${10 * (stack + 1)}% 감소합니다 (${stack}/2)" }
    ),

    // === 3. 흡혈 ===
    LIFESTEAL_RARE(
        "생명력 흡수",
        UpgradeTier.RARE,
        1,
        { _ -> "명중 데미지의 0.5%만큼 체력을 회복합니다" }
    ),
    LIFESTEAL_UNIQUE(
        "생명력 흡수",
        UpgradeTier.UNIQUE,
        1,
        { _ -> "명중 데미지의 1%만큼 체력을 회복합니다" }
    ),

    // === 4. 에너지 쉴드 ===
    ENERGY_SHIELD_RARE(
        "에너지 쉴드",
        UpgradeTier.RARE,
        1,
        { _ -> "스킬 시전 시 1의 에너지 쉴드를 획득합니다" }
    ),
    ENERGY_SHIELD_UNIQUE(
        "에너지 쉴드",
        UpgradeTier.UNIQUE,
        1,
        { _ -> "스킬 시전 시 2의 에너지 쉴드를 획득합니다" }
    ),

    // === 5. 체력 증가 ===
    HP_NORMAL(
        "체력 증가",
        UpgradeTier.NORMAL,
        3,
        { stack -> "최대 체력이 ${10 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    HP_RARE(
        "체력 증가",
        UpgradeTier.RARE,
        3,
        { stack -> "최대 체력이 ${30 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    HP_UNIQUE(
        "체력 증가",
        UpgradeTier.UNIQUE,
        1,
        { _ -> "최대 체력이 100% 증가합니다" }
    ),

    // === 6. 치명타 확률 (상한선 50%) ===
    CRIT_CHANCE_NORMAL(
        "치명타 확률 증가",
        UpgradeTier.NORMAL,
        3,
        { stack -> "치명타 확률이 ${5 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    CRIT_CHANCE_RARE(
        "치명타 확률 증가",
        UpgradeTier.RARE,
        3,
        { stack -> "치명타 확률이 ${10 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    CRIT_CHANCE_UNIQUE(
        "치명타 확률 증가",
        UpgradeTier.UNIQUE,
        2,
        { stack -> "치명타 확률이 ${20 * (stack + 1)}% 증가합니다 (${stack}/2)" }
    ),

    // === 7. 치명타 데미지 ===
    CRIT_DAMAGE_NORMAL(
        "치명타 데미지 증가",
        UpgradeTier.NORMAL,
        5,
        { stack -> "치명타 데미지가 ${10 * (stack + 1)}% 증가합니다 (${stack}/5)" }
    ),
    CRIT_DAMAGE_RARE(
        "치명타 데미지 증가",
        UpgradeTier.RARE,
        3,
        { stack -> "치명타 데미지가 ${30 * (stack + 1)}% 증가합니다 (${stack}/3)" }
    ),
    CRIT_DAMAGE_UNIQUE(
        "치명타 데미지 증가",
        UpgradeTier.UNIQUE,
        1,
        { _ -> "치명타 데미지가 100% 증가합니다" }
    ),

    // === 8. 골드 획득 (폴백 옵션) ===
    GOLD_BONUS(
        "골드 획득",
        UpgradeTier.NORMAL,
        Int.MAX_VALUE, // 무제한 선택 가능
        { _ -> "골드를 50 획득합니다" }
    );

    companion object {
        /**
         * 티어별 옵션 리스트
         */
        fun getOptionsByTier(tier: UpgradeTier): List<LevelUpOptionType> {
            return values().filter { it.tier == tier }
        }
    }

    /**
     * 현재 중첩 횟수에 따른 설명
     */
    fun getDescription(currentStack: Int): String {
        return description(currentStack)
    }
}

/**
 * 선택지 데이터 (중첩 카운트 포함)
 */
data class LevelUpOptionData(
    val type: LevelUpOptionType,
    val currentStack: Int = 0
) {
    /**
     * 다음 중첩의 설명
     */
    fun getNextDescription(): String = type.getDescription(currentStack)
}
