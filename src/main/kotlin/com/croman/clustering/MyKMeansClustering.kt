package com.croman.clustering

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Item
import kotlin.math.roundToInt
import kotlin.random.Random

data class KCluster(val centroid: Pair<Int, Int>, val dataPoints: List<Pair<Int, Int>>)


@Throws(IllegalArgumentException::class)
// dataPoints: [[1, 2], [4, 5], [7, 8], [10, 11]]
// minMax : [[1,2], [10,11]]
fun clusterer(dataPoints: List<Pair<Int, Int>>, distance: SimilarityCalculator, k: Int): List<KCluster> {
    val minX = dataPoints.minOf { it.first }
    val maxX = dataPoints.maxOf { it.first }
    val minY = dataPoints.minOf { it.second }
    val maxY = dataPoints.maxOf { it.second }

    // create k random centroids between the min and max dataPoints
    val centroids = List(k) {
        Pair(
            Random.nextInt(minX, maxX + 1),
            Random.nextInt(minY, maxY + 1)
        )
    }

    var mutableDataPoints = dataPoints.toMutableList()
    var mutableCentroids = centroids
    var bestCentroids = centroids
    val mappedCentroids =
        mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
    // Loop a max of 100 times to place the centroids on their correct spots
    for (i in 0..100) {
        var divider = k
//        var internalDataPoints = dataPoints
        for (centroid in mutableCentroids) {
            val bestSpace = bestSpaceFinderByDividedSize(mutableDataPoints, centroid, distance, divider--)
            mappedCentroids[centroid] = bestSpace
            // remove the points from the best space
            bestSpace.forEach { mutableDataPoints.remove(it) }
        }
        // move the centroids closer to the points assigned to them
        mutableCentroids = mutableListOf()
        mappedCentroids.keys
            .forEach { centroid ->
                val avgX = mappedCentroids[centroid]!!.map { it.first }.average()
                val avgY = mappedCentroids[centroid]!!.map { it.second }.average()

                mutableCentroids.add(Pair(avgX.roundToInt(), avgY.roundToInt()))
            }

        if (bestCentroids == mutableCentroids) {
            break
        } else {
            bestCentroids = mutableCentroids
            mutableDataPoints = dataPoints.toMutableList()
            mappedCentroids.clear()
        }

    }

    return bestCentroids.map { KCluster(it, mappedCentroids[it]!!) }
}

private fun bestSpaceFinderByDividedSize(
    dataPoints: List<Pair<Int, Int>>,
    centroid: Pair<Int, Int>,
    distance: SimilarityCalculator,
    divider: Int) : List<Pair<Int, Int>> {

    // Sort all the dataPoints by distance
    // the closer they are the closer the value is to 1
    val closestPoints = dataPoints.map { it to distance.calculate(centroid.toEntity(), it.toEntity()) }
        .sortedByDescending { it.second }
        .map { it.first }
        .take(dataPoints.size/divider)

    return closestPoints
}

private fun bestSpaceFinderByQuadrants(
    dataPoints: List<Pair<Int, Int>>,
    minX: Int, maxX: Int,
    minY: Int, maxY: Int,
    centroid: Pair<Int, Int>,
    distance: SimilarityCalculator): List<Pair<Int, Int>> {
    // divide all the points in a 2^n spaces per centroid
    // s1 : (Xmin, Ymin) -> (X,Y)
    // s2:  (Xmin, Ymax) -> (X,Y)
    // s3:  (X,Y) -> (Xmax, Ymax)
    // s4:  (X,Y) -> (Xmax, Ymin)
    val x = centroid.first
    val y = centroid.second
    // collect all the dataPoints around the centroid
    val s1 = dataPoints.filter { it.first in minX..< x && it.second in minY..< y }
    val s2 = dataPoints.filter { it.first in minX..< x && it.second in y .. maxY }
    val s3 = dataPoints.filter { it.first in x..maxX && it.second in y .. maxY }
    val s4 = dataPoints.filter { it.first in x..maxX && it.second in minY ..< y }

    // pick the best space
    // distanceCalculation will average all the distances between the points,
    // the closer they are the value will be 1, otherwise 0
    // therefore bestSpace will be the one with the closes value to 1
    return listOf(s1, s2, s3, s4).asSequence()
        .filter { it.isNotEmpty() }
        .map { it to distanceCalculation(it, centroid, distance) } // distance -> the closer -> 1
        .maxBy { it.second }
        .first
}


private fun Pair<Int, Int>.toEntity(n: String = "e_${System.currentTimeMillis()}") =
    Entity(
        id = n,
        items = setOf(
            Item("x", first.toDouble()),
            Item("y", second.toDouble())
        )
    )



private fun distanceCalculation(
    dataPoints: List<Pair<Int, Int>>,
    centroid: Pair<Int, Int>,
    distance: SimilarityCalculator) : Double {
    //data class Item(val id: String, val value: Double)
    val e = centroid.toEntity("e1")
    val es = dataPoints.map { it.toEntity() }

    // calculate the average of all the distances
    return es.map { distance.calculate(e, it) }
        .average()
}