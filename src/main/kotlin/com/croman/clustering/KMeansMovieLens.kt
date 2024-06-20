package com.croman.clustering

import com.croman.collaborative.filtering.TanimotoCoefficient
import com.croman.utils.Entity
import com.croman.utils.MovieLensFactory
import com.croman.utils.MovieLensMovie
import com.croman.utils.Feature
import java.nio.file.Paths

@Throws(java.lang.IllegalArgumentException::class)
fun main() {
    val entities = loadMovies().map { it.toEntity() }
    println("Loaded ${entities.size} movies")
    // pick a film: Nightmare on Elm Street, A (1984) and based on the genre, collect all films that have the
    // same genres
    val movie = entities.find { it.id == "From Dusk Till Dawn (1996)" }!!
    val genres = movie.features.map { it.id }.toSet()
    val toEval = entities.filter { m -> m.features.map { it.id }.intersect(genres).isNotEmpty() }
    println("To eval ${toEval.size} films")

    val clusters = clusterer(toEval, TanimotoCoefficient(), 3)
    println("Created ${clusters.size} clusters")

}

private fun MovieLensMovie.toEntity() =
    Entity(
        id = this.title,
        features = this.genres.map { Feature(it.lowercase(), 1.0) }.toSet()
    )



private fun loadMovies() : List<MovieLensMovie> {
    val path =
        Paths.get("/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv")
    return MovieLensFactory.createMovies(path)
}

