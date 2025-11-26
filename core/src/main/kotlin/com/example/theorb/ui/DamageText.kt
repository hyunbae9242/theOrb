package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.example.theorb.util.formatNumber

class DamageText(
    private val damage: Int,
    private var x: Float,
    private var y: Float
) {
    private var alpha: Float = 1.0f
    private var lifeTime: Float = 0f
    private val maxLifeTime: Float = 1.5f
    private val moveSpeed: Float = 30f
    private val fadeSpeed: Float = 1.0f / maxLifeTime

    fun update(delta: Float): Boolean {
        lifeTime += delta
        y += moveSpeed * delta
        alpha = 1.0f - (lifeTime / maxLifeTime)

        return lifeTime < maxLifeTime
    }

    fun draw(batch: SpriteBatch, font: BitmapFont) {
        val originalColor = font.color.cpy()
        val damageColor = Color(1f, 1f, 1f, alpha) // 기본 흰색

        font.color = damageColor
        font.draw(batch, formatNumber(damage), x, y, 0f, Align.center, false)

        // 폰트 색상 복원
        font.color = originalColor
    }
}
