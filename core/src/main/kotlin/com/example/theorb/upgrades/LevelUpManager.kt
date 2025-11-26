package com.example.theorb.upgrades

import com.example.theorb.balance.LevelUpOptionType
import com.example.theorb.balance.LevelUpOptionData
import com.example.theorb.balance.UpgradeTier
import com.example.theorb.data.SaveData
import kotlin.random.Random

/**
 * 인게임 레벨업 시스템 관리
 */
object LevelUpManager {

    /**
     * 레벨별 필요 경험치 계산 (로그 함수 기반 - 초반 완만, 후반 급격)
     *
     * 공식: baseExp * (1 + scalingFactor * level^exponent)
     * - 초반(레벨 1-5): 완만하게 증가 (빠른 레벨업)
     * - 중반(레벨 6-15): 점진적 증가
     * - 후반(레벨 16+): 급격하게 증가 (느린 레벨업)
     */
    fun getRequiredExpForLevel(level: Int): Int {
        val baseExp = 50 // 기본 경험치 (레벨 1)
        val scalingFactor = 0.15 // 스케일링 계수
        val exponent = 1.8 // 지수 (1.8~2.0이 적당, 높을수록 후반이 어려워짐)

        return (baseExp * (1.0 + scalingFactor * Math.pow(level.toDouble(), exponent))).toInt()
    }

    /**
     * 경험치 추가 및 레벨업 체크
     * @return 레벨업 했는지 여부
     */
    fun addExp(saveData: SaveData, exp: Int): Boolean {
        saveData.inGameExp += exp
        val requiredExp = getRequiredExpForLevel(saveData.inGameLevel)

        if (saveData.inGameExp >= requiredExp) {
            saveData.inGameExp -= requiredExp
            saveData.inGameLevel++
            return true
        }

        return false
    }

    /**
     * 레벨업 선택지 3개 생성
     */
    fun generateLevelUpOptions(saveData: SaveData, random: Random = Random): List<LevelUpOptionData> {
        val options = mutableListOf<LevelUpOptionData>()
        val usedTypes = mutableSetOf<LevelUpOptionType>()

        repeat(3) {
            // 모든 사용 가능한 옵션을 먼저 필터링 (GOLD_BONUS 제외)
            val allAvailableOptions = LevelUpOptionType.values()
                .filter { optionType ->
                    // 골드 보너스는 폴백 전용이므로 제외
                    if (optionType == LevelUpOptionType.GOLD_BONUS) return@filter false

                    // 이미 사용된 옵션 제외
                    if (optionType in usedTypes) return@filter false

                    // 현재 선택된 옵션 중 해당 타입의 개수 세기
                    val currentStack = saveData.selectedLevelUpOptions.count { it == optionType.name }
                    currentStack < optionType.maxStack
                }

            if (allAvailableOptions.isNotEmpty()) {
                // 사용 가능한 옵션이 있으면 티어 가중치로 선택
                val selectedType = selectOptionByTierWeight(allAvailableOptions, saveData, random)
                val currentStack = saveData.selectedLevelUpOptions.count { it == selectedType.name }
                options.add(LevelUpOptionData(selectedType, currentStack))
                usedTypes.add(selectedType)
            }
        }

        // 3개가 안 채워졌으면 골드 보너스로 채우기
        while (options.size < 3) {
            val goldOption = LevelUpOptionType.GOLD_BONUS
            val currentStack = saveData.selectedLevelUpOptions.count { it == goldOption.name }
            options.add(LevelUpOptionData(goldOption, currentStack))
        }

        return options
    }

    /**
     * 사용 가능한 옵션 중에서 티어 가중치에 따라 선택
     */
    private fun selectOptionByTierWeight(
        availableOptions: List<LevelUpOptionType>,
        saveData: SaveData,
        random: Random
    ): LevelUpOptionType {
        // 각 옵션에 티어에 따른 가중치 부여
        val weightedOptions = availableOptions.map { option ->
            val weight = when (option.tier) {
                UpgradeTier.UNIQUE -> saveData.tierChanceUnique
                UpgradeTier.RARE -> saveData.tierChanceRare
                UpgradeTier.NORMAL -> 100f - saveData.tierChanceUnique - saveData.tierChanceRare
            }
            option to weight
        }

        // 가중치 합계
        val totalWeight = weightedOptions.sumOf { it.second.toDouble() }.toFloat()

        // 가중치에 따라 랜덤 선택
        val roll = random.nextFloat() * totalWeight
        var currentWeight = 0f

        for ((option, weight) in weightedOptions) {
            currentWeight += weight
            if (roll < currentWeight) {
                return option
            }
        }

        // 폴백 (일어나지 않아야 함)
        return weightedOptions.last().first
    }

    /**
     * 확률에 따라 티어 선택
     */
    private fun selectTier(saveData: SaveData, random: Random): UpgradeTier {
        val roll = random.nextFloat() * 100f

        return when {
            roll < saveData.tierChanceUnique -> UpgradeTier.UNIQUE
            roll < saveData.tierChanceUnique + saveData.tierChanceRare -> UpgradeTier.RARE
            else -> UpgradeTier.NORMAL
        }
    }

    /**
     * 선택지 적용
     */
    fun applyOption(saveData: SaveData, option: LevelUpOptionData) {
        // 골드 보너스는 즉시 적용하고 리스트에 추가하지 않음
        if (option.type == LevelUpOptionType.GOLD_BONUS) {
            saveData.gold += 50
        } else {
            // 일반 옵션은 선택된 옵션 ID를 리스트에 추가
            saveData.selectedLevelUpOptions.add(option.type.name)
        }
    }

    /**
     * 리롤 가능 여부 확인
     */
    fun canReroll(saveData: SaveData): Boolean {
        return saveData.currentRerollCount > 0
    }

    /**
     * 리롤 횟수 사용
     * @return 성공 여부
     */
    fun useReroll(saveData: SaveData): Boolean {
        if (saveData.currentRerollCount > 0) {
            saveData.currentRerollCount--
            return true
        }
        return false
    }

    /**
     * 게임 시작 시 리롤 횟수 충전
     */
    fun rechargeRerolls(saveData: SaveData) {
        saveData.currentRerollCount = saveData.maxRerollCount
    }

}
