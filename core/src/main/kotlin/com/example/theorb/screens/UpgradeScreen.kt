package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.TheOrb
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.ui.ModalDialog
import com.example.theorb.ui.TopBar
import com.example.theorb.upgrades.UpgradeCategory
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.UpgradeType
import com.example.theorb.util.ResourceManager

class UpgradeScreen(private val game: TheOrb) : BaseScreen() {
    private val uiStage = Stage(viewport)
    private lateinit var goldLabel: Label
    private val upgradeLabels = mutableMapOf<UpgradeType, Label>()
    private val upgradeButtons = mutableMapOf<UpgradeType, com.badlogic.gdx.scenes.scene2d.ui.Stack>()
    private lateinit var modalDialog: ModalDialog
    private lateinit var topBar: TopBar

    private var selectedTab = UpgradeCategory.ATTACK
    private val tabButtons = mutableMapOf<UpgradeCategory, com.badlogic.gdx.scenes.scene2d.ui.Stack>()
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
        topBar = TopBar(uiStage, skin)


        setupUI()
    }

    private fun setupUI() {
        // 공통 레이아웃 시스템 사용
        val root = createRootLayout(uiStage)

        // ===== 상단 바 =====
        val topBarTable = topBar.createTopBar()
        addTopBar(root, topBarTable)

        // ===== 메인 컨텐츠 =====
        val mainContent = createMainContent()
        addMainContent(root, mainContent)

        // ===== 하단 네비게이션 =====
        val bottomNavigation = BottomNavigation(game, skin, BottomNavigation.Tab.UPGRADE)
        val bottomNav = bottomNavigation.createBottomNavigation()
        addBottomNavigation(root, bottomNav)
        // UI 초기화 완료 플래그 설정
        isUIInitialized = true
        needsUpdate = true
    }


    private fun createMainContent(): Table {
        val mainContent = Table().apply {
            background = ResourceManager.getRectanglePanel180340()
        }

        // 컨텐츠 영역 높이 계산
        val contentHeight = getContentAreaHeight()
        val tabHeightRatio = 0.15f      // 탭 영역 15%
        val scrollHeightRatio = 0.7f    // 스크롤 영역 70%
        val buttonHeightRatio = 0.15f   // 버튼 영역 15%

        // 탭 버튼들
        val tabRow = createTabButtons()
        mainContent.add(tabRow).height(contentHeight * tabHeightRatio).row()

        // 업그레이드 목록
        upgradeScrollPane = createUpgradeList()
        mainContent.add(upgradeScrollPane).expand().fill().height(contentHeight * scrollHeightRatio).row()

        // 하단 버튼들
        val bottomRow = createBottomButtons()

        mainContent.add(bottomRow).width(virtualWidth * 0.3f).height(contentHeight * buttonHeightRatio).row()
        return mainContent
    }

    private fun createTabButtons(): Table {
        val tabTable = Table()
        val contentHeight = getContentAreaHeight()

        for (category in UpgradeCategory.values()) {
            val isSelected = category == selectedTab
            val buttonWidth = virtualWidth * 0.25f
            val buttonHeight = contentHeight * 0.08f

            val tabButton = com.example.theorb.ui.RetroButton.createTextButton(
                text = category.displayName,
                skin = skin,
                labelStyle = "label-default-bold",
                textColor = if (isSelected) TEXT_PRIMARY else TEXT_SECONDARY,
                defaultImage = if (isSelected) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
                eventImage = if (isSelected) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent(),
                buttonSize = buttonHeight
            ) {
                if (selectedTab != category) {
                    selectedTab = category
                    updateTabButtons()
                    updateUpgradeList()
                    needsUpdate = true
                }
            }

            tabButtons[category] = tabButton

            tabTable.add(tabButton).width(buttonWidth).height(buttonHeight).pad(4f)
        }

        return tabTable
    }

    private fun updateTabButtons() {
        for ((category, button) in tabButtons) {
            val isSelected = category == selectedTab

            // RetroButton의 스타일 업데이트
            com.example.theorb.ui.RetroButton.updateTextButtonEnabled(
                button,
                true,
                if (isSelected) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
                if (isSelected) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent()
            )

            // RetroButton의 텍스트 색상 업데이트
            com.example.theorb.ui.RetroButton.updateTextButtonStyle(
                button,
                skin,
                "label-default-bold",
                if (isSelected) TEXT_PRIMARY else TEXT_SECONDARY
            )
        }
    }

    private fun updateUpgradeList() {
        val table = Table()
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        // 이전 탭의 버튼/라벨 참조들 클리어
        upgradeButtons.clear()
        upgradeLabels.clear()

        val rowWidth = virtualWidth * 0.75f
        val rowHeight = virtualHeight * 0.1f  // 화면 높이의 8%로 고정

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        // 테이블 전체 높이 계산 (모든 아이템이 보이도록)
        val totalHeight = filteredUpgrades.size * (rowHeight + 8f)
        table.height = totalHeight

        upgradeScrollPane.actor = table
        // 새로운 버튼들이 생성되었으므로 업데이트 필요
        needsUpdate = true
    }

    private fun createUpgradeList(): ScrollPane {
        val contentHeight = getContentAreaHeight()
        val table = Table()
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        val rowWidth = virtualWidth * 0.75f
        val rowHeight = contentHeight * 0.1f  // 화면 높이의 10%로 고정

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        // 테이블 전체 높이 계산 (모든 아이템이 보이도록)
        val totalHeight = filteredUpgrades.size * (rowHeight + 8f)
        table.height = totalHeight

        return ScrollPane(table, skin).apply {
            setScrollingDisabled(true, false)
            // 스크롤 영역의 최대 높이를 제한해서 스크롤 생성
            val mainPanelHeight = contentHeight * 0.8f
            val scrollHeightRatio = 0.7f
            val maxScrollHeight = mainPanelHeight * scrollHeightRatio - 40f  // 패딩 제외
            setSize(virtualWidth * 0.75f, maxScrollHeight)
        }
    }

    private fun createUpgradeRow(upgradeType: UpgradeType): Table {
        val upgradeTable = Table()
        upgradeTable.background = ResourceManager.getRectanglePanel25284()
        upgradeTable.pad(12f)

        // 업그레이드 정보
        val nameLabel = Label(upgradeType.displayName, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        val valueLabel = Label("", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        upgradeLabels[upgradeType] = valueLabel

        // 업그레이드 버튼 - Retro 스타일
        val currentLevel = UpgradeManager.getUpgradeLevel(game.saveData, upgradeType)
        val initialCost = if (currentLevel < upgradeType.maxLevel) upgradeType.getCostForLevel(currentLevel) else -1
        val initialText = if (currentLevel >= upgradeType.maxLevel) "MAX" else "Lv UP\n(${initialCost}G)"
        val canUpgrade = UpgradeManager.canUpgrade(game.saveData, upgradeType)

        val upgradeButton = com.example.theorb.ui.RetroButton.createTextButton(
            text = initialText,
            skin = skin,
            labelStyle = "label-small-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
            eventImage = ResourceManager.getRetroRectanglePosEvent(),
            disabledImage = ResourceManager.getRetroRectangleNagDefault(),
            buttonSize = 42f,
            isEnabled = canUpgrade
        ) {
            if (UpgradeManager.purchaseUpgrade(game.saveData, upgradeType)) {
                SaveManager.save(game.saveData)
                // 즉시 업데이트 플래그 설정
                needsUpdate = true
            }
        }

        upgradeButtons[upgradeType] = upgradeButton

        // 레이아웃
        val leftTable = Table()
        leftTable.add(nameLabel).expand().left().row()
        leftTable.add(valueLabel).expand().left()

        upgradeTable.add(leftTable).expand().fill().left().padLeft(8f)
        upgradeTable.add(upgradeButton).width(84f).height(42f).right().padRight(8f)

        return upgradeTable
    }

    private fun updateUpgradeDisplay() {
        topBar.updateCurrency()

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

                // RetroButton 텍스트 업데이트
                val buttonText = if (level >= maxLevel) {
                    "MAX"
                } else {
                    "Lv UP\n(${cost}G)"
                }
                com.example.theorb.ui.RetroButton.updateText(button, buttonText)

                // RetroButton 활성화/비활성화 상태 업데이트
                com.example.theorb.ui.RetroButton.updateTextButtonEnabled(
                    button,
                    canUpgrade,
                    ResourceManager.getRetroRectanglePosDefault(),
                    ResourceManager.getRetroRectanglePosEvent(),
                    ResourceManager.getRetroRectangleNagDefault(),
                    TEXT_PRIMARY
                )
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

    private fun createBottomButtons(): Table {
        val bottomTable = Table()
        val contentHeight = getContentAreaHeight()
        val buttonWidth = virtualWidth * 0.25f
        val buttonHeight = contentHeight * 0.08f

        val resetButton = com.example.theorb.ui.RetroButton.createTextButton(
            text = "초기화",
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = DANGER,
            defaultImage = ResourceManager.getRetroRectangleNagDefault(),
            eventImage = ResourceManager.getRetroRectangleNagEvent(),
            buttonSize = 42f
        ) {
            showResetConfirmDialog()
        }

        bottomTable.add(resetButton).width(buttonWidth).height(buttonHeight).pad(4f)

        return bottomTable
    }

    private fun showResetConfirmDialog() {
        modalDialog.show(
            title = "업그레이드 초기화",
            message = "모든 업그레이드를 초기화하고\n사용한 골드를 환불받겠습니까?",
            confirmText = "초기화",
            cancelText = "취소",
            confirmColor = DANGER,
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
        disposeSharedResources()
    }
}
