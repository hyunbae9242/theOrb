package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.example.theorb.balance.EnemyType
import com.example.theorb.data.SaveManager
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.EnemyFactory
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile
import com.example.theorb.skills.SkillRegistry
import com.example.theorb.ui.DamageText
import com.example.theorb.ui.RewardText
import com.example.theorb.ui.RewardType
import com.example.theorb.ui.InGameStatusPanel
import com.example.theorb.modal.ModalDialog
import com.example.theorb.modal.PauseModal
import com.example.theorb.ui.ToastMessage
import com.example.theorb.modal.GameResultModal
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.LevelUpManager
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber
import com.example.theorb.data.OrbRegistry
import com.example.theorb.modal.LevelUpSelectionModal
import com.example.theorb.skills.SkillInventory
import com.example.theorb.util.OrbManager
import com.example.theorb.skills.SubSkillInventoryItem
import com.example.theorb.skills.SubSkillType
import com.example.theorb.skills.SkillRank
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.stages.StageManager
import com.example.theorb.stages.StageData

class GameScreen : BaseScreen() {
    private val shape = ShapeRenderer()
    private val batch = SpriteBatch()

    // 레이아웃 비율 (퍼센트 기반) - 더 많은 여유공간
    private val topUIHeightRatio = 0.15f // 15% - 상단 UI
    private val gameAreaHeightRatio = 0.50f // 50% - 게임 영역
    private val upgradeUIHeightRatio = 0.35f // 35% - 하단 업그레이드 UI

    private lateinit var player: Player
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private val effects = mutableListOf<Effect>()
    private val damageTexts = mutableListOf<DamageText>()
    private val rewardTexts = mutableListOf<RewardText>()

    // UI 관련
    private val uiStage = Stage(viewport)
    private lateinit var goldLabel: Label
    private lateinit var orbsLabel: Label
    private lateinit var timerLabel: Label
    private lateinit var bossHealthBar: Table
    private lateinit var bossHealthBarBackground: Table
    private lateinit var bossHealthBarFill: Table
    private lateinit var bossNameLabel: Label
    private var currentBoss: Enemy? = null
    private lateinit var speedButton: ImageButton

    // 웨이브 진행도 바
    private lateinit var waveProgressBarBackground: Table
    private lateinit var waveProgressBarFill: Table
    private lateinit var waveProgressContainer: Table

    private var spawnTimer = 0f
    private var bossSpawnTimer = 60f // 1분마다 보스 스폰
    private var gameTimer = 0f
    private val maxGameTime = 300f // 5분 (초)
    private var isPaused = false
    private var isGameOver = false
    private var isVictory = false
    private var animationTime = 0f

    // 웨이브 시스템 (20초 사이클: 15초 스폰 + 5초 휴식)
    private var waveCycleTimer = 0f
    private val waveCycleDuration = 20f // 웨이브 사이클 총 시간
    private val waveSpawnDuration = 15f // 스폰 활성 시간
    private val waveRestDuration = 5f // 휴식 시간
    private var currentWave = 1 // 현재 웨이브 (1부터 시작)
    private var waveEnemyKillCount = 0 // 현재 웨이브에서 처치한 적 수
    private var waveEnemySpawnCount = 0 // 현재 웨이브에서 실제로 스폰된 적 수
    private var waveExpectedSpawnCount = 0 // 현재 웨이브에서 스폰될 예정인 총 적 수

    // 스테이지 시스템
    private lateinit var currentStageData: StageData

    private lateinit var skillInventory: SkillInventory
    private lateinit var pauseModal: PauseModal
    private lateinit var modalDialog: ModalDialog
    private lateinit var statusPanel: InGameStatusPanel
    private lateinit var levelUpSelectionModal: LevelUpSelectionModal
    private lateinit var gameResultModal: GameResultModal

    // 게임 통계 추적
    private var initialGold = 0
    private var initialOrbs = 0
    private val skillDamageStats = mutableMapOf<String, Long>()
    private val acquiredActiveSkills = mutableListOf<String>() // 획득한 액티브 스킬
    private val acquiredSubSkills = mutableListOf<String>() // 획득한 보조스킬

    // 흡혈 누적 시스템
    private var pendingLifestealHealing = 0f // 아직 적용되지 않은 회복량

    override fun show() {
        initSharedResources()

        // 현재 스테이지 데이터 로드
        currentStageData = StageManager.getStage(gameObject.saveData.currentStage)
            ?: StageManager.getStage(1)!! // 기본값: 스테이지 1

        // 게임 시작 시 인게임 진행도 초기화
        resetInGameProgress()

        // 첫 웨이브 예상 스폰 수 계산
        calculateExpectedSpawnCount()

        // 게임 시작 시 초기 통계 저장
        initialGold = gameObject.saveData.gold
        initialOrbs = gameObject.saveData.orbs
        skillDamageStats.clear()


        skillInventory = SkillInventory()
        skillInventory.fromSaveData(gameObject.saveData.skillInventory)

        // 배경 설정 (공통 배경 렌더러 사용)
        val backgroundRenderer = getSharedBackgroundRenderer()
        backgroundRenderer.initForSpriteBatch(viewport.worldWidth, viewport.worldHeight)

        Gdx.input.inputProcessor = uiStage
        pauseModal = PauseModal(uiStage, skin)
        modalDialog = ModalDialog(uiStage, skin)
        gameResultModal = GameResultModal(uiStage, skin)
        statusPanel = InGameStatusPanel(uiStage, skin, gameObject.saveData)
        levelUpSelectionModal = LevelUpSelectionModal(uiStage, skin, gameObject.saveData)
        setupUi()
        loadSaveData()
    }

    private fun setupUi() {
        val mainLayout = Table().apply {
            setSize(virtualWidth, virtualHeight)
            setPosition(0f, 0f)
        }
        uiStage.addActor(mainLayout)

        // 상단 UI 영역
        val topUIContainer = Table().apply {
            top()
            pad(SCREEN_PADDING)
        }

        // 게임 화면 영역 (빈 공간)
        val gameArea = Table()

        // 하단 업그레이드 패널 영역 480 x 280
        val upgradeAreaHeight = 280f
        val upgradeContainer = statusPanel.createUI(upgradeAreaHeight)

        // 좌측: 골드, 오브, 경험치 정보
        goldLabel = Label("골드: ${formatNumber(gameObject.saveData.gold)}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(1f, 0.84f, 0f, 1f) // 골드 색상
        }
        orbsLabel = Label("오브: ${gameObject.saveData.orbs}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(0.5f, 1f, 1f, 1f) // 시안 색상 (오브)
        }

        val topLeft = Table().apply {
            add(goldLabel).left().row()
            add(orbsLabel).left().row()
        }

        // 설정 버튼
        val pauseButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getPauseBasePos(),
            eventImage = ResourceManager.getPauseEventPos()
        ) {
            pauseGame()
            showSettingsModal()
        }

        // 배속 버튼 - Retro 스타일 (RetroButton 유틸리티 사용)
        speedButton = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getSpeedBasePos(gameObject.saveData.currentSpeedMultiplier),
            eventImage = ResourceManager.getSpeedEventPos(gameObject.saveData.currentSpeedMultiplier)
        ) {
            toggleGameSpeed()
        }

        val topRight = Table().apply {
            top()
            add(pauseButton).size(48f).right().padBottom(8f).row()
            add(speedButton).size(48f).right()
        }

        topUIContainer.add(topLeft).left().top()
        topUIContainer.add(Table()).expandX() // 중간 공간 채우기
        topUIContainer.add(topRight).right().top()

        // 메인 레이아웃 구성
        mainLayout.add(topUIContainer).expandX().fillX().height(120f).top().row()
        mainLayout.add(gameArea).expandY().fillY().row()
        mainLayout.add(upgradeContainer).height(280f).bottom()

        // 중앙 상단에 웨이브 진행도 바 추가
        setupWaveProgressBar()

        // 보스 체력바 설정 (초기에는 숨겨짐)
        setupBossHealthBar()
    }

    private fun setupWaveProgressBar() {
        // 웨이브 라벨
        timerLabel = Label("WAVE 1/20", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        // 진행도 바 배경 (검은색)
        waveProgressBarBackground = Table().apply {
            background = BaseScreen.skin.newDrawable("white", Color(0.1f, 0.1f, 0.1f, 1f))
        }

        // 진행도 바 채우기 (밝은 색)
        waveProgressBarFill = Table().apply {
            background = BaseScreen.skin.newDrawable("white", Color(0.3f, 0.8f, 1f, 1f))
        }

        // 진행도 바 너비
        val progressBarWidth = 240f
        val progressBarHeight = 8f

        // 진행도 바 컨테이너 (배경 위에 fill을 겹쳐서 표시)
        val progressBarContainer = Table().apply {
            add(waveProgressBarBackground).size(progressBarWidth, progressBarHeight)
        }

        // 전체 컨테이너
        waveProgressContainer = Table().apply {
            background = BaseScreen.skin.newDrawable("white", PANEL_BG)
            pad(12f, 16f, 12f, 16f)
            add(timerLabel).center().padBottom(6f).row()
            add(progressBarContainer).center()
        }

        uiStage.addActor(waveProgressContainer.apply {
            pack()
            setPosition(
                (uiStage.viewport.worldWidth - width) / 2f,
                uiStage.viewport.worldHeight - height - 12f
            )
        })

        // Fill 액터를 별도로 추가 (배경 위에 겹치기)
        uiStage.addActor(waveProgressBarFill.apply {
            setSize(0f, progressBarHeight)
            // 위치는 updateWaveProgressBar에서 업데이트
        })
    }

    private fun setupBossHealthBar() {
        // 보스 이름 라벨 (볼드 폰트 사용)
        bossNameLabel = Label("", skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        // 체력바 배경 (더 밝은 회색)
        bossHealthBarBackground = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = Color(0.6f, 0.6f, 0.6f, 1f)
        }

        // 체력바 채우기 (빨간색)
        bossHealthBarFill = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = Color.RED
        }

        // 체력바 컨테이너 - 배경만 표시하고, 채우기는 별도로 관리
        // 뷰포트 넓이의 60%를 체력바 너비로 사용
        val healthBarWidth = viewport.worldWidth * 0.6f
        val healthBarContainer = Table().apply {
            add(bossHealthBarBackground).size(healthBarWidth, 20f)
            isVisible = false
        }

        bossHealthBar = healthBarContainer

        val bossContainer = Table().apply {
            add(bossNameLabel).center().padBottom(2f).row() // 아래 패딩 줄임
            add(bossHealthBar).center()
            pack()
            isVisible = false
        }

        uiStage.addActor(bossContainer)

        // 체력바 채우기를 별도로 추가 (배경 위에 겹쳐서 표시)
        uiStage.addActor(bossHealthBarFill.apply {
            isVisible = false
        })

        // 위치 설정 (타이머와 겹치지 않도록 더 아래로)
        bossContainer.setPosition(
            (uiStage.viewport.worldWidth - bossContainer.width) / 2f,
            uiStage.viewport.worldHeight - 120f
        )

        // 컨테이너도 참조로 저장
        bossHealthBar = bossContainer
    }

    private fun loadSaveData() {
        val saveData = gameObject.saveData
        val skills = saveData.equippedSkills.mapNotNull { skillType ->
            try {
                val rank = skillInventory.getRankByType(skillType)
                val skill = SkillRegistry.createSkill(skillType)
                skill.rank = rank

                // 보조스킬 로드
                val equippedSubSkillsData = saveData.equippedSubSkills[skillType] ?: emptyList()
                val subSkills = equippedSubSkillsData.mapNotNull { subSkillData ->
                    try {
                        val typeName = subSkillData["type"] as? String
                        val level = (subSkillData["level"] as? Number)?.toInt() ?: 1

                        val subSkillType = com.example.theorb.skills.SubSkillType.values()
                            .find { it.name == typeName }

                        if (subSkillType != null) {
                            com.example.theorb.skills.SubSkill(
                                id = "${skillType}_${typeName}_$level",
                                type = subSkillType,
                                level = level
                            )
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Gdx.app.log("GameScreen", "보조스킬 로드 실패: $subSkillData - ${e.message}")
                        null
                    }
                }
                skill.updateSubSkillEffects(subSkills)

                skill
            } catch (e: Exception) {
                Gdx.app.log("GameScreen", "스킬 로드 실패: $skillType - ${e.message}")
                null
            }
        }.toMutableList()

        // 스킬 로깅 (디버깅용)
        Gdx.app.log("GameScreen", "=== 로드된 스킬 확인 ===")
        skills.forEach { skill ->
            Gdx.app.log("GameScreen", "스킬: ${skill.name}, 등급: ${skill.rank.displayName}, 데미지 배율: ${skill.damageMul}")
            Gdx.app.log("GameScreen", "  - 보조스킬: ${skill.equippedSubSkills.size}개")
            Gdx.app.log("GameScreen", "  - 투사체 개수: ${skill.getProjectileCount()}")
        }

        // 오브 능력치 로깅 (디버깅용)
        Gdx.app.log("GameScreen", "=== 오브 능력치 확인 ===")
        Gdx.app.log("GameScreen", "선택된 오브: ${saveData.selectedOrb}")
        val selectedOrb = OrbRegistry.getOrbById(saveData.selectedOrb)
        selectedOrb?.let { orb ->
            Gdx.app.log("GameScreen", "오브 이름: ${orb.name}")
            Gdx.app.log("GameScreen", "오브 설명: ${orb.description}")
            orb.abilities.forEach { ability ->
                Gdx.app.log("GameScreen", "능력: ${ability.type} = ${ability.value}")
            }
        }
        OrbManager.logOrbEffects(saveData)

        // 플레이어 위치를 게임 영역 중앙으로 조정 (퍼센트 기반)
        player = Player(skills = skills, saveData = saveData).apply {
            val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
            val gameAreaHeight = viewport.worldHeight * (gameAreaHeightRatio + (topUIHeightRatio / 2))
            y = gameAreaStartY + (gameAreaHeight / 2f)
        }
    }

    override fun render(delta: Float) {
        // ======= Update =======
        if (!isPaused && !isVictory) {
            // 배속 적용
            val speedMultiplier = gameObject.saveData.currentSpeedMultiplier
            val adjustedDelta = delta * speedMultiplier

            // 에너지 실드 자동 회복
            updateEnergyShieldRegen(adjustedDelta)

            // 게임 타이머 업데이트 (배속 적용)
            gameTimer += adjustedDelta

            // 웨이브 완료 체크: 마지막 웨이브에서 모든 적 처치 시 게임 종료
            if (currentWave > currentStageData.requiredWaves && enemies.isEmpty()) {
                isGameOver = true
            }

            // 적 스폰 (마지막 웨이브까지만)
            if (currentWave <= currentStageData.requiredWaves) {
                // 웨이브 사이클 타이머 업데이트
                waveCycleTimer += adjustedDelta
                if (waveCycleTimer >= waveCycleDuration) {
                    waveCycleTimer = 0f // 사이클 리셋
                    currentWave++ // 다음 웨이브로
                    waveEnemyKillCount = 0 // 킬 카운트 리셋
                    waveEnemySpawnCount = 0 // 스폰 카운트 리셋
                    calculateExpectedSpawnCount() // 다음 웨이브 예상 스폰 수 계산
                }

                // 스폰 활성 구간인지 확인 (15초 스폰, 5초 휴식)
                val isSpawnActive = waveCycleTimer < waveSpawnDuration

                if (isSpawnActive) {
                    spawnTimer -= adjustedDelta

                    if (spawnTimer <= 0f) {
                        val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
                        val gameAreaHeight = viewport.worldHeight * gameAreaHeightRatio

                        // 웨이브별 스폰 밀도 및 개수 조정
                        val (spawnInterval, spawnCount) = when {
                            currentWave <= 2 -> Pair(0.8f, 1)      // 1~2웨이브: 0.8초마다 1마리 (초당 1.25마리)
                            currentWave <= 5 -> Pair(0.6f, 2)      // 3~5웨이브: 0.6초마다 2마리 (초당 3.3마리)
                            currentWave <= 10 -> Pair(0.4f, 3)     // 6~10웨이브: 0.4초마다 3마리 (초당 7.5마리)
                            currentWave <= 15 -> Pair(0.3f, 4)     // 11~15웨이브: 0.3초마다 4마리 (초당 13마리)
                            else -> Pair(0.25f, 5)                 // 16~20웨이브: 0.25초마다 5마리 (초당 20마리)
                        }

                        repeat(spawnCount) {
                            enemies.add(EnemyFactory.spawnRandom(
                                width = viewport.worldWidth,
                                gameAreaHeight = gameAreaHeight,
                                gameAreaStartY = gameAreaStartY,
                                gameTimeSeconds = gameTimer,
                                currentWave = currentWave,
                                stageHpMultiplier = currentStageData.enemyHpMultiplier,
                                stageDamageMultiplier = currentStageData.enemyDamageMultiplier
                            ))
                            waveEnemySpawnCount++ // 스폰 카운트 증가
                        }

                        spawnTimer = spawnInterval
                    }
                }

                // 보스 스폰 (5의 배수 웨이브에만: 5, 10, 15, 20)
                // 웨이브가 시작되고 1초 후에 보스 스폰
                if (currentWave % 5 == 0 && waveCycleTimer >= 1f && waveCycleTimer < 1.1f && currentBoss == null) {
                    val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
                    val gameAreaHeight = viewport.worldHeight * gameAreaHeightRatio
                    val boss = EnemyFactory.spawnBoss(
                        width = viewport.worldWidth,
                        gameAreaHeight = gameAreaHeight,
                        gameAreaStartY = gameAreaStartY,
                        gameTimeSeconds = gameTimer,
                        currentWave = currentWave,
                        stageHpMultiplier = currentStageData.enemyHpMultiplier,
                        stageDamageMultiplier = currentStageData.enemyDamageMultiplier
                    )
                    enemies.add(boss)
                    currentBoss = boss
                    waveEnemySpawnCount++ // 보스도 카운트에 포함
                    showBossHealthBar()
                }
            }

            player.update(
                delta = adjustedDelta,
                enemies = enemies,
                projectiles = projectiles,
                effects = effects,
                onDamage = { damage, x, y, skillName ->
                    addDamageText(damage, x, y)
                    // 스킬별 데미지 통계 추적 (실제 스킬 이름 사용)
                    trackSkillDamage(skillName, damage.toLong())
                    // 흡혈 적용
                    applyLifesteal(damage)
                },
                onSkillCast = {
                    // 스킬 시전 시 에너지 실드 회복
                    onSkillCast()
                }
            )

            enemies.forEach { it.update(adjustedDelta, player) }

            // 적과 플레이어 충돌 체크 (피격 로직)
            checkEnemyPlayerCollision()

            // 투사체 업데이트 시 ConcurrentModificationException 방지
            // 새로 생성될 투사체를 임시 리스트에 모아두고 순회 후 추가
            val newProjectiles = mutableListOf<Projectile>()
            projectiles.forEach { it.update(adjustedDelta, enemies, newProjectiles, effects) }
            projectiles.addAll(newProjectiles)

            effects.forEach { it.update(adjustedDelta) }

            // 데미지 텍스트 업데이트
            damageTexts.removeAll { !it.update(adjustedDelta) }

            // 보상 텍스트 업데이트
            rewardTexts.removeAll { !it.update(adjustedDelta) }
        }

        // 애니메이션 시간은 일시정지와 관계없이 계속 진행
        animationTime += delta

        // 승리 조건 체크 (10분 완료 후 모든 적 처치)
        if (isGameOver && !isVictory && enemies.isEmpty()) {
            isVictory = true
            showVictoryScreen()
        }

        // 적 사망 체크 → 이펙트 추가
        val deadEnemies = enemies.filter { it.isDead() }
        deadEnemies.forEach { enemy ->
            effects.add(
                Effect(
                    EffectManager.load(enemy.getDeathEffectType()),
                    enemy.x, enemy.y, scale = 1.2f
                )
            )

            // 웨이브 킬 카운트 증가 (예상 스폰 수를 초과하지 않도록)
            if (waveEnemyKillCount < waveExpectedSpawnCount) {
                waveEnemyKillCount++
            }

            // 골드/경험치 획득 (스테이지 배율 적용)
            val baseReward = enemy.rewardGold
            val goldReward = (baseReward * currentStageData.goldMultiplier).toInt()
            val expReward = (baseReward * 5 * currentStageData.expMultiplier).toInt() // 기본 경험치는 골드의 5배

            // 골드 드롭 확률 (적 타입별)
            val goldDropChance = when (enemy.type) {
                EnemyType.NORMAL -> 0.10f
                EnemyType.SPEED -> 0.15f
                EnemyType.TANK -> 0.15f
                EnemyType.BOSS -> 0.50f
            }

            if (Math.random() < goldDropChance) {
                gameObject.saveData.gold += goldReward
                addRewardText(goldReward, 0f, 0f, RewardType.GOLD)
            }

            // 경험치는 항상 획득
            val leveledUp = LevelUpManager.addExp(gameObject.saveData, expReward)
            addRewardText(expReward, 0f, 0f, RewardType.EXP)

            // 경험치 바 업데이트
            statusPanel.refreshUI()

            // 레벨업 발생 시 선택지 모달 표시
            if (leveledUp) {
                showLevelUpModal()
            }

            // 보스 처치 시 오브 획득 및 액티브 스킬 드롭 (스테이지 배율 적용)
            if (enemy.type == EnemyType.BOSS) {
                val orbReward = (1 * currentStageData.orbMultiplier).toInt().coerceAtLeast(1)

                gameObject.saveData.orbs += orbReward
                addRewardText(orbReward, 0f, 0f, RewardType.ORB)

                // 액티브 스킬 20% 확률 드롭 (5마리 보스 중 평균 1개)
                if (Math.random() < 0.2) {
                    dropRandomActiveSkill()
                }

                // 보스가 죽으면 체력바 숨기기 및 현재 보스 참조 제거
                if (enemy == currentBoss) {
                    hideBossHealthBar()
                    currentBoss = null
                }
            } else {
                // 일반 적 처치 시 드롭
                // 보조스킬 0.11% 확률 (2,184마리 중 평균 2.4개)
                if (Math.random() < 0.0011) {
                    dropRandomSubSkill()
                }
                // 액티브 스킬 0.05% 확률 (2,184마리 중 평균 1.09개)
                if (Math.random() < 0.0005) {
                    dropRandomActiveSkill()
                }
            }

            SaveManager.save(gameObject.saveData)
        }

        // 보스 체력바 업데이트
        updateBossHealthBar()

        // 정리
        enemies.removeAll { it.isDead() }
        projectiles.removeAll { !it.alive }
        effects.removeAll { it.finished }


        // ======= Draw =======
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined

        // --- 이펙트와 플레이어 (SpriteBatch) ---
        batch.begin()

        // 배경 그리기 (맨 먼저)
        getSharedBackgroundRenderer().drawWithSpriteBatch(batch)

        batch.end()

        // 사정거리 표시 (배경 위에 그리기)
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = Color.GRAY
        val effectiveRange = UpgradeManager.getEffectiveRange(gameObject.saveData, player.baseRange)
        shape.circle(player.x, player.y, effectiveRange)
        shape.end()

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.end()

        // --- 게임 요소들 (SpriteBatch) ---
        batch.begin()

        // Player (선택된 오브 이미지)
        val selectedOrbData = OrbRegistry.getOrbById(gameObject.saveData.selectedOrb)
            ?: OrbRegistry.getOrbById("base")!!
        val orbDrawable = selectedOrbData.getDrawable()
        val orbSize = 30f
        orbDrawable.draw(batch, player.x - orbSize/2, player.y - orbSize/2, orbSize, orbSize)

        // All enemies (스프라이트로 렌더링)
        enemies.forEach { enemy ->
            val animation = EffectManager.load(enemy.getSpriteEffectType())
            val currentFrame = animation.getKeyFrame(enemy.animationTime, true) // 각 적의 개별 애니메이션 시간
            val enemySize = enemy.type.radius * 2f // 타입별 크기 적용
            batch.draw(currentFrame, enemy.x - enemySize/2, enemy.y - enemySize/2, enemySize, enemySize)
        }

        projectiles.forEach { it.draw(batch) }
        effects.forEach { it.draw(batch) }

        // 데미지 텍스트 렌더링
        damageTexts.forEach { it.draw(batch, fontSm) }

        // 보상 텍스트 렌더링
        rewardTexts.forEach { it.draw(batch, fontSm) }

        batch.end()

        // --- UI ---
        goldLabel.setText("골드: ${formatNumber(gameObject.saveData.gold)}")
        orbsLabel.setText("오브: ${gameObject.saveData.orbs}")
        statusPanel.refreshUI()

        // 보스 체력바 실시간 업데이트
        updateBossHealthBar()

        // 웨이브 진행도 표시 및 진행도 바 업데이트
        updateWaveProgressBar()

        uiStage.act(delta)
        uiStage.draw()
    }

    private fun pauseGame() {
        isPaused = true
    }

    private fun resumeGame() {
        isPaused = false
    }

    private fun showSettingsModal() {
        pauseModal.show(
            onHome = { showExitConfirmation() },
            onPlay = { resumeGame() }
        )
    }

    private fun showExitConfirmation() {
        modalDialog.show(
            title = "게임 나가기",
            message = "게임을 나가시겠습니까?",
            onConfirm = {
                gameObject.setScreen(HomeScreen(gameObject))
            },
            onCancel = { showSettingsModal() } // 설정 모달로 돌아가기
        )
    }


    private fun restartGame() {
        // 게임 상태 초기화
        enemies.clear()
        projectiles.clear()
        effects.clear()
        damageTexts.clear()
        spawnTimer = 0f
        bossSpawnTimer = 60f
        gameTimer = 0f
        isGameOver = false
        isVictory = false
        hideBossHealthBar()

        // 통계 초기화
        initialGold = gameObject.saveData.gold
        initialOrbs = gameObject.saveData.orbs
        skillDamageStats.clear()

        // 흡혈 누적량 초기화
        pendingLifestealHealing = 0f

        // 재시작 시 인게임 진행도 초기화
        resetInGameProgress()

        // 플레이어 재생성
        loadSaveData()

        // 모든 모달 닫기
        pauseModal.hide()
        gameResultModal.hide()

        // 게임 재개
        resumeGame()

        // 배속 버튼 텍스트 업데이트
        updateSpeedButton()
    }

    fun addDamageText(damage: Int, x: Float, y: Float) {
        damageTexts.add(DamageText(damage, x, y))
    }

    private fun addRewardText(amount: Int, x: Float, y: Float, type: RewardType) {
        // 업그레이드 패널 바로 위쪽, 우측에 로그 쌓이게 표시
        val upgradeAreaY = viewport.worldHeight * upgradeUIHeightRatio + 10f
        val logX = viewport.worldWidth - 10f // 우측에서 10px 여백

        // 기존 텍스트들을 위로 밀어올림
        rewardTexts.forEach { it.moveUp(22f) }

        val logY = upgradeAreaY + 10f // 업그레이드 패널에서 10px 위
        rewardTexts.add(RewardText(amount, logX, logY, type, logY))
    }

    private fun trackSkillDamage(skillName: String, damage: Long) {
        skillDamageStats[skillName] = skillDamageStats.getOrDefault(skillName, 0L) + damage
    }

    private fun showVictoryScreen() {
        isPaused = true
        val goldEarned = gameObject.saveData.gold - initialGold
        val orbsEarned = gameObject.saveData.orbs - initialOrbs

        // 스테이지 클리어 처리 및 다음 스테이지 해금
        val currentStage = gameObject.saveData.currentStage

        // 최고 클리어 스테이지 업데이트
        if (currentStage > gameObject.saveData.highestClearedStage) {
            gameObject.saveData.highestClearedStage = currentStage
        }

        // 다음 스테이지 해금
        val nextStageId = currentStage + 1
        if (nextStageId <= StageManager.getMaxStageCount()) {
            if (nextStageId > gameObject.saveData.unlockedStages) {
                gameObject.saveData.unlockedStages = nextStageId
            }
        }

        SaveManager.save(gameObject.saveData)

        gameResultModal.show(
            title = "Victory!",
            goldEarned = goldEarned,
            orbsEarned = orbsEarned,
            skillStats = skillDamageStats,
            acquiredActiveSkills = acquiredActiveSkills,
            acquiredSubSkills = acquiredSubSkills,
            onHome = {
                gameResultModal.hide()
                gameObject.setScreen(HomeScreen(gameObject))
            },
            onRestart = {
                gameResultModal.hide()
                restartGame()
            }
        )
    }

    private fun showBossHealthBar() {
        currentBoss?.let { boss ->
            bossNameLabel.setText("BOSS")
            bossHealthBar.isVisible = true
            bossHealthBarFill.isVisible = true
            updateBossHealthBarPosition()
        }
    }

    private fun hideBossHealthBar() {
        bossHealthBar.isVisible = false
        bossHealthBarFill.isVisible = false
        currentBoss = null
    }

    private fun updateBossHealthBarPosition() {
        // 체력바 채우기의 위치를 배경과 맞추기
        bossHealthBar.pack()
        val healthBarWidth = viewport.worldWidth * 0.6f
        val backgroundX = bossHealthBar.x + (bossHealthBar.width - healthBarWidth) / 2f
        val backgroundY = bossHealthBar.y - 25f // 라벨 아래에 위치
        bossHealthBarFill.setPosition(backgroundX, backgroundY)
    }

    private fun updateBossHealthBar() {
        currentBoss?.let { boss ->
            if (boss.isDead()) {
                hideBossHealthBar()
            } else {
                // 체력바 비율 계산 (현재 체력/최대 체력 기준으로 정확한 비율 계산)
                // HP가 음수일 수 있으므로 0 이상으로 클램프
                val currentHp = maxOf(0, boss.hp)
                val healthPercentage = currentHp.toFloat() / boss.maxHp.toFloat()

                // 체력바 너비 업데이트 (뷰포트 넓이의 60%가 최대 너비, 0~1 범위로 클램프)
                val maxHealthBarWidth = viewport.worldWidth * 0.6f
                val fillWidth = maxHealthBarWidth * healthPercentage.coerceIn(0f, 1f)
                bossHealthBarFill.setSize(fillWidth, 20f)

                // 위치도 다시 업데이트
                updateBossHealthBarPosition()
            }
        }
    }

    private fun calculateExpectedSpawnCount() {
        // 웨이브별 스폰 간격 결정
        val (spawnInterval, spawnCount) = when {
            currentWave <= 2 -> Pair(0.8f, 1)
            currentWave <= 5 -> Pair(0.6f, 2)
            currentWave <= 10 -> Pair(0.4f, 3)
            currentWave <= 15 -> Pair(0.3f, 4)
            else -> Pair(0.25f, 5)
        }

        // 15초 동안 스폰되는 총 적 수 계산
        val spawnTimes = (waveSpawnDuration / spawnInterval).toInt()
        waveExpectedSpawnCount = spawnTimes * spawnCount

        // 5의 배수 웨이브면 보스 1마리 추가
        if (currentWave % 5 == 0) {
            waveExpectedSpawnCount += 1
        }

        println("[Wave $currentWave] Expected spawn: $waveExpectedSpawnCount (interval: $spawnInterval, count: $spawnCount, times: $spawnTimes)")
    }

    private fun updateWaveProgressBar() {
        // 웨이브 라벨 업데이트
        timerLabel.setText("WAVE $currentWave/${currentStageData.requiredWaves}")

        // 진행도 계산 (예상 스폰 수 기준)
        val progress = if (waveExpectedSpawnCount > 0) {
            waveEnemyKillCount.toFloat() / waveExpectedSpawnCount.toFloat()
        } else {
            0f
        }

        // 진행도 바 너비
        val progressBarWidth = 240f
        val progressBarHeight = 8f
        val fillWidth = (progressBarWidth * progress).coerceIn(0f, progressBarWidth)

        // 진행도 바 위치 계산 (배경 바와 같은 위치)
        waveProgressContainer.pack()
        val containerX = (uiStage.viewport.worldWidth - waveProgressContainer.width) / 2f
        val containerY = uiStage.viewport.worldHeight - waveProgressContainer.height - 12f

        // 진행도 바 배경의 위치 (컨테이너 내부 패딩 고려)
        val progressBarX = containerX + 16f // 좌 패딩
        val progressBarY = containerY + 12f // 하 패딩

        // Fill 위치 및 크기 업데이트
        waveProgressBarFill.setPosition(progressBarX, progressBarY)
        waveProgressBarFill.setSize(fillWidth, progressBarHeight)
    }

    private fun updateSpeedButton() {
        RetroButtonV01.updateIconButton(
            speedButton,
            true,
            defaultImage = ResourceManager.getSpeedBasePos(gameObject.saveData.currentSpeedMultiplier),
            eventImage = ResourceManager.getSpeedEventPos(gameObject.saveData.currentSpeedMultiplier),
        )
    }

    private fun toggleGameSpeed() {
        val saveData = gameObject.saveData
        when (saveData.currentSpeedMultiplier) {
            1.0f -> saveData.currentSpeedMultiplier = 2.0f
            2.0f -> {
                if (saveData.maxSpeedMultiplier >= 3.0f) {
                    saveData.currentSpeedMultiplier = 3.0f
                } else {
                    saveData.currentSpeedMultiplier = 1.0f
                }
            }
            3.0f -> saveData.currentSpeedMultiplier = 1.0f
            else -> saveData.currentSpeedMultiplier = 1.0f
        }
        updateSpeedButton()
        SaveManager.save(saveData)
    }

    // 3배속 해금 함수 (과금 시스템에서 호출)
    fun unlock3xSpeed() {
        gameObject.saveData.maxSpeedMultiplier = 3.0f
        SaveManager.save(gameObject.saveData)
    }

    // 게임 시작/재시작 시 인게임 진행도 초기화
    fun resetInGameProgress() {
        println("인게임 진행도 초기화 시작")
        println("  초기화 전 - 레벨: ${gameObject.saveData.inGameLevel}, 경험치: ${gameObject.saveData.inGameExp}")

        gameObject.saveData.inGameLevel = 1
        gameObject.saveData.inGameExp = 0
        gameObject.saveData.selectedLevelUpOptions.clear()

        // 리롤 횟수 충전
        LevelUpManager.rechargeRerolls(gameObject.saveData)
        println("  리롤 횟수 충전: ${gameObject.saveData.currentRerollCount}/${gameObject.saveData.maxRerollCount}")

        // 희귀도 보너스 적용
        UpgradeManager.applyRarityBonusUpgrade(gameObject.saveData)
        println("  희귀도 확률: 유니크 ${gameObject.saveData.tierChanceUnique}%, 레어 ${gameObject.saveData.tierChanceRare}%, 노멀 ${gameObject.saveData.tierChanceNormal}%")

        // HP 초기화
        initializePlayerStats()

        SaveManager.save(gameObject.saveData)

        println("  초기화 완료 - 레벨: ${gameObject.saveData.inGameLevel}, 경험치: ${gameObject.saveData.inGameExp}")
        println("  HP 초기화: ${gameObject.saveData.currentHp}/${gameObject.saveData.maxHp}")
    }

    /**
     * 플레이어 스탯 초기화 (HP, 에너지 실드)
     */
    private fun initializePlayerStats() {
        // 최대 HP 계산 및 설정
        gameObject.saveData.maxHp = com.example.theorb.calculation.PlayerStatsCalculator.calculateMaxHp(gameObject.saveData)
        gameObject.saveData.currentHp = gameObject.saveData.maxHp

        // 에너지 실드 초기화 (최대값으로 시작)
        gameObject.saveData.currentEnergyShield = gameObject.saveData.maxEnergyShield
    }

    /**
     * 에너지 실드 자동 회복
     */
    private fun updateEnergyShieldRegen(delta: Float) {
        val regenAmount = gameObject.saveData.energyShieldRegenRate * delta
        gameObject.saveData.currentEnergyShield = (gameObject.saveData.currentEnergyShield + regenAmount.toInt())
            .coerceAtMost(gameObject.saveData.maxEnergyShield)
    }

    /**
     * 스킬 시전 시 에너지 실드 획득 (인게임 레벨업 선택지)
     */
    fun onSkillCast() {
        val esdPerCast = com.example.theorb.calculation.PlayerStatsCalculator.getEnergyShieldPerCast(gameObject.saveData)
        if (esdPerCast > 0) {
            gameObject.saveData.currentEnergyShield = (gameObject.saveData.currentEnergyShield + esdPerCast)
                .coerceAtMost(gameObject.saveData.maxEnergyShield)
        }
    }

    /**
     * 플레이어가 데미지를 받을 때 처리 (방어력 → 방어율 → 에너지 실드 → HP 순서)
     */
    fun takeDamage(damage: Int) {
        // 1. 방어력으로 데미지 감소 (고정 수치)
        val armor = UpgradeManager.getArmor(gameObject.saveData)
        val damageAfterArmor = (damage - armor).coerceAtLeast(1) // 최소 1 데미지

        // 2. 방어율로 데미지 감소 (%)
        val armorPercentage = UpgradeManager.getArmorPercentage(gameObject.saveData)
        val damageReduction = 1f - (armorPercentage / 100f)
        var finalDamage = (damageAfterArmor * damageReduction).toInt().coerceAtLeast(1) // 최소 1 데미지

        var remainingDamage = finalDamage

        // 3. 에너지 실드부터 소모
        if (gameObject.saveData.currentEnergyShield > 0) {
            val esdDamage = remainingDamage.coerceAtMost(gameObject.saveData.currentEnergyShield)
            gameObject.saveData.currentEnergyShield -= esdDamage
            remainingDamage -= esdDamage
        }

        // 4. 남은 데미지는 HP에 적용
        if (remainingDamage > 0) {
            gameObject.saveData.currentHp = (gameObject.saveData.currentHp - remainingDamage).coerceAtLeast(0)

            // 사망 체크
            if (gameObject.saveData.currentHp <= 0) {
                onPlayerDeath()
            }
        }

        // UI 업데이트
        statusPanel.refreshUI()
    }

    /**
     * 플레이어 사망 처리
     */
    private fun onPlayerDeath() {
        println("플레이어 사망!")
        isGameOver = true
        showGameOverScreen()
    }

    /**
     * 게임오버 화면 표시
     */
    private fun showGameOverScreen() {
        isPaused = true
        val goldEarned = gameObject.saveData.gold - initialGold
        val orbsEarned = gameObject.saveData.orbs - initialOrbs

        gameResultModal.show(
            title = "Game Over!",
            goldEarned = goldEarned,
            orbsEarned = orbsEarned,
            skillStats = skillDamageStats,
            acquiredActiveSkills = acquiredActiveSkills,
            acquiredSubSkills = acquiredSubSkills,
            onHome = {
                gameResultModal.hide()
                gameObject.setScreen(HomeScreen(gameObject))
            },
            onRestart = {
                gameResultModal.hide()
                restartGame()
            }
        )
    }

    /**
     * 흡혈 적용 (데미지 입힐 때마다 호출)
     * 회복량이 0.1부터 누적되며, 1.0 이상이 되면 정수 부분만큼 체력에 추가
     */
    private fun applyLifesteal(damage: Int) {
        val lifestealRate = com.example.theorb.calculation.PlayerStatsCalculator.getLifestealRate(gameObject.saveData)

        if (lifestealRate > 0f) {
            // 회복량 계산 (최소 0.1)
            val rawHealAmount = (damage * lifestealRate).coerceAtLeast(0.1f)

            // 누적 회복량에 추가
            pendingLifestealHealing += rawHealAmount

            // 1.0 이상이 되면 정수 부분만큼 체력 회복
            if (pendingLifestealHealing >= 1.0f) {
                val healAmount = pendingLifestealHealing.toInt()
                val oldHp = gameObject.saveData.currentHp

                gameObject.saveData.currentHp = (gameObject.saveData.currentHp + healAmount)
                    .coerceAtMost(gameObject.saveData.maxHp)

                val actualHeal = gameObject.saveData.currentHp - oldHp

                // 적용된 만큼 누적량에서 차감
                pendingLifestealHealing -= healAmount.toFloat()

                if (actualHeal > 0) {
                    println("흡혈 발동! 데미지: $damage, 회복: $actualHeal (누적: ${String.format("%.1f", pendingLifestealHealing)}, ${(lifestealRate * 100).toInt()}%)")
                }
            }
        }
    }

    /**
     * 적과 플레이어의 충돌 체크 및 피격 처리
     */
    private fun checkEnemyPlayerCollision() {
        val playerCollisionRadius = 25f // 플레이어 충돌 반경

        enemies.forEach { enemy ->
            // 플레이어와 적의 거리 계산
            val dx = player.x - enemy.x
            val dy = player.y - enemy.y
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

            // 충돌 범위 내에 있고, 적이 공격 가능한 상태라면
            if (distance < playerCollisionRadius && enemy.canAttack()) {
                // 플레이어가 데미지를 받음
                takeDamage(enemy.contactDamage)

                // 적의 공격 쿨다운 리셋
                enemy.resetAttackCooldown()

                println("플레이어 피격! 데미지: ${enemy.contactDamage}, 남은 HP: ${gameObject.saveData.currentHp}, 남은 ESD: ${gameObject.saveData.currentEnergyShield}")
            }
        }
    }

    /**
     * 레벨업 모달 표시
     *
     * UI 커스터마이징을 위한 콜백 기반 인터페이스:
     * 1. 이 함수는 3개의 선택지를 생성하고 게임을 일시정지합니다
     * 2. 사용자가 선택지를 선택하면 onOptionSelected를 호출합니다
     * 3. 선택이 완료되면 게임을 재개합니다
     */
    private fun showLevelUpModal() {
        pauseGame() // 게임 일시정지
        val options = LevelUpManager.generateLevelUpOptions(gameObject.saveData)
        levelUpSelectionModal.show(
            options = options,
            onSelection = { selected ->
                onOptionSelected(selected)
            },
            onReroll = {
                // 리롤 시 새로운 선택지 생성하고 다시 모달 표시
                val newOptions = LevelUpManager.generateLevelUpOptions(gameObject.saveData)
                levelUpSelectionModal.show(
                    options = newOptions,
                    onSelection = { selected ->
                        onOptionSelected(selected)
                    },
                    onReroll = ::showLevelUpModal // 재귀적으로 다시 호출
                )
            }
        )
    }

    /**
     * 선택지 선택 시 호출되는 함수
     * UI에서 이 함수를 호출하도록 구현하면 됩니다
     */
    private fun onOptionSelected(option: com.example.theorb.balance.LevelUpOptionData) {
        LevelUpManager.applyOption(gameObject.saveData, option)

        // HP 관련 선택지를 선택한 경우 maxHp 재계산
        if (option.type.name.startsWith("HP_")) {
            val oldMaxHp = gameObject.saveData.maxHp
            val newMaxHp = com.example.theorb.calculation.PlayerStatsCalculator.calculateMaxHp(gameObject.saveData)
            val hpIncrease = newMaxHp - oldMaxHp

            gameObject.saveData.maxHp = newMaxHp
            gameObject.saveData.currentHp += hpIncrease // 증가량만큼 현재 체력도 증가

            println("HP 업그레이드: $oldMaxHp → $newMaxHp (현재 HP: ${gameObject.saveData.currentHp})")
        }

        SaveManager.save(gameObject.saveData)
        levelUpSelectionModal.hide()
        resumeGame()
    }

    /**
     * 보스 처치 시 랜덤 보조스킬 드롭 (30% 확률)
     */
    private fun dropRandomSubSkill() {
        // 랜덤 보조스킬 타입 선택
        val randomSubSkillType = SubSkillType.values().random()
        val subSkillTypeName = randomSubSkillType.name

        val saveData = gameObject.saveData

        // 경험치 획득량 결정 (90% 1개, 8% 2개, 2% 3개)
        val expAmount = when {
            Math.random() < 0.90 -> 1
            Math.random() < 0.80 -> 2 // 나머지 10% 중 80% = 전체의 8%
            else -> 3 // 나머지 2%
        }

        // 이미 보유한 보조스킬인지 확인
        val existingData = saveData.subSkillInventory[subSkillTypeName]

        if (existingData != null) {
            // 중복: 경험치 추가
            val currentLevel = (existingData["level"] as? Number)?.toInt() ?: 1
            val currentExp = (existingData["exp"] as? Number)?.toInt() ?: 0

            val item = SubSkillInventoryItem(randomSubSkillType, currentLevel, currentExp)
            val leveledUp = item.addExp(expAmount)

            // 업데이트된 정보 저장
            saveData.subSkillInventory[subSkillTypeName] = mapOf(
                "level" to item.level,
                "exp" to item.exp
            )

            if (leveledUp) {
                // 레벨업 시
                ToastMessage.show(
                    uiStage,
                    "${randomSubSkillType.displayName} 보조스킬이 Lv.${item.level}로 레벨업했습니다! (경험치 +$expAmount)",
                    skin,
                    duration = 2f
                )
            } else {
                // 경험치만 획득
                ToastMessage.show(
                    uiStage,
                    "${randomSubSkillType.displayName} 보조스킬 경험치 +$expAmount\n\r(${item.exp}/${item.getRequiredExpForNextLevel()})",
                    skin,
                    duration = 2f
                )
            }
        } else {
            // 신규 획득
            saveData.subSkillInventory[subSkillTypeName] = mapOf(
                "level" to 1,
                "exp" to 0
            )

            // 획득 목록에 추가
            acquiredSubSkills.add(randomSubSkillType.displayName)

            ToastMessage.show(
                uiStage,
                "${randomSubSkillType.displayName} 보조스킬을 획득했습니다!",
                skin,
                duration = 2f
            )
        }

        SaveManager.save(saveData)
        Gdx.app.log("GameScreen", "보조스킬 드롭: ${randomSubSkillType.displayName} (경험치 +$expAmount)")
    }

    private fun dropRandomActiveSkill() {
        // 사용 가능한 액티브 스킬 목록
        val availableSkills = listOf("LightningStrike", "Fireball", "IceLance", "DivineNova")

        val saveData = gameObject.saveData

        // 랜덤 스킬 선택
        val skillToDrop = availableSkills.random()

        // 경험치 획득량 결정 (90% 1개, 8% 2개, 2% 3개)
        val expAmount = when {
            Math.random() < 0.90 -> 1
            Math.random() < 0.80 -> 2 // 나머지 10% 중 80% = 전체의 8%
            else -> 3 // 나머지 2%
        }

        // SkillInventory를 통해 스킬 로드
        val skillInventory = SkillInventory()
        skillInventory.fromSaveData(saveData.skillInventory)

        // 기존 스킬 찾기
        val existingSkills = skillInventory.getSkillsByType(skillToDrop)
        val existingSkill = existingSkills.firstOrNull()

        val skillName = SkillRegistry.createSkill(skillToDrop).name

        if (existingSkill != null) {
            // 중복: 경험치 추가
            val oldRank = existingSkill.rank
            val oldExp = existingSkill.exp

            existingSkill.exp += expAmount
            existingSkill.updateRank()

            val rankChanged = existingSkill.rank != oldRank

            if (rankChanged) {
                ToastMessage.show(
                    uiStage,
                    "$skillName 스킬이 ${existingSkill.rank.displayName}등급으로 승급했습니다! (경험치 +$expAmount)",
                    skin,
                    duration = 2f
                )
            } else {
                ToastMessage.show(
                    uiStage,
                    "$skillName 스킬 경험치 +$expAmount\n\r(${existingSkill.exp}/${existingSkill.rank.upgradeRequirement})",
                    skin,
                    duration = 2f
                )
            }
        } else {
            // 신규 획득
            skillInventory.addSkill(skillToDrop, SkillRank.C, 0)

            // 획득 목록에 추가
            acquiredActiveSkills.add(skillName)

            ToastMessage.show(
                uiStage,
                "$skillName 스킬을 획득했습니다!",
                skin,
                duration = 2f
            )
        }

        // SaveData에 다시 저장
        saveData.skillInventory.clear()
        saveData.skillInventory.addAll(skillInventory.toSaveData())

        SaveManager.save(saveData)
        Gdx.app.log("GameScreen", "액티브 스킬 드롭: $skillToDrop (경험치 +$expAmount)")
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        uiStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        shape.dispose()
        batch.dispose()
        disposeSharedResources()
    }
}
