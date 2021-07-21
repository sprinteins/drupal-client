package com.sprinteins.drupalcli.commands;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "export", description = "Export page")
public class Export implements Callable<Integer> {

    @Option(names = { "--html" }, description = "Local path to the html file", required = true)
    String htmlFile;

    @Override
    public Integer call() throws Exception {
        String html = Files.readString(Paths.get(htmlFile));
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
        System.out.println(markdown);
        return 0;
    }

}
