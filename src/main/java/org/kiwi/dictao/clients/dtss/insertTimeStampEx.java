package org.kiwi.dictao.clients.dtss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.dictao.responses.dtss.insertTimeStampEx.Resultat;
import com.dictao.dtss.ws.*;
import java.io.FileOutputStream;
import org.kiwi.utils.DataTypes;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class insertTimeStampEx extends StandardWebService {

    @Option(name = "--signature", required = true, usage = "Signature à horodater")
    private File dataToSign = null;
    @Option(name = "--signatureParameter", required = false, usage = "Un ou plusieurs paramètres de signature, dépendants du format de signature demandé")
    private String signatureParameter = null;
    @Option(name = "--signedFile", required = false, usage = "(optionel) Fichie signés en sortie")
    private File signedFile = null;

    public static void main(String[] args) {
        insertTimeStampEx monAppel = new insertTimeStampEx();
        monAppel.parseAndRun(args);
    }

    @Override
    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);

            System.out.println("signature          : " + dataToSign.getPath());
            System.out.println("signatureParameter : " + signatureParameter);
            System.out.println();

            DTSSCaller dtssCaller = new DTSSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);

            ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();
            DataType maSignature = dtssCaller.buildDataType(dataToSign, isPlaintext, charset);
            
            beginCall();
            DTSSResponseEx maReponse = dtssCaller.insertTimeStampEx(requestId,
                    transactionId, tag, maSignature,
                    signatureParameter, mesParamsPlugins);
            endCall();

            System.out.println(new Resultat(maReponse));

            if (signedFile != null) {
                System.out.println("Sortie dans : " + signedFile.getPath());
                FileOutputStream monStream = new FileOutputStream(signedFile);
                if (maReponse.getExtendedSignature() != null) {
                    /* A revoir */
                    monStream.write(maReponse.getExtendedSignature().getBinaryValue().getValue());
                }
            } else {
                System.out.println("Taille de la réponse signée : " + maReponse.getExtendedSignature().getBinaryValue().getValue().length);
            }

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

}
