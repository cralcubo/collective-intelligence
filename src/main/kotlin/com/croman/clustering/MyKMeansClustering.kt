package com.croman.clustering

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Item
import kotlin.math.roundToInt
import kotlin.random.Random

data class KCluster(val centroid: Pair<Int, Int>, val dataPoints: List<Pair<Int, Int>>)


@Throws(IllegalArgumentException::class)
fun clusterer(dataPoints: List<Pair<Int, Int>>, distance: SimilarityCalculator, k: Int): List<KCluster> {
    val (minX, maxX) = dataPoints.map { it.first }.let { it.min() to it.max() }
    val (minY, maxY) = dataPoints.map { it.second }.let { it.min() to it.max() }

    // create k random centroids between the min and max dataPoints
    val centroids = List(k) {
        Pair(
            Random.nextInt(minX, maxX + 1),
            Random.nextInt(minY, maxY + 1)
        )
    }

    var bestCentroids = centroids
    var bestClusters = emptyList<KCluster>()

    // Loop a max of 100 times to place the centroids on their correct spots
    for (i in 0..100) {
        var divider = k
        var leftDataPoints = dataPoints

        bestClusters = bestCentroids.map {
            val bestSpace = bestSpaceFinderByDividedSize(leftDataPoints, it, distance, divider--)
            // remove the points from the best space
            leftDataPoints = leftDataPoints - bestSpace.toSet()
            KCluster(it, bestSpace)
        }.toList()

        // move the centroids closer to the points assigned to them
        val movableCentroids = bestClusters.map { it.dataPoints }
            .map { space ->
                val avgX = space.map { it.first }.average()
                val avgY = space.map { it.second }.average()
                Pair(avgX.roundToInt(), avgY.roundToInt())
            }

        if (bestCentroids == movableCentroids) {
            break
        }

        bestCentroids = movableCentroids
    }

    return bestClusters
}

private fun bestSpaceFinderByDividedSize(
    dataPoints: List<Pair<Int, Int>>,
    centroid: Pair<Int, Int>,
    distance: SimilarityCalculator,
    divider: Int
): List<Pair<Int, Int>> {

    // Sort all the dataPoints by distance
    // the closer they are the closer the value is to 1
    val closestPoints = dataPoints.map { it to distance.calculate(centroid.toEntity(), it.toEntity()) }
        .sortedByDescending { it.second }
        .map { it.first }
        .take(dataPoints.size / divider)

    return closestPoints
}

private fun Pair<Int, Int>.toEntity(n: String = "e_${System.currentTimeMillis()}") =
    Entity(
        id = n,
        items = setOf(
            Item("x", first.toDouble()),
            Item("y", second.toDouble())
        )
    )