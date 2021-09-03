package com.sprinteins.drupalcli.proxy;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.Logger.LogLevel;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.sprinteins.drupalcli.commands.GlobalOptions;

import java.net.ProxySelector;

public class GlobalOptionsProxySearchStrategy implements ProxySearchStrategy {

    private GlobalOptions globalOptions;

    public GlobalOptionsProxySearchStrategy(GlobalOptions globalOptions) {
        this.globalOptions = globalOptions;
    }
    
    @Override
    public String getName() {
        return "global-options";
    }

    @Override
    public ProxySelector getProxySelector() {
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();

        ProxySelector httpPS = ProxyUtil.parseProxySettings(globalOptions.proxy);
        if (httpPS != null) {           
            Logger.log(getClass(), LogLevel.TRACE, "Http Proxy is {}", globalOptions.proxy);
            ps.setSelector("http", httpPS);
            Logger.log(getClass(), LogLevel.TRACE, "Https Proxy is {}", globalOptions.proxy);
            ps.setSelector("https", httpPS);
        }

        // Wrap with white list support
        ProxySelector result = ps;
        if (globalOptions.noProxy != null && globalOptions.noProxy.trim().length() > 0) {
            Logger.log(getClass(), LogLevel.TRACE, "Using proxy bypass list: {}", globalOptions.noProxy);
            result = new ProxyBypassListSelector(globalOptions.noProxy, ps);
        }

        return result;
    }

}
