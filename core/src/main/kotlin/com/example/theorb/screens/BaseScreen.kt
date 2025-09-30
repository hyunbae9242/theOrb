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
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.example.theorb.TheOrb
import com.example.theorb.ui.BackgroundRenderer
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
//        val TEXT_PRIMARY = Color(75/255f, 48f/255f, 40f/255f, 1f)  // 기본 텍스트 (짙은 브라운)
//        val TEXT_SECONDARY = Color(216f/255f, 188f/255f, 180f/255f, 1f)    // 보조 텍스트 (따뜻한 베이지)
        val TEXT_PRIMARY: Color = Color.WHITE  // 기본 텍스트
        val TEXT_SECONDARY: Color = Color.GRAY   // 보조 텍스트
        val TEXT_DISABLED: Color = Color.DARK_GRAY      // 비활성화 텍스트
        val ACCENT = Color(0xD4AF37FF.toInt())              // 액센트 골든
        val SUCCESS = Color(0x8FBC8FFF.toInt())             // 성공 다크시그린
        val WARNING = Color(0xCD853FFF.toInt())             // 경고 페루
        val DANGER = Color(0xA0522DFF.toInt())              // 위험 시에나브라운

        // 공통 패딩 설정
        const val SCREEN_PADDING = 12f                       // 화면 전체 패딩
        const val COMPONENT_PADDING = 12f                    // 컴포넌트 간 패딩

        // UI 크기 비율 (기준 해상도 480x800 기준)
        const val VIRTUAL_WIDTH = 480f
        const val VIRTUAL_HEIGHT = 800f

        // 표준 버튼 크기 비율
        const val BUTTON_HEIGHT_RATIO = 42f / VIRTUAL_HEIGHT  // 약 5.25%
        const val RECTANGLE_BUTTON_WIDTH_RATIO = 84f / VIRTUAL_WIDTH  // 약 17.5%
        const val SQUARE_BUTTON_SIZE_RATIO = 42f / VIRTUAL_HEIGHT  // 정사각형 버튼

        // 기준 폰트 크기 (480x800 기준)
        const val BASE_FONT_SMALL = 16f
        const val BASE_FONT_MEDIUM = 20f
        const val BASE_FONT_LARGE = 26f

        // 폰트 스케일링 계산 (DPI 기반)
        fun calculateFontScale(): Float {
            // Android DPI 기준으로 스케일 계산
            val density = Gdx.graphics.density
            // 기준 밀도 (mdpi = 1.0) 기준으로 스케일링
            val baseScale = when {
                density >= 4.0f -> 2.5f  // xxxhdpi
                density >= 3.0f -> 2.0f  // xxhdpi
                density >= 2.0f -> 1.5f  // xhdpi
                density >= 1.5f -> 1.25f // hdpi
                density >= 1.0f -> 1.0f  // mdpi
                else -> 0.8f             // ldpi
            }

            return baseScale.coerceIn(0.8f, 2.5f)
        }
        lateinit var gameObject: TheOrb
        lateinit var fontSm: BitmapFont
        lateinit var fontMd: BitmapFont
        lateinit var fontLg: BitmapFont
        lateinit var fontSmBold: BitmapFont
        lateinit var fontMdBold: BitmapFont
        lateinit var fontLgBold: BitmapFont
        lateinit var skin: Skin
        private var initialized = false

        // 공통 배경 렌더러
        private var sharedBackgroundRenderer: BackgroundRenderer? = null

        // 고정 UI 요소들의 높이
        const val TOP_BAR_HEIGHT = 80f      // 상단바 고정 높이
        const val BOTTOM_NAV_HEIGHT = 60f   // 하단네비 고정 높이
        const val TOP_BAR_PADDING = 12f     // 상단바 아래 패딩
        const val CONTENT_SIDE_PADDING = 12f // 컨텐츠 좌우 패딩
        const val BOTTOM_NAV_PADDING = 6f   // 하단네비 아래 패딩

        fun initSharedResources() {
            if (!initialized) {
                // --- object ---
                gameObject = Gdx.app.applicationListener as TheOrb

                // --- 동적 폰트 크기 계산 ---
                val fontScale = calculateFontScale()

                val smallSize = (BASE_FONT_SMALL * fontScale).toInt().coerceAtLeast(10)
                val mediumSize = (BASE_FONT_MEDIUM * fontScale).toInt().coerceAtLeast(12)
                val largeSize = (BASE_FONT_LARGE * fontScale).toInt().coerceAtLeast(14)

                Gdx.app.log("FontSystem", "Density: ${Gdx.graphics.density}, Scale: $fontScale, Sizes: $smallSize/$mediumSize/$largeSize")

                // --- Fonts ---
                fontSm = FontUtil.load(smallSize)
                fontMd = FontUtil.load(mediumSize)
                fontLg = FontUtil.load(largeSize)
                fontSmBold = FontUtil.loadBold(smallSize)
                fontMdBold = FontUtil.loadBold(mediumSize)
                fontLgBold = FontUtil.loadBold(largeSize)

                // --- Skin ---
                skin = Skin().apply {
                    // 기본 흰색 텍스처
                    add("white", Texture(Pixmap(2, 2, Pixmap.Format.RGBA8888).apply {
                        setColor(Color.WHITE); fill()
                    }))

                    // Label 스타일 - 새로운 기본 텍스트 색상 적용
                    add("label-small", Label.LabelStyle(fontSm, TEXT_PRIMARY))
                    add("label-default", Label.LabelStyle(fontMd, TEXT_PRIMARY))
                    add("label-large", Label.LabelStyle(fontLg, TEXT_PRIMARY))
                    add("label-small-bold", Label.LabelStyle(fontSmBold, TEXT_PRIMARY))
                    add("label-default-bold", Label.LabelStyle(fontMdBold, TEXT_PRIMARY))
                    add("label-large-bold", Label.LabelStyle(fontLgBold, TEXT_PRIMARY))

                    // 버튼 스타일
                    val btn = TextButton.TextButtonStyle().apply {
                        font = fontSm
                        fontColor = TEXT_PRIMARY
                        up = null; down = null; over = null
                    }
                    add("btn", btn)

                    // 작은 볼드 버튼 스타일
                    val btnSmallBold = TextButton.TextButtonStyle().apply {
                        font = fontSmBold
                        fontColor = TEXT_PRIMARY
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
                sharedBackgroundRenderer?.dispose()
                sharedBackgroundRenderer = null
                initialized = false
                Gdx.app.log("BaseScreen", "Fonts & Skin disposed")
            }
        }

        // 공통 배경 렌더러 관련 함수들
        fun getSharedBackgroundRenderer(): BackgroundRenderer {
            if (sharedBackgroundRenderer == null) {
                sharedBackgroundRenderer = BackgroundRenderer()
                // 저장된 배경화면 설정으로 초기화
                sharedBackgroundRenderer!!.setBackground(gameObject.saveData.selectedBackground)
            }
            return sharedBackgroundRenderer!!
        }

        fun updateSharedBackground(newBackgroundName: String) {
            if (sharedBackgroundRenderer == null) {
                sharedBackgroundRenderer = BackgroundRenderer()
            }
            sharedBackgroundRenderer!!.setBackground(newBackgroundName)
        }

        fun changeSharedBackground(stage: Stage, newBackgroundName: String, screenWidth: Float, screenHeight: Float) {
            if (sharedBackgroundRenderer == null) {
                sharedBackgroundRenderer = BackgroundRenderer()
            }
            sharedBackgroundRenderer!!.changeBackground(stage, newBackgroundName, screenWidth, screenHeight)
        }

        // UI 헬퍼 함수들을 static으로 제공
        fun getSquareButtonSize(): Float {
            val camera = Gdx.graphics.width.toFloat()
            val height = Gdx.graphics.height.toFloat()
            val virtualHeight = 800f
            return virtualHeight * SQUARE_BUTTON_SIZE_RATIO
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    // UI 크기 계산 헬퍼 함수들
    protected fun getButtonHeight(): Float = virtualHeight * BUTTON_HEIGHT_RATIO
    protected fun getRectangleButtonWidth(): Float = virtualWidth * RECTANGLE_BUTTON_WIDTH_RATIO
    protected fun getSquareButtonSize(): Float = virtualHeight * SQUARE_BUTTON_SIZE_RATIO

    // 커스텀 비율 계산
    protected fun scaleWidth(ratio: Float): Float = virtualWidth * ratio
    protected fun scaleHeight(ratio: Float): Float = virtualHeight * ratio

    // 공통 레이아웃 시스템
    protected fun createRootLayout(stage: Stage, horizontalPadding: Float = 12f, verticalPadding: Float = SCREEN_PADDING): Table {
        val rootLayout = Table().apply {
            setSize(virtualWidth, virtualHeight)
            setPosition(0f, 0f)
            pad(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding)
        }
        stage.addActor(rootLayout)

        // 공통 배경 렌더러 적용
        val backgroundRenderer = getSharedBackgroundRenderer()
        backgroundRenderer.addToStage(stage, virtualWidth, virtualHeight)

        return rootLayout
    }


    // 실제 컨텐츠가 사용할 수 있는 높이 계산
    protected fun getContentAreaHeight(): Float {
        return virtualHeight - SCREEN_PADDING * 2 - TOP_BAR_HEIGHT - BOTTOM_NAV_HEIGHT - TOP_BAR_PADDING - BOTTOM_NAV_PADDING
    }

    // 표준 레이아웃 패턴들
    protected fun addTopBar(rootLayout: Table, topBarContent: Table?, height: Float = TOP_BAR_HEIGHT): Table? {
        return topBarContent?.also {
            rootLayout.add(it).width(virtualWidth * 0.95f).height(height).padBottom(TOP_BAR_PADDING).row()
        }
    }

    protected fun addMainContent(rootLayout: Table, mainContent: Table): Table {
        Gdx.app.log("baseScreen", "getContentAreaHeight: ${getContentAreaHeight()}")
        // expand 대신 정확한 높이 지정으로 다른 요소들이 밀리지 않도록 함
        rootLayout.add(mainContent).height(getContentAreaHeight()).fillX().padRight(CONTENT_SIDE_PADDING).padLeft(CONTENT_SIDE_PADDING).row()
        return mainContent
    }

    protected fun addBottomNavigation(rootLayout: Table, bottomNav: Table): Table {
        rootLayout.add(bottomNav).growX().padBottom(BOTTOM_NAV_PADDING)
        return bottomNav
    }
}
