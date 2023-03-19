/**
 * TCPsp.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 02/01/2023
 * Last update: 02/01/2023
 */

import java.io.IOException;
import java.net.*;
import java.util.List;

public class TCPspMT implements Runnable {

    private LogRegister register;
    private List<String[]> cache;
    private String domain;
    private List<String> ssIPS;
    private String allPath;

    /**
     * TCPspMT constructor
     */
    public TCPspMT(LogRegister register, List<String[]> cache, String domain, List<String> ssIPS, String allPath) {
        this.register = register;
        this.cache = cache;
        this.domain = domain;
        this.ssIPS = ssIPS;
        this.allPath = allPath;
    }

    /**
     * Run method that calls another thread
     */
    public void run() {
        try {
            ServerSocket server = new ServerSocket(6060);
            while (true) {
                Socket socket = server.accept();
                Thread thread = new Thread(new TCPsp(this.register, this.cache, this.domain, this.ssIPS, socket, this.allPath));
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
