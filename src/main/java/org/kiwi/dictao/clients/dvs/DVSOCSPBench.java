/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dvs;

import java.io.File;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.dictao.clients.StandardWebService;
import static org.kiwi.dictao.clients.dvs.ocsp.generateOCSPRequest;
import org.kiwi.utils.Affichages;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.*;
import org.kiwi.dictao.clients.HttpsWebServiceClient;
import org.kiwi.dictao.responses.dvs.ocsp.Resultat;
import org.kiwi.utils.BenchExecutor;
import org.kiwi.utils.Connections;
import org.kiwi.utils.Hex;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DVSOCSPBench extends HttpsWebServiceClient {

    @Option(name = "--certificat", required = true, usage = "Certificat à vérifier")
    private File certFile = null;

    @Option(name = "--issuer-file", required = true, usage = "Emetteur du certificat à vérifier")
    private File issuerFile = null;

    @Option(name = "--ca-file", required = false, usage = "(optionnel) Emetteur du certificat de signature OCSP")
    private File caFile = null;
    @Option(name = "--va-file", required = false, usage = "(optionnel) Certificat publique de signature OCSP")
    private File vaFile = null;

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;
    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private final List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        DVSOCSPBench monAppel = new DVSOCSPBench();
        monAppel.parseAndRun(args);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);
        //build path
        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);
            Security.addProvider(new BouncyCastleProvider());

            final BigInteger certSerial = Affichages.getAndDisplayCertificate(
                    certFile, "Certificat à vérifier").getSerialNumber();
            X509Certificate issuerCertif = Affichages.getAndDisplayCertificate(
                    issuerFile, "Certificat émetteur");

            final PublicKey maCleDeValidation;
            if (vaFile != null) {
                maCleDeValidation = Affichages.getAndDisplayCertificate(
                        vaFile, "Certificat de signature OCSP (validation)").getPublicKey();
            } else {
                maCleDeValidation = null;
            }

            final X509Certificate monCertificatEmeteurSignature;
            if (caFile != null) {
                monCertificatEmeteurSignature = Affichages.getAndDisplayCertificate(
                        caFile, "Certificat émetteur de signature OCSP (validation)");
            } else {
                monCertificatEmeteurSignature = null;
            }

            final CertificateID id = new CertificateID(
                    CertificateID.HASH_SHA1, issuerCertif, certSerial);

            System.out.println(BenchExecutor.getPrintableHeader());

            for (int i = 0; i < thread; i++) {
                executors.add(new BenchExecutor(i, loop) {
                    public boolean execute() {
                        try {
                            OCSPReqGenerator generator = generateOCSPRequest();
                            generator.addRequest(id);
                            OCSPReq requete = generator.generate();

                            OCSPResp maReponse = new OCSPResp(
                                    Connections.getMyResponse(wsUri,
                                            requete.getEncoded(),
                                            "application/ocsp-request",
                                            "application/ocsp-response", false));

                            Resultat result = new Resultat(maReponse, maCleDeValidation,
                                    monCertificatEmeteurSignature);

                            return result.isValid();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }
                });
            }

            long start = System.currentTimeMillis();
            for (BenchExecutor ex : executors) {
                ex.start();
            }

            Thread.sleep(1000);

            boolean isTerminated = false;
            while (!isTerminated) {
                for (BenchExecutor ex : executors) {
                    isTerminated = ex.isTerminated();
                    if (!isTerminated) {
                        Thread.sleep(100);
                        break;
                    }
                }
            }
            long end = System.currentTimeMillis();

            //Print total
            System.out.println(BenchExecutor.getPrintableTotal(executors, end - start));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
