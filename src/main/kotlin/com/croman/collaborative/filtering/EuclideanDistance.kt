package com.croman.collaborative.filtering

import kotlin.math.pow
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
class EuclideanDistance : CollaborativeFilter {
    /**
     * Calculates the Euclidean distance between 2 entities
     * based on the common items they have with each other
     */
    override fun calculate(e1: Entity, e2: Entity): Double {
        // find common items and calc Sum(i2 - i1)^2
        val sumPows = e1.items.asSequence()
            .flatMap { i1 ->
                e2.items.asSequence()
                    .filter { i1.id == it.id }
                    .map { i1.value - it.value }
                    .map { it.pow(2) }
            }
            .sum()

        return 1.0 / (1 + sqrt(sumPows))
    }
}