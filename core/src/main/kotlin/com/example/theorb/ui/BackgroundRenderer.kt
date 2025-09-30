package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.example.theorb.util.ResourceManager

class BackgroundRenderer {

    companion object {
        // 원본 배경 이미지 사이즈
        private const val ORIGINAL_WIDTH = 576f
        private const val ORIGINAL_HEIGHT = 324f

        // 배경별 기본 투명도 설정
        private val BACKGROUND_ALPHAS = mapOf(
            "clouds01" to 1.0f,
            "clouds02" to 1.0f
        )
    }

    private val backgroundLayers = mutableListOf<Image>()
    private val backgroundTextures = mutableListOf<TextureRegion>()
    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f
    private var backgroundAlpha: Float = 1.0f // 전체 배경 투명도 (레거시)
    private var currentBackgroundName: String = "clouds01" // 현재 사용 중인 배경 이름

    /**
     * 배경 투명도 설정 (0.0f = 완전 투명, 1.0f = 완전 불투명)
     */
    fun setAlpha(alpha: Float) {
        backgroundAlpha = alpha.coerceIn(0.0f, 1.0f)

        // Stage용 Image들의 투명도도 업데이트
        backgroundLayers.forEach { image ->
            image.color = Color(1f, 1f, 1f, backgroundAlpha)
        }
    }

    /**
     * 현재 배경 투명도 반환
     */
    fun getAlpha(): Float = backgroundAlpha


    /**
     * 배경 종류 설정 (미리 정의된 투명도 적용)
     */
    fun setBackground(backgroundName: String) {
        currentBackgroundName = backgroundName
        val predefinedAlpha = BACKGROUND_ALPHAS[backgroundName] ?: 1.0f
        setAlpha(predefinedAlpha)
    }

    /**
     * 현재 배경 이름 반환
     */
    fun getCurrentBackgroundName(): String = currentBackgroundName

    /**
     * 배경의 미리 정의된 투명도 값 반환
     */
    fun getPredefinedAlpha(backgroundName: String): Float {
        return BACKGROUND_ALPHAS[backgroundName] ?: 1.0f
    }

    /**
     * 배경 레이어들을 생성하고 화면에 맞게 스케일링/크롭
     */
    fun createBackground(screenWidth: Float, screenHeight: Float): List<Image> {
        backgroundLayers.clear()

        val layers = ResourceManager.getBackgroundLayers(currentBackgroundName)

        for (layer in layers) {
            val backgroundImage = Image(layer)

            // 화면에 맞게 스케일 계산 (비율 유지하면서 화면을 완전히 채우도록)
            val scaleX = screenWidth / ORIGINAL_WIDTH
            val scaleY = screenHeight / ORIGINAL_HEIGHT
            val scale = maxOf(scaleX, scaleY) // 더 큰 스케일을 사용해서 화면을 완전히 채움

            // 스케일 적용
            val scaledWidth = ORIGINAL_WIDTH * scale
            val scaledHeight = ORIGINAL_HEIGHT * scale

            backgroundImage.setSize(scaledWidth, scaledHeight)

            // 중앙 정렬 (크롭 효과)
            val offsetX = (screenWidth - scaledWidth) / 2f
            val offsetY = (screenHeight - scaledHeight) / 2f

            backgroundImage.setPosition(offsetX, offsetY)

            // 투명도 적용
            backgroundImage.color = Color(1f, 1f, 1f, backgroundAlpha)

            backgroundLayers.add(backgroundImage)
        }

        return backgroundLayers
    }

    /**
     * 배경 레이어들을 Stage에 추가 (맨 뒤에)
     */
    fun addToStage(stage: com.badlogic.gdx.scenes.scene2d.Stage, screenWidth: Float, screenHeight: Float) {
        val backgrounds = createBackground(screenWidth, screenHeight)

        // 4번부터 1번까지 역순으로 추가 (1번이 맨 뒤, 4번이 상대적으로 앞에 오도록)
        backgrounds.reversed().forEach { background ->
            stage.addActor(background)
            background.toBack() // 각각을 맨 뒤로 보내서 UI 요소들보다 뒤에 오도록
        }
    }

    /**
     * 런타임에 배경 변경 (Stage용)
     */
    fun changeBackground(stage: com.badlogic.gdx.scenes.scene2d.Stage, newBackgroundName: String, screenWidth: Float, screenHeight: Float) {
        // 기존 배경 제거
        backgroundLayers.forEach { it.remove() }

        // 새 배경 설정
        setBackground(newBackgroundName)

        // 새 배경 추가
        addToStage(stage, screenWidth, screenHeight)
    }

    /**
     * 배경 위치 업데이트 (화면 크기 변경 시)
     */
    fun updateSize(screenWidth: Float, screenHeight: Float) {
        if (backgroundLayers.isEmpty()) return

        val scaleX = screenWidth / ORIGINAL_WIDTH
        val scaleY = screenHeight / ORIGINAL_HEIGHT
        val scale = maxOf(scaleX, scaleY)

        val scaledWidth = ORIGINAL_WIDTH * scale
        val scaledHeight = ORIGINAL_HEIGHT * scale

        val offsetX = (screenWidth - scaledWidth) / 2f
        val offsetY = (screenHeight - scaledHeight) / 2f

        backgroundLayers.forEach { background ->
            background.setSize(scaledWidth, scaledHeight)
            background.setPosition(offsetX, offsetY)
        }
    }

    /**
     * SpriteBatch로 배경 렌더링 (GameScreen용)
     */
    fun initForSpriteBatch(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        backgroundTextures.clear()
        val layers = ResourceManager.getBackgroundLayers(currentBackgroundName)

        // TextureRegion으로 변환
        layers.forEach { drawable ->
            backgroundTextures.add(drawable.region)
        }
    }

    /**
     * SpriteBatch로 배경 그리기
     */
    fun drawWithSpriteBatch(batch: SpriteBatch) {
        if (backgroundTextures.isEmpty()) return

        // 투명도 설정을 위한 색상 저장
        val originalColor = batch.color.cpy()

        // 배경 투명도 적용
        batch.setColor(1f, 1f, 1f, backgroundAlpha)

        // 화면에 맞게 스케일 계산 (비율 유지하면서 화면을 완전히 채우도록)
        val scaleX = screenWidth / ORIGINAL_WIDTH
        val scaleY = screenHeight / ORIGINAL_HEIGHT
        val scale = maxOf(scaleX, scaleY) // 더 큰 스케일을 사용해서 화면을 완전히 채움

        // 스케일 적용
        val scaledWidth = ORIGINAL_WIDTH * scale
        val scaledHeight = ORIGINAL_HEIGHT * scale

        // 중앙 정렬 (크롭 효과)
        val offsetX = (screenWidth - scaledWidth) / 2f
        val offsetY = (screenHeight - scaledHeight) / 2f

        // 1번부터 5번까지 순서대로 그리기 (1번이 맨 뒤에 그려짐)
        backgroundTextures.forEach { textureRegion ->
            batch.draw(
                textureRegion,
                offsetX,
                offsetY,
                scaledWidth,
                scaledHeight
            )
        }

        // 원래 색상으로 복원
        batch.color = originalColor
    }

    fun dispose() {
        backgroundLayers.clear()
        backgroundTextures.clear()
    }
}
