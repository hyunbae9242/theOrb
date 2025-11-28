package com.example.theorb.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.example.theorb.util.ResourceManager

object ToastMessage {
    private val TEXT_PRIMARY = com.badlogic.gdx.graphics.Color(0xF0F0F0FF.toInt())

    /**
     * 화면 중앙에 토스트 메시지를 표시하고 위로 올라가면서 페이드아웃
     * @param stage 메시지를 표시할 Stage
     * @param message 표시할 메시지
     * @param skin UI 스킨
     * @param duration 애니메이션 지속 시간 (기본 1초)
     */
    fun show(stage: Stage, message: String, skin: Skin, duration: Float = 1f) {
        val toastLabel = Label(message, skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
            setAlignment(Align.center)
        }

        val toastPanel = Table().apply {
            pad(16f)
            add(toastLabel).center()
        }

        // 화면 중앙에 배치
        toastPanel.setPosition(
            (stage.width) / 2,
            stage.height / 2
        )

        stage.addActor(toastPanel)

        // 위로 올라가면서 페이드아웃 애니메이션
        toastPanel.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 100f, duration),
                    Actions.fadeOut(duration)
                ),
                Actions.removeActor()
            )
        )
    }
}
