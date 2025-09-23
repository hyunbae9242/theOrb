package com.example.theorb.data

import com.example.theorb.util.ResourceManager

/**
 * 오브 능력 타입
 */
enum class OrbAbilityType(val calculationType: DamageCalculationType = DamageCalculationType.INCREASE) {
    // 데미지 관련
    DAMAGE_ADDITION(DamageCalculationType.ADDITION),      // 데미지 추가
    DAMAGE_INCREASE(DamageCalculationType.INCREASE),      // 전체 데미지 증가
    DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),        // 전체 데미지 증폭

    FIRE_DAMAGE_ADDITION(DamageCalculationType.ADDITION),     // 화염 속성 데미지 추가
    FIRE_DAMAGE_INCREASE(DamageCalculationType.INCREASE),     // 화염 속성 데미지 증가
    FIRE_DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),       // 화염 속성 데미지 증폭

    LIGHTNING_DAMAGE_ADDITION(DamageCalculationType.ADDITION), // 번개 속성 데미지 추가
    LIGHTNING_DAMAGE_INCREASE(DamageCalculationType.INCREASE), // 번개 속성 데미지 증가
    LIGHTNING_DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),   // 번개 속성 데미지 증폭

    COLD_DAMAGE_ADDITION(DamageCalculationType.ADDITION),     // 얼음 속성 데미지 추가
    COLD_DAMAGE_INCREASE(DamageCalculationType.INCREASE),     // 얼음 속성 데미지 증가
    COLD_DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),       // 얼음 속성 데미지 증폭

    ANGEL_DAMAGE_ADDITION(DamageCalculationType.ADDITION),    // 신성 속성 데미지 추가
    ANGEL_DAMAGE_INCREASE(DamageCalculationType.INCREASE),    // 신성 속성 데미지 증가
    ANGEL_DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),      // 신성 속성 데미지 증폭

    DEMON_DAMAGE_ADDITION(DamageCalculationType.ADDITION),    // 악마 속성 데미지 추가
    DEMON_DAMAGE_INCREASE(DamageCalculationType.INCREASE),    // 악마 속성 데미지 증가
    DEMON_DAMAGE_AMPLIFY(DamageCalculationType.AMPLIFY),      // 악마 속성 데미지 증폭

    // 기타 능력
    COOLDOWN_REDUCTION,      // 쿨다운 감소
    RANGE_INCREASE,          // 사정거리 증가
    MULTI_SHOT,              // 다중 발사
    CRIT_CHANCE,             // 치명타 확률
    CRIT_DAMAGE,             // 치명타 데미지
    HEALTH_BOOST             // 체력 증가
}

/**
 * 오브 능력 정보
 */
data class OrbAbility(
    val type: OrbAbilityType,
    val value: Float
)

/**
 * 오브 데이터 클래스
 */
data class OrbData(
    val id: String,
    val name: String,
    val description: String,
    val imagePath: String,
    val abilities: List<OrbAbility>, // 여러 능력을 가질 수 있음
    val unlocked: Boolean = true // 해금 여부
) {
    fun getDrawable() = ResourceManager.getDrawable(imagePath)

    // 특정 능력 타입의 값을 가져오기
    fun getAbilityValue(abilityType: OrbAbilityType): Float {
        return abilities.find { it.type == abilityType }?.value ?: 0f
    }

    // 특정 능력을 가지고 있는지 확인
    fun hasAbility(abilityType: OrbAbilityType): Boolean {
        return abilities.any { it.type == abilityType }
    }
}

/**
 * 오브 레지스트리
 */
object OrbRegistry {
    private val orbs = listOf(
        OrbData(
            id = "base",
            name = "기본 오브",
            description = "특별한 능력이 없는 기본 오브입니다.",
            imagePath = "images/orbs/Base_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.DAMAGE_INCREASE, 1.0f)
            )
        ),
        OrbData(
            id = "balance",
            name = "균형의 오브",
            description = "모든 데미지가 15% 증가하고 쿨다운이 10% 감소합니다.",
            imagePath = "images/orbs/Balance_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.DAMAGE_INCREASE, 1.15f),
                OrbAbility(OrbAbilityType.COOLDOWN_REDUCTION, 0.9f)
            )
        ),
        OrbData(
            id = "fire",
            name = "폭염의 오브",
            description = "화염 속성 데미지가 30% 증가합니다.",
            imagePath = "images/orbs/Fire_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.FIRE_DAMAGE_INCREASE, 1.3f)
            )
        ),
        OrbData(
            id = "lightning",
            name = "뇌전의 오브",
            description = "번개 속성 데미지가 20% 증가하고 치명타 확률이 10% 증가합니다.",
            imagePath = "images/orbs/Lightning_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.LIGHTNING_DAMAGE_INCREASE, 1.20f),
                OrbAbility(OrbAbilityType.CRIT_CHANCE, 0.10f)
            )
        ),
        OrbData(
            id = "ice",
            name = "빙결의 오브",
            description = "얼음 속성 데미지가 20% 증가하고 쿨다운이 10% 감소합니다.",
            imagePath = "images/orbs/Ice_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.COLD_DAMAGE_INCREASE, 1.20f),
                OrbAbility(OrbAbilityType.COOLDOWN_REDUCTION, 0.9f)
            )
        ),
        OrbData(
            id = "angel",
            name = "천사의 오브",
            description = "신성 속성 데미지가 30% 증가하고 사정거리가 15% 증가합니다.",
            imagePath = "images/orbs/Angel_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.ANGEL_DAMAGE_INCREASE, 1.30f),
                OrbAbility(OrbAbilityType.RANGE_INCREASE, 1.15f)
            )
        ),
        OrbData(
            id = "demon",
            name = "악마의 오브",
            description = "악마 속성 데미지가 20% 증가하고 모든 최종 데미지가 20% 증폭됩니다.",
            imagePath = "images/orbs/Demon_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.DEMON_DAMAGE_AMPLIFY, 1.20f),
                OrbAbility(OrbAbilityType.DAMAGE_AMPLIFY, 1.20f)
            )
        ),
        OrbData(
            id = "critical",
            name = "치명의 오브",
            description = "치명타 확률이 15% 증가하고 치명타 데미지가 40% 증가합니다.",
            imagePath = "images/orbs/Critical_orb.png",
            abilities = listOf(
                OrbAbility(OrbAbilityType.CRIT_CHANCE, 0.15f),
                OrbAbility(OrbAbilityType.CRIT_DAMAGE, 1.40f)
            )
        ),
//        OrbData(
//            id = "orb_09",
//            name = "수호의 오브",
//            description = "최대 체력이 50% 증가하고 사정거리가 15% 증가합니다.",
//            imagePath = "images/orbs/Orb_01.png",
//            abilities = listOf(
//                OrbAbility(OrbAbilityType.HEALTH_BOOST, 1.5f),
//                OrbAbility(OrbAbilityType.RANGE_INCREASE, 1.15f)
//            )
//        ),
//        OrbData(
//            id = "orb_10",
//            name = "파워 오브",
//            description = "데미지가 50 추가되고 모든 데미지가 15% 증가합니다.",
//            imagePath = "images/orbs/Orb_01.png",
//            abilities = listOf(
//                OrbAbility(OrbAbilityType.DAMAGE_ADDITION, 50f),
//                OrbAbility(OrbAbilityType.DAMAGE_INCREASE, 1.15f)
//            )
//        ),
//        OrbData(
//            id = "orb_11",
//            name = "증폭의 오브",
//            description = "모든 데미지가 25% 증폭됩니다.",
//            imagePath = "images/orbs/Orb_01.png",
//            abilities = listOf(
//                OrbAbility(OrbAbilityType.DAMAGE_AMPLIFY, 1.25f)
//            )
//        ),
//        OrbData(
//            id = "orb_12",
//            name = "마스터 오브",
//            description = "데미지가 100 추가되고, 모든 데미지가 30% 증가하며, 최종적으로 20% 증폭됩니다.",
//            imagePath = "images/orbs/Orb_01.png",
//            abilities = listOf(
//                OrbAbility(OrbAbilityType.DAMAGE_ADDITION, 100f),
//                OrbAbility(OrbAbilityType.DAMAGE_INCREASE, 1.30f),
//                OrbAbility(OrbAbilityType.DAMAGE_AMPLIFY, 1.20f)
//            )
//        ),
//        OrbData(
//            id = "orb_13",
//            name = "데몬 로드 오브",
//            description = "악마 속성 데미지가 40% 증가하고 최종적으로 15% 증폭됩니다.",
//            imagePath = "images/orbs/Orb_01.png",
//            abilities = listOf(
//                OrbAbility(OrbAbilityType.DEMON_DAMAGE_INCREASE, 1.40f),
//                OrbAbility(OrbAbilityType.DEMON_DAMAGE_AMPLIFY, 1.15f)
//            )
//        )
    )

    fun getAllOrbs(): List<OrbData> = orbs

    fun getOrbById(id: String): OrbData? = orbs.find { it.id == id }

    fun getUnlockedOrbs(): List<OrbData> = orbs.filter { it.unlocked }
}
