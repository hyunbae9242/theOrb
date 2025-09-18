package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.data.SaveManager
import com.example.theorb.effects.Effect
import com.example.theorb.effects.EffectManager
import com.example.theorb.balance.Balance
import com.example.theorb.balance.EnemyType
import com.example.theorb.upgrades.InGameUpgradeManager
import com.example.theorb.entities.Enemy
import com.example.theorb.entities.EnemyFactory
import com.example.theorb.entities.Player
import com.example.theorb.entities.Projectile
import com.example.theorb.skills.SkillRegistry
import com.example.theorb.ui.DamageText
import com.example.theorb.ui.InGameUpgradePanel
import com.example.theorb.ui.ModalDialog
import com.example.theorb.ui.SettingsModal
import com.example.theorb.util.ResourceManager

class GameScreen : BaseScreen() {
    private val shape = ShapeRenderer()
    private val batch = SpriteBatch()

    // 레이아웃 비율 (퍼센트 기반) - 더 많은 여유공간
    private val topUIHeightRatio = 0.15f // 15% - 상단 UI
    private val gameAreaHeightRatio = 0.60f // 60% - 게임 영역
    private val upgradeUIHeightRatio = 0.25f // 25% - 하단 업그레이드 UI

    private lateinit var player: Player
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private val effects = mutableListOf<Effect>()
    private val damageTexts = mutableListOf<DamageText>()

    // UI 관련
    private val uiStage = Stage(viewport)
    private lateinit var goldLabel: Label
    private lateinit var gemLabel: Label
    private lateinit var silverLabel: Label
    private lateinit var timerLabel: Label
    private lateinit var bossHealthBar: Table
    private lateinit var bossHealthBarBackground: Table
    private lateinit var bossHealthBarFill: Table
    private lateinit var bossNameLabel: Label
    private var currentBoss: Enemy? = null
    private lateinit var speedButton: TextButton

    private var spawnTimer = 0f
    private var bossSpawnTimer = 60f // 1분마다 보스 스폰
    private var gameTimer = 0f
    private val maxGameTime = 600f // 10분 (초)
    private var isPaused = false
    private var isGameOver = false
    private var animationTime = 0f
    private lateinit var settingsModal: SettingsModal
    private lateinit var modalDialog: ModalDialog
    private lateinit var upgradePanel: InGameUpgradePanel

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = uiStage
        settingsModal = SettingsModal(uiStage, skin)
        modalDialog = ModalDialog(uiStage, skin)
        upgradePanel = InGameUpgradePanel(gameObject.saveData)
        setupUi()
        loadSaveData()
    }

    private fun setupUi() {
        val mainLayout = Table().apply {
            setFillParent(true)
        }
        uiStage.addActor(mainLayout)

        // 상단 UI 영역
        val topUIContainer = Table()

        // 게임 화면 영역 (빈 공간)
        val gameArea = Table()

        // 하단 업그레이드 패널 영역
        val upgradeContainer = upgradePanel.createUI()

        // 좌측: 골드, 젬, 실버 정보
        goldLabel = Label("골드: ${gameObject.saveData.gold}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(1f, 0.84f, 0f, 1f) // 골드 색상
        }
        gemLabel = Label("젬: ${gameObject.saveData.gems}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(0.5f, 1f, 1f, 1f) // 시안 색상 (젬)
        }
        // 실버 표시 추가
        silverLabel = Label("실버: ${gameObject.saveData.silver}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = Color(0.8f, 0.8f, 0.9f, 1f) // 은색 계열
        }

        val topLeft = Table().apply {
            add(goldLabel).left().row()
            add(silverLabel).left().row()
            add(gemLabel).left().row()
        }

        val gearButton = ImageButton(ResourceManager.getPauseButtonDrawable()).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    pauseGame()
                    showSettingsModal()
                }
            })
        }

        // 배속 버튼 (정사각형 배경 이미지 사용)
        val speedButtonStyle = TextButton.TextButtonStyle().apply {
            font = skin.get("btn", TextButton.TextButtonStyle::class.java).font
            fontColor = Color.WHITE
            up = ResourceManager.getSquareButton2()
            down = ResourceManager.getSquareButtonHighlight()
            over = ResourceManager.getSquareButtonHighlight()
        }
        speedButton = TextButton(getSpeedText(), speedButtonStyle).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    toggleGameSpeed()
                }
            })
        }

        val topRight = Table().apply {
            add(gearButton).size(56f).padBottom(8f).row() // 14px * 4 = 56px (4배 스케일링)
            add(speedButton).size(56f, 56f).top() // 정사각형 배경 이미지 크기 56x56
        }

        topUIContainer.add(topLeft).left().top().padLeft(8f)
        topUIContainer.add(Table()).expandX() // 중간 공간 채우기
        topUIContainer.add(topRight).right().top().padRight(8f)

        // 메인 레이아웃 구성 (퍼센트 기반)
        val screenHeight = viewport.worldHeight
        mainLayout.add(topUIContainer).fillX().height(screenHeight * topUIHeightRatio).row()
        mainLayout.add(gameArea).fillX().height(screenHeight * gameAreaHeightRatio).row()
        mainLayout.add(upgradeContainer).fillX().height(screenHeight * upgradeUIHeightRatio)

        // 중앙 상단에 타이머 추가
        timerLabel = Label("10:00", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        val timerContainer = Table().apply {
            background = BaseScreen.skin.getDrawable("white")
            color = PANEL_BG
            pad(8f, 12f, 8f, 12f)
            add(timerLabel)
        }

        uiStage.addActor(timerContainer.apply {
            pack() // 컨테이너 크기 계산
            setPosition(
                (uiStage.viewport.worldWidth - width) / 2f,
                uiStage.viewport.worldHeight - height - 12f
            )
        })

        // 보스 체력바 설정 (초기에는 숨겨짐)
        setupBossHealthBar()
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
        val healthBarContainer = Table().apply {
            add(bossHealthBarBackground).size(300f, 20f)
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
        val skills = saveData.equippedSkills.map { SkillRegistry.createSkill(it) }.toMutableList()
        // 플레이어 위치를 게임 영역 중앙으로 조정 (퍼센트 기반)
        player = Player(skills = skills, saveData = saveData).apply {
            val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
            val gameAreaHeight = viewport.worldHeight * gameAreaHeightRatio
            y = gameAreaStartY + (gameAreaHeight / 2f)
        }
    }

    override fun render(delta: Float) {
        // ======= Update =======
        if (!isPaused && !isGameOver) {
            // 배속 적용
            val speedMultiplier = gameObject.saveData.currentSpeedMultiplier
            val adjustedDelta = delta * speedMultiplier
            // 게임 타이머 업데이트 (배속 적용)
            gameTimer += adjustedDelta
            if (gameTimer >= maxGameTime) {
                isGameOver = true
                gameTimer = maxGameTime
                // 게임 오버 처리 (향후 추가 가능)
            }

            // 적 스폰 (업그레이드 효과 적용)
            val spawnSpeedMultiplier = InGameUpgradeManager.getEnemySpawnSpeedMultiplier(gameObject.saveData)
            spawnTimer -= adjustedDelta * spawnSpeedMultiplier

            if (spawnTimer <= 0f) {
                // 기본 적 1마리 + 업그레이드 효과로 추가 적들
                val baseSpawnCount = 1
                val bonusSpawnCount = InGameUpgradeManager.getEnemySpawnCountBonus(gameObject.saveData)
                val totalSpawnCount = baseSpawnCount + bonusSpawnCount

                repeat(totalSpawnCount) {
                    val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
                    val gameAreaHeight = viewport.worldHeight * gameAreaHeightRatio
                    enemies.add(EnemyFactory.spawnRandom(viewport.worldWidth, gameAreaHeight, gameAreaStartY))
                }

                spawnTimer = 1f // 1초마다 적 추가
            }

            // 보스 스폰 (1분마다)
            bossSpawnTimer -= adjustedDelta
            if (bossSpawnTimer <= 0f) {
                val gameAreaStartY = viewport.worldHeight * upgradeUIHeightRatio
                val gameAreaHeight = viewport.worldHeight * gameAreaHeightRatio
                val boss = EnemyFactory.spawnBoss(viewport.worldWidth, gameAreaHeight, gameAreaStartY)
                enemies.add(boss)
                currentBoss = boss
                bossSpawnTimer = 60f // 1분 후 다시 스폰
                showBossHealthBar()
            }

            player.update(adjustedDelta, enemies, projectiles, effects) { damage, x, y, element ->
                addDamageText(damage, x, y, element)
            }

            enemies.forEach { it.update(adjustedDelta, player) }
            projectiles.forEach { it.update(adjustedDelta) }
            effects.forEach { it.update(adjustedDelta) }

            // 데미지 텍스트 업데이트
            damageTexts.removeAll { !it.update(adjustedDelta) }
        }

        // 애니메이션 시간은 일시정지와 관계없이 계속 진행
        animationTime += delta

        // 적 사망 체크 → 이펙트 추가
        val deadEnemies = enemies.filter { it.isDead() }
        deadEnemies.forEach { enemy ->
            effects.add(
                Effect(
                    EffectManager.load(enemy.getDeathEffectType()),
                    enemy.x, enemy.y, scale = 1.2f
                )
            )

            // 업그레이드 효과가 적용된 골드/실버 획득
            val baseReward = enemy.rewardGold
            val goldMultiplier = InGameUpgradeManager.getGoldMultiplier(gameObject.saveData)
            val silverMultiplier = InGameUpgradeManager.getSilverMultiplier(gameObject.saveData)

            val goldReward = (baseReward * goldMultiplier).toInt()
            val silverReward = (baseReward * silverMultiplier).toInt()

            gameObject.saveData.gold += goldReward
            gameObject.saveData.silver += silverReward

            // 보스 처치 시 젬 획득
            if (enemy.type == EnemyType.BOSS) {
                val baseGemReward = 1
                val gemBonus = InGameUpgradeManager.getGemBonus(gameObject.saveData)
                val totalGemReward = baseGemReward + gemBonus

                gameObject.saveData.gems += totalGemReward

                // 보스가 죽으면 체력바 숨기기 및 현재 보스 참조 제거
                if (enemy == currentBoss) {
                    hideBossHealthBar()
                    currentBoss = null
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
        shape.projectionMatrix = camera.combined

        // 사정거리 표시 (Line 모드로 원의 테두리만 그리기)
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = Color.GRAY
        val effectiveRange = UpgradeManager.getEffectiveRange(gameObject.saveData, player.baseRange)
        shape.circle(player.x, player.y, effectiveRange)
        shape.end()

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.end()

        // --- 이펙트와 플레이어 (SpriteBatch) ---
        batch.begin()

        // Player (base_orb 이미지)
        val orbDrawable = ResourceManager.getBaseOrbDrawable()
        val orbSize = 50f
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

        batch.end()

        // --- UI ---
        goldLabel.setText("골드: ${gameObject.saveData.gold}")
        gemLabel.setText("젬: ${gameObject.saveData.gems}")
        silverLabel.setText("실버: ${gameObject.saveData.silver}")
        upgradePanel.refreshUI()

        // 타이머 업데이트 (남은 시간으로 표시)
        val remainingTime = maxGameTime - gameTimer
        val minutes = (remainingTime / 60).toInt()
        val seconds = (remainingTime % 60).toInt()
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds))

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
        settingsModal.show(
            onHome = { showExitConfirmation() },
            onPlay = { resumeGame() },
            onRestart = { showRestartConfirmation() }
        )
    }

    private fun showExitConfirmation() {
        modalDialog.show(
            title = "게임 나가기",
            message = "게임을 나가시겠습니까?",
            confirmText = "나가기",
            cancelText = "취소",
            confirmColor = Color.RED,
            onConfirm = {
                gameObject.setScreen(HomeScreen(gameObject))
            },
            onCancel = { showSettingsModal() } // 설정 모달로 돌아가기
        )
    }

    private fun showRestartConfirmation() {
        modalDialog.show(
            title = "게임 재시작",
            message = "게임을 다시 시작하시겠습니까?",
            confirmText = "재시작",
            cancelText = "취소",
            confirmColor = Color.ORANGE,
            onConfirm = {
                restartGame()
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
        hideBossHealthBar()

        // 플레이어 재생성
        loadSaveData()

        // 게임 재개
        resumeGame()
        settingsModal.hide()

        // 배속 버튼 텍스트 업데이트
        speedButton.setText(getSpeedText())
    }

    fun addDamageText(damage: Int, x: Float, y: Float, element: com.example.theorb.balance.Element) {
        damageTexts.add(DamageText(damage, x, y, element))
    }

    private fun showBossHealthBar() {
        currentBoss?.let { boss ->
            bossNameLabel.setText("${boss.element.name} BOSS")
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
        val backgroundX = bossHealthBar.x + (bossHealthBar.width - 300f) / 2f
        val backgroundY = bossHealthBar.y - 25f // 라벨 아래에 위치
        bossHealthBarFill.setPosition(backgroundX, backgroundY)
    }

    private fun updateBossHealthBar() {
        currentBoss?.let { boss ->
            if (boss.isDead()) {
                hideBossHealthBar()
            } else {
                // 체력바 비율 계산 (최대 체력 기준)
                val maxHp = (Balance.BASE_HP * Balance.TYPE_MULTIPLIERS[EnemyType.BOSS]!!.hpMul).toInt()
                val healthPercentage = boss.hp.toFloat() / maxHp.toFloat()

                // 체력바 너비 업데이트 (300f가 최대 너비)
                val fillWidth = 300f * healthPercentage
                bossHealthBarFill.setSize(fillWidth, 20f)

                // 위치도 다시 업데이트
                updateBossHealthBarPosition()
            }
        }
    }

    private fun getSpeedText(): String {
        return "${gameObject.saveData.currentSpeedMultiplier.toInt()}x"
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

        speedButton.setText(getSpeedText())
        SaveManager.save(saveData)
    }

    // 3배속 해금 함수 (과금 시스템에서 호출)
    fun unlock3xSpeed() {
        gameObject.saveData.maxSpeedMultiplier = 3.0f
        SaveManager.save(gameObject.saveData)
    }

    // 스테이지 종료 시 인게임 화폐 및 업그레이드 초기화
    fun resetInGameProgress() {
        gameObject.saveData.silver = 0
        gameObject.saveData.inGameUpgrades.clear()
        gameObject.saveData.criticalChance = 5f
        gameObject.saveData.criticalDamage = 150f
        SaveManager.save(gameObject.saveData)
    }

    override fun dispose() {
        super.dispose()
        shape.dispose()
        batch.dispose()
        disposeSharedResources()
    }
}
