package com.example.theorb.modal

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.screens.BaseScreen
import com.example.theorb.stages.StageManager
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.util.ResourceManager

class StageSelectModal(
    private val stage: Stage,
    private val skin: Skin
) {
    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null
    private lateinit var saveData: SaveData
    private var onStageSelected: ((Int) -> Unit)? = null

    fun show(saveData: SaveData, onStageSelected: (Int) -> Unit) {
        this.saveData = saveData
        this.onStageSelected = onStageSelected

        // 기존 모달이 있으면 제거
        hide()

        // 반투명 배경
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        backgroundOverlay = Image(skin.getDrawable("white")).apply {
            color = Color(0f, 0f, 0f, 0.7f)
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
        }

        createDialogContainer()

        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙에 위치
        centerDialog(stageWidth, stageHeight)
    }

    private fun createDialogContainer() {
        dialogContainer = Table().apply {
            background = ResourceManager.getCommonModalPanel()
            pad(20f)
        }

        // 타이틀
        val titleLabel = Label("스테이지 선택", skin.get("label-large-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        // 스테이지 리스트
        val stageList = createStageList()

        // 닫기 버튼
        val closeButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getCloseBasePos(),
            eventImage = ResourceManager.getCloseEventPos()
        ) {
            hide()
        }

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(16f).row()
            add(stageList).center().height(320f).padBottom(16f).row()
            add(closeButton).center()
        }
    }

    private fun createStageList(): ScrollPane {
        val table = Table().apply {
            top()
        }

        val allStages = StageManager.getAllStages()

        allStages.forEach { stageData ->
            val isUnlocked = stageData.stageId <= saveData.unlockedStages
            val isCleared = stageData.stageId <= saveData.highestClearedStage
            val isCurrent = stageData.stageId == saveData.currentStage

            val stageRow = createStageRow(
                stageData.stageId,
                stageData.stageName,
                stageData.description,
                stageData.enemyHpMultiplier,
                stageData.goldMultiplier,
                isUnlocked,
                isCleared,
                isCurrent
            )
            table.add(stageRow).padBottom(8f).row()
        }

        return ScrollPane(table, skin).apply {
            setScrollingDisabled(true, false)
        }
    }

    private fun createStageRow(
        stageId: Int,
        stageName: String,
        description: String,
        enemyHpMul: Float,
        goldMul: Float,
        isUnlocked: Boolean,
        isCleared: Boolean,
        isCurrent: Boolean
    ): Table {
        val rowTable = Table().apply {
            background = ResourceManager.getUpgradeListPanel()
            pad(12f)
        }

        // 왼쪽: 스테이지 정보
        val infoTable = Table().apply {
            left()
        }

        // 스테이지 번호와 이름
        val nameLabel = Label(
            if (isUnlocked) "Stage $stageId: $stageName" else "Stage $stageId: ???",
            skin.get("label-default", Label.LabelStyle::class.java)
        ).apply {
            color = if (isCurrent) BaseScreen.TEXT_PRIMARY else BaseScreen.TEXT_SECONDARY
        }

        // 설명
        val descLabel = Label(
            if (isUnlocked) description else "잠금됨",
            skin.get("label-small", Label.LabelStyle::class.java)
        ).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        // 난이도 정보 (해금된 경우에만)
        val difficultyLabel = if (isUnlocked) {
            Label(
                "난이도: 적 ${String.format("%.1f", enemyHpMul)}배 | 보상 ${String.format("%.1f", goldMul)}배",
                skin.get("label-small", Label.LabelStyle::class.java)
            ).apply {
                color = when {
                    enemyHpMul <= 1.5f -> Color(0.5f, 1f, 0.5f, 1f) // 쉬움 (초록)
                    enemyHpMul <= 3.0f -> Color(1f, 1f, 0.5f, 1f) // 보통 (노랑)
                    else -> Color(1f, 0.5f, 0.5f, 1f) // 어려움 (빨강)
                }
            }
        } else null

        // 상태 라벨
        val statusLabel = Label(
            when {
                !isUnlocked -> "[잠금]"
                isCleared -> "[클리어]"
                isCurrent -> "[선택됨]"
                else -> ""
            },
            skin.get("label-small", Label.LabelStyle::class.java)
        ).apply {
            color = when {
                !isUnlocked -> BaseScreen.TEXT_SECONDARY
                isCleared -> BaseScreen.SUCCESS
                isCurrent -> BaseScreen.ACCENT
                else -> BaseScreen.TEXT_PRIMARY
            }
        }

        infoTable.add(nameLabel).left().row()
        infoTable.add(descLabel).left().padTop(4f).row()
        if (difficultyLabel != null) {
            infoTable.add(difficultyLabel).left().padTop(4f).row()
        }
        infoTable.add(statusLabel).left().padTop(4f)

        // 오른쪽: 선택 버튼
        val selectButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getConfirmBasePos(),
            eventImage = ResourceManager.getConfirmEventPos(),
            disabledImage = ResourceManager.getConfirmBaseNag(),
            isEnabled = isUnlocked && !isCurrent,
        ) {
            selectStage(stageId)
        }

        rowTable.add(infoTable).expand().fill().left()
        rowTable.add(selectButton).right()

        return rowTable
    }

    private fun selectStage(stageId: Int) {
        saveData.currentStage = stageId
        SaveManager.save(saveData)

        // 콜백 호출
        onStageSelected?.invoke(stageId)

        // 모달 닫기
        hide()
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

    fun isVisible(): Boolean {
        return dialogContainer != null
    }
}
