package com.example.theorb.data

data class SaveData(
    var gold: Int = 0,
    var orbs: Int = 0,
    var pBaseDmg: Int = 10,
    var pBaseHp: Int = 100,
    var pBaseCastSpeedMul: Int = 1,
    var pBaseCriChance: Int = 0,
    var unlockedStages: Int = 1, // 해금된 스테이지 수 (1~5)
    var currentStage: Int = 1, // 현재 선택된 스테이지
    var highestClearedStage: Int = 0, // 가장 높은 클리어 스테이지 (0이면 클리어 없음)
    var upgrades: MutableMap<String, Int> = mutableMapOf(), // ex) "atkPower" -> 2
    var permanentUpgrades: MutableMap<String, Int> = mutableMapOf(), // 영구 업그레이드 레벨
    var equippedSkills: MutableList<String> = mutableListOf(),
    var skillInventory: MutableList<Map<String, Any>> = mutableListOf(), // 스킬 인벤토리
    var maxSkillSlots: Int = 1, // 최대 스킬 슬롯 개수
    var equippedSubSkills: MutableMap<String, MutableList<Map<String, Any>>> = mutableMapOf(), // 메인스킬ID -> 보조스킬 데이터 리스트 (type, level)
    var subSkillInventory: MutableMap<String, Map<String, Any>> = mutableMapOf(), // 보조스킬 인벤토리 (effectType -> {level, exp})
    var selectedOrb: String = "base_orb", // 선택된 오브 ID
    var selectedBackground: String = "clouds02", // 선택된 배경화면 이름
    var currentSpeedMultiplier: Float = 1.0f, // 현재 선택된 배속 (1.0, 2.0, 3.0)
    var maxSpeedMultiplier: Float = 2.0f, // 최대 사용 가능한 배속 (과금으로 3.0까지 확장)

    // 인게임 레벨 시스템 (스테이지마다 초기화)
    var inGameLevel: Int = 1, // 인게임 레벨 (스테이지마다 1부터 시작)
    var inGameExp: Int = 0, // 현재 경험치
    var selectedLevelUpOptions: MutableList<String> = mutableListOf(), // 선택된 레벨업 옵션 ID 리스트 (예: ["DAMAGE_NORMAL", "COOLDOWN_RARE", ...])

    // 레벨업 선택지 티어 확률 (영구 업그레이드로 수정 가능)
    var tierChanceNormal: Float = 90f, // 노멀 티어 확률
    var tierChanceRare: Float = 7f, // 레어 티어 확률
    var tierChanceUnique: Float = 3f, // 유니크 티어 확률

    // 레벨업 선택지 리롤 시스템
    var maxRerollCount: Int = 0, // 최대 리롤 횟수 (영구 업그레이드로 증가, 최대 5)
    var currentRerollCount: Int = 0, // 현재 남은 리롤 횟수 (게임 시작마다 maxRerollCount로 충전)

    // 인게임 HP 시스템 (스테이지마다 초기화 및 재계산)
    var maxHp: Int = 100, // 최대 체력 (baseHp + 업그레이드 + 인게임 선택지)
    var currentHp: Int = 100, // 현재 체력

    // 에너지 실드 시스템
    var maxEnergyShield: Int = 50, // 최대 에너지 실드
    var currentEnergyShield: Int = 0, // 현재 에너지 실드
    var energyShieldRegenRate: Float = 1f, // 초당 회복량

) {
    init {
        // 처음 게임 시작 시 기본 스킬 제공
        if (skillInventory.isEmpty() && equippedSkills.isEmpty()) {
            initializeDefaultSkills()
        }

        // 기존 세이브 데이터 마이그레이션: "type" -> "skillType"
        migrateSkillInventoryKeys()
    }

    private fun migrateSkillInventoryKeys() {
        skillInventory.forEach { skill ->
            if (skill is MutableMap && skill.containsKey("type") && !skill.containsKey("skillType")) {
                val typeValue = skill["type"]
                skill["skillType"] = typeValue!!
                skill.remove("type")
            }
        }
    }

    private fun initializeDefaultSkills() {
        // 화염구 C랭크 기본 제공
        val fireballC = mapOf(
            "skillType" to "Fireball",
            "rank" to "C",
            "exp" to 1
        )

        skillInventory.add(fireballC)
        equippedSkills.add("Fireball")
//        SaveManager.save(this)
    }
}
