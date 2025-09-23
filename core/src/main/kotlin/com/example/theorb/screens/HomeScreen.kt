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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.example.theorb.TheOrb
import com.example.theorb.data.OrbRegistry
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.BackgroundRenderer
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
        val root = Table().apply { setFillParent(true) }
        stage.addActor(root)

        // ===== 상단 바 =====
        val top = Table().apply { pad(12f) }

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
        val profile = square(40f) // 흰 정사각형(프로필 자리)

        top.add(left).left().expandX()
        top.add(profile).right()
        // 상단 하얀 선
        val lineTop = line()

        // ===== 중앙(오브 + 스테이지 선택) =====
        val center = Table()

        // 선택된 오브 이미지
        val selectedOrbData = OrbRegistry.getOrbById(gameObject.saveData.selectedOrb)
            ?: OrbRegistry.getOrbById("base_orb")!!

        val orb = Image(selectedOrbData.getDrawable()).apply {
            setSize(60f, 60f)
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

        center.add(orb).padTop(24f).padBottom(24f).row()
        center.add(stageRow).padBottom(12f).row()

        val startBtn = TextButton("게임 시작", skin.get("btn", TextButton.TextButtonStyle::class.java).apply { font = fontLg })
        startBtn.addChangeListener {
            Gdx.app.log("Home", "게임 시작 버튼 클릭됨")
            game.setScreen(GameScreen())
        }

        // ===== 하단 탭 =====
        val bottomLine = line()
        val bottom = Table().apply { pad(6f) }

        val tabs = listOf("상점", "카드", "메인", "업그레이드", "스킬")
        tabs.forEach { name ->
            val t = TextButton(name, skin.get("btn", TextButton.TextButtonStyle::class.java).apply {
                font = fontLg
            }).apply {
                label.setAlignment(Align.center)
            }
            // '메인' 탭 강조: 아래 선을 조금 두껍게
//            if (name == "메인") addActionLineBelow(height = 2f) else addActionLineBelow()
            t.addChangeListener {
                when (name) {
                    "업그레이드" -> {
                        Gdx.app.log("Tab", "업그레이드 탭 클릭")
                        game.setScreen(UpgradeScreen(game as TheOrb))
                    }
                    else -> {
                        Gdx.app.log("Tab", "$name 클릭")
                    }
                }
            }
            bottom.add(t).expandX().pad(4f)
        }

        // ===== 루트 조립 =====
        root.add(top).growX().row()
        root.add(lineTop).height(1f).growX().row()
        root.add(center).expand().row()
        root.add(startBtn).pad(12f).growX().height(80f).row()
        root.add(bottomLine).height(1f).growX().row()
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

    private fun TextButton.addActionLineBelow(height: Float = 1f) {
        val underline = Image(Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.WHITE)
            fill()
        })).apply {
            setSize(this@addActionLineBelow.width, height)
            color = Color.WHITE
        }

        val stack = Stack().apply {
            add(this@addActionLineBelow) // 버튼
            add(underline)              // 밑줄
        }

        // 버튼의 부모 컨테이너에 교체 삽입
        this.parent?.let { parent ->
            if (parent is Table) {
                val index = parent.children.indexOf(this, true)
                parent.removeActor(this)
                parent.addActorAt(index, stack)
            }
        }
    }

    private fun TextButton.addChangeListener(block: () -> Unit) {
        addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            override fun changed(event: com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent?, actor: Actor?) {
                block()
            }
        })
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
