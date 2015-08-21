package org.kiwi.dictao.clients;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class HttpsWebServiceClient extends HttpWebServiceClient {

    @Option(name = "--keystore", required = false, usage = "(optionnel) Certificat de connexion au WebService")
    protected File keystoreFile = null;
    @Option(name = "--keystore-type", required = false, usage = "(optionnel) Type de certificat de connexion au WebService (JKS/PKCS12)")
    protected String keystoreType = "JKS";
    @Option(name = "--keystore-pass", required = false, usage = "(optionnel) Mot de passe du catalogue de certificat de confiance pour le serveur")
    protected String keystorePass = "changeit";
    @Option(name = "--truststore", required = false, usage = "(optionnel) Catalogue de certificats de confiance (TrustStore au format JKS)")
    protected File truststoreFile = null;
    @Option(name = "--truststore-pass", required = false, usage = "mot de passe du trustore")
    protected String trustStorePassword = null;

    private Map<String, String> sslSessions = new HashMap<String, String>();

    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {
        super.parseAndRun(arguments);

        CmdLineParser monParseur = new CmdLineParser(this);
        monParseur.parseArgument(arguments);

        if (truststoreFile != null) {
            System.setProperty("javax.net.ssl.trustStore", truststoreFile.getCanonicalPath());

        }
        if (trustStorePassword != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        }

        if (keystoreFile != null) {
            System.setProperty("javax.net.ssl.keyStore", keystoreFile.getCanonicalPath());
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
            System.setProperty("javax.net.ssl.keyStoreType", keystoreType);
        }

//        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
    }

    public SSLSocketFactory buildSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("SSLv3");

            KeyManagerFactory kmf
                    = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            KeyStore ks = null;
            char[] toCharArray = null;
            if (keystoreFile != null) {
                ks = KeyStore.getInstance(keystoreType);
                ks.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
                toCharArray = keystorePass.toCharArray();
            }
            kmf.init(ks, toCharArray);

            //DO NOT USED THAT IN PRODUCTION 
            final TrustManager[] trustManager = new TrustManager[]{new myTrustManager(sc, sslSessions)};

            sc.init(kmf.getKeyManagers(), trustManager, null);

            return sc.getSocketFactory();
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HttpsWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void printListSSLSesion() {
        System.out.println("\nList SSL sessions:");
        for (Map.Entry<String, String> session : sslSessions.entrySet()) {
            System.out.println(String.format("%s:%s", session.getKey(), session.getValue()));
        }
    }

    private static class myTrustManager implements
            X509TrustManager {

        final TrustManagerFactory tmf;
        final SSLContext sc;
        final String uid = UUID.randomUUID().toString();
        Map<String, String> sslSessions;

        myTrustManager(SSLContext sc, Map<String, String> sslSession) throws NoSuchAlgorithmException, KeyStoreException {
            this.sc = sc;
            this.sslSessions = sslSession;
            this.tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs,
                String authType) throws CertificateException {
            final X509TrustManager name = (X509TrustManager) tmf.getTrustManagers()[0];
            name.checkClientTrusted(certs, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs,
                String authType) throws CertificateException {

            final String cn = certs[0].getSubjectDN().getName();

            if (sslSessions.get(uid) != null && sslSessions.get(uid).equals(cn)) {
                System.out.println("WARNING cn has changed :" + uid + ": " + sslSessions.get(uid));
            }
            sslSessions.put(uid, certs[0].getSubjectDN().getName());

            final X509TrustManager name = (X509TrustManager) tmf.getTrustManagers()[0];
            name.checkServerTrusted(certs, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
            final X509TrustManager name = (X509TrustManager) tmf.getTrustManagers()[0];
            return name.getAcceptedIssuers();
        }
    }
}
