package com.croman.collaborative.filtering.match

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity

/**
 * Find all the entities sorted by similarity.
 */
class TopMatchFinder(private val similarityCalculator: SimilarityCalculator) {

    /**
     * @param entityId of the entity to find all the other entities similar to it
     * @param entities all the entities to compare against to
     * @param size the number of matches requested
     * @return the [size] matches found
     * @throws IllegalArgumentException if the [entityId] does not correspond with any of the [entities] provided
     */
    fun find(entityId: String, entities: List<Entity>, size: Int): List<Pair<Entity, Double>> {
        val entity = entities.find { it.id == entityId }
            ?: throw IllegalArgumentException("The entity $entityId was not found")

        return entities.asSequence()
            .filter { it.id != entityId }
            .map { it to similarityCalculator.calculate(it, entity) }
            .sortedByDescending { it.second }
            .take(size)
            .toList()
    }

}