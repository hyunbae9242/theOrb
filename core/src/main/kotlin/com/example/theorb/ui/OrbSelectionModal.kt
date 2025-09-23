package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
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
        ?: OrbRegistry.getOrbById("base_orb")!!

    fun show(onClose: () -> Unit, onOrbSelected: (OrbData) -> Unit) {
        // 반투명 배경
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        backgroundOverlay = Image(skin.getDrawable("white")).apply {
            color = Color(0f, 0f, 0f, 0.7f)
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
        }

        createDialogContainer(onClose, onOrbSelected)

        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙 정렬 (480x600 크기 기준)
        dialogContainer!!.setPosition(
            (stageWidth - 480f) / 2f,
            (stageHeight - 600f) / 2f
        )
    }

    private fun createDialogContainer(onClose: () -> Unit, onOrbSelected: (OrbData) -> Unit) {
        dialogContainer = Table().apply {
            background = ResourceManager.getRectanglePanel430278()
            pad(20f)
            setSize(480f, 600f)
        }

        // 제목
        val titleLabel = Label("오브 선택", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 상단: 선택된 오브 정보
        val selectedOrbSection = createSelectedOrbSection()

        // 중하단: 오브 그리드 리스트
        val orbGridSection = createOrbGridSection(onOrbSelected)

        // 닫기 버튼
        val closeButton = TextButton("닫기", TextButton.TextButtonStyle().apply {
            font = skin.get("btn", TextButton.TextButtonStyle::class.java).font
            fontColor = Color.WHITE
            up = ResourceManager.getButtonCancelBg()
            down = ResourceManager.getButtonHighlightBg()
            over = ResourceManager.getButtonHighlightBg()
        }).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onClose()
                }
            })
        }

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(20f).row()
            add(selectedOrbSection).growX().padBottom(20f).row()
            add(orbGridSection).grow().padBottom(20f).row()
            add(closeButton).size(100f, 40f).center()
        }
    }

    private fun createSelectedOrbSection(): Table {
        val section = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = Color(0.2f, 0.2f, 0.2f, 0.8f)
            pad(15f)
        }

        // 선택된 오브 이미지
        val selectedOrbImage = Image(selectedOrbData.getDrawable()).apply {
            setSize(80f, 80f)
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
            add(selectedOrbImage).left().padRight(20f)
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

            gridTable.add(orbButton).size(120f, 120f).pad(10f)

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

    private fun createOrbButton(orb: OrbData, onOrbSelected: (OrbData) -> Unit): Table {
        val buttonTable = Table().apply {
            // 선택된 오브인지 확인
            val isSelected = orb.id == selectedOrbData.id
            background = if (isSelected) {
                ResourceManager.getSquareButtonHighlight()
            } else {
                ResourceManager.getSquareButton1()
            }
        }

        val orbImage = Image(orb.getDrawable()).apply {
            setSize(60f, 60f)
        }

        val nameLabel = Label(orb.name, skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
            wrap = true
        }

        buttonTable.apply {
            add(orbImage).center().padBottom(5f).row()
            add(nameLabel).center().width(100f)

            touchable = Touchable.enabled
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    selectedOrbData = orb
                    saveData.selectedOrb = orb.id
                    onOrbSelected(orb)
                    // 모달 새로고침
                    refreshModal(onOrbSelected) { hide() }
                }
            })
        }

        return buttonTable
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
