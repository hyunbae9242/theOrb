package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.util.ResourceManager

class SettingsModal(private val stage: Stage, private val skin: Skin) {

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
        onHome: () -> Unit,
        onPlay: () -> Unit,
        onRestart: () -> Unit
    ) {
        // 기존 다이얼로그가 있으면 제거
        hide()

        // Stage 크기 미리 가져오기
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        // 반투명 배경 오버레이 생성
        createBackgroundOverlay(stageWidth, stageHeight)

        // 다이얼로그 컨테이너 생성
        createDialogContainer(onHome, onPlay, onRestart)

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
        }
    }

    private fun createDialogContainer(
        onHome: () -> Unit,
        onPlay: () -> Unit,
        onRestart: () -> Unit
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getSquarePanel320()
            pad(40f)
        }

        // 제목
        val titleLabel = Label("게임 일시정지", this@SettingsModal.skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = com.example.theorb.screens.BaseScreen.TEXT_PRIMARY
        }

        // 버튼들을 가로로 배치
        val buttonTable = Table()

        // 홈 버튼 - Stack으로 오버레이 구현 (원래 이미지 위에 이벤트 이미지 겹침)
        val homeButtonIcon = Image(ResourceManager.getHomeButtonDrawable())
        val homeButtonOverlay = Image(ResourceManager.getSquareMenuButtonEvent()).apply {
            isVisible = false
        }
        val homeButton = Stack().apply {
            add(homeButtonIcon)
            add(homeButtonOverlay)

            addListener(object : com.badlogic.gdx.scenes.scene2d.InputListener() {
                override fun enter(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    homeButtonOverlay.isVisible = true
                }
                override fun exit(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    homeButtonOverlay.isVisible = false
                }
                override fun touchDown(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    homeButtonOverlay.isVisible = true
                    return true
                }
                override fun touchUp(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    homeButtonOverlay.isVisible = false
                    onHome()
                }
            })
        }

        // 플레이 버튼 - Stack으로 오버레이 구현 (원래 이미지 위에 이벤트 이미지 겹침)
        val playButtonIcon = Image(ResourceManager.getPlayButtonDrawable())
        val playButtonOverlay = Image(ResourceManager.getSquareMenuButtonEvent()).apply {
            isVisible = false
        }
        val playButton = Stack().apply {
            add(playButtonIcon)
            add(playButtonOverlay)

            addListener(object : com.badlogic.gdx.scenes.scene2d.InputListener() {
                override fun enter(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    playButtonOverlay.isVisible = true
                }
                override fun exit(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    playButtonOverlay.isVisible = false
                }
                override fun touchDown(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    playButtonOverlay.isVisible = true
                    return true
                }
                override fun touchUp(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    playButtonOverlay.isVisible = false
                    onPlay()
                    hide()
                }
            })
        }

        // 재시작 버튼 - Stack으로 오버레이 구현 (원래 이미지 위에 이벤트 이미지 겹침)
        val restartButtonIcon = Image(ResourceManager.getRestartButtonDrawable())
        val restartButtonOverlay = Image(ResourceManager.getSquareMenuButtonEvent()).apply {
            isVisible = false
        }
        val restartButton = Stack().apply {
            add(restartButtonIcon)
            add(restartButtonOverlay)

            addListener(object : com.badlogic.gdx.scenes.scene2d.InputListener() {
                override fun enter(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    restartButtonOverlay.isVisible = true
                }
                override fun exit(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    restartButtonOverlay.isVisible = false
                }
                override fun touchDown(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    restartButtonOverlay.isVisible = true
                    return true
                }
                override fun touchUp(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    restartButtonOverlay.isVisible = false
                    onRestart()
                }
            })
        }

        // 버튼들 배치 (가로) - 아이콘 이미지 크기
        buttonTable.add(homeButton).size(42f, 42f).pad(10f).padRight(10f)
        buttonTable.add(playButton).size(42f, 42f).pad(10f).padRight(10f)
        buttonTable.add(restartButton).size(42f, 42f).pad(10f)

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
