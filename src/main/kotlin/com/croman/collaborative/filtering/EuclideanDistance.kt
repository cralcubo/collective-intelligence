package com.croman.collaborative.filtering

import com.croman.utils.Entity
import kotlin.math.sqrt

/**
 * It takes 2 items between N entities
 * to find how close those entities are between those items.
 *
 * The closer the entities are, the more similar they are.
 *
 * To calculate how close the entities are we use the Pythagorean Theorem
 * (hypotenuse of a triangle)
 */
class EuclideanDistance(private val minSize: Int = 10) : SimilarityCalculator {
    /**
     * Calculates the Euclidean distance between 2 entities
     * based on the common items they have with each other
     */
    override fun calculate(e1: Entity, e2: Entity): Double {
        val commonItems = e1.features.asSequence()
            .flatMap { i1 ->
                e2.features.asSequence()
                    .filter { i1.id == it.id }
                    .map { i1 to it }
            }
            .toList()
        if(commonItems.size < minSize) {
            return 0.0
        }

        // find common items and calc Sum(i2 - i1)^2
        val sumPows = commonItems.map { it.first.weight - it.second.weight }
            .sumOf { it * it }

        return 1.0 / (1 + sqrt(sumPows))
    }
}