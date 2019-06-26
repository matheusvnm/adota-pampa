import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Console {

    public static void log(String log) {
        DateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");

        String now = dateFormat.format(new Date());

        System.out.println(now +" - Server Log: "+ log);
    }
}
