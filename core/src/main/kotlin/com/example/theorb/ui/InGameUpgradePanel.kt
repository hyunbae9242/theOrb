package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.example.theorb.balance.InGameUpgrades
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager

class InGameUpgradePanel(
    private val saveData: SaveData
) {
    private lateinit var mainContainer: Table
    private lateinit var contentScrollPane: ScrollPane
    private lateinit var contentTable: Table
    private var currentTab = InGameUpgrades.UpgradeTab.ATTACK

    // 탭 버튼들 참조
    private lateinit var attackTabBtn: TextButton
    private lateinit var defenseTabBtn: TextButton
    private lateinit var utilityTabBtn: TextButton

    fun createUI(): Table {
        // 메인 컨테이너 (화면 전체 폭 사용)
        mainContainer = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = BaseScreen.PANEL_BG
            pad(8f)
            setFillParent(true) // 부모 컨테이너 전체 크기 사용
        }

        // 상단: 탭 버튼들 (직사각형 배경 적용)
        val topRow = Table().apply {
            val tabContainer = Table()

            // 공격 탭 버튼
            attackTabBtn = createTabButton("공격")
            attackTabBtn.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    currentTab = InGameUpgrades.UpgradeTab.ATTACK
                    updateTabStates()
                    showTab(InGameUpgrades.UpgradeTab.ATTACK)
                }
            })

            // 방어 탭 버튼
            defenseTabBtn = createTabButton("방어")
            defenseTabBtn.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    currentTab = InGameUpgrades.UpgradeTab.DEFENSE
                    updateTabStates()
                    showTab(InGameUpgrades.UpgradeTab.DEFENSE)
                }
            })

            // 유틸 탭 버튼
            utilityTabBtn = createTabButton("유틸")
            utilityTabBtn.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    currentTab = InGameUpgrades.UpgradeTab.UTILITY
                    updateTabStates()
                    showTab(InGameUpgrades.UpgradeTab.UTILITY)
                }
            })

            tabContainer.add(attackTabBtn).size(112f, 56f).pad(2f)
            tabContainer.add(defenseTabBtn).size(112f, 56f).pad(2f)
            tabContainer.add(utilityTabBtn).size(112f, 56f).pad(2f)

            add(tabContainer).center().expandX().fillX()
        }

        // 콘텐츠 영역
        contentTable = Table()
        contentScrollPane = ScrollPane(contentTable, BaseScreen.skin.get("default", ScrollPane.ScrollPaneStyle::class.java)).apply {
            setFadeScrollBars(false)
            setScrollingDisabled(true, false)
        }

        // 레이아웃 구성 (전체 폭 사용)
        mainContainer.add(topRow).fillX().expandX().pad(4f).row()
        mainContainer.add(contentScrollPane).expand().fill().pad(4f)

        // 기본 탭 표시 및 상태 설정
        updateTabStates()
        showTab(InGameUpgrades.UpgradeTab.ATTACK)

        return mainContainer
    }

    // 탭 버튼 생성 함수
    private fun createTabButton(text: String): TextButton {
        val buttonStyle = TextButton.TextButtonStyle().apply {
            font = BaseScreen.skin.get("btn-small-bold", TextButton.TextButtonStyle::class.java)?.font
                ?: BaseScreen.skin.get("btn", TextButton.TextButtonStyle::class.java).font
            fontColor = Color.WHITE
            up = ResourceManager.getButtonCancelBg() // 기본 상태
            down = ResourceManager.getButtonHighlightBg()
            over = ResourceManager.getButtonHighlightBg()
        }
        return TextButton(text, buttonStyle)
    }

    // 탭 상태 업데이트 함수
    private fun updateTabStates() {
        // 모든 탭을 기본 상태로 설정
        attackTabBtn.style.up = ResourceManager.getButtonCancelBg()
        defenseTabBtn.style.up = ResourceManager.getButtonCancelBg()
        utilityTabBtn.style.up = ResourceManager.getButtonCancelBg()

        // 현재 활성 탭을 하이라이트로 설정
        when (currentTab) {
            InGameUpgrades.UpgradeTab.ATTACK -> attackTabBtn.style.up = ResourceManager.getButtonHighlightBg()
            InGameUpgrades.UpgradeTab.DEFENSE -> defenseTabBtn.style.up = ResourceManager.getButtonHighlightBg()
            InGameUpgrades.UpgradeTab.UTILITY -> utilityTabBtn.style.up = ResourceManager.getButtonHighlightBg()
        }
    }

    private fun showTab(tab: InGameUpgrades.UpgradeTab) {
        currentTab = tab
        contentTable.clear()

        if (tab == InGameUpgrades.UpgradeTab.DEFENSE) {
            contentTable.add(Label("방어 업그레이드는 준비 중입니다.", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }).center().expand()
            return
        }

        // 해당 탭의 업그레이드들을 세로로 배치
        InGameUpgrades.UPGRADE_DATA.filter { it.value.tab == tab }.forEach { (upgradeType, info) ->
            val currentLevel = saveData.inGameUpgrades[upgradeType.name] ?: 0
            val cost = InGameUpgrades.getUpgradeCost(upgradeType, currentLevel)
            val currentBonus = InGameUpgrades.getCurrentBonus(upgradeType, currentLevel)

            val upgradeRow = createUpgradeRow(upgradeType, info, currentLevel, cost, currentBonus)
            contentTable.add(upgradeRow).fillX().pad(1f).row()
        }
    }

    private fun createUpgradeRow(
        upgradeType: InGameUpgrades.UpgradeType,
        info: InGameUpgrades.UpgradeInfo,
        currentLevel: Int,
        cost: Int,
        currentBonus: Float
    ): Table {
        val row = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = BaseScreen.BORDER
            pad(1f)
        }

        val innerRow = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = Color(0.15f, 0.15f, 0.15f, 1f)
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
            color = Color.YELLOW
        }

        // 업그레이드 버튼 - 새로운 배경 이미지 사용
        val canUpgrade = currentLevel < info.maxLevel && saveData.silver >= cost
        val buttonText = if (currentLevel >= info.maxLevel) "MAX" else "Level Up\n($cost silver)"

        val upgradeButtonStyle = TextButton.TextButtonStyle().apply {
            font = BaseScreen.skin.get("btn-small-bold", TextButton.TextButtonStyle::class.java)?.font
                ?: BaseScreen.skin.get("btn", TextButton.TextButtonStyle::class.java).font
            fontColor = Color.WHITE
            up = if (canUpgrade) ResourceManager.getButtonConfirmBg() else ResourceManager.getButtonCancelBg()
            down = ResourceManager.getButtonHighlightBg()
            over = ResourceManager.getButtonHighlightBg()
        }

        val upgradeButton = TextButton(buttonText, upgradeButtonStyle).apply {
            if (canUpgrade) {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        purchaseUpgrade(upgradeType)
                    }
                })
            }
        }

        // 레이아웃 (가로 배치)
        innerRow.add(nameLabel).left().padRight(8f)
        innerRow.add(levelLabel).left().padRight(8f)
        innerRow.add(bonusLabel).left().padRight(8f)
        innerRow.add(upgradeButton).size(112f, 56f).right().expandX() // 새로운 배경 이미지 크기

        row.add(innerRow).expand().fill()
        return row
    }

    private fun purchaseUpgrade(upgradeType: InGameUpgrades.UpgradeType) {
        val currentLevel = saveData.inGameUpgrades[upgradeType.name] ?: 0
        val cost = InGameUpgrades.getUpgradeCost(upgradeType, currentLevel)

        if (saveData.silver >= cost) {
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
        showTab(currentTab)
    }
}
