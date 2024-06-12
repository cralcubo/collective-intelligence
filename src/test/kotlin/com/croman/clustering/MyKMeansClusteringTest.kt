package com.croman.clustering

import com.croman.collaborative.filtering.EuclideanDistance
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test


class MyKMeansClusteringTest {

    @Test
    fun perfectCentroids() {
        val dataPoints = listOf(
            Pair(9,6),
            Pair(1,3),
            Pair(2,4),
            Pair(9,4),
            Pair(2,2),
            Pair(6,8),
            Pair(4,7),
            Pair(5,9),
            Pair(7,5),
//            Pair(8,3), // extra
        )

        val kMeans = clusterer(dataPoints, EuclideanDistance(), 3)
        println(kMeans)
        kMeans shouldHaveSize 3
//        kMeans[0].dataPoints shouldHaveSize 3
//        kMeans[1].dataPoints shouldHaveSize 3
//        kMeans[2].dataPoints shouldHaveSize 3

        kMeans.map { it.centroid } shouldContainExactlyInAnyOrder listOf(Pair(8, 5), Pair(5, 8), Pair(2, 3))
        kMeans.find { it.centroid == Pair(8, 5) }!!.dataPoints shouldContainExactlyInAnyOrder listOf(Pair(9, 6), Pair(9, 4), Pair(7, 5))
        kMeans.find { it.centroid == Pair(5, 8) }!!.dataPoints shouldContainExactlyInAnyOrder listOf(Pair(6, 8), Pair(5, 9), Pair(4, 7))
        kMeans.find { it.centroid == Pair(2, 3) }!!.dataPoints shouldContainExactlyInAnyOrder listOf(Pair(2, 2), Pair(1, 3), Pair(2, 4))
    }

}