package com.example.theorb.data

/**
 * 데미지 계산 타입
 */
enum class DamageCalculationType {
    ADDITION,    // 추가 (정수로 + 연산, 가장 먼저 계산)
    INCREASE,    // 증가 (% 증가, 합연산 후 곱연산)
    AMPLIFY      // 증폭 (모든 계산 후 마지막 곱연산)
}