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

    public Optional<String> getValueIgnoringCase(String key) {
        String value = variables.get(key);
        if (value != null) {
            return Optional.of(value);
        }
        return variables.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .filter(e -> !e.getValue().isBlank())
                .findFirst()
                .map(Entry::getValue)
                .map(String::trim);
    }

    private Optional<ProxySelector> parseProxySettings(String proxy) {
        return Optional.ofNullable(ProxyUtil.parseProxySettings(proxy));
    }

    @Override
    public ProxySelector getProxySelector() {
        var httpSelector = parseProxySettings(globalOptions.proxy)
                .or(() -> getValueIgnoringCase("http_proxy")
                    .flatMap(this::parseProxySettings));
        var httpsSelector = getValueIgnoringCase("https_proxy")
                .flatMap(this::parseProxySettings)
                .or(() -> httpSelector);
        
        ProtocolDispatchSelector result = new ProtocolDispatchSelector();
        httpSelector.ifPresent(ps -> result.setSelector("http", ps));
        httpsSelector.ifPresent(ps -> result.setSelector("https", ps));
        
        if (result.size() == 0) {
            return null;
        }
        
        Optional<ProxySelector> noProxy = Optional.ofNullable(globalOptions.noProxy)
                .map(String::trim)
                .or(() -> getValueIgnoringCase("no_proxy"))
                .map(np -> new ProxyBypassListSelector(np, result));
        return noProxy.orElse(result);
    }

}
