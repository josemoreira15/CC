/**
 * Server.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 03/11/2022
 * Last update: 02/01/2023
 */

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Server {

    /**
     * Server executor
     */
    public static void main (String[] args) throws IOException, InterruptedException {
        LocalTime time = LocalTime.now();

        if (args.length != 2)
            System.out.println("Wrong number of arguments... Server OFF!");

        else {
            Cache cache = new Cache(time);
            String type;

            String configPath = args[0];
            String domain = args[1];
            int points = (int) domain.chars().filter(ch -> ch == '.').count();

            Parsing parsingALL = new Parsing();

            Map<String, Map<String, List<String>>> configs = parsingALL.configsParse(configPath, domain);

            List<String> dbPaths = configs.get(domain).get("DB");
            String dbPath = null;

            int port;

            if(dbPaths == null) {
                type = "SS";
                port = domain.equals("messi") ? 5050 : 5051;
                if (points == 1) {
                    type = "SSS";
                    port = domain.equals("goat.messi") ? 5555 : 5556;
                }
            }
            else {
                type = "SP";
                dbPath = dbPaths.get(0);
                port = domain.equals("messi") ? 5005 : 5006;
                if (points == 1) {
                    type = "SPS";
                    port = domain.equals("goat.messi") ? 5500 : 5501;
                }
            }

            if(domain.equals(".")) {
                type = "ST";
                port = 7070;
            }

            List<String> dd = configs.get(domain).get("DD");
            if (dd != null) {
                type = "SR";
                port = 6666;
            }

            String logFilePath = configs.get(domain).get("LG").get(0);
            File logFile = new File(logFilePath);
            LogRegister register = new LogRegister(logFile);
            String allPath = configs.get("all").get("LG").get(0);

            register.addRegist("ST", String.valueOf(port), "debug",allPath);
            register.addRegist("EV", "config-file-read", configPath,allPath);
            register.addRegist("EV", "log-file-created", logFilePath,allPath);

            DatagramSocket socket = new DatagramSocket(port);
            if(type.equals("SP") || type.equals("ST") || type.equals("SPS")) {
                ConvertVar fileToList = new ConvertVar();
                List<String> listDB = fileToList.fileList(dbPath);

                if(type.equals("ST")) {
                    cache.rsCache(listDB);
                    register.addRegist("EV", "database-read-to-cache", dbPath,allPath);
                }
                else {
                    cache.createCache(listDB, "FILE");
                    register.addRegist("EV", "database-read-to-cache", dbPath,allPath);

                    new Thread(new TCPspMT(register,cache.getCache(),domain,configs.get(domain).get("SS"),allPath)).start();
                }
            }
            else if (type.equals("SS") || type.equals("SSS")){
                TCPss connectionToSP = new TCPss(register, cache, domain);
                connectionToSP.startSSTCP(configs.get(domain).get("SP").get(0),allPath);
            }

            List<String> topSV = new ArrayList<>();
            if(!type.equals("ST")) {
                String topFilePath = configs.get("root").get("ST").get(0);
                topSV = parsingALL.topParsing(topFilePath);
                register.addRegist("EV", "root-servers-file-read", topFilePath,allPath);
            }

            UDPconnection connection = new UDPconnection(socket);
            while (true) {
                String result;
                String[] receive = connection.receiveDatagram(64);
                String query = receive[0];
                String[] splitQuery = query.split(",");
                String origin = receive[1];
                register.addRegist("QR",origin,query.trim(),allPath);


                result = switch (type) {
                    case "ST" -> cache.findRSCache(receive[0]);
                    case "SS", "SP" -> cache.searchSOASP(receive[0], topSV, splitQuery[1]);
                    case "SPS", "SSS" -> cache.cacheSearch(receive[0], topSV, splitQuery[1]);
                    default -> cache.SRsearch(receive[0], topSV, configs);
                };

                if (splitQuery[1].equals("Q")) {
                    Thread thread = new Thread(new MultiThreadedServer(result, connection, register, receive,origin,cache,type,allPath));
                    thread.start();
                    thread.join();
                }

                else {
                    connection.sendDatagram(result, origin);
                    register.addRegist("QE", origin, result.trim(),allPath);

                    if (type.equals("SR")){
                        String[] splitThis = result.split("\n");
                        List<String> list = new ArrayList<>(Arrays.asList(splitThis).subList(1, splitThis.length));

                        cache.createCache(list,"OTHERS");
                    }
                }
            }
        }
    }
}