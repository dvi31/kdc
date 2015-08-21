package org.kiwi.dictao.clients.dvs;

import com.dictao.dvs.ws.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.dictao.responses.dvs.verifySignatureEx.Resultat;
import org.kiwi.utils.DataTypes;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class verifySignatureEx extends StandardWebService {

    @Option(name = "--refreshCRLs", required = false, usage = "(optionnel) Force le serveur à re-télécharger les CRL avant traitement")
    private boolean refreshCRLs = false;

    @Option(name = "--signature", required = true, usage = "Signature à valider")
    private File signature = null;
    /* A faire */
    @Option(name = "--signedData", required = false, usage = "Donnée objet de la signature")
    private File signedData = null;
    @Option(name = "--signedDataHash", required = false, usage = "Empreinte de la donnée signée")
    private String signedDataHash = null;
    @Option(name = "--certificat", required = false, usage = "Certificat du signataire (pour le format PKCS#1)")
    private File certificat = null;
    @Option(name = "--properties", required = false, usage = "Propriétés de la méthode de validationd de signature")
    private String properties = null;

    @Override
    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);

            System.out.println("refreshCRLs   : " + refreshCRLs);
            System.out.println("signature     : " + signature.getPath());
            System.out.println("signedData    : " + signedData/*.getPath()*/);
            System.out.println("signedDataHash: " + signedDataHash);
            System.out.println("certificat    : " + certificat/*.getPath()*/);
            System.out.println("properties    : " + properties);
            System.out.println();

            DVSCaller dvSCaller = new DVSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
            String strCertificat = new String();
            ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();
            DataType maSignature = dvSCaller.buildDataType(signature, isPlaintext, charset);
            DataType maSignedData = dvSCaller.buildDataType(signedData, false, null);

            beginCall();
            DVSResponseEx maReponse = dvSCaller.verifySignatureEx(requestId,
                    transactionId, refreshCRLs, tag,
                    maSignature, maSignedData, signedDataHash, strCertificat,
                    properties, mesParamsPlugins);
            endCall();

            System.out.println(new Resultat(maReponse));

            //print list SSL session
            printListSSLSesion();
            
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

    public static void main(String[] args) {
        verifySignatureEx monAppel = new verifySignatureEx();
        monAppel.parseAndRun(args);
    }
}
