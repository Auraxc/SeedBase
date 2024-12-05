package org.example;

import org.junit.jupiter.api.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SimpleSimpleDataBaseTest {
    private static final String TEST_DB_FILENAME = "test_db_simple.db";
    private SimpleDataBase db;

    @BeforeEach
    void setUp() {
        // Modify the DataBase class to accept a filename in its constructor
        db = new SimpleDataBase(TEST_DB_FILENAME, true);
    }

    @AfterEach
    void tearDown() {
        // Clean up the test database file after each test
        new File(TEST_DB_FILENAME).delete();
    }

    @Test
    void testIntToBytes() {
        byte[] bytes = db.intToBytes(0x12345678);
        assertArrayEquals(new byte[] {0x12, 0x34, 0x56, 0x78}, bytes);

        bytes = db.intToBytes(127);
        assertArrayEquals(new byte[] {0, 0, 0, 127}, bytes);

        bytes = db.intToBytes(256);
        assertArrayEquals(new byte[] {0, 0, 1, 0}, bytes);
    }

    @Test
    void testBytesToInt() {
        int value = db.bytesToInt(new byte[] {0x12, 0x34, 0x56, 0x78});
        assertEquals(0x12345678, value);

        value = db.bytesToInt(new byte[] {0, 0, 0, 127});
        assertEquals(127, value);

        value = db.bytesToInt(new byte[] {0, 0, 1, 0});
        assertEquals(256, value);
    }

    @Test
    void testInsertAndSelect() {
        db.insert(1, "Alice");
        db.insert(2, "Bob");

        User user1 = db.select(1);
        User user2 = db.select(2);

        assertNotNull(user1);
        assertEquals(1, user1.id());
        assertEquals("Alice", user1.name());

        assertNotNull(user2);
        assertEquals(2, user2.id());
        assertEquals("Bob", user2.name());
    }

    @Test
    void testSelectBinarySearch() {
        for (int i = 0; i < 20; i++) {
            // Choose an arbitrary length
            String name = String.valueOf(i).repeat(5);
            db.insert(i, name);
        }

        for (int i = 0; i < 20; i++) {
            User user = db.selectBinarySearch(i);
            assertEquals(i, user.id());
            String name = String.valueOf(i).repeat(5);
            assertEquals(name, user.name());
        }
    }

    @Test
    void testSelectNonExistentUser() {
        User user = db.select(999);
        assertNull(user);
    }

    @Test
    void testClear() {
        db.insert(1, "Alice");
        db.clear();

        User user = db.select(1);
        assertNull(user);
    }

    @Test
    void testInsertWithLongName() {
        String longName = "ThisIsAVeryLongNameThatExceedsTwelveCharacters";
        db.insert(1, longName);

        User user = db.select(1);
        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals(longName.substring(0, 12), user.name());
    }

    @Test
    void testMultipleInserts() {
        for (int i = 0; i < 100; i++) {
            db.insert(i, "User" + i);
        }

        for (int i = 0; i < 100; i++) {
            User user = db.select(i);
            assertNotNull(user);
            assertEquals(i, user.id());
            assertEquals("User" + i, user.name());
        }
    }

    @Test
    void testReopen() {
        db.insert(1, "Alice");
        db.insert(2, "Bob");

        // Create a new instance of DataBase with the same file
        SimpleDataBase newDb = new SimpleDataBase(TEST_DB_FILENAME, false);

        User user1 = newDb.select(1);
        User user2 = newDb.select(2);

        assertNotNull(user1);
        assertEquals(1, user1.id());
        assertEquals("Alice", user1.name());

        assertNotNull(user2);
        assertEquals(2, user2.id());
        assertEquals("Bob", user2.name());
    }
}