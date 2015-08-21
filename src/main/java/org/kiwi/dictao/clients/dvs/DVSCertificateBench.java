/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dvs;

import com.dictao.dvs.ws.DVSResponseEx;
import com.dictao.dvs.ws.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.utils.Base64;
import org.kiwi.utils.BenchExecutor;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DVSCertificateBench extends StandardWebService {

    @Option(name = "--refreshCRLs", required = false, usage = "(optionnel) Force le serveur à re-télécharger les CRL avant traitement")
    private boolean refreshCRLs = false;
    @Option(name = "--plugin", multiValued = true, required = false, usage = "(optionnel) plugin à utiliser")
    private List<String> plugin = null;

    @Option(name = "--certificat", required = true, usage = "Certificat à valider")
    private File certificat = null;

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;
    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private final List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        DVSCertificateBench monAppel = new DVSCertificateBench();
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

            System.out.println(BenchExecutor.getPrintableHeader());

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream inStream = new FileInputStream(certificat);
            X509Certificate inCertif = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();

            for (int i = 0; i < thread; i++) {
                final DVSCaller dvSCaller = new DVSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
                final ArrayOfPluginParameterStruct mesParamsPlugins = dvSCaller.buildPluginParameter(plugin);
                final String strCertificat = Base64.encodeBytes(inCertif.getEncoded());

                executors.add(new BenchExecutor(i, loop) {
                    public boolean execute() {
                        try {

                            DVSResponseEx maReponse = dvSCaller.verifyCertificateEx(requestId,
                                    transactionId, refreshCRLs, tag,
                                    strCertificat, mesParamsPlugins);

                            int detailedStatus = 0;
                            for (DVSDetailedStatusStruct dt : maReponse.getDVSDetailedStatus().getDVSDetailedStatusStruct()) {
                                detailedStatus = (dt.getDVSStatus().intValue() == 0) ? detailedStatus : dt.getDVSStatus().intValue();
                            }

                            return (maReponse.getDVSGlobalStatus() == 0 && detailedStatus == 0);
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

            //print list SSL session
            printListSSLSesion();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
