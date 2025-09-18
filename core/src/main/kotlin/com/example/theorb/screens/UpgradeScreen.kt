package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.example.theorb.TheOrb
import com.example.theorb.data.SaveManager
import com.example.theorb.ui.ModalDialog
import com.example.theorb.upgrades.UpgradeManager
import com.example.theorb.upgrades.UpgradeType

class UpgradeScreen(private val game: TheOrb) : BaseScreen() {
    private val uiStage = Stage(viewport)
    private lateinit var goldLabel: Label
    private val upgradeLabels = mutableMapOf<UpgradeType, Label>()
    private val upgradeButtons = mutableMapOf<UpgradeType, TextButton>()
    private lateinit var modalDialog: ModalDialog

    private fun createRoundedRectWithBorder(width: Int, height: Int,
                                          bgColor: Color, borderColor: Color, borderWidth: Int): TextureRegionDrawable {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

        // 전체를 테두리 색으로 채우기
        pixmap.setColor(borderColor)
        pixmap.fill()

        // 내부를 배경색으로 채우기 (테두리를 남기기 위해 안쪽 영역만)
        pixmap.setColor(bgColor)
        pixmap.fillRectangle(borderWidth, borderWidth, width - 2 * borderWidth, height - 2 * borderWidth)

        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
    }

    override fun show() {
        initSharedResources()
        Gdx.input.inputProcessor = uiStage
        modalDialog = ModalDialog(uiStage, skin)
        setupUI()
    }

    private fun setupUI() {
        val root = Table().apply {
            setFillParent(true)
            pad(20f)
        }
        uiStage.addActor(root)

        // 상단 골드 표시
        goldLabel = Label("GOLD: ${game.saveData.gold}", skin.get("label-default", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        root.add(goldLabel).padBottom(20f).row()

        // 제목
        val titleLabel = Label("BASE UPGRADE", skin.get("label-large", Label.LabelStyle::class.java)).apply {
            color = TEXT_PRIMARY
        }
        root.add(titleLabel).padBottom(30f).row()

        // 업그레이드 목록
        val scrollPane = createUpgradeList()
        root.add(scrollPane).expand().fill().padBottom(20f).row()

        // 버튼들
        val buttonTable = Table()

        val backButtonWidth = (virtualWidth * 0.3f).toInt()
        val backButtonHeight = (virtualHeight * 0.075f).toInt()

        val backButton = TextButton("뒤로가기", TextButton.TextButtonStyle().apply {
            font = skin.get("btn", TextButton.TextButtonStyle::class.java).font
            up = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, ACCENT, BORDER, 2)
            down = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, ACCENT.cpy().mul(0.8f), BORDER, 2)
            over = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, ACCENT.cpy().mul(1.2f), BORDER, 2)
            fontColor = Color.WHITE
        })
        backButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(HomeScreen(game))
            }
        })

        val resetButton = TextButton("초기화", TextButton.TextButtonStyle().apply {
            font = skin.get("btn", TextButton.TextButtonStyle::class.java).font
            up = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, DANGER, BORDER, 2)
            down = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, DANGER.cpy().mul(0.8f), BORDER, 2)
            over = createRoundedRectWithBorder(backButtonWidth, backButtonHeight, DANGER.cpy().mul(1.2f), BORDER, 2)
            fontColor = Color.WHITE
        })
        resetButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                showResetConfirmDialog()
            }
        })

        buttonTable.add(resetButton).width(virtualWidth * 0.3f).height(virtualHeight * 0.075f).padRight(20f)
        buttonTable.add(backButton).width(virtualWidth * 0.3f).height(virtualHeight * 0.075f)
        root.add(buttonTable)
    }

    private fun createUpgradeList(): ScrollPane {
        val table = Table()

        for (upgradeType in UpgradeType.values()) {
            val upgradeTable = Table()
            upgradeTable.background = skin.getDrawable("white")
            upgradeTable.color = PANEL_BG // 다크 테마 패널 색상
            upgradeTable.pad(15f)

            // 업그레이드 정보
            val nameLabel = Label(upgradeType.displayName, skin.get("label-default", Label.LabelStyle::class.java)).apply {
                color = TEXT_PRIMARY
            }
            val descLabel = Label(upgradeType.description, skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = TEXT_SECONDARY
            }

            val levelLabel = Label("", skin.get("label-small", Label.LabelStyle::class.java)).apply {
                color = TEXT_SECONDARY
            }
            upgradeLabels[upgradeType] = levelLabel

            // 업그레이드 버튼
            val buttonWidth = (virtualWidth * 0.25f).toInt()
            val buttonHeight = (virtualHeight * 0.06f).toInt()
            val upgradeButton = TextButton("LEVEL UP", TextButton.TextButtonStyle().apply {
                font = skin.get("btn", TextButton.TextButtonStyle::class.java).font
                up = createRoundedRectWithBorder(buttonWidth, buttonHeight, SUCCESS, BORDER, 2)
                down = createRoundedRectWithBorder(buttonWidth, buttonHeight, SUCCESS.cpy().mul(0.8f), BORDER, 2)
                over = createRoundedRectWithBorder(buttonWidth, buttonHeight, SUCCESS.cpy().mul(1.2f), BORDER, 2)
                disabled = createRoundedRectWithBorder(buttonWidth, buttonHeight, Color.GRAY, BORDER, 2)
                fontColor = Color.WHITE
            })
            upgradeButtons[upgradeType] = upgradeButton

            upgradeButton.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    if (UpgradeManager.purchaseUpgrade(game.saveData, upgradeType)) {
                        SaveManager.save(game.saveData)
                        updateUpgradeDisplay()
                    }
                }
            })

            // 레이아웃
            val leftTable = Table()
            leftTable.add(nameLabel).expand().left().row()
            leftTable.add(descLabel).expand().left().row()
            leftTable.add(levelLabel).expand().left()

            upgradeTable.add(leftTable).expand().fill().left().padLeft(10f)
            upgradeTable.add(upgradeButton).width(virtualWidth * 0.3f).height(virtualHeight * 0.06f).right().padRight(10f)

            table.add(upgradeTable).fillX().width(virtualWidth * 0.85f).padBottom(10f).row()
        }

        return ScrollPane(table, skin).apply {
            setScrollingDisabled(true, false)
        }
    }

    private fun updateUpgradeDisplay() {
        goldLabel.setText("GOLD: ${game.saveData.gold}")

        for (upgradeType in UpgradeType.values()) {
            val level = UpgradeManager.getUpgradeLevel(game.saveData, upgradeType)
            val maxLevel = upgradeType.maxLevel
            val currentValue = UpgradeManager.getUpgradeValue(game.saveData, upgradeType)
            val cost = if (level < maxLevel) upgradeType.getCostForLevel(level) else -1

            val levelText = if (level >= maxLevel) {
                "레벨: $level/$maxLevel (최대)"
            } else {
                "레벨: $level/$maxLevel\n현재 효과: ${formatValue(upgradeType, currentValue)}\n비용: ${cost} GOLD"
            }

            upgradeLabels[upgradeType]?.setText(levelText)

            val button = upgradeButtons[upgradeType]!!
            val canUpgrade = UpgradeManager.canUpgrade(game.saveData, upgradeType)
            button.isDisabled = !canUpgrade
        }
    }

    private fun formatValue(upgradeType: UpgradeType, value: Float): String {
        return when (upgradeType) {
            UpgradeType.DAMAGE -> "+${value.toInt()}"
            UpgradeType.RANGE -> "+${(value * 100).toInt()} %" // % 형태로 표시
            UpgradeType.COOLDOWN_REDUCTION -> "-${(value * 100).toInt()} %"
        }
    }

    private fun showResetConfirmDialog() {
        modalDialog.show(
            title = "업그레이드 초기화",
            message = "모든 업그레이드를 초기화하고\n사용한 골드를 환불받겠습니까?",
            confirmText = "초기화",
            cancelText = "취소",
            confirmColor = Color.RED,
            onConfirm = { performReset() },
            onCancel = { /* 아무것도 하지 않음 */ }
        )
    }

    private fun performReset() {
        val refundAmount = UpgradeManager.resetAllUpgrades(game.saveData)
        SaveManager.save(game.saveData)
        updateUpgradeDisplay()

        Gdx.app.log("UpgradeScreen", "업그레이드 초기화 완료! 환불된 골드: $refundAmount")
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        updateUpgradeDisplay()

        uiStage.act(delta)
        uiStage.draw()
    }

    override fun dispose() {
        super.dispose()
        uiStage.dispose()
        disposeSharedResources()
    }
}
