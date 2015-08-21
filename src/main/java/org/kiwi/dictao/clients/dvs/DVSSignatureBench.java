/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dvs;

import com.dictao.dvs.ws.DVSResponseEx;
import com.dictao.dvs.ws.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.utils.BenchExecutor;
import org.kiwi.utils.DataTypes;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DVSSignatureBench extends StandardWebService {

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

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;
    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private final List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        DVSSignatureBench monAppel = new DVSSignatureBench();
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

            for (int i = 0; i < thread; i++) {
                final DVSCaller dvSCaller = new DVSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
                final String strCertificat = new String();
                final ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();
                final DataType maSignature = dvSCaller.buildDataType(signature, isPlaintext, charset);
                final DataType maSignedData = dvSCaller.buildDataType(signedData, false, null);

                executors.add(new BenchExecutor(i, loop) {
                    public boolean execute() {
                        try {
                            DVSResponseEx maReponse = dvSCaller.verifySignatureEx(requestId,
                                    transactionId, refreshCRLs, tag,
                                    maSignature, maSignedData, signedDataHash, strCertificat,
                                    properties, mesParamsPlugins);

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
