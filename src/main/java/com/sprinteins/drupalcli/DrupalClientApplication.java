package com.sprinteins.drupalcli;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.sprinteins.drupalcli.commands.Export;
import com.sprinteins.drupalcli.commands.ManifestVersionProvider;
import com.sprinteins.drupalcli.commands.Update;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.ProxySelector;

@Command(
        name ="drupal-client",
        subcommands = {Update.class, Export.class, CommandLine.HelpCommand.class},
        description = "Test description",
        mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class
        )
class DrupalClientApplication implements Runnable {

    @Override
    public void run() {
        System.out.println("You are missing some arguments!");
        System.out.println("Type in --help or -h for help.");
    }

    public static void main(String[] args) {
        ProxySearch proxySearch = new ProxySearch();
        proxySearch.addStrategy(Strategy.ENV_VAR);
        ProxySelector proxySelector = proxySearch.getProxySelector();
        if (proxySelector != null) {
            ProxySelector.setDefault(proxySelector);
        }
        
        int status = new CommandLine(new DrupalClientApplication())
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(args);
        System.exit(status);
    }

}
