package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager

class SettingsModal(private val stage: Stage, private val skin: Skin) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null
    private var currentBackgroundIndex = 0
    private val availableBackgrounds = listOf("clouds01", "clouds02", "city01", "city02", "city03")

    private var onBackgroundChanged: ((String) -> Unit)? = null

    fun show(
        currentBackground: String,
        onClose: () -> Unit,
        onBackgroundChange: (String) -> Unit
    ) {
        this.onBackgroundChanged = onBackgroundChange
        currentBackgroundIndex = availableBackgrounds.indexOf(currentBackground).coerceAtLeast(0)

        // 기존 다이얼로그가 있으면 제거
        hide()

        // Stage 크기 미리 가져오기
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        // 반투명 배경 오버레이 생성
        createBackgroundOverlay(stageWidth, stageHeight, onClose)

        // 다이얼로그 컨테이너 생성
        createDialogContainer(onClose)

        // 스테이지에 추가
        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙 정렬
        centerDialog(stageWidth, stageHeight)
    }

    private fun createBackgroundOverlay(stageWidth: Float, stageHeight: Float, onClose: () -> Unit) {
        backgroundOverlay = Image(skin.getDrawable("white")).apply {
            color = Color(0f, 0f, 0f, 0.7f)
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onClose()
                }
            })
        }
    }

    private fun createDialogContainer(onClose: () -> Unit) {
        dialogContainer = Table().apply {
            background = ResourceManager.getSquarePanel360()
            setSize(360f, 360f)
            pad(30f)
        }

        // 제목
        val titleLabel = Label("설정", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = com.example.theorb.screens.BaseScreen.TEXT_PRIMARY
        }

        // 배경화면 섹션
        val backgroundSection = createBackgroundSection()

        // 닫기 버튼
        val closeButton = RetroButton.createTextButton(
            text = "닫기",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
            eventImage = ResourceManager.getRetroRectanglePosEvent(),
            buttonSize = 42f
        ) {
            onClose()
        }

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(30f).row()
            add(backgroundSection).center().padBottom(30f).row()
            add(closeButton).center().width(100f).height(42f).row()
        }
    }

    private fun createBackgroundSection(): Table {
        val section = Table()

        // 배경화면 라벨
        val backgroundLabel = Label("배경화면", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 배경화면 선택 컨트롤
        val backgroundControl = Table()

        // 좌측 화살표
        val leftArrow = RetroButton.createTextButton(
            text = "<",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroSquareNagDefault(),
            eventImage = ResourceManager.getRetroSquareNagEvent(),
            buttonSize = 42f
        ) {
            changeBackground(-1)
        }

        // 현재 배경화면 이름 표시
        val currentBackgroundLabel = Label(
            getCurrentBackgroundDisplayName(),
            skin.get("label-default", Label.LabelStyle::class.java)
        ).apply {
            color = com.example.theorb.screens.BaseScreen.TEXT_PRIMARY
            setAlignment(Align.center)
        }

        // 우측 화살표
        val rightArrow = RetroButton.createTextButton(
            text = ">",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroSquarePosDefault(),
            eventImage = ResourceManager.getRetroSquarePosEvent(),
            buttonSize = 42f
        ) {
            changeBackground(1)
        }

        backgroundControl.add(leftArrow).size(42f)
        backgroundControl.add(currentBackgroundLabel).width(80f).center()
        backgroundControl.add(rightArrow).size(42f)

        section.add(backgroundLabel).center().padRight(20f)
        section.add(backgroundControl).center()

        return section
    }

    private fun getCurrentBackgroundDisplayName(): String {
        return availableBackgrounds[currentBackgroundIndex]
    }

    private fun changeBackground(direction: Int) {
        currentBackgroundIndex = (currentBackgroundIndex + direction + availableBackgrounds.size) % availableBackgrounds.size
        val newBackground = availableBackgrounds[currentBackgroundIndex]

        // 배경화면 즉시 변경
        onBackgroundChanged?.invoke(newBackground)

        // 라벨 업데이트
        updateBackgroundLabel()
    }

    private fun updateBackgroundLabel() {
        dialogContainer?.let { container ->
            // 배경화면 라벨 찾아서 업데이트
            val backgroundSection = container.children[1] as Table
            val backgroundControl = backgroundSection.children[1] as Table
            val currentBackgroundLabel = backgroundControl.children[1] as Label
            currentBackgroundLabel.setText(getCurrentBackgroundDisplayName())
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
