package tug.tobkul.ontologybrowser.ontology.graph;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityGraphUtil {
    public static DefaultDirectedGraph<Entity, DefaultEdge> buildGraph(oSystem system) {
        DefaultDirectedGraph<Entity, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Entity e : system.getEntities()) {
            graph.addVertex(e);
        }
        for (Entity e : system.getEntities()) {
            for (Entity sub : e.getSubEntities()) {
                graph.addEdge(e, sub);
            }
        }
        for (Relation r : system.getRelations()) {
            graph.addEdge(r.getEntityA(), r.getEntityB());
        }
        return graph;
    }

    public static Set<Entity> detectCycle(DefaultDirectedGraph<Entity, DefaultEdge> graph) {
        CycleDetector<Entity, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) {
            return cycleDetector.findCycles();
        }
        return null;
    }

    public static Set<Entity> detectOrphans(DefaultDirectedGraph<Entity, DefaultEdge> graph) {
        Set<Entity> o = new HashSet<>();
        for (Entity e : graph.vertexSet()) {
            if (graph.inDegreeOf(e) == 0 && graph.outDegreeOf(e) == 0) {
                o.add(e);
            }
        }
        if (o.isEmpty()) {
            return null;
        }
        return o;
    }

    public static Optional<Entity> findRootEntity(DefaultDirectedGraph<Entity, DefaultEdge> graph) {
        for (Entity e : graph.vertexSet()) {
            if (graph.inDegreeOf(e) == 0 && graph.outDegreeOf(e) > 0) {
                boolean canReachAll = true;
                for (Entity o : graph.vertexSet()) {
                    if (e.equals(o)) continue;
                    if (graph.inDegreeOf(o) > 0 || graph.outDegreeOf(o) > 0) {
                        if (DijkstraShortestPath.findPathBetween(graph, e, o).getEdgeList().isEmpty()) {
                            canReachAll = false;
                            break;
                        }
                    }
                }
                if (canReachAll) return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}
