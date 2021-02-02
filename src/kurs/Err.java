package kurs;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Err {
    public static final int PRINT_NONE = 0;
    public static final int PRINT_MAIN = 1;
    public static final int PRINT_FULL = 2;

    //попробую внутри сделать создаваемый один раз объект
    //чтобы можно было к нему обращаться, когда лень создавать свои
    private static final Err last = new Err();

    private boolean er = false;
    private String msg = null;
    private Exception e = null;

    /*
    стандартная проверка, есть ли ошибка
    */
    public boolean hasError() {
        return er;
    }

    /*
    проверка последней ошибки
    */
    public static boolean wasError() {
        return last.hasError();
    }

    /*
    проверка последней ошибки, а заодно и печать о ней, если задано
    */
    public static boolean wasError(int needPrint) {
        boolean res = last.hasError();
        if (needPrint == PRINT_MAIN || needPrint == PRINT_FULL) last.printMsg();
        if (needPrint == PRINT_FULL) last.printStackTrace();
        return res;
    }

    /*
    остальные методы предлагается вызывать уже из полученного
    этим методом объекта
    */
    public static Err getLast() {
        return last;
    }

    /*
    единый внутренний метод очистки
    */
    private void clearInner() {
        er = false;
        msg = null;
        e = null;
        //очистка любого объекта очищает и last
        //но только если это не он сам, а то будет бесконечная рекурсия
        if (this != last)
            clear(last);
    }

    /*
    единый внутренний метод, через который идет установка любой ошибки
    */
    private void setInner(String msg, Exception e) {
        er = true;
        if (msg == null && e != null) {
            this.msg = e.getMessage();
            //здесь тоже может так случиться, что запишется null
            if (this.msg == null) {
                //можно попробовать хоть что-то записать
                //вот только забыл, как такое смоделировать, но вроде сталкивался где-то
                this.msg = e.toString();
            }
        } else
            if (msg != null)
                this.msg = msg;
        if (e != null)
            this.e = e;
        //установка любой ошибки заполняет и last
        //но только если это не он сам, а то будет бесконечная рекурсия
        if (this != last)
            set(last, msg, e);
    }

    /*
    сокращенный внутренний метод, вызывает единый
    */
    private void setInner(String msg) {
        setInner(msg, null);
    }

    /*
    возвращает пользовательское сообщение об ошибке
    */
    public String getMsg() {
        if (msg == null) return "";
        return msg;
    }

    /*
    возвращает сообщение об ошибке из поля e
    */
    public String getDetailMsg() {
        if (e == null) return "";
        String s = e.getMessage();
        if (s == null || s.isEmpty()) return "";
        return s;
    }

    /*
    возвращает трассировку из поля e
    */
    public String getStackTrace() {
        if (e == null) return "";
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /*
    печатает трассировку из поля e
    */
    public void printStackTrace() {
        String s = getStackTrace();
        if (s == null || s.isEmpty()) return;
        System.out.println(s);
    }

    /*
    печатает пользовательское сообщение об ошибке
    */
    public void printMsg() {
        String s = getMsg();
        if (s == null || s.isEmpty()) return;
        System.out.println(s);
    }

    /*
    печатает сообщение об ошибке из поля e
    */
    public void printDetailMsg() {
        String s = getDetailMsg();
        if (s == null || s.isEmpty()) return;
        System.out.println(s);
    }

    /*
    статический метод чистит переданную ошибку с проверкой на нулл
    */
    public static void clear(Err err) {
        if (err == null) return;
        err.clearInner();
    }

    /*
    статический метод без параметров чистит именно последнюю ошибку
    */
    public static void clear() {
        last.clearInner();
    }

    /*
    статический метод без параметров назначает именно последнюю ошибку
    */
    public static void set(String msg, Exception e) {
        last.setInner(msg, e);
    }

    /*
    сокращенный статический метод без параметров назначает именно последнюю ошибку
    */
    public static void set(String msg) {
        set(msg, null);
    }

    /*
    статический самый кородкий метод
    */
    public static void set(Exception e) {
        last.setInner(null, e);
    }

    /*
    статический метод назначает именно ошибку
    */
    public static void set(Err err, String msg, Exception e) {
        if (err == null)  return;
        err.setInner(msg, e);
    }

    /*
    сокращенный статический метод назначает именно ошибку
    */
    public static void set(Err err, String msg) {
        set(err, msg, null);
    }

}
