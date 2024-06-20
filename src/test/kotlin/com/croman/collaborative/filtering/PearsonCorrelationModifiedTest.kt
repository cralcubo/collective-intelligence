package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Feature
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PearsonCorrelationModifiedTest {

    private val filter = PearsonCorrelationModified(1)

    @Test
    fun calculateZeroItemsInCommon() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("A", 4.0),
                Feature("B", 4.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("C", 3.0),
                Feature("D", 3.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 0.0
    }

    @Test
    fun calculateNotEnoughItems() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("A", 4.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("A", 3.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 0.0
    }

    @Test
    fun calculateRepeatedWeights() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("A", 4.0),
                Feature("B", 4.0),
                Feature("C", 4.0),
                Feature("D", 7.0),
                Feature("E", 8.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("A", 5.0),
                Feature("B", 5.0),
                Feature("C", 5.0),
                Feature("D", 8.0),
                Feature("E", 9.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0
    }

    @Test
    fun calculatePositiveCorrelativeItems() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("1", 4.0),
                Feature("2", 5.0),
                Feature("3", 6.0),
                Feature("4", 7.0),
                Feature("5", 8.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("1", 5.0),
                Feature("2", 6.0),
                Feature("3", 7.0),
                Feature("4", 8.0),
                Feature("5", 9.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0
    }

    @Test
    fun calculateNegativeCorrelativeItems() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("1", 7.0),
                Feature("2", 9.0),
                Feature("3", 5.0),
                Feature("4", 10.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("1", -7.0),
                Feature("2", -9.0),
                Feature("3", -5.0),
                Feature("4", -10.0),
            )
        )

        filter.calculate(e1,e2) shouldBe -1.0
    }

    @Test
    fun calculateNonCorrelativeItems() {
        val e1 = Entity(
            id = "1",
            features = setOf(
                Feature("1", 1.0),
                Feature("2", 3.0),
                Feature("3", 7.0),
                Feature("4", 5.0),
            )
        )

        val e2 = Entity(
            id = "2",
            features = setOf(
                Feature("1", 1.0),
                Feature("2", 7.0),
                Feature("3", 3.0),
                Feature("4", 5.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0/5
    }

}