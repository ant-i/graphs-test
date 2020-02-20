package graphs

import spock.lang.Specification
import spock.lang.Unroll
import test.utils.Any

/**
 * @see SimpleMutableGraph
 * @author antonovia
 * @since 2/16/2020
 */
class SimpleMutableGraphTest extends Specification {

    @SuppressWarnings("GroovyPointlessBoolean")
    def "Add node to undirected graph"() {
        given:
        def g = new SimpleMutableGraph(false)

        expect:
        g.addVertex(1)
        g.addVertex(2)

        and:
        !g.addVertex(1) // Already exists

        and:
        g.getNodes().size() == 2

        and:
        g.nodes.containsAll([1, 2])
    }

    def "Add edges to undirected graph"() {
        given:
        def g = new SimpleMutableGraph(false)

        /*
                3
              / |
            1 - 2
                |
                4
        */
        expect:
        g.addEdge(1, 2)
        g.addEdge(2, 3)
        g.addEdge(2, 4)
        g.addEdge(3, 1)

        and:
        g.edges.size() == 4

        and:
        g.edges.containsAll(
                Edge.unordered(1, 2),
                Edge.unordered(2, 3),
                Edge.unordered(2, 4),
                Edge.unordered(3, 1)
        )

        and: 'edge (1, 2) already exists, that is the same in undirected graph'
        !g.addEdge(2, 1)
    }

    def "Connecting nodes on undirected graph"() {
        given:
        def g = new SimpleMutableGraph(false)

        when:
        g.addVertex(1)
        g.addVertex(2)
        g.addVertex(3)

        and:
        g.addEdge(1, 2)
        g.addEdge(2, 3)
        g.addEdge(3, 1)

        then:
        g.edges.size() == 3

        and:
        g.edges.containsAll(
                Edge.unordered(1, 2),
                Edge.unordered(2, 3),
                Edge.unordered(3, 1)
        )

        and:
        g.nodes.size() == 3

        and:
        g.nodes.containsAll(1, 2, 3)

        when:
        g.addEdge(1, 5)

        then:
        g.nodes.contains(5)

        and:
        g.edges.size() == 4
    }

    @Unroll
    def "#connect #u to multiple of #vs"() {
        given:
        def g = new SimpleMutableGraph<Integer>(false)

        when:
        g.connect(1, null)

        then:
        thrown(IllegalArgumentException)

        when:
        g.connect(1, [] as Integer[])

        then:
        thrown(IllegalArgumentException)

        when:
        def isConnected = g.connect(u, vs.toArray { i -> new Integer[i] })

        then:
        Arrays.equals(conn as boolean[], isConnected)

        and:
        g.edges.containsAll(toSetOfEdges(edges))

        where:
        u | vs        || conn                || edges
        1 | [2, 3, 4] || [true] * 3          || [[1, 2], [1, 3], [1, 4]]
        1 | [2, 2, 3] || [true, false, true] || [[1, 2], [1, 3]]
    }

    def "Connecting ordered nodes in directed graph"() {
        given:
        def g = new SimpleMutableGraph(true)

        expect:
        g.addVertex(1)
        g.addVertex(2)

        and: 'in directed graph these are two different edges'
        g.addEdge(1, 2)
        g.addEdge(2, 1)

        and: 'though adding parallel edge should still fail'
        !g.addEdge(1, 2)

        and:
        g.edges.containsAll(
                Edge.ordered(1, 2),
                Edge.ordered(2, 1)
        )
    }

    @Unroll
    def "Get path (#u -- #v) on undirected graph"() {
        when:
        def path = g.getPath(u, v)

        then:
        path.size() == ps

        and:
        if (expectedPath instanceof Any) {
            def any = (Any<Collection>) expectedPath
            assert any.anyTrue { toSetOfEdges(it).containsAll(path) }
        } else {
            assert toSetOfEdges((Collection) expectedPath).containsAll(path)
        }

        where:
        g        | u | v || ps || expectedPath
        graph0() | 0 | 6 || 2  || [[0, 1], [1, 6]]  // Find a path from 0 to 6
        graph0() | 5 | 6 || 1  || [[5, 6]]
        graph0() | 4 | 2 || 2  || [[4, 0], [0, 2]]
        graph1() | 1 | 2 || 0  || []                // Path does not exists, since vertex "2" not exists
        graph1() | 1 | 1 || 0  || []                // Path to itself only possible if there is a cycle, which is not supported
        graph2() | 1 | 2 || 1  || [[1, 2]]
        graph2() | 2 | 1 || 1  || [[2, 1]]          // In undirected graph this edges are the same
        graph3() | 1 | 3 || 0  || []                // No path between disjoint nodes
        // test.utils.Any path is possible and any of them is viable
        graph4() | 4 | 1 || 2  || Any.of([[4, 2], [2, 1]], [[4, 3], [3, 1]])
    }

    @Unroll
    def "Get path (#u -> #v) on directed graph"() {
        when:
        def path = g.getPath(u, v)

        then:
        if (expectedPath instanceof Any) {
            def any = (Any<Collection>) expectedPath
            assert any.anyTrue { toSetOfEdges(it, true).containsAll(path) }
        } else {
            assert toSetOfEdges((Collection) expectedPath, true).containsAll(path)
        }

        where:
        g        | u | v || expectedPath
        graph5() | 1 | 2 || [[1, 2]]
        graph5() | 2 | 1 || []                // In directed graph there is no backwards path
        graph5() | 2 | 4 || [[2, 4]]
        graph5() | 4 | 2 || [[4, 2]]
        graph5() | 4 | 3 || [[4, 2], [2, 3]]
        graph5() | 4 | 1 || []
        graph6() | 1 | 5 || [[1, 3], [3, 5]]
        graph6() | 1 | 7 || [[1, 7]]
        graph6() | 7 | 1 || [[7, 9], [9, 6], [6, 3], [3, 1]]
        graph6() | 1 | 4 || Any.of([[1, 2], [2, 4]], [[1, 3], [3, 4]])
        graph6() | 9 | 8 || [[9, 6], [6, 3], [3, 5], [5, 7], [7, 8]]
        graph6() | 8 | 1 || []
        // Cyclic edges
        graph6() | 2 | 2 || [[2, 2]]
        graph6() | 1 | 2 || [[1, 2]]
        graph6() | 1 | 4 || [[1, 2], [2, 4]]
        graph6() | 1 | 1 || []
    }

    private static SimpleMutableGraph<Integer> newUndirected() {
        return new SimpleMutableGraph<>(false)
    }

    private static SimpleMutableGraph<Integer> newDirected() {
        return new SimpleMutableGraph<>(true)
    }

    private static <N> Set<GraphEdge<N>> toSetOfEdges(Collection<Collection<N>> set, boolean ordered = false) {
        set.collect { xs -> ordered ? Edge.ordered(xs[0], xs[1]) : Edge.unordered(xs[0], xs[1]) }
    }

    // --------------- Predefined undirected graphs

    /*
    A regular graph with multiple connections
       4
     /
    0 -- 1 -- 5 -- 7
    |     \  /   /
    |      6 --+
    |     /
    2 -- 3
    */

    private static Graph<Integer> graph0() {
        def g = newUndirected()
        g.addEdge(0, 4)
        g.addEdge(0, 1)
        g.addEdge(0, 2)
        g.addEdge(1, 5)
        g.addEdge(2, 3)
        g.addEdge(3, 6)
        g.addEdge(5, 6)
        g.addEdge(1, 6)
        g.addEdge(5, 7)
        g.addEdge(7, 6)

        return g
    }

    /*
    A simple one node graph
    */

    private static Graph<Integer> graph1() {
        def g = newUndirected()
        g.addVertex(1)

        return g
    }

    /*
    A simple graph with two nodes and one edge connecting them
    1 - 2
    */

    private static Graph<Integer> graph2() {
        def g = newUndirected()
        g.addEdge(1, 2)

        return g
    }

    /*
    Graph with disjoint nodes
    1   2   3
      /
    4
    */

    private static Graph<Integer> graph3() {
        def g = newUndirected()
        g.addVertex(1)
        g.addVertex(3)
        g.addEdge(2, 4)

        return g
    }

    /*
    Looped graph
    1 - 2
    |   |
    3 - 4
    */

    private static Graph<Integer> graph4() {
        def g = newUndirected()
        g.addEdge(1, 2)
        g.addEdge(1, 3)
        g.addEdge(3, 4)
        g.addEdge(2, 4)

        return g
    }

    // --------------- Predefined directed graphs

    /*
    Simple directed graph
    1 -> 2 -> 3
          ↘↖
            4
    */

    private static Graph<Integer> graph5() {
        def g = newDirected()
        g.addEdge(1, 2)
        g.addEdge(2, 3)
        g.addEdge(2, 4)
        g.addEdge(4, 2)

        return g
    }

    /*
    More complex directed graph
      ⟲
      2 -> 4 <- 8
     ↗ ↘↖ ↗       ↖
    1 <> 3 -> 5 -> 7 <-+
    ↓      ↘↖      ↓   ↑
    ↓       6 <--> 9   ↑
    ↓                  ↑
    + -> ------>-----> +
    */

    private static Graph<Integer> graph6() {
        def g = newDirected()
        g.connect(1, [2, 3, 7] as Integer[])
        // With a cyclic edge 2 -> 2
        g.connect(2, [4, 3, 2] as Integer[])
        g.connect(3, [1, 4, 5, 6] as Integer[])
        g.connect(7, [8, 9] as Integer[])

        g.addEdge(5, 7)
        g.addEdge(8, 4)
        g.addEdge(6, 9)
        g.addEdge(9, 6)

        return g
    }

}
