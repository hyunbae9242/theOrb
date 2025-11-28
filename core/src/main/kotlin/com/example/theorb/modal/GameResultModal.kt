package com.example.theorb.modal

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.example.theorb.screens.BaseScreen
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class GameResultModal(private val stage: Stage, private val skin: Skin) {

    private var backgroundOverlay: Image? = null
    private var dialogContainer: Table? = null

    fun show(
        title: String, // "Victory!" 또는 "Game Over!"
        goldEarned: Int,
        orbsEarned: Int,
        skillStats: Map<String, Long>,
        acquiredActiveSkills: List<String> = emptyList(),
        acquiredSubSkills: List<String> = emptyList(),
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

        createDialogContainer(title, goldEarned, orbsEarned, skillStats, acquiredActiveSkills, acquiredSubSkills, onHome, onRestart)

        stage.addActor(backgroundOverlay)
        stage.addActor(dialogContainer)

        // 중앙에 위치
        centerDialog(stageWidth, stageHeight)
    }

    private fun createDialogContainer(
        title: String,
        goldEarned: Int,
        orbsEarned: Int,
        skillStats: Map<String, Long>,
        acquiredActiveSkills: List<String>,
        acquiredSubSkills: List<String>,
        onHome: () -> Unit,
        onRestart: () -> Unit
    ) {
        dialogContainer = Table().apply {
            background = ResourceManager.getCommonModalPanel()
            pad(20f)
        }

        // 제목 (Victory! 또는 Game Over!)
        val titleLabel = Label(title, skin.get("label-large-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.DANGER
        }

        // 골드/오브 획득 정보
        val rewardsTable = Table().apply {
            val goldLabel = Label("골드: +${formatNumber(goldEarned)}", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = Color(1f, 0.84f, 0f, 1f) // 골드 색상
            }
            val gemsLabel = Label("오브: +$orbsEarned", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = Color(0.5f, 1f, 1f, 1f) // 시안 색상
            }

            add(goldLabel).padRight(16f)
            add(gemsLabel)
        }

        // 획득한 스킬 목록
        val acquiredSkillsTable = Table()
        if (acquiredActiveSkills.isNotEmpty() || acquiredSubSkills.isNotEmpty()) {
            val acquiredTitle = Label("획득한 스킬:", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_PRIMARY
            }
            acquiredSkillsTable.add(acquiredTitle).colspan(2).padBottom(8f).row()

            // 액티브 스킬
            acquiredActiveSkills.forEach { skillName ->
                val skillLabel = Label("⚔ $skillName", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = Color(1.0f, 0.8f, 0.2f, 1f) // 황금색
                }
                acquiredSkillsTable.add(skillLabel).left().colspan(2).row()
            }

            // 보조 스킬
            acquiredSubSkills.forEach { subSkillName ->
                val subSkillLabel = Label("◆ $subSkillName", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = Color(0.4f, 0.8f, 1.0f, 1f) // 파란색
                }
                acquiredSkillsTable.add(subSkillLabel).left().colspan(2).row()
            }
        }

        // 스킬별 데미지 통계
        val statsTable = Table()
        val statsTitle = Label("스킬 데미지 통계:", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }
        statsTable.add(statsTitle).colspan(2).padBottom(8f).row()

        skillStats.forEach { (skillName, damage) ->
            val skillLabel = Label(skillName, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }
            val damageLabel = Label(formatNumber(damage.toInt()), skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_PRIMARY
            }

            statsTable.add(skillLabel).left().padRight(16f)
            statsTable.add(damageLabel).right().row()
        }

        // 버튼들
        val buttonTable = Table()

        val homeButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getQuitBasePos(),
            eventImage = ResourceManager.getQuitEventPos()
        ) {
            onHome()
        }

        val restartButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getAgainBasePos(),
            eventImage = ResourceManager.getAgainEventPos()
        ) {
            onRestart()
        }

        buttonTable.add(homeButton).padRight(16f)
        buttonTable.add(restartButton)

        // 레이아웃 구성
        dialogContainer!!.apply {
            add(titleLabel).center().padBottom(16f).row()
            add(rewardsTable).center().padBottom(16f).row()

            // 획득한 스킬이 있으면 표시
            if (acquiredActiveSkills.isNotEmpty() || acquiredSubSkills.isNotEmpty()) {
                add(acquiredSkillsTable).center().padBottom(16f).row()
            }

            add(statsTable).center().expandY().fillY().padBottom(16f).row()
            add(buttonTable).center()
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
