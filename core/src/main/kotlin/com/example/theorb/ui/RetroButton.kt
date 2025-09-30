package com.example.theorb.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.example.theorb.screens.BaseScreen
import com.example.theorb.util.ResourceManager

/**
 * Retro 스타일 버튼을 위한 유틸리티 클래스
 * 텍스트가 있는 버튼의 경우 상태에 따라 텍스트 위치를 자동으로 조정합니다.
 */
object RetroButton {

    /**
     * 텍스트가 있는 Retro 버튼을 생성합니다.
     * @param text 버튼에 표시할 텍스트
     * @param skin UI 스킨
     * @param labelStyle 라벨 스타일 이름 (예: "label-default-bold")
     * @param textColor 텍스트 색상
     * @param defaultImage 기본 상태 이미지
     * @param eventImage 이벤트 상태 이미지 (hover, down)
     * @param buttonSize 버튼 크기 (정사각형 기준, 높이)
     * @param onClick 클릭 이벤트 핸들러
     * @return 생성된 Stack (ImageButton + Label)
     */
    fun createTextButton(
        text: String,
        skin: Skin,
        labelStyle: String = "label-default-bold",
        textColor: Color = BaseScreen.TEXT_PRIMARY,
        defaultImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosDefault(),
        eventImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosEvent(),
        disabledImage: TextureRegionDrawable? = null,
        buttonSize: Float = 42f,
        isEnabled: Boolean = true,
        onClick: () -> Unit
    ): Stack {

        // 버튼 크기에 따른 오프셋 계산 (14px당 1px 비율)
        val upOffset = buttonSize / 14f
        val downOffset = 0f

        // ImageButton 생성
        val imageButton = ImageButton(ImageButton.ImageButtonStyle().apply {
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

        // 텍스트 라벨 생성
        val textLabel = Label(text, skin.get(labelStyle, Label.LabelStyle::class.java)).apply {
            color = if (isEnabled) textColor else BaseScreen.TEXT_DISABLED
            touchable = Touchable.disabled // 터치 이벤트가 하위로 전달되도록
            setAlignment(Align.center) // 텍스트 중앙 정렬
        }

        // Stack 생성 및 상태 변화 리스너 추가
        return object : Stack() {
            init {
                add(imageButton)
                add(textLabel)

                // Stack 전체가 터치 가능하도록 설정 (하지만 이벤트는 하위로 전달)
                touchable = Touchable.childrenOnly
            }

            // 초기 위치 설정 함수
            fun updateTextPosition(offset: Float) {
                // Stack의 중앙에서 offset만큼 위로 이동
                val stackCenterX = width / 2f
                val stackCenterY = height / 2f
                textLabel.setPosition(
                    stackCenterX - textLabel.width / 2f,
                    stackCenterY - textLabel.height / 2f + offset
                )
            }

            init {
                // 레이아웃 완료 후 초기 위치 설정
                addListener(object : InputListener() {
                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                        if (!isEnabled) return
                        // 처음 enter 시에만 기본 위치 설정
                        if (textLabel.x == 0f && textLabel.y == 0f) {
                            updateTextPosition(upOffset)
                        }
                        // 호버 상태
                        updateTextPosition(downOffset)
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                        if (!isEnabled) return
                        // 기본 상태
                        updateTextPosition(upOffset)
                    }

                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        if (!isEnabled) return false
                        // 클릭 상태
                        updateTextPosition(downOffset)
                        return false
                    }

                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        if (!isEnabled) return
                        // 기본 상태로 복귀
                        updateTextPosition(upOffset)
                    }
                })
            }

            // 스택이 레이아웃될 때마다 기본 위치로 설정
            override fun layout() {
                super.layout()
                if (width > 0 && height > 0) {
                    // ImageButton의 크기를 Stack 전체 크기로 설정 (터치 영역 확장)
                    imageButton.setSize(width, height)
                    updateTextPosition(upOffset)
                }
            }
        }
    }

    /**
     * 아이콘만 있는 Retro 버튼을 생성합니다.
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
        imageSize: Float = 45f,
        defaultImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosDefault(),
        eventImage: TextureRegionDrawable = ResourceManager.getRetroSquarePosEvent(),
        buttonSize: Float = 90f,
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
     * 텍스트만 업데이트하는 헬퍼 메소드
     * Stack 내부의 Label을 찾아서 텍스트를 업데이트합니다.
     */
    fun updateText(stack: Stack, newText: String) {
        stack.children.forEach { child ->
            if (child is Label) {
                child.setText(newText)
                return
            }
        }
    }

    /**
     * 텍스트 버튼의 활성화 상태를 업데이트합니다.
     * @param stack 업데이트할 텍스트 버튼 Stack
     * @param isEnabled 활성화 여부
     * @param defaultImage 기본 상태 이미지
     * @param eventImage 이벤트 상태 이미지
     * @param disabledImage 비활성화 상태 이미지
     * @param textColor 텍스트 색상
     */
    fun updateTextButtonEnabled(
        stack: Stack,
        isEnabled: Boolean,
        defaultImage: TextureRegionDrawable,
        eventImage: TextureRegionDrawable,
        disabledImage: TextureRegionDrawable? = null,
        textColor: Color = BaseScreen.TEXT_PRIMARY
    ) {
        var imageButton: ImageButton? = null
        var textLabel: Label? = null

        stack.children.forEach { child ->
            when (child) {
                is ImageButton -> imageButton = child
                is Label -> textLabel = child
            }
        }

        imageButton?.let { button ->
            button.isDisabled = !isEnabled
            button.style.up = if (isEnabled) defaultImage else (disabledImage ?: defaultImage)
            button.style.down = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
            button.style.over = if (isEnabled) eventImage else (disabledImage ?: defaultImage)
            button.style.disabled = disabledImage ?: defaultImage
        }

        textLabel?.let { label ->
            label.color = if (isEnabled) textColor else BaseScreen.TEXT_DISABLED
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

    /**
     * 텍스트 버튼의 라벨 스타일을 업데이트합니다.
     * @param stack 업데이트할 텍스트 버튼 Stack
     * @param skin UI 스킨
     * @param labelStyle 새로운 라벨 스타일
     * @param textColor 새로운 텍스트 색상
     */
    fun updateTextButtonStyle(
        stack: Stack,
        skin: Skin,
        labelStyle: String,
        textColor: Color
    ) {
        stack.children.forEach { child ->
            if (child is Label) {
                child.style = skin.get(labelStyle, Label.LabelStyle::class.java)
                child.color = textColor
                return
            }
        }
    }
}
