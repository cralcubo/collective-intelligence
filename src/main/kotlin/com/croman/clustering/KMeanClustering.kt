package com.croman.clustering


import kotlin.random.Random
fun pearson(v1: List<Double>, v2: List<Double>): Double {
    // Example implementation of Pearson distance
    // Placeholder implementation: replace with the actual calculation
    return v1.zip(v2).sumOf { (a, b) -> (a - b) * (a - b) }
}

/**
 * data: [[1, 2, 3], [2, 2, 3], [3, 3, 3], [4, 4, 4], [5, 5, 5]]
 *
 *  Conditions:
 * All data points must have the same size
 */
fun kcluster(data: List<List<Double>>, distance: (List<Double>, List<Double>) -> Double = ::pearson, k: Int = 4): List<List<Int>> {
    // Determine the minimum and maximum values for each point
    val ranges = data[0].indices.map { i ->
        val col = data.map { it[i] }
        Pair(col.minOrNull()!!, col.maxOrNull()!!)
    }

    // Create k randomly placed centroids
    val centroids = List(k) {
        ranges.map { (min, max) -> Random.nextDouble() * (max - min) + min }
    }.toMutableList()

    var lastMatches: List<List<Int>>? = null
    for (t in 0 until 100) { // max 100 iterations
        println("Iteration $t")

        val bestMatches = List(k) { mutableListOf<Int>() }

        // Find which centroid is the closest for each row
        for (j in data.indices) {
            val row = data[j]
            var bestMatch = 0
            for (i in 1 until k) {
                val d = distance(centroids[i], row)
                if (d < distance(centroids[bestMatch], row)) bestMatch = i
            }
            bestMatches[bestMatch].add(j)
        }

        // If the results are the same as last time, this is complete
        if (bestMatches == lastMatches) break
        lastMatches = bestMatches

        // Move the centroids to the average of their members
        for (i in 0 until k) {
            val clusterRows = bestMatches[i]
            if (clusterRows.isNotEmpty()) {
                val avgs = MutableList(data[0].size) { 0.0 }
                for (rowId in clusterRows) {
                    for (m in data[rowId].indices) {
                        avgs[m] += data[rowId][m]
                    }
                }
                for (j in avgs.indices) {
                    avgs[j] = avgs[j]/clusterRows.size
                }
                centroids[i] = avgs
            }
        }
    }
    return lastMatches ?: listOf()
}

fun main() {
    val rows = listOf(
        listOf(1.0, 2.0),
        listOf(2.0, 3.0),
        listOf(3.0, 4.0),
        listOf(4.0, 5.0)
    )
    val clusters = kcluster(rows)
    println(clusters)
}
