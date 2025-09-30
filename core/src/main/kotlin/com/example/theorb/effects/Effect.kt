package com.example.theorb.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

enum class Anchor { CENTER, BOTTOM }

class Effect(
    private val animation: Animation<TextureRegion>,
    var x: Float,
    var y: Float,
    private var scale: Float = 1f,
    private var rotation: Float = 0f,
    private val anchor: Anchor = Anchor.CENTER,
    private var alpha: Float = 1f
) {
    private var stateTime = 0f
    var finished = false
        private set

    fun update(delta: Float) {
        stateTime += delta
        if (animation.isAnimationFinished(stateTime)) finished = true
    }

    fun setPosition(newX: Float, newY: Float) {
        this.x = newX
        this.y = newY
    }

    fun setRotation(angleDeg: Float) {
        this.rotation = angleDeg
    }

    fun setScale(newScale: Float) {
        this.scale = newScale
    }

    fun setAlpha(newAlpha: Float) {
        this.alpha = newAlpha
    }

    fun draw(batch: SpriteBatch) {
        val frame = animation.getKeyFrame(stateTime)
        val w = frame.regionWidth * scale
        val h = frame.regionHeight * scale

        // 이전 색상 저장
        val originalColor = batch.color.cpy()
        // 알파값 적용
        batch.color = originalColor.cpy().apply { a = alpha }

        when (anchor) {
            Anchor.CENTER -> batch.draw(
                frame,
                x - w / 2f, y - h / 2f, // 위치
                w / 2f, h / 2f,         // 회전 기준점 (중앙)
                w, h,
                1f, 1f,                 // scaleX, scaleY
                rotation                 // 회전 각도 (도 단위)
            )
            Anchor.BOTTOM -> batch.draw(
                frame,
                x - w / 2f, y,          // 위치
                w / 2f, 0f,             // 회전 기준점 (아래쪽 중앙)
                w, h,
                1f, 1f,
                rotation
            )
        }

        // 원래 색상 복원
        batch.color = originalColor
    }
}
