package com.example.theorb.modal

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.screens.BaseScreen
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.util.ResourceManager

class PauseModal(private val stage: Stage, private val skin: Skin) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null

    fun show(
        onHome: () -> Unit,
        onPlay: () -> Unit
    ) {
        // 기존 다이얼로그가 있으면 제거
        hide()

        // Stage 크기 미리 가져오기
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        // 반투명 배경 오버레이 생성
        createBackgroundOverlay(stageWidth, stageHeight)

        // 다이얼로그 컨테이너 생성
        createDialogContainer(onHome, onPlay)

        // 스테이지에 추가
        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙 정렬
        centerDialog(stageWidth, stageHeight)
    }

    private fun createBackgroundOverlay(stageWidth: Float, stageHeight: Float) {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(BaseScreen.BACKGROUND.r, BaseScreen.BACKGROUND.g, BaseScreen.BACKGROUND.b, 0.5f) // 50% 투명도
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()

        backgroundOverlay = Image(TextureRegionDrawable(texture)).apply {
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
        }
    }

    private fun createDialogContainer(
        onHome: () -> Unit,
        onPlay: () -> Unit
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getCommonSmallModalPanel()
            pad(40f)
        }

        // 제목
        val titleLabel = Label("게임 일시정지", this@PauseModal.skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 버튼들을 가로로 배치
        val buttonTable = Table()

        // 홈 버튼 - Retro 스타일 (이미지 교체 방식)
        val homeButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getHomeBasePos(),
            eventImage = ResourceManager.getHomeEventPos()
        ) {
            onHome()
        }

        // 플레이 버튼 - Retro 스타일 (이미지 교체 방식)
        val playButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getPlayBasePos(),
            eventImage = ResourceManager.getPlayEventPos()
        ) {
            onPlay()
            hide()
        }


        // 버튼들 배치 (가로) - 아이콘 이미지 크기
        buttonTable.add(homeButton).pad(10f)
        buttonTable.add(playButton).pad(10f)

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).padBottom(30f).row()
            add(buttonTable).row()
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
