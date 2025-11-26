package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align

enum class RewardType {
    GOLD,
    EXP,
    ORB
}

class RewardText(
    private val amount: Int,
    private val x: Float,
    private var y: Float,
    private val type: RewardType,
    private var startY: Float
) {
    private var alpha: Float = 1.0f
    private var lifeTime: Float = 0f
    private val maxLifeTime: Float = 2f

    fun update(delta: Float): Boolean {
        lifeTime += delta

        // 페이드아웃 (마지막 0.5초 동안)
        if (lifeTime > maxLifeTime - 0.5f) {
            alpha = (maxLifeTime - lifeTime) / 0.5f
        }

        return lifeTime < maxLifeTime
    }

    fun moveUp(distance: Float) {
        y += distance
        startY += distance
    }

    private fun getRewardText(): String {
        return when (type) {
//            RewardType.GOLD -> "골드를 획득했습니다. (+${amount}골드)"
//            RewardType.EXP -> "경험치를 획득했습니다. (+${amount}EXP)"
//            RewardType.ORB -> "오브를 획득했습니다. (+${amount}오브)"
            RewardType.GOLD -> "+${amount} 골드"
            RewardType.EXP -> "+${amount} EXP"
            RewardType.ORB -> "+${amount} 오브"
        }
    }

    fun draw(batch: SpriteBatch, font: BitmapFont) {
        val originalColor = font.color.cpy()
        val whiteColor = Color.WHITE.cpy()
        whiteColor.a = alpha

        font.color = whiteColor
        font.draw(batch, getRewardText(), x, y, 0f, Align.right, false)

        // 폰트 색상 복원
        font.color = originalColor
    }
}
