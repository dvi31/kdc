package org.kiwi.utils;

import java.io.File;
import java.io.PrintStream;

public final class Logger {

    private static volatile Logger instance = null;
    private static PrintStream output = null;

    private Logger() {

        if (output == null) {
            output = System.out;
        }

    }

    public final static Logger getInstance() {
        //Le "Double-Checked Singleton"/"Singleton doublement v�rifi�" permet d'�viter un appel co�teux � synchronized, 
        //une fois que l'instanciation est faite.
        if (Logger.instance == null) {
            // Le mot-cl� synchronized sur ce bloc emp�che toute instanciation multiple m�me par diff�rents "threads".
            // Il est TRES important.
            synchronized (Logger.class) {
                if (Logger.instance == null) {
                    Logger.instance = new Logger();
                }
            }
        }

        return Logger.instance;
    }

    public static boolean setLogFile(String logFile) {
        boolean setResult = false;

        try {
            //v�rifie si le fichier existe
            File file = new File(logFile);

            if (!file.exists()) {
                file.createNewFile();
            }
            output = new PrintStream(logFile);
        } catch (Exception e) {

        }
        return setResult;

    }

    public void println(String info) {
        output.println(info);
    }

    public void print(String info) {
        output.print(info);
    }

}
