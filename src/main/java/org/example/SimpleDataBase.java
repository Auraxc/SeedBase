package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

record User(int id, String name) {
    // 使用 record 可以自动生成构造器、getter 方法、equals()、hashCode() 和 toString() 方法。
    // 表明这个类只存数据
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }

}

public class SimpleDataBase {
    private static String filename = "db_simple.db";

    public SimpleDataBase(String filename, boolean clear) {
        SimpleDataBase.filename = filename;
        if (clear) {
            clear();
        }
    }

    public void insert(int id, String name) {
        try (FileOutputStream fos = new FileOutputStream(filename, true)) {
            // FileOutputStream 是用于写入字节流到文件的类
            // 追加模式打开，新数据会写入到文件末尾
            // Use 4 bytes to represent id
            // 使用 4 bytes 保存 id
            // Byte length can be chosen arbitrarily
            // 实际的字节长度是任意的
            byte[] idBytes = intToBytes(id);

            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            // Use 12 bytes to represent name
            // 使用 12 bytes 保存 name
            // Byte length can be chosen arbitrarily
            // Pad the data length
            // name 定长，需要填充
            byte[] nameBytePadded = Arrays.copyOf(nameBytes, 12);

            System.out.println("insert " + Arrays.toString(idBytes) + " " + Arrays.toString(nameBytePadded));

            fos.write(idBytes);
            fos.write(nameBytePadded);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public User select(int idQuery) {
        // o(n) time complexity
        // incorrect read: all at once
        // content = file.readall()
        // users = decode(content)
        // correct read: one user at a time
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] idBytes = new byte[4];
            byte[] nameBytePadded = new byte[12];

            // Process and read as much data as needed, not loading everything into memory.
            // One of the main requirements for file storage is that the data is larger than
            // available memory.
            while (fis.read(idBytes) != -1) {
                fis.read(nameBytePadded);
                int id = bytesToInt(idBytes);
                if (id == idQuery) {
                    // Remove trailing zeros
                    byte[] nameBytes = stripTrailingZeros(nameBytePadded);
                    String name = new String(nameBytes, StandardCharsets.UTF_8);
                    return new User(id, name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return null;
    }

    public User selectBinarySearch(int idQuery) {
        // o(log n) time complexity
        // binary search for id
        try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
            long low = 0;
            // Each user record is 16 bytes (4 bytes for id + 12 bytes for name)
            long high = raf.length() / 16 - 1;

            while (low <= high) {
                long mid = (low + high) / 2;
                // Since the data is fixed size, 16 bytes per record.
                // We can calculate the file index using mid * 16
                raf.seek(mid * 16);

                byte[] idBytes = new byte[4];
                raf.read(idBytes);
                int id = bytesToInt(idBytes);

                if (id < idQuery) {
                    low = mid + 1;
                } else if (id > idQuery) {
                    high = mid - 1;
                } else {
                    byte[] nameBytePadded = new byte[12];
                    raf.read(nameBytePadded);
                    byte[] nameBytes = stripTrailingZeros(nameBytePadded);
                    String name = new String(nameBytes, StandardCharsets.UTF_8);
                    return new User(id, name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return null;
    }

    public void clear() {
        // create a new db file
        File file = new File(filename);
        // Delete if exists
        if (file.exists()) {
            file.delete();
        }
        // Create an empty file
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) |
                (bytes[1] << 16) |
                (bytes[2] << 8) |
                (bytes[3]);
    }

    private byte[] stripTrailingZeros(byte[] array) {
        int i = array.length - 1;
        while (i >= 0 && array[i] == 0) {
            i--;
        }
        return Arrays.copyOf(array, i + 1);
    }

    public static void testDb() {
        SimpleDataBase db = new SimpleDataBase("simple_database.db", true);
        db.clear();

        for (int i = 0; i < 20; i++) {
            // Choose an arbitrary length
            String name = String.valueOf(i).repeat(5);
            db.insert(i, name);
            System.out.println("insert " + i + " " + name);
        }

        for (int i = 0; i < 20; i++) {
            User user = db.select(i);
            System.out.println("select " + user);
        }
    }

    public static void main(String[] args) {
        // Two main requirements for file storage:
        // 1. Permanent storage
        // 2. Not enough memory
        // This file is a single-file database implementation with add and query
        // functionality
        testDb();
    }
}