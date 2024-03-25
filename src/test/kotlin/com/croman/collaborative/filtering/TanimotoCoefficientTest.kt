package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Item
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TanimotoCoefficientTest {
    private val calculator = TanimotoCoefficient()
    @Test
    fun sameItems() {
        val e1 = Entity(
            "1",
            setOf(
                Item("a", 1.0),
                Item("b", 1.0),
                Item("c", 1.0),
                )
        )

        calculator.calculate(e1, e1) shouldBe 1.0
    }

    @Test
    fun noItemsInCommon() {
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
                Item("x", 1.0),
                Item("y", 1.0),
                Item("z", 1.0),
            )
        )

        calculator.calculate(e1, e2) shouldBe 0.0
    }

    @Test
    fun someItemsInCommon() {
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

        calculator.calculate(e1, e2) shouldBe 0.5
    }
}