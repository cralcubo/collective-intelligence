package com.croman.clustering

import com.croman.collaborative.filtering.EuclideanDistance
import com.croman.collaborative.filtering.PearsonCorrelation
import com.croman.collaborative.filtering.PearsonCorrelationModified
import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.collaborative.filtering.match.TopMatchFinder
import com.croman.collaborative.filtering.recommend.FeaturesRecommender
import com.croman.utils.Entity
import com.croman.utils.Feature
import com.croman.utils.traverseEntities
import java.nio.file.Files.*
import java.nio.file.Paths
import kotlin.math.roundToInt
import kotlin.streams.asSequence

private const val MOVIE_RATINGS = "/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/ratings.csv"
private const val MOVIES = "/Users/croman/git/collective-intelligence/src/main/resources/com/croman/collaborative/filtering/movie-lens/movies.csv"

private fun averageFeatureSize(entityId: String, entities: List<Entity>) : Int {
    val entity = entities.find { it.id == entityId }!!
    val featuresId = entity.features.map { it.id }.toSet()
    return entities.asSequence()
        .map { it.features.map { f -> f.id }.intersect(featuresId).size }
        .filter { it > 0 }
        .max()
}

fun main() {
    val u1 = "1"
    val u2 = "210"
    val m = "318" // Shawshank Redemption
    val m1 = "1347" // Freddy
    val m2 = "596" // pinocchio
    val m3 = "356" // forrest gump
    val m4 = "1954" // Rocky
    val m5 = "1960" // last emperor
    val m6  = "4993" //LTR


//    printSimilarUsersByMovieRatings(PearsonCorrelationModified(minSize), u1, 10 )
//    analyzeRecommendedFilms(PearsonCorrelationModified(minSize), u1)
//    analyzeSimilarUsersByMovieRatings(PearsonCorrelationModified(minSize), u1, 10)
    val theMovie = m6
    val minSize = 100
    println("Min Size: $minSize")
    analyzeSimilarFilmsByUserMovieRatings(EuclideanDistance(minSize), 10, theMovie )
    println("----")
    analyzeSimilarFilmsByUserMovieRatings(PearsonCorrelationModified(minSize), 10, theMovie )
    println("----")
    analyzeSimilarFilmsByUserMovieRatings(PearsonCorrelation(minSize), 10, theMovie )

}

private fun analyzeRecommendedFilms(similarityCalculator: SimilarityCalculator, userId: String) {
    val recommender = FeaturesRecommender(similarityCalculator)
    val recommended = recommender.recommend(loadUsersRatedFilms().map { it.toEntity() }, userId, 0.5)
        .map { MovieRating(it.id, it.weight.toFloat()) }

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

private fun analyzeSimilarUsersByMovieRatings(similarityCalculator: SimilarityCalculator, user: String, topUsersSize: Int) {
    val similar = findClosestMatchesBySimilarRankedFilms(similarityCalculator, topUsersSize, user)
    println("The $topUsersSize most similar are: ${similar.map { it.user.userId to it.similarityScore }}")
}

private fun analyzeSimilarFilmsByUserMovieRatings(calculator: SimilarityCalculator, sampleSize: Int = 10, movieId: String) {
    val matches = findClosestMatchesFilms(calculator, sampleSize, movieId)
    println("The $sampleSize most similar films are: ${matches.map { it.first.title to it.second }}")
}

fun findClosestMatchesFilms(calculator: SimilarityCalculator, sampleSize: Int = 10, movieId: String): List<Pair<Movie, Double>> {
    val allMovies = loadMovies()
    println("Finding top matches for ${allMovies.find { it.id == movieId }}")

    val entities = traverseEntities(loadUsersRatedFilms().map { it.toEntity() })


    return TopMatchFinder(calculator).find(movieId, entities, sampleSize)
        .mapNotNull {
            allMovies.find { m->  m.id == it.first.id }?.let { m ->
                m to it.second
            }
        }
}

fun findClosestMatchesBySimilarRankedFilms(calculator: SimilarityCalculator, sampleSize: Int = 10, userId: String): List<UserSimilarity> {
    val ratingsByUser = loadUsersRatedFilms()
        .map { it.toEntity() }

    return TopMatchFinder(calculator).find(userId, ratingsByUser, sampleSize)
        .map { UserSimilarity(it.first.toUser(), it.second) }

//    val u = ratingsByUser.find { it.id == userId }!!
//    return ratingsByUser.asSequence()
//        .filter { it.id != userId }
//        .map { it to calculator.calculate(u, it) }
//        .sortedByDescending {it.second}
//        .take(sampleSize)
//        .map { UserSimilarity(it.first.toUser(), it.second) }
//        .toList()
}

fun movieRecommender(similarityCalculator: SimilarityCalculator, userId: String): List<MovieRating> {
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
            .map { SuggestedMovie(MovieRating(it.id, it.rating * similarity.toFloat()), similarity) }

        movieSuggestions.addAll(moviesSuggestedByCurrentUser)

    }

    return movieSuggestions.groupBy { it.movieRating.id }
        .map { e ->
            val movieId = e.key
            val ratingsAdded = e.value.map { it.movieRating.rating }.sum()
            val simSum = e.value.sumOf { it.similarityScore }
            MovieRating(movieId, ratingsAdded/simSum.toFloat())
        }
}


private fun User.toEntity() =
    Entity(
        id = userId,
        features = moviesRated.map { Feature(it.id, it.rating.toDouble()) }.toSet()
    )

private fun Entity.toUser() =
    User(id, features.map { MovieRating(it.id, it.weight.toFloat()) }.toList())

@Throws(java.io.IOException::class)
private fun loadUsersRatedFilms() : List<User> {
    return lines(Paths.get(MOVIE_RATINGS)).asSequence()
        .drop(1)
        .map {
            val values = it.split(",")
            values[0] to MovieRating(
                id = values[1],
                rating = values[2].toFloat()
            )
        }
        .groupBy({ it.first }, { it.second })
        .map { User(it.key, it.value) }

}

private fun loadMovies() =
    lines(Paths.get(MOVIES)).asSequence()
        .drop(1)
        .map {
            val values = it.split(",")
            Movie(values[0], values[1], values[2].split("|"))
        }
        .toList()


data class UserSimilarity(val user: User, val similarityScore: Double)
data class User(val userId: String, val moviesRated: List<MovieRating>)
data class Movie(val id: String, val title: String, val genres: List<String>)

data class MovieRating(val id: String, val rating: Float)
data class SuggestedMovie(val movieRating: MovieRating, val similarityScore: Double)
