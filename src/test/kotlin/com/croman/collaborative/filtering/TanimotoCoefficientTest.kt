package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Value
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TanimotoCoefficientTest {
    private val calculator = TanimotoCoefficient()
    @Test
    fun sameItems() {
        val e1 = Entity(
            "1",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("c", 1.0),
                )
        )

        calculator.calculate(e1, e1) shouldBe 1.0
    }

    @Test
    fun noItemsInCommon() {
        val e1 = Entity(
            "1",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("c", 1.0),
            )
        )
        val e2 = Entity(
            "2",
            setOf(
                Value("x", 1.0),
                Value("y", 1.0),
                Value("z", 1.0),
            )
        )

        calculator.calculate(e1, e2) shouldBe 0.0
    }

    @Test
    fun someItemsInCommon() {
        val e1 = Entity(
            "1",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("c", 1.0),
            )
        )
        val e2 = Entity(
            "2",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("z", 1.0),
            )
        )

        calculator.calculate(e1, e2) shouldBe 0.5
    }
}