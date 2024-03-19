package com.croman.collaborative.filtering

interface CollaborativeFilter {
    /**
     * A value that indicate how close 2 entities are.
     * The closer they are (meaning the most similar they are)
     * the value will be: 1
     * The less close they are (meaning the least related they are)
     * the value will be: 0
     */
    fun calculate(e1: Entity, e2: Entity) : Double
}