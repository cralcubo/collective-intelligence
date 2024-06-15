package com.croman.collaborative.filtering

import com.croman.utils.Entity
import com.croman.utils.Value
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PearsonCorrelationTest {

    private val filter = PearsonCorrelation()

    @Test
    fun calculateOneItem() {
        val e1 = Entity(
            id = "1",
            values = setOf(
                Value("1", 4.0),
            )
        )

        val e2 = Entity(
            id = "2",
            values = setOf(
                Value("1", 3.0),
            )
        )
        assertThrows<IllegalArgumentException> {
            filter.calculate(e1,e2)
        }
    }

    @Test
    fun calculateTwoItems() {
        val e1 = Entity(
            id = "1",
            values = setOf(
                Value("1", 7.0),
                Value("2", 9.0),
            )
        )

        val e2 = Entity(
            id = "2",
            values = setOf(
                Value("1", 5.0),
                Value("2", 6.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0
    }

    @Test
    fun calculateNotEnoughItems() {
        val e1 = Entity(
            id = "2",
            values = setOf(
                Value("1", 4.0),
            )
        )

        val e2 = Entity(
            id = "1",
            values = setOf(
                Value("1", 3.0),
            )
        )
        assertThrows<IllegalArgumentException> { filter.calculate(e1, e2) }
    }

    @Test
    fun calculateCorrelativeItems() {
        val e1 = Entity(
            id = "1",
            values = setOf(
                Value("1", 7.0),
                Value("2", 9.0),
                Value("3", 5.0),
                Value("4", 10.0),
            )
        )

        val e2 = Entity(
            id = "2",
            values = setOf(
                Value("1", 7.0),
                Value("2", 9.0),
                Value("3", 5.0),
                Value("4", 10.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0
    }

    @Test
    fun calculateNonCorrelativeItems() {
        val e1 = Entity(
            id = "1",
            values = setOf(
                Value("1", 1.0),
                Value("2", 3.0),
                Value("3", 7.0),
                Value("4", 5.0),
            )
        )

        val e2 = Entity(
            id = "2",
            values = setOf(
                Value("1", 1.0),
                Value("2", 7.0),
                Value("3", 3.0),
                Value("4", 5.0),
            )
        )

        filter.calculate(e1,e2) shouldBe 1.0/5
    }

}