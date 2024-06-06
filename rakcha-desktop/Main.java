import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {
    public static void main(String[] args) throws IOException {
        Path startPath = Paths.get("src");
        addNewLineToJavaFiles(startPath);
    }

    private static void addNewLineToJavaFiles(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw");
                    long length = raf.length();
                    if (length > 0) {
                        raf.seek(length - 1);
                        byte lastByte = raf.readByte();
                        if (lastByte != '\n') {
                            raf.write('\n');
                        }
                    }
                    raf.close();
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}