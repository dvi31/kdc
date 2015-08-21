/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.xsd.v2010_10.common.Metadatas;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.kiwi.utils.BenchExecutor;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SBench extends D3SWebService {

    public enum BENCH_OPERATION {

        GRANT,
        READ,
        WRD,
    };

    @Option(name = "--safebox-path", required = true, usage = "Chemin vers le coffre ou sauvegarder le secret")
    private final String safeboxPath = null;

    @Option(name = "--domain-name", required = true, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = true, usage = "Login du compte; doit etre unique")
    private String appLogin;

    @Option(name = "--password-data", required = false, usage = "ID du mot de passe a lire")
    private String passwordData = "123456789";

    @Option(name = "--operation", required = true, usage = "Login du compte; doit etre unique")
    private BENCH_OPERATION operation;

    @Option(name = "--thread", required = true, usage = "number of concurrent threads (defaults to 1)")
    protected int thread;

    @Option(name = "--loop", required = true, usage = "number of loop by thread")
    protected int loop;

    private List<BenchExecutor> executors = new ArrayList<BenchExecutor>();

    public static void main(String[] args) {
        D3SBench monAppel = new D3SBench();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SBench.class.getName() + " FAILED");
            log.println(e.getMessage());
        }
    }

    private D3SVerifReadWrite dsvrw = new D3SVerifReadWrite();

    @SuppressWarnings("deprecation")
    @Override
    public void parseAndRun(String[] args) throws CmdLineException, IOException {
        CmdLineParser myParser = new CmdLineParser(this);
        super.parseAndRun(args);
        myParser.parseArgument(args);

        //build path
        final String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;
        final String boxPath = D3SConstants.SAFEBOX_PATH_PREFIX + "/" + safeboxPath;

        try {
            System.out.println(BenchExecutor.getPrintableHeader());
            for (int threadId = 0; threadId < thread; threadId++) {
                final AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory(), soapMessage);
                final StorageProxy stoProxy = new StorageProxy(storageWs, buildSSLSocketFactory(), soapMessage);

                final D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);
                //add thread id in appDomainName to bypass unicity constraint 
                String _appDomainName = appDomainName + "_" + threadId;
                //read var
                final String depositReadPath = "/DEPOSIT?_boxPath=" + boxPath
                        + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                        + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");
                //write var
                final String depositWritePath = "/DEPOSIT?_boxPath=" + boxPath
                        + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                        + "&appDomainName=" + URLEncoder.encode(_appDomainName, "UTF-8");
                final Metadatas metadata = secretManager.buildPasswordMetadata(appLogin, _appDomainName);

                executors.add(new BenchExecutor(threadId, loop) {
                    public boolean execute() {
                        try {
                            switch (operation) {
                                case GRANT: {
                                    authProxy.getSecretPort().grantWrite(
                                            userName, motivation, boxPath);
                                    return true;
                                }
                                case READ: {
                                    String dataRead = secretManager.read(
                                            userName, motivation, depositReadPath);
                                    return (dataRead != null);
                                }
                                case WRD: {
                                    //WRITE
                                    int exitCde = secretManager.write(userName, motivation, D3SConstants.SAFEBOX_PATH_PREFIX
                                            + "/" + safeboxPath, metadata, passwordData);
                                    //READ
                                    String dataRead = secretManager.read(userName, motivation, depositWritePath);
                                    if (dataRead == null) {
                                        exitCde = exitCde | D3SConstants.D3SError.ERROR_READ.value;
                                    }
                                    //DELETE
                                    exitCde = exitCde | secretManager.setDeletable(userName, motivation, depositWritePath);
                                    exitCde = exitCde | secretManager.discard(userName, motivation, depositWritePath);
                                    exitCde = exitCde | secretManager.delete(userName, motivation, depositWritePath);

                                    return (exitCde == 0);
                                }
                                default:
                                    return false;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }
                }
                );
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

            System.out.println(BenchExecutor.getPrintableTotal(executors, end - start));

            //print list SSL session
            printListSSLSesion();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
