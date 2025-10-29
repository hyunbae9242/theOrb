package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.skills.SkillInventory
import com.example.theorb.skills.SkillRank
import com.example.theorb.skills.SubSkillSlots
import com.example.theorb.skills.SubSkillType
import com.example.theorb.skills.SubSkillLevelSystem
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.modal.ModalDialog
import com.example.theorb.skills.SkillItem
import com.example.theorb.skills.SkillRegistry
import com.example.theorb.ui.RetroButton
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.ui.ToastMessage
import com.example.theorb.ui.TopBar
import com.example.theorb.util.ResourceManager

class SkillScreen(private val game: Game, private val saveData: SaveData) : BaseScreen() {
    private lateinit var stage: Stage
    private lateinit var mainLayout: Table
    private lateinit var skillInventory: SkillInventory
    private lateinit var topBar: TopBar
    private lateinit var modalDialog: ModalDialog

    // UI 컴포넌트
    private lateinit var equippedSkillsContainer: Table
    private lateinit var equippedSkillScrollPane: ScrollPane
    private lateinit var skillListContainer: Table
    private lateinit var skillScrollPane: ScrollPane

    // 스킬 타입 (메인스킬/보조스킬)
    private enum class SkillType(val displayName: String) {
        MAIN("메인스킬"),
        SUB("보조스킬")
    }
    private var currentSkillType = SkillType.MAIN
    private lateinit var mainSkillButton: ImageButton
    private lateinit var subSkillButton: ImageButton

    // 보조스킬 관련
    private var selectedSubSkill: SubSkillType? = null
    private lateinit var subSkillDescriptionContainer: Table

    // 선택된 스킬 관련 (보조스킬 탭 전용)
    private var selectedMainSkillForSubSkill: String? = null // 보조스킬 장착을 위해 선택된 메인스킬

    override fun show() {
        stage = Stage(viewport)
        com.badlogic.gdx.Gdx.input.inputProcessor = stage

        // Skin 초기화
        BaseScreen.initSharedResources()
        topBar = TopBar(stage, skin)
        modalDialog = ModalDialog(stage, skin)

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
        mainLayout = Table().apply {
            top()
        }

        // 상단
        val equippedSection = createEquippedSkillsSection()

        // 하단
        val skillListSection = createSkillListSection()

        // total 656 - 16 - 16 (top,bottom pad) = 624
        // 상단 128
        // 하단 624 - 128 - 16 (상단패딩) = 480
        mainLayout.add(equippedSection).width(400f).height(128f).padTop(16f).row()
        mainLayout.add(skillListSection).width(448f).height(480f).padTop(16f)

        return mainLayout
    }

    private fun createEquippedSkillsSection(): Table {
        val section = Table().apply {
            background = ResourceManager.getSkillEquipPanel()
            setSize(400f, 128f)
            top()
        }

        val titleLabel = Label("장착 스킬", skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        equippedSkillsContainer = Table()
        updateEquippedSkillsUI()
        equippedSkillScrollPane = ScrollPane(equippedSkillsContainer, skin).apply {
            setScrollingDisabled(false, true)
            setFlickScroll(true)
            setSmoothScrolling(true)
        }

        section.add(titleLabel).center().padTop(16f).row()
        section.add(equippedSkillScrollPane).padRight(16f).padLeft(16f)

        return section
    }

    private fun createSkillListSection(): Table {
        val section = Table().apply {
            background = ResourceManager.getSkillInventoryPanel()
            top()
        }

        // 상단: 스킬 보관함 라벨과 메인스킬/보조스킬 탭
        val headerTable = Table()

        // 원래 텍스트
        val titleLabel = Label("스킬 보관함", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        // 메인스킬/보조스킬 탭 버튼들
        val tabButtonTable = Table()

        mainSkillButton = RetroButtonV01.createIconButton(
            defaultImage = if (currentSkillType == SkillType.MAIN) ResourceManager.getActiveBasePos() else ResourceManager.getActiveBaseNag(),
            eventImage = if (currentSkillType == SkillType.MAIN) ResourceManager.getActiveEventPos() else ResourceManager.getActiveEventNag()
        ) {
            if (currentSkillType != SkillType.MAIN) {
                currentSkillType = SkillType.MAIN
                selectedMainSkillForSubSkill = null // 탭 전환 시 선택 초기화
                updateSkillTypeUI()
                updateTabButtons()
            }
        }

        subSkillButton = RetroButtonV01.createIconButton(
            defaultImage = if (currentSkillType == SkillType.SUB) ResourceManager.getSubBasePos() else ResourceManager.getSubBaseNag(),
            eventImage = if (currentSkillType == SkillType.SUB) ResourceManager.getSubEventPos() else ResourceManager.getSubEventNag()
        ) {
            if (currentSkillType != SkillType.SUB) {
                currentSkillType = SkillType.SUB
                selectedMainSkillForSubSkill = null // 탭 전환 시 선택 초기화
                selectedSubSkill = null
                updateSkillTypeUI()
                updateTabButtons()
            }
        }

        tabButtonTable.add(mainSkillButton).width(80f).height(48f).padRight(8f)
        tabButtonTable.add(subSkillButton).width(80f).height(48f)

        // 헤더 레이아웃: 제목을 왼쪽, 탭 버튼을 오른쪽에 배치
        headerTable.add(titleLabel).padLeft(16f).expandX().left()
        headerTable.add(tabButtonTable).padRight(16f).right()

        // 보조스킬 설명 영역 (보조스킬 탭에서만 표시)
        subSkillDescriptionContainer = createSubSkillDescriptionArea()

        skillListContainer = Table().apply { top() }
        updateSkillListUI()

        skillScrollPane = ScrollPane(skillListContainer, BaseScreen.skin).apply {
            setScrollingDisabled(true, false)
            setFlickScroll(true)
            setSmoothScrolling(true)
        }

        section.add(headerTable).padTop(16f).fillX().padBottom(8f).row()

        // 보조스킬 탭일 때만 설명 영역 + 버튼 영역 추가
        if (currentSkillType == SkillType.SUB) {
            section.add(subSkillDescriptionContainer).fillX().padBottom(8f).row()
        }

        section.add(skillScrollPane).expand().fill()
        return section
    }

    private fun updateEquippedSkillsUI() {
        equippedSkillsContainer.clear()
        val maxSlots = saveData.maxSkillSlots

        for (i in 0 until maxSlots) {
            val skillSlot = createSkillSlot(i)
            equippedSkillsContainer.add(skillSlot).size(64f).pad(8f)
        }

        // 슬롯 해금 버튼 (오브로 해금)
        if (maxSlots < 6) { // 최대 6개 슬롯
            val cost = getSlotUnlockCost(maxSlots)
            val unlockButton = RetroButtonV01.createIconButton(
                defaultImage = ResourceManager.getPlusBasePos(),
                eventImage = ResourceManager.getPlusEventPos()
            ) {
                modalDialog.show(
                    title = "스킬 슬롯 해금",
                    message = "장착 스킬 슬롯을 구매하시겠습니까?\n필요 오브 : $cost",
                    onConfirm = { unlockSkillSlot() },
                    onCancel = { /* 아무것도 하지 않음 */ }
                )
            }
            equippedSkillsContainer.add(unlockButton).size(48f).pad(8f)
        }
    }

    private fun createSkillSlot(slotIndex: Int): Table {
        val slot = Table().apply {
            background = ResourceManager.getSquareBasePanel()
        }

        val equippedSkillType = if (slotIndex < saveData.equippedSkills.size) {
            saveData.equippedSkills[slotIndex]
        } else null

        if (equippedSkillType != null) {
            // 장착된 스킬 표시
            val skill = com.example.theorb.skills.SkillRegistry.createSkill(equippedSkillType)
            val rank = skillInventory.getRankByType(equippedSkillType)
            val rankLabel = Label(rank.displayName, skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
                color = rank.color
            }
            slot.background = skill.baseIcon
            slot.bottom().left()
            slot.add(rankLabel).pad(4f)

            // 보조스킬 탭에서만 클릭 이벤트 처리
            if (currentSkillType == SkillType.SUB) {
                slot.touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
                slot.addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                        onEquippedSkillClickedForSubSkill(equippedSkillType)
                    }
                })
            }
        } else {
            // 빈 슬롯
            val emptyLabel = Label("EMPTY", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = TEXT_SECONDARY
            }
            slot.add(emptyLabel).center()
        }

        // 보조스킬 탭에서 선택된 메인 스킬이면 선택 패널 event 변경
        if (currentSkillType == SkillType.SUB && equippedSkillType != null && selectedMainSkillForSubSkill != null) {
            if (equippedSkillType == selectedMainSkillForSubSkill) {
                val skill = com.example.theorb.skills.SkillRegistry.createSkill(equippedSkillType)
                slot.background = skill.eventIcon
            }
        }
        return slot
    }

    private fun onEquippedSkillClickedForSubSkill(skillType: String) {
        // 보조스킬 탭에서만 사용: 메인 스킬 선택
        selectedMainSkillForSubSkill = skillType
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
                val availableSkills = skillInventory.getAllSkills()

                for (skill in availableSkills) {
                    val skillRow = createSkillRow(skill)
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


        // 상단
        val equippedSection = createEquippedSkillsSection()

        // 하단
        val skillListSection = createSkillListSection()

        // total 656 - 16 - 16 (top,bottom pad) = 624
        // 상단 128
        // 하단 624 - 128 - 16 (상단패딩) = 480
        mainLayout.add(equippedSection).width(400f).height(128f).padTop(16f).row()
        mainLayout.add(skillListSection).width(448f).height(480f).padTop(16f)

        updateSkillListUI()
        // 스크롤 위치 초기화
        skillScrollPane.scrollY = 0f
    }


    private fun updateTabButtons() {
        // 메인스킬 버튼 업데이트
        val isMainSelected = currentSkillType == SkillType.MAIN
        RetroButtonV01.updateIconButtonEnabled(
            mainSkillButton,
            true,
            if (isMainSelected) ResourceManager.getActiveBasePos() else ResourceManager.getActiveBaseNag(),
            if (isMainSelected) ResourceManager.getActiveEventPos() else ResourceManager.getActiveEventNag()
        )
        // 보조스킬 버튼 업데이트
        val isSubSelected = currentSkillType == SkillType.SUB
        RetroButtonV01.updateIconButtonEnabled(
            subSkillButton,
            true,
            if (isSubSelected) ResourceManager.getSubBasePos() else ResourceManager.getSubBaseNag(),
            if (isSubSelected) ResourceManager.getSubEventPos() else ResourceManager.getSubEventNag()
        )
    }

    private fun createSubSkillDescriptionArea(): Table {
        val container = Table()

        // 초기에도 배경 패널 표시
        val panel = Table().apply {
            background = ResourceManager.getSkillSubDecPanel()
            setSize(320f, 200f)
            pad(16f)
        }

        val placeholderLabel = Label("장착스킬을 선택해주세요", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_SECONDARY
        }

        panel.add(placeholderLabel).center()
        container.add(panel).fillX()

        return container
    }

    private fun createSubSkillGrid() {
        val columns = 6
        val rows = 3
        val subSkillList = SubSkillType.values().toList()
        var skillIndex = 0

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (skillIndex < subSkillList.size) {
                    val subSkillType = subSkillList[skillIndex]
                    val skillButton = createSubSkillButton(subSkillType)
                    skillListContainer.add(skillButton).size(48f, 48f).pad(4f)
                    skillIndex++
                } else {
                    // 빈 슬롯
                    val emptyPanel = Image(ResourceManager.getSquareBasePanel()).apply {
                        setSize(48f, 48f)
                    }
                    skillListContainer.add(emptyPanel).size(48f, 48f).pad(4f)
                }
            }
            skillListContainer.row()
        }
    }

    private fun createSubSkillButton(subSkillType: SubSkillType): com.badlogic.gdx.scenes.scene2d.ui.Stack {
        val stack = com.badlogic.gdx.scenes.scene2d.ui.Stack()
        val isSelected = selectedSubSkill != null && selectedSubSkill!!.displayName == subSkillType.displayName
        val button = RetroButtonV01.createIconButton(
            // 이미지에 각 스킬넣기
            defaultImage = if (isSelected) ResourceManager.getSquareEventPanel() else ResourceManager.getSquareBasePanel(),
            eventImage = if (isSelected) ResourceManager.getSquareEventPanel() else ResourceManager.getSquareBasePanel()
        ) {
            selectSubSkill(subSkillType)
        }

        stack.add(button)

        // 장착된 보조스킬이면 체크 표시 추가
        if (selectedMainSkillForSubSkill != null) {
            val equippedSubSkills = getCompatibleSubSkills(selectedMainSkillForSubSkill!!)
            val equippedTypes = equippedSubSkills.mapNotNull { it["type"] as? String }

            if (subSkillType.name in equippedTypes) {
                val equippedTable = Table().apply {
                    bottom()
                }
                val equippedLabel = Label("E", skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
                    color = TEXT_PRIMARY
                }
                equippedTable.add(equippedLabel).left()
                stack.add(equippedTable)
            }
        }

        return stack
    }

    private fun selectSubSkill(subSkillType: SubSkillType) {
        selectedSubSkill = subSkillType
        updateSubSkillDescription()
        updateSkillListUI()
    }


    private fun updateSubSkillDescription() {
        subSkillDescriptionContainer.clear()

        if (currentSkillType == SkillType.SUB) {
            // 전체를 하나의 배경 패널로 (고정 높이 설정)
            val panel = Table().apply {
                background = ResourceManager.getSkillSubDecPanel()
                setSize(320f, 200f)
                pad(16f)
                top()
            }

            if (selectedMainSkillForSubSkill != null && selectedSubSkill != null) {
                val mainSkill = selectedMainSkillForSubSkill!!
                val mainSkillRank = skillInventory.getRankByType(mainSkill)
                val subSkill = selectedSubSkill!!

                val equippedSubSkills = getCompatibleSubSkills(mainSkill)
                val equippedTypes = equippedSubSkills.mapNotNull { it["type"] as? String }
                val maxSlots = SubSkillSlots.getMaxSubSkillSlots(mainSkillRank)
                val isEquipped = subSkill.name in equippedTypes
                val canEquip = !isEquipped && equippedSubSkills.size < maxSlots
                        && (subSkill.requiredTags.isEmpty() || subSkill.requiredTags.any { it in SkillRegistry.createSkill(mainSkill).tags })

                // 보조스킬 이름
                val nameTable = Table()
                val nameLabel = Label(subSkill.displayName, skin.get("label-default-bold", Label.LabelStyle::class.java)).apply {
                    color = TEXT_PRIMARY
                }

                // 보조스킬 인벤토리에서 경험치 정보 가져오기
                val subSkillTypeName = subSkill.name
                val inventoryData = saveData.subSkillInventory[subSkillTypeName]
                val currentExp = (inventoryData?.get("exp") as? Number)?.toInt() ?: 0
                val inventoryLevel = (inventoryData?.get("level") as? Number)?.toInt() ?: 1
                val requiredExp = SubSkillLevelSystem.getRequiredExp(inventoryLevel)

                val expLabel = Label("$currentExp/$requiredExp", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = TEXT_SECONDARY
                }

                nameTable.add(nameLabel).left()
                nameTable.add(expLabel).left().padLeft(8f)

                // 태그 정보
                val tagsText = if (subSkill.requiredTags.isNotEmpty()) {
                    subSkill.requiredTags.joinToString(", ") { it.displayName }
                } else {
                    "모든 스킬에 적용 가능"
                }
                val tagsLabel = Label(tagsText, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = ACCENT
                }
                // 스킬 설명 (장착된 경우 실제 레벨과 값 표시, 아니면 레벨별 범위 표시)
                val equippedSkillData = if (isEquipped) {
                    equippedSubSkills.find { (it["type"] as? String) == subSkill.name }
                } else null

                val equippedLevel = (equippedSkillData?.get("level") as? Number)?.toInt() ?: 1

                val description = if (isEquipped) {
                    // 장착된 경우 현재 레벨의 효과 표시
                    "Lv.$equippedLevel - ${subSkill.getFullDescription(equippedLevel)}"
                } else {
                    // 장착 안된 경우 1레벨 기준 설명
                    "Lv.1 - ${subSkill.getFullDescription(1)}"
                }

                val descLabel = Label(description, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = TEXT_SECONDARY
                    setWrap(true)
                }

                panel.add(nameTable).left().colspan(2).row()
                panel.add(tagsLabel).left().colspan(2).padTop(4f).row()
                panel.add(descLabel).left().colspan(2).expand().fill().padTop(8f).row()


                val slotInfoLabel = Label("보조스킬: ${equippedSubSkills.size}/$maxSlots",
                    skin.get("label-small", Label.LabelStyle::class.java)).apply {
                    color = TEXT_PRIMARY
                }
                panel.add(slotInfoLabel).left().bottom().padBottom(8f)

                // 장착/장착해제 버튼
                if (isEquipped) {
                    val unequipButton = RetroButtonV01.createIconButton(
                        defaultImage = ResourceManager.getUnequipBasePos(),
                        eventImage = ResourceManager.getUnequipEventPos(),
                    ) {
                        unequipSubSkillDirect(subSkill)
                    }
                    panel.add(unequipButton).width(80f).height(48f).right()
                } else {
                    val equipButton = RetroButtonV01.createIconButton(
                        defaultImage = ResourceManager.getEquipBasePos(),
                        eventImage = ResourceManager.getEquipEventPos(),
                        disabledImage = ResourceManager.getEquipBaseNag(),
                        isEnabled = canEquip
                    ) {
                        equipSubSkillDirect(subSkill)
                    }
                    panel.add(equipButton).width(80f).height(48f).right()
                }

            } else {
                // 메인 or 보조 선택 안되어있는경우
                val message = if (selectedMainSkillForSubSkill == null) "메인 스킬을 선택해 주세요" else "보조 스킬을 선택해 주세요."
                val placeholderLabel = Label(message, skin.get("label-default", Label.LabelStyle::class.java)).apply {
                    color = TEXT_SECONDARY
                }
                panel.add(placeholderLabel).center().expand().height(100f)
            }
            subSkillDescriptionContainer.add(panel)
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
        val subSkillTypeName = subSkillType.name
        val inventoryData = saveData.subSkillInventory[subSkillTypeName]
        if (inventoryData == null) {
            ToastMessage.show(stage, "보유하지 않은 보조스킬입니다.", skin)
            return
        }

        val inventoryLevel = (inventoryData["level"] as? Number)?.toInt() ?: 1

        val currentSubSkills = getCompatibleSubSkills(selectedMainSkillForSubSkill!!).toMutableList()
        val currentSubSkillTypes = currentSubSkills.mapNotNull { it["type"] as? String }

        // 같은 타입이 이미 장착되어 있는지 확인
        if (subSkillType.name in currentSubSkillTypes) {
            return
        }

        val maxSlots = SubSkillSlots.getMaxSubSkillSlots(skillInventory.getRankByType(selectedMainSkillForSubSkill!!))
        if (currentSubSkills.size < maxSlots) {
            // 인벤토리의 레벨로 장착
            currentSubSkills.add(mapOf(
                "type" to subSkillType.name,
                "level" to inventoryLevel
            ))
            saveData.equippedSubSkills[selectedMainSkillForSubSkill!!] = currentSubSkills
            SaveManager.save(saveData)
            updateSubSkillDescription()
            updateSkillListUI() // 체크 표시 업데이트
        }
    }

    private fun unequipSubSkillDirect(subSkillType: SubSkillType) {
        if (selectedMainSkillForSubSkill == null) return

        val currentSubSkills = getCompatibleSubSkills(selectedMainSkillForSubSkill!!).toMutableList()

        // 해당 타입의 보조스킬 찾아서 제거
        val removed = currentSubSkills.removeIf { (it["type"] as? String) == subSkillType.name }

        if (removed) {
            saveData.equippedSubSkills[selectedMainSkillForSubSkill!!] = currentSubSkills
            SaveManager.save(saveData)
            updateSubSkillDescription()
            updateSkillListUI() // 체크 표시 업데이트
        }
    }

    private fun createSkillRow(skillItem: SkillItem): Table {
        val row = Table().apply {
            background = ResourceManager.getUpgradeListPanel()
            pad(8f)
        }
        val skill = com.example.theorb.skills.SkillRegistry.createSkill(skillItem.skillType)
        // 스킬 아이콘
        val skillIcon = Table().apply {
            background = skill.baseIcon
        }
        val nameTable = Table().apply { left() }
        // 스킬 이름
        val skillName = skill.name
        val nameLabel = Label(skillName, skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        val skillRank = skillItem.rank.displayName
        val skillExp = skillItem.exp
        val rankLabel = Label("Rank:$skillRank / Exp:$skillExp", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = TEXT_SECONDARY
        }
        nameTable.add(nameLabel).left()
        nameTable.add(rankLabel).padLeft(4f).left()

        val skillDescription = skill.getDescription()
        val descriptionLabel = Label(skillDescription, skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = TEXT_SECONDARY
            setWrap(true)
        }

        val rightSection = Table().apply {
            top()
        }
        rightSection.add(nameTable).left().row()
        rightSection.add(descriptionLabel).expandX().fillX().left()

        val isEquipped = saveData.equippedSkills.contains(skillItem.skillType)
        val equipButton = RetroButtonV01.createIconButton(
            defaultImage = if (isEquipped) ResourceManager.getUnequipBasePos() else ResourceManager.getEquipBasePos(),
            eventImage = if (isEquipped) ResourceManager.getUnequipEventPos() else ResourceManager.getEquipEventPos(),

        ) {
            if (isEquipped) {
                // 장착되어 있는 스킬 클릭 → 즉시 장착 해제
                val slotIndex = saveData.equippedSkills.indexOf(skillItem.skillType)
                unequipSkill(slotIndex)
            } else {
                // 장착되지 않은 스킬 클릭 → 즉시 장착
                equipSkill(skillItem.skillType)
            }
        }

        row.add(skillIcon).left()
        row.add(rightSection).padLeft(8f).expandX().fillX()
        row.add(equipButton).width(80f).right()

        return row
    }

    private fun equipSkill(skillType: String) {
        // 빈 슬롯 찾기
        val emptySlotIndex = findEmptySkillSlot()
        if (emptySlotIndex == -1) {
            // TODO: "슬롯이 가득참" 메시지 표시
            return
        }

        // 이미 장착되어 있는지 확인
        if (saveData.equippedSkills.contains(skillType)) {
            // TODO: "이미 장착됨" 메시지 표시
            return
        }

        // 스킬 장착
        if (emptySlotIndex >= saveData.equippedSkills.size) {
            saveData.equippedSkills.add(skillType)
        } else {
            saveData.equippedSkills[emptySlotIndex] = skillType
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
