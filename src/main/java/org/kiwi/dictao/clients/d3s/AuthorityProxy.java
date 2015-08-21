package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.Authority;
import com.dictao.d3s.wsdl.v2.authority.DepositPort;
import com.dictao.d3s.wsdl.v2.authority.SafeboxPort;
import com.dictao.d3s.wsdl.v2.authority.SecretPort;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.util.LinkedList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import org.kiwi.utils.TraceHandler;

public class AuthorityProxy {

    private static final String WSDL_AUTHORITY = "/wsdl/d3s/v2/v2_1_Authority.wsdl";

    // web service proxy to call grantWrite
    private final SecretPort secretPort;
    private final DepositPort depositPort;
    private final SafeboxPort safeboxPort;
    private final SSLSocketFactory sslFactory;
    private final boolean soapMessage;

    private final Authority service;

    public AuthorityProxy(String baseurl, SSLSocketFactory sslFactory, boolean soapMessage) {
        this.sslFactory = sslFactory;
        this.soapMessage = soapMessage;
        service = new Authority(
                getClass().getResource(WSDL_AUTHORITY),
                new QName("http://www.dictao.com/d3s/wsdl/v2/Authority", "Authority"));

        secretPort = initSecretPort(baseurl + "SecretPort");
        depositPort = initDepositPort(baseurl + "DepositPort");
        safeboxPort = initSafeboxPort(baseurl + "SafeboxPort");
    }

    private SafeboxPort initSafeboxPort(String baseurl) {
        SafeboxPort port = service.getSafeboxPort();
        initPort((BindingProvider) port, baseurl, sslFactory, soapMessage);
        return port;
    }

    private DepositPort initDepositPort(String baseurl) {
        DepositPort port = service.getDepositPort();
        initPort((BindingProvider) port, baseurl, sslFactory, soapMessage);
        return port;
    }

    private SecretPort initSecretPort(String baseurl) {
        SecretPort port = service.getSecretPort();
        initPort((BindingProvider) port, baseurl, sslFactory, soapMessage);
        return port;
    }

    private void initPort(BindingProvider bindingProvider, String baseurl, SSLSocketFactory sslFactory, boolean soapMessage) {
        if (baseurl != null) {
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseurl);
            if (soapMessage) {
                List<Handler> handlerChain = new LinkedList<Handler>();
                handlerChain.add(new TraceHandler());
                bindingProvider.getBinding().setHandlerChain(handlerChain);
            }
        }

        // uncomment this lines if you what to use spécific keyStore and trustStore,
        // and not the default parameters from JVM. Do not forget to handle correctly
        // the exceptions.
        if (sslFactory != null) {
            try {
                bindingProvider.getRequestContext().put(
                        JAXWSProperties.SSL_SOCKET_FACTORY, sslFactory);
            } catch (Exception ex) {
                // do something to log/track execptions
                ex.printStackTrace();
            }
        }
    }

    public DepositPort getDepositPort() {
        return depositPort;
    }

    public SecretPort getSecretPort() {
        return secretPort;
    }

    public SafeboxPort getSafeboxPort() {
        return safeboxPort;
    }

}
