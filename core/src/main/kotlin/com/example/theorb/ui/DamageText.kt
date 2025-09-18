package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.example.theorb.balance.Element
import com.example.theorb.util.formatNumber

class DamageText(
    private val damage: Int,
    private var x: Float,
    private var y: Float,
    private val element: Element
) {
    private var alpha: Float = 1.0f
    private var lifeTime: Float = 0f
    private val maxLifeTime: Float = 1.5f
    private val moveSpeed: Float = 50f
    private val fadeSpeed: Float = 1.0f / maxLifeTime

    fun update(delta: Float): Boolean {
        lifeTime += delta
        y += moveSpeed * delta
        alpha = 1.0f - (lifeTime / maxLifeTime)

        return lifeTime < maxLifeTime
    }

    private fun getElementColor(): Color {
        return when (element) {
            Element.FIRE -> Color(1f, 0.6f, 0.4f, 1f)         // 주황빛 빨강
            Element.COLD -> Color(0.6f, 0.8f, 1f, 1f)         // 연한 파랑
            Element.LIGHTNING -> Color(1f, 1f, 0.6f, 1f)      // 연한 노랑
            Element.ANGEL -> Color(1f, 0.9f, 0.7f, 1f)        // 크림색/금색
            Element.DEMON -> Color(0.8f, 0.6f, 1f, 1f)        // 연한 보라
        }
    }

    fun draw(batch: SpriteBatch, font: BitmapFont) {
        val originalColor = font.color.cpy()
        val elementColor = getElementColor()
        elementColor.a = alpha

        font.color = elementColor
        font.draw(batch, formatNumber(damage), x, y, 0f, Align.center, false)

        // 폰트 색상 복원
        font.color = originalColor
    }
}