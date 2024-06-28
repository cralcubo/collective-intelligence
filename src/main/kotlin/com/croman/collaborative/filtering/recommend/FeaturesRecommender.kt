package com.croman.collaborative.filtering.recommend

import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Feature

class FeaturesRecommender(private val similarityCalculator: SimilarityCalculator) {

    /**
     * Based on all the entities provided it finds all the features recommended
     * to the [entityId]
     * To calculate such recommendation the [SimilarityCalculator] provided is used to find
     * the similarities between the [entityId] and all the other subjects present in the
     * [entities] collection.
     * The weight of the recommended features is calculated with the formula:
     * w = Sum(Fw * similarity) / Sum(similarities)
     * The recommended features are [Feature]s that the subject lacks.
     *
     * @param entities all the entities to base the recommendation
     * @param entityId the ID of the entity to recommend
     * @param similarityLimit the similarity value from which recommendations will be considered
     * @return a sorted list with all the [Feature]s recommended to [entityId]
     */
    fun recommend(entities: List<Entity>, entityId: String, similarityLimit: Double) : List<Feature> {
        val entity = entities.find { it.id == entityId }
            ?: throw IllegalArgumentException("The entity $entityId was not found")
        val entityFeaturesId = entity.features.map(Feature::id)

        return entities.asSequence()
            .filter { it.id != entityId }
            .map { it to similarityCalculator.calculate(it, entity) }
            .filter { it.second > similarityLimit }
            .flatMap {
                it.first.features.asSequence()
                    .filter { f -> !entityFeaturesId.contains(f.id) }
                    .map { f -> Suggestion(Feature(f.id, f.weight * it.second), it.second) }
            }
            .groupBy { it.feature.id }
            .map {
                val id = it.key
                val sumWeights = it.value.sumOf { sug -> sug.feature.weight }
                val sumSimilarity = it.value.sumOf { sug -> sug.similarity }
                Feature(id, sumWeights/sumSimilarity)
            }
            .sortedByDescending { it.weight }
    }
}

private data class Suggestion(val feature: Feature, val similarity: Double)