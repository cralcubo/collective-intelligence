package com.croman.collaborative.filtering

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
class PearsonCorrelation: CollaborativeFilter {

    override fun calculate(e1: Entity, e2: Entity): Double {
        // find common items and proceed with calculations
        val calculator = e1.items.asSequence()
            .flatMap { i1 ->
                e2.items.asSequence()
                    .filter { i1.id == it.id }
                    .map { i1.value to it.value }
            }.toList().let { Calculator(it) }

        // To calculate the correlation we should have at least
        // 2 values in the calculator (only 2 points can make a line to see if it correlates)
        return evalIf(calculator.N > 1) {
            calculator.run {
                val numerator = (sumXY - (sumX * sumY / N))
                val xDen = sumXSqr - (sumX.pow(2) / N)
                val yDen = sumYSqr - (sumY.pow(2) / N)
                numerator / sqrt(xDen * yDen)
            }
        } ?: throw IllegalArgumentException("Not enough items to calculate correlation")
    }

    private class Calculator(val xy: List<Pair<Double, Double>>) {
        val N = xy.size

        val sumXY: Double by lazy {
            xy.sumOf { it.first * it.second }
        }

        val sumX: Double by lazy {
            xy.sumOf { it.first }
        }

        val sumY: Double by lazy {
            xy.sumOf { it.second }
        }

        val sumXSqr: Double by lazy {
            xy.sumOf { it.first * it.first }
        }

        val sumYSqr: Double by lazy {
            xy.sumOf { it.second * it.second }
        }
    }


}