package com.example.theorb.stages

/**
 * 스테이지 정보
 */
data class StageData(
    val stageId: Int,
    val stageName: String,
    val description: String,

    // 난이도 배율
    val enemyHpMultiplier: Float,      // 적 체력 배율
    val enemyDamageMultiplier: Float,  // 적 공격력 배율
    val goldMultiplier: Float,         // 골드 획득량 배율
    val orbMultiplier: Float,          // 오브 획득량 배율
    val expMultiplier: Float,          // 경험치 획득량 배율

    // 클리어 조건
    val requiredWaves: Int = 5         // 클리어에 필요한 웨이브 수
)

/**
 * 스테이지 관리자
 */
object StageManager {

    private val stages = listOf(
        // 스테이지 1: 튜토리얼 난이도
        StageData(
            stageId = 1,
            stageName = "평화로운 숲",
            description = "첫 번째 모험을 시작하세요",
            enemyHpMultiplier = 1.0f,
            enemyDamageMultiplier = 1.0f,
            goldMultiplier = 1.0f,
            orbMultiplier = 1.0f,
            expMultiplier = 1.0f,
            requiredWaves = 20
        ),

        // 스테이지 2: 약간 증가
        StageData(
            stageId = 2,
            stageName = "어두운 동굴",
            description = "적들이 더 강해졌습니다",
            enemyHpMultiplier = 1.5f,
            enemyDamageMultiplier = 1.3f,
            goldMultiplier = 1.3f,
            orbMultiplier = 1.2f,
            expMultiplier = 1.2f,
            requiredWaves = 20
        ),

        // 스테이지 3: 중간 난이도
        StageData(
            stageId = 3,
            stageName = "불타는 화산",
            description = "뜨거운 용암이 흐르는 위험한 지역",
            enemyHpMultiplier = 2.2f,
            enemyDamageMultiplier = 1.7f,
            goldMultiplier = 1.7f,
            orbMultiplier = 1.5f,
            expMultiplier = 1.4f,
            requiredWaves = 20
        ),

        // 스테이지 4: 높은 난이도
        StageData(
            stageId = 4,
            stageName = "얼어붙은 설원",
            description = "차가운 얼음 폭풍이 몰아칩니다",
            enemyHpMultiplier = 3.2f,
            enemyDamageMultiplier = 2.2f,
            goldMultiplier = 2.2f,
            orbMultiplier = 2.0f,
            expMultiplier = 1.7f,
            requiredWaves = 20
        ),

        // 스테이지 5: 최고 난이도
        StageData(
            stageId = 5,
            stageName = "어둠의 성채",
            description = "최종 결전의 장소",
            enemyHpMultiplier = 4.5f,
            enemyDamageMultiplier = 3.0f,
            goldMultiplier = 3.0f,
            orbMultiplier = 2.5f,
            expMultiplier = 2.0f,
            requiredWaves = 20
        )
    )

    /**
     * 스테이지 정보 가져오기
     */
    fun getStage(stageId: Int): StageData? {
        return stages.find { it.stageId == stageId }
    }

    /**
     * 모든 스테이지 가져오기
     */
    fun getAllStages(): List<StageData> {
        return stages
    }

    /**
     * 다음 스테이지 가져오기
     */
    fun getNextStage(currentStageId: Int): StageData? {
        return stages.find { it.stageId == currentStageId + 1 }
    }

    /**
     * 최대 스테이지 수
     */
    fun getMaxStageCount(): Int {
        return stages.size
    }
}
