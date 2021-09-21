package com.sprinteins.drupalcli.proxy;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.sprinteins.drupalcli.commands.GlobalOptions;

import java.net.ProxySelector;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;

public class CustomProxySearchStrategy implements ProxySearchStrategy {

    private final GlobalOptions globalOptions;
    private final Map<String, String> variables;

    public CustomProxySearchStrategy(GlobalOptions globalOptions, Map<String, String> variables) {
        this.globalOptions = globalOptions;
        this.variables = variables;
    }

    @Override
    public String getName() {
        return "custom";
    }

    @Override
    public ProxySelector getProxySelector() {
        ProtocolDispatchSelector result = new ProtocolDispatchSelector();
        
        parseProxySettings(globalOptions.proxy)
                .or(() -> getValueIgnoringCase("http_proxy")
                    .flatMap(this::parseProxySettings))
                .ifPresent(ps -> {
                    result.setSelector("http", ps);
                    result.setSelector("https", ps);
                });
        getValueIgnoringCase("https_proxy")
                .flatMap(this::parseProxySettings)
                .ifPresent(ps -> result.setSelector("https", ps));
        
        if (result.size() == 0) {
            return null;
        }
        
        return Optional.ofNullable(globalOptions.noProxy)
                .or(() -> getValueIgnoringCase("no_proxy"))
                .filter(Predicate.not(String::isBlank))
                .map(noProxy -> (ProxySelector) new ProxyBypassListSelector(noProxy, result))
                .orElse(result);
    }
    
    private Optional<String> getValueIgnoringCase(String key) {
        return Optional.ofNullable(variables.get(key))
                .or(() -> variables.entrySet()
                        .stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(key))
                        .findFirst()
                        .map(Entry::getValue));
    }

    private Optional<ProxySelector> parseProxySettings(String proxy) {
        return Optional.ofNullable(ProxyUtil.parseProxySettings(proxy));
    }

}
