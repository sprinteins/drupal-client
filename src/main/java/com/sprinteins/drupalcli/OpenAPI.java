package com.sprinteins.drupalcli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenAPI {

    private String openAPISpecFileName;
    private String directory;

    public String getOpenAPISpecFileName() { return openAPISpecFileName; }
    public void setOpenAPISpecFileName(String openAPISpecFile) { openAPISpecFileName = openAPISpecFile; }

    public String getDirectory() { return directory; }
    public void setDirectory(String directory) { this.directory = directory; }

    public OpenAPI(String path) throws Exception {
        setDirectory(path);
        setYamlFile();
    }

    private void setYamlFile() throws Exception {
        
        List<String> result = null;
        
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            result = walk.map(Path::toString)
                    .filter(file -> file.endsWith(".yaml"))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        assert result != null;
        if(result.size() == 0){
            throw new Exception("No yaml file for OpenAPI spec in given directory (" + directory + ")");
        }

        String fileName = result.get(0).split("/")[1];
        setOpenAPISpecFileName(fileName);
    }
}
