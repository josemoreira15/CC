/**
 * Cache.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 21/12/2022
 * Last update: 02/01/2023
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPconnection {

    private DatagramSocket socket;

    /**
     * UDPconnection constructor
     */
    public UDPconnection (DatagramSocket socket){
        this.socket = socket;
    }

    /**
     * Receive a datagram
     */
    public String[] receiveDatagram(int size) throws IOException {
        String[] result = new String[2];

        byte[] buffer = new byte[size];
        DatagramPacket datagramReceive = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(datagramReceive);
        String queryReceived = new String(datagramReceive.getData(), 0, datagramReceive.getData().length);

        InetAddress client = datagramReceive.getAddress();
        int clientPort = datagramReceive.getPort();
        result[1] = client.getHostAddress().concat(":").concat(String.valueOf(clientPort));

        result[0] = queryReceived;

        return result;
    }

    /**
     * Send a datagram
     */
    public void sendDatagram(String query, String IP) throws IOException {
        String[] location = IP.split(":");

        InetAddress address = InetAddress.getByName(location[0].trim());
        int port = Integer.parseInt(location[1].trim());

        DatagramPacket sendDatagram = new DatagramPacket(query.getBytes(), query.getBytes().length, address, port);
        this.socket.send(sendDatagram);
    }
}
