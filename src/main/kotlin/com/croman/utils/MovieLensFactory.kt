package com.croman.utils

import java.nio.file.Files
import java.nio.file.Path

/**
 * Class that will convert a movie-lens file to a
 * movieLens object with all the properties presented in the
 * file.
 * There are 4 different files with this information:
 * - tags
 * - movies
 * - ratings
 * This information come in CSV format
 * Depending on the type of file fed to this class
 * a:
 * - MovieLensTags
 * - MovieLensMovies
 * - MovieLensRatings
 * object will be created.
 */
class MovieLensFactory {

    companion object {

        data class Rating(val userId: String, val movieId: String, val rating: String)
        fun createRatings(source: Path): List<MovieLensRatings> {
            val lines = Files.lines(source).toList()
            //userId,movieId,rating,timestamp
            //1,1,4.0,964982703
            return lines.drop(1).map {
                it.split(",").let {ls ->
                    Rating(ls[0], ls[1], ls[2])
                }
            }.groupBy ( {it.userId}, {it.movieId to it.rating} )
                .map { entries ->
                    MovieLensRatings(
                        entries.key.toInt(),
                        entries.value.associateBy({it.first.toInt()}, {it.second.toDouble()})
                    )
                }
        }

        fun createMovies(source: Path): List<MovieLensMovie> {
            //movieId,title,genres
            //1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy
            val lines = Files.lines(source).toList()
            return lines.drop(1).map {
                it.split(",").let { ls ->
                    MovieLensMovie(ls[0].toInt(), ls[1], ls[2].split("|").toSet())
                }
            }
        }

        fun createTags(source: Path): List<MovieLensTags> {
            //userId,movieId,tag,timestamp
            // 2,60756,funny,1445714994
            // 2,60756,Highly quotable,1445714996
            val lines = Files.lines(source).toList()

            return lines.drop(1).map {
                it.split(",").let { sl ->
                    sl[1] to sl[2]
                }
            }.groupBy ({ it.first }, {it.second})
                .map { MovieLensTags(it.key.toInt(), it.value.toSet()) }
        }
    }
}

sealed interface MovieLens
// userId,movieId,tag,timestamp
data class MovieLensTags(val movieId: Int, val tags: Set<String>) : MovieLens
// movieId,title,genres
data class MovieLensMovie(val movieId: Int, val title: String, val genres:Set<String>): MovieLens
// userId,movieId,rating,timestamp
data class MovieLensRatings(val userId: Int, val movieRating: Map<Int, Double>): MovieLens


