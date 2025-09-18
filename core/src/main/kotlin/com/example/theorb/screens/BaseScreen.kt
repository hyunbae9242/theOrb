package com.example.theorb.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.example.theorb.TheOrb
import com.example.theorb.util.FontUtil

abstract class BaseScreen : ScreenAdapter() {
    // 기준 해상도 (세로 게임 기준)
    protected val virtualWidth = 480f
    protected val virtualHeight = 800f

    protected val camera = OrthographicCamera()
    protected val viewport: Viewport = FitViewport(virtualWidth, virtualHeight, camera)

    companion object {
        // 다크 테마 색상 팔레트
        val BACKGROUND = Color(0x0F0F0FFF.toInt())           // 거의 검정 배경
        val PANEL_BG = Color(0x1A1A1AFF.toInt())            // 어두운 회색 패널
        val BORDER = Color(0x333333FF.toInt())              // 테두리
        val TEXT_PRIMARY = Color(0xE0E0E0FF.toInt())        // 기본 텍스트
        val TEXT_SECONDARY = Color(0xB0B0B0FF.toInt())      // 보조 텍스트
        val ACCENT = Color(0x4A90E2FF.toInt())              // 액센트 파랑
        val SUCCESS = Color(0x2ECC71FF.toInt())             // 성공 민트그린
        val WARNING = Color(0xF39C12FF.toInt())             // 경고 주황
        val DANGER = Color(0xE74C3CFF.toInt())              // 위험 빨강
        lateinit var gameObject: TheOrb
        lateinit var fontSm: BitmapFont
        lateinit var fontMd: BitmapFont
        lateinit var fontLg: BitmapFont
        lateinit var fontSmBold: BitmapFont
        lateinit var skin: Skin
        private var initialized = false

        fun initSharedResources() {
            if (!initialized) {
                // --- object ---
                gameObject = Gdx.app.applicationListener as TheOrb
                // --- Fonts ---
                fontSm = FontUtil.load(18)
                fontMd = FontUtil.load(22)
                fontLg = FontUtil.load(28)
                fontSmBold = FontUtil.loadBold(18)

                // --- Skin ---
                skin = Skin().apply {
                    // 기본 흰색 텍스처
                    add("white", Texture(Pixmap(2, 2, Pixmap.Format.RGBA8888).apply {
                        setColor(Color.WHITE); fill()
                    }))

                    // Label 스타일
                    add("label-small", Label.LabelStyle(fontSm, Color.WHITE))
                    add("label-default", Label.LabelStyle(fontMd, Color.WHITE))
                    add("label-large", Label.LabelStyle(fontLg, Color.WHITE))
                    add("label-small-bold", Label.LabelStyle(fontSmBold, Color.WHITE))

                    // 버튼 스타일
                    val btn = TextButton.TextButtonStyle().apply {
                        font = fontSm
                        fontColor = Color.WHITE
                        up = null; down = null; over = null
                    }
                    add("btn", btn)

                    // 작은 볼드 버튼 스타일
                    val btnSmallBold = TextButton.TextButtonStyle().apply {
                        font = fontSmBold
                        fontColor = Color.WHITE
                        up = null; down = null; over = null
                    }
                    add("btn-small-bold", btnSmallBold)

                    // ScrollPane 스타일
                    val scrollPaneStyle = ScrollPane.ScrollPaneStyle().apply {
                        background = null
                        vScroll = null
                        vScrollKnob = null
                        hScroll = null
                        hScrollKnob = null
                        corner = null
                    }
                    add("default", scrollPaneStyle)
                }

                initialized = true
                Gdx.app.log("BaseScreen", "Fonts & Skin initialized")
            }
        }

        fun disposeSharedResources() {
            if (initialized) {
                fontSm.dispose()
                fontMd.dispose()
                fontLg.dispose()
                fontSmBold.dispose()
                skin.dispose()
                initialized = false
                Gdx.app.log("BaseScreen", "Fonts & Skin disposed")
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
