package dumb.code;

import java.io.IOException;

public interface IFileManager {
    String readFile(String filePath) throws IOException;
    void writeFile(String filePath, String content) throws IOException;
    boolean fileExists(String filePath);
    String getRootDir();
}
