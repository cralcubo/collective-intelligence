package com.croman.collaborative.filtering.recommend

import com.croman.collaborative.filtering.EuclideanDistance
import com.croman.collaborative.filtering.PearsonCorrelation
import com.croman.collaborative.filtering.PearsonCorrelationModified
import com.croman.utils.Entity
import com.croman.utils.Feature
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FeaturesRecommenderTest {
    private val entities: List<Entity> = listOf(
        Entity("Lisa Rose" , setOf(
            Feature("Lady in the Water", 2.5),
            Feature("Snakes on a Plane", 3.5),
            Feature("Just My Luck", 3.0),
            Feature("Superman Returns", 3.5),
            Feature("You, Me and Dupree", 2.5),
            Feature("The Night Listener", 3.0)
        )),
        Entity("Gene Seymour", setOf(
            Feature("Lady in the Water", 3.0),
            Feature("Snakes on a Plane", 3.5),
            Feature("Just My Luck", 1.5),
            Feature("Superman Returns", 5.0),
            Feature("The Night Listener", 3.0),
            Feature("You, Me and Dupree", 3.5)
        )),
        Entity("Michael Phillips" , setOf(
            Feature("Lady in the Water", 2.5),
            Feature("Snakes on a Plane", 3.0),
            Feature("Superman Returns", 3.5),
            Feature("The Night Listener", 4.0)
        )),
        Entity("Claudia Puig" , setOf(
            Feature("Snakes on a Plane", 3.5),
            Feature("Just My Luck", 3.0),
            Feature("The Night Listener", 4.5),
            Feature("Superman Returns", 4.0),
            Feature("You, Me and Dupree", 2.5)
        )),
        Entity("Mick LaSalle" , setOf(
            Feature("Lady in the Water", 3.0),
            Feature("Snakes on a Plane", 4.0),
            Feature("Just My Luck", 2.0),
            Feature("Superman Returns", 3.0),
            Feature("The Night Listener", 3.0),
            Feature("You, Me and Dupree", 2.0)
        )),
        Entity("Jack Matthews" , setOf(
            Feature("Lady in the Water", 3.0),
            Feature("Snakes on a Plane", 4.0),
            Feature("The Night Listener", 3.0),
            Feature("Superman Returns", 5.0),
            Feature("You, Me and Dupree", 3.5)
        )),
        Entity("Toby" , setOf(
            Feature("Snakes on a Plane", 4.5),
            Feature("You, Me and Dupree", 1.0),
            Feature("Superman Returns", 4.0)
        ))
    )

    @Test
    fun recommendationsMoviesBook() {
        val recommender = FeaturesRecommender(PearsonCorrelation(1))
        val features = recommender.recommend(entities, "Toby", 0.1)
        println(features)
    }

    @Test
    fun recommendations() {
        val similarityCalculator = PearsonCorrelationModified(1)
        val e1 = Entity("e1", setOf(Feature("m1", 10.0), Feature("m2", 8.0), Feature("m4", 6.0)))

        val e2 = Entity("e2", setOf(Feature("m1", 10.0), Feature("m2", 8.0), Feature("m3", 4.0), Feature("m4", 6.0)))
        val e3 = Entity("e3", setOf(Feature("m1", 5.0), Feature("m2", 4.0), Feature("m3", 2.0), Feature("m4", 3.0)))
        val e4 = Entity("e4", setOf(Feature("m1", 8.0), Feature("m2", 6.0), Feature("m3", 2.0), Feature("m4", 4.0)))

        val recommend = FeaturesRecommender(similarityCalculator).recommend(listOf(e1, e2, e3, e4), e1.id, 0.5)
        recommend shouldBe listOf(Feature("m3", 8.0/3.0))
    }

}