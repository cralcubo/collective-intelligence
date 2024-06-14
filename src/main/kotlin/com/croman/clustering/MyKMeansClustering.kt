package com.croman.clustering

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Item
import kotlin.math.roundToInt

data class KCluster(val centroid: Pair<Int, Int>, val dataPoints: List<Pair<Int, Int>>)


@Throws(IllegalArgumentException::class)
fun clusterer(dataPoints: List<Pair<Int, Int>>, distance: SimilarityCalculator, k: Int): List<KCluster> {
    if(dataPoints.isEmpty()) {
        return emptyList()
    }

    var bestCentroids = pickCentroids(dataPoints, distance, k)

    // Loop a max of 100 times to place the centroids on their correct spots
    for (i in 0..100) {
        val clusters = dataPoints.map { point ->
            val centroid = bestCentroids.maxBy { centroid -> distance.calculate(centroid.toEntity(), point.toEntity()) }
            centroid to point
        }.groupBy({ it.first }, { it.second })
            .map { KCluster(it.key, it.value) }

        val movableCentroids = clusters.map {
            val avgX = it.dataPoints.map { p -> p.first }.average()
            val avgY = it.dataPoints.map { p -> p.second }.average()
            Pair(avgX.roundToInt(), avgY.roundToInt())
        }

        if (bestCentroids == movableCentroids) {
            println("Iterations: $i")
            return clusters
        }

        bestCentroids = movableCentroids
    }

    return emptyList()
}

private fun pickCentroids(dataPoints: List<Pair<Int, Int>>, distance: SimilarityCalculator, k: Int): List<Pair<Int, Int>> {
    require(k > 1) { "At least 2 centroids are required" }

    val c1 = dataPoints.random()
    // next centroid is the one that is the farthest away
    val c2 = dataPoints.minBy { distance.calculate(it.toEntity(), c1.toEntity()) }

    return centroidsCollector(mutableListOf(c1, c2), dataPoints, distance, k)
}

private tailrec fun centroidsCollector(
    mutableCentroids: MutableList<Pair<Int, Int>>,
    dataPoints: List<Pair<Int, Int>>,
    distance: SimilarityCalculator,
    k: Int
): List<Pair<Int, Int>> {
    if (k == mutableCentroids.size) {
        return mutableCentroids
    }

    val newCentroid = dataPoints.map { point ->
        val centroid = mutableCentroids.maxBy { centroid -> distance.calculate(centroid.toEntity(), point.toEntity()) }
        point to distance.calculate(centroid.toEntity(), point.toEntity())
    }.minBy { it.second }.first

    mutableCentroids.add(newCentroid)

    return centroidsCollector(mutableCentroids, dataPoints, distance, k)
}

private fun Pair<Int, Int>.toEntity(n: String = "e_${System.currentTimeMillis()}") =
    Entity(
        id = n,
        items = setOf(
            Item("x", first.toDouble()),
            Item("y", second.toDouble())
        )
    )