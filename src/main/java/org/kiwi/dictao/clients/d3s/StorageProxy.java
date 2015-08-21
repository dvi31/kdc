package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.storage.Storage;
import com.dictao.d3s.wsdl.v2.storage.SecretPort;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.util.LinkedList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import org.kiwi.utils.TraceHandler;

class StorageProxy {
    // web service proxy to call grantWrite

    private static final String WSDL_STORAGE = "/wsdl/d3s/v2/v2_1_Storage.wsdl";

    private SecretPort port;

    public StorageProxy(String baseurl, SSLSocketFactory sslFactory, boolean soapMessage) {
        Storage service = new Storage(
                getClass().getResource(WSDL_STORAGE),
                new QName("http://www.dictao.com/d3s/wsdl/v2/Storage", "Storage"));
        // get the proxy
        port = service.getSecretPort();
        initPort((BindingProvider) port, baseurl + "secret", sslFactory, soapMessage);

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

    SecretPort getSecretPort() {
        return port;
    }
}
