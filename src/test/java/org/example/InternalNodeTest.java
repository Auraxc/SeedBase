package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InternalNodeTest {
    private InternalNode internalNode;

    @BeforeEach
    void setUp() {
        internalNode = new InternalNode(4); // Order 4 for testing
    }

    @Test
    void testInsert() {
        // Setup: Create child nodes
        LeafNode child1 = new LeafNode(4);
        child1.keys.addAll(List.of(1, 2, 3));
        child1.values.addAll(List.of("value1", "value2", "value3"));
        LeafNode child2 = new LeafNode(4);
        child2.keys.addAll(List.of(10, 12, 13));
        child2.values.addAll(List.of("value10", "value12", "value13"));
        internalNode.keys.add(10);
        internalNode.children.add(child1);
        internalNode.children.add(child2);

        // Test case 1: Insert into first child
        Node result = internalNode.insert(5, "");
        assertNull(result);
        assertEquals(4, child1.keys.size());
        assertEquals(5, child1.keys.get(3));

        // Test case 2: Insert into second child
        result = internalNode.insert(15, "");
        assertNull(result);
        assertEquals(4, child2.keys.size());
        assertEquals(15, child2.keys.get(3));

        // Test case 3: Insert causing child split
        internalNode.insert(6, "");
        assertEquals(2, internalNode.keys.size());
        assertEquals(List.of(3, 10), internalNode.keys);
        assertEquals(3, internalNode.children.size());
        assertEquals(List.of(1, 2), internalNode.children.get(0).keys);
        assertEquals(List.of(3, 5, 6), internalNode.children.get(1).keys);
        assertEquals(List.of(10, 12, 13, 15), internalNode.children.get(2).keys);
    }

    @Test
    void testSplitInternalNode() {
        // Setup: Create a full internal node
        internalNode.keys.addAll(List.of(10, 20, 30));

        Node leafNode1 = new LeafNode(4);
        leafNode1.keys.addAll(List.of(0, 1, 2, 3));
        leafNode1.values.addAll(List.of("value0", "value1", "value2", "value3"));
        internalNode.children.add(leafNode1);

        Node leafNode2 = new LeafNode(4);
        leafNode2.keys.addAll(List.of(10, 11, 12, 13));
        leafNode2.values.addAll(List.of("value10", "value11", "value12", "value13"));
        internalNode.children.add(leafNode2);

        Node leafNode3 = new LeafNode(4);
        leafNode3.keys.addAll(List.of(20, 21, 22, 23));
        leafNode3.values.addAll(List.of("value20", "value21", "value22", "value23"));
        internalNode.children.add(leafNode3);

        Node leafNode4 = new LeafNode(4);
        leafNode4.keys.addAll(List.of(30, 31, 32, 33));
        leafNode4.values.addAll(List.of("value30", "value31", "value32","value33"));
        internalNode.children.add(leafNode4);

        Node leafNode5 = new LeafNode(4);
        leafNode5.keys.addAll(List.of(40, 41, 42, 43));
        leafNode5.values.addAll(List.of("value40", "value41", "value42", "value43"));
        internalNode.children.add(leafNode5);
        // Split full internal node
        Node result = internalNode.insert(25, "");
        assertNotNull(result);
        assertInstanceOf(InternalNode.class, result);
        assertEquals(List.of(10, 20), internalNode.keys);
        assertEquals(List.of(22, 30), result.keys);

        // Verify children distribution after split
        assertEquals(3, internalNode.children.size());
        assertEquals(3, ((InternalNode) result).children.size());
        assertEquals(List.of(0, 1, 2, 3), internalNode.children.get(0).keys);
        assertEquals(List.of(10, 11, 12, 13), internalNode.children.get(1).keys);
        assertEquals(List.of(20, 21), internalNode.children.get(2).keys);

        assertEquals(List.of(22, 23, 25), ((InternalNode) result).children.get(0).keys);
        assertEquals(List.of(30, 31, 32, 33), ((InternalNode) result).children.get(1).keys);
        assertEquals(List.of(40, 41, 42, 43), ((InternalNode) result).children.get(2).keys);
    }   
}