package com.croman.genetic.programming

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GPKtTest {
    // Math operations
    private val addW = Wrapper(name = "add", childCount = 2) { it[0] + it[1] }
    private val subW = Wrapper(name = "subtract", childCount = 2) { it[0] - it[1] }
    private val mulW = Wrapper(name = "multiply", childCount = 2) { it[0] * it[1] }

    // Logical operations
    private val ifW = Wrapper(name = "if", childCount = 3) {
        if(it[0] > 0) it[1]
        else it[2]
    }
    private val gtW = Wrapper(name = "gt", childCount = 2) {
        if(it[0] > it[1]) 1
        else 0
    }


    // Trees
    private fun ifTree() =
        Node(wrapper = ifW,
            children = listOf(
                Node(gtW, listOf(ParamNode(0), ConstNode(3))), // First operation
                Node(addW, listOf(ParamNode(1), ConstNode(5))), // gtW -> T
                Node(subW, listOf(ParamNode(1), ConstNode(2))) // gtW -> F
            )
        )

    private fun addTree() =
        Node(wrapper = addW,
            children = listOf(
                ParamNode(0), ParamNode(1)
            )
        )

    private fun gtTree() =
        Node(wrapper = gtW,
            children = listOf(ParamNode(0), ParamNode(1))
        )

    @Test
    fun isGreaterThanTest() {
        gtTree().evaluate(3, -1) shouldBe 1
        gtTree().evaluate(10, 100) shouldBe 0
    }

    @Test
    fun ifTest() {
        ifTree().evaluate(2, 3) shouldBe 1
        ifTree().evaluate(5, 10) shouldBe 15
    }

    @Test
    fun addTest() {
        addTree().evaluate(99, 1) shouldBe 100
        addTree().evaluate(5, 5) shouldBe 10.0
    }



}