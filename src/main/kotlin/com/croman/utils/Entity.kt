package com.croman.utils

data class Entity(val id: String, val features: Set<Feature>)

data class Feature(val id: String, val weight: Double)

