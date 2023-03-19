/**
 * Client.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 03/11/2022
 * Last update: 02/01/2023
*/

import java.io.*;
import java.net.DatagramSocket;

public class Client {

    /**
     * Client executor
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3)
            System.out.println("Wrong number of arguments... DNS can't solve your query!");
        else {
            Converter converter = new Converter("");
            String query = converter.createMessage(args[1],args[2]);

            DatagramSocket socket = new DatagramSocket();
            UDPconnection connection = new UDPconnection(socket);

            connection.sendDatagram(query,args[0]);
            String[] receive = connection.receiveDatagram(1024);
            socket.close();

            System.out.println(receive[0]);
        }
    }
}