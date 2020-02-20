package graphs

import spock.lang.Specification

/**
 * TODO Document me
 *
 * @author antonovia
 * @since 2/14/2020
 */
class GraphBuilderTest extends Specification {

    def "Instance creation"() {
        given:
        def undirected = GraphBuilder.undirected().build()
        def directed   = GraphBuilder.directed().build()

        expect: 'More sanity tests'
        ! undirected.isDirected()
        directed.isDirected()
    }

}
