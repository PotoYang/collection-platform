/**
 * Created by 申卓 on 2017/9/4.
 */
public class StringParse {

    public static String parseDec(String str){
        StringBuilder stringBuilder = new StringBuilder();
        String[] strArr = str.split(", ");
        for (String s : strArr) {
            int ch = 0xff & Integer.parseInt(s);
            stringBuilder.append((char) ch);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        String str = "42, 77, 71, 50, 48, 49, 54, 57, 51, 53, 48, 50, 48, 48, 48, 48, 57, 56, 53, 53, 53, 44, 65, 66, 38, 65, 49, 48, 51, 50, 52, 51, 50, 50, 52, 51, 55, 57, 50, 57, 49, 49, 52, 48, 49, 49, 48, 56, 52, 55, 48, 48, 48, 48, 48, 53, 48, 53, 49, 55, 38, 80, 48, 52, 54, 48, 48, 48, 48, 48, 48, 51, 51, 49, 53, 97, 98, 53, 38, 69, 49, 55, 48, 57, 48, 52, 49, 48, 51, 49, 48, 54, 38, 66, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 38, 87, 48, 48, 48, 48, 38, 78, 48, 55, 38, 90, 49, 50, 38, 85, 95, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 38, 84, 48, 48, 48, 49, 35";

        String string = parseDec(str);
        System.out.println(string);
    }
}
