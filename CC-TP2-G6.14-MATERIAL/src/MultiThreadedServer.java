/**
 * Server.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 27/12/2022
 * Last update: 02/01/2023
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiThreadedServer implements Runnable{
    private String result;
    private UDPconnection connection;
    private LogRegister register;
    private String[] receive;
    private String origin;
    private Cache cache;
    private String type;
    private String allPath;

    /**
     * MultiThreadedServer constructor
     */
    public MultiThreadedServer(String result, UDPconnection connection, LogRegister register, String[] receive, String origin, Cache cache, String type, String allPath) {
        this.result = result;
        this.connection = connection;
        this.register = register;
        this.receive = receive;
        this.origin = origin;
        this.cache = cache;
        this.type = type;
        this.allPath = allPath;
    }

    /**
     * Run function that allows the object running in a multithreaded system
     */
    @Override
    public void run(){
        String[] splitAdress = result.split(";");
        String[] queryFields = splitAdress[0].split(",");
        while (queryFields[2].equals("1")) {
            try {
                connection.sendDatagram(splitAdress[0], splitAdress[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                register.addRegist("QE",splitAdress[1].trim(),splitAdress[0].trim(),allPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                receive = connection.receiveDatagram(1024);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                register.addRegist("QR",receive[1].trim(), receive[0].trim(),allPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            result = receive[0];

            splitAdress = result.split(";");
            queryFields = splitAdress[0].split(",");
        }

        try {
            connection.sendDatagram(result, origin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            register.addRegist("QE", origin, result.trim(),allPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (type.equals("SR")){
            String[] splitThis = result.split("\n");
            List<String> list = new ArrayList<>(Arrays.asList(splitThis).subList(1, splitThis.length));

            cache.createCache(list,"OTHERS");
        }

    }
}
