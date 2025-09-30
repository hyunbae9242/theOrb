package com.example.theorb.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.skills.SkillInventory
import com.example.theorb.skills.SkillRank
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.ui.RetroButton
import com.example.theorb.ui.TopBar
import com.example.theorb.util.ResourceManager

class SkillScreen(private val game: Game, private val saveData: SaveData) : BaseScreen() {
    private lateinit var stage: Stage
    private lateinit var mainLayout: Table
    private lateinit var skillInventory: SkillInventory
    private lateinit var topBar: TopBar

    // UI 컴포넌트
    private lateinit var equippedSkillsContainer: Table
    private lateinit var skillListContainer: Table
    private lateinit var skillScrollPane: ScrollPane

    // 스킬 타입 (메인스킬/보조스킬)
    private enum class SkillType(val displayName: String) {
        MAIN("메인스킬"),
        SUB("보조스킬")
    }
    private var currentSkillType = SkillType.MAIN
    private lateinit var mainSkillButton: com.badlogic.gdx.scenes.scene2d.ui.Stack
    private lateinit var subSkillButton: com.badlogic.gdx.scenes.scene2d.ui.Stack

    override fun show() {
        stage = Stage(viewport)
        com.badlogic.gdx.Gdx.input.inputProcessor = stage

        // Skin 초기화
        BaseScreen.initSharedResources()
        topBar = TopBar(stage, skin)


        // 기존 equippedSkills 정리 (잘못된 형식 제거)
        saveData.equippedSkills = saveData.equippedSkills.filter { skillId ->
            skillId.contains(":") && skillId.split(":").size == 2
        }.toMutableList()

        // 스킬 인벤토리 초기화
        skillInventory = SkillInventory()
        skillInventory.fromSaveData(saveData.skillInventory)

        createUI()
    }

    private fun createUI() {
        // 공통 레이아웃 시스템 사용
        val root = createRootLayout(stage)

        // ===== 상단 바 =====
        val topBarTable = topBar.createTopBar()
        addTopBar(root, topBarTable)

        // ===== 메인 컨텐츠 =====
        val mainContent = createMainContent()
        addMainContent(root, mainContent)

        // ===== 하단 네비게이션 =====
        val bottomNav = BottomNavigation(game, BaseScreen.skin, BottomNavigation.Tab.SKILL)
        val bottomNavTable = bottomNav.createBottomNavigation()
        addBottomNavigation(root, bottomNavTable)
    }

    private fun createMainContent(): Table {
        mainLayout = Table()

        // 컨텐츠 영역 높이 계산
        val contentHeight = getContentAreaHeight()

        // 상단: 장착된 스킬 슬롯 (25%)
        val equippedSection = createEquippedSkillsSection()

        // 하단: 모든 스킬 리스트 (75%)
        val skillListSection = createSkillListSection()

        mainLayout.add(equippedSection).fillX().expandX().height(contentHeight * 0.25f).padBottom(12f).row()
        mainLayout.add(skillListSection).fillX().expandX().height(contentHeight * 0.75f)

        return mainLayout
    }

    private fun createEquippedSkillsSection(): Table {
        val section = Table().apply {
            pad(16f)
        }

        val titleLabel = Label("장착 스킬", BaseScreen.skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
            color = BaseScreen.TEXT_PRIMARY
        }

        equippedSkillsContainer = Table()
        updateEquippedSkillsUI()

        section.add(titleLabel).center().padBottom(12f).row()
        section.add(equippedSkillsContainer).expand().fill()

        return section
    }

    private fun createSkillListSection(): Table {
        val section = Table().apply {
            background = ResourceManager.getSquarePanel360()
            pad(16f)
        }

        // 상단: 스킬 보관함 라벨과 메인스킬/보조스킬 탭
        val headerTable = Table()

        // 원래 텍스트
        val titleLabel = Label("스킬 보관함", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        // 메인스킬/보조스킬 탭 버튼들
        val tabButtonTable = Table()

        mainSkillButton = RetroButton.createTextButton(
            text = SkillType.MAIN.displayName,
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = if (currentSkillType == SkillType.MAIN) TEXT_PRIMARY else TEXT_SECONDARY,
            defaultImage = if (currentSkillType == SkillType.MAIN) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
            eventImage = if (currentSkillType == SkillType.MAIN) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent(),
            buttonSize = 42f
        ) {
            if (currentSkillType != SkillType.MAIN) {
                currentSkillType = SkillType.MAIN
                updateSkillTypeUI()
                updateTabButtons()
            }
        }

        subSkillButton = RetroButton.createTextButton(
            text = SkillType.SUB.displayName,
            skin = skin,
            labelStyle = "label-default-bold",
            textColor = if (currentSkillType == SkillType.SUB) TEXT_PRIMARY else TEXT_SECONDARY,
            defaultImage = if (currentSkillType == SkillType.SUB) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
            eventImage = if (currentSkillType == SkillType.SUB) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent(),
            buttonSize = 42f
        ) {
            if (currentSkillType != SkillType.SUB) {
                currentSkillType = SkillType.SUB
                updateSkillTypeUI()
                updateTabButtons()
            }
        }

        tabButtonTable.add(mainSkillButton).width(100f).height(42f).padRight(8f)
        tabButtonTable.add(subSkillButton).width(100f).height(42f)

        // 헤더 레이아웃: 제목을 왼쪽, 탭 버튼을 오른쪽에 배치
        headerTable.add(titleLabel).expandX().left()
        headerTable.add(tabButtonTable).right()

        skillListContainer = Table()
        updateSkillListUI()

        skillScrollPane = ScrollPane(skillListContainer, BaseScreen.skin).apply {
            setScrollingDisabled(true, false)
            setFlickScroll(true)
            setSmoothScrolling(true)
        }

        section.add(headerTable).fillX().padBottom(12f).row()
        section.add(skillScrollPane).expand().fill()

        return section
    }

    private fun updateEquippedSkillsUI() {
        equippedSkillsContainer.clear()

        val slotSize = 64f
        val maxSlots = saveData.maxSkillSlots

        for (i in 0 until maxSlots) {
            val skillSlot = createSkillSlot(i, slotSize)
            equippedSkillsContainer.add(skillSlot).size(slotSize).pad(4f)
        }

        // 슬롯 해금 버튼 (젬으로 해금)
        if (maxSlots < 6) { // 최대 6개 슬롯
            val unlockButton = RetroButton.createTextButton(
                text = "+",
                skin = BaseScreen.skin,
                labelStyle = "label-default-bold",
                textColor = BaseScreen.TEXT_PRIMARY,
                defaultImage = ResourceManager.getRetroSquareNagDefault(),
                eventImage = ResourceManager.getRetroSquareNagEvent()
            ) {
                unlockSkillSlot()
            }
            equippedSkillsContainer.add(unlockButton).size(slotSize).pad(4f)
        }
    }

    private fun createSkillSlot(slotIndex: Int, slotSize: Float): Table {
        val slot = Table().apply {
            background = ResourceManager.getRetroSquareNagDefault()
        }

        val equippedSkillId = if (slotIndex < saveData.equippedSkills.size) {
            saveData.equippedSkills[slotIndex]
        } else null

        if (equippedSkillId != null) {
            // 장착된 스킬 표시
            val skillInfo = parseSkillId(equippedSkillId)
            val skillName = getSkillDisplayName(skillInfo.first)
            val rank = skillInfo.second

            val skillLabel = Label(skillName, BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = rank.color
            }
            val rankLabel = Label(rank.displayName, BaseScreen.skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
                color = rank.color
            }

            slot.add(skillLabel).center().row()
            slot.add(rankLabel).center()

            // 클릭으로 해제
            slot.touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
            slot.addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                    unequipSkill(slotIndex)
                }
            })
        } else {
            // 빈 슬롯
            val emptyLabel = Label("빈 슬롯", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }
            slot.add(emptyLabel).center()
        }

        return slot
    }

    private fun updateSkillListUI() {
        skillListContainer.clear()

        when (currentSkillType) {
            SkillType.MAIN -> {
                val availableSkills = listOf("Fireball", "IceLance", "LightningStrike", "DivineNova")

                for (skillType in availableSkills) {
                    val skillRow = createSkillRow(skillType)
                    skillListContainer.add(skillRow).fillX().pad(4f).row()
                }
            }
            SkillType.SUB -> {
                createSubSkillGrid()
            }
        }
    }

    private fun updateSkillTypeUI() {
        updateSkillListUI()
        // 스크롤 위치 초기화
        skillScrollPane.scrollY = 0f
    }

    private fun updateTabButtons() {
        // 메인스킬 버튼 업데이트
        val isMainSelected = currentSkillType == SkillType.MAIN
        RetroButton.updateTextButtonEnabled(
            mainSkillButton,
            true,
            if (isMainSelected) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
            if (isMainSelected) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent()
        )
        RetroButton.updateTextButtonStyle(
            mainSkillButton,
            skin,
            "label-default-bold",
            if (isMainSelected) TEXT_PRIMARY else TEXT_SECONDARY
        )

        // 보조스킬 버튼 업데이트
        val isSubSelected = currentSkillType == SkillType.SUB
        RetroButton.updateTextButtonEnabled(
            subSkillButton,
            true,
            if (isSubSelected) ResourceManager.getRetroRectanglePosDefault() else ResourceManager.getRetroRectangleNagDefault(),
            if (isSubSelected) ResourceManager.getRetroRectanglePosEvent() else ResourceManager.getRetroRectangleNagEvent()
        )
        RetroButton.updateTextButtonStyle(
            subSkillButton,
            skin,
            "label-default-bold",
            if (isSubSelected) TEXT_PRIMARY else TEXT_SECONDARY
        )
    }

    private fun createSubSkillGrid() {
        val columns = 6
        val rows = 3

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val skillIconPanel = Image(ResourceManager.getSkillIconPanel4848()).apply {
                    setSize(48f, 48f)
                }

                // 임시로 빈 아이콘 패널만 표시
                skillListContainer.add(skillIconPanel).size(48f, 48f).pad(4f)
            }
            skillListContainer.row()
        }
    }

    private fun createSkillRow(skillType: String): Table {
        val row = Table().apply {
            pad(8f)
        }

        // 스킬 이름
        val skillName = getSkillDisplayName(skillType)
        val nameLabel = Label(skillName, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        row.add(nameLabel).left().width(80f).padRight(8f)

        // 각 등급별 버튼 (C ~ SSS)
        for (rank in SkillRank.values()) {
            val skillId = "${skillType}:${rank.name}"
            val isUnlocked = saveData.unlockedSkills.contains(skillId)
            val isOwned = skillInventory.getSkillsByTypeAndRank(skillType, rank).isNotEmpty()

            val buttonContainer = createSkillRankButton(skillType, rank, isUnlocked, isOwned)
            row.add(buttonContainer).width(50f).height(60f).pad(2f)
        }

        return row
    }

    private fun createSkillRankButton(
        skillType: String,
        rank: SkillRank,
        isUnlocked: Boolean,
        isOwned: Boolean
    ): Table {
        val container = Table()

        val textColor = if (isUnlocked) TEXT_PRIMARY else TEXT_SECONDARY

        // 보유 개수 계산
        val ownedCount = skillInventory.getSkillsByTypeAndRank(skillType, rank).size
        val requiredCount = rank.upgradeRequirement

        val button = RetroButton.createTextButton(
            text = rank.displayName,
            skin = skin,
            labelStyle = "label-small-bold",
            textColor = textColor,
            defaultImage = if (isUnlocked) ResourceManager.getRetroSquarePosDefault() else ResourceManager.getRetroSquareNagDefault(),
            eventImage = if (isUnlocked) ResourceManager.getRetroSquarePosEvent() else ResourceManager.getRetroSquareNagDefault(),
            isEnabled = isUnlocked
        ) {
            if (isUnlocked) {
                equipSkill(skillType, rank)
            }
        }

        // 보유 개수 / 필요 개수 라벨
        val countText = if (rank.canUpgrade()) {
            "$ownedCount/$requiredCount"
        } else {
            "$ownedCount" // 최고 등급(SSS)은 필요 개수 없음
        }

        val countLabel = Label(countText, skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
            color = if (ownedCount > 0) TEXT_PRIMARY else TEXT_DISABLED
        }

        // 버튼과 개수 라벨을 세로로 배치
        container.add(button).row()
        container.add(countLabel).padTop(2f)

        return container
    }

    private fun equipSkill(skillType: String, rank: SkillRank) {
        val skillId = "${skillType}:${rank.name}"

        // 빈 슬롯 찾기
        val emptySlotIndex = findEmptySkillSlot()
        if (emptySlotIndex == -1) {
            // TODO: "슬롯이 가득참" 메시지 표시
            return
        }

        // 이미 장착되어 있는지 확인
        if (saveData.equippedSkills.contains(skillId)) {
            // TODO: "이미 장착됨" 메시지 표시
            return
        }

        // 스킬 장착
        if (emptySlotIndex >= saveData.equippedSkills.size) {
            saveData.equippedSkills.add(skillId)
        } else {
            saveData.equippedSkills[emptySlotIndex] = skillId
        }

        SaveManager.save(saveData)
        updateEquippedSkillsUI()
    }

    private fun unequipSkill(slotIndex: Int) {
        if (slotIndex < saveData.equippedSkills.size) {
            saveData.equippedSkills.removeAt(slotIndex)
            SaveManager.save(saveData)
            updateEquippedSkillsUI()
        }
    }

    private fun unlockSkillSlot() {
        val cost = getSlotUnlockCost(saveData.maxSkillSlots)
        if (saveData.gems >= cost) {
            saveData.gems -= cost
            saveData.maxSkillSlots++
            SaveManager.save(saveData)
            updateEquippedSkillsUI()
        } else {
            // TODO: "젬이 부족합니다" 메시지 표시
        }
    }

    private fun findEmptySkillSlot(): Int {
        for (i in 0 until saveData.maxSkillSlots) {
            if (i >= saveData.equippedSkills.size || saveData.equippedSkills[i].isEmpty()) {
                return i
            }
        }
        return -1
    }

    private fun getSlotUnlockCost(currentSlots: Int): Int {
        return when (currentSlots) {
            1 -> 100  // 2번째 슬롯: 100젬
            2 -> 200  // 3번째 슬롯: 200젬
            3 -> 400  // 4번째 슬롯: 400젬
            4 -> 800  // 5번째 슬롯: 800젬
            5 -> 1600 // 6번째 슬롯: 1600젬
            else -> Int.MAX_VALUE
        }
    }

    private fun getSkillDisplayName(skillType: String): String {
        return try {
            val skill = com.example.theorb.skills.SkillRegistry.createSkill(skillType)
            skill.name
        } catch (e: Exception) {
            skillType // 스킬 생성에 실패하면 타입명을 그대로 반환
        }
    }

    private fun parseSkillId(skillId: String): Pair<String, SkillRank> {
        val parts = skillId.split(":")
        if (parts.size != 2) {
            // 잘못된 형식의 skillId 처리 - 기본값 반환
            return Pair("Fireball", SkillRank.C)
        }
        val skillType = parts[0]
        val rank = try {
            SkillRank.valueOf(parts[1])
        } catch (e: IllegalArgumentException) {
            SkillRank.C // 잘못된 랭크명일 경우 기본값
        }
        return Pair(skillType, rank)
    }


    override fun render(delta: Float) {
        com.badlogic.gdx.Gdx.gl.glClearColor(
            BaseScreen.BACKGROUND.r,
            BaseScreen.BACKGROUND.g,
            BaseScreen.BACKGROUND.b,
            BaseScreen.BACKGROUND.a
        )
        com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage?.dispose()
    }
}
