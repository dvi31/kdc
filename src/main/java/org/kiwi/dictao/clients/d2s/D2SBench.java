/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.d2s;

import com.dictao.d2s.ws.D2SResponseEx;
import com.dictao.d2s.ws.D2SSoap;
import com.dictao.d2s.ws.DataType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.dictao.clients.StandardWebService;
import org.kiwi.utils.BenchExecutor;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D2SBench extends StandardWebService {

    @Option(name = "--dataToSign", required = false, usage = "Données à signer")
    protected File dataToSign = null;

    @Option(name = "--signatureFormat", required = true, usage = "Format de signature demandé")
    protected D2SCaller.FormatSignature signatureFormat = null;

    @Option(name = "--signatureType", required = true, usage = "Type de signature demandé")
    protected D2SCaller.TypeSignature signatureType = null;

    @Option(name = "--signatureParameter", required = false, usage = "(optionel) Paramètres de signature")
    protected String signatureParameter = null;

    @Option(name = "--signedFile", required = false, usage = "(optionel) Fichie signés en sortie")
    protected File signedFile = null;

    @Option(name = "--detachedSignature", required = false, usage = "(optionel) Signature détachée")
    protected File detachedSignature = null;

    @Option(name = "--c14n", required = false, usage = "(optionel) C14N - Canonicalize Xml")
    protected boolean isC14n = false;

    @Option(name = "--signatureContext", required = false, usage = "(optionel) Contexte de signature (clés)")
    protected String signatureContext = null;

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;
    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private final List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        D2SBench monAppel = new D2SBench();
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
                final D2SCaller d2SCaller = new D2SCaller(wsUri, wsdlUri, super.buildSSLSocketFactory(), soapMessage);

                final DataType maDataToSign = d2SCaller.buildDataType(
                        dataToSign, isPlaintext, charset);
                signatureParameter = d2SCaller.buildSignatureParameter(
                        signatureType, detachedSignature, isC14n);

                executors.add(new BenchExecutor(i, loop) {
                    public boolean execute() {
                        try {
                            D2SResponseEx maReponse = d2SCaller.signatureEx(maDataToSign,
                                    requestId, transactionId, tag, signatureFormat,
                                    signatureType, signatureParameter);
                            return (maReponse.getD2SStatus() == 0 && maReponse.getOpStatus() == 0);
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
