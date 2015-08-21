/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dvs;

import com.dictao.dvs.ws.ArrayOfPluginParameterStruct;
import com.dictao.dvs.ws.DVS;
import com.dictao.dvs.ws.DVSResponseEx;
import com.dictao.dvs.ws.DVSSoap;
import com.dictao.dvs.ws.DataBinary;
import com.dictao.dvs.ws.DataString;
import com.dictao.dvs.ws.DataType;
import com.dictao.dvs.ws.PluginParameterStruct;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLContext;
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
public class DVSCaller {

    private static final String WSDL_DVS_INTERFACE_FRONT_END = "/wsdl/dxs/49/DVSInterfaceFrontEnd.wsdl";
    private final DVSSoap monPort;

    public DVSCaller(URL wsUri, URL wsdlUri, SSLSocketFactory sslFactory, boolean soapMessage) {
        if (wsdlUri == null) {
            wsdlUri = getClass().getResource(WSDL_DVS_INTERFACE_FRONT_END);
        }

        DVS monDVS = new DVS(wsdlUri, new QName("http://www.dictao.com/DVS/Interface", "DVS"));
        monPort = monDVS.getDVSSoap();
        BindingProvider bindingProvider = (BindingProvider) monPort;
        if (bindingProvider != null) {
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUri.toString());
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

    public DVSResponseEx verifyCertificateEx(String requestId,
            String transactionId, boolean refreshCRLs, String tag,
            String strCertificat, ArrayOfPluginParameterStruct mesParamsPlugins) {
        return monPort.verifyCertificateEx(requestId,
                transactionId, (refreshCRLs ? 1 : 0), tag,
                strCertificat, mesParamsPlugins);
    }

    public DVSResponseEx verifySignatureEx(String requestId, String transactionId,
            boolean refreshCRLs, String tag, DataType maSignature,
            DataType maSignedData, String signedDataHash, String strCertificat,
            String properties, ArrayOfPluginParameterStruct mesParamsPlugins) {

        return monPort.verifySignatureEx(requestId, transactionId,
                (refreshCRLs ? 1 : 0), tag, maSignature, maSignedData,
                signedDataHash, strCertificat, properties, mesParamsPlugins);
    }

    public ArrayOfPluginParameterStruct buildPluginParameter(List<String> plugin) {
        ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();
        if (plugin != null) {
            for (String monPlugin : plugin) {
                PluginParameterStruct monParam = new PluginParameterStruct();
                System.out.println("Plugin : " + monPlugin);
                monParam.setLabel(monPlugin);
                monParam.setData("");
                mesParamsPlugins.getPluginParameterStruct().add(monParam);
            }
        }
        return mesParamsPlugins;
    }

    public DataType buildDataType(File file, boolean isPlaintext, String charset) throws IOException {
        DataType maSignature = new DataType();
        if (file == null) {
            return maSignature;
        }
        if (isPlaintext) {
            DataString maStringData = new DataString();
            maStringData.setDataFormat(null);

            if (charset != null) {
                maStringData.setValue(DataTypes.osStringFromFile(file, charset));
            } else {
                maStringData.setValue(DataTypes.osStringFromFile(file));
            }

            maSignature.setValue(maStringData);

        } else {
            DataBinary maBinaryData = new DataBinary();
            maBinaryData.setDataFormat(null);
            maBinaryData.setValue(DataTypes.osArrayFromFile(file).toByteArray());

            maSignature.setBinaryValue(maBinaryData);
        }
        return maSignature;
    }
}
