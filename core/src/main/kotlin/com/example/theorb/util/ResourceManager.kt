package com.example.theorb.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.example.theorb.ui.BottomNavigation
import com.example.theorb.upgrades.UpgradeCategory

object ResourceManager {

    // 이미지 경로 상수들
    object Images {
        // v01 작업분 panel
        // common
        const val COMMON_TOP_PANEL = "images/v01/panel/Common_top.png"
        const val COMMON_MODAL_PANEL = "images/v01/panel/Common_modal_panel.png"
        const val COMMON_SMALL_MODAL_PANEL = "images/v01/panel/Common_small_modal_panel.png"
        const val HOME_MAIN_PANEL = "images/v01/panel/Home_main.png"
        const val HOME_ORB_SELECTION = "images/v01/panel/Home_orb_selection.png"
        const val HOME_ORB_SELECT = "images/v01/panel/Home_orb_select.png"
        const val SQUARE_BASE = "images/v01/panel/Square_base.png"
        const val SQUARE_EVENT = "images/v01/panel/Square_event.png"
        const val UPGRADE_BACK_PANEL = "images/v01/panel/Upgrade_back.png"
        const val UPGRADE_LIST_PANEL = "images/v01/panel/Upgrade_list.png"
        const val SKILL_EQUIP_PANEL = "images/v01/panel/Skill_equip.png"
        const val SKILL_INVENTORY_PANEL = "images/v01/panel/Skill_inventory.png"
        const val SKILL_SUB_DEC_PANEL = "images/v01/panel/Skill_sub_dec.png"
        const val GAME_STATUS_BACK_PANEL = "images/v01/panel/Game_status_back.png"
        const val LEVEL_UP_SELECTION_BASE_PANEL = "images/v01/panel/Level_up_selection_base.png"
        const val LEVEL_UP_SELECTION_EVENT_PANEL = "images/v01/panel/Level_up_selection_event.png"
        const val EXP_GRAPH_BACK_PANEL = "images/v01/panel/Exp_graph_back.png"
        const val EXP_GRAPH_FRONT_PANEL = "images/v01/panel/Exp_graph_front.png"
        const val HP_GRAPH_BACK_PANEL = "images/v01/panel/Hp_graph_back.png"
        const val HP_GRAPH_FRONT_PANEL = "images/v01/panel/Hp_graph_front.png"
        const val ESD_GRAPH_FRONT_PANEL = "images/v01/panel/Esd_graph_front.png"

        // v01 작업분 button
        // common
        const val GEAR_BASE_POS = "images/v01/button/gear/Gear_base_p.png"
        const val GEAR_EVENT_POS = "images/v01/button/gear/Gear_event_p.png"
        const val LEFT_BASE_POS = "images/v01/button/left/Left_base_p.png"
        const val LEFT_EVENT_POS = "images/v01/button/left/Left_event_p.png"
        const val LEFT_BASE_NAG = "images/v01/button/left/Left_base_n.png"
        const val RIGHT_BASE_POS = "images/v01/button/right/Right_base_p.png"
        const val RIGHT_EVENT_POS = "images/v01/button/right/Right_event_p.png"
        const val RIGHT_BASE_NAG = "images/v01/button/right/Right_base_n.png"
        const val CONFIRM_BASE_POS = "images/v01/button/confirm/Confirm_base_p.png"
        const val CONFIRM_EVENT_POS = "images/v01/button/confirm/Confirm_event_p.png"
        const val CONFIRM_BASE_NAG = "images/v01/button/confirm/Confirm_base_n.png"
        const val CANCEL_BASE_POS = "images/v01/button/cancel/Cancel_base_p.png"
        const val CANCEL_EVENT_POS = "images/v01/button/cancel/Cancel_event_p.png"
        const val CANCEL_BASE_NAG = "images/v01/button/cancel/Cancel_base_n.png"
        const val PLUS_BASE_POS = "images/v01/button/plus/Plus_base_p.png"
        const val PLUS_EVENT_POS = "images/v01/button/plus/Plus_event_p.png"
        const val PLUS_BASE_NAG = "images/v01/button/plus/Plus_base_n.png"
        const val PAUSE_BASE_POS = "images/v01/button/pause/Pause_base_p.png"
        const val PAUSE_EVENT_POS = "images/v01/button/pause/Pause_event_p.png"
        const val PAUSE_BASE_NAG = "images/v01/button/pause/Pause_base_n.png"
        const val SPEED_1X_BASE_POS = "images/v01/button/speed/Speed1x_base_p.png"
        const val SPEED_1X_EVENT_POS = "images/v01/button/speed/Speed1x_event_p.png"
        const val SPEED_1X_BASE_NAG = "images/v01/button/speed/Speed1x_base_n.png"
        const val SPEED_2X_BASE_POS = "images/v01/button/speed/Speed2x_base_p.png"
        const val SPEED_2X_EVENT_POS = "images/v01/button/speed/Speed2x_event_p.png"
        const val SPEED_2X_BASE_NAG = "images/v01/button/speed/Speed2x_base_n.png"
        const val SPEED_3X_BASE_POS = "images/v01/button/speed/Speed3x_base_p.png"
        const val SPEED_3X_EVENT_POS = "images/v01/button/speed/Speed3x_event_p.png"
        const val SPEED_3X_BASE_NAG = "images/v01/button/speed/Speed3x_base_n.png"

        const val SELECT_BASE_POS = "images/v01/button/select/Select_base_p.png"
        const val SELECT_EVENT_POS = "images/v01/button/select/Select_event_p.png"
        const val SELECT_BASE_NAG = "images/v01/button/select/Select_base_n.png"

        const val REROLL_BASE_POS = "images/v01/button/reroll/Reroll_base_p.png"
        const val REROLL_EVENT_POS = "images/v01/button/reroll/Reroll_event_p.png"
        const val REROLL_BASE_NAG = "images/v01/button/reroll/Reroll_base_n.png"

        const val HOME_BASE_POS = "images/v01/button/home/Home_base_p.png"
        const val HOME_EVENT_POS = "images/v01/button/home/Home_event_p.png"
        const val HOME_BASE_NAG = "images/v01/button/home/Home_base_n.png"

        const val PLAY_BASE_POS = "images/v01/button/play/Play_base_p.png"
        const val PLAY_EVENT_POS = "images/v01/button/play/Play_event_p.png"
        const val PLAY_BASE_NAG = "images/v01/button/play/Play_base_n.png"

        const val QUIT_BASE_POS = "images/v01/button/quit/Quit_base_p.png"
        const val QUIT_EVENT_POS = "images/v01/button/quit/Quit_event_p.png"
        const val QUIT_BASE_NAG = "images/v01/button/quit/Quit_base_n.png"
        const val QUIT_EVENT_NAG = "images/v01/button/quit/Quit_event_n.png"

        const val AGAIN_BASE_POS = "images/v01/button/again/Again_base_p.png"
        const val AGAIN_EVENT_POS = "images/v01/button/again/Again_event_p.png"
        const val AGAIN_BASE_NAG = "images/v01/button/again/Again_base_n.png"
        const val AGAIN_EVENT_NAG = "images/v01/button/again/Again_event_n.png"

        const val LIST_BASE_POS = "images/v01/button/list/List_base_p.png"
        const val LIST_EVENT_POS = "images/v01/button/list/List_event_p.png"
        const val LIST_BASE_NAG = "images/v01/button/list/List_base_n.png"

        // nav
        const val SHOP_BASE_POS = "images/v01/button/shop/Shop_base_p.png"
        const val SHOP_EVENT_POS = "images/v01/button/shop/Shop_event_p.png"
        const val SHOP_BASE_NAG = "images/v01/button/shop/Shop_base_n.png"
        const val SHOP_EVENT_NAG = "images/v01/button/shop/Shop_event_n.png"

        const val CARD_BASE_POS = "images/v01/button/card/Card_base_p.png"
        const val CARD_EVENT_POS = "images/v01/button/card/Card_event_p.png"
        const val CARD_BASE_NAG = "images/v01/button/card/Card_base_n.png"
        const val CARD_EVENT_NAG = "images/v01/button/card/Card_event_n.png"

        const val MAIN_BASE_POS = "images/v01/button/main/Main_base_p.png"
        const val MAIN_EVENT_POS = "images/v01/button/main/Main_event_p.png"
        const val MAIN_BASE_NAG = "images/v01/button/main/Main_base_n.png"
        const val MAIN_EVENT_NAG = "images/v01/button/main/Main_event_n.png"

        const val UPGRADE_BASE_POS = "images/v01/button/upgrade/Upgrade_base_p.png"
        const val UPGRADE_EVENT_POS = "images/v01/button/upgrade/Upgrade_event_p.png"
        const val UPGRADE_BASE_NAG = "images/v01/button/upgrade/Upgrade_base_n.png"
        const val UPGRADE_EVENT_NAG = "images/v01/button/upgrade/Upgrade_event_n.png"

        const val SKILL_BASE_POS = "images/v01/button/skill/Skill_base_p.png"
        const val SKILL_EVENT_POS = "images/v01/button/skill/Skill_event_p.png"
        const val SKILL_BASE_NAG = "images/v01/button/skill/Skill_base_n.png"
        const val SKILL_EVENT_NAG = "images/v01/button/skill/Skill_event_n.png"

        const val START_BASE_POS = "images/v01/button/start/Start_base_p.png"
        const val START_EVENT_POS = "images/v01/button/start/Start_event_p.png"
        const val START_BASE_NAG = "images/v01/button/start/Start_base_n.png"
        const val START_EVENT_NAG = "images/v01/button/start/Start_event_n.png"

        const val CLOSE_BASE_POS = "images/v01/button/close/Close_base_p.png"
        const val CLOSE_EVENT_POS = "images/v01/button/close/Close_event_p.png"
        const val CLOSE_BASE_NAG = "images/v01/button/close/Close_base_n.png"

        const val ATTACK_BASE_POS = "images/v01/button/attack/Attack_base_p.png"
        const val ATTACK_EVENT_POS = "images/v01/button/attack/Attack_event_p.png"
        const val ATTACK_BASE_NAG = "images/v01/button/attack/Attack_base_n.png"
        const val ATTACK_EVENT_NAG = "images/v01/button/attack/Attack_event_n.png"

        const val DEFENSE_BASE_POS = "images/v01/button/defense/Defense_base_p.png"
        const val DEFENSE_EVENT_POS = "images/v01/button/defense/Defense_event_p.png"
        const val DEFENSE_BASE_NAG = "images/v01/button/defense/Defense_base_n.png"
        const val DEFENSE_EVENT_NAG = "images/v01/button/defense/Defense_event_n.png"
        const val UTILITY_BASE_POS = "images/v01/button/utility/Utility_base_p.png"
        const val UTILITY_EVENT_POS = "images/v01/button/utility/Utility_event_p.png"
        const val UTILITY_BASE_NAG = "images/v01/button/utility/Utility_base_n.png"
        const val UTILITY_EVENT_NAG = "images/v01/button/utility/Utility_event_n.png"

        const val RESET_BASE_POS = "images/v01/button/reset/Reset_base_p.png"
        const val RESET_EVENT_POS = "images/v01/button/reset/Reset_event_p.png"
        const val RESET_BASE_NAG = "images/v01/button/reset/Reset_base_n.png"
        const val RESET_EVENT_NAG = "images/v01/button/reset/Reset_event_n.png"

        const val LV_UP_BASE_POS = "images/v01/button/levelup/Levelup_base_p.png"
        const val LV_UP_EVENT_POS = "images/v01/button/levelup/Levelup_event_p.png"
        const val LV_UP_BASE_NAG = "images/v01/button/levelup/Levelup_base_n.png"
        const val LV_UP_EVENT_NAG = "images/v01/button/levelup/Levelup_event_n.png"

        const val LV_UP_S_BASE_POS = "images/v01/button/levelup/Levelup_s_base_p.png"
        const val LV_UP_S_EVENT_POS = "images/v01/button/levelup/Levelup_s_event_p.png"
        const val LV_UP_S_BASE_NAG = "images/v01/button/levelup/Levelup_s_base_n.png"
        const val LV_UP_S_EVENT_NAG = "images/v01/button/levelup/Levelup_s_event_n.png"

        const val MAX_BASE_POS = "images/v01/button/max/Max_base_p.png"
        const val MAX_EVENT_POS = "images/v01/button/max/Max_event_p.png"
        const val MAX_BASE_NAG = "images/v01/button/max/Max_base_n.png"
        const val MAX_EVENT_NAG = "images/v01/button/max/Max_event_n.png"

        const val ACTIVE_BASE_POS = "images/v01/button/active/Active_base_p.png"
        const val ACTIVE_EVENT_POS = "images/v01/button/active/Active_event_p.png"
        const val ACTIVE_BASE_NAG = "images/v01/button/active/Active_base_n.png"
        const val ACTIVE_EVENT_NAG = "images/v01/button/active/Active_event_n.png"


        const val SUB_BASE_POS = "images/v01/button/sub/Sub_base_p.png"
        const val SUB_EVENT_POS = "images/v01/button/sub/Sub_event_p.png"
        const val SUB_BASE_NAG = "images/v01/button/sub/Sub_base_n.png"
        const val SUB_EVENT_NAG = "images/v01/button/sub/Sub_event_n.png"

        const val EQUIP_BASE_POS = "images/v01/button/equip/Equip_base_p.png"
        const val EQUIP_EVENT_POS = "images/v01/button/equip/Equip_event_p.png"
        const val EQUIP_BASE_NAG = "images/v01/button/equip/Equip_base_n.png"
        const val EQUIP_EVENT_NAG = "images/v01/button/equip/Equip_event_n.png"

        const val UNEQUIP_BASE_POS = "images/v01/button/unequip/Unequip_base_p.png"
        const val UNEQUIP_EVENT_POS = "images/v01/button/unequip/Unequip_event_p.png"
        const val UNEQUIP_BASE_NAG = "images/v01/button/unequip/Unequip_base_n.png"
        const val UNEQUIP_EVENT_NAG = "images/v01/button/unequip/Unequip_event_n.png"

        // v01 작업분 skill icon
        const val FIREBALL_BASE = "images/v01/skill/fireball/fireball_base.png"
        const val FIREBALL_EVENT = "images/v01/skill/fireball/fireball_event.png"

        // 배경 이미지들 - clouds01
        const val BACKGROUND_CLOUDS01_1 = "images/background/clouds01/1.png"
        const val BACKGROUND_CLOUDS01_2 = "images/background/clouds01/2.png"
        const val BACKGROUND_CLOUDS01_3 = "images/background/clouds01/3.png"
        const val BACKGROUND_CLOUDS01_4 = "images/background/clouds01/4.png"
        const val BACKGROUND_CLOUDS01_5 = "images/background/clouds01/5.png"

        // 배경 이미지들 - clouds02
        const val BACKGROUND_CLOUDS02_1 = "images/background/clouds02/1.png"
        const val BACKGROUND_CLOUDS02_2 = "images/background/clouds02/2.png"
        const val BACKGROUND_CLOUDS02_3 = "images/background/clouds02/3.png"
        const val BACKGROUND_CLOUDS02_4 = "images/background/clouds02/4.png"

        // 배경 이미지들 - city01, city02, city03
        const val BACKGROUND_CITY01_1 = "images/background/city01/1.png"
        const val BACKGROUND_CITY02_1 = "images/background/city02/1.png"
        const val BACKGROUND_CITY03_1 = "images/background/city03/1.png"
    }

    // 텍스처 캐시
    private val textureCache = mutableMapOf<String, Texture>()

    /**
     * 텍스처를 로드하고 캐시에 저장
     */
    fun getTexture(path: String): Texture {
        return textureCache.getOrPut(path) {
            Texture(Gdx.files.internal(path))
        }
    }

    /**
     * TextureRegionDrawable을 반환
     */
    fun getDrawable(path: String): TextureRegionDrawable {
        return TextureRegionDrawable(getTexture(path))
    }

    // v01 작업분 panel
    // home screen
    fun getCommonTopPanel(): TextureRegionDrawable = getDrawable(Images.COMMON_TOP_PANEL)
    fun getCommonModalPanel(): TextureRegionDrawable = getDrawable(Images.COMMON_MODAL_PANEL)
    fun getCommonSmallModalPanel(): TextureRegionDrawable = getDrawable(Images.COMMON_SMALL_MODAL_PANEL)
    fun getHomeMainPanel(): TextureRegionDrawable = getDrawable(Images.HOME_MAIN_PANEL)
    fun getHomeOrbSelectionPanel(): TextureRegionDrawable = getDrawable(Images.HOME_ORB_SELECTION)

    fun getHomeOrbSelectPanel(): TextureRegionDrawable = getDrawable(Images.HOME_ORB_SELECT)
    fun getSquareBasePanel(): TextureRegionDrawable = getDrawable(Images.SQUARE_BASE)
    fun getSquareEventPanel(): TextureRegionDrawable = getDrawable(Images.SQUARE_EVENT)
    fun getUpgradeBackPanel(): TextureRegionDrawable = getDrawable(Images.UPGRADE_BACK_PANEL)
    fun getUpgradeListPanel(): TextureRegionDrawable = getDrawable(Images.UPGRADE_LIST_PANEL)
    fun getSkillEquipPanel(): TextureRegionDrawable = getDrawable(Images.SKILL_EQUIP_PANEL)
    fun getSkillInventoryPanel(): TextureRegionDrawable = getDrawable(Images.SKILL_INVENTORY_PANEL)
    fun getSkillSubDecPanel(): TextureRegionDrawable = getDrawable(Images.SKILL_SUB_DEC_PANEL)
    fun getGameStatusBackPanel(): TextureRegionDrawable = getDrawable(Images.GAME_STATUS_BACK_PANEL)
    fun getLevelUpSelectionBasePanel(): TextureRegionDrawable = getDrawable(Images.LEVEL_UP_SELECTION_BASE_PANEL)
    fun getLevelUpSelectionEventPanel(): TextureRegionDrawable = getDrawable(Images.LEVEL_UP_SELECTION_EVENT_PANEL)
    fun getExpGraphBackPanel(): TextureRegionDrawable = getDrawable(Images.EXP_GRAPH_BACK_PANEL)
    fun getExpGraphFrontPanel(): TextureRegionDrawable = getDrawable(Images.EXP_GRAPH_FRONT_PANEL)
    fun getHpGraphBackPanel(): TextureRegionDrawable = getDrawable(Images.HP_GRAPH_BACK_PANEL)
    fun getHpGraphFrontPanel(): TextureRegionDrawable = getDrawable(Images.HP_GRAPH_FRONT_PANEL)
    fun getEsdGraphFrontPanel(): TextureRegionDrawable = getDrawable(Images.ESD_GRAPH_FRONT_PANEL)




    // v01 작업분 button
    // home screen
    fun getGearBasePos(): TextureRegionDrawable = getDrawable(Images.GEAR_BASE_POS)
    fun getGearEventPos(): TextureRegionDrawable = getDrawable(Images.GEAR_EVENT_POS)
    fun getLeftBasePos(): TextureRegionDrawable = getDrawable(Images.LEFT_BASE_POS)
    fun getLeftEventPos(): TextureRegionDrawable = getDrawable(Images.LEFT_EVENT_POS)
    fun getLeftBaseNag(): TextureRegionDrawable = getDrawable(Images.LEFT_BASE_NAG)
    fun getRightBasePos(): TextureRegionDrawable = getDrawable(Images.RIGHT_BASE_POS)
    fun getRightEventPos(): TextureRegionDrawable = getDrawable(Images.RIGHT_EVENT_POS)
    fun getRightBaseNag(): TextureRegionDrawable = getDrawable(Images.RIGHT_BASE_NAG)
    fun getConfirmBasePos(): TextureRegionDrawable = getDrawable(Images.CONFIRM_BASE_POS)
    fun getConfirmEventPos(): TextureRegionDrawable = getDrawable(Images.CONFIRM_EVENT_POS)
    fun getConfirmBaseNag(): TextureRegionDrawable = getDrawable(Images.CONFIRM_BASE_NAG)

    fun getCancelBasePos(): TextureRegionDrawable = getDrawable(Images.CANCEL_BASE_POS)
    fun getCancelEventPos(): TextureRegionDrawable = getDrawable(Images.CANCEL_EVENT_POS)
    fun getCancelBaseNag(): TextureRegionDrawable = getDrawable(Images.CANCEL_BASE_NAG)
    fun getPlusBasePos(): TextureRegionDrawable = getDrawable(Images.PLUS_BASE_POS)
    fun getPlusEventPos(): TextureRegionDrawable = getDrawable(Images.PLUS_EVENT_POS)
    fun getPlusBaseNag(): TextureRegionDrawable = getDrawable(Images.PLUS_BASE_NAG)

    fun getPauseBasePos(): TextureRegionDrawable = getDrawable(Images.PAUSE_BASE_POS)
    fun getPauseEventPos(): TextureRegionDrawable = getDrawable(Images.PAUSE_EVENT_POS)
    fun getPauseBaseNag(): TextureRegionDrawable = getDrawable(Images.PAUSE_BASE_NAG)

    fun getSpeedBasePos(speed: Float): TextureRegionDrawable = when (speed) {
        1.0f -> getDrawable(Images.SPEED_1X_BASE_POS)
        2.0f -> getDrawable(Images.SPEED_2X_BASE_POS)
        3.0f -> getDrawable(Images.SPEED_3X_BASE_POS)
        else -> {getDrawable(Images.SPEED_1X_BASE_POS)}
    }

    fun getSpeedEventPos(speed: Float): TextureRegionDrawable = when (speed) {
        1.0f -> getDrawable(Images.SPEED_1X_EVENT_POS)
        2.0f -> getDrawable(Images.SPEED_2X_EVENT_POS)
        3.0f -> getDrawable(Images.SPEED_3X_EVENT_POS)
        else -> {getDrawable(Images.SPEED_1X_EVENT_POS)}
    }

    fun getSpeedBaseNag(speed: Float): TextureRegionDrawable = when (speed) {
        1.0f -> getDrawable(Images.SPEED_1X_BASE_NAG)
        2.0f -> getDrawable(Images.SPEED_2X_BASE_NAG)
        3.0f -> getDrawable(Images.SPEED_3X_BASE_NAG)
        else -> {getDrawable(Images.SPEED_1X_BASE_NAG)}
    }

    fun getNavBasePos(tab: BottomNavigation.Tab): TextureRegionDrawable = when (tab) {
        BottomNavigation.Tab.SHOP -> getDrawable(Images.SHOP_BASE_POS)
        BottomNavigation.Tab.CARD -> getDrawable(Images.CARD_BASE_POS)
        BottomNavigation.Tab.MAIN -> getDrawable(Images.MAIN_BASE_POS)
        BottomNavigation.Tab.UPGRADE -> getDrawable(Images.UPGRADE_BASE_POS)
        BottomNavigation.Tab.SKILL -> getDrawable(Images.SKILL_BASE_POS)
    }
    fun getNavEventPos(tab: BottomNavigation.Tab): TextureRegionDrawable = when (tab) {
        BottomNavigation.Tab.SHOP -> getDrawable(Images.SHOP_EVENT_POS)
        BottomNavigation.Tab.CARD -> getDrawable(Images.CARD_EVENT_POS)
        BottomNavigation.Tab.MAIN -> getDrawable(Images.MAIN_EVENT_POS)
        BottomNavigation.Tab.UPGRADE -> getDrawable(Images.UPGRADE_EVENT_POS)
        BottomNavigation.Tab.SKILL -> getDrawable(Images.SKILL_EVENT_POS)
    }
    fun getNavBaseNag(tab: BottomNavigation.Tab): TextureRegionDrawable = when (tab) {
        BottomNavigation.Tab.SHOP -> getDrawable(Images.SHOP_BASE_NAG)
        BottomNavigation.Tab.CARD -> getDrawable(Images.CARD_BASE_NAG)
        BottomNavigation.Tab.MAIN -> getDrawable(Images.MAIN_BASE_NAG)
        BottomNavigation.Tab.UPGRADE -> getDrawable(Images.UPGRADE_BASE_NAG)
        BottomNavigation.Tab.SKILL -> getDrawable(Images.SKILL_BASE_NAG)
    }
    fun getNavEventNag(tab: BottomNavigation.Tab): TextureRegionDrawable = when (tab) {
        BottomNavigation.Tab.SHOP -> getDrawable(Images.SHOP_EVENT_NAG)
        BottomNavigation.Tab.CARD -> getDrawable(Images.CARD_EVENT_NAG)
        BottomNavigation.Tab.MAIN -> getDrawable(Images.MAIN_EVENT_NAG)
        BottomNavigation.Tab.UPGRADE -> getDrawable(Images.UPGRADE_EVENT_NAG)
        BottomNavigation.Tab.SKILL -> getDrawable(Images.SKILL_EVENT_NAG)
    }

    fun getStartBasePos(): TextureRegionDrawable = getDrawable(Images.START_BASE_POS)
    fun getStartEventPos(): TextureRegionDrawable = getDrawable(Images.START_EVENT_POS)
    fun getStartBaseNag(): TextureRegionDrawable = getDrawable(Images.START_BASE_NAG)
    fun getStartEventNag(): TextureRegionDrawable = getDrawable(Images.START_EVENT_NAG)

    fun getCloseBasePos(): TextureRegionDrawable = getDrawable(Images.CLOSE_BASE_POS)
    fun getCloseEventPos(): TextureRegionDrawable = getDrawable(Images.CLOSE_EVENT_POS)
    fun getCloseBaseNag(): TextureRegionDrawable = getDrawable(Images.CLOSE_BASE_NAG)

    fun getUpgradeTabBasePos(type: UpgradeCategory): TextureRegionDrawable = when (type) {
        UpgradeCategory.ATTACK -> getDrawable(Images.ATTACK_BASE_POS)
        UpgradeCategory.DEFENSE -> getDrawable(Images.DEFENSE_BASE_POS)
        UpgradeCategory.UTILITY -> getDrawable(Images.UTILITY_BASE_POS)
    }
    fun getUpgradeTabEventPos(type: UpgradeCategory): TextureRegionDrawable = when (type) {
        UpgradeCategory.ATTACK -> getDrawable(Images.ATTACK_EVENT_POS)
        UpgradeCategory.DEFENSE -> getDrawable(Images.DEFENSE_EVENT_POS)
        UpgradeCategory.UTILITY -> getDrawable(Images.UTILITY_EVENT_POS)
    }
    fun getUpgradeTabBaseNag(type: UpgradeCategory): TextureRegionDrawable = when (type) {
        UpgradeCategory.ATTACK -> getDrawable(Images.ATTACK_BASE_NAG)
        UpgradeCategory.DEFENSE -> getDrawable(Images.DEFENSE_BASE_NAG)
        UpgradeCategory.UTILITY -> getDrawable(Images.UTILITY_BASE_NAG)
    }
    fun getUpgradeTabEventNag(type: UpgradeCategory): TextureRegionDrawable = when (type) {
        UpgradeCategory.ATTACK -> getDrawable(Images.ATTACK_EVENT_NAG)
        UpgradeCategory.DEFENSE -> getDrawable(Images.DEFENSE_EVENT_NAG)
        UpgradeCategory.UTILITY -> getDrawable(Images.UTILITY_EVENT_NAG)
    }

    fun getResetBasePos(): TextureRegionDrawable = getDrawable(Images.RESET_BASE_POS)
    fun getResetEventPos(): TextureRegionDrawable = getDrawable(Images.RESET_EVENT_POS)
    fun getResetBaseNag(): TextureRegionDrawable = getDrawable(Images.RESET_BASE_NAG)
    fun getResetEventNag(): TextureRegionDrawable = getDrawable(Images.RESET_EVENT_NAG)

    fun getLvUpBasePos(): TextureRegionDrawable = getDrawable(Images.LV_UP_BASE_POS)
    fun getLvUpEventPos(): TextureRegionDrawable = getDrawable(Images.LV_UP_EVENT_POS)
    fun getLvUpBaseNag(): TextureRegionDrawable = getDrawable(Images.LV_UP_BASE_NAG)
    fun getLvUpEventNag(): TextureRegionDrawable = getDrawable(Images.LV_UP_EVENT_NAG)

    fun getActiveBasePos(): TextureRegionDrawable = getDrawable(Images.ACTIVE_BASE_POS)
    fun getActiveEventPos(): TextureRegionDrawable = getDrawable(Images.ACTIVE_EVENT_POS)
    fun getActiveBaseNag(): TextureRegionDrawable = getDrawable(Images.ACTIVE_BASE_NAG)
    fun getActiveEventNag(): TextureRegionDrawable = getDrawable(Images.ACTIVE_EVENT_NAG)

    fun getSubBasePos(): TextureRegionDrawable = getDrawable(Images.SUB_BASE_POS)
    fun getSubEventPos(): TextureRegionDrawable = getDrawable(Images.SUB_EVENT_POS)
    fun getSubBaseNag(): TextureRegionDrawable = getDrawable(Images.SUB_BASE_NAG)
    fun getSubEventNag(): TextureRegionDrawable = getDrawable(Images.SUB_EVENT_NAG)

    fun getEquipBasePos(): TextureRegionDrawable = getDrawable(Images.EQUIP_BASE_POS)
    fun getEquipEventPos(): TextureRegionDrawable = getDrawable(Images.EQUIP_EVENT_POS)
    fun getEquipBaseNag(): TextureRegionDrawable = getDrawable(Images.EQUIP_BASE_NAG)
    fun getEquipEventNag(): TextureRegionDrawable = getDrawable(Images.EQUIP_EVENT_NAG)

    fun getUnequipBasePos(): TextureRegionDrawable = getDrawable(Images.UNEQUIP_BASE_POS)
    fun getUnequipEventPos(): TextureRegionDrawable = getDrawable(Images.UNEQUIP_EVENT_POS)
    fun getUnequipBaseNag(): TextureRegionDrawable = getDrawable(Images.UNEQUIP_BASE_NAG)
    fun getUnequipEventNag(): TextureRegionDrawable = getDrawable(Images.UNEQUIP_EVENT_NAG)

    fun getSelectBasePos(): TextureRegionDrawable = getDrawable(Images.SELECT_BASE_POS)
    fun getSelectEventPos(): TextureRegionDrawable = getDrawable(Images.SELECT_EVENT_POS)
    fun getSelectBaseNag(): TextureRegionDrawable = getDrawable(Images.SELECT_BASE_NAG)

    fun getRerollBasePos(): TextureRegionDrawable = getDrawable(Images.REROLL_BASE_POS)
    fun getRerollEventPos(): TextureRegionDrawable = getDrawable(Images.REROLL_EVENT_POS)
    fun getRerollBaseNag(): TextureRegionDrawable = getDrawable(Images.REROLL_BASE_NAG)

    fun getHomeBasePos(): TextureRegionDrawable = getDrawable(Images.HOME_BASE_POS)
    fun getHomeEventPos(): TextureRegionDrawable = getDrawable(Images.HOME_EVENT_POS)
    fun getHomeBaseNag(): TextureRegionDrawable = getDrawable(Images.HOME_BASE_NAG)

    fun getPlayBasePos(): TextureRegionDrawable = getDrawable(Images.PLAY_BASE_POS)
    fun getPlayEventPos(): TextureRegionDrawable = getDrawable(Images.PLAY_EVENT_POS)
    fun getPlayBaseNag(): TextureRegionDrawable = getDrawable(Images.PLAY_BASE_NAG)

    fun getQuitBasePos(): TextureRegionDrawable = getDrawable(Images.QUIT_BASE_POS)
    fun getQuitEventPos(): TextureRegionDrawable = getDrawable(Images.QUIT_EVENT_POS)
    fun getQuitBaseNag(): TextureRegionDrawable = getDrawable(Images.QUIT_BASE_NAG)
    fun getQuitEventNag(): TextureRegionDrawable = getDrawable(Images.QUIT_EVENT_NAG)

    fun getAgainBasePos(): TextureRegionDrawable = getDrawable(Images.AGAIN_BASE_POS)
    fun getAgainEventPos(): TextureRegionDrawable = getDrawable(Images.AGAIN_EVENT_POS)
    fun getAgainBaseNag(): TextureRegionDrawable = getDrawable(Images.AGAIN_BASE_NAG)
    fun getAgainEventNag(): TextureRegionDrawable = getDrawable(Images.AGAIN_EVENT_NAG)

    fun getListBasePos(): TextureRegionDrawable = getDrawable(Images.LIST_BASE_POS)
    fun getListEventPos(): TextureRegionDrawable = getDrawable(Images.LIST_EVENT_POS)
    fun getListBaseNag(): TextureRegionDrawable = getDrawable(Images.LIST_BASE_NAG)

    // v01 작업분 skill icon
    fun getFireballBase(): TextureRegionDrawable = getDrawable(Images.FIREBALL_BASE)
    fun getFireballEvent(): TextureRegionDrawable = getDrawable(Images.FIREBALL_EVENT)

    // 배경 이미지들을 위한 편의 메소드 - clouds01
    fun getBackgroundClouds01Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_1)
    fun getBackgroundClouds01Layer2(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_2)
    fun getBackgroundClouds01Layer3(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_3)
    fun getBackgroundClouds01Layer4(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_4)
    fun getBackgroundClouds01Layer5(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_5)

    // 배경 이미지들을 위한 편의 메소드 - clouds02
    fun getBackgroundClouds02Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_1)
    fun getBackgroundClouds02Layer2(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_2)
    fun getBackgroundClouds02Layer3(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_3)
    fun getBackgroundClouds02Layer4(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_4)

    // 배경 이미지들을 위한 편의 메소드 - city 배경들
    fun getBackgroundCity01Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY01_1)
    fun getBackgroundCity02Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY02_1)
    fun getBackgroundCity03Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY03_1)

    /**
     * 배경 레이어들을 순서대로 반환
     */
    fun getBackgroundClouds01Layers(): List<TextureRegionDrawable> {
        return listOf(
            getBackgroundClouds01Layer1(),
            getBackgroundClouds01Layer2(),
            getBackgroundClouds01Layer3(),
            getBackgroundClouds01Layer4(),
            getBackgroundClouds01Layer5()
        )
    }

    fun getBackgroundClouds02Layers(): List<TextureRegionDrawable> {
        return listOf(
            getBackgroundClouds02Layer1(),
            getBackgroundClouds02Layer2(),
            getBackgroundClouds02Layer3(),
            getBackgroundClouds02Layer4()
        )
    }

    fun getBackgroundCity01Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity01Layer1())
    }

    fun getBackgroundCity02Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity02Layer1())
    }

    fun getBackgroundCity03Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity03Layer1())
    }

    /**
     * 배경 이름에 따라 해당 배경의 레이어들을 반환
     */
    fun getBackgroundLayers(backgroundName: String): List<TextureRegionDrawable> {
        return when (backgroundName) {
            "clouds01" -> getBackgroundClouds01Layers()
            "clouds02" -> getBackgroundClouds02Layers()
            "city01" -> getBackgroundCity01Layers()
            "city02" -> getBackgroundCity02Layers()
            "city03" -> getBackgroundCity03Layers()
            else -> getBackgroundClouds01Layers() // 기본값
        }
    }

    /**
     * 모든 캐시된 텍스처 해제 (게임 종료 시 호출)
     */
    fun dispose() {
        textureCache.values.forEach { it.dispose() }
        textureCache.clear()
        Gdx.app.log("ResourceManager", "All textures disposed")
    }

    /**
     * 특정 텍스처 해제
     */
    fun disposeTexture(path: String) {
        textureCache[path]?.let { texture ->
            texture.dispose()
            textureCache.remove(path)
            Gdx.app.log("ResourceManager", "Texture disposed: $path")
        }
    }

    /**
     * 캐시 상태 확인 (디버깅용)
     */
    fun getCacheInfo(): String {
        return "Cached textures: ${textureCache.size} - ${textureCache.keys.joinToString(", ")}"
    }
}
