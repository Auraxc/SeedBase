package org.example;

/**
 * B+ Tree implementation focusing only on keys, with configurable order.

 * Key characteristics of a B+ tree:
 * 1. All leaves are at the same level.
 * 2. Internal nodes store keys and pointers to child nodes, but do not store actual data.
 * 3. Leaf nodes store keys and are linked for efficient range queries.
 * 4. All nodes except root must be at least half full.
 * 5. Internal nodes can have order - 1 keys.
 * 6. Leaf nodes can have order keys.

 * Time complexity:
 * - Search: O(log n) 
 * - Insertion: O(log n) 

 * Space complexity: O(n)
 */
class BPlusTreeMemory {
    private Node root;
    private final int order;  // Order of the B+ tree, now configurable

    /**
     * Constructor allowing specification of the B+ tree order.
     * @param order The order of the B+ tree. Must be at least 3.
     */
    public BPlusTreeMemory(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.order = order;
        root = new LeafNode(order);
    }


    /**
     * Insert a key into the tree.
     * This method handles the case where the root needs to be split,
     * which is the only way the tree grows in height.

     * Time complexity: O(log n) in the average and worst case, where n is the number of keys.
     * However, when the root splits, it takes O(order) time to create a new root.
     */
    public void insert(int key, String value) {
        Node result = root.insert(key, value);
        if (result != null) {
            // Root was split, create a new root
            InternalNode newRoot = new InternalNode(order);
            
            int newKey = result.keys.getFirst();
            System.out.println("newKey" + newKey);

            System.out.println("newValues" + result.values);
            // Remove the first key from InternalNode since it is already in the parent
            if (result instanceof InternalNode) {
                result.keys.removeFirst();
            }

            newRoot.keys.add(newKey);
            newRoot.children.add(root);
            newRoot.children.add(result);
            root = newRoot;
        }
    }

    /**
     * Search for a key in the tree.

     * Time complexity: O(log n) in the worst case, where n is the number of keys.
     * This is because the height of the tree is logarithmic in the number of keys.
     */
    public String search(int key) {
        return root.search(key);
    }
    public Boolean update(int key, String value) {
        return root.update(key, value);
    }
    /**
     * Print the tree structure for debugging and visualization purposes.
     * This method is not part of the standard B+ tree operations but is useful for understanding the tree structure.

     * Time complexity: O(n), where n is the number of keys, as it visits every node once.
     */
    public void printTree() {
        printNode(root, 0);
    }

    private void printNode(Node node, int level) {
        StringBuilder indent = new StringBuilder();
        indent.append("  ".repeat(Math.max(0, level)));

        System.out.println(indent + "Level " + level + ": " + node.keys);
        System.out.println(indent + "Level " + level + ": " + node.values);

        if (node instanceof InternalNode internalNode) {
            for (Node child : internalNode.children) {
                printNode(child, level + 1);
            }
        }
    }
}