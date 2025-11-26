package com.example.theorb.modal

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.balance.LevelUpOptionData
import com.example.theorb.data.SaveData
import com.example.theorb.screens.BaseScreen
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.ui.ToastMessage
import com.example.theorb.upgrades.LevelUpManager
import com.example.theorb.util.ResourceManager

class LevelUpSelectionModal(private val stage: Stage, private val skin: Skin, private val saveData: SaveData) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null
    private var selection0: Table? = null
    private var selection1: Table? = null
    private var selection2: Table? = null
    private var selectedCount: Int = -1
    private var currentOptions: List<LevelUpOptionData> = emptyList()
    private var onSelectionCallback: ((LevelUpOptionData) -> Unit)? = null
    private var onRerollCallback: (() -> Unit)? = null

    fun show(
        options: List<LevelUpOptionData>,
        onSelection: (LevelUpOptionData) -> Unit,
        onReroll: (() -> Unit)? = null
    ) {
        this.currentOptions = options
        this.onSelectionCallback = onSelection
        this.onRerollCallback = onReroll
        // 기존 다이얼로그가 있으면 제거
        hide()

        // Stage 크기 미리 가져오기
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        // 반투명 배경 오버레이 생성
        createBackgroundOverlay(stageWidth, stageHeight)

        // 다이얼로그 컨테이너 생성
        createDialogContainer()

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

    private fun createDialogContainer() {
        dialogContainer = Table().apply {
            top()
            center()
        }

        // 제목
        val titleLabel = Label("Level Up!", this@LevelUpSelectionModal.skin.get("label-large-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 선택지
        selection0 = createSelection(currentOptions[0], 0)
        selection1 = createSelection(currentOptions[1], 1)
        selection2 = createSelection(currentOptions[2], 2)
        selectedCount = -1

        val selectionTable = Table().apply {
            top()
        }
        selectionTable.add(selection0).padRight(16f)
        selectionTable.add(selection1).padRight(16f)
        selectionTable.add(selection2)

        val buttonTable = createButtonTable()

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).padBottom(32f).row()
            add(selectionTable).padBottom(32f).row()
            add(buttonTable)
        }
    }

    private fun createSelection(option: LevelUpOptionData, index: Int): Table {
        val selection = Table().apply{
            background = ResourceManager.getLevelUpSelectionBasePanel()
            touchable = Touchable.enabled
            setSize(128f, 256f)
            top()
            pad(16f, 8f, 16f, 8f)
        }

        val tierLabel = Label(option.type.tier.displayName, this@LevelUpSelectionModal.skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
            color = option.type.tier.color
        }
        val nameLabel = Label(option.type.displayName, this@LevelUpSelectionModal.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }
        val descLabel = Label(option.getNextDescription(), this@LevelUpSelectionModal.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
            wrap = true
        }

        selection.add(tierLabel).padBottom(8f).row()
        selection.add(nameLabel).padBottom(8f).row()
        selection.add(descLabel).expand().fill()

        selection.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                selection0?.background = ResourceManager.getLevelUpSelectionBasePanel()
                selection1?.background = ResourceManager.getLevelUpSelectionBasePanel()
                selection2?.background = ResourceManager.getLevelUpSelectionBasePanel()
                selection.background = ResourceManager.getLevelUpSelectionEventPanel()
                selectedCount = index
            }
        })

        return selection
    }

    private fun createButtonTable(): Table {
        val table = Table().apply {
            top()
        }

        val selectButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getSelectBasePos(),
            eventImage = ResourceManager.getSelectEventPos(),
        ) {
            if (selectedCount == -1) {
                ToastMessage.show(
                    stage,
                    "스킬을 선택해 주세요",
                    skin,
                    2f
                )
            } else {
                onSelectionCallback?.invoke(currentOptions[selectedCount])
            }
        }
        table.add(selectButton)

        if(LevelUpManager.canReroll(saveData)) {
            val rerollLabel = Label("${saveData.currentRerollCount}/${saveData.maxRerollCount}", this@LevelUpSelectionModal.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_PRIMARY
            }
            val rerollButton = RetroButtonV01.createIconButton(
                defaultImage = ResourceManager.getRerollBasePos(),
                eventImage = ResourceManager.getRerollEventPos(),
            ) {
                handleReroll()
            }

            table.add(rerollButton).padLeft(16f)
            table.add(rerollLabel).padLeft(8f)
        }

        return table
    }

    private fun handleReroll() {
        if (LevelUpManager.useReroll(saveData)) {
            // 외부에서 전달받은 리롤 콜백 호출
            onRerollCallback?.invoke()
            ToastMessage.show(
                stage,
                "선택지를 리롤했습니다! (남은 횟수: ${saveData.currentRerollCount})",
                skin,
                1.5f
            )
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
