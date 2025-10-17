package com.example.theorb.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveManager
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

/**
 * 재사용 가능한 상단 바 (골드/오브 + 설정 버튼)
 */
class TopBar(
    private val stage: Stage,
    private val skin: Skin
) {
    private lateinit var goldLabel: Label
    private lateinit var gemLabel: Label
    private lateinit var settingsModal: SettingsModal

    fun createTopBar(): Table {
        settingsModal = SettingsModal(stage, skin)

        val topBar = Table().apply {
            background = ResourceManager.getCommonTopPanel()
        }

        // 좌측 - 골드/오브 정보
        goldLabel = Label(
            "골드: ${formatNumber(BaseScreen.gameObject.saveData.gold)}",
            skin.get("label-default", Label.LabelStyle::class.java)
        ).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        gemLabel = Label(
            "오브: ${formatNumber(BaseScreen.gameObject.saveData.orbs)}",
            skin.get("label-default", Label.LabelStyle::class.java)
        ).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        val leftInfo = Table().apply {
            add(goldLabel).left().row()
            add(gemLabel).left()
        }

        // 우측 - 설정 버튼
        val settingsButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getGearBasePos(),
            eventImage = ResourceManager.getGearEventPos()
        ) {
            openSettingsModal()
        }

        // 레이아웃 구성
        topBar.add(leftInfo).left().pad(BaseScreen.COMPONENT_PADDING).expandX()
        topBar.add(settingsButton).size(BaseScreen.BUTTON_SIZE).right().pad(BaseScreen.COMPONENT_PADDING)

        return topBar
    }

    private fun openSettingsModal() {
        settingsModal.show(
            currentBackground = BaseScreen.gameObject.saveData.selectedBackground,
            onClose = {
                settingsModal.hide()
            },
            onBackgroundChange = { newBackground ->
                BaseScreen.changeSharedBackground(
                    stage,
                    newBackground,
                    stage.viewport.worldWidth,
                    stage.viewport.worldHeight
                )
                BaseScreen.gameObject.saveData.selectedBackground = newBackground
                SaveManager.save(BaseScreen.gameObject.saveData)
            }
        )
    }

    /**
     * 골드/오브 정보 업데이트
     */
    fun updateCurrency() {
        goldLabel.setText("골드: ${formatNumber(BaseScreen.gameObject.saveData.gold)}")
        gemLabel.setText("오브: ${formatNumber(BaseScreen.gameObject.saveData.orbs)}")
    }
}
