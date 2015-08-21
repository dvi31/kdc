package org.kiwi.dictao.clients.d2s;

import com.dictao.d2s.ws.*;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.apache.xml.security.c14n.Canonicalizer;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.dictao.responses.d2s.signatureEx.*;
import org.kiwi.utils.Base64;
import org.kiwi.utils.DataTypes;
import org.kiwi.utils.TraceHandler;

public class D2SCaller {

    private static final String WSDL_D2S_INTERFACE_FRONT_END = "/wsdl/dxs/49/D2SInterfaceFrontEnd.wsdl";
    private final D2SSoap monPort;

    public enum FormatSignature {

        XMLDSIG, XADES,
        PKCS7, CMS, SMIME,
        PDF,
        PKCS1
    };

    public enum TypeSignature {

        ENVELOPING, ENVELOPING_CO, ENVELOPING_COUNT,
        ENVELOPED, ENVELOPED_COUNT,
        DETACHED, DETACHED_CO, DETACHED_COUNT,
        MANIFEST,
        SHA1RSA, SHA256RSA, SHA384RSA, SHA512RSA,
        MD5RSA
    };

    public D2SCaller(URL wsUri, URL wsdlUri, SSLSocketFactory sslFactory, boolean soapMessage) {
        if (wsdlUri == null) {
            wsdlUri = getClass().getResource(WSDL_D2S_INTERFACE_FRONT_END);
        }

        D2S monD2S = new D2S(wsdlUri, new QName("http://www.dictao.com/D2S/Interface", "D2S"));
        monPort = monD2S.getD2SSoap();
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

    public D2SResponseEx signatureEx(DataType maDataToSign,
            String requestId, String transactionId, String tag,
            FormatSignature signatureFormat, TypeSignature signatureType,
            String signatureParameter) throws IOException {

        DataType maDetached = new DataType();
        ContextType monContext = new ContextType();
        ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();

        D2SResponseEx maReponse = monPort.signatureEx(requestId, transactionId,
                tag, maDataToSign,
                maDetached, signatureFormat.toString(),
                signatureType.toString(), signatureParameter,
                monContext, mesParamsPlugins);

        return maReponse;
    }

    public String buildSignatureParameter(TypeSignature signatureType, File detachedSignature, boolean isC14n) throws NoSuchAlgorithmException, IOException {
        String signParam;
        if (detachedSignature == null) {
            return null;
        }
        switch (signatureType) {
            case MANIFEST:
                signParam = computeManifest(detachedSignature.getPath(), isC14n);
                break;
            default:
                signParam = computeDetachedSignature(detachedSignature.getPath(), isC14n);
        }
        return signParam;
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

    public String computeManifest(String file, boolean isC14n) throws IOException, NoSuchAlgorithmException {
        String hash = isC14n ? computeXMLHash(file) : computeHash(file);
        String transform = "";
        if (isC14n) {
            transform = "<Transforms>"
                    + "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"
                    + "</Transforms>";
        }

        StringBuilder sigParam = new StringBuilder();
        sigParam.append("<Parameters>");
        sigParam.append("<Manifest>");
        String signatureParameterManifest = "<Reference>"
                + transform
                + "<DigestValue>%s</DigestValue>"
                + "<DigestMethod>%s</DigestMethod>"
                + "<URI>%s</URI>"
                + "</Reference>";

        sigParam.append(String.format(
                signatureParameterManifest,
                hash,
                "SHA256",
                "mydata#" + UUID.randomUUID().toString()
        ));

        sigParam.
                append("</Manifest>").
                append("</Parameters>");
        return sigParam.toString();
    }

    public String computeDetachedSignature(String file, boolean isC14n) throws IOException, NoSuchAlgorithmException {
        String hash = isC14n ? computeXMLHash(file) : computeHash(file);
        String transform = "";
        if (isC14n) {
            transform = "<Transforms>"
                    + "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"
                    + "</Transforms>";
        }

        StringBuilder sigParam = new StringBuilder();
        sigParam.append("<Parameters>");
        sigParam.append("<DetachedSignature>");
        String signatureParameterManifest = "<Reference>"
                + transform
                + "<DigestValue>%s</DigestValue>"
                + "<DigestMethod>%s</DigestMethod>"
                + "<URI>%s</URI>"
                + "</Reference>";

        sigParam.append(String.format(
                signatureParameterManifest,
                hash,
                "SHA256",
                "mydata#" + UUID.randomUUID().toString()
        ));

        sigParam.
                append("</DetachedSignature>").
                append("</Parameters>");
        return sigParam.toString();
    }

    public String computeHash(String file) throws IOException, NoSuchAlgorithmException {
        InputStream fs = new FileInputStream(file);
        DigestInputStream dgst = new DigestInputStream(fs,
                MessageDigest.getInstance("SHA-256"));
        while (dgst.read() != -1) {
        }
        String hash = Base64.encodeBytes(dgst.getMessageDigest().digest());
        fs.close();

        return hash;
    }

    public String computeXMLHash(String file) {
        try {
//            InputStream fs = new FileInputStream(file);
            File fs = new File(file);
            org.apache.xml.security.Init.init();
            Canonicalizer c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            byte[] data = c14n.canonicalize(DataTypes.osArrayFromFile(fs).toByteArray());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hash = Base64.encodeBytes(md.digest(data));
            return hash;
        } catch (Exception ex) {
            System.err.println("Erreur de calcul de hash : " + ex.getMessage());
        }
        return null;
    }
}
