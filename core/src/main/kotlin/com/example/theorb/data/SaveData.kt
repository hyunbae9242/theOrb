package com.example.theorb.data

data class SaveData(
    var gold: Int = 0,
    var gems: Int = 0,
    var pBaseDmg: Int = 10,
    var pBaseHp: Int = 100,
    var pBaseCastSpeedMul: Int = 1,
    var pBaseCriChance: Int = 0,
    var unlockedStages: Int = 1,
    var upgrades: MutableMap<String, Int> = mutableMapOf(), // ex) "atkPower" -> 2
    var permanentUpgrades: MutableMap<String, Int> = mutableMapOf(), // 영구 업그레이드 레벨
    var equippedSkills: MutableList<String> = mutableListOf(),
    var currentSpeedMultiplier: Float = 1.0f, // 현재 선택된 배속 (1.0, 2.0, 3.0)
    var maxSpeedMultiplier: Float = 2.0f, // 최대 사용 가능한 배속 (과금으로 3.0까지 확장)

    // 인게임 화폐 및 업그레이드 (스테이지마다 초기화)
    var silver: Int = 0, // 실버 (스테이지 종료 시 0으로 초기화)
    var inGameUpgrades: MutableMap<String, Int> = mutableMapOf(), // 인게임 업그레이드 레벨

    // 인게임 업그레이드 보너스 값들 (계산 편의를 위해 캐시)
    var criticalChance: Float = 5f, // 기본 치명타 확률 5%
    var criticalDamage: Float = 150f // 기본 치명타 데미지 150%
)
