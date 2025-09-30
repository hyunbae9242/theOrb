package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class VictoryModal(private val stage: Stage, private val skin: Skin) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null

    fun show(
        goldEarned: Int,
        gemsEarned: Int,
        skillStats: Map<String, Long>,
        onHome: () -> Unit,
        onRestart: () -> Unit
    ) {
        // 반투명 배경
        val stageWidth = stage.viewport.worldWidth
        val stageHeight = stage.viewport.worldHeight

        backgroundOverlay = Image(skin.getDrawable("white")).apply {
            color = Color(0f, 0f, 0f, 0.7f)
            setSize(stageWidth, stageHeight)
            setPosition(0f, 0f)
            touchable = Touchable.enabled
        }

        createDialogContainer(goldEarned, gemsEarned, skillStats, onHome, onRestart)

        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙에 위치 (340x366 크기 기준)
        dialogContainer!!.setPosition(
            (stageWidth - 340f) / 2f,
            (stageHeight - 366f) / 2f
        )
    }

    private fun createDialogContainer(
        goldEarned: Int,
        gemsEarned: Int,
        skillStats: Map<String, Long>,
        onHome: () -> Unit,
        onRestart: () -> Unit
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getSquarePanel360()
            pad(20f)
            // Victory Panel 크기에 맞게 고정 크기 설정 (340x366)
            setSize(340f, 366f)
        }

        // 승리 제목
        val titleLabel = Label("Victory!", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.ACCENT
        }

        // 골드/젬 획득 정보
        val rewardsTable = Table().apply {
            val goldLabel = Label("골드: +${formatNumber(goldEarned)}", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = Color(1f, 0.84f, 0f, 1f) // 골드 색상
            }
            val gemsLabel = Label("젬: +$gemsEarned", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = Color(0.5f, 1f, 1f, 1f) // 시안 색상
            }

            add(goldLabel).padRight(15f)
            add(gemsLabel)
        }

        // 스킬별 데미지 통계
        val statsTable = Table()
        val statsTitle = Label("스킬 데미지 통계:", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }
        statsTable.add(statsTitle).colspan(2).padBottom(10f).row()

        skillStats.forEach { (skillName, damage) ->
            val skillLabel = Label(skillName, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }
            val damageLabel = Label(formatNumber(damage.toInt()), skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_PRIMARY
            }

            statsTable.add(skillLabel).left().padRight(15f)
            statsTable.add(damageLabel).right().row()
        }

        // 버튼들
        val buttonTable = Table()

        val homeButton = RetroButton.createTextButton(
            text = "홈으로",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroRectangleNagDefault(),
            eventImage = ResourceManager.getRetroRectangleNagEvent(),
            buttonSize = 40f
        ) {
            onHome()
        }

        val restartButton = RetroButton.createTextButton(
            text = "다시하기",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
            eventImage = ResourceManager.getRetroRectanglePosEvent(),
            buttonSize = 40f
        ) {
            onRestart()
        }

        buttonTable.add(homeButton).size(100f, 40f).padRight(15f)
        buttonTable.add(restartButton).size(100f, 40f)

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(15f).row()
            add(rewardsTable).center().padBottom(15f).row()
            add(statsTable).center().padBottom(20f).row()
            add(buttonTable).center()
        }
    }

    fun hide() {
        backgroundOverlay?.remove()
        dialogContainer?.remove()
        backgroundOverlay = null
        dialogContainer = null
    }
}
