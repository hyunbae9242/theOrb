package com.example.theorb.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.example.theorb.data.OrbRegistry
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.modal.OrbSelectionModal
import com.example.theorb.ui.RetroButtonV01
import com.example.theorb.ui.TopBar
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class HomeScreen(private val game: Game) : BaseScreen() {
    private val stage = Stage(viewport)
    private lateinit var orbSelectionModal: OrbSelectionModal
    private lateinit var topBar: TopBar


    // stage 관련 임시데이터
    private var stageIdx = 1
    private val minStageIdx = 1
    private val maxStageIdx = 5

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = stage
        orbSelectionModal = OrbSelectionModal(stage, skin, gameObject.saveData)
        topBar = TopBar(stage, skin)

        setupUi()
    }

    private fun setupUi() {
        // 공통 레이아웃 시스템 사용
        val root = createRootLayout(stage)

        // ===== 상단 바 =====
        val topBarTable = topBar.createTopBar()
        addTopBar(root, topBarTable)

        // ===== 중앙 컨텐츠 =====
        val mainContent = createMainContent()
        addMainContent(root, mainContent)

        // ===== 하단 네비게이션 =====
        val bottomNavigation = BottomNavigation(game, skin, BottomNavigation.Tab.MAIN)
        val bottomNav = bottomNavigation.createBottomNavigation()
        addBottomNavigation(root, bottomNav)
    }


    private fun createMainContent(): Table {
        val mainContent = Table()

        // 오브 이미지 테이블 (300f)
        val orbTable = createOrbTable()
        // 버튼 테이블
        val buttonTable = createMainTable()

        mainContent.add(orbTable).row()
        mainContent.add(buttonTable).padTop(80f)

        return mainContent
    }

    private fun createOrbTable(): Table {
        val orbTable = Table()

        val selectedOrbData = OrbRegistry.getOrbById(gameObject.saveData.selectedOrb)
            ?: OrbRegistry.getOrbById("base")!!

        val orb = Image(selectedOrbData.getDrawable()).apply {
            touchable = Touchable.enabled
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    orbSelectionModal.show(
                        onClose = {
                            orbSelectionModal.hide()
                        },
                        onOrbSelected = { newOrbData ->
                            this@apply.drawable = newOrbData.getDrawable()
                            gameObject.saveData.selectedOrb = newOrbData.id
                            SaveManager.save(gameObject.saveData)
                        }
                    )
                }
            })
        }

        orbTable.add(orb).size(300f)
        return orbTable
    }

    private fun createMainTable(): Table {
        val mainTable = Table().apply {
            top()
            left()
            background = ResourceManager.getHomeMainPanel()
        }


        val stageTable = Table()
        val leftBtn = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getLeftBasePos(),
            eventImage = ResourceManager.getLeftEventPos(),
            disabledImage = ResourceManager.getLeftBaseNag(),
            stageIdx != minStageIdx
        ) {
            stageIdx -= 1;
        }

        val rightBtn = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getRightBasePos(),
            eventImage = ResourceManager.getRightEventPos(),
            disabledImage = ResourceManager.getRightBaseNag(),
            stageIdx != maxStageIdx
        ) {
            stageIdx += 1;
        }

        val stageLabel = Label("STAGE ${formatNumber(stageIdx)}", skin.get("label-large-bold", Label.LabelStyle::class.java))

        val startBtn = RetroButtonV01.createIconButton(
            defaultImage = ResourceManager.getStartBasePos(),
            eventImage = ResourceManager.getStartEventPos(),
            disabledImage = ResourceManager.getStartBaseNag(),
            gameObject.saveData.equippedSkills.isNotEmpty(),
        ) {
            game.setScreen(GameScreen())
        }

        stageTable.add(leftBtn).padLeft(16f).left()
        stageTable.add(stageLabel).center().expandX()
        stageTable.add(rightBtn).padRight(16f).right()

        mainTable.add(stageTable).padTop(12f).padBottom(8f).expandX().fillX().row()
        mainTable.add(startBtn).center()

        return mainTable
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        stage.dispose()
        disposeSharedResources()
    }
}
