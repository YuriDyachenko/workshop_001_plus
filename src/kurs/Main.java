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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        String userName = "Юрий";

        int hour = hourOfDay();
        //можно сразу выйти, потому что ошибка уже выведена на "своих местах"
        if (hour == -1) return;

        String hello = hour > 17 ? "Добрый вечер" : hour > 10 ? "Добрый день" :
                hour > 5 ? "Доброе утро" : "Доброй ночи";
        System.out.printf("%s, %s!\n", hello, userName);
    }

    private static int hourOfDay() {
        String text = getDateStringFromInternet();
        if (Err.wasError(Err.PRINT_FULL)) return -1;

        Long unixTime = extractUnixTimeFromJSON(text, "unixtime");
        if (Err.wasError(Err.PRINT_FULL)) return -1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private static String getDateStringFromInternet(){
        Err.clear();

        URL url;
        try {
            url = new URL("http://worldtimeapi.org/api/Etc/GMT-3");
        } catch (MalformedURLException e) {
            Err.set(e);
            return null;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Err.set(e);
            return null;
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Err.set(e);
            return null;
        }

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception e) {
            Err.set(e);
            return "";
        }
    }

    public static Long extractUnixTimeFromJSON(String textJSON, String name){
        Err.clear();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(textJSON);
            Long res = (Long) jsonObject.get(name);
            if (res == null) Err.set("Нет такого поля: " + name);
            return res;
        } catch (ParseException e) {
            Err.set(e);
            return null;
        }
    }

}
