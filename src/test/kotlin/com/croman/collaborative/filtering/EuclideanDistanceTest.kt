package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Item
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EuclideanDistanceTest {

    private val filter = EuclideanDistance()

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

        filter.calculate(e1,e2) shouldBe 0.5
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

        filter.calculate(e1,e2) shouldBe (1.0/6)
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

        filter.calculate(e1,e2) shouldBe 0.5
    }

    @Test
    fun calculateSameItems() {
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
                Item("1", 7.0),
                Item("2", 9.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0
    }

}