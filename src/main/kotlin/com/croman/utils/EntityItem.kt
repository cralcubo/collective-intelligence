package com.croman.utils

import com.fasterxml.jackson.annotation.JsonIgnore

data class Entity(val id: String, val items: Set<Item>)

data class Item(val id: String, val value: Double)

