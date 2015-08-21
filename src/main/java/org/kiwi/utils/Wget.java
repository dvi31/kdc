package org.kiwi.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import org.kiwi.dictao.clients.HttpsWebServiceClient;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Wget extends HttpsWebServiceClient {

    @Option(name = "--outFile", required = true, usage = "outFile")
    private File outFile = null;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Wget.class.getName());

    public static void main(String[] args) {
        Wget monAppel = new Wget();
        monAppel.parseAndRun(args);
    }

    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            super.parseAndRun(arguments);
            monParseur.parseArgument(arguments);

            System.out.println("URL \t: " + wsUri);
            System.out.println("outFile \t: " + outFile.getAbsolutePath());
            System.out.println();
            long start = new Date().getTime();

            URL url;
            HttpsURLConnection connection = null;
            InputStream is = null;

            //Create connection
            //Uncomment this in case server demands some unsafe operations
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            connection = (HttpsURLConnection) wsUri.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");

//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, null, null);
//            SSLSocketFactory sslSocketFactory = context.getSocketFactory();
//            connection.setSSLSocketFactory(sslSocketFactory);
            //Process response
            is = connection.getInputStream();

            saveFile(outFile, is);

            long end = new Date().getTime();

            if (outFile != null) {
                System.out.println("Result saved : " + outFile.getAbsolutePath());
            }

            System.out.println("Time (ms) : " + (end - start));

        } catch (CmdLineException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            monParseur.printUsage(System.err);
        } catch (UnknownHostException ex) {
            System.err.println("Erreur de résolution du nom de machine : " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.err.println("Erreur de lecture du fichier : " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Erreur d\'E/S : " + ex.getMessage());
        } catch (Exception ex) {
            if (ex.getCause() != null) {
                System.err.println("Erreur : " + ex.getCause().getMessage());
            } else {
                System.err.println("Erreur : " + ex.getMessage());
            }
        }
    }

    public void saveFile(final File file, InputStream in)
            throws MalformedURLException, IOException {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
}
