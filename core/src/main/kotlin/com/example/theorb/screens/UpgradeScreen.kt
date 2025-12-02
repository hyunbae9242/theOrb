package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.TheOrb
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.modal.ModalDialog
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.ui.TopBar
import com.example.theorb.upgrades.UpgradeCategory
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.UpgradeType
import com.example.theorb.util.ResourceManager

class UpgradeScreen(private val game: TheOrb) : BaseScreen() {
    private val uiStage = Stage(viewport)
    private val upgradeLabels = mutableMapOf<UpgradeType, Label>()
    private val upgradeCostLabels = mutableMapOf<UpgradeType, Label>()
    private val upgradeButtons = mutableMapOf<UpgradeType, ImageButton>()
    private lateinit var modalDialog: ModalDialog
    private lateinit var topBar: TopBar

    private var selectedTab = UpgradeCategory.ATTACK
    private val tabButtons = mutableMapOf<UpgradeCategory, ImageButton>()
    private lateinit var upgradeScrollPane: ScrollPane
    private var isUIInitialized = false
    private var needsUpdate = false

    private val rowWidth = 416f
    private val rowHeight = 80f

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
        val background = Table().apply {
            background = ResourceManager.getUpgradeBackPanel()
            top()
        }
        // 탭 버튼들
        val tabRow = createTabButtons()
        // 업그레이드 목록
        upgradeScrollPane = createUpgradeList()
        // 하단 버튼들
        val bottomRow = createBottomButtons()

        // total 656 - 16 - 16 (top,bottom pad) = 624
        // tab 16 , 48, 16 (padTop, btnHeight, padBottom)
        // list 464
        // btn 16, 48, 16
        background.add(tabRow).padTop(16f).padBottom(16f).center().row()
        background.add(upgradeScrollPane).height(464f).center().row()
        background.add(bottomRow).padTop(16f).padBottom(16f).center()

        val mainContent = Table().apply {
            add(background)
            padTop(16f)
            padBottom(16f)
        }
        return mainContent
    }

    private fun createTabButtons(): Table {
        val tabTable = Table()

        for (category in UpgradeCategory.values()) {
            val isSelected = category == selectedTab

            val tabButton = RetroButtonV01.createIconButton(
                defaultImage = if(isSelected) ResourceManager.getUpgradeTabBasePos(category) else ResourceManager.getUpgradeTabBaseNag(category),
                eventImage = if(isSelected) ResourceManager.getUpgradeTabEventPos(category) else ResourceManager.getUpgradeTabEventNag(category)
            ){
                if (selectedTab != category) {
                    selectedTab = category
                    updateTabButtons()
                    updateUpgradeList()
                    needsUpdate = true
                }
            }

            tabButtons[category] = tabButton

            tabTable.add(tabButton).width(100f).height(48f).padLeft(8f).padRight(8f)
        }

        return tabTable
    }

    private fun updateTabButtons() {
        for ((category, button) in tabButtons) {
            val isSelected = category == selectedTab

            // RetroButton의 스타일 업데이트
            RetroButtonV01.updateIconButton(
                button,
                true,
                if(isSelected) ResourceManager.getUpgradeTabBasePos(category) else ResourceManager.getUpgradeTabBaseNag(category),
                if(isSelected) ResourceManager.getUpgradeTabEventPos(category) else ResourceManager.getUpgradeTabEventNag(category)
            )
        }
    }

    private fun updateUpgradeList() {
        val table = Table().apply{
            top()
        }
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        // 이전 탭의 버튼/라벨 참조들 클리어
        upgradeButtons.clear()
        upgradeLabels.clear()

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        upgradeScrollPane.actor = table
        // 새로운 버튼들이 생성되었으므로 업데이트 필요
        needsUpdate = true
    }

    private fun createUpgradeList(): ScrollPane {
        val table = Table().apply{
            top()
        }
        val filteredUpgrades = UpgradeType.values().filter { it.category == selectedTab }

        for (upgradeType in filteredUpgrades) {
            val upgradeTable = createUpgradeRow(upgradeType)
            table.add(upgradeTable).size(rowWidth, rowHeight).padBottom(8f).row()
        }

        return ScrollPane(table, skin).apply {
            setScrollingDisabled(true, false)
        }
    }

    private fun createUpgradeRow(upgradeType: UpgradeType): Table {
        val upgradeTable = Table()
        upgradeTable.background = ResourceManager.getUpgradeListPanel()
        upgradeTable.pad(8f)

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
        val initialText = if (currentLevel >= upgradeType.maxLevel) "MAX" else "${initialCost}G"
        val costLabel = Label(initialText, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        upgradeCostLabels[upgradeType] = costLabel

        val canUpgrade = UpgradeManager.canUpgrade(game.saveData, upgradeType)

        val lvUpButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getLvUpBasePos(),
            eventImage = ResourceManager.getLvUpEventPos(),
            disabledImage = ResourceManager.getLvUpBaseNag(),
            isEnabled = canUpgrade,
        ) {
            if (UpgradeManager.purchaseUpgrade(game.saveData, upgradeType)) {
                // 리롤 업그레이드인 경우 SaveData 동기화
                if (upgradeType == UpgradeType.REROLL_COUNT) {
                    UpgradeManager.applyRerollUpgrade(game.saveData)
                }
                SaveManager.save(game.saveData)
                // 즉시 업데이트 플래그 설정
                needsUpdate = true
            }
        }

        upgradeButtons[upgradeType] = lvUpButton

        // 레이아웃
        val leftTable = Table()
        leftTable.add(nameLabel).expand().left().row()
        leftTable.add(valueLabel).expand().left()

        upgradeTable.add(leftTable).expand().fill().left().padLeft(8f)
        upgradeTable.add(costLabel).left().padRight(8f)
        upgradeTable.add(lvUpButton).width(80f).height(48f).right().padRight(8f)

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

            val costText = if (level >= maxLevel) {
                "MAX"
            } else {
                "${cost}G"
            }
            upgradeCostLabels[upgradeType]?.setText(costText)

            // 현재 탭에 표시된 버튼만 업데이트
            upgradeButtons[upgradeType]?.let { button ->
                val canUpgrade = UpgradeManager.canUpgrade(game.saveData, upgradeType)
                // RetroButton 활성화/비활성화 상태 업데이트
                RetroButtonV01.updateIconButton(
                    button,
                    canUpgrade,
                    ResourceManager.getLvUpBasePos(),
                    ResourceManager.getLvUpEventPos(),
                    ResourceManager.getLvUpBaseNag(),
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
            UpgradeType.ARMOR_PERCENTAGE -> "+${value.toInt()}%"
            UpgradeType.RANGE -> "+${(value * 100).toInt()}%"
            UpgradeType.COOLDOWN_REDUCTION -> "-${(value * 100).toInt()}%"
            UpgradeType.GOLD_BONUS -> "+${(value * 100).toInt()}%"
            UpgradeType.REROLL_COUNT -> "+${value.toInt()}회"
            UpgradeType.RARITY_BONUS -> {
                val level = value.toInt()
                val uniqueBonus = UpgradeType.RARITY_BONUS.getRarityBonusUnique(level)
                val rareBonus = UpgradeType.RARITY_BONUS.getRarityBonusRare(level)
                "U +${uniqueBonus}%, R +${rareBonus}%"
            }
        }
    }

    private fun createBottomButtons(): Table {
        val bottomTable = Table()

        val resetButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getResetBasePos(),
            eventImage = ResourceManager.getResetEventPos()
        ) {
            showResetConfirmDialog()
        }

        bottomTable.add(resetButton).width(160f).height(48f).center()

        return bottomTable
    }

    private fun showResetConfirmDialog() {
        modalDialog.show(
            title = "업그레이드 초기화",
            message = "모든 업그레이드를 초기화하고\n사용한 골드를 환불받겠습니까?",
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
