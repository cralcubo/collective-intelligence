package com.croman.genetic.programming

import kotlin.random.Random

// Function Wrapper class
class Wrapper(
    val childCount: Int,
    val name: String,
    val function: (List<Int>) -> Int
)

interface INode {
    fun evaluate(vararg inputs: Int): Int
    fun display(indent: Int = 0)
}

// Node class
class Node(
    wrapper: Wrapper,
    val children: List<INode>
) : INode {
    private val function = wrapper.function
    private val name = wrapper.name

    override fun evaluate(vararg inputs: Int): Int {
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
    override fun evaluate(vararg inputs: Int): Int {
        return inputs[inputIndex]
    }

    override fun display(indent: Int) {
        println("${" ".repeat(indent)}p$inputIndex")
    }

}

// Constant Node class
class ConstNode(private val value: Int): INode {
    override fun evaluate(vararg inputs: Int): Int {
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
        else -> ConstNode(Random.nextInt(0, 10))
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
    if(it[0] > it[1]) 1
    else 0
}

private val wrappers = listOf(addW, subW, mulW, ifW, gtW)
fun main() {
    val rt = makeRandomTree(parametersSize = 2, wrappers = wrappers)
    rt.display()

    println(">>>Op 1<<<")
    println( rt.evaluate(2,5) )

    println(">>>Op 2<<<")
    println( rt.evaluate(-2, 10) )
}

