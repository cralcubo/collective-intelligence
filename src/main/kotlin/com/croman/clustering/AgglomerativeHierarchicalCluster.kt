package com.croman.clustering

import com.croman.utils.Entity

sealed interface ACluster
class MonoCluster(val entity: Entity) : ACluster
class ABiCluster(val left: ACluster, val right: ACluster) : ACluster

/**
 * Will create an agglomerative cluster with all the entities provided
 * The process will be iterative
 * The first iteration will create a Map where the key will be the cluster and the value the distance between its entities
 * From all these distances the shortest will be picked to merge them and create a new cluster
 * The order in which the clusters were shaped is important to determine the hierarchy!
 */
class AgglomerativeHierarchicalCluster {


}