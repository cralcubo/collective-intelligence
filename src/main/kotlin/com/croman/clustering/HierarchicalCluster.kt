package com.croman.clustering

import com.croman.collaborative.filtering.CollaborativeFilter
import com.croman.collaborative.filtering.Entity
import com.croman.utils.bestEntityFinder
import com.croman.utils.bestPairFinder
import java.util.*

sealed class Cluster(val level: Int, val virtualEntity: Entity, val similarity: Double)
class EntityCluster(val left: Entity, val right: Entity, level: Int, similarity: Double)
    : Cluster(level, left.mergeTo(right), similarity)
class BiCluster(val left: Cluster, val right: Entity, level: Int, similarity: Double)
    : Cluster(level, left.virtualEntity.mergeTo(right), similarity)


fun clusterCreator(entities: List<Entity>, similarityMeasurer: CollaborativeFilter) : Cluster {
    // find the innermost cluster and the left entities to create the
    // super cluster
    // 0 is the shortest distance between the entities to be evaluated
    // but the distanceMeasurer has values in the range of 1 and 0, being 1
    // the closest the relation between 2 entities, therefore
    // we need to substract the value to the calculation to 1
    // if it is for instance 0.9 (entities very related) taking out of 1
    // would be 0.1, that is a close distance for the best pair finder
    val (bestPair, leftEntities) =
        bestPairFinder(entities) { e1, e2 -> 1 - similarityMeasurer.calculate(e1, e2) }
    val innerCluster = EntityCluster(
        bestPair.first,
        bestPair.second,
        0,
        similarityMeasurer.calculate(bestPair.first, bestPair.second)
    )
    // Iterate the inner-custer to the remaining entities to shape a super-cluster
    var remainingEntities = leftEntities
    var superCluster: Cluster = innerCluster

    while (remainingEntities.isNotEmpty()) {
        val (bestEntity, entityList) = bestEntityFinder(
            superCluster.virtualEntity,
            remainingEntities
        ) { e1, e2 -> 1 - similarityMeasurer.calculate(e1, e2) }
        superCluster = BiCluster(
            superCluster,
            bestEntity,
            superCluster.level + 1,
            similarityMeasurer.calculate(superCluster.virtualEntity, bestEntity)
        )
        remainingEntities = entityList
    }

    return superCluster
}

/**
 * Find all the common items between e1 and e2
 * to create a new entity
 */
private fun Entity.mergeTo(e2: Entity) =
    Entity(UUID.randomUUID().toString(), this.items.intersect(e2.items))
