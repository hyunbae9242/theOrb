package com.example.theorb.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.util.ResourceManager

/**
 * Retro 스타일 버튼을 위한 유틸리티 클래스
 * 텍스트가 있는 버튼의 경우 상태에 따라 텍스트 위치를 자동으로 조정합니다.
 */
object RetroButtonV01 {

    /**
     * 이미지와 배경이 있는 Retro 버튼을 생성합니다 (오브 선택 등에 사용).
     * @param image 표시할 이미지
     * @param imageSize 이미지 크기
     * @param defaultImage 기본 상태 배경 이미지
     * @param eventImage 이벤트 상태 배경 이미지 (hover, down)
     * @param buttonSize 버튼 크기
     * @param onClick 클릭 이벤트 핸들러
     * @return 생성된 Stack (ImageButton + Image)
     */
    fun createImageButton(
        image: TextureRegionDrawable,
        imageSize: Float = 48f,
        defaultImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosDefault(),
        eventImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosEvent(),
        buttonSize: Float = 48f,
        onClick: () -> Unit
    ): Stack {

        // 배경 ImageButton 생성
        val backgroundButton = ImageButton(ImageButton.ImageButtonStyle().apply {
            up = defaultImage
            down = eventImage
            over = eventImage
        }).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onClick()
                }
            })
        }

        // 표시할 이미지 생성
        val contentImage = Image(image).apply {
            setSize(imageSize, imageSize)
            touchable = Touchable.disabled // 터치 이벤트가 하위로 전달되도록
        }

        // Stack 생성
        return object : Stack() {
            init {
                add(backgroundButton)
                add(contentImage)

                // Stack 전체가 터치 가능하도록 설정
                touchable = Touchable.childrenOnly
            }

            // 스택이 레이아웃될 때마다 이미지를 중앙에 배치
            override fun layout() {
                super.layout()
                if (width > 0 && height > 0) {
                    // ImageButton의 크기를 Stack 전체 크기로 설정 (터치 영역 확장)
                    backgroundButton.setSize(width, height)

                    // 이미지를 중앙에 배치
                    val centerX = width / 2f - contentImage.width / 2f
                    val centerY = height / 2f - contentImage.height / 2f
                    contentImage.setPosition(centerX, centerY)
                }
            }
        }
    }

    /**
     * @param defaultImage 기본 상태 이미지
     * @param eventImage 이벤트 상태 이미지 (hover, down)
     * @param disabledImage 비활성화 상태 이미지
     * @param isEnabled 활성화 여부
     * @param onClick 클릭 이벤트 핸들러
     * @return 생성된 ImageButton
     */
    fun createIconButton(
        defaultImage: TextureRegionDrawable,
        eventImage: TextureRegionDrawable,
        disabledImage: TextureRegionDrawable? = null,
        isEnabled: Boolean = true,
        onClick: () -> Unit
    ): ImageButton {
        return ImageButton(ImageButton.ImageButtonStyle().apply {
            up = if (isEnabled) defaultImage else (disabledImage ?: defaultImage)
            down = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
            over = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
            disabled = disabledImage ?: defaultImage
        }).apply {
            this.isDisabled = !isEnabled
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    if (isEnabled) {
                        onClick()
                    }
                }
            })
        }
    }

    fun getNavButton(tab: BottomNavigation.Tab, isPos: Boolean = true, isEnabled: Boolean = true, onClick: () -> Unit): ImageButton {
        return ImageButton(ImageButton.ImageButtonStyle().apply {
            up = if (isPos) ResourceManager.getNavBasePos(tab) else ResourceManager.getNavBaseNag(tab)
            down = if (isPos) ResourceManager.getNavEventPos(tab) else ResourceManager.getNavEventNag(tab)
            over = if (isPos) ResourceManager.getNavEventPos(tab) else ResourceManager.getNavEventNag(tab)
        }).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    if (isEnabled) {
                        onClick()
                    }
                }
            })
        }
    }

    /**
     * 아이콘 버튼의 활성화 상태를 업데이트합니다.
     * @param button 업데이트할 ImageButton
     * @param isEnabled 활성화 여부
     * @param defaultImage 기본 상태 이미지
     * @param eventImage 이벤트 상태 이미지
     * @param disabledImage 비활성화 상태 이미지
     */
    fun updateIconButtonEnabled(
        button: ImageButton,
        isEnabled: Boolean,
        defaultImage: TextureRegionDrawable,
        eventImage: TextureRegionDrawable,
        disabledImage: TextureRegionDrawable? = null
    ) {
        button.isDisabled = !isEnabled
        button.style.up = if (isEnabled) defaultImage else (disabledImage ?: defaultImage)
        button.style.down = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
        button.style.over = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
        button.style.disabled = disabledImage ?: defaultImage
    }

}
