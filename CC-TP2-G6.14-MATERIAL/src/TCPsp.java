/**
 * TCPsp.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 22/11/2022
 * Last update: 02/01/2023
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class TCPsp implements Runnable {

    private LogRegister register;
    private List<String[]> myDB;
    private String domain;
    private List<String> ssIPS;
    private Socket socket;
    private String allPath;

    /**
     * TCPsp constructor
     */
    public TCPsp(LogRegister register, List<String[]> myDB, String domain, List<String> ssIPS, Socket socket, String allPath){
        this.register = register;
        this.myDB = myDB;
        this.domain = domain;
        this.ssIPS = ssIPS;
        this.socket = socket;
        this.allPath = allPath;
    }

    /**
     * TCP/Zone Transfer - Primary Server side executor
     */
    public void run() {
        try {
            DataInputStream dintSP = new DataInputStream(this.socket.getInputStream());
            DataOutputStream doutSP = new DataOutputStream(this.socket.getOutputStream());
            String receive = dintSP.readUTF();

            int recFlag = 0;
            InetAddress ssAddress = ((InetSocketAddress) this.socket.getRemoteSocketAddress()).getAddress();
            for (String ip : this.ssIPS) {
                InetAddress myConfigSSAddress = InetAddress.getByName(ip);
                if (ssAddress.equals(myConfigSSAddress)) {
                    recFlag = 1;
                    break;
                }
            }

            if (receive.equals(this.domain) && recFlag == 1) {
                ConvertVar convCache = new ConvertVar();
                String convertion = convCache.cacheToString(this.myDB);
                String count = String.valueOf(convertion.chars().filter(ch -> ch == '\n').count() + 1);
                doutSP.writeUTF(count);

                String answer = dintSP.readUTF();
                if (answer.equals(count)) {
                    doutSP.writeUTF(convertion);
                    this.register.addRegist("ZT", String.valueOf(ssAddress), "SP", this.allPath);
                }
            } else {
                doutSP.writeUTF("end");
                this.register.addRegist("EZ", String.valueOf(ssAddress), "SP", this.allPath);
            }
            this.socket.shutdownOutput();
            this.socket.shutdownInput();
            this.socket.close();
        } catch (IOException e){
            throw new RuntimeException();
        }
    }
}