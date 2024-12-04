package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeafNodeTest {
    private LeafNode leafNode;

    @BeforeEach
    void setUp() {
        leafNode = new LeafNode(4); // Order 4 for testing
    }

    @Test
    void testInsert() {
        // Test case 1: Insert into empty leaf
        Node result = leafNode.insert(5, "value5");
        assertNull(result);
        assertEquals(1, leafNode.keys.size());
        assertEquals(5, leafNode.keys.getFirst());

        // Test case 2: Insert maintaining order
        result = leafNode.insert(3, "value3");
        assertNull(result);
        assertEquals(2, leafNode.keys.size());
        assertEquals(3, leafNode.keys.get(0));
        assertEquals(5, leafNode.keys.get(1));

    }

    @Test
    void testSplitLeafNode() {
        leafNode.keys.addAll(List.of(10, 20, 30, 40));
        leafNode.values.addAll(List.of("value10", "value20", "value30", "value40"));
        Node result = leafNode.insert(50, "value50");
        assertNotNull(result);
        assertInstanceOf(LeafNode.class, result);
        assertEquals(2, leafNode.keys.size());
        assertEquals(2, leafNode.values.size());
        assertEquals(3, result.keys.size());
        assertEquals(3, result.values.size());
        assertEquals(20, leafNode.keys.get(1));
        assertEquals("value20", leafNode.values.get(1));
        assertEquals(30, result.keys.getFirst());
        assertEquals("value30", result.values.getFirst());
    }
}