package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.skills.SkillInventory
import com.example.theorb.skills.SkillRank
import com.example.theorb.skills.SubSkill
import com.example.theorb.skills.SubSkillSlots
import com.example.theorb.skills.SubSkillType
import com.example.theorb.skills.SubSkillLevelSystem
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.ui.RetroButton
import com.example.theorb.ui.ToastMessage
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

    // 보조스킬 관련
    private var selectedSubSkill: SubSkillType? = null
    private lateinit var subSkillDescriptionContainer: Table

    // 선택된 스킬 관련 (보조스킬 탭 전용)
    private var selectedMainSkillForSubSkill: Pair<String, SkillRank>? = null // 보조스킬 장착을 위해 선택된 메인스킬

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

        // 하단: 스킬 리스트 (75%)
        val skillListSection = createSkillListSection()

        mainLayout.add(equippedSection).fillX().expandX().height(contentHeight * 0.25f).padBottom(12f).row()
        mainLayout.add(skillListSection).fillX().expandX().height(contentHeight * 0.75f)

        return mainLayout
    }

    private fun createEquippedSkillsSection(): Table {
        val section = Table().apply {
            pad(16f)
        }

        val titleLabel = Label("메인 스킬", skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
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
                selectedMainSkillForSubSkill = null // 탭 전환 시 선택 초기화
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
                selectedMainSkillForSubSkill = null // 탭 전환 시 선택 초기화
                selectedSubSkill = null
                updateSkillTypeUI()
                updateTabButtons()
            }
        }

        tabButtonTable.add(mainSkillButton).width(100f).height(42f).padRight(8f)
        tabButtonTable.add(subSkillButton).width(100f).height(42f)

        // 헤더 레이아웃: 제목을 왼쪽, 탭 버튼을 오른쪽에 배치
        headerTable.add(titleLabel).expandX().left()
        headerTable.add(tabButtonTable).right()

        // 보조스킬 설명 영역 (보조스킬 탭에서만 표시)
        subSkillDescriptionContainer = createSubSkillDescriptionArea()

        skillListContainer = Table()
        updateSkillListUI()

        skillScrollPane = ScrollPane(skillListContainer, BaseScreen.skin).apply {
            setScrollingDisabled(true, false)
            setFlickScroll(true)
            setSmoothScrolling(true)
        }

        section.add(headerTable).fillX().padBottom(12f).row()

        // 보조스킬 탭일 때만 설명 영역 + 버튼 영역 추가
        if (currentSkillType == SkillType.SUB) {
            section.add(subSkillDescriptionContainer).fillX().padBottom(8f).row()
        }

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

        // 슬롯 해금 버튼 (오브로 해금)
        if (maxSlots < 6) { // 최대 6개 슬롯
            val cost = getSlotUnlockCost(maxSlots)
            val unlockButton = RetroButton.createTextButton(
                text = "+\n${cost}오브",
                skin = BaseScreen.skin,
                labelStyle = "label-small-bold",
                textColor = if (saveData.orbs >= cost) BaseScreen.TEXT_PRIMARY else TEXT_SECONDARY,
                defaultImage = ResourceManager.getRetroSquareNagDefault(),
                eventImage = ResourceManager.getRetroSquareNagEvent()
            ) {
                unlockSkillSlot()
            }
            equippedSkillsContainer.add(unlockButton).size(slotSize).pad(4f)
        }
    }

    private fun createSkillSlot(slotIndex: Int, slotSize: Float): com.badlogic.gdx.scenes.scene2d.ui.Stack {
        val stack = com.badlogic.gdx.scenes.scene2d.ui.Stack()

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

            // 보조스킬 탭에서만 클릭 이벤트 처리
            if (currentSkillType == SkillType.SUB) {
                slot.touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
                slot.addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                        onEquippedSkillClickedForSubSkill(skillInfo.first, rank)
                    }
                })
            }
        } else {
            // 빈 슬롯
            val emptyLabel = Label("빈 슬롯", BaseScreen.skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = BaseScreen.TEXT_SECONDARY
            }
            slot.add(emptyLabel).center()
        }

        stack.add(slot)

        // 보조스킬 탭에서 선택된 메인 스킬이면 선택 패널 오버레이 추가
        if (currentSkillType == SkillType.SUB && equippedSkillId != null && selectedMainSkillForSubSkill != null) {
            val skillInfo = parseSkillId(equippedSkillId)
            if (skillInfo.first == selectedMainSkillForSubSkill!!.first && skillInfo.second == selectedMainSkillForSubSkill!!.second) {
                val overlaySize = slotSize * 1.15f
                val overlay = Image(ResourceManager.getSkillSelectedPanel4850()).apply {
                    setSize(overlaySize, overlaySize)
                }
                val overlayContainer = Table().apply {
                    add(overlay).size(overlaySize)
                }
                stack.add(overlayContainer)
            }
        }

        return stack
    }

    private fun onEquippedSkillClickedForSubSkill(skillType: String, rank: SkillRank) {
        // 보조스킬 탭에서만 사용: 메인 스킬 선택
        selectedMainSkillForSubSkill = Pair(skillType, rank)
        selectedSubSkill = null

        // UI 업데이트
        updateEquippedSkillsUI()
        updateSkillListUI()
        updateSubSkillDescription()
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
        // 메인 레이아웃을 다시 생성
        mainLayout.clear()

        // 컨텐츠 영역 높이 계산
        val contentHeight = getContentAreaHeight()

        // 상단: 장착된 스킬 슬롯 (25%)
        val equippedSection = createEquippedSkillsSection()

        // 하단: 스킬 리스트
        val skillListSection = createSkillListSection()

        mainLayout.add(equippedSection).fillX().expandX().height(contentHeight * 0.25f).padBottom(12f).row()
        mainLayout.add(skillListSection).fillX().expandX().height(contentHeight * 0.75f)

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

    private fun createSubSkillDescriptionArea(): Table {
        val container = Table()

        // 초기에도 배경 패널 표시
        val panel = Table().apply {
            background = ResourceManager.getRectanglePanel340120()
            pad(16f)
        }

        val placeholderLabel = Label("장착스킬을 선택해주세요", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_SECONDARY
        }

        panel.add(placeholderLabel).center().expand().height(100f)
        container.add(panel).fillX()

        return container
    }

    private fun createSubSkillGrid() {
        val columns = 6
        val rows = 3

        // 선택된 메인 스킬이 있으면 호환되는 보조스킬만, 없으면 모든 보조스킬 표시
        val availableSubSkills = if (selectedMainSkillForSubSkill != null) {
            val mainSkill = com.example.theorb.skills.SkillRegistry.createSkill(selectedMainSkillForSubSkill!!.first)
            SubSkillType.values().filter { subSkillType ->
                subSkillType.requiredTags.isEmpty() || subSkillType.requiredTags.any { it in mainSkill.tags }
            }
        } else {
            SubSkillType.values().toList()
        }

        var skillIndex = 0

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (skillIndex < availableSubSkills.size) {
                    val subSkillType = availableSubSkills[skillIndex]
                    val skillButton = createSubSkillButton(subSkillType)
                    skillListContainer.add(skillButton).size(48f, 48f).pad(4f)
                    skillIndex++
                } else {
                    // 빈 슬롯
                    val emptyPanel = Image(ResourceManager.getSkillIconPanel4848()).apply {
                        setSize(48f, 48f)
                        color = com.badlogic.gdx.graphics.Color.DARK_GRAY
                    }
                    skillListContainer.add(emptyPanel).size(48f, 48f).pad(4f)
                }
            }
            skillListContainer.row()
        }
    }

    private fun createSubSkillButton(subSkillType: SubSkillType): com.badlogic.gdx.scenes.scene2d.ui.Stack {
        val stack = com.badlogic.gdx.scenes.scene2d.ui.Stack()

        // 보조스킬 인벤토리에서 보유 여부 확인
        val effectTypeName = subSkillType.effectType.name
        val inventoryData = saveData.subSkillInventory[effectTypeName]
        val isOwned = inventoryData != null
        val level = (inventoryData?.get("level") as? Number)?.toInt() ?: 1

        val button = RetroButton.createTextButton(
            text = if (isOwned) "${subSkillType.displayName.take(2)}\nLv.$level" else "???", // 미보유 시 ???
            skin = skin,
            labelStyle = "label-small",
            textColor = if (isOwned) TEXT_PRIMARY else com.badlogic.gdx.graphics.Color.GRAY,
            defaultImage = ResourceManager.getSkillIconPanel4848(),
            eventImage = ResourceManager.getSkillIconPanel4848(),
            buttonSize = 48f
        ) {
            selectSubSkill(subSkillType)
        }

        stack.add(button)

        // 장착된 보조스킬이면 체크 표시 추가
        if (selectedMainSkillForSubSkill != null) {
            val mainSkillId = "${selectedMainSkillForSubSkill!!.first}:${selectedMainSkillForSubSkill!!.second.name}"
            val equippedSubSkills = getCompatibleSubSkills(mainSkillId)
            val equippedTypes = equippedSubSkills.mapNotNull { it["type"] as? String }

            if (subSkillType.effectType.name in equippedTypes) {
                val checkIcon = Image(ResourceManager.getRetroCheck()).apply {
                    setSize(16f, 16f)
                }
                val checkTable = Table().apply {
                    add(checkIcon).size(16f).top().left().expand()
                }
                stack.add(checkTable)
            }
        }

        return stack
    }

    private fun selectSubSkill(subSkillType: SubSkillType) {
        selectedSubSkill = subSkillType
        updateSubSkillDescription()
    }


    private fun updateSubSkillDescription() {
        subSkillDescriptionContainer.clear()

        if (currentSkillType == SkillType.SUB) {
            // 전체를 하나의 배경 패널로 (고정 높이 설정)
            val panel = Table().apply {
                background = ResourceManager.getRectanglePanel340120()
                pad(16f)
            }

            if (selectedSubSkill != null) {
                val skill = selectedSubSkill!!

                // 메인 스킬이 선택되었는지에 따라 장착 관련 정보 표시
                val mainSkillId = if (selectedMainSkillForSubSkill != null) {
                    "${selectedMainSkillForSubSkill!!.first}:${selectedMainSkillForSubSkill!!.second.name}"
                } else null

                val equippedSubSkills = if (mainSkillId != null) getCompatibleSubSkills(mainSkillId) else emptyList()
                val equippedTypes = equippedSubSkills.mapNotNull { it["type"] as? String }
                val maxSlots = if (mainSkillId != null) SubSkillSlots.getMaxSubSkillSlots(mainSkillId) else 0
                val isEquipped = mainSkillId != null && skill.effectType.name in equippedTypes
                val canEquip = mainSkillId != null && !isEquipped && equippedSubSkills.size < maxSlots

                // 스킬 이름과 경험치 표시
                val nameTable = Table()
                val nameLabel = Label(skill.displayName, skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
                    color = TEXT_PRIMARY
                }

                // 보조스킬 인벤토리에서 경험치 정보 가져오기
                val effectTypeName = skill.effectType.name
                val inventoryData = saveData.subSkillInventory[effectTypeName]
                val currentExp = (inventoryData?.get("exp") as? Number)?.toInt() ?: 0
                val inventoryLevel = (inventoryData?.get("level") as? Number)?.toInt() ?: 1
                val requiredExp = SubSkillLevelSystem.getRequiredExp(inventoryLevel)

                val expLabel = Label("$currentExp/$requiredExp", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = TEXT_SECONDARY
                }

                nameTable.add(nameLabel).left()
                nameTable.add(expLabel).left().padLeft(8f)

                // 태그 정보
                val tagsText = if (skill.requiredTags.isNotEmpty()) {
                    skill.requiredTags.joinToString(", ") { it.displayName }
                } else {
                    "모든 스킬에 적용 가능"
                }
                val tagsLabel = Label(tagsText, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = ACCENT
                }

                // 스킬 설명 (장착된 경우 실제 레벨과 값 표시, 아니면 레벨별 범위 표시)
                val equippedSkillData = if (isEquipped) {
                    equippedSubSkills.find { (it["type"] as? String) == skill.effectType.name }
                } else null

                val equippedLevel = (equippedSkillData?.get("level") as? Number)?.toInt() ?: 1

                val description = if (isEquipped) {
                    // 장착된 경우 현재 레벨의 효과 표시
                    "Lv.$equippedLevel - ${skill.getDescriptionForLevel(equippedLevel)}"
                } else {
                    // 장착 안된 경우 1레벨 기준 설명
                    "Lv.1 - ${skill.getDescriptionForLevel(1)}"
                }

                val descLabel = Label(description, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = TEXT_SECONDARY
                    setWrap(true)
                }

                panel.add(nameTable).left().colspan(2).row()
                panel.add(tagsLabel).left().colspan(2).padTop(4f).row()
                panel.add(descLabel).left().colspan(2).width(300f).height(60f).padTop(8f).row()

                // 슬롯 정보와 버튼을 한 줄에 배치
                if (mainSkillId != null) {
                    val slotInfoLabel = Label("보조스킬: ${equippedSubSkills.size}/$maxSlots",
                        skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
                        color = TEXT_PRIMARY
                    }
                    panel.add(slotInfoLabel).left().expandX().padTop(12f)

                    // 장착/장착해제 버튼
                    if (isEquipped) {
                        val unequipButton = RetroButton.createTextButton(
                            text = "장착 해제",
                            skin = skin,
                            labelStyle = "label-default-bold",
                            textColor = TEXT_PRIMARY,
                            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
                            eventImage = ResourceManager.getRetroRectanglePosEvent(),
                            buttonSize = 36f
                        ) {
                            unequipSubSkillDirect(skill)
                        }
                        panel.add(unequipButton).width(100f).height(36f).right().padTop(12f)
                    } else if (canEquip) {
                        val equipButton = RetroButton.createTextButton(
                            text = "장착",
                            skin = skin,
                            labelStyle = "label-default-bold",
                            textColor = TEXT_PRIMARY,
                            defaultImage = ResourceManager.getRetroRectanglePosDefault(),
                            eventImage = ResourceManager.getRetroRectanglePosEvent(),
                            buttonSize = 36f
                        ) {
                            equipSubSkillDirect(skill)
                        }
                        panel.add(equipButton).width(100f).height(36f).right().padTop(12f)
                    }
                } else {
                    // 메인 스킬이 선택되지 않았을 때도 장착 버튼 표시
                    val equipButton = RetroButton.createTextButton(
                        text = "장착",
                        skin = skin,
                        labelStyle = "label-default-bold",
                        textColor = TEXT_PRIMARY,
                        defaultImage = ResourceManager.getRetroRectanglePosDefault(),
                        eventImage = ResourceManager.getRetroRectanglePosEvent(),
                        buttonSize = 36f
                    ) {
                        showToastMessage("메인스킬을 선택해주세요")
                    }
                    panel.add(Table()).left().expandX().padTop(12f) // 빈 공간
                    panel.add(equipButton).width(100f).height(36f).right().padTop(12f)
                }
            } else {
                // 보조스킬이 선택되지 않은 경우
                if (selectedMainSkillForSubSkill != null) {
                    val mainSkillId = "${selectedMainSkillForSubSkill!!.first}:${selectedMainSkillForSubSkill!!.second.name}"
                    val equippedSubSkills = getCompatibleSubSkills(mainSkillId)
                    val maxSlots = SubSkillSlots.getMaxSubSkillSlots(mainSkillId)

                    val placeholderLabel = Label("보조스킬을 선택해 주세요.", skin.get("label-default", Label.LabelStyle::class.java)).apply {
                        color = TEXT_SECONDARY
                    }
                    panel.add(placeholderLabel).center().expand().height(60f).colspan(2).row()

                    // 슬롯 정보만 표시
                    val slotInfoLabel = Label("보조스킬: ${equippedSubSkills.size}/$maxSlots",
                        skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
                        color = TEXT_PRIMARY
                    }
                    panel.add(slotInfoLabel).left().expandX()
                } else {
                    // 메인 스킬도 선택되지 않은 경우
                    val placeholderLabel = Label("메인스킬을 선택해 주세요", skin.get("label-default", Label.LabelStyle::class.java)).apply {
                        color = TEXT_SECONDARY
                    }
                    panel.add(placeholderLabel).center().expand().height(100f)
                }
            }

            subSkillDescriptionContainer.add(panel).fillX().height(180f)
        }
    }

    private fun showToastMessage(message: String) {
        ToastMessage.show(stage, message, skin)
    }

    /**
     * 보조스킬 데이터 호환성 처리 헬퍼
     * 기존 String 형식 데이터를 새로운 Map 형식으로 변환
     */
    @Suppress("UNCHECKED_CAST")
    private fun getCompatibleSubSkills(mainSkillId: String): List<Map<String, Any>> {
        val rawData: MutableList<Map<String, Any>>? = saveData.equippedSubSkills[mainSkillId]
        if (rawData == null) return emptyList()

        // 모든 아이템이 이미 올바른 Map 형식이어야 함
        // 만약 오류가 발생하면 전체 리스트를 정리
        return try {
            rawData.filter { item ->
                item is Map<*, *> && item.containsKey("type")
            }
        } catch (e: Exception) {
            // 오류 발생 시 데이터 초기화
            Gdx.app.log("SkillScreen", "보조스킬 데이터 형식 오류, 초기화: ${e.message}")
            saveData.equippedSubSkills[mainSkillId] = mutableListOf()
            SaveManager.save(saveData)
            emptyList()
        }
    }

    private fun equipSubSkillDirect(subSkillType: SubSkillType) {
        if (selectedMainSkillForSubSkill == null) return

        // 보조스킬 보유 여부 확인
        val effectTypeName = subSkillType.effectType.name
        val inventoryData = saveData.subSkillInventory[effectTypeName]
        if (inventoryData == null) {
            ToastMessage.show(stage, "보유하지 않은 보조스킬입니다.", skin)
            return
        }

        val inventoryLevel = (inventoryData["level"] as? Number)?.toInt() ?: 1

        val mainSkillId = "${selectedMainSkillForSubSkill!!.first}:${selectedMainSkillForSubSkill!!.second.name}"
        val currentSubSkills = getCompatibleSubSkills(mainSkillId).toMutableList()
        val currentSubSkillTypes = currentSubSkills.mapNotNull { it["type"] as? String }

        // 같은 타입이 이미 장착되어 있는지 확인
        if (subSkillType.effectType.name in currentSubSkillTypes) {
            return
        }

        val maxSlots = SubSkillSlots.getMaxSubSkillSlots(mainSkillId)
        if (currentSubSkills.size < maxSlots) {
            // 인벤토리의 레벨로 장착
            currentSubSkills.add(mapOf(
                "type" to subSkillType.effectType.name,
                "level" to inventoryLevel
            ))
            saveData.equippedSubSkills[mainSkillId] = currentSubSkills
            SaveManager.save(saveData)
            updateSubSkillDescription()
            updateSkillListUI() // 체크 표시 업데이트
        }
    }

    private fun unequipSubSkillDirect(subSkillType: SubSkillType) {
        if (selectedMainSkillForSubSkill == null) return

        val mainSkillId = "${selectedMainSkillForSubSkill!!.first}:${selectedMainSkillForSubSkill!!.second.name}"
        val currentSubSkills = getCompatibleSubSkills(mainSkillId).toMutableList()

        // 해당 타입의 보조스킬 찾아서 제거
        val removed = currentSubSkills.removeIf { (it["type"] as? String) == subSkillType.effectType.name }

        if (removed) {
            saveData.equippedSubSkills[mainSkillId] = currentSubSkills
            SaveManager.save(saveData)
            updateSubSkillDescription()
            updateSkillListUI() // 체크 표시 업데이트
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

        // 장착되어 있는지 확인
        val skillId = "${skillType}:${rank.name}"
        val isEquipped = saveData.equippedSkills.contains(skillId)

        val buttonStack = com.badlogic.gdx.scenes.scene2d.ui.Stack()
        val buttonContainer = Table()

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
                onSkillRankButtonClicked(skillType, rank)
            }
        }

        buttonContainer.add(button).size(50f)
        buttonStack.add(buttonContainer)

        // 장착된 스킬이면 체크 표시 추가 (좌상단)
        if (isEquipped) {
            val checkIcon = Image(ResourceManager.getRetroCheck()).apply {
                setSize(16f, 16f)
            }
            val checkTable = Table().apply {
                add(checkIcon).size(16f).top().left().expand()
            }
            buttonStack.add(checkTable)
        }

        // 메인스킬 탭에서는 선택 오버레이 없음
        container.add(buttonStack).size(50f).row()

        // 보유 개수 / 필요 개수 라벨
        val countText = if (rank.canUpgrade()) {
            "$ownedCount/$requiredCount"
        } else {
            "$ownedCount" // 최고 등급(SSS)은 필요 개수 없음
        }

        val countLabel = Label(countText, skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
            color = if (ownedCount > 0) TEXT_PRIMARY else TEXT_DISABLED
        }

        container.add(countLabel).padTop(2f)

        return container
    }

    private fun onSkillRankButtonClicked(skillType: String, rank: SkillRank) {
        if (currentSkillType == SkillType.MAIN) {
            val skillId = "${skillType}:${rank.name}"
            val isEquipped = saveData.equippedSkills.contains(skillId)

            if (isEquipped) {
                // 장착되어 있는 스킬 클릭 → 즉시 장착 해제
                val slotIndex = saveData.equippedSkills.indexOf(skillId)
                unequipSkill(slotIndex)
            } else {
                // 장착되지 않은 스킬 클릭 → 즉시 장착
                equipSkill(skillType, rank)
            }
        } else {
            // 보조스킬 탭: 선택하지 않음 (메인 스킬은 장착된 스킬에서만 선택)
        }
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

        // UI 업데이트
        updateEquippedSkillsUI()
        updateSkillListUI()
    }

    private fun unequipSkill(slotIndex: Int) {
        if (slotIndex < saveData.equippedSkills.size) {
            saveData.equippedSkills.removeAt(slotIndex)
            SaveManager.save(saveData)

            // UI 업데이트
            updateEquippedSkillsUI()
            updateSkillListUI()
        }
    }

    private fun unlockSkillSlot() {
        val cost = getSlotUnlockCost(saveData.maxSkillSlots)
        if (saveData.orbs >= cost) {
            saveData.orbs -= cost
            saveData.maxSkillSlots++
            SaveManager.save(saveData)
            updateEquippedSkillsUI()
        } else {
            ToastMessage.show(stage, "오브가 부족합니다. (필요: ${cost}오브, 보유: ${saveData.orbs}오브)", skin, duration = 2f)
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
            1 -> 50   // 2번째 슬롯: 50젬
            2 -> 200  // 3번째 슬롯: 200젬
            3 -> 500  // 4번째 슬롯: 500젬
            4 -> 1000 // 5번째 슬롯: 1000젬
            5 -> 2000 // 6번째 슬롯: 2000젬
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
