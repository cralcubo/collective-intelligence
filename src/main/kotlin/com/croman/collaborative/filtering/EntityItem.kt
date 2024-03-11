package com.croman.collaborative.filtering

data class Entity(val id: String, val items: Set<Item>)

data class Item(val id: String, val value: Double)

