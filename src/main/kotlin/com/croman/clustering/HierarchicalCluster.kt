package com.croman.clustering

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.collaborative.filtering.TanimotoCoefficient
import com.croman.utils.*
import java.nio.file.Paths
import java.util.*

sealed class Cluster(val level: Int, val virtualEntity: Entity, val similarity: Double) : Iterable<Cluster> {
    override fun iterator() =
        EntityIterator(this)

    inner class EntityIterator(cluster: Cluster) : Iterator<Cluster> {
        private var nextCluster: Cluster? = cluster

        override fun hasNext() =
            nextCluster != null

        override fun next(): Cluster {
            require(nextCluster != null)
            val currentCluster = nextCluster!!
            nextCluster = evalIf(currentCluster is BiCluster) { (currentCluster as BiCluster).left }
            return currentCluster
        }
    }
}

class EntityCluster(val left: Entity, val right: Entity, level: Int, similarity: Double) :
    Cluster(level, left.mergeTo(right), similarity)


class BiCluster(val left: Cluster, val right: Entity, level: Int, similarity: Double) :
    Cluster(level, left.virtualEntity.mergeTo(right), similarity)

/**
 * Find all the common items between e1 and e2
 * to create a new entity
 */
private fun Entity.mergeTo(e2: Entity) =
    Entity(UUID.randomUUID().toString(), this.items.intersect(e2.items))


private fun superClusterCreator(innerCluster: Cluster, entities: List<Entity>, similarityMeasurer: SimilarityCalculator) : Cluster {
    // Iterate the inner-custer to the remaining entities to shape a super-cluster
    var remainingEntities = entities
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
 * Create a cluster based the [entity] provided. This entity will be used to shape the
 * inner-most cluster of the super-cluster to be returned by this function.
 */
fun clusterCreatorByEntity(entity: Entity, entities: List<Entity>, similarityMeasurer: SimilarityCalculator): Cluster {
    // remove the entity from all the entities to find the best entity
    val mutableEntities = entities.toMutableList()
    mutableEntities.remove(entity)
    val (bestEntity, remainingEntities) =
        bestEntityFinder(
            entity,
            mutableEntities) { e1, e2 -> 1 - similarityMeasurer.calculate(e1, e2) }

    return superClusterCreator(
        innerCluster = EntityCluster(entity, bestEntity, 0, similarityMeasurer.calculate(entity, bestEntity)),
        entities = remainingEntities,
        similarityMeasurer = similarityMeasurer
    )
}

fun clusterCreator(entities: List<Entity>, similarityMeasurer: SimilarityCalculator): Cluster {
    require(entities.size > 1) { "There must be at least 2 entities to build a cluster" }

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

    return superClusterCreator(innerCluster, leftEntities, similarityMeasurer)
}

/**
 * Create a cluster with all the movies from MovieLens
 */
fun main() {
//    run()
//    test()
//    runByEntity("Intouchables (2011)")
    runTag()
//    runMix("Driving Miss Daisy (1989)")
}

private fun createItems(genres: Set<String>, tags:Set<String>): Set<Item> {
    val gi = genres.map { Item(it.lowercase(), 1.0) }.toSet()
    val gt = tags.map { Item(it.lowercase(), 1.0) }.toSet()
    return gi + gt
}

private fun runTagByEntity(entityId: String) {
    val pathMovies =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    val movies = MovieLensFactory.createMovies(pathMovies)

    val pathTags =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/clustering/movie-lens/tags.csv")
    val tags = MovieLensFactory.createTags(pathTags)

    val entities = tags.mapNotNull {
        movies.find { m -> m.movieId == it.movieId }?.let { m ->
            Entity(m.title, it.tags.map { t -> Item(t.lowercase(), 1.0) }.toSet() )
        }
    }
    runEntity(entityId, entities)
}

private fun runTag() {
    val pathMovies =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    val movies = MovieLensFactory.createMovies(pathMovies)

    val pathTags =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/clustering/movie-lens/tags.csv")
    val tags = MovieLensFactory.createTags(pathTags)

    val entities = tags.mapNotNull {
        movies.find { m -> m.movieId == it.movieId }?.let { m ->
            Entity(m.title, it.tags.map { t -> Item(t, 1.0) }.toSet() )
        }
    }
    runAll(entities)
}

private fun test() {
    val e1 = Entity(
        "1",
        setOf(
            Item("a", 1.0),
            Item("b", 1.0),
            Item("c", 1.0),
        )
    )
    val e2 = Entity(
        "2",
        setOf(
            Item("a", 1.0),
            Item("b", 1.0),
            Item("z", 1.0),
        )
    )

    val e3 = Entity(
        "3",
        setOf(
            Item("a", 1.0),
            Item("b", 1.0),
            Item("c", 1.0),
        )
    )
    val cluster = clusterCreator(listOf(e1, e2, e3), TanimotoCoefficient())
    val iterator = cluster.iterator()
    while (iterator.hasNext()) {
        println("|")
        iterator.next().print()
    }
}

private fun run() {
    val path =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    val movies = MovieLensFactory.createMovies(path)
    val entities = movies.map {
        Entity(it.title, it.genres.map { g -> Item(g, 1.0) }.toSet())
    }
    runAll(entities)
}

private fun runByEntity(entityId: String) {
    val path =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    val movies = MovieLensFactory.createMovies(path)
    val entities = movies.map {
        Entity(it.title, it.genres.map { g -> Item(g, 1.0) }.toSet())
    }

    runEntity(entityId, entities)
}

private fun runAll(entities: List<Entity>) {
    println("Clustering ${entities.size} entities...")

    val st = Date().time
    val cluster = clusterCreator(entities, TanimotoCoefficient())
    println("It took ${(Date().time - st).toDouble() / 1000} s. to cluster all entities")

    val iterator = cluster.iterator()
    while (iterator.hasNext()) {
        println("|")
        iterator.next().print()
    }

    println("done")
}

private fun runEntity(entityId: String, entities: List<Entity>) {
    val entity = entities.find { it.id == entityId }
    require(entity != null) { "Entity $entityId not found" }

    println("Clustering ${entities.size} around $entity")

    val st = Date().time
    val cluster = clusterCreatorByEntity(entity, entities, TanimotoCoefficient())
    println("It took ${(Date().time - st).toDouble() / 1000} s. to cluster all entities")

    val iterator = cluster.iterator()
    while (iterator.hasNext()) {
        println("|")
        iterator.next().print()
    }

    println("done")

}

private fun runMix(entityId: String) {
    val pathMovies =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    val movies = MovieLensFactory.createMovies(pathMovies)

    val pathTags =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/clustering/movie-lens/tags.csv")
    val tags = MovieLensFactory.createTags(pathTags)

    val entities = tags.mapNotNull {
        movies.find { m -> m.movieId == it.movieId }?.let { m ->
            Entity(m.title, createItems(m.genres, it.tags) )
        }
    }
    runEntity(entityId, entities)
}


private fun Cluster.print() {
    val itemsStr : (Entity)-> String = { it.items.joinToString { i -> i.id } }
    when(this) {
        is BiCluster -> "${this.right.id}[${itemsStr(this.right)}]: [${this.similarity}]"
        is EntityCluster -> "${this.left.id}[${itemsStr(this.left)}] -- ${this.right.id}[${itemsStr(this.right)}]: [${this.similarity}]"
    }.run {
        println(this)
    }
}