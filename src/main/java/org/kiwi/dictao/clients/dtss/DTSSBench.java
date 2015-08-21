/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.dtss;

import com.dictao.dtss.ws.ArrayOfPluginParameterStruct;
import com.dictao.dtss.ws.DTSSResponseEx;
import com.dictao.dtss.ws.DataType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.utils.BenchExecutor;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DTSSBench extends StandardWebService {

    @Option(name = "--signature", required = true, usage = "Signature à horodater")
    private File dataToSign = null;
    @Option(name = "--signatureParameter", required = false, usage = "Un ou plusieurs paramètres de signature, dépendants du format de signature demandé")
    private String signatureParameter = null;
    @Option(name = "--signedFile", required = false, usage = "(optionel) Fichie signés en sortie")
    private File signedFile = null;

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;
    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private final List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        DTSSBench monAppel = new DTSSBench();
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
                final DTSSCaller dtssCaller = new DTSSCaller(wsUri, wsdlUri, buildSSLSocketFactory(), soapMessage);
                final ArrayOfPluginParameterStruct mesParamsPlugins = new ArrayOfPluginParameterStruct();
                final DataType maSignature = dtssCaller.buildDataType(dataToSign, isPlaintext, charset);

                executors.add(new BenchExecutor(i, loop) {
                    public boolean execute() {
                        try {
                            DTSSResponseEx maReponse = dtssCaller.insertTimeStampEx(requestId,
                                    transactionId, tag, maSignature,
                                    signatureParameter, mesParamsPlugins);
                            return (maReponse.getDTSSGlobalStatus() == 0 && maReponse.getOpStatus() == 0);
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
            long stop = System.currentTimeMillis();

            //Print total
            System.out.println(BenchExecutor.getPrintableTotal(executors, stop - start));
            
            //print list SSL session
            printListSSLSesion();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
