package com.example.theorb.modal

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.example.theorb.screens.BaseScreen
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.util.ResourceManager

class ModalDialog(private val stage: Stage, private val skin: Skin) {

    // 비율 기반 크기 계산
    private val virtualWidth get() = stage.viewport.worldWidth
    private val virtualHeight get() = stage.viewport.worldHeight
    private fun getButtonHeight(): Float = virtualHeight * 0.0525f // 42/800
    private fun getRectangleButtonWidth(): Float = virtualWidth * 0.175f // 84/480

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null


    fun show(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        // 기존 다이얼로그가 있으면 제거
        hide()

        // Stage 크기 미리 가져오기
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        // 반투명 배경 오버레이 생성
        createBackgroundOverlay(stageWidth, stageHeight)

        // 다이얼로그 컨테이너 생성
        createDialogContainer(title, message, onConfirm, onCancel)

        // 스테이지에 추가
        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙 정렬
        centerDialog(stageWidth, stageHeight)
    }

    private fun createBackgroundOverlay(stageWidth: Float, stageHeight: Float) {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0.5f) // 50% 투명도
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()

        backgroundOverlay = Image(TextureRegionDrawable(texture)).apply {
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    // 배경 클릭 시 다이얼로그 닫기 (선택사항)
                }
            })
        }
    }

    private fun createDialogContainer(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)?,
        isConfirmEnabled: Boolean = true
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getCommonModalPanel()
            setSize(360f,360f)
        }

        // 제목
        val titleLabel = Label(title, this@ModalDialog.skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 메시지
        val messageLabel = Label(message, this@ModalDialog.skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
            setWrap(true)
            setAlignment(Align.center)
        }

        // 버튼들
        val buttonTable = Table()

        val confirmButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getConfirmBasePos(),
            eventImage = ResourceManager.getConfirmEventPos(),
            disabledImage = ResourceManager.getConfirmBaseNag(),
            isEnabled = isConfirmEnabled
        ) {
            onConfirm()
            hide()
        }

        if (onCancel != null) {
            val cancelButton = RetroButtonV01.createIconButton(
                defaultImage = ResourceManager.getCancelBasePos(),
                eventImage = ResourceManager.getCancelEventPos(),
            ) {
                onCancel()
                hide()
            }
            buttonTable.add(cancelButton).size(48f).padRight(48f)
        }

        buttonTable.add(confirmButton).size(48f)

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(32f).row()
            add(messageLabel).center().width(300f).padBottom(32f).row()
            add(buttonTable).center().row()
        }
    }

    private fun centerDialog(stageWidth: Float, stageHeight: Float) {
        dialogContainer?.let { dialog ->
            dialog.pack()
            dialog.setPosition(
                (stageWidth - dialog.width) / 2,
                (stageHeight - dialog.height) / 2
            )
        }
    }

    fun hide() {
        backgroundOverlay?.remove()
        dialogContainer?.remove()
        backgroundOverlay = null
        dialogContainer = null
    }
}
