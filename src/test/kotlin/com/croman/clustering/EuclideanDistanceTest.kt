package com.croman.clustering

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import com.croman.clustering.EuclideanDistance.Companion as calculator

class EuclideanDistanceTest {

    @Test
    fun calculateOneItem() {
        val e1 = Entity(
            id = "1",
            items = setOf(
                Item("1", 4.0),
            )
        )

        val e2 = Entity(
            id = "2",
            items = setOf(
                Item("1", 3.0),
            )
        )

        calculator.calculate(e1,e2) shouldBe 0.5
    }

    @Test
    fun calculateTwoItems() {
        val e1 = Entity(
            id = "1",
            items = setOf(
                Item("1", 7.0),
                Item("2", 9.0),
            )
        )

        val e2 = Entity(
            id = "2",
            items = setOf(
                Item("1", 3.0),
                Item("2", 6.0),
            )
        )

        calculator.calculate(e1,e2) shouldBe (1.0/6)
    }

    @Test
    fun calculateNoCommonItems() {
        val e1 = Entity(
            id = "2",
            items = setOf(
                Item("1", 4.0),
            )
        )

        val e2 = Entity(
            id = "1",
            items = setOf(
                Item("1", 3.0),
            )
        )

        calculator.calculate(e1,e2) shouldBe 0.5
    }

}