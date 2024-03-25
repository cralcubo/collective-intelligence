package com.croman.collaborative.filtering

import com.croman.utils.Entity

class TanimotoCoefficient: SimilarityCalculator {

    /**
     * Calculates what items have in common both entities
     * If they have the same items then they are similar and the value is 1
     * If no item are similar then the value is 0
     *
     * Formula:
     * J = |A intersect B| / |A union B|
     */
    override fun calculate(e1: Entity, e2: Entity): Double {
        val intersection = e1.items intersect e2.items
        val union = e1.items union e2.items
        return intersection.size.toDouble() / union.size
    }
}