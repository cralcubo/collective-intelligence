package com.croman.clustering

import com.croman.utils.Entity
import com.croman.utils.Value
import com.croman.collaborative.filtering.SimilarityCalculator
import com.croman.collaborative.filtering.TanimotoCoefficient
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HierarchicalClusterTest {
    private val similarity = mockk<SimilarityCalculator>()


    @Test
    fun noEntities(){
        assertThrows<IllegalArgumentException> { clusterCreator(emptyList(), similarity) }
    }

    @Test
    fun oneEntity(){
        assertThrows<IllegalArgumentException> { clusterCreator(listOf(Entity("1", emptySet())), similarity) }
    }

    @Test
    fun superCluster() {
        val e1 = Entity(
            "1",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("c", 1.0),
            )
        )
        val e2 = Entity(
            "2",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("z", 1.0),
            )
        )

        val e3 = Entity(
            "3",
            setOf(
                Value("a", 1.0),
                Value("b", 1.0),
                Value("c", 1.0),
            )
        )
        val cluster = clusterCreator(listOf(e1, e2, e3), TanimotoCoefficient())
        (cluster is BiCluster) shouldBe true
        val biCluster = cluster as BiCluster

        (biCluster.left is EntityCluster) shouldBe true
        val entityCluster = biCluster.left as EntityCluster
        entityCluster.left shouldBe e1
        entityCluster.right shouldBe e3
        entityCluster.similarity shouldBe 1.0

        biCluster.right shouldBe e2
        biCluster.similarity shouldBe 0.5
    }



}