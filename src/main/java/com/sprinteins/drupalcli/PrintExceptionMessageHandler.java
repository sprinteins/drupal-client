package com.sprinteins.drupalcli;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.IExecutionExceptionHandler;

class PrintExceptionMessageHandler implements IExecutionExceptionHandler {
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        ParseResult parseResult){

        cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
