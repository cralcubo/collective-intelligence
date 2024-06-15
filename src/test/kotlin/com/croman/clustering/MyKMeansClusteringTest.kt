package com.croman.clustering

import com.croman.collaborative.filtering.EuclideanDistance
import com.croman.utils.Entity
import com.croman.utils.Value
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt
import kotlin.test.Test


class MyKMeansClusteringTest {

    @Test
    fun noDataPoints() {
        clusterer(emptyList(), EuclideanDistance(), 2).isEmpty() shouldBe true
    }

    @Test
    fun unbalancedClusters() {
        val dataPoints = listOf(
            Pair(2,2),
            Pair(2,4),

            Pair(9,6),
            Pair(9,4),
            Pair(8,5),
            Pair(10,5),
        ).map { it.toEntity() }

        val kMeans = clusterer(dataPoints, EuclideanDistance(), 2)
        kMeans.map { it.centroid }.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(9, 5),Pair(2, 3))
        kMeans.find { it.centroid.toPair() == Pair(9, 5) }!!.dataPoints.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(9, 6), Pair(9, 4), Pair(8, 5), Pair(10,5))
        kMeans.find { it.centroid.toPair() == Pair(2, 3) }!!.dataPoints.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(2, 2), Pair(2, 4))
    }

    @Test
    fun perfectCentroids() {
        val dataPoints = listOf(
            Pair(9,6),
            Pair(9,4),
            Pair(7,5),
            Pair(1,3),
            Pair(2,4),
            Pair(2,2),
            Pair(6,8),
            Pair(4,7),
            Pair(5,9),
        ).map { it.toEntity() }

        val kMeans = clusterer(dataPoints, EuclideanDistance(), 3)
        kMeans.map { it.centroid }.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(8, 5), Pair(5, 8), Pair(2, 3))
        kMeans.find { it.centroid.toPair() == Pair(8, 5) }!!.dataPoints.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(9, 6), Pair(9, 4), Pair(7, 5))
        kMeans.find { it.centroid.toPair() == Pair(5, 8) }!!.dataPoints.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(6, 8), Pair(5, 9), Pair(4, 7))
        kMeans.find { it.centroid.toPair() == Pair(2, 3) }!!.dataPoints.map { it.toPair() } shouldContainExactlyInAnyOrder listOf(Pair(2, 2), Pair(1, 3), Pair(2, 4))
    }
}

private fun Entity.toPair(): Pair<Int, Int> {
    val x = this.values.find { it.id == "x" }!!.weight
    val y = this.values.find { it.id == "y" }!!.weight
    return x.roundToInt() to y.roundToInt()
}




private fun Pair<Int, Int>.toEntity(n: String = "e_${System.currentTimeMillis()}") =
    Entity(
        id = n,
        values = setOf(
            Value("x", first.toDouble()),
            Value("y", second.toDouble())
        )
    )