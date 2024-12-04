package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal nodes guide the search process to the appropriate leaf node.
 * They contain only keys and pointers to child nodes, not actual data.
 * This design allows for more keys in internal nodes, reducing tree height.
 */
public class InternalNode extends Node {
    List<Node> children;

    InternalNode(int order) {
        super(order);
        children = new ArrayList<>();
    }

    @Override
    Node insert(int key, String value) {
        // Binary search could be used here for better performance in larger nodes
        int index = 0;
        while (index < keys.size() && key >= keys.get(index)) {
            index++;
        }
        Node child = children.get(index);
        Node result = child.insert(key, value);

        if (result != null) {
            // Child node was split, we need to insert the new key and child pointer
            int newKey = result.keys.getFirst();
            // Remove the first key from InternalNode since it is already in the parent
            if (result instanceof InternalNode) {
                result.keys.removeFirst();
            }

            // insert first then check if it needs to split 
            insertIntoNode(newKey, result);
            // internal node can has order - 1 keys
            if (keys.size() <= order - 1) {
                return null; // No further split needed
            } else {
                return splitInternalNode();
            }
        }
        return null;
    }

    private void insertIntoNode(int key, Node child) {
        int index = 0;
        while (index < keys.size() && key >= keys.get(index)) {
            index++;
        }
        keys.add(index, key);
        children.add(index + 1, child);
    }

    /**
     * Split an internal node when it's full.
     * This operation maintains the B+ tree property and might propagate up to the root.
     * The split creates a new sibling node (child) rather than a new parent.
     * The middle key is pushed up to the parent, acting as a separator.
     */
    private Node splitInternalNode() {
        InternalNode newNode = new InternalNode(order);
        int keyMiddleIndex = keys.size() / 2;

        // Notes that update order is important since left and right bounderay is dependent on previous update
        // Move right half of the keys to the new node
        // Update the new node first because subList's toIndex depends on the current node
        newNode.keys = new ArrayList<>(keys.subList(keyMiddleIndex, keys.size()));
        // Update the current node to keep left half
        keys = new ArrayList<>(keys.subList(0, keyMiddleIndex));
        // Move right half of the children to the new node
        // Update the new node first because subList's toIndex depends on the current node
        // keys.size() + 1 will cause size of children is same as keys
        // but it is ok because first key of newNode will be pushed up to the parent
        // which means it will be removed from newNode in parent
        newNode.children = new ArrayList<>(children.subList(keys.size() + 1, children.size()));
        // Update the current node to keep left half
        // keys.size() + 1 because children is 1 more than keys
        children = new ArrayList<>(children.subList(0, keys.size() + 1));

        return newNode;
    }

    @Override
    String search(int key) {
        int index = 0;
        while (index < keys.size() && key >= keys.get(index)) {
            index++;
        }
        return children.get(index).search(key);
    }

    @Override
    Boolean update(int key, String value) {
        int index = 0;
        while (index < keys.size() && key >= keys.get(index)) {
            index++;
        }
        return children.get(index).update(key, value);
    }
    @Override
    public String toString() {
        List<String> childrenKeys = new ArrayList<>();
        for (Node child : children) {
            childrenKeys.add(child.keys.toString());
        }
        String childrenKeysString = String.join(",",  childrenKeys);
        return "InternalNode{" + "keys=" + keys + ", children=" + childrenKeysString + '}';
    }
}