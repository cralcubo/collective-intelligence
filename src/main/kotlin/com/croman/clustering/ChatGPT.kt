package com.croman.clustering
import kotlin.math.pow
import kotlin.math.sqrt

class BiCluster(
    val vec: List<Double>,
    var left: BiCluster? = null,
    var right: BiCluster? = null,
    var distance: Double = 0.0,
    val id: Int? = null
)

fun pearson(v1: List<Double>, v2: List<Double>): Double {
    val sum1 = v1.sum()
    val sum2 = v2.sum()
    val sum1Sq = v1.sumByDouble { it.pow(2) }
    val sum2Sq = v2.sumByDouble { it.pow(2) }
    val pSum = v1.indices.sumByDouble { v1[it] * v2[it] }
    val num = pSum - (sum1 * sum2 / v1.size)
    val den = sqrt((sum1Sq - sum1.pow(2) / v1.size) * (sum2Sq - sum2.pow(2) / v1.size))
    return if (den == 0.0) 0.0 else 1.0 - num / den
}

fun hCluster(rows: List<List<Double>>, distance: (List<Double>, List<Double>) -> Double = ::pearson): BiCluster {
    val distances = mutableMapOf<Pair<Int, Int>, Double>()
    var currentClustId = -1
    val clust = rows.mapIndexed { i, vec -> BiCluster(vec, id = i) }.toMutableList()

    while (clust.size > 1) {
        var lowestPair = Pair(0, 1)
        var closest = distance(clust[0].vec, clust[1].vec)

        for (i in clust.indices) {
            for (j in (i + 1) until clust.size) {
                if ((clust[i].id to clust[j].id) !in distances) {
                    distances[(clust[i].id to clust[j].id)] = distance(clust[i].vec, clust[j].vec)
                }
                val d = distances.getValue(clust[i].id to clust[j].id)
                if (d < closest) {
                    closest = d
                    lowestPair = Pair(i, j)
                }
            }
        }

        val mergeVec = clust[lowestPair.first].vec.zip(clust[lowestPair.second].vec)
            .map { (v1, v2) -> (v1 + v2) / 2.0 }

        val newCluster = BiCluster(
            mergeVec,
            left = clust[lowestPair.first],
            right = clust[lowestPair.second],
            distance = closest,
            id = currentClustId
        )

        currentClustId--
        clust.removeAt(lowestPair.second)
        clust.removeAt(lowestPair.first)
        clust.add(newCluster)
    }

    return clust[0]
}

fun main() {
    // Example usage:
    val data = listOf(
        listOf(1.0, 2.0, 3.0),
        listOf(4.0, 5.0, 6.0),
        listOf(7.0, 8.0, 9.0)
    )
    val result = hCluster(data)
    println(result)
}
