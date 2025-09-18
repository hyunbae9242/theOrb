package com.example.theorb.entities

import com.example.theorb.balance.Balance
import com.example.theorb.balance.Element
import com.example.theorb.balance.EnemyType
import com.example.theorb.util.weightedRandom
import kotlin.math.min
import kotlin.random.Random

object EnemyFactory {

    fun spawnRandom(width: Float = 480f, gameAreaHeight: Float = 550f, gameAreaStartY: Float = 150f, rnd: Random = Random): Enemy {
        val type = weightedRandom(Balance.TYPE_WEIGHTS_NO_BOSS, rnd)
        val element = weightedRandom(Balance.ELEMENT_WEIGHTS, rnd)

        // 스폰 위치(게임 영역의 4변 랜덤)
        val side = rnd.nextInt(4)
        val (sx, sy) = when (side) {
            0 -> 0f to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)          // left
            1 -> width to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)       // right
            2 -> rnd.nextFloat() * width to gameAreaStartY                          // bottom
            else -> rnd.nextFloat() * width to (gameAreaHeight + gameAreaStartY)    // top
        }

        // 타입별 배수 적용
        val mul = Balance.TYPE_MULTIPLIERS[type]!!
        val hp = (Balance.BASE_HP * mul.hpMul).toInt()
        val speed = Balance.BASE_SPEED * mul.speedMul
        val contactDmg = (Balance.BASE_CONTACT_DAMAGE * mul.dmgMul).toInt()
        val rewardGold = (Balance.BASE_REWARD_GOLD * mul.goldMul).toInt()

        // 저항 맵 (기본 10%, 해당 속성 40%, 상한 75%)
        val resist = Element.values().associateWith { e ->
            val r = if (e == element) Balance.MATCH_RESIST else Balance.BASE_RESIST
            min(r, Balance.MAX_RESIST)
        }

        return Enemy(
            type = type,
            element = element,
            hp = hp,
            contactDamage = contactDmg,
            x = sx,
            y = sy,
            speed = speed,
            rewardGold,
            resist = resist
        ).apply {
            vhp = hp // vhp 초기화
        }
    }

    fun spawnBoss(width: Float = 480f, gameAreaHeight: Float = 550f, gameAreaStartY: Float = 150f, rnd: Random = Random): Enemy {
        val element = weightedRandom(Balance.ELEMENT_WEIGHTS, rnd)

        // 스폰 위치(게임 영역의 4변 랜덤)
        val side = rnd.nextInt(4)
        val (sx, sy) = when (side) {
            0 -> 0f to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)          // left
            1 -> width to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)       // right
            2 -> rnd.nextFloat() * width to gameAreaStartY                          // bottom
            else -> rnd.nextFloat() * width to (gameAreaHeight + gameAreaStartY)    // top
        }

        // 보스 타입별 배수 적용
        val mul = Balance.TYPE_MULTIPLIERS[EnemyType.BOSS]!!
        val hp = (Balance.BASE_HP * mul.hpMul).toInt()
        val speed = Balance.BASE_SPEED * mul.speedMul
        val contactDmg = (Balance.BASE_CONTACT_DAMAGE * mul.dmgMul).toInt()
        val rewardGold = (Balance.BASE_REWARD_GOLD * mul.goldMul).toInt()

        // 저항 맵 (기본 10%, 해당 속성 40%, 상한 75%)
        val resist = Element.values().associateWith { e ->
            val r = if (e == element) Balance.MATCH_RESIST else Balance.BASE_RESIST
            min(r, Balance.MAX_RESIST)
        }

        return Enemy(
            type = EnemyType.BOSS,
            element = element,
            hp = hp,
            contactDamage = contactDmg,
            x = sx,
            y = sy,
            speed = speed,
            rewardGold,
            resist = resist
        ).apply {
            vhp = hp // vhp 초기화
        }
    }
}
