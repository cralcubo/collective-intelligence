package com.croman.genetic.programming

import kotlin.random.Random

// Function Wrapper class
class Wrapper(
    val childCount: Int,
    val name: String,
    val function: (List<Double>) -> Double
)

interface INode {
    fun evaluate(vararg inputs: Double): Double
    fun display(indent: Int = 0)
}

// Node class
class Node(
    wrapper: Wrapper,
    val children: List<INode>
) : INode {
    private val function = wrapper.function
    private val name = wrapper.name

    override fun evaluate(vararg inputs: Double): Double {
        val results = children.map { it.evaluate(*inputs) }
        return function(results)
    }

    override fun display(indent: Int) {
        println(" ".repeat(indent) + name)
        children.forEach { it.display(indent + 1) }
    }
}

// Parameter Node class
class ParamNode(private val inputIndex: Int) : INode {
    override fun evaluate(vararg inputs: Double): Double {
        return inputs[inputIndex]
    }

    override fun display(indent: Int) {
        println("${" ".repeat(indent)}p$inputIndex")
    }

}

// Constant Node class
class ConstNode(private val value: Double): INode {
    override fun evaluate(vararg inputs: Double): Double {
        return value
    }

    override fun display(indent: Int) {
        println("${" ".repeat(indent)}$value")
    }
}

fun makeRandomTree(parametersSize: Int, maxDepth: Int = 4, fNodePr: Double = 0.5, pNodePr: Double = 0.6, wrappers: List<Wrapper>): INode =
    when {
        Random.nextDouble() < fNodePr && maxDepth > 0 -> {
            val wrapper = wrappers.random()
            val children = List(wrapper.childCount) {
                makeRandomTree(parametersSize, maxDepth - 1, fNodePr, pNodePr, wrappers)
            }
            Node(wrapper, children)
        }
        Random.nextDouble() < pNodePr -> ParamNode(Random.nextInt(0, parametersSize))
        else -> ConstNode(Random.nextDouble(0.0, 10.0))
    }

/**
 * Testing
 */

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
    if(it[0] > it[1]) 1.0
    else 0.0
}

private val wrappers = listOf(addW, subW, mulW, ifW, gtW)
fun main() {
    val t1 = makeRandomTree(parametersSize = 1, wrappers = wrappers)
    val t2 = makeRandomTree(parametersSize = 2, wrappers = wrappers)
    val t3 = makeRandomTree(parametersSize = 3, wrappers = wrappers)

    t1.display()
    println("---")
    t2.display()
    println("---")
    t3.display()
    println("---")


}

