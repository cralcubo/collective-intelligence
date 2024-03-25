package com.croman.utils

import com.croman.utils.MovieLensFactory.Companion.createMovies
import com.croman.utils.MovieLensFactory.Companion.createTags
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class MovieLensFactoryTest {

    @Test
    fun createTags() {
        val url = MovieLensFactoryTest::class.java.getResource("tags.csv")
        assertNotNull(url)

        val tags = createTags(Paths.get(url.toURI()))

        tags shouldHaveSize 5
        tags[0].tags shouldBe setOf("funny", "Highly quotable", "will ferrell")
        tags[4].tags shouldBe setOf("Al Pacino", "gangster", "mafia")
    }

    @Test
    fun createMovies() {
        val url = MovieLensFactoryTest::class.java.getResource("movies.csv")
        assertNotNull(url)

        val movies = createMovies(Paths.get(url.toURI()))

        movies.size shouldBe 20
        movies[0].title shouldBe "Toy Story (1995)"
        movies[0].movieId shouldBe 1
        movies[0].genres shouldBe setOf("Adventure","Animation","Children","Comedy","Fantasy")

        movies[19].title shouldBe "Money Train (1995)"
        movies[19].movieId shouldBe 20
        movies[19].genres shouldBe setOf("Action","Comedy","Crime","Drama","Thriller")
    }

    @Test
    fun createRatings() {
        val url = MovieLensFactoryTest::class.java.getResource("ratings.csv")
        assertNotNull(url)

        val ratings = MovieLensFactory.createRatings(Paths.get(url.toURI()))

        ratings.size shouldBe 3
        ratings[0].userId shouldBe 1
        ratings[0].movieRating.size shouldBe 232
        ratings[0].movieRating[5060] shouldBe 5.0
        ratings[0].movieRating[3639] shouldBe 4.0
        ratings[0].movieRating[1676] shouldBe 3.0
        ratings[0].movieRating[2338] shouldBe 2.0
    }
}