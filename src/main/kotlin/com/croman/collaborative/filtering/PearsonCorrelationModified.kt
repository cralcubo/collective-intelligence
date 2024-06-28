package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Feature
import com.croman.utils.evalIf
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The Pearson Correlation calculates
 * how correlated the points on a plane are.
 * The higher the correlation, the most aligned all the points
 * on a plane are.
 * For such calculation the values will be:
 * - 1: The highest correlation possible
 * - -1: The lowest correlation possible
 */
class PearsonCorrelationModified(private val minSize: Int=10): SimilarityCalculator {

    override fun calculate(e1: Entity, e2: Entity): Double {
        // find common items and proceed with calculations
        val calculator = e1.features.asSequence()
            .flatMap { v1 ->
                e2.features.asSequence()
                    .filter { v1.id == it.id }
                    .map { v1 to it }
            }.toList().let { Calculator(it) }

        // To calculate the correlation we should have at least
        // minSize values in the calculator
        return evalIf(calculator.N > minSize) {
            calculator.run {
                val numerator = (sumXY - (sumX * sumY / N))
                val xDen = sumXSqr - (sumX.pow(2) / N)
                val yDen = sumYSqr - (sumY.pow(2) / N)
                numerator / sqrt(xDen * yDen)
            }
        } ?: 0.0
    }

    private class Calculator(xy: List<Pair<Feature, Feature>>) {
        private val xyTransformed = xy.transform()
        val N = xy.size

        private fun List<Pair<Feature, Feature>>.transform() : List<Pair<Double, Double>> {
            val offset = 0.01
            var multiplier = 0

            val repeatedValueWeights =
                this.map { "${it.first.weight}-${it.second.weight}" to it }
                .groupBy({ it.first }, { it.second })
                .filter { it.value.size > 1 }
                .flatMap { it.value }

            val repeatedValuesOffset = repeatedValueWeights.map {
                val nX = it.first.weight + (offset * multiplier)
                val nY = it.second.weight + (offset * multiplier)
                multiplier++
                nX to nY
            }

            val differentWeights = this - repeatedValueWeights.toSet()
            return differentWeights.map { it.first.weight to it.second.weight } + repeatedValuesOffset
        }

        val sumXY: Double by lazy {
            xyTransformed.sumOf { it.first * it.second }
        }

        val sumX: Double by lazy {
            xyTransformed.sumOf { it.first }
        }

        val sumY: Double by lazy {
            xyTransformed.sumOf { it.second }
        }

        val sumXSqr: Double by lazy {
            xyTransformed.sumOf { it.first.pow(2) }
        }

        val sumYSqr: Double by lazy {
            xyTransformed.sumOf { it.second.pow(2) }
        }
    }


}