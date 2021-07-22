package com.sprinteins.drupalcli;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

class PrintExceptionMessageHandler implements IExecutionExceptionHandler {

	@Override
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        ParseResult parseResult){

        cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));
        
        if (parseResult.expandedArgs().contains("--debug")) { 
            ex.printStackTrace(cmd.getErr());
        }

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
