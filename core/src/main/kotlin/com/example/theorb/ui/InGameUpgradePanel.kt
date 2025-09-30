package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
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
    private lateinit var contentScrollPane: ScrollPane
    private lateinit var contentTable: Table
    private var currentTab = InGameUpgrades.UpgradeTab.ATTACK

    // 탭 버튼들 참조
    private lateinit var attackTabBtn: Stack
    private lateinit var defenseTabBtn: Stack
    private lateinit var utilityTabBtn: Stack

    fun createUI(availableHeight: Float? = null): Table {
        // 메인 컨테이너 (할당된 영역 내에서 전체 폭 사용)
        mainContainer = Table().apply {
            background = ResourceManager.getRectanglePanel340180()
            pad(8f)
        }

        // 상단: 탭 버튼들 (직사각형 배경 적용)
        val topRow = createTabButtons()

        // 초기 컨텐츠를 먼저 생성
        contentTable = createContentForTab(currentTab)

        // ScrollPane 생성 (완성된 컨텐츠로)
        val maxScrollHeight = if (availableHeight != null) {
            // GameScreen에서 넘겨받은 높이에서 탭 버튼과 패딩 공간 제외
            availableHeight - 100f // 탭 버튼(42f) + 패딩들(58f) 대략 100f
        } else {
            // 기본값: 전체 높이의 35%에서 여백 제외 (fallback)
            BaseScreen.VIRTUAL_HEIGHT * 0.35f - 100f
        }

        contentScrollPane = ScrollPane(contentTable, BaseScreen.skin).apply {
            setScrollingDisabled(true, false) // 세로 스크롤만 허용
            setFlickScroll(true) // 플릭 스크롤 활성화
            setSmoothScrolling(true) // 부드러운 스크롤 활성화
            setOverscroll(false, false) // 오버스크롤 비활성화

            // 터치 이벤트 관련 설정 - 버튼 클릭과 스크롤 충돌 방지
            setCancelTouchFocus(true) // 터치 포커스 취소 활성화
            setClamp(true) // 스크롤 영역 제한

            // ScrollPane의 높이를 명시적으로 설정
            setHeight(maxScrollHeight)
        }

        // 레이아웃 구성 (전체 폭 사용)
        mainContainer.add(topRow).fillX().expandX().pad(4f).row()
        mainContainer.add(contentScrollPane).expandX().fillX().height(maxScrollHeight).pad(8f, 4f, 4f, 4f)

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
        val newContentTable = Table()

        if (tab == InGameUpgrades.UpgradeTab.DEFENSE) {
            newContentTable.add(Label("방어 업그레이드는 준비 중입니다.", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }).center().expand()
            return newContentTable
        }

        // 해당 탭의 업그레이드들을 세로로 배치
        val upgrades = InGameUpgrades.UPGRADE_DATA.filter { it.value.tab == tab }.toList()

        val rowWidth = BaseScreen.VIRTUAL_WIDTH * 0.8f
        val rowHeight = BaseScreen.VIRTUAL_HEIGHT * 0.08f

        upgrades.forEachIndexed { index, (upgradeType, info) ->
            val currentLevel = saveData.inGameUpgrades[upgradeType.name] ?: 0
            val cost = InGameUpgrades.getUpgradeCost(upgradeType, currentLevel)
            val currentBonus = InGameUpgrades.getCurrentBonus(upgradeType, currentLevel)
            val upgradeRow = createUpgradeRow(upgradeType, info, currentLevel, cost, currentBonus)
            newContentTable.add(upgradeRow).size(rowWidth, rowHeight).pad(2f).padBottom(4f).row()
        }

        return newContentTable
    }

    private fun switchToTab(tab: InGameUpgrades.UpgradeTab) {
        currentTab = tab

        // 새로운 컨텐츠 테이블 생성
        val newContentTable = createContentForTab(tab)

        // ScrollPane의 actor를 새로운 테이블로 교체
        contentScrollPane.actor = newContentTable
        contentTable = newContentTable

        // 스크롤 위치를 맨 위로 리셋
        contentScrollPane.scrollY = 0f
    }

    private fun createUpgradeRow(
        upgradeType: InGameUpgrades.UpgradeType,
        info: InGameUpgrades.UpgradeInfo,
        currentLevel: Int,
        cost: Int,
        currentBonus: Float
    ): Table {
        val upgradeRow = Table().apply {
            background = ResourceManager.getRectanglePanel25284()
            pad(8f)
        }

        // 업그레이드 정보 (가로 배치)
        val nameLabel = Label(info.name, BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        val levelLabel = Label("Lv.$currentLevel/${info.maxLevel}", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_SECONDARY
        }

        val bonusText = if (upgradeType == InGameUpgrades.UpgradeType.GEM_INCREASE || upgradeType == InGameUpgrades.UpgradeType.ENEMY_SPAWN_COUNT) {
            "+${currentBonus.toInt()}"
        } else {
            "+${currentBonus.toInt()}%"
        }

        val bonusLabel = Label(bonusText, BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.WARNING
        }

        // 업그레이드 버튼 - Retro 스타일 사용
        val canUpgrade = currentLevel < info.maxLevel && saveData.silver >= cost
        val buttonText = if (currentLevel >= info.maxLevel) "MAX" else "Level Up\n(${formatNumber(cost)} silver)"

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

        // 레이아웃 (가로 배치) - 업그레이드 버튼을 오른쪽으로 배치
        upgradeRow.add(nameLabel).left().padLeft(8f).padRight(8f)
        upgradeRow.add(levelLabel).left().padRight(8f)
        upgradeRow.add(bonusLabel).left().padRight(8f)
        upgradeRow.add(upgradeButton).size(
            BaseScreen.VIRTUAL_WIDTH * BaseScreen.RECTANGLE_BUTTON_WIDTH_RATIO,
            BaseScreen.VIRTUAL_HEIGHT * BaseScreen.BUTTON_HEIGHT_RATIO
        ).right().expandX().padRight(8f)
        return upgradeRow
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
        // 현재 스크롤 위치 저장
        val currentScrollY = contentScrollPane.scrollY

        // 같은 탭 내용 새로고침 (탭 전환이 아님)
        val newContentTable = createContentForTab(currentTab)
        contentScrollPane.actor = newContentTable
        contentTable = newContentTable

        // 스크롤 위치 복원
        contentScrollPane.scrollY = currentScrollY
    }
}
