package dumb.tools;

import java.io.IOException;

import java.util.List;

public interface IFileManager {
    String readFile(String filePath) throws IOException;
    void writeFile(String filePath, String content) throws IOException;
    boolean fileExists(String filePath);
    String getRootDir();
    List<String> listFiles() throws IOException;
}
