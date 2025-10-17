package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.example.theorb.balance.InGameUpgrades
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class InGameUpgradePanel(
    private val saveData: SaveData
) {
    private lateinit var mainContainer: Table
    private lateinit var contentContainer: Table
    private lateinit var contentTable: Table
    private var currentTab = InGameUpgrades.UpgradeTab.ATTACK
    private var currentPage = 0
    private var maxPage = 0
    private val itemsPerPage = 4 // 한 페이지에 4개씩 표시

    // 탭 버튼들 참조
    private lateinit var attackTabBtn: Stack
    private lateinit var defenseTabBtn: Stack
    private lateinit var utilityTabBtn: Stack

    // 페이지 버튼 참조
    private lateinit var leftButton: Stack
    private lateinit var rightButton: Stack
    private lateinit var pageLabel: Label

    fun createUI(availableHeight: Float? = null): Table {
        // 메인 컨테이너 (할당된 영역 내에서 전체 폭 사용)
        mainContainer = Table().apply {
            background = ResourceManager.getRectanglePanel340180()
            pad(8f)
        }

        // 상단: 탭 버튼들 (직사각형 배경 적용)
        val topRow = createTabButtons()

        // 컨텐츠 영역 높이 계산
        val maxContentHeight = if (availableHeight != null) {
            availableHeight - 100f
        } else {
            BaseScreen.VIRTUAL_HEIGHT * 0.35f - 100f
        }

        // 컨텐츠 컨테이너 (좌우 버튼 + 컨텐츠)
        contentContainer = Table()

        // 좌측 버튼
        leftButton = RetroButton.createTextButton(
            text = "<",
            skin = BaseScreen.skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroSquarePosDefault(),
            eventImage = ResourceManager.getRetroSquarePosEvent(),
            disabledImage = ResourceManager.getRetroSquareNagDefault(),
            buttonSize = 42f,
            isEnabled = false
        ) {
            previousPage()
        }

        // 우측 버튼
        rightButton = RetroButton.createTextButton(
            text = ">",
            skin = BaseScreen.skin,
            labelStyle = "label-default-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroSquarePosDefault(),
            eventImage = ResourceManager.getRetroSquarePosEvent(),
            disabledImage = ResourceManager.getRetroSquareNagDefault(),
            buttonSize = 42f,
            isEnabled = false
        ) {
            nextPage()
        }

        // 페이지 표시 라벨
        pageLabel = Label("", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        // 초기 컨텐츠 생성
        contentTable = createContentForTab(currentTab)

        // 레이아웃 구성
        mainContainer.add(topRow).fillX().expandX().pad(4f).row()

        // 컨텐츠 영역 (좌 버튼 + 컨텐츠 + 우 버튼)
        val contentRow = Table()
        contentRow.add(leftButton).size(42f).padRight(8f)
        contentRow.add(contentTable).expandX().fillX().height(maxContentHeight)
        contentRow.add(rightButton).size(42f).padLeft(8f)

        mainContainer.add(contentRow).expandX().fillX().height(maxContentHeight).pad(8f, 4f, 4f, 4f).row()
        mainContainer.add(pageLabel).center().padTop(4f)

        // 기본 탭 상태 설정
        updateTabStates()

        return mainContainer
    }

    private fun createTabButtons(): Table {
        return Table().apply {
            val tabContainer = Table()

            // 공격 탭 버튼
            attackTabBtn = RetroButton.createTextButton(
                text = "공격",
                skin = BaseScreen.skin,
                labelStyle = "label-default-bold",
                textColor = BaseScreen.TEXT_PRIMARY,
                defaultImage = ResourceManager.getRetroRectangleNagDefault(),
                eventImage = ResourceManager.getRetroRectangleNagEvent(),
                buttonSize = 42f
            ) {
                currentTab = InGameUpgrades.UpgradeTab.ATTACK
                updateTabStates()
                switchToTab(InGameUpgrades.UpgradeTab.ATTACK)
            }

            // 방어 탭 버튼
            defenseTabBtn = RetroButton.createTextButton(
                text = "방어",
                skin = BaseScreen.skin,
                labelStyle = "label-default-bold",
                textColor = BaseScreen.TEXT_PRIMARY,
                defaultImage = ResourceManager.getRetroRectangleNagDefault(),
                eventImage = ResourceManager.getRetroRectangleNagEvent(),
                buttonSize = 42f
            ) {
                currentTab = InGameUpgrades.UpgradeTab.DEFENSE
                updateTabStates()
                switchToTab(InGameUpgrades.UpgradeTab.DEFENSE)
            }

            // 유틸 탭 버튼
            utilityTabBtn = RetroButton.createTextButton(
                text = "유틸",
                skin = BaseScreen.skin,
                labelStyle = "label-default-bold",
                textColor = BaseScreen.TEXT_PRIMARY,
                defaultImage = ResourceManager.getRetroRectangleNagDefault(),
                eventImage = ResourceManager.getRetroRectangleNagEvent(),
                buttonSize = 42f
            ) {
                currentTab = InGameUpgrades.UpgradeTab.UTILITY
                updateTabStates()
                switchToTab(InGameUpgrades.UpgradeTab.UTILITY)
            }

            val tabWidth = BaseScreen.VIRTUAL_WIDTH * 0.23f // 약 110px
            val tabHeight = BaseScreen.VIRTUAL_HEIGHT * BaseScreen.BUTTON_HEIGHT_RATIO

            tabContainer.add(attackTabBtn).size(tabWidth, tabHeight).pad(2f).padRight(10f)
            tabContainer.add(defenseTabBtn).size(tabWidth, tabHeight).pad(2f).padRight(10f)
            tabContainer.add(utilityTabBtn).size(tabWidth, tabHeight).pad(2f)

            add(tabContainer).center().expandX().fillX()
        }
    }

    // 탭 상태 업데이트 함수 - 선택된 탭은 pos 버튼+화이트+볼드, 비선택 탭은 nag 버튼+기본색+일반
    private fun updateTabStates() {
        // 모든 탭을 비활성 상태로 설정 (nag 이미지 + 기본 색상 + 일반 폰트)
        RetroButton.updateTextButtonEnabled(
            attackTabBtn, true,
            ResourceManager.getRetroRectangleNagDefault(),
            ResourceManager.getRetroRectangleNagEvent()
        )
        RetroButton.updateTextButtonStyle(
            attackTabBtn, BaseScreen.skin, "label-default", BaseScreen.TEXT_SECONDARY
        )

        RetroButton.updateTextButtonEnabled(
            defenseTabBtn, true,
            ResourceManager.getRetroRectangleNagDefault(),
            ResourceManager.getRetroRectangleNagEvent()
        )
        RetroButton.updateTextButtonStyle(
            defenseTabBtn, BaseScreen.skin, "label-default", BaseScreen.TEXT_SECONDARY
        )

        RetroButton.updateTextButtonEnabled(
            utilityTabBtn, true,
            ResourceManager.getRetroRectangleNagDefault(),
            ResourceManager.getRetroRectangleNagEvent()
        )
        RetroButton.updateTextButtonStyle(
            utilityTabBtn, BaseScreen.skin, "label-default", BaseScreen.TEXT_SECONDARY
        )

        // 현재 활성 탭을 활성 상태로 설정 (pos 이미지 + 화이트 + 볼드)
        when (currentTab) {
            InGameUpgrades.UpgradeTab.ATTACK -> {
                RetroButton.updateTextButtonEnabled(
                    attackTabBtn, true,
                    ResourceManager.getRetroRectanglePosDefault(),
                    ResourceManager.getRetroRectanglePosEvent()
                )
                RetroButton.updateTextButtonStyle(
                    attackTabBtn, BaseScreen.skin, "label-default-bold", BaseScreen.TEXT_PRIMARY
                )
            }
            InGameUpgrades.UpgradeTab.DEFENSE -> {
                RetroButton.updateTextButtonEnabled(
                    defenseTabBtn, true,
                    ResourceManager.getRetroRectanglePosDefault(),
                    ResourceManager.getRetroRectanglePosEvent()
                )
                RetroButton.updateTextButtonStyle(
                    defenseTabBtn, BaseScreen.skin, "label-default-bold", BaseScreen.TEXT_PRIMARY
                )
            }
            InGameUpgrades.UpgradeTab.UTILITY -> {
                RetroButton.updateTextButtonEnabled(
                    utilityTabBtn, true,
                    ResourceManager.getRetroRectanglePosDefault(),
                    ResourceManager.getRetroRectanglePosEvent()
                )
                RetroButton.updateTextButtonStyle(
                    utilityTabBtn, BaseScreen.skin, "label-default-bold", BaseScreen.TEXT_PRIMARY
                )
            }
        }
    }


    private fun createContentForTab(tab: InGameUpgrades.UpgradeTab): Table {
        val newContentTable = Table().apply {
            touchable = Touchable.childrenOnly
        }

        if (tab == InGameUpgrades.UpgradeTab.DEFENSE) {
            newContentTable.add(Label("방어 업그레이드는 준비 중입니다.", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }).center().expand()
            currentPage = 0
            maxPage = 0
            updatePageButtons()
            return newContentTable
        }

        // 해당 탭의 업그레이드들
        val upgrades = InGameUpgrades.UPGRADE_DATA.filter { it.value.tab == tab }.toList()

        // 페이지 계산
        maxPage = kotlin.math.max(0, (upgrades.size - 1) / itemsPerPage)
        if (currentPage > maxPage) currentPage = maxPage

        // 현재 페이지에 표시할 아이템들
        val startIndex = currentPage * itemsPerPage
        val endIndex = kotlin.math.min(startIndex + itemsPerPage, upgrades.size)
        val pageUpgrades = upgrades.subList(startIndex, endIndex)

        // 카드 크기 설정
        val cardWidth = BaseScreen.VIRTUAL_WIDTH * 0.18f
        val cardHeight = BaseScreen.VIRTUAL_HEIGHT * 0.28f

        pageUpgrades.forEach { (upgradeType, info) ->
            val currentLevel = saveData.inGameUpgrades[upgradeType.name] ?: 0
            val cost = InGameUpgrades.getUpgradeCost(upgradeType, currentLevel)
            val currentBonus = InGameUpgrades.getCurrentBonus(upgradeType, currentLevel)
            val upgradeCard = createUpgradeCard(upgradeType, info, currentLevel, cost, currentBonus)
            newContentTable.add(upgradeCard).size(cardWidth, cardHeight).pad(4f)
        }

        updatePageButtons()
        return newContentTable
    }

    private fun previousPage() {
        if (currentPage > 0) {
            currentPage--
            switchToTab(currentTab)
        }
    }

    private fun nextPage() {
        if (currentPage < maxPage) {
            currentPage++
            switchToTab(currentTab)
        }
    }

    private fun updatePageButtons() {
        // 좌측 버튼 활성화/비활성화
        RetroButton.updateTextButtonEnabled(
            leftButton,
            currentPage > 0,
            ResourceManager.getRetroSquarePosDefault(),
            ResourceManager.getRetroSquarePosEvent(),
            ResourceManager.getRetroSquareNagDefault()
        )

        // 우측 버튼 활성화/비활성화
        RetroButton.updateTextButtonEnabled(
            rightButton,
            currentPage < maxPage,
            ResourceManager.getRetroSquarePosDefault(),
            ResourceManager.getRetroSquarePosEvent(),
            ResourceManager.getRetroSquareNagDefault()
        )

        // 페이지 라벨 업데이트
        pageLabel.setText("${currentPage + 1} / ${maxPage + 1}")
    }

    private fun switchToTab(tab: InGameUpgrades.UpgradeTab) {
        currentTab = tab
        currentPage = 0 // 탭 변경 시 첫 페이지로

        refreshContent()
    }

    private fun refreshContent() {
        // 새로운 컨텐츠 테이블 생성
        val newContentTable = createContentForTab(currentTab)

        // 기존 컨텐츠 제거하고 새 컨텐츠 추가
        contentTable.remove()
        contentTable = newContentTable

        // contentRow 찾아서 업데이트
        val contentRow = mainContainer.children[1] as Table
        val cell = contentRow.cells.get(1) as com.badlogic.gdx.scenes.scene2d.ui.Cell<*>
        cell.setActor<Actor>(contentTable)
    }

    private fun createUpgradeCard(
        upgradeType: InGameUpgrades.UpgradeType,
        info: InGameUpgrades.UpgradeInfo,
        currentLevel: Int,
        cost: Int,
        currentBonus: Float
    ): Table {
        val upgradeCard = Table().apply {
            background = ResourceManager.getRectanglePanel180340()
            pad(8f)
            touchable = Touchable.childrenOnly
        }

        // 고정 높이 설정
        val nameLabelHeight = 32f
        val labelHeight = 20f
        val bonusLabelHeight = 24f
        val buttonHeight = BaseScreen.VIRTUAL_HEIGHT * BaseScreen.BUTTON_HEIGHT_RATIO

        // 업그레이드 정보 (세로 배치)
        val nameLabel = Label(info.name, BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
            setAlignment(com.badlogic.gdx.utils.Align.center)
            wrap = true
        }

        val levelLabel = Label("Lv.$currentLevel/${info.maxLevel}", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        val bonusText = if (upgradeType == InGameUpgrades.UpgradeType.GEM_INCREASE || upgradeType == InGameUpgrades.UpgradeType.ENEMY_SPAWN_COUNT) {
            "+${currentBonus.toInt()}"
        } else {
            "+${currentBonus.toInt()}%"
        }

        val bonusLabel = Label(bonusText, BaseScreen.skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.WARNING
        }

        // 업그레이드 버튼 - Retro 스타일 사용
        val canUpgrade = currentLevel < info.maxLevel && saveData.silver >= cost
        val buttonText = if (currentLevel >= info.maxLevel) "MAX" else "Level Up"

        val upgradeButton = RetroButton.createTextButton(
            text = buttonText,
            skin = BaseScreen.skin,
            labelStyle = "label-small-bold",
            textColor = BaseScreen.TEXT_PRIMARY,
            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
            eventImage = ResourceManager.getRetroRectanglePosEvent(),
            disabledImage = ResourceManager.getRetroRectangleNagDefault(),
            buttonSize = 42f,
            isEnabled = canUpgrade
        ) {
            purchaseUpgrade(upgradeType)
        }

        val costLabel = Label("(${formatNumber(cost)})", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        // 레이아웃 (세로 배치) - 고정 높이로 정렬
        upgradeCard.add(nameLabel).width(BaseScreen.VIRTUAL_WIDTH * 0.16f).height(nameLabelHeight).center().padBottom(4f).row()
        upgradeCard.add(levelLabel).height(labelHeight).center().padBottom(2f).row()
        upgradeCard.add(bonusLabel).height(bonusLabelHeight).center().padBottom(8f).row()
        upgradeCard.add(upgradeButton).size(
            BaseScreen.VIRTUAL_WIDTH * 0.15f,
            buttonHeight
        ).center().padBottom(2f).row()

        // 비용 라벨은 항상 추가 (MAX일 때는 빈 라벨)
        if (currentLevel < info.maxLevel) {
            upgradeCard.add(costLabel).height(labelHeight).center()
        } else {
            upgradeCard.add(Label("", BaseScreen.skin)).height(labelHeight).center() // 빈 공간 유지
        }

        return upgradeCard
    }

    private fun purchaseUpgrade(upgradeType: InGameUpgrades.UpgradeType) {
        val currentLevel = saveData.inGameUpgrades[upgradeType.name] ?: 0
        val cost = InGameUpgrades.getUpgradeCost(upgradeType, currentLevel)
        val info = InGameUpgrades.UPGRADE_DATA[upgradeType] ?: return

        // 최대 레벨 체크와 실버 체크
        if (currentLevel < info.maxLevel && saveData.silver >= cost) {
            saveData.silver -= cost
            saveData.inGameUpgrades[upgradeType.name] = currentLevel + 1

            // 캐시된 값들 업데이트
            updateCachedValues()

            SaveManager.save(saveData)
            refreshUI()
        }
    }

    private fun updateCachedValues() {
        // 치명타 관련 값들 업데이트
        val critChanceLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.CRITICAL_CHANCE.name] ?: 0
        val critDamageLevel = saveData.inGameUpgrades[InGameUpgrades.UpgradeType.CRITICAL_DAMAGE.name] ?: 0

        saveData.criticalChance = 5f + InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.CRITICAL_CHANCE, critChanceLevel)
        saveData.criticalDamage = 150f + InGameUpgrades.getCurrentBonus(InGameUpgrades.UpgradeType.CRITICAL_DAMAGE, critDamageLevel)
    }

    fun refreshUI() {
        // 현재 페이지 유지하면서 내용 새로고침
        refreshContent()
    }
}
