package com.sprinteins.drupalcli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class OpenAPI {

    private String openAPISpecFileName;
    private Path directory;

    public String getOpenAPISpecFileName() { return openAPISpecFileName; }
    public void setOpenAPISpecFileName(String openAPISpecFile) { openAPISpecFileName = openAPISpecFile; }

    public Path getDirectory() { return directory; }
    public void setDirectory(Path directory) { this.directory = directory; }

    public OpenAPI(Path path) throws Exception {
        setDirectory(path);
        setYamlFile();
    }

    private void setYamlFile() throws Exception {
        String fileName = Files.list(directory)
            .map(Path::getFileName)
            .map(Path::toString)
            .filter(name -> name.toLowerCase(Locale.ROOT).endsWith(".yaml") || name.toLowerCase(Locale.ROOT).endsWith(".yml"))
            .findFirst()
            .orElseThrow(() -> new Exception("No yaml file for OpenAPI spec in given directory (" + directory + ")"));
        setOpenAPISpecFileName(fileName);
    }
}
