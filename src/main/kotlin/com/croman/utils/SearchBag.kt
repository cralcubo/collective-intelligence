package com.croman.utils

/**
 * Brute force algorithm to find the best pair of a list of entities.
 * In the wort case this algorithm will last O(n^2)
 * @param distanceCalculator The shortest the distance the better, being 0 the best distance possible
 */
fun <E> bestPairFinder(entities: List<E>, distanceCalculator: (E, E) -> Double): Pair<Pair<E, E>, List<E>> {
    require(entities.size > 1) { "There must be at least 2 entities to find a pair" }
    // Because I have no idea how to sort entities to make the search of an inner
    // cluster better than O(N^2), I will proceed by brute force to find it :/
    var bestPair: Pair<E, E>? = null
    var bag0 = entities
    var bag1 = mutableListOf<E>()
    val remainsBag = mutableListOf<E>()

    while (bag0.isNotEmpty()) {
        val e1 = bag0.first()
        val searchBag = bag0.drop(1)
        var iterationSucceed = false // Iteration succeeds if the best inner-cluster was found
        if (searchBag.isEmpty()) {
            // e1 was the last entity in the bag, check if there was a better inner-cluster
            // comparing with the second entity of the previous best inner-cluster
            if (bestPair != null) {
                val e2 = bestPair.second
                val similarity = distanceCalculator(e1, e2)
                if (distanceCalculator(bestPair.first, bestPair.second) > similarity) {
                    remainsBag.add(bestPair.first)
                    bestPair = e1 to e2
                    iterationSucceed = true
                }
            }
        }

        for ((i, e2) in searchBag.withIndex()) {
            // If a perfect pair is found (both balls are equal) stop the operation
            // and add the resting entities to remainsBag
            val similarity = distanceCalculator(e1, e2)
            if (similarity == 0.0) {
                remainsBag.addAll(bag1)
                if (bestPair != null) {
                    remainsBag.add(bestPair.second)
                }
                // add the resting elements to the bag
                remainsBag.addAll(searchBag.subList(i + 1, searchBag.size))
                return Pair(e1, e2) to remainsBag
            }

            if (bestPair == null || distanceCalculator(bestPair.first, bestPair.second) > similarity) {
                if (bestPair != null) {
                    bag1.add(bestPair.second)
                    if (e1 != bestPair.first) {
                        remainsBag.add(bestPair.first)
                    }
                }
                bestPair = e1 to e2
                iterationSucceed = true
            } else {
                bag1.add(e2)
            }
        }

        if (!iterationSucceed) {
            // There was no success beating the previous best pair
            // throw b1 to the bagR
            remainsBag.add(e1)
        }
        // replace bag0 with bag1 to iterate with the resting elements
        bag0 = bag1
        bag1 = mutableListOf() // create a new empty bag1
    }


    require(bestPair != null) { "A best pair was not found after iteration, something went wrong!" }
    return bestPair to remainsBag
}

/**
 * Brute force algorithm that will iterate on all the entities to find the entity
 * which distance is the closest to the mainEntity.
 * @param distanceCalculator The shortest the distance the better, being 0 the best distance possible
 * @throws IllegalArgumentException if there are no [entities] to do a search
 */
fun <E> bestEntityFinder(mainEntity: E, entities: List<E>, distanceCalculator: (E, E) -> Double) : Pair<E, List<E>> {
    require(entities.isNotEmpty()) { "No entities to do a search" }
    val b1 = mutableListOf<E>()
    var bestEntity = entities.first()
    var bestDistance = distanceCalculator(mainEntity, bestEntity)
    for(e in entities.drop(1)){
        val currentDistance = distanceCalculator(mainEntity, e)
        if(currentDistance < bestDistance) {
            if(bestEntity != null) {
                b1.add(bestEntity)
            }
            bestEntity = e
            bestDistance = currentDistance
        } else {
            b1.add(e)
        }
    }

    return bestEntity to b1
}

fun <E> bestPairFinder2(entities: List<E>, distanceCalculator: (E, List<E>) -> Double): Pair<Pair<E, E>, List<E>> {
    require(entities.size > 1) { "There must be at least 2 entities to find a pair" }

    // Step 1: Sort the entities based on distance
    val sortedEntities = entities.sortedWith(compareBy { e1 ->
        distanceCalculator(e1, entities)
    })


    // Step 2: Find the closest pair
    var bestPair: Pair<E, E>? = null
    for (i in 0 until sortedEntities.size - 1) {
        val e1 = sortedEntities[i]
        val e2 = sortedEntities[i + 1]
        val distance = distanceCalculator(e1, listOf(e2))
        if (bestPair == null || distance < distanceCalculator(bestPair.first, listOf(bestPair.second))) {
            bestPair = e1 to e2
        }
    }

    // Construct the result
    val remainsBag = sortedEntities.filterNot { it == bestPair?.first || it == bestPair?.second }
    return bestPair!! to remainsBag
}


fun <E> bestPairFinderNLogN(entities: List<E>, distanceCalculator: (E, E) -> Double): Pair<Pair<E, E>, List<E>> {
    require(entities.size > 1) { "There must be at least 2 entities to find a pair" }

    // Step 1: Sort the entities based on distance
    val sortedEntities = entities.sortedWith(compareBy { e1 ->
        entities.minOf { e2 -> distanceCalculator(e1, e2) }
    })

    // Step 2: Find the closest pair
    var bestPair: Pair<E, E>? = null
    for (i in 0 until sortedEntities.size - 1) {
        val e1 = sortedEntities[i]
        val e2 = sortedEntities[i + 1]
        val distance = distanceCalculator(e1, e2)
        if (bestPair == null || distance < distanceCalculator(bestPair.first, bestPair.second)) {
            bestPair = e1 to e2
        }
    }

    // Construct the result
    val remainsBag = sortedEntities.filterNot { it == bestPair?.first || it == bestPair?.second }
    return bestPair!! to remainsBag
}