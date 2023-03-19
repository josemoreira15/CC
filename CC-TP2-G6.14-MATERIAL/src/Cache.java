/**
 * Cache.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 17/12/2022
 * Last update: 02/01/2023
 */

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Cache {

    private List<String[]> cache;
    private LocalTime time;

    /**
     * Cache constructor
     */
    public Cache(LocalTime time) {
        this.cache = new ArrayList<>();
        this.time = time;
    }

    /**
     * Cache.cache getter
     */
    public List<String[]> getCache() {
        return this.cache;
    }

    /**
     * Add a line to the cache
     */
    public void addToCache(String line, String origin) {
        String[] fracts = line.split(" ");
        int flag = 0;
        String mode = fracts[3].trim();
        char test = mode.charAt(mode.length()-1);
        String index3 = (test == ',' || test == ';') ? mode.substring(0,mode.length()-1) : mode;

        if (origin.equals("OTHERS")) {
            List<Integer> indexs = findLines(fracts[0],fracts[1]);
            if (indexs.size() > 0) {
                for (Integer index : indexs) {
                    if (index < this.cache.size()){
                        String[] thisLine = this.cache.get(index);
                        if (fracts[2].equals(thisLine[2]) && index3.equals(thisLine[3])) {
                            thisLine[5] = String.valueOf(Duration.between(this.time, LocalTime.now()).getSeconds());
                            flag = 1;
                        }
                    }
                }
                if (flag == 0) {
                    String[] newList = new String[7];

                    System.arraycopy(fracts, 0, newList, 0, 3);
                    newList[3] = index3;
                    newList[4] = origin;

                    LocalTime nowTime = LocalTime.now();
                    newList[5] = String.valueOf(Duration.between(this.time, nowTime).getSeconds());

                    newList[6] = String.valueOf(this.cache.size() + 1);

                    this.cache.add(newList);
                }

            }
            else {
                String[] newList = new String[7];

                System.arraycopy(fracts, 0, newList, 0, 3);
                newList[3] = index3;
                newList[4] = origin;

                LocalTime nowTime = LocalTime.now();
                newList[5] = String.valueOf(Duration.between(this.time, nowTime).getSeconds());

                newList[6] = String.valueOf(this.cache.size() + 1);

                this.cache.add(newList);
            }
        }
        else {
            String[] newList = new String[7];

            System.arraycopy(fracts, 0, newList, 0, 3);
            newList[3] = index3;
            newList[4] = origin;

            LocalTime nowTime = LocalTime.now();
            newList[5] = String.valueOf(Duration.between(this.time, nowTime).getSeconds());

            newList[6] = String.valueOf(this.cache.size() + 1);

            this.cache.add(newList);
        }
    }

    /**
     * Create cache from database content
     */
    public void createCache(List<String> listDB, String origin){
        for(String line : listDB){
            addToCache(line,origin);
        }
    }

    /**
     * Client Root Server cache
     */
    public void rsCache(List<String> listDB){
        for(String string : listDB) {
            String[] split = string.split(" ");
            this.cache.add(split);
        }
    }

    /**
     * Find information in Root Server cache
     */
    public String findRSCache(String query) {
        String[] parameters = query.split(",");
        Converter converter = new Converter(query);
        int r_code = 2;
        String domain = parameters[6];
        if (domain.charAt(domain.length()-1) != '.')
            r_code = 3;
        else {
            String[] splitDomain = domain.split("\\.");
            if (splitDomain.length < 2 || splitDomain.length > 3)
                r_code = 3;
            else {
                for (String[] list : this.cache)
                    if (list[0].equals(splitDomain[splitDomain.length - 1])) {
                        return converter.convertFunc(1, 0, 0, 0).concat(";").concat(list[2]);
                    }
            }
        }

        return converter.convertFunc(r_code,0,0,0);
    }

    /**
     * Find a line in the cache
     */
    public int findLine(String name, String type, String index){
        int result = -1;

        List<String[]> linesToRemove = new ArrayList<>();
        for(String[] line : this.cache){
            if(line[4].equals("OTHERS")){
                //int time = (int) Duration.between(this.time,LocalTime.now()).getSeconds()*1000;
                int time = (int) Duration.between(this.time,LocalTime.now()).getSeconds();
                int compareTime = Integer.parseInt(line[3]);
                //if (time > (compareTime + Integer.parseInt(line[5])*1000))
                if (time > (compareTime + Integer.parseInt(line[5])))
                    linesToRemove.add(line);
            }
            if(Integer.parseInt(index) <= Integer.parseInt(line[6])){
                if(name.equals(line[0]) && type.equals(line[1])) {
                    result = Integer.parseInt(line[6]) - 1;
                    break;
                }
            }
        }
        for(String[] line : linesToRemove)
            this.cache.remove(line);
        return result;
    }

    /**
     * Find an amount of lines
     */
    public List<Integer> findLines(String name, String type){
        List<Integer> result = new ArrayList<>();
        String index = "1";
        int ind = 0;

        while(Integer.parseInt(index)-1 < this.cache.size() && ind != -1){
            ind = findLine(name,type,index);
            if(ind != -1)
                result.add(ind);
            index = String.valueOf(ind+2);
        }

        return result;
    }

    /**
     * Search info in a TLD server
     */
    public String searchSOASP(String query, List<String> topSv, String flag){
        String[] parameters = query.split(",");
        Converter converter = new Converter(query);
        String domain = parameters[6];
        int r_code = 2;
        if (domain.charAt(domain.length()-1) != '.')
            r_code = 3;
        else {
            String[] domainSplit = domain.split("\\.");
            if (domainSplit.length < 2 || domainSplit.length > 3)
                r_code = 3;
            else {
                int result = findLine(domainSplit[domainSplit.length - 1].concat("."), "SOASP", "1");
                if (result == -1) {
                    if (flag.equals("Q"))
                        return converter.convertFunc(1, 0, 0, 0).concat(";").concat(topSv.get(topSv.size()-1));
                }
                else {
                    String name = this.cache.get(result)[2];
                    int finalResult = findLine(name, "A", "1");
                    return converter.convertFunc(1, 0, 0, 0).concat(";").concat(this.cache.get(finalResult)[2]);
                }
            }
        }
        return converter.convertFunc(r_code, 0, 0, 0);
    }

    /**
     * Cache search
     */
    public String cacheSearch(String query, List<String> topSV, String flag) {
        String[] parameters = query.split(",");
        Converter converter = new Converter(query);

        String domain = parameters[6].trim();
        String type = parameters[7].trim();

        StringBuilder sb = new StringBuilder();
        StringBuilder xtraSB = new StringBuilder();

        int response = 0;
        int rValuess = 0;
        int aValuess = 0;
        int xValuess = 0;

        if (!type.equals("MX") && !type.equals("NS") && !type.equals("A")) {
            response = 3;
        }

        else if (domain.charAt(domain.length()-1) != '.') {
            response = 3;
        }


        else {
            List<Integer> indexs = findLines(domain, type);
            if (indexs.size() == 0) {
                for(String[] cLine : this.cache) {
                    if (cLine[1].equals("CNAME") && domain.equals(cLine[0]))
                        indexs = findLines(cLine[2], type);
                }
            }

            if (indexs.size() == 0)
                response = 2;

            else {
                for (Integer index : indexs) {
                    if (index < this.cache.size()) {
                        String[] getLine = this.cache.get(index);
                        sb.append(getLine[0]).append(" ").append(getLine[1]).append(" ")
                                .append(getLine[2]).append(" ").append(getLine[3]).append(",\n");
                        rValuess++;

                        List<Integer> indexsA = findLines(getLine[2], "A");
                        for (Integer indexA : indexsA) {
                            if (indexs.get(0) != -1) {
                                String[] getLineA = this.cache.get(indexA);
                                xtraSB.append(getLineA[0]).append(" ").append(getLineA[1]).append(" ").
                                        append(getLineA[2]).append(" ").append(getLineA[3]).append(",\n");
                                xValuess++;
                            }
                        }
                    }
                }
                if (sb.length() > 0)
                    sb.setCharAt(sb.length() - 2, ';');

                if (type.equals("NS"))
                    aValuess = rValuess;
                else {
                    List<Integer> indexsNS = findLines(domain, "NS");
                    for (Integer indexNS : indexsNS) {
                        String[] getLineNS = this.cache.get(indexNS);
                        sb.append(getLineNS[0]).append(" ").append(getLineNS[1]).append(" ")
                                .append(getLineNS[2]).append(" ").append(getLineNS[3]).append(",\n");
                        aValuess++;

                        List<Integer> indexsANS = findLines(getLineNS[2], "A");
                        if (indexsANS.size() > 0) {
                            for (Integer indexANS : indexsANS) {
                                String[] getLineANS = this.cache.get(indexANS);
                                xtraSB.append(getLineANS[0]).append(" ").append(getLineANS[1]).append(" ")
                                        .append(getLineANS[2]).append(" ").append(getLineANS[3]).append(",\n");
                                xValuess++;
                            }
                        }
                    }
                    if (sb.length() > 0)
                        sb.setCharAt(sb.length() - 2, ';');
                }
                String ans;
                if (sb.length() > 0)
                    ans = converter.convertFunc(response, rValuess, aValuess, xValuess)
                            .concat("\n").concat(sb.deleteCharAt(sb.length() - 1).toString());
                else
                    ans = converter.convertFunc(response, rValuess, aValuess, xValuess);

                if (xtraSB.length() > 0) {
                    xtraSB.setCharAt(xtraSB.length() - 2, ';');
                    xtraSB.deleteCharAt(xtraSB.length()-1);
                    return ans.concat("\n").concat(xtraSB.toString());
                }
                return ans;
            }
        }

        if(flag.equals("Q")) {
            if (this.cache.size() == 0)
                return converter.convertFunc(1, rValuess, aValuess, xValuess).concat(";").concat(topSV.get(0));
            else {
                String compareCache = this.cache.get(0)[0].split("\\.")[1].trim();
                String[] splitThis = domain.split("\\.");
                if (splitThis.length < 2 || splitThis.length > 3)
                    response = 3;
                else {
                    String compareOther = splitThis[splitThis.length - 1].trim();
                    if (!compareCache.equals(compareOther)) {
                        response = 1;
                        return converter.convertFunc(response, rValuess, aValuess, xValuess).concat(";").concat(topSV.get(0));
                    }
                }
                return converter.convertFunc(response, rValuess, aValuess, xValuess);
            }
        }
        else {
            return converter.convertFunc(response, rValuess, aValuess, xValuess);
        }
    }

    /**
     * Root Server cache search
     */
    public String SRsearch(String query, List<String> topSV, Map<String,Map<String,List<String>>> config) {
        String[] parameters = query.split(",");
        Converter converter = new Converter(query);

        String domain = parameters[6].trim();
        String search = cacheSearch(query,topSV,"Q");
        String[] splitQuery = search.split(",");
        if (!splitQuery[2].equals("1") && !(splitQuery[2].equals(splitQuery[3]))) {
            return search;
        }
        Map<String,List<String>> thisMap = config.get(domain.substring(0,domain.length()-1));
        if (thisMap != null)
            return converter.convertFunc(1,0,0,0).concat(";").concat(thisMap.get("DD").get(0));
        return cacheSearch(query,topSV,"Q");
    }

    /**
     * Print cache - debugging
     */
    public void printCache(){
        for(String[] list : this.cache)
            System.out.println(Arrays.toString(list));
    }
}