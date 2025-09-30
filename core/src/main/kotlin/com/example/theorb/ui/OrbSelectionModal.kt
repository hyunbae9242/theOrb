package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.example.theorb.data.OrbData
import com.example.theorb.data.OrbRegistry
import com.example.theorb.data.SaveData
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager

class OrbSelectionModal(
    private val stage: Stage,
    private val skin: Skin,
    private val saveData: SaveData
) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null
    private var selectedOrbData: OrbData = OrbRegistry.getOrbById(saveData.selectedOrb)
        ?: OrbRegistry.getOrbById("base")!!

    fun show(onClose: () -> Unit, onOrbSelected: (OrbData) -> Unit) {
        // 반투명 배경
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        backgroundOverlay = Image(skin.getDrawable("white")).apply {
            color = Color(BaseScreen.BACKGROUND.r, BaseScreen.BACKGROUND.g, BaseScreen.BACKGROUND.b, 0.7f)
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
        }

        createDialogContainer(onClose, onOrbSelected)

        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙 정렬 (동적 크기 기준)
        val panelWidth = stage.viewport.worldWidth * 0.95f
        val panelHeight = stage.viewport.worldHeight * 0.8f
        dialogContainer!!.setPosition(
            (stageWidth - panelWidth) / 2f,
            (stageHeight - panelHeight) / 2f
        )
    }

    private fun createDialogContainer(onClose: () -> Unit, onOrbSelected: (OrbData) -> Unit) {
        val panelWidth = stage.viewport.worldWidth * 0.95f
        val panelHeight = stage.viewport.worldHeight * 0.8f

        dialogContainer = Table().apply {
            background = ResourceManager.getRectanglePanel340448()
            pad(20f)
            setSize(panelWidth, panelHeight)
        }

        // 제목 섹션 (배경 포함)
        val titleSection = Table().apply {
            background = ResourceManager.getRetroRectanglePosEvent()
            pad(15f)
        }

        val titleLabel = Label("오브 선택", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        titleSection.add(titleLabel).center()

        // 상단: 선택된 오브 정보
        val selectedOrbSection = createSelectedOrbSection()

        // 중하단: 오브 그리드 리스트
        val orbGridSection = createOrbGridSection(onOrbSelected)

        // 닫기 버튼 - Retro 스타일
        val closeButton = com.example.theorb.ui.RetroButton.createTextButton(
            text = "닫기",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_SECONDARY,
            defaultImage = ResourceManager.getRetroRectangleNagDefault(),
            eventImage = ResourceManager.getRetroRectangleNagEvent(),
            buttonSize = (panelHeight * 0.1f)
        ) {
            onClose()
        }

        // 레이아웃 구성 - 퍼센트 기반 높이
        // 타이틀: 10%, 선택된 오브: 20%, 오브 리스트: 60%, 닫기: 10%
        dialogContainer!!.apply {
            add(titleSection).width(panelWidth * 0.4f).height(panelHeight * 0.1f).padBottom(10f).row()
            add(selectedOrbSection).growX().height(panelHeight * 0.2f).padBottom(10f).row()
            add(orbGridSection).grow().height(panelHeight * 0.6f).padBottom(10f).row()
            add(closeButton).width(panelWidth * 0.25f).height(panelHeight * 0.1f).center()
        }
    }

    private fun createSelectedOrbSection(): Table {
        val section = Table().apply {
            background = ResourceManager.getRetroRectangleNagEvent()
            pad(15f)
        }

        // 선택된 오브 이미지 (높이 118f에 맞게 조정)
        val selectedOrbImage = Image(selectedOrbData.getDrawable()).apply {
            setSize(60f, 60f)
        }

        // 선택된 오브 정보
        val infoTable = Table()

        val nameLabel = Label(selectedOrbData.name, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        val descLabel = Label(selectedOrbData.description, skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
            wrap = true
        }

        infoTable.apply {
            add(nameLabel).left().row()
            add(descLabel).left().width(300f).row()
        }

        section.apply {
            add(selectedOrbImage).size(60f, 60f).left().padRight(20f)
            add(infoTable).expandX().fillX().left()
        }

        return section
    }

    private fun createOrbGridSection(onOrbSelected: (OrbData) -> Unit): Table {
        val gridTable = Table()
        val scrollPane = ScrollPane(gridTable, skin)
        scrollPane.setScrollingDisabled(true, false) // 가로 스크롤 비활성화

        val unlockedOrbs = OrbRegistry.getUnlockedOrbs()
        val columns = 3
        var row = 0
        var col = 0

        for (orb in unlockedOrbs) {
            val orbButton = createOrbButton(orb, onOrbSelected)

            gridTable.add(orbButton).size(90f, 90f).pad(8f)

            col++
            if (col >= columns) {
                gridTable.row()
                col = 0
                row++
            }
        }

        val container = Table()
        container.add(scrollPane).grow()
        return container
    }

    private fun createOrbButton(orb: OrbData, onOrbSelected: (OrbData) -> Unit): com.badlogic.gdx.scenes.scene2d.ui.Stack {
        // 선택된 오브인지 확인
        val isSelected = orb.id == selectedOrbData.id

        // RetroButton을 사용하여 오브 버튼 생성
        val orbButton = com.example.theorb.ui.RetroButton.createImageButton(
            image = orb.getDrawable(),
            imageSize = 45f,
            defaultImage = if (isSelected) ResourceManager.getRetroSquarePosDefault() else ResourceManager.getRetroSquareNagDefault(),
            eventImage = if (isSelected) ResourceManager.getRetroSquarePosEvent() else ResourceManager.getRetroSquareNagEvent(),
            buttonSize = 90f
        ) {
            selectedOrbData = orb
            saveData.selectedOrb = orb.id
            onOrbSelected(orb)
            // 모달 새로고침
            refreshModal(onOrbSelected) { hide() }
        }

        return orbButton
    }

    private fun refreshModal(onOrbSelected: (OrbData) -> Unit, onClose: () -> Unit) {
        hide()
        show(onClose, onOrbSelected)
    }

    fun hide() {
        backgroundOverlay?.remove()
        dialogContainer?.remove()
        backgroundOverlay = null
        dialogContainer = null
    }
}
