package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeMemoryTest {

    private BPlusTreeMemory tree;

    @BeforeEach
    void setUp() {
        tree = new BPlusTreeMemory(4);  // Using order 4 for tests
    }

    @Test
    void testInsertAndSearch() {
        tree.insert(10, "value10");
        tree.insert(20, "value20");
        tree.insert(5, "value5");

        assert(Objects.equals(tree.search(10), "value10"));
        assert(Objects.equals(tree.search(20), "value20"));
        assert(Objects.equals(tree.search(5), "value5"));
        assertNull(tree.search(15));
    }

    @Test
    void testInsertAndSearchMiddle() {
        for (int i = 0; i < 20; i++) {
            tree.insert(i, "value" + i);
            System.out.println("after insert " + i);
            tree.printTree();
        }

        for (int i = 0; i < 20; i++) {
            assert(Objects.equals(tree.search(i), "value" + i));
        }

        assertNull(tree.search(100));
    }

    @Test
    void testInsertAndSearchLarge() {
        for (int i = 0; i < 1000; i++) {
            tree.insert(i, "value" + i);
        }

        for (int i = 0; i < 1000; i++) {
            assert (Objects.equals(tree.search(i), "value" + i));
        }

        assertNull(tree.search(1000));
    }

    @Test
    void testInsertionOrder() {
        int[] keys = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        for (int key : keys) {
            tree.insert(key, "value" + key);
        }

        for (int key : keys) {
            assert(Objects.equals(tree.search(key), "value" + key));
        }

        int[] keys2 = {5, 6, 7, 8, 9, 2, 10, 3, 4, 48, 16, 78};
        for (int key : keys2) {
            tree.insert(key, "value" + key);
            tree.printTree();
        }
    }


    @Test
    void testEmptyTree() {
        assertNull(tree.search(1));
    }

    @Test
    void testPrintTree() {
        // Insert some keys
        tree.insert(5, "value5");
        tree.insert(15, "value15");
        tree.insert(25, "value25");
        tree.insert(35, "value35");
        tree.insert(45, "value45");

        // Call printTree
        tree.printTree();

    }



    @Test
    void testDifferentOrders() {
        BPlusTreeMemory tree3 = new BPlusTreeMemory(3);
        BPlusTreeMemory tree5 = new BPlusTreeMemory(5);

        for (int i = 1; i <= 20; i++) {
            tree3.insert(i, "value" + i);
            tree5.insert(i, "value" + i);
        }
        assert(Objects.equals(tree3.search(10), "value10"));
        assert(Objects.equals(tree5.search(10), "value10"));

        // You could also print these trees to visually inspect their different structures
        System.out.println("Tree with order 3:");
        tree3.printTree();
        System.out.println("\nTree with order 5:");
        tree5.printTree();
    }


    @Test
    void testUpdate() {
        for (int i = 0; i < 10; i++) {
            tree.insert(i, "value" + i);
        }

        for (int i = 0; i < 5; i++) {
            assertTrue(tree.update(i, "update"+ i));
            assert (Objects.equals(tree.search(i), "update" + i));
        }
        tree.printTree();
        assertFalse(tree.update(1000, "notExist"));
    }
}