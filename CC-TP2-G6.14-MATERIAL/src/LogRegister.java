/**
 * LogRegister.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 21/11/2022
 * Last update: 02/01/2023
 */

import java.io.*;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogRegister {

    private File logFile;
    private Lock lock = new ReentrantLock();

    /**
     * LogRegister constructor
     */
    public LogRegister(File logFile) {
        this.logFile = logFile;
    }

    /**
     * Log-file filler
     */
    public void addRegist(String type, String address, String message, String allPath) throws IOException {
        FileOutputStream fOut = new FileOutputStream(this.logFile, true);
        OutputStreamWriter osw = new OutputStreamWriter(fOut);

        File all = new File(allPath);
        FileOutputStream allOut = new FileOutputStream(all, true);
        OutputStreamWriter allOsw = new OutputStreamWriter(allOut);

        String result = Instant.now() + "  " +
                type + "  " +
                "@" + "  " +
                address + "  " +
                message + "\n";
        System.out.print(result);
        osw.write(result);
        osw.flush();
        osw.close();

        lock.lock();
        allOsw.write(result);
        allOsw.flush();
        allOsw.close();
        lock.unlock();
    }
}