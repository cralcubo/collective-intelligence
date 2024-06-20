package com.croman.clustering

import com.croman.collaborative.filtering.EuclideanDistance
import com.croman.collaborative.filtering.PearsonCorrelation
import com.croman.collaborative.filtering.PearsonCorrelationModified
import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.utils.Entity
import com.croman.utils.Feature
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asSequence

private const val MOVIE_RATINGS = "/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/ratings.csv"

fun main() {
    val u1 = "1"
    val u2 = "210"
    val minSize = 10

//    printSimilarUsersByMovieRatings(PearsonCorrelationModified(minSize), u1, 10 )
    analyzeRecommendedFilms(EuclideanDistance(minSize), u1)
}

private fun analyzeRecommendedFilms(similarityCalculator: SimilarityCalculator, userId: String) {
    val recommended = movieRecommender(similarityCalculator, userId)
        .sortedByDescending { it.rating }

    println("${recommended.size} movies recommended")
    println("Top 10 are: ${recommended.take(10)}")
    println("Last 10 are:${recommended.drop(recommended.size - 10)} ")
}


private fun compareUsersBySimilarity(similarityCalculator: SimilarityCalculator, u1: String, u2: String) {
    val rf = loadUsersRatedFilms()
    val userA = rf.find { it.userId == u1 }!!
    val userB = rf.find { it.userId == u2 }!!
    println("User ${userA.userId} rated ${userA.moviesRated.size} movies")
    println("User ${userB.userId} rated ${userB.moviesRated.size} movies")
    // Similar movies ratings
    val similarMovies = userA.moviesRated.asSequence()
        .flatMap { m1 ->
            userB.moviesRated.asSequence()
                .filter { it.id == m1.id }
                .map { m1 to it }
        }
        .toList()

    println("Similar rated movies[${similarMovies.size}]")

    println("Distance between ${userA.userId} and ${userB.userId} = ${
        similarityCalculator.calculate(
            userA.toEntity(),
            userB.toEntity()
        )
    }")
}

private fun printSimilarUsersByMovieRatings(similarityCalculator: SimilarityCalculator, user: String, topUsersSize: Int) {
    val similar = findClosestMatchesBySimilarRankedFilms(similarityCalculator, topUsersSize, user)
    println("The $topUsersSize most similar are: ${similar.map { it.user.userId to it.similarityScore }}")
}

fun findClosestMatchesBySimilarRankedFilms(calculator: SimilarityCalculator, sampleSize: Int = 10, userId: String): List<UserSimilarity> {
    val ratingsByUser = loadUsersRatedFilms()
        .map { it.toEntity() }

    val u = ratingsByUser.find { it.id == userId }!!
    return ratingsByUser
        .filter { it.id != userId }
        .sortedByDescending {
            calculator.calculate(u, it)
        }
        .take(sampleSize)
        .map { UserSimilarity(it.toUser(), calculator.calculate(u, it)) }
}

fun movieRecommender(similarityCalculator: SimilarityCalculator, userId: String): List<Movie> {
    //find all the films that were not rated by user
    val usersRatedFilms = loadUsersRatedFilms()
    val user = usersRatedFilms.find { it.userId == userId }!!

    val movieSuggestions = mutableListOf<SuggestedMovie>()
    for(currentUser in usersRatedFilms) {
        if(currentUser.userId == userId) continue

        val similarity = similarityCalculator.calculate(currentUser.toEntity(), user.toEntity())
        if(similarity < 0.5) continue

        // find the films to suggest
        val moviesSuggestedByCurrentUser  = currentUser.moviesRated
            .filter { m -> !user.moviesRated.map { it.id }.contains(m.id) }
            .map { SuggestedMovie(Movie(it.id, it.rating * similarity.toFloat()), similarity) }

        movieSuggestions.addAll(moviesSuggestedByCurrentUser)

    }

    return movieSuggestions.groupBy { it.movie.id }
        .map { e ->
            val movieId = e.key
            val ratingsAdded = e.value.map { it.movie.rating }.sum()
            val simSum = e.value.sumOf { it.similarityScore }
            Movie(movieId, ratingsAdded/simSum.toFloat())
        }
}


private fun User.toEntity() =
    Entity(
        id = userId,
        features = moviesRated.map { Feature(it.id, it.rating.toDouble()) }.toSet()
    )

private fun Entity.toUser() =
    User(id, features.map { Movie(it.id, it.weight.toFloat()) }.toList())

@Throws(java.io.IOException::class)
private fun loadUsersRatedFilms() =
    Files.lines(Paths.get(MOVIE_RATINGS)).asSequence()
        .drop(1)
        .map {
            val values = it.split(",")
            values[0] to Movie(values[1], values[2].toFloat())
        }
        .groupBy({ it.first }, { it.second })
        .map { User(it.key, it.value) }

data class UserSimilarity(val user: User, val similarityScore: Double)
data class User(val userId: String, val moviesRated: List<Movie>)
data class Movie(val id: String, val rating: Float)
data class SuggestedMovie(val movie: Movie, val similarityScore: Double)
