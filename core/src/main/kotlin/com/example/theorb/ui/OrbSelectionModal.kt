package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
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

    private val modalWidth = 350f
    private val modalHeight = 500f

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
        dialogContainer!!.setPosition(
            (stageWidth - modalWidth) / 2f,
            (stageHeight - modalHeight) / 2f
        )
    }

    private fun createDialogContainer(onClose: () -> Unit, onOrbSelected: (OrbData) -> Unit) {
        dialogContainer = Table().apply {
            background = ResourceManager.getHomeOrbSelectionPanel()
            setSize(modalWidth, modalHeight)
        }

        // 제목 섹션 (배경 포함)
        val titleSection = Table()
        val titleLabel = Label("SELECT ORB", skin.get("label-large-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }
        titleSection.add(titleLabel).center()

        // 상단: 선택된 오브 정보
        val selectedOrbSection = createSelectedOrbSection()

        // 중하단: 오브 그리드 리스트
        val orbGridSection = createOrbGridSection(onOrbSelected)
        // 하단: 닫기 버튼
        val buttonSection = createBottomButtonSection(onClose)

        dialogContainer!!.apply {
            add(titleSection).padTop(32f).height(26f).row() // 32 + 26 = 58
            add(selectedOrbSection).pad(16f).height(96f).row() // 58 + 16 + 96 + 16 = 186
            add(orbGridSection).center().height(218f).row() // 500 - 186 - 96 = 218
            add(buttonSection).center().height(48f).padTop(16f).padBottom(32f) // 버튼 32 + 48 + 16 = 96
        }
    }

    private fun createSelectedOrbSection(): Table {
        val section = Table().apply {
            background = ResourceManager.getHomeOrbSelectPanel()
        }

        // 선택된 오브 이미지 (높이 118f에 맞게 조정)
        val selectedOrbImage = Image(selectedOrbData.getDrawable()).apply {
            setSize(64f, 64f)
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
            add(nameLabel).left().padBottom(8f).row()
            add(descLabel).left().expandX().fillX().row()
        }

        section.apply {
            add(selectedOrbImage).size(64f).left().padRight(16f)
            add(infoTable).left().expandX().fillX()
        }
        return section
    }

    private fun createOrbGridSection(onOrbSelected: (OrbData) -> Unit): Table {
        val gridTable = Table()

        val unlockedOrbs = OrbRegistry.getUnlockedOrbs()
        val columns = 3
        var row = 0
        var col = 0

        for (orb in unlockedOrbs) {
            val orbButton = createOrbButton(orb, onOrbSelected)

            gridTable.add(orbButton).size(48f, 48f).pad(8f)

            col++
            if (col >= columns) {
                gridTable.row()
                col = 0
                row++
            }
        }
        val container = Table()
        val scrollPane = ScrollPane(gridTable, skin)
        scrollPane.setScrollingDisabled(true, false) // 가로 스크롤 비활성화
        container.add(scrollPane).grow()
        return container
    }

    private fun createBottomButtonSection(onClose: () -> Unit): Table {
        val table = Table()

        val closeButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getCloseBasePos(),
            eventImage = ResourceManager.getCloseEventPos(),
        ) {
            onClose()
        }

        table.add(closeButton).center()

        return table
    }

    private fun createOrbButton(orb: OrbData, onOrbSelected: (OrbData) -> Unit): com.badlogic.gdx.scenes.scene2d.ui.Stack {
        val isSelected = orb.id == selectedOrbData.id

        // RetroButton으로 오브 버튼 생성
        val button = RetroButtonV01.createImageButton(
            image = orb.getDrawable(),
            imageSize = 48f,
            defaultImage = if(isSelected) ResourceManager.getSquareEventPanel() else ResourceManager.getSquareBasePanel(),
            eventImage = if(isSelected) ResourceManager.getSquareEventPanel() else ResourceManager.getSquareBasePanel(),
            buttonSize = 48f
        ) {
            selectedOrbData = orb
            saveData.selectedOrb = orb.id
            onOrbSelected(orb)
            // 모달 새로고침
            refreshModal(onOrbSelected) { hide() }
        }

        return button
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
