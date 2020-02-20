package graphs

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @see Edge
 *
 * @author antonovia
 * @since 2/14/2020
 */
class EdgeTest extends Specification {

    def "Sanity test: (un)ordered"() {
        given: '(u, v)'
        def edge = Edge.unordered(1, 2)

        expect:
        !edge.ordered

        and:
        edge == edge

        and:
        edge == Edge.unordered(1, 2)

        and:
        staticEquals(Edge.unordered(2, 3), Edge.unordered(2, 3))
        staticEquals(Edge.unordered(3, 2), Edge.unordered(2, 3))
    }

    def "Sanity test: source - target"() {
        given: '(u, v)'
        def edge = Edge.ordered(1, 2)

        expect:
        edge.ordered

        and:
        edge.source == 1
        edge.target == 2

        and: 'source = u, target = v'
        edge.source == edge.nodeU
        edge.target == edge.nodeV

        and:
        edge == edge
        edge == Edge.ordered(1, 2)
    }

    @Unroll
    def "Unordered edge - (#u, #v) == (#v, #u)"() {
        expect:
        Edge.unordered(u, v) == Edge.unordered(v, u)

        and:
        Edge.unordered(u, v) == Edge.unordered(u, v)

        where:
        u | v
        1 | 2
        2 | 1
        3 | 3
    }

    @Unroll
    def "Ordered edge - (#u, #v) == only (#u, #v)"() {
        expect:
        Edge.ordered(u, v) == Edge.ordered(u, v)

        and:
        Edge.ordered(u, v) != Edge.ordered(v, u)

        where:
        u | v
        1 | 2
        2 | 1
        // 3 | 3 would be a cyclic connection
    }

    def "Ordered <> unordered"() {
        expect: // Even if the nodes are same
        Edge.ordered(1, 2) != Edge.unordered(1, 2)
        Edge.ordered(1, 2) != Edge.unordered(2, 1)
    }

    @CompileStatic
    private static <N> boolean staticEquals(GraphEdge<N> e1, GraphEdge<N> e2) {
        return Objects.equals(e1, e2)
    }

}
