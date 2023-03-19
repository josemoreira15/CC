/**
 * TCPss.java file
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

public class TCPss {

    private LogRegister register;
    private Cache cache;
    private String domain;

    /**
     * TCPss constructor
     */
    public TCPss(LogRegister register, Cache cache, String domain){
        this.register = register;
        this.cache = cache;
        this.domain = domain;
    }

    /**
     * TCP/Zone Transfer - Secondary Server side executor
     */
    public void startSSTCP(String sAddress, String allPath) throws IOException {
        InetAddress address = InetAddress.getByName(sAddress);
        System.out.println(address);
        Socket socketSS = new Socket(address,6060);
        String addressToLog = String.valueOf(((InetSocketAddress)socketSS.getRemoteSocketAddress()).getAddress());
        DataInputStream dintSS = new DataInputStream(socketSS.getInputStream());
        DataOutputStream doutSS = new DataOutputStream(socketSS.getOutputStream());
        doutSS.writeUTF(this.domain);
        String receive = dintSS.readUTF();

        if(!receive.equals("end")) {
            doutSS.writeUTF(receive);
            String db = dintSS.readUTF();
            ConvertVar toList = new ConvertVar();
            this.cache.createCache(toList.infoList(db),"SP");

            this.register.addRegist("ZT", addressToLog,"SS",allPath);

        }
        else
            this.register.addRegist("EZ", addressToLog,"SS", allPath);
        socketSS.close();
    }
}