package kurs;

//В задаче №1 мы получаем текущее время, указанное на компьютере. Это не так интересно, как получать его с
//какого-нибудь стороннего сервиса. В этой задаче предлагаю выбрать любой сервис времени, API которого
//вам нравится (например http://worldtimeapi.org/). Следует написать GET-запрос, используя HTTP-библиотеку
//вашего языка (например, requests для Python или Apache HTTP Client для Java) и получить текущее время
//с учетом вашего UTC.
//В остальном задача выглядит аналогично. Надо написать программу, которая приветствует Вас следующим образом:
//C 00 часов до 04 часов включительно программа при запуске пишет - "Доброй ночи, {username}"
//С 05 часов до 09 часов включительно программа при запуске пишет - "Доброе утро, {username}"
//С 10 часов до 16 часов включительно программа при запуске пишет - "Добрый день, {username}"
//С 17 часов до 23 часов включительно программа при запуске пишет - "Добрый вечер, {username}"
//Само собой, {username} должен заменяться на Ваше имя.

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) {

        String userName = "Юрий";
        int hour = hourOfDay();
        if (hour == -1) {
            System.out.println("Ошибка при получении hourOfDay()...");
            return;
        }
        String hello = hour > 17 ? "Добрый вечер" : hour > 10 ? "Добрый день" :
                hour > 5 ? "Доброе утро" : "Доброй ночи";
        System.out.printf("%s, %s!\n", hello, userName);

    }

    private static int hourOfDay() {
        String text;
        try {
            text = getDateStringFromInternet();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        Long unixTime;
        try {
            unixTime = extractUnixTimeFromJSON(text, "unixtime");
            if (unixTime == null || unixTime == 0) return -1;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private static String getDateStringFromInternet() throws IOException {
        final URL url = new URL("http://worldtimeapi.org/api/timezone/Etc/GMT-3");
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(500);
        con.setReadTimeout(500);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static Long extractUnixTimeFromJSON(String textJSON, String name) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(textJSON);
        return (Long) jsonObject.get(name);
    }

}
