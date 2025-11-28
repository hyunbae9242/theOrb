package com.example.theorb.entities

import com.example.theorb.balance.Balance
import com.example.theorb.balance.EnemyType
import com.example.theorb.balance.ProgressionBalance
import com.example.theorb.util.weightedRandom
import kotlin.random.Random

object EnemyFactory {

    fun spawnRandom(
        width: Float = 480f,
        gameAreaHeight: Float = 550f,
        gameAreaStartY: Float = 150f,
        gameTimeSeconds: Float = 0f,
        currentWave: Int = 1,
        stageHpMultiplier: Float = 1.0f,
        stageDamageMultiplier: Float = 1.0f,
        rnd: Random = Random
    ): Enemy {
        // 웨이브에 따른 적 타입 가중치 결정
        val weights = when {
            currentWave <= 5 -> mapOf(EnemyType.NORMAL to 100) // 1~5웨이브: 노말만
            currentWave <= 10 -> mapOf(EnemyType.NORMAL to 85, EnemyType.TANK to 15) // 6~10웨이브: 노말 85 탱크 15
            else -> mapOf(EnemyType.NORMAL to 70, EnemyType.SPEED to 20, EnemyType.TANK to 10) // 11~20웨이브: 노말 70 스피드 20 탱크 10
        }

        val type = weightedRandom(weights, rnd)

        // 스폰 위치(게임 영역의 4변 랜덤)
        val side = rnd.nextInt(4)
        val (sx, sy) = when (side) {
            0 -> 0f to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)          // left
            1 -> width to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)       // right
            2 -> rnd.nextFloat() * width to gameAreaStartY                          // bottom
            else -> rnd.nextFloat() * width to (gameAreaHeight + gameAreaStartY)    // top
        }

        // 타입별 배수 적용 및 웨이브 기반 스케일링 + 스테이지 배율 적용
        val mul = Balance.TYPE_MULTIPLIERS[type]!!
        val waveScalingMultiplier = 1.0f + (currentWave - 1) * 0.15f // 웨이브당 15% 증가
        val hp = (Balance.BASE_HP * mul.hpMul * waveScalingMultiplier * stageHpMultiplier).toInt()
        val speed = Balance.BASE_SPEED * mul.speedMul
        val contactDmg = (Balance.BASE_CONTACT_DAMAGE * mul.dmgMul * stageDamageMultiplier).toInt()
        val rewardGold = (Balance.BASE_REWARD_GOLD * mul.goldMul).toInt()

        return Enemy(
            type = type,
            hp = hp,
            contactDamage = contactDmg,
            x = sx,
            y = sy,
            speed = speed,
            rewardGold = rewardGold
        ).apply {
            vhp = hp // vhp 초기화
        }
    }

    fun spawnBoss(
        width: Float = 480f,
        gameAreaHeight: Float = 550f,
        gameAreaStartY: Float = 150f,
        gameTimeSeconds: Float = 0f,
        currentWave: Int = 1,
        stageHpMultiplier: Float = 1.0f,
        stageDamageMultiplier: Float = 1.0f,
        rnd: Random = Random
    ): Enemy {
        // 스폰 위치(게임 영역의 4변 랜덤)
        val side = rnd.nextInt(4)
        val (sx, sy) = when (side) {
            0 -> 0f to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)          // left
            1 -> width to (rnd.nextFloat() * gameAreaHeight + gameAreaStartY)       // right
            2 -> rnd.nextFloat() * width to gameAreaStartY                          // bottom
            else -> rnd.nextFloat() * width to (gameAreaHeight + gameAreaStartY)    // top
        }

        // 보스 타입별 배수 적용 및 웨이브 기반 스케일링 + 스테이지 배율 적용
        val mul = Balance.TYPE_MULTIPLIERS[EnemyType.BOSS]!!
        val waveScalingMultiplier = 1.0f + (currentWave - 1) * 0.15f // 웨이브당 15% 증가
        val hp = (Balance.BASE_HP * mul.hpMul * waveScalingMultiplier * stageHpMultiplier).toInt()
        val speed = Balance.BASE_SPEED * mul.speedMul
        val contactDmg = (Balance.BASE_CONTACT_DAMAGE * mul.dmgMul * stageDamageMultiplier).toInt()
        val rewardGold = (Balance.BASE_REWARD_GOLD * mul.goldMul).toInt()

        return Enemy(
            type = EnemyType.BOSS,
            hp = hp,
            contactDamage = contactDmg,
            x = sx,
            y = sy,
            speed = speed,
            rewardGold = rewardGold
        ).apply {
            vhp = hp // vhp 초기화
        }
    }
}
