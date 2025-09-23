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
import com.example.theorb.util.ResourceManager
import com.example.theorb.util.formatNumber

class HomeScreen(private val game: Game) : BaseScreen() {
    private val stage = Stage(viewport)
    private var stageIndex = 1
    private val backgroundRenderer = BackgroundRenderer()
    private lateinit var orbSelectionModal: OrbSelectionModal

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = stage

        // 배경 설정 (미리 정의된 투명도가 자동 적용됨)
        backgroundRenderer.setBackground("clouds02") // clouds02 배경 사용 (투명도 1.0f 자동 적용)
        backgroundRenderer.addToStage(stage, viewport.worldWidth, viewport.worldHeight)

        // 오브 선택 모달 초기화
        orbSelectionModal = OrbSelectionModal(stage, skin, gameObject.saveData)

        setupUi()
    }

    private fun setupUi() {
        val root = Table().apply {
            setFillParent(true)
            pad(SCREEN_PADDING)
        }
        stage.addActor(root)

        // ===== 상단 바 =====
        val top = Table().apply { pad(COMPONENT_PADDING) }

        val gold = Label("골드: ${formatNumber(gameObject.saveData.gold)}", skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        val gem  = Label("젬: 5",    skin.get("label-small", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        val left = Table().apply {
            add(gold).right().row()
            add(gem).right()
        }
        val menuIcon = Image(ResourceManager.getDrawable("images/buttons/blue/Menu_icon.png")).apply {
            setSize(40f, 40f)
            touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
            // 추후 메뉴 기능 추가 가능
        }

        top.add(left).left().expandX()
        top.add(menuIcon).right()

        // ===== 중앙(오브 + 스테이지 선택) =====
        val center = Table()

        // 선택된 오브 이미지
        val selectedOrbData = OrbRegistry.getOrbById(gameObject.saveData.selectedOrb)
            ?: OrbRegistry.getOrbById("base")!!

        val orb = Image(selectedOrbData.getDrawable()).apply {
            setSize(45f, 45f)
            touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
            addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                    Gdx.app.log("HomeScreen", "오브 클릭됨!")
                    // 오브 선택 모달 열기
                    orbSelectionModal.show(
                        onClose = {
                            orbSelectionModal.hide()
                        },
                        onOrbSelected = { newOrbData ->
                            // 선택된 오브가 바뀌었을 때 이미지 업데이트
                            this@apply.drawable = newOrbData.getDrawable()
                            // 세이브 데이터 저장
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

        val stageRow = Table().apply {
            add(stageLabel).padRight(16f)
        }

        center.add(orb).pad(60f).row()
        center.add(stageRow).padBottom(12f).row()

        val startBtn = TextButton("게임 시작", skin.get("btn", TextButton.TextButtonStyle::class.java).apply { font = fontLg })
        startBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Gdx.app.log("Home", "게임 시작 버튼 클릭됨")
                game.setScreen(GameScreen())
            }
        })
        // ===== 하단 탭 =====
        val bottomNavigation = BottomNavigation(game, skin, BottomNavigation.Tab.MAIN)
        val bottom = bottomNavigation.createBottomNavigation()

        // ===== 루트 조립 =====
        root.add(top).growX().row()
        root.add(center).expand().row()
        root.add(startBtn).pad(COMPONENT_PADDING).growX().height(80f).row()
        root.add(bottom).growX().padBottom(6f)
        // root.debugAll() // 디버그 모드 비활성화
    }

    // ---- helpers ----
    private fun line(): Image =
        Image(skin.getDrawable("white")).apply { color = Color.WHITE }

    private fun square(size: Float): Image {
        val pm = Pixmap(size.toInt(), size.toInt(), Pixmap.Format.RGBA8888)
        pm.setColor(Color.WHITE); pm.fill()
        val tex = Texture(pm); pm.dispose()
        return Image(TextureRegionDrawable(tex)).apply {
            this.setSize(size, size)
        }
    }

    private fun circle(diameter: Float): Image {
        val d = diameter.toInt().coerceAtLeast(2)
        val pm = Pixmap(d, d, Pixmap.Format.RGBA8888)
        pm.setColor(Color.CLEAR); pm.fill()
        pm.setColor(Color.WHITE)
        val r = d / 2f
        for (y in 0 until d) for (x in 0 until d) {
            val dx = x - r + 0.5f
            val dy = y - r + 0.5f
            if (dx*dx + dy*dy <= r*r) pm.drawPixel(x, y)
        }
        val tex = Texture(pm); pm.dispose()
        return Image(TextureRegionDrawable(tex)).apply { setSize(diameter, diameter) }
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
        backgroundRenderer.dispose()
        disposeSharedResources()
    }
}
