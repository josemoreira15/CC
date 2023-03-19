/**
 * ConvertVar.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 21/11/2022
 * Last update: 02/01/2023
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConvertVar {

    /**
     * ConvertVar constructor
     */
    public ConvertVar() {
    }

    /**
     * Convert the content of the cache in a single string
     */
    public String cacheToString(List<String[]> cache) {
        int index = 1;
        StringBuilder sb = new StringBuilder();
        for(String[] line : cache){
            sb.append(index).append(" ");
            for(int i=0; i<5;i++) {
                sb.append(line[i]).append(" ");
            }
            sb.setCharAt(sb.length() - 1, '\n');
        }

        return sb.toString();
    }

    /**
     * File reading into a List<String>
     */
    public List<String> fileList(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        List<String> stringList = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String addLine = scanner.nextLine();
            String[] fracts = addLine.split(" ");
            if(!fracts[0].equals("#") && fracts.length > 1)
                stringList.add(addLine);
        }
        return  stringList;
    }

    /**
     * String to List<String> converter
     */
    public List<String> infoList(String info){
        List<String> listString = new ArrayList<>();

        String[] lines = info.split("\n");
        for(String s : lines){
            String [] param = s.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i<param.length; i++)
                sb.append(param[i]).append(" ");
            sb.deleteCharAt(sb.length()-1);
            listString.add(sb.toString());
        }

        return listString;
    }
}