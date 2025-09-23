package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.example.theorb.util.ResourceManager

class ModalDialog(private val stage: Stage, private val skin: Skin) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null

    private fun createRoundedRectWithBorder(width: Int, height: Int, cornerRadius: Int,
                                          bgColor: Color, borderColor: Color, borderWidth: Int): TextureRegionDrawable {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

        // 전체를 테두리 색으로 채우기
        pixmap.setColor(borderColor)
        pixmap.fill()

        // 내부를 배경색으로 채우기 (테두리를 남기기 위해 안쪽 영역만)
        pixmap.setColor(bgColor)
        pixmap.fillRectangle(borderWidth, borderWidth, width - 2 * borderWidth, height - 2 * borderWidth)

        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
    }

    fun show(
        title: String,
        message: String,
        confirmText: String = "확인",
        cancelText: String = "취소",
        confirmColor: Color = Color.RED,
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
        createDialogContainer(title, message, confirmText, cancelText, confirmColor, onConfirm, onCancel)

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
        confirmText: String,
        cancelText: String,
        confirmColor: Color,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)?
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getSquarePanel320()
            pad(30f)
        }

        // 제목
        val titleLabel = Label(title, this@ModalDialog.skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = com.example.theorb.screens.BaseScreen.TEXT_PRIMARY
        }

        // 메시지
        val messageLabel = Label(message, this@ModalDialog.skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = com.example.theorb.screens.BaseScreen.TEXT_SECONDARY
            setWrap(true)
            setAlignment(Align.center)
        }

        // 버튼들
        val buttonTable = Table()

        val confirmButton = TextButton(confirmText, TextButton.TextButtonStyle().apply {
            font = this@ModalDialog.skin.get("btn", TextButton.TextButtonStyle::class.java).font
            up = ResourceManager.getButtonConfirmBg()
            down = ResourceManager.getButtonHighlightBg()
            over = ResourceManager.getButtonHighlightBg()
            fontColor = Color.WHITE
        }).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onConfirm()
                    hide()
                }
            })
        }

        if (onCancel != null) {
            val cancelButton = TextButton(cancelText, TextButton.TextButtonStyle().apply {
                font = this@ModalDialog.skin.get("btn", TextButton.TextButtonStyle::class.java).font
                up = ResourceManager.getButtonCancelBg()
                down = ResourceManager.getButtonHighlightBg()
                over = ResourceManager.getButtonHighlightBg()
                fontColor = Color.WHITE
            }).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        onCancel()
                        hide()
                    }
                })
            }
            buttonTable.add(cancelButton).width(112f).height(56f).padRight(20f) // 새로운 배경 이미지 크기
        }

        buttonTable.add(confirmButton).width(112f).height(56f) // 새로운 배경 이미지 크기

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(20f).row()
            add(messageLabel).center().width(300f).padBottom(30f).row()
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
