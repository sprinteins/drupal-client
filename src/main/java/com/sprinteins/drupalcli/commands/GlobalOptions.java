package com.sprinteins.drupalcli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;

@Command
public class GlobalOptions {
    
    @Option(
            names = { "--debug" },
            description = "Enable debug mode"
            )
    public boolean debug;
    
    @Option(
            names = { "--api-page-directory" },
            description = "Local path to the API page documentation",
            defaultValue = "api-docs"
            )
    public Path apiPageDirectory;
    
    @Option(
            names = { "--token-file" },
            description = { "Path to the file containing the authentication token.",
                    "Can also be set via environment variable DHL_API_DEVELOPER_PORTAL_TOKEN_FILE" },
            defaultValue = "${env:DHL_API_DEVELOPER_PORTAL_TOKEN_FILE}",
            required = true
            )
    public Path tokenFile;

}
