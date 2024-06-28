package com.croman.utils


fun traverseEntities(source: List<Entity>) =
    source.asSequence()
        .flatMap {
            it.features.asSequence()
                .map { f -> Entity(f.id, setOf(Feature(it.id, f.weight))) }
        }
        .groupBy({ it.id }, { it.features })
        .map { Entity(it.key, it.value.flatMap { fs -> fs.asSequence() }.toSet()) }