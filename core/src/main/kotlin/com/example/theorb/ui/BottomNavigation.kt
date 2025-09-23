package com.example.theorb.ui

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.example.theorb.TheOrb
import com.example.theorb.screens.BaseScreen
import com.example.theorb.screens.HomeScreen
import com.example.theorb.screens.UpgradeScreen
import com.example.theorb.util.ResourceManager

/**
 * 재사용 가능한 하단 네비게이션 메뉴
 */
class BottomNavigation(
    private val game: Game,
    private val skin: Skin,
    private val currentTab: Tab = Tab.MAIN
) {

    enum class Tab(val displayName: String) {
        SHOP("SHOP"),
        CARD("CARD"),
        MAIN("MAIN"),
        UPGRADE("UPGRADE"),
        SKILL("SKILL")
    }

    /**
     * 하단 네비게이션 Table을 생성합니다
     */
    fun createBottomNavigation(): Table {
        val bottom = Table().apply { pad(BaseScreen.COMPONENT_PADDING) }

        // 버튼 개수에 따른 최대 폭 계산
        val buttonCount = Tab.values().size
        val screenWidth = Gdx.graphics.width.toFloat()
        val availableWidth = screenWidth - (BaseScreen.COMPONENT_PADDING * 2)
        val maxButtonWidth = availableWidth / buttonCount
        val padding = 2f
        val buttonWidth = (maxButtonWidth - padding * 2).coerceAtMost(165f)

        Tab.values().forEach { tab ->
            val isSelected = tab == currentTab
            val button = ImageButton(ImageButton.ImageButtonStyle().apply {
                up = if (isSelected) {
                    ResourceManager.getNavButtonHighlightBg()
                } else {
                    ResourceManager.getNavButtonCancelBg()
                }
                down = ResourceManager.getNavButtonConfirmBg()
                over = ResourceManager.getNavButtonHighlightBg()
            })

            // 버튼에 텍스트 라벨 추가
            val buttonLabel = Label(tab.displayName, skin.get("label-small-bold", Label.LabelStyle::class.java)).apply {
                color = if (isSelected) Color.WHITE else BaseScreen.TEXT_SECONDARY
                setAlignment(Align.center)
            }
            button.add(buttonLabel)

            button.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    handleTabClick(tab)
                }
            })

            bottom.add(button).width(buttonWidth).height(42f).pad(padding)
        }

        return bottom
    }

    private fun handleTabClick(tab: Tab) {
        when (tab) {
            Tab.MAIN -> {
                if (currentTab != Tab.MAIN) {
                    Gdx.app.log("BottomNav", "메인 탭 클릭")
                    game.screen = HomeScreen(game)
                }
            }
            Tab.UPGRADE -> {
                if (currentTab != Tab.UPGRADE) {
                    Gdx.app.log("BottomNav", "업그레이드 탭 클릭")
                    game.screen = UpgradeScreen(game as TheOrb)
                }
            }
            Tab.SHOP -> {
                Gdx.app.log("BottomNav", "상점 탭 클릭 (미구현)")
                // 추후 상점 화면 구현
            }
            Tab.CARD -> {
                Gdx.app.log("BottomNav", "카드 탭 클릭 (미구현)")
                // 추후 카드 화면 구현
            }
            Tab.SKILL -> {
                Gdx.app.log("BottomNav", "스킬 탭 클릭 (미구현)")
                // 추후 스킬 화면 구현
            }
        }
    }
}
