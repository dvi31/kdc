package org.kiwi.dictao.clients.d2s;

import com.dictao.d2s.ws.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.dictao.clients.d2s.D2SCaller.FormatSignature;
import org.kiwi.dictao.clients.d2s.D2SCaller.TypeSignature;
import org.kiwi.dictao.responses.d2s.signatureEx.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class signatureEx extends StandardWebService {

    @Option(name = "--dataToSign", required = false, usage = "(optionel) Données à signer")
    protected File dataToSign = null;
    @Option(name = "--signatureFormat", required = true, usage = "Format de signature demandé")
    protected FormatSignature signatureFormat = null;
    @Option(name = "--signatureType", required = true, usage = "Type de signature demandé")
    protected TypeSignature signatureType = null;
    @Option(name = "--signatureParameter", required = false, usage = "(optionel) Paramètres de signature")
    protected String signatureParameter = null;

    @Option(name = "--signedFile", required = false, usage = "(optionel) Fichie signés en sortie")
    protected File signedFile = null;

    /* A faire */
    @Option(name = "--detachedSignature", required = false, usage = "(optionel) Signature détachée")
    protected File detachedSignature = null;

    @Option(name = "--c14n", required = false, usage = "(optionel) C14N - Canonicalize Xml")
    protected boolean isC14n = false;

    @Option(name = "--signatureContext", required = false, usage = "(optionel) Contexte de signature (clés)")
    protected String signatureContext = null;

    public static void main(String[] args) {
        signatureEx monAppel = new signatureEx();
        monAppel.parseAndRun(args);
    }

    @Override
    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);

            if (dataToSign != null) {
                System.out.println("dataToSign         : " + dataToSign.getPath());
            }
            if (detachedSignature != null) {
                System.out.println("detachedSignature  : " + detachedSignature.getPath());
            }
            System.out.println("signatureFormat    : " + signatureFormat);
            System.out.println("signatureType      : " + signatureType);
            System.out.println("signatureParameter : " + signatureParameter);
            System.out.println("signatureContext   : " + signatureContext);
            System.out.println();

            D2SCaller d2SCaller = new D2SCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
            DataType maDataToSign = d2SCaller.buildDataType(dataToSign, isPlaintext, charset);
            if (signatureParameter == null) {
                signatureParameter = d2SCaller.buildSignatureParameter(
                        signatureType, detachedSignature, isC14n);
            }

            beginCall();
            D2SResponseEx maReponse = d2SCaller.signatureEx(maDataToSign,
                    requestId, transactionId, tag, signatureFormat,
                    signatureType, signatureParameter);
            endCall();

            System.out.println(new Resultat(maReponse));

            if (maReponse.getD2SSignature() != null) {
                if (signedFile != null) {
                    System.out.println("Sortie dans : " + signedFile.getPath());
                    FileOutputStream monStream = new FileOutputStream(signedFile);
                    /* A revoir */
                    monStream.write(maReponse.getD2SSignature().getBinaryValue().getValue());

                } else {
                    System.out.println("Taille de la réponse signée : " + maReponse.getD2SSignature().getBinaryValue().getValue().length);
                }
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
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Erreur de calcul de hash : " + ex.getMessage());
        }
    }

}
