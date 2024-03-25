package com.croman.clustering

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BagBallProblemKtTest {
    
    @Nested
    inner class BestPair {
        @Test
        fun twoBallsOnly() {
            val balls = listOf(1,2)
            val (pair, rest) = finderGeneric(balls)
            pair shouldBe BallPair(1,2)
            rest shouldHaveSize 0
        }

        @Test
        fun twoEqualBallsOnly() {
            val balls = listOf(1,1)
            val (pair, rest) = finderGeneric(balls)
            pair shouldBe BallPair(1,1)
            rest shouldHaveSize 0
        }

        @Test
        fun threeBalls() {
            val (pair, rest) = finderGeneric(listOf(1,2,3))
            pair shouldBe BallPair(1,2)
            rest shouldBe listOf(3)
        }

        @Test
        fun manyBalls1() {
            val (pair, rest) = finderGeneric(listOf(8, 1, 12,15,6,2))
            pair shouldBe BallPair(1,2)
            rest shouldContainAll listOf(8,12,15,6)
        }

        @Test
        fun manyBalls2() {
            val (pair, rest) = finderGeneric(listOf(1,90, 80, 70,6,4))
            pair shouldBe BallPair(6,4)
            rest shouldContainAll listOf(1, 90, 80, 70)
        }

        @Test
        fun manyBalls3() {
            val (pair, rest) = finderGeneric(listOf(99, 8, 1, 12,15,6,3, 100))
            pair shouldBe BallPair(99,100)
            rest shouldContainAll listOf(8,12,15,6, 1,3)
        }

        @Test
        fun manyBalls4() {
            val (pair, rest) = finderGeneric(listOf(5,2,8,1))
            pair shouldBe BallPair(1,2)
            rest shouldContainAll listOf(5,8)
        }

        @Test
        fun repeatedBalls() {
            val (pair, rest) = finderGeneric(listOf(1, 90, 6, 70, 80, 90, 90))
            pair shouldBe BallPair(90,90)
            rest shouldContainAll listOf(1, 6, 70, 80, 90)
        }
    }

    @Nested
    inner class BestEntity {

        @Test
        fun noBalls() {
            assertThrows<IllegalArgumentException> {  bestBall(5, emptyList()) }
        }

        @Test
        fun oneBall() {
            val (i, ints) = bestBall(5, listOf(1))
            i shouldBe 1
            ints shouldContainAll emptyList()
        }

        @Test
        fun twoEntities() {
            val (i, ints) = bestBall(10, listOf(8,9))
            i shouldBe 9
            ints shouldContainAll listOf(8)
        }

        @Test
        fun threeEntities() {
            val (i, ints) = bestBall(7, listOf(1,90,9))
            i shouldBe 9
            ints shouldContainAll listOf(1,90)
        }

        @Test
        fun multipleEntities() {
            val (i, ints) = bestBall(8, listOf(1,4,16,78,6,10))
            i shouldBe 6
            ints shouldContainAll listOf(1,4,16,78,10)
        }

        @Test
        fun repeated() {
            val (i, ints) = bestBall(8, listOf(1,4,16,78,8,8))
            i shouldBe 8
            ints shouldContainAll listOf(1,4,16,78,8)
        }



    }

    

}