package com.example.theorb.skills

import com.badlogic.gdx.graphics.Color

enum class SkillRank(
    val displayName: String,
    val upgradeRequirement: Int, // 다음 등급으로 업그레이드하는데 필요한 개수
    val color: Color
) {
    C("C", 10, Color(0.7f, 0.7f, 0.7f, 1f)), // 회색
    B("B", 10, Color(0.4f, 0.8f, 0.4f, 1f)), // 녹색
    A("A", 5, Color(0.4f, 0.6f, 1.0f, 1f)),  // 파랑
    S("S", 4, Color(1.0f, 0.8f, 0.2f, 1f)),  // 황금
    SS("SS", 3, Color(1.0f, 0.4f, 0.8f, 1f)), // 분홍
    SSS("SSS", 0, Color(1.0f, 0.2f, 0.2f, 1f)); // 빨강 (최고 등급)

    fun getNextRank(): SkillRank? {
        return when (this) {
            C -> B
            B -> A
            A -> S
            S -> SS
            SS -> SSS
            SSS -> null // 최고 등급
        }
    }

    fun canUpgrade(): Boolean = getNextRank() != null
}