package org.kiwi.dictao.clients.dvs;

import com.dictao.dvs.ws.*;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.dictao.responses.dvs.verifyCertificateEx.Resultat;
import org.kiwi.utils.Base64;
import org.kiwi.utils.TraceHandler;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class verifyCertificateEx extends StandardWebService {

    @Option(name = "--refreshCRLs", required = false, usage = "(optionnel) Force le serveur à re-télécharger les CRL avant traitement")
    private boolean refreshCRLs = false;
    @Option(name = "--plugin", multiValued = true, required = false, usage = "(optionnel) plugin à utiliser")
    private List<String> plugin = null;

    @Option(name = "--certificat", required = true, usage = "Certificat à valider")
    private File certificat = null;

    public static void main(String[] args) {
        verifyCertificateEx monAppel = new verifyCertificateEx();
        monAppel.parseAndRun(args);
    }

    @Override
    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);

            System.out.println("refreshCRLs   : " + refreshCRLs);
            System.out.println("certificat    : " + certificat.getPath());

            X509Certificate inCertif = loadCertificate(certificat);
            System.out.println("certificat(i) : " + inCertif.getSubjectDN() + " (sn : " + inCertif.getSerialNumber() + '/' + inCertif.getSerialNumber().toString(16) + ")");
            System.out.println("certificat(i2): " + inCertif.getNotBefore() + " -> " + inCertif.getNotAfter());
            System.out.println("emeteur       : " + inCertif.getIssuerDN());
            System.out.println();

            DVSCaller dvSCaller = new DVSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
            ArrayOfPluginParameterStruct mesParamsPlugins = dvSCaller.buildPluginParameter(plugin);
            String strCertificat = Base64.encodeBytes(inCertif.getEncoded());

            beginCall();
            DVSResponseEx maReponse = dvSCaller.verifyCertificateEx(requestId,
                    transactionId, refreshCRLs, tag,
                    strCertificat, mesParamsPlugins);
            endCall();

            System.out.println(new Resultat(maReponse));

            //print list SSL session
            printListSSLSesion();
            
        } catch (CertificateException ex) {
            System.err.println("Erreur de certificat : " + ex.getMessage());
        } catch (CmdLineException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            monParseur.printUsage(System.err);
        } catch (UnknownHostException ex) {
            System.err.println("Erreur de résolution du nom de machine : " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.err.println("Erreur de lecture du fichier : " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Erreur d\'E/S : " + ex.getMessage());
        }
    }

    private X509Certificate loadCertificate(File certificat) throws CertificateException, IOException, FileNotFoundException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream inStream = new FileInputStream(certificat);
        X509Certificate inCertif = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();
        return inCertif;
    }

}
