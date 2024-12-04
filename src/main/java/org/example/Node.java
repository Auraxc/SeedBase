package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for tree nodes.
 * We use an abstract class to share common properties and methods
 * between internal and leaf nodes, promoting code reuse.
 */
public abstract class Node {
    List<Integer> keys;
    List<String> values;
    int order;

    Node(int order) {
        keys = new ArrayList<>();
        values = new ArrayList<>();
        this.order = order;
    }

    abstract Node insert(int key, String value);
    abstract String search(int key );
    abstract Boolean update(int key, String value);

    @Override
    public String toString() {
        return keys.toString();
    }
}
