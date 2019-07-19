package org.adoptopenjdk.jheappo.model;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class HeapGraphExtras {
    // Kotlin plays poorly with enums (which have a .name) that also implement an interface that requires name(),
    // so we use plain old Java for these

    enum Labels implements Label {
        Instance, Class
    }
    enum Relationships implements RelationshipType {
        CONTAINS, IS_CLASS, SUPERCLASS;
    }
}
