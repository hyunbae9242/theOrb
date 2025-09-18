package com.example.theorb.effects

enum class EffectType(val path: String, val row: Int, val rows: Int, val cols: Int, val fps: Float, val scale: Float) {
    //common

    // skills
    // fireball
    FIREBALL_HIT("effects/skills/fireball/fireball_hit.png", 7, 9, 12, 24f, 1.3f),
    FIREBALL_FLY("effects/skills/fireball/fireball_fly.png", 7, 9, 10, 20f, 0.7f),
    // lightning strike
    LIGHTNING_STRIKE("effects/skills/lightningStrike/lightning_strike.png", 4, 9, 8, 12f, 2f),


    // enemies
    ENEMY_NORMAL_FIRE("effects/enemies/normal.png", 7, 9, 10, 10f, 1f),
    ENEMY_NORMAL_COLD("effects/enemies/normal.png", 2, 9, 10, 10f, 1f),
    ENEMY_NORMAL_LIGHTNING("effects/enemies/normal.png", 4, 9, 10, 10f, 1f),
    ENEMY_NORMAL_ANGEL("effects/enemies/normal.png", 5, 9, 10, 10f, 1f),
    ENEMY_NORMAL_DEMON("effects/enemies/normal.png", 1, 9, 10, 10f, 1f),

    ENEMY_SPEED_FIRE("effects/enemies/speed.png", 7, 9, 12, 12f, 1f),
    ENEMY_SPEED_COLD("effects/enemies/speed.png", 2, 9, 12, 12f, 1f),
    ENEMY_SPEED_LIGHTNING("effects/enemies/speed.png", 4, 9, 12, 12f, 1f),
    ENEMY_SPEED_ANGEL("effects/enemies/speed.png", 5, 9, 12, 12f, 1f),
    ENEMY_SPEED_DEMON("effects/enemies/speed.png", 1, 9, 12, 12f, 1f),

    ENEMY_TANK_FIRE("effects/enemies/tank.png", 7, 9, 10, 10f, 5f),
    ENEMY_TANK_COLD("effects/enemies/tank.png", 2, 9, 10, 10f, 5f),
    ENEMY_TANK_LIGHTNING("effects/enemies/tank.png", 4, 9, 10, 10f, 5f),
    ENEMY_TANK_ANGEL("effects/enemies/tank.png", 5, 9, 10, 10f, 5f),
    ENEMY_TANK_DEMON("effects/enemies/tank.png", 1, 9, 10, 10f, 5f),

    ENEMY_BOSS_FIRE("effects/enemies/boss.png", 7, 9, 14, 14f, 10f),
    ENEMY_BOSS_COLD("effects/enemies/boss.png", 2, 9, 14, 14f, 10f),
    ENEMY_BOSS_LIGHTNING("effects/enemies/boss.png", 4, 9, 14, 14f, 10f),
    ENEMY_BOSS_ANGEL("effects/enemies/boss.png", 5, 9, 14, 14f, 10f),
    ENEMY_BOSS_DEMON("effects/enemies/boss.png", 1, 9, 14, 14f, 10f),

    // enemies die
    // 02
    ENEMY_DIE_02_FIRE("effects/enemies/die_02.png", 7, 9, 10, 10f, 1.5f),
    ENEMY_DIE_02_COLD("effects/enemies/die_02.png", 2, 9, 10, 10f, 1.5f),
    ENEMY_DIE_02_LIGHTNING("effects/enemies/die_02.png", 4, 9, 10, 10f, 1.5f),
    ENEMY_DIE_02_ANGEL("effects/enemies/die_02.png", 5, 9, 10, 10f, 1.5f),
    ENEMY_DIE_02_DEMON("effects/enemies/die_02.png", 1, 9, 10, 10f, 1.5f),
    // 03
    ENEMY_DIE_03_FIRE("effects/enemies/die_03.png", 7, 9, 9, 9f, 1.5f),
    ENEMY_DIE_03_COLD("effects/enemies/die_03.png", 2, 9, 9, 9f, 1.5f),
    ENEMY_DIE_03_LIGHTNING("effects/enemies/die_03.png", 4, 9, 9, 9f, 1.5f),
    ENEMY_DIE_03_ANGEL("effects/enemies/die_03.png", 5, 9, 9, 9f, 1.5f),
    ENEMY_DIE_03_DEMON("effects/enemies/die_03.png", 1, 9, 9, 9f, 1.5f),
}
