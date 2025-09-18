package com.example.theorb.balance

import kotlin.math.pow

/**
 * 게임 진행에 따른 난이도 스케일링 시스템
 */
object ProgressionBalance {

    // === 시간 기반 스케일링 설정 ===
    const val SCALING_INTERVAL_SECONDS = 30f  // 30초마다 스케일링
    const val BASE_SCALING_RATE = 1.25f       // 25% 증가 (기본)
    const val SCALING_CURVE_FACTOR = 0.02f    // 시간이 지날수록 스케일링 완화

    /**
     * 게임 진행 시간(초)에 따른 적 체력 배수 계산
     *
     * 계산 공식:
     * - 30초마다 체력이 25% 증가 (초기)
     * - 시간이 지날수록 증가폭이 완만해짐 (플레이어의 업그레이드 기회 제공)
     *
     * 예시:
     * - 0-30초: 1.0배 (기본 체력)
     * - 30-60초: 1.25배
     * - 60-90초: 1.56배
     * - 90-120초: 1.95배
     * - 120-150초: 2.44배
     */
    fun getHealthScalingMultiplier(gameTimeSeconds: Float): Float {
        val intervals = gameTimeSeconds / SCALING_INTERVAL_SECONDS
        val flooredIntervals = intervals.toInt()

        if (flooredIntervals <= 0) return 1.0f

        // 시간이 지날수록 스케일링이 완만해지는 공식
        // 초기: 25% → 후기: 15% 정도로 수렴
        var multiplier = 1.0f

        for (i in 1..flooredIntervals) {
            val currentRate = BASE_SCALING_RATE - (i * SCALING_CURVE_FACTOR)
            val finalRate = kotlin.math.max(currentRate, 1.15f) // 최소 15% 증가 보장
            multiplier *= finalRate
        }

        return multiplier
    }

    /**
     * 현재 플레이어의 예상 데미지 배수 계산
     * (업그레이드로 얻을 수 있는 실버 기반)
     */
    fun getExpectedPlayerDamageMultiplier(gameTimeSeconds: Float): Float {
        // 30초마다 평균적으로 얻을 수 있는 실버 예상
        val intervals = (gameTimeSeconds / SCALING_INTERVAL_SECONDS).toInt()

        // 평균적으로 30초에 10-15마리 적 처치 → 10-15 실버 획득 예상
        val averageSilverPer30Sec = 12
        val totalExpectedSilver = intervals * averageSilverPer30Sec

        // 데미지 업그레이드 우선으로 투자한다고 가정
        // 10, 11, 13, 15, 17, 20, 23, ... 순으로 비용 증가
        var silverSpent = 0
        var damageLevel = 0
        var currentCost = 10

        while (silverSpent + currentCost <= totalExpectedSilver) {
            silverSpent += currentCost
            damageLevel++
            currentCost = (10 * (1.15f.pow(damageLevel))).toInt()
        }

        // 레벨당 10% 증가
        return 1.0f + (damageLevel * 0.1f)
    }

    /**
     * 밸런스 확인 - 플레이어가 따라잡을 수 있는지 검증
     */
    fun getBalanceRatio(gameTimeSeconds: Float): Float {
        val enemyScaling = getHealthScalingMultiplier(gameTimeSeconds)
        val playerScaling = getExpectedPlayerDamageMultiplier(gameTimeSeconds)

        // 1.0에 가까울수록 균형 잡힘
        // > 1.0: 적이 더 강해짐 (어려워짐)
        // < 1.0: 플레이어가 더 강해짐 (쉬워짐)
        return enemyScaling / playerScaling
    }

    /**
     * 디버깅용 - 시간대별 밸런스 정보 출력
     */
    fun printBalanceInfo() {
        println("=== 게임 진행 밸런스 분석 ===")
        for (minutes in 1..10) {
            val seconds = minutes * 60f
            val enemyMult = getHealthScalingMultiplier(seconds)
            val playerMult = getExpectedPlayerDamageMultiplier(seconds)
            val balance = getBalanceRatio(seconds)

            println("${minutes}분: 적 체력 ${String.format("%.2f", enemyMult)}배, " +
                   "플레이어 데미지 ${String.format("%.2f", playerMult)}배, " +
                   "밸런스 비율 ${String.format("%.2f", balance)}")
        }
    }
}