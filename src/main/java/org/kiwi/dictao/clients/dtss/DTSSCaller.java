/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dtss;

import com.dictao.dtss.ws.*;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.kiwi.utils.DataTypes;
import org.kiwi.utils.TraceHandler;

/**
 *
 * @author i2165aq
 */
public class DTSSCaller {

    private static final String WSDL_DTSS_INTERFACE_FRONT_END = "/wsdl/dxs/49/DTSSInterfaceFrontEnd.wsdl";
    private final DTSSSoap monPort;

    public DTSSCaller(URL wsUri, URL wsdlUri, SSLSocketFactory sslFactory, boolean soapMessage) {
        if (wsdlUri == null) {
            wsdlUri = getClass().getResource(WSDL_DTSS_INTERFACE_FRONT_END);
        }

        DTSS monDTSS = new DTSS(wsdlUri, new QName("http://www.dictao.com/DTSS/Interface", "DTSS"));
        monPort = monDTSS.getDTSSSoap();
        BindingProvider bindingProvider = (BindingProvider) monPort;
        if (bindingProvider != null) {
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUri.toString());
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

    public DTSSResponseEx insertTimeStampEx(String requestId, String transactionId, String tag,
            DataType maSignature, String signatureParameter, ArrayOfPluginParameterStruct mesParamsPlugins) {
        return monPort.insertTimeStampEx(requestId,
                transactionId, tag, maSignature,
                signatureParameter, mesParamsPlugins);
    }

    public DataType buildDataType(File dataToSign, boolean isPlaintext, String charset) throws IOException {
        DataType maDataToSign = new DataType();
        if (dataToSign != null) {
            if (isPlaintext) {
                DataString maStringData = new DataString();
                maStringData.setDataFormat(null);

                if (charset != null) {
                    maStringData.setValue(DataTypes.osStringFromFile(dataToSign, charset));
                } else {
                    maStringData.setValue(DataTypes.osStringFromFile(dataToSign));
                }

                maDataToSign.setValue(maStringData);

            } else {
                DataBinary maBinaryData = new DataBinary();
                maBinaryData.setDataFormat(null);
                maBinaryData.setValue(DataTypes.osArrayFromFile(dataToSign).toByteArray());

                maDataToSign.setBinaryValue(maBinaryData);
            }
        }
        return maDataToSign;
    }
}
