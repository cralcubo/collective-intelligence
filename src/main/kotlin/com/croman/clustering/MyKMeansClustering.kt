package com.croman.clustering

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Feature

data class KCluster(val centroid: Entity, val dataPoints: List<Entity>)


@Throws(IllegalArgumentException::class)
fun clusterer(dataPoints: List<Entity>, distance: SimilarityCalculator, k: Int): List<KCluster> {
    if(dataPoints.isEmpty()) {
        return emptyList()
    }

    var bestCentroids = pickCentroids(dataPoints, distance, k)

    // Loop a max of 100 times to place the centroids on their correct spots
    for (i in 0..100) {
        val clusters = dataPoints.map { point ->
            val centroid = bestCentroids.maxBy { centroid -> distance.calculate(centroid, point) }
            centroid to point
        }.groupBy({ it.first }, { it.second })
            .map { KCluster(it.key, it.value) }

        val movableCentroids = clusters.map { it.dataPoints }
            .map { averageEntity(it) }

        if (bestCentroids == movableCentroids) {
            println("Iterations: $i")
            return clusters
        }

        bestCentroids = movableCentroids
    }

    return emptyList()
}

private fun pickCentroids(dataPoints: List<Entity>, distance: SimilarityCalculator, k: Int): List<Entity> {
    require(k > 1) { "At least 2 centroids are required" }

    val c1 = dataPoints.random()
    // next centroid is the one that is the farthest away
    val c2 = dataPoints.minBy { distance.calculate(it, c1) }

    return centroidsCollector(mutableListOf(c1, c2), dataPoints, distance, k)
}

private tailrec fun centroidsCollector(
    mutableCentroids: MutableList<Entity>,
    dataPoints: List<Entity>,
    distance: SimilarityCalculator,
    k: Int
): List<Entity> {
    if (k == mutableCentroids.size) {
        return mutableCentroids
    }

    val newCentroid = dataPoints.map { point ->
        val centroid = mutableCentroids.maxBy { centroid -> distance.calculate(centroid, point) }
        point to distance.calculate(centroid, point)
    }.minBy { it.second }.first

    mutableCentroids.add(newCentroid)

    return centroidsCollector(mutableCentroids, dataPoints, distance, k)
}

private fun averageEntity(entities: List<Entity>) : Entity {
    // calc the average weight of all the values
    val features = entities.flatMap { it.features }
        .groupBy ({ it.id }, {it.weight} )
        .map { Feature(it.key, it.value.average()) }
        .toSet()

    return Entity(
        id = "e_${System.currentTimeMillis()}",
        features = features
    )
}

