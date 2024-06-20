package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Feature
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TanimotoCoefficientTest {
    private val calculator = TanimotoCoefficient()
    @Test
    fun sameItems() {
        val e1 = Entity(
            "1",
            setOf(
                Feature("a", 1.0),
                Feature("b", 1.0),
                Feature("c", 1.0),
                )
        )

        calculator.calculate(e1, e1) shouldBe 1.0
    }

    @Test
    fun noItemsInCommon() {
        val e1 = Entity(
            "1",
            setOf(
                Feature("a", 1.0),
                Feature("b", 1.0),
                Feature("c", 1.0),
            )
        )
        val e2 = Entity(
            "2",
            setOf(
                Feature("x", 1.0),
                Feature("y", 1.0),
                Feature("z", 1.0),
            )
        )

        calculator.calculate(e1, e2) shouldBe 0.0
    }

    @Test
    fun someItemsInCommon() {
        val e1 = Entity(
            "1",
            setOf(
                Feature("a", 1.0),
                Feature("b", 1.0),
                Feature("c", 1.0),
            )
        )
        val e2 = Entity(
            "2",
            setOf(
                Feature("a", 1.0),
                Feature("b", 1.0),
                Feature("z", 1.0),
            )
        )

        calculator.calculate(e1, e2) shouldBe 0.5
    }
}