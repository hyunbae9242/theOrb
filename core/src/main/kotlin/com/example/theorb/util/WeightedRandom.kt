package com.example.theorb.util

import kotlin.random.Random

fun <T> weightedRandom(weights: Map<T, Int>, rnd: Random = Random): T {
    val total = weights.values.sum().coerceAtLeast(1)
    var roll = rnd.nextInt(total)
    for ((k, w) in weights) {
        if (roll < w) return k
        roll -= w
    }
    return weights.keys.first() // fallback
}
