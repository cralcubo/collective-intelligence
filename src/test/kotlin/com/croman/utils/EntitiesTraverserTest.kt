package com.croman.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EntitiesTraverserTest {

    @Test
    fun traverseEntities() {
        val e1 = Entity("e1", setOf(Feature("f1", 10.0), Feature("f2", 5.0)))
        val e2 = Entity("e2", setOf(Feature("f1", 7.0), Feature("f2", 8.0), Feature("f3", 1.0)))
        val e3 = Entity("e3", setOf(Feature("f1", 1.0), Feature("f3", 3.0)))
        val traverse = traverseEntities(listOf(e1, e2, e3))

        // expected
        val f1 = Entity("f1", setOf(Feature("e1", 10.0), Feature("e2", 7.0), Feature("e3", 1.0 )))
        val f2 = Entity("f2", setOf(Feature("e1", 5.0), Feature("e2", 8.0)))
        val f3 = Entity("f3", setOf(Feature("e2", 1.0), Feature("e3", 3.0)))
        traverse shouldBe listOf(f1,f2,f3)
    }

}