package com.sprinteins.drupalcli;

import com.sprinteins.drupalcli.exceptions.ForbiddenException;
import com.sprinteins.drupalcli.exceptions.UnauthorizedException;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

class PrintExceptionMessageHandler implements IExecutionExceptionHandler {

	@Override
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        ParseResult parseResult){

	    String errorText = "Something went wrong";
	    if (ex instanceof ForbiddenException) {
	        errorText = "You don't seem to have access.\n- Is the API key correct?\n- Are you using the correct page ID?";
	    } else if (ex instanceof UnauthorizedException) {
	        errorText = "You don't seem to be authorized.\n- Is the API key correct?\n- Are you using the correct page ID?";
	    } else if (ex.getMessage() != null) {
	        errorText = ex.getMessage();
	    }

	    cmd.getErr().println(cmd.getColorScheme().errorText(errorText));
        
        if (parseResult.expandedArgs().contains("--debug")) { 
            ex.printStackTrace(cmd.getErr());
        }

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
