package hao.alvin.chase;

import java.io.IOException;
import java.io.RandomAccessFile;

class DataAccess {
    private final String fileName;

    public DataAccess(String fileName) {
        this.fileName = fileName;
    }

    public byte readRow(int position) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(position);
            return raf.readByte();
        }
    }

    public void writeRow(int position, byte rowData) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek(position);
            raf.writeByte(rowData);
        }
    }

    public void initializeFile(int rows) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            byte[] emptyRows = new byte[rows]; // Each row is stored as a single byte
            raf.write(emptyRows);
        }
    }
}
