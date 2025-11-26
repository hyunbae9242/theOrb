package com.example.theorb.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveData
import com.example.theorb.screens.BaseScreen
import com.example.theorb.upgrades.LevelUpManager
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.getPercent

class InGameStatusPanel(
    private val stage: Stage,
    private val skin: Skin,
    private val saveData: SaveData
) {
    private lateinit var mainContainer: Table
    private lateinit var inventoryScrollPane: ScrollPane
    private lateinit var levelLabel: Label
    private lateinit var expLabel: Label
    private lateinit var expBarFill: Table
    private lateinit var hpLabel: Label
    private lateinit var hpBarFill: Table
    private lateinit var esdLabel: Label
    private lateinit var esdBarFill: Table

    fun createUI(availableHeight: Float? = null): Table {
        mainContainer = Table().apply {
            background = ResourceManager.getGameStatusBackPanel()
            setSize(480f, 280f)
            pad(16f)
            top()
        }
        val levelTable = createLevelTable()
        val hpTable = createHpTable()
        val esdTable = createEsdTable()

        mainContainer.add(levelTable).colspan(2).expandX().fillX().padBottom(8f).row()
        mainContainer.add(hpTable).left().padBottom(8f).row()
        mainContainer.add(esdTable).left().padBottom(8f).row()
        return mainContainer
    }

    private fun createLevelTable(): Table {
        val level = saveData.inGameLevel
        val exp = saveData.inGameExp
        val expPercentage = getPercent(exp, LevelUpManager.getRequiredExpForLevel(level))

        levelLabel = Label("Level $level", skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }
        expLabel = Label("${expPercentage}($exp/${LevelUpManager.getRequiredExpForLevel(level)})", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        val expBarWidth = 448f // mainContainer 480 - pad 32 = 448
        val expBarHeight = 16f
        val expBarBackground = Table().apply {
            background = ResourceManager.getExpGraphBackPanel()
            setSize(expBarWidth, expBarHeight)
        }
        expBarFill = Table().apply {
            background = ResourceManager.getExpGraphFrontPanel()
        }
        val currentExpRatio = exp.toFloat() / LevelUpManager.getRequiredExpForLevel(level)
        expBarBackground.add(expBarFill).width(expBarWidth * currentExpRatio).height(expBarHeight).left().expand()

        val levelTable = Table().apply {
            top()
        }
        levelTable.add(levelLabel).left()
        levelTable.add().expandX()
        levelTable.add(expLabel).right().row()
        levelTable.add(expBarBackground).colspan(3).expandX().fillX().height(expBarHeight).padTop(8f)

        return levelTable
    }

    private fun createHpTable(): Table {
        val currentHp = saveData.currentHp
        val maxHp = saveData.maxHp

        hpLabel = Label("HP: $currentHp/$maxHp", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        val barWidth = 208f
        val barHeight = 16f

        val hpBarBackground = Table().apply {
            background = ResourceManager.getHpGraphBackPanel()
            setSize(barWidth, barHeight)
        }

        hpBarFill = Table().apply {
            background = ResourceManager.getHpGraphFrontPanel()
        }

        val hpRatio = currentHp.toFloat() / maxHp
        hpBarBackground.add(hpBarFill).width(barWidth * hpRatio).height(barHeight).left().expand()

        val hpTable = Table().apply {
            top()
            left()
        }
        hpTable.add(hpLabel).left().row()
        hpTable.add(hpBarBackground).width(barWidth).height(barHeight).padTop(8f)

        return hpTable
    }

    private fun createEsdTable(): Table {
        val currentEsd = saveData.currentEnergyShield
        val maxEsd = saveData.maxEnergyShield

        esdLabel = Label("ESD: $currentEsd/$maxEsd", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        val barWidth = 208f
        val barHeight = 16f

        val esdBarBackground = Table().apply {
            background = ResourceManager.getHpGraphBackPanel()
            setSize(barWidth, barHeight)
        }

        esdBarFill = Table().apply {
            background = ResourceManager.getEsdGraphFrontPanel()
        }

        val esdRatio = if (maxEsd > 0) currentEsd.toFloat() / maxEsd else 0f
        esdBarBackground.add(esdBarFill).width(barWidth * esdRatio).height(barHeight).left().expand()

        val esdTable = Table().apply {
            top()
        }
        esdTable.add(esdLabel).left().row()
        esdTable.add(esdBarBackground).width(barWidth).height(barHeight).padTop(8f)

        return esdTable
    }


    fun refreshUI() {
        // 레벨 & 경험치
        val level = saveData.inGameLevel
        val exp = saveData.inGameExp
        val requiredExp = LevelUpManager.getRequiredExpForLevel(level)
        val expPercentage = getPercent(exp, requiredExp)

        levelLabel.setText("Level $level")
        expLabel.setText("${expPercentage}($exp/$requiredExp)")

        val expBarWidth = 448f
        val currentExpRatio = exp.toFloat() / requiredExp

        expBarFill.clearChildren()
        expBarFill.parent?.let { parent ->
            if (parent is Table) {
                val cell = parent.getCell(expBarFill)
                cell?.width(expBarWidth * currentExpRatio)
            }
        }

        // HP
        val currentHp = saveData.currentHp
        val maxHp = saveData.maxHp
        hpLabel.setText("HP: $currentHp/$maxHp")

        val hpBarWidth = 208f
        val hpRatio = if (maxHp > 0) currentHp.toFloat() / maxHp else 0f

        hpBarFill.clearChildren()
        hpBarFill.parent?.let { parent ->
            if (parent is Table) {
                val cell = parent.getCell(hpBarFill)
                cell?.width(hpBarWidth * hpRatio)
            }
        }

        // 에너지 실드
        val currentEsd = saveData.currentEnergyShield
        val maxEsd = saveData.maxEnergyShield
        esdLabel.setText("ESD: $currentEsd/$maxEsd")

        val esdBarWidth = 208f
        val esdRatio = if (maxEsd > 0) currentEsd.toFloat() / maxEsd else 0f

        esdBarFill.clearChildren()
        esdBarFill.parent?.let { parent ->
            if (parent is Table) {
                val cell = parent.getCell(esdBarFill)
                cell?.width(esdBarWidth * esdRatio)
            }
        }
    }
}
