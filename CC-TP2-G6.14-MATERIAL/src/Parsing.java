/**
 * Parsing.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 05/11/2022
 * Last update: 02/01/2023
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parsing {

    /**
     * Parsing constructor
     */
    public Parsing(){
    }

    /**
     * Config-file parser
     */
    public Map<String, Map<String,List<String>>> configsParse(String file_path, String domain) throws FileNotFoundException {
        File file = new File(file_path);
        Scanner scanner = new Scanner(file);

        Map<String, Map<String, List<String>>> configs = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] parseLine = scanner.nextLine().split(" ");

            if (parseLine.length > 1 && !parseLine[0].equals("#")){
                if(!parseLine[0].equals(domain) && !parseLine[0].equals("all") && !parseLine[0].equals("root"))
                    System.exit(1);
                if(parseLine[0].equals(domain))
                    if(!parseLine[1].equals("DB") && !parseLine[1].equals("SP") && !parseLine[1].equals("SS") && !parseLine[1].equals("DD") && !parseLine[1].equals("LG"))
                        System.exit(1);
                if(parseLine[0].equals("all") && !parseLine[1].equals("LG"))
                        System.exit(1);
                if(parseLine[0].equals("root") && !parseLine[1].equals("ST"))
                        System.exit(1);

                Map<String,List<String>> newMap = configs.get(parseLine[0]);
                if (newMap == null) {
                    newMap = new HashMap<>();
                    List<String> list = new ArrayList<>();
                    list.add(parseLine[2]);
                    newMap.put(parseLine[1],list);
                }

                else {
                    List<String> getList = newMap.get(parseLine[1]);
                    if (getList == null){
                        getList = new ArrayList<>();
                    }
                        getList.add(parseLine[2]);
                        newMap.put(parseLine[1], getList);
                }
                configs.put(parseLine[0],newMap);
            }
        }
        return configs;
    }

    /**
     * Root-Servers file parser
     */
    public List<String> topParsing(String file_path) throws FileNotFoundException {
        File file = new File(file_path);
        Scanner scanner = new Scanner(file);
        List<String> listIPs = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String addIP = scanner.nextLine();
            if(addIP.charAt(0) != '#' && addIP.charAt(0) != ' ')
                listIPs.add(addIP);
        }

        return listIPs;
    }
}