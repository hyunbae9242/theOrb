package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.example.theorb.TheOrb
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BackgroundRenderer
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.ui.ModalDialog
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.UpgradeType
import com.example.theorb.upgrades.UpgradeCategory
import com.example.theorb.util.ResourceManager

class UpgradeScreen(private val game: TheOrb) : BaseScreen() {
    private val uiStage = Stage(viewport)
    private val backgroundRenderer = BackgroundRenderer()
    private lateinit var goldLabel: Label
    private val upgradeLabels = mutableMapOf<UpgradeType, Label>()
    private val upgradeButtons = mutableMapOf<UpgradeType, ImageButton>()
    private lateinit var modalDialog: ModalDialog

    private var selectedTab = UpgradeCategory.ATTACK
    private val tabButtons = mutableMapOf<UpgradeCategory, ImageButton>()
    private lateinit var upgradeScrollPane: ScrollPane
    private var isUIInitialized = false
    private var needsUpdate = false

    private fun createRoundedRectWithBorder(width: Int, height: Int,
                                          bgColor: Color, borderColor: Color, borderWidth: Int): TextureRegionDrawable {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

        // 전체를 테두리 색으로 채우기
        pixmap.setColor(borderColor)
        pixmap.fill()

        // 내부를 배경색으로 채우기 (테두리를 남기기 위해 안쪽 영역만)
        pixmap.setColor(bgColor)
        pixmap.fillRectangle(borderWidth, borderWidth, width - 2 * borderWidth, height - 2 * borderWidth)

        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
    }

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = uiStage
        modalDialog = ModalDialog(uiStage, skin)

        // 홈화면과 동일한 배경 설정
        backgroundRenderer.setBackground("clouds02")
        backgroundRenderer.addToStage(uiStage, viewport.worldWidth, viewport.worldHeight)

        setupUI()
    }

    private fun setupUI() {
        val root = Table().apply {
            setFillParent(true)
            pad(SCREEN_PADDING)
        }
        uiStage.addActor(root)

        // 상단 골드 표시
        goldLabel = Label("GOLD: ${game.saveData.gold}", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        root.add(goldLabel).padBottom(20f).row()

        // 탭 + 리스트 전체를 하나의 패널로 감싸기
        val mainPanel = Table().apply {
            background = ResourceManager.getRectanglePanel430590()
            pad(20f)
        }

        // 탭 버튼들
        val tabRow = createTabButtons()
        mainPanel.add(tabRow).padBottom(20f).row()

        // 업그레이드 목록
        upgradeScrollPane = createUpgradeList()
        mainPanel.add(upgradeScrollPane).expand().fill().row()

        // 초기화 버튼을 패널 안에 추가
        val resetButton = ImageButton(ImageButton.ImageButtonStyle().apply {
            up = ResourceManager.getButtonCancelBg()
            down = ResourceManager.getButtonConfirmBg()
            over = ResourceManager.getButtonHighlightBg()
        })

        val resetLabel = Label("초기화", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = Color(0.7f, 0.3f, 0.3f, 1f)  // 부드러운 적색 (위험한 동작 표시)
        }
        resetButton.add(resetLabel)
        resetButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                showResetConfirmDialog()
            }
        })

        mainPanel.add(resetButton).width(virtualWidth * 0.3f).height(virtualHeight * 0.06f).padTop(15f).row()

        root.add(mainPanel).expand().fill().padBottom(20f).row()

        // 하단 네비게이션
        val bottomNavigation = BottomNavigation(game, skin, BottomNavigation.Tab.UPGRADE)
        val bottom = bottomNavigation.createBottomNavigation()
        root.add(bottom).growX().padBottom(6f)

        // UI 초기화 완료 플래그 설정
        isUIInitialized = true
        needsUpdate = true
    }

    private fun createTabButtons(): Table {
        val tabTable = Table()

        for (category in UpgradeCategory.values()) {
            val isSelected = category == selectedTab
            val buttonWidth = (virtualWidth * 0.25f).toInt()
            val buttonHeight = (virtualHeight * 0.06f).toInt()

            val tabButton = ImageButton(ImageButton.ImageButtonStyle().apply {
                up = if (isSelected) {
                    ResourceManager.getButtonHighlightBg()
                } else {
                    ResourceManager.getButtonCancelBg()
                }
                down = ResourceManager.getButtonConfirmBg()
                over = ResourceManager.getButtonHighlightBg()
            })

            // 탭 버튼에 텍스트 라벨 추가
            val tabLabel = Label(category.displayName, skin.get("label-large", Label.LabelStyle::class.java)).apply {
                color = TEXT_PRIMARY
            }
            tabButton.add(tabLabel)

            tabButtons[category] = tabButton

            tabButton.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    if (selectedTab != category) {
                        selectedTab = category
                        updateTabButtons()
                        updateUpgradeList()
                        needsUpdate = true
                    }
                }
            })

            tabTable.add(tabButton).width(virtualWidth * 0.25f).height(virtualHeight * 0.06f).pad(4f)
        }

        return tabTable
    }

    private fun updateTabButtons() {
        for ((category, button) in tabButtons) {
            val isSelected = category == selectedTab
            val style = button.style as ImageButton.ImageButtonStyle
            style.up = if (isSelected) {
                ResourceManager.getButtonHighlightBg()
            } else {
                ResourceManager.getButtonCancelBg()
            }

            // 라벨 색상 업데이트
            val label = button.children.firstOrNull() as? Label
            label?.color = Color(0.2f, 0.3f, 0.5f, 1f)  // 부드러운 짙은 파랑
        }
    }

    private fun updateUpgradeList() {
        val table = Table()
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        // 이전 탭의 버튼/라벨 참조들 클리어
        upgradeButtons.clear()
        upgradeLabels.clear()

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            val rowWidth = virtualWidth * 0.75f
            val rowHeight = rowWidth * (88f / 376f)  // 376x88 비율 유지
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        upgradeScrollPane.actor = table
        // 새로운 버튼들이 생성되었으므로 업데이트 필요
        needsUpdate = true
    }

    private fun createUpgradeList(): ScrollPane {
        val table = Table()
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            val rowWidth = virtualWidth * 0.75f
            val rowHeight = rowWidth * (88f / 376f)  // 376x88 비율 유지
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        return ScrollPane(table, skin).apply {
            setScrollingDisabled(true, false)
        }
    }

    private fun createUpgradeRow(upgradeType: UpgradeType): Table {
        val upgradeTable = Table()
        upgradeTable.background = ResourceManager.getListRowPanel37688()
        upgradeTable.pad(12f)

        // 업그레이드 정보
        val nameLabel = Label(upgradeType.displayName, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        val valueLabel = Label("", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        upgradeLabels[upgradeType] = valueLabel

        // 업그레이드 버튼
        val upgradeButton = ImageButton(ImageButton.ImageButtonStyle().apply {
            up = ResourceManager.getButtonConfirmBg()
            down = ResourceManager.getButtonConfirmBg()
            over = ResourceManager.getButtonHighlightBg()
            disabled = ResourceManager.getButtonCancelBg()
        })

        // 버튼에 "Lv UP (cost)" 텍스트 추가 - 초기값으로 설정, 실제 값은 updateUpgradeDisplay에서 업데이트
        val currentLevel = UpgradeManager.getUpgradeLevel(game.saveData, upgradeType)
        val initialCost = if (currentLevel < upgradeType.maxLevel) upgradeType.getCostForLevel(currentLevel) else -1
        val initialText = if (currentLevel >= upgradeType.maxLevel) "MAX" else "Lv UP (${initialCost}G)"

        val buttonLabel = Label(initialText, skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(0.2f, 0.3f, 0.5f, 1f)  // 부드러운 짙은 파랑
        }
        upgradeButton.add(buttonLabel)
        upgradeButtons[upgradeType] = upgradeButton

        upgradeButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (UpgradeManager.purchaseUpgrade(game.saveData, upgradeType)) {
                    SaveManager.save(game.saveData)
                    // 즉시 업데이트 플래그 설정
                    needsUpdate = true
                }
            }
        })

        // 레이아웃
        val leftTable = Table()
        leftTable.add(nameLabel).expand().left().row()
        leftTable.add(valueLabel).expand().left()

        upgradeTable.add(leftTable).expand().fill().left().padLeft(8f)
        upgradeTable.add(upgradeButton).width(120f).height(35f).right().padRight(8f)

        return upgradeTable
    }

    private fun updateUpgradeDisplay() {
        goldLabel.setText("GOLD: ${game.saveData.gold}")

        // 현재 선택된 탭의 업그레이드만 업데이트
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        for (upgradeType in filteredUpgrades) {
            val level = UpgradeManager.getUpgradeLevel(game.saveData, upgradeType)
            val maxLevel = upgradeType.maxLevel
            val currentValue = UpgradeManager.getUpgradeValue(game.saveData, upgradeType)
            val cost = if (level < maxLevel) upgradeType.getCostForLevel(level) else -1

            // 새로운 형식: "+12 (6/50)"
            val valueText = if (level >= maxLevel) {
                "${formatValue(upgradeType, currentValue)} ($level/$maxLevel)"
            } else {
                "${formatValue(upgradeType, currentValue)} ($level/$maxLevel)"
            }

            upgradeLabels[upgradeType]?.setText(valueText)

            // 현재 탭에 표시된 버튼만 업데이트
            upgradeButtons[upgradeType]?.let { button ->
                val canUpgrade = UpgradeManager.canUpgrade(game.saveData, upgradeType)
                button.isDisabled = !canUpgrade

                // 버튼 텍스트 업데이트: "Lv UP (xxG)"
                val buttonLabel = button.children.firstOrNull() as? Label
                val buttonText = if (level >= maxLevel) {
                    "MAX"
                } else {
                    "Lv UP (${cost}G)"
                }
                buttonLabel?.setText(buttonText)
                buttonLabel?.invalidateHierarchy()  // UI 강제 새로고침

                // 버튼 상태에 따른 글자색 설정
                buttonLabel?.color = if (canUpgrade) {
                    Color(0.2f, 0.3f, 0.5f, 1f)  // 활성화된 버튼 (부드러운 짙은 파랑)
                } else {
                    Color(0.6f, 0.6f, 0.6f, 1f)  // 비활성화된 버튼 (부드러운 회색)
                }
            }
        }
    }

    private fun formatValue(upgradeType: UpgradeType, value: Float): String {
        return when (upgradeType) {
            UpgradeType.DAMAGE -> "+${value.toInt()}"
            UpgradeType.CRITICAL_CHANCE -> "+${(value).toInt()}%"
            UpgradeType.CRITICAL_DAMAGE -> "+${(value).toInt()}%"
            UpgradeType.HEALTH -> "+${value.toInt()}"
            UpgradeType.ARMOR -> "+${value.toInt()}"
            UpgradeType.REGENERATION -> "+${value.toInt()}/s"
            UpgradeType.RANGE -> "+${(value * 100).toInt()}%"
            UpgradeType.COOLDOWN_REDUCTION -> "-${(value * 100).toInt()}%"
            UpgradeType.MOVEMENT_SPEED -> "+${(value * 100).toInt()}%"
            UpgradeType.GOLD_BONUS -> "+${(value * 100).toInt()}%"
        }
    }

    private fun showResetConfirmDialog() {
        modalDialog.show(
            title = "업그레이드 초기화",
            message = "모든 업그레이드를 초기화하고\n사용한 골드를 환불받겠습니까?",
            confirmText = "초기화",
            cancelText = "취소",
            confirmColor = Color.RED,
            onConfirm = { performReset() },
            onCancel = { /* 아무것도 하지 않음 */ }
        )
    }

    private fun performReset() {
        val refundAmount = UpgradeManager.resetAllUpgrades(game.saveData)
        SaveManager.save(game.saveData)
        // 즉시 업데이트 플래그 설정
        needsUpdate = true

        Gdx.app.log("UpgradeScreen", "업그레이드 초기화 완료! 환불된 골드: $refundAmount")
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // UI가 초기화된 후에 업데이트 필요 시에만 업데이트
        if (isUIInitialized && needsUpdate) {
            updateUpgradeDisplay()
            needsUpdate = false
        }

        uiStage.act(delta)
        uiStage.draw()
    }

    override fun dispose() {
        super.dispose()
        uiStage.dispose()
        backgroundRenderer.dispose()
        disposeSharedResources()
    }
}
