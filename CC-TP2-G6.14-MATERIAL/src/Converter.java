/**
 * Converter.java file
 * Authors : Gonçalo Pereira, José Moreira, Santiago Domingues
 * Creation date: 08/11/2022
 * Last update: 02/01/2023
 */

public class Converter {

    private String message;

    /**
     * Converter constructor
     */
    public Converter(String message){
        this.message = message;
    }

    /**
     * Query DNS message maker
     */
    public String createMessage(String arg1, String arg2) {
        StringBuilder stringToSv = new StringBuilder();
        int random = (int) (Math.random() * (65535 - 1)) + 1;

        stringToSv.append(random).append(",").append("Q").append(",")
                .append(0).append(",").append(0).append(",").append(0).append(",").append(0).append(",")
                .append(arg1).append(",").append(arg2);

        return stringToSv.toString();
    }

    /**
     * Query result DNS message maker
     */
    public String convertFunc(int r_code, int n_val, int n_aut, int x_val) {
        String[] decomp = this.message.split(",");

        String sb = decomp[0] + "," + "A" + "," +
                r_code + "," + n_val + "," +
                n_aut + "," + x_val + "," +
                decomp[6] + "," + decomp[7];
        return sb.trim();
    }
}