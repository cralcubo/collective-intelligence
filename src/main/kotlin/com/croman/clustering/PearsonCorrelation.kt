package com.croman.clustering

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
 * - (-1): The lowest correlation possible
 */
class PearsonCorrelation {

    companion object {

        fun calculate(e1: Entity, e2: Entity) : Double {
            // find common items and proceed with calculations
            val calculator = e1.items.asSequence()
                .flatMap { i1 ->
                    e2.items.asSequence()
                        .filter { i1.id == it.id }
                        .map { i1.value to it.value }
                }.toList().let { Calculator(it) }

            return evalIf(calculator.N > 0) {
                calculator.run {
                    val numerator = (sumXY - (sumX * sumY / N))
                    val xDen = sumXSqr - (sumX.pow(2) / N)
                    val yDen = sumYSqr - (sumY.pow(2) / N)
                    numerator / sqrt(xDen * yDen)
                }
            } ?: -1.0
        }
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