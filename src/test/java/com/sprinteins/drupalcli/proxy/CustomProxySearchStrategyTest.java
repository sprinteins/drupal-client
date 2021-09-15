package com.sprinteins.drupalcli.proxy;

import com.sprinteins.drupalcli.commands.GlobalOptions;
import org.junit.jupiter.api.Test;

import java.net.ProxySelector;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomProxySearchStrategyTest {
    
    @Test
    void testDefaultIsNull() throws Exception {
        GlobalOptions globalOptions = new GlobalOptions();
        ProxySelector proxySelector = getProxySelector(globalOptions);
        assertThat(proxySelector).isNull();
    }
    
    @Test
    void testProxyIsSet() throws Exception {
        GlobalOptions globalOptions = new GlobalOptions();
        globalOptions.proxy = "http://localhost:8888";
        ProxySelector proxySelector = getProxySelector(globalOptions);
        assertThat(proxySelector).isNotNull();
    }
    
    @Test
    void testProxyIsSetToCrap() throws Exception {
        GlobalOptions globalOptions = new GlobalOptions();
        globalOptions.proxy = "http://localhost:-8888";
        ProxySelector proxySelector = getProxySelector(globalOptions);
        assertThat(proxySelector).isNull();
    }
    
    @Test
    void testNoProxyIsSet() throws Exception {
        GlobalOptions globalOptions = new GlobalOptions();
        globalOptions.noProxy = "dhl.com";
        ProxySelector proxySelector = getProxySelector(globalOptions);
        assertThat(proxySelector).isNull();
    }

    
    @Test
    void testUnsetVariables() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of());
        assertThat(proxySelector).isNull();
    }
    
    @Test
    void testHttpProxy() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("http_proxy", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }
    
    @Test
    void testHTTP_PROXY() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("HTTP_PROXY", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }
    
    @Test
    void testHttpsProxy() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("https_proxy", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }
    
    @Test
    void testHTTPS_PROXY() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("HTTPS_PROXY", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }
    
    
    @Test
    void testHttpAndHttpsProxy() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("http_proxy", "http://localhost:8888", "https_proxy", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }
    
    @Test
    void testHTTPAndHTTPSProxy() throws Exception {
        ProxySelector proxySelector = getProxySelector(Map.of("HTTP_PROXY", "http://localhost:8888", "HTTPS_PROXY", "http://localhost:8888"));
        assertThat(proxySelector).isNotNull();
    }

    private ProxySelector getProxySelector(Map<String, String> variables) {
        var searchStrategy = new CustomProxySearchStrategy(new GlobalOptions(), variables);
        ProxySelector proxySelector = searchStrategy.getProxySelector();
        return proxySelector;
    }
    
    private ProxySelector getProxySelector(GlobalOptions globalOptions) {
        var searchStrategy = new CustomProxySearchStrategy(globalOptions, Map.of());
        ProxySelector proxySelector = searchStrategy.getProxySelector();
        return proxySelector;
    }
    

}
