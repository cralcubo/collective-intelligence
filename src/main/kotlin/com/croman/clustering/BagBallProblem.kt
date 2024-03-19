package com.croman.clustering

import com.croman.utils.bestEntityFinder
import com.croman.utils.bestPairFinder
import com.croman.utils.bestPairFinder2
import kotlin.math.abs

fun finderNLogNGeneric(balls: List<Int>): Pair<BallPair, List<Int>> {
    println(balls)
    val (pair, bs) = bestPairFinder2(balls) { e, testBalls ->
        val s = testBalls.sumOf { abs(e - it).toDouble() }
        println("$e sumOf $s")
        return@bestPairFinder2 s
    }
    return BallPair(pair.first, pair.second) to bs
}

fun finderNLogN(balls: List<Int>): Pair<BallPair, List<Int>> {
    require(balls.size > 1) { "There must be at least 2 balls in the bag" }

    val sortedBalls = balls.sorted() // *** This is the optimizer!
    var minDistance = Int.MAX_VALUE
    var closestPair: BallPair? = null
    var index = 0

    while (index < sortedBalls.size - 1) {
        val b1 = sortedBalls[index]
        val b2 = sortedBalls[index + 1]
        val distance = abs(b1 - b2)

        if (distance < minDistance) {
            minDistance = distance
            closestPair = BallPair(b1, b2)
        }

        index++
    }

    requireNotNull(closestPair)

    val remainingBalls = mutableListOf<Int>()
    for (ball in sortedBalls) {
        if (ball != closestPair.b1 && ball != closestPair.b2) {
            remainingBalls.add(ball)
        }
    }

    return closestPair to remainingBalls
}

// Find an optimal way to the problem
// of finding two balls that are equal or at least
// close to each other leaving the bag with the resting balls
fun finderN2(balls: List<Int>) : Pair<BallPair, List<Int>> {
    require(balls.size > 1) { "There must be at least 2 balls in the bag" }

    var bestPair: BallPair? = null
    var bag0 = balls.toMutableList()
    var bag1 = mutableListOf<Int>()
    val remainsBag = mutableListOf<Int>()

    while (bag0.isNotEmpty()) {
        val b1 = bag0.first()
        val searchBag = bag0.drop(1)
        var iterationSucceed = false // Iteration succeeds if the best pair was found
        if(searchBag.isEmpty()) {
            // b1 was the last ball in the bag, check if there was a better pair
            // comparing with the second element of the previous best pair
            if(bestPair != null) {
                val b2 = bestPair.b2
                if(bestPair.distance > abs(b1 - b2)) {
                    remainsBag.add(bestPair.b1)
                    bestPair = BallPair(b1, b2)
                    iterationSucceed = true
                }
            }
        }

        for((i, b2) in searchBag.withIndex()) {
            // If a perfect pair is found (both balls are equal) stop the operation
            // and add the resting balls to bagR
            if(b1 == b2) {
                remainsBag.addAll(bag1)
                if(bestPair != null) {
                    remainsBag.add(bestPair.b1)
                    remainsBag.add(bestPair.b2)
                }
                // add the resting elements to the bag
                remainsBag.addAll(searchBag.subList(i + 1, searchBag.size))
                return BallPair(b1,b2) to remainsBag
            }

            if(bestPair == null || bestPair.distance > abs(b1 - b2)) {
                if(bestPair != null) {
                    bag1.add(bestPair.b2)
                    if(b1 != bestPair.b1) {
                        remainsBag.add(bestPair.b1)
                    }
                }
                bestPair = BallPair(b1, b2)
                iterationSucceed = true
            } else {
                bag1.add(b2)
            }
        }
        if(!iterationSucceed) {
            // There was no success beating the previous best pair
            // throw b1 to the bagR
            remainsBag.add(b1)
        }
        // replace bag0 with bag1 to iterate with the resting elements
        bag0 = bag1
        bag1 = mutableListOf() // create a new empty bag1
    }

    require(bestPair != null){ "A best pair was not found after iteration, something went wrong!" }
    return bestPair to remainsBag
}

fun finderGeneric(balls: List<Int>) : Pair<BallPair, List<Int>> {
    val (pair, ints) = bestPairFinder(balls) { b1, b2 ->
        abs(b1 - b2).toDouble()
    }
    return BallPair(pair.first, pair.second) to ints
}

fun bestBall(b: Int, bs: List<Int>) : Pair<Int, List<Int>> {
    return bestEntityFinder(b, bs) { b1,b2 -> abs(b2 - b1).toDouble() }
}



data class BallPair(val b1: Int, val b2: Int) {
    val distance: Int
        get() = abs(b1 - b2)
}

