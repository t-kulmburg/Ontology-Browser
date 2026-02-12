package tug.tobkul.ontologybrowser.ontology.graph;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.*;

public class GraphMLBuilder {
    private final oSystem system;
    private final DefaultDirectedGraph<Entity, DefaultEdge> graph;
    private final List<List<Entity>> entityMap;

    private final Map<String, Entity> nodeMap;
    private final Map<String, Relation> edgeMap;

    private final int xOffset = 200;
    private final int yOffset = 150;
    private final int heightOffset = 20;

    public GraphMLBuilder(oSystem system) throws Exception {
        this.system = system;
        this.graph = EntityGraphUtil.buildGraph(system);
        Optional<Entity> root = EntityGraphUtil.findRootEntity(graph);
        if (root.isEmpty()) {
            throw new Exception("Error: Could not determine root entity");
        }
        this.entityMap = EntityGraphUtil.traverseGraph(graph, root.get());
        this.nodeMap = buildNodeMap();
        this.edgeMap = buildEdgeMap();
    }

    public String build() throws ParserConfigurationException, URISyntaxException, IOException, SAXException,
            TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document nodeDocument = builder.parse(Objects.requireNonNull(GraphMLBuilder.class.getResource("node.xml"))
                .toURI().toString());
        Document edgeDocument = builder.parse(Objects.requireNonNull(GraphMLBuilder.class.getResource("edge.xml"))
                .toURI().toString());
        Document graphDocument = builder.parse(Objects.requireNonNull(GraphMLBuilder.class.getResource("graph.xml"))
                .toURI().toString());
        Document outputDocument = builder.newDocument();

        Node nodeNode = outputDocument.importNode(nodeDocument.getDocumentElement().getChildNodes().item(1), true);
        Node edgeNode = outputDocument.importNode(edgeDocument.getDocumentElement().getChildNodes().item(1), true);
        Node graphNode = outputDocument.importNode(graphDocument.getDocumentElement(), true);

        outputDocument.appendChild(graphNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        int y = 0;
        for (List<Entity> level : entityMap) {
            int x = 0;
            for (Entity e : level) {
                Node newNode = getEntityNode(nodeNode.cloneNode(true), e, x * xOffset, y * yOffset);
                graphNode.appendChild(newNode);
                x++;
            }
            y++;
        }

        for (Relation r : edgeMap.values()) {
            Node newNode = getRelationNode(edgeNode.cloneNode(true), r);
            graphNode.appendChild(newNode);
        }

        for (Entity e : nodeMap.values()) {
            if (e.getSuperEntity() != null) {
                Node newNode = getInheritanceNode(edgeNode.cloneNode(true), e);
                graphNode.appendChild(newNode);
            }
        }

        DOMSource source = new DOMSource(outputDocument);

        System.out.print(source);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);

        return stringWriter.toString();
    }

    private Node getEntityNode(Node node, Entity entity, int x, int y) {
        node.getAttributes().getNamedItem("id").setNodeValue(getEntityNodeId(entity));

        Node umlData = node.getChildNodes().item(3) // <data>
                .getChildNodes().item(1); // <y:UMLClassNode>

        Node geometry = umlData.getChildNodes().item(1);

        int longestStringLength = 0;

        int height = 30 + (heightOffset * entity.getAttributes().size());
        geometry.getAttributes().getNamedItem("height").setNodeValue(String.valueOf(height));
        geometry.getAttributes().getNamedItem("x").setNodeValue(String.valueOf(x));
        geometry.getAttributes().getNamedItem("y").setNodeValue(String.valueOf(y));

        //<y:NodeLevel>
        umlData.getChildNodes().item(7).setTextContent(entity.getName());
        int width = 100; // default
        int overflow = entity.getName().length() - 10;
        while (overflow >= 0) {
            width += 6;
            overflow -= 1;
        }
        geometry.getAttributes().getNamedItem("width").setNodeValue(String.valueOf(width));

        //<y:UML>
        Node UML = umlData.getChildNodes().item(9);
        List<String> attributeStrings = new ArrayList<>();
        for (Attribute attribute : entity.getAttributes()) {
            String attributeString = "";
            attributeString = attributeString + attribute.getName() + ": {";
            attributeString = attributeString + String.join(",",
                    attribute.getValue().getTikzUmlValueString().replace("\\ldots", "...")
            );
            attributeString = attributeString + "}";
            attributeStrings.add(attributeString);
            longestStringLength = Math.max(longestStringLength, attributeString.length());
        }
        // <AttributeLabel>
        UML.getChildNodes().item(1).setTextContent(String.join("\n", attributeStrings));
        return node;
    }

    private Node getRelationNode(Node node, Relation relation) {
        node.getAttributes().getNamedItem("id").setNodeValue(getRelationEdgeId(relation));

        node.getAttributes().getNamedItem("source").setNodeValue(getEntityNodeId(relation.getEntityA()));
        node.getAttributes().getNamedItem("target").setNodeValue(getEntityNodeId(relation.getEntityB()));

        String content = relation.getName() + "\n" + relation.getCardinalityMin() + ".." + relation.getCardinalityMax();
        node.getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(7)
                .setTextContent(content);

        return node;
    }

    private Node getInheritanceNode(Node node, Entity subEntity) {
        String id = "r" + edgeMap.size();
        edgeMap.put(id, null);
        node.getAttributes().getNamedItem("id").setNodeValue(id);

        node.getAttributes().getNamedItem("source").setNodeValue(getEntityNodeId(subEntity.getSuperEntity()));
        node.getAttributes().getNamedItem("target").setNodeValue(getEntityNodeId(subEntity));

        node.getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(5)
                .getAttributes().getNamedItem("source").setNodeValue("white_delta");
        node.getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(5)
                .getAttributes().getNamedItem("target").setNodeValue("none");


        node.getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(7)
                .setTextContent(null);

        return node;
    }

    private Map<String, Entity> buildNodeMap() {
        Map<String, Entity> map = new HashMap<>();
        int i = 0;
        for (Entity e : system.getEntities()) {
            map.put("e" + i, e);
            i++;
        }
        return map;
    }

    private String getEntityNodeId(Entity e) {
        for (String key : nodeMap.keySet()) {
            if (nodeMap.get(key).equals(e)) {
                return key;
            }
        }
        return null;
    }

    private Map<String, Relation> buildEdgeMap() {
        Map<String, Relation> map = new HashMap<>();
        int i = 0;
        for (Relation r : system.getRelations()) {
            map.put("e" + i, r);
            i++;
        }
        return map;
    }

    private String getRelationEdgeId(Relation r) {
        for (String key : edgeMap.keySet()) {
            if (edgeMap.get(key).equals(r)) {
                return key;
            }
        }
        return null;
    }
}

