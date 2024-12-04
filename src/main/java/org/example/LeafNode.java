package org.example;

import java.util.ArrayList;

/**
 * Leaf nodes store the actual keys and form a linked list.
 * This allows for efficient range queries and sequential access.
 */
public class LeafNode extends Node {
    LeafNode(int order) {
        super(order);
    }


    @Override
    Node insert(int key, String value) {
        int index = 0;
        while (index < keys.size() && key > keys.get(index)) {
            index++;
        }
        System.out.println("insert key: " + key+ " insert value: "+ value + " index: " + index + " " + this);

        // insert first then check if it needs to split 
        insertIntoNode(key, value);
        // max number of keys in leaf node is order
        if (keys.size() <= order) {
            return null; // Leaf node not full, no split needed
        } else {
            return splitLeafNode();
        }
    }

    private void insertIntoNode(int key, String value) {
        int index = 0;
        while (index < keys.size() && key >= keys.get(index)) {
            index++;
        }
        keys.add(index, key);
        values.add(index, value);
    }


    /**
     * Split a leaf node when it's full.
     * This operation maintains the B+ tree property and might propagate up to the root.

     * The split creates a new sibling node (child) rather than a new parent.
     * The first key of the new node is pushed up to the parent as a separator.
     */
    private Node splitLeafNode() {
        LeafNode newNode = new LeafNode(order);
        int keyMiddleIndex = keys.size() / 2;
        
        // Move the right half of the keys to the new node.
        // Update the new node first because subList's toIndex depends on the current node's keys.
        newNode.keys = new ArrayList<>(keys.subList(keyMiddleIndex, keys.size()));
        newNode.values = new ArrayList<>(values.subList(keyMiddleIndex, values.size()));
        // Update the current node to keep left half
        keys = new ArrayList<>(keys.subList(0, keyMiddleIndex));
        values = new ArrayList<>(values.subList(0, keyMiddleIndex));


        return newNode;
    }

    @Override
    String search(int key) {
        // Linear search is used here. For very large order, binary search could be more efficient
        for (int i = 0; i < keys.size(); i++) {
            if (key == keys.get(i)) {
                return values.get(i);
            }
        }
        return null;
    }

    @Override
    Boolean update(int key, String value) {
        for (int i = 0; i < keys.size(); i++) {
            if (key == keys.get(i)) {
                values.set(i, value);
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "LeafNode{" + keys.toString() + "}";
    }
}
