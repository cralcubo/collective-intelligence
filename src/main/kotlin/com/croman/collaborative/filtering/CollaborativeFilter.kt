package com.croman.collaborative.filtering

interface CollaborativeFilter {

    fun calculate(e1: Entity, e2: Entity) : Double
}