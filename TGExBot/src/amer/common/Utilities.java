/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amer.common;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author S53788
 */
public class Utilities {

    private static final String[] type = {"M", "E", "W", "D"};
   
    public static long dateToTS(String date_to_conv, String format) {

        try {
            DateFormat formatter;
            Date date;
            formatter = new SimpleDateFormat(format);
            date = (Date) formatter.parse(date_to_conv);

            return (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(22);
        }

        return 0;
    }

    public static String tsToDate(long timestamp, String date_format) {
        Date d = new Date(timestamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);

        return sdf.format(d);
    }

    public static String getCurrentDateTime(String date_format) {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);

        return sdf.format(d);
    }

    /**
     * Print the line to an output window
     *
     * @param line
     * @param mode <br />
     * 0 = Message<br />
     * 1 = Error<br />
     */
    public static void log(String line, int mode) {

        String ts = type[mode] + " [" + getCurrentDateTime("yyyy/MM/dd HH:mm:ss") + "] ";

        System.out.println(ts + line);
    }

    public static void logStack(Exception e) {

        e.printStackTrace();
    }
    
    public static boolean moveFile(File originalFile, String destinationFolder){
      
      if(!destinationFolder.endsWith(constant.FILE_SEPARATOR)){
        destinationFolder += constant.FILE_SEPARATOR;
      }
      
      String destinationFile = destinationFolder + originalFile.getName();
      
      return originalFile.renameTo(new File(destinationFile));
      
    }

    public static String rightPad(String text_to_pad, int max_length, String pad_with) {

        if (text_to_pad.length() >= max_length) {
            return text_to_pad;
        }

        int remaning_space = max_length - text_to_pad.length();

        String output = text_to_pad;

        for (int i = 0; i < remaning_space; i++) {
            output += pad_with;
        }

        return output;

    }

    public static String leftPad(String text_to_pad, int max_length, String pad_with) {
        if (text_to_pad.length() >= max_length) {
            return text_to_pad;
        }

        int remaning_space = max_length - text_to_pad.length();

        String output = text_to_pad;

        for (int i = 0; i < remaning_space; i++) {
            output = pad_with + output;

        }

        return output;

    }

    /**
     *
     * @param mode either R or D
     * @return
     */
    public static String getCurrentPartition(String mode) {

        long curr = System.currentTimeMillis() / 1000;
        long exact = curr - curr % 86400;

        String cMon = tsToDate(exact, "MM");
        String cYear = tsToDate(exact, "yyyy");
        int mon = Integer.parseInt(cMon) + 1;
        int yearoffset = 0;

        if (mon > 12) {
            yearoffset = mon / 12;
            mon -= 12;
        }

        int year = Integer.parseInt(cYear) + yearoffset;

        long nuts = dateToTS(year + leftPad("" + mon, 2, "0") + "01", "yyyyMMdd");
        nuts -= 86400;

        cMon = tsToDate(nuts, "MMddyyyy");

        return "P_" + mode + "_" + cMon;
    }

    public static String getPreviousPartition(String mode) {

        long curr = System.currentTimeMillis() / 1000;
        long exact = curr - curr % 86400;

        String cMon = tsToDate(exact, "MM");
        String cYear = tsToDate(exact, "yyyy");
        int mon = Integer.parseInt(cMon);
        int year = Integer.parseInt(cYear);

        long nuts = dateToTS(year + leftPad("" + mon, 2, "0") + "01", "yyyyMMdd");
        nuts -= 86400;

        cMon = tsToDate(nuts, "MMddyyyy");

        return "P_" + mode + "_" + cMon;
    }
    
    public static String getYesterdayPartition(String mode) {

        long curr = System.currentTimeMillis() / 1000;
        long exact = curr - curr % 86400 - 86400;

        String cMon = tsToDate(exact, "MM");
        String cYear = tsToDate(exact, "yyyy");
        int mon = Integer.parseInt(cMon) + 1;
        int yearoffset = 0;

        if (mon > 12) {
            yearoffset = mon / 12;
            mon -= 12;
        }

        int year = Integer.parseInt(cYear) + yearoffset;

        long nuts = dateToTS(year + leftPad("" + mon, 2, "0") + "01", "yyyyMMdd");
        nuts -= 86400;

        cMon = tsToDate(nuts, "MMddyyyy");

        return "P_" + mode + "_" + cMon;
    }
    
    public static String getCurrentStgPartition() {

        long curr = System.currentTimeMillis() / 1000;
        long exact = curr - curr % 86400;

        String cMon = tsToDate(exact, "MMM");
        String cYear = tsToDate(exact, "yyyy");
        int yearoffset = 0;
        String stgP;
         
        int year = Integer.parseInt(cYear) + yearoffset;
        //P_2018_FEB
        stgP = "P_" + cYear + "_" + cMon;
        //return sgtP;
        return stgP;
    }

        public static String getPreviousStgPartition() {

        long curr = System.currentTimeMillis() / 1000;
        long exact = curr - curr % 86400;

        String cMon = tsToDate(exact, "MMM");
        String cYear = tsToDate(exact, "yyyy");
        int yearoffset = 0;
        String stgPr;

        int year = Integer.parseInt(cYear) + yearoffset;
        //P_2018_FEB
        stgPr = "P_" + cYear + "_" + cMon;
        //return sgtP;
        return stgPr;
    }
}
