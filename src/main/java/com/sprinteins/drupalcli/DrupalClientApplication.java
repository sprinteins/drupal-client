package com.sprinteins.drupalcli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name ="drupal-client",
        subcommands = {Update.class, CommandLine.HelpCommand.class},
        description = "Test description",
        mixinStandardHelpOptions = true)
class DrupalClientApplication implements Runnable {

    public void run() {
        System.out.println("You are missing some arguments!");
        System.out.println("Type in --help or -h for help.");
    }

    public static void main(String[] args) {
        int status = new CommandLine(new DrupalClientApplication())
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(args);
        System.exit(status);
    }

}
