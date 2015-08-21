/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dsam;

import com.dictao.dsam.ws.DSAMResponse;
import com.dictao.dsam.ws.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.kiwi.utils.TraceHandler;

/**
 *
 * @author i2165aq
 */
public class DSAMCaller {

    private static final String WSDL_DSAM_INTERFACE_FRONT_END = "/wsdl/dxs/42/DSAMInterface.wsdl";
    private final DSAMSoap monPort;

    public DSAMCaller(URL wsUri, URL wsdlUri, boolean soapMessage) {
        if (wsdlUri == null) {
            wsdlUri = getClass().getResource(WSDL_DSAM_INTERFACE_FRONT_END);
        }

        DSAM monDSAM = new DSAM(wsdlUri, new QName("http://www.dictao.com/DSAM/Interface", "DSAM"));
        monPort = monDSAM.getDSAMSoap();
        BindingProvider monProvider = (BindingProvider) monPort;
        if (monProvider != null) {
            monProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUri.toString());
            if (soapMessage) {
                List<Handler> handlerChain = new LinkedList<Handler>();
                handlerChain.add(new TraceHandler());
                monProvider.getBinding().setHandlerChain(handlerChain);
            }
        }
    }

    public DSAMResponse dsaMverifySignature(String requestId, String transactionId, com.dictao.dsam.ws.DataType maSignature, String tag) {
        return monPort.dsaMverifySignature(requestId,
                transactionId, maSignature, tag);
    }
}
