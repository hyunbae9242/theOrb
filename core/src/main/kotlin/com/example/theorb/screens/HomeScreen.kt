package com.example.theorb.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.example.theorb.TheOrb
import com.example.theorb.data.OrbRegistry
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BackgroundRenderer
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.ui.OrbSelectionModal
import com.example.theorb.ui.SettingsModal
import com.example.theorb.ui.TopBar
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class HomeScreen(private val game: Game) : BaseScreen() {
    private val stage = Stage(viewport)
    private var stageIndex = 1
    private lateinit var orbSelectionModal: OrbSelectionModal
    private lateinit var topBar: TopBar

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = stage


        // UI 컴포넌트들 초기화
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

        // 오브 + 스테이지 정보
        val selectedOrbData = OrbRegistry.getOrbById(gameObject.saveData.selectedOrb)
            ?: OrbRegistry.getOrbById("base")!!

        val orb = Image(selectedOrbData.getDrawable()).apply {
            setSize(45f, 45f)
            touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
            addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
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

        val stageLabel = Label("스테이지 $stageIndex", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }

        val startBtn = TextButton("게임 시작", skin.get("btn", TextButton.TextButtonStyle::class.java).apply { font = fontLg })
        startBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(GameScreen())
            }
        })

        // 레이아웃 구성
        mainContent.add(orb).pad(60f).row()
        mainContent.add(stageLabel).padBottom(40f).row()
        mainContent.add(startBtn).pad(COMPONENT_PADDING).width(280f).height(80f)

        return mainContent
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
