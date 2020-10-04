import java.util.Calendar;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Helper class with static methods for String formatting and conversions with regards
 * to Church Gathering information, time and date, etc.
 * 
 * Not all inputs were considered, neither default outputs and algorithm efficiency.
 * Please add as ideas (and problems) arise.
 *
 * @author:     Josh Ibad
 * @contact:    joshcibad@gmail.com
 * @Version:    24-Feb-2020     1400
 */
public class Gatherings
{
    /**
     * Converts a String of the day of week to int.
     * 
     * @param   str - Day of week
     * @ret     Integer corresponding to day of week with 1 corresponding to Sunday
     *          and 7 to Saturday, etc.
     */
    public static int dayOfWeekToInt(String str){
        str = str.toLowerCase();
        String[] arr = {"sunday", "monday", "tuesday", "wednesday", "thursday",
            "friday", "saturday", "1", "2", "3", "4", "5", "6", "7"};
        int i;
        for(i=0; i<arr.length; i++){
            if(arr[i].startsWith(str)){
                break;
            }
        }
        return (i%7)+1;
    }
    
    /**
     * Converts an int of the day of week with 1 corresponding to Sunday and 7 to Saturday,
     * etc., to a 3-4 character ALL CAPS abbreviation string.
     * 
     * @param   numDOW - Integer corresponding to day of week, with 1 corresponding to
     *          Sunday and 7 to Saturday, etc.
     * @ret     3-4 character ALL CAPS abbreviation string of day of week
     *          (SUN, MON, TUE, WED, THUR, FRI, SAT)
     */
    public static String intToDayOfWeek(int numDOW){
        switch(numDOW){
            case 1: return "SUN";
            case 2: return "MON";
            case 3: return "TUE";
            case 4: return "WED";
            case 5: return "THUR";
            case 6: return "FRI";
            case 7: return "SAT";
            default: return ""+numDOW;
        }
    }
    
    /**
     * Converts an int of the day of week with 1 corresponding to Sunday and 7 to Saturday,
     * etc., to a 3-4 character ALL CAPS abbreviation string.
     * 
     * @param   numDOW - String of integer corresponding to day of week, with
     *          1 corresponding to Sunday and 7 to Saturday, etc.
     * @ret     3-4 character ALL CAPS abbreviation string of day of week
     *          (SUN, MON, TUE, WED, THUR, FRI, SAT). If no valid, day of week string is
     *          found, what was passed will be returned.
     */
    public static String intToDayOfWeek(String numDOW){
        try{
            return intToDayOfWeek(Integer.parseInt(numDOW));
        }catch(Exception e){return numDOW;}
    }
    
    /** Documentation stopped here, pick up later **/
    public static String toMilitaryTime(String time){
        String buffer = "";
        for(char c: time.toLowerCase().toCharArray()){
            switch(c){
                case '.': case ' ': case '\t': case '\n': 
                    break;
                default:
                    buffer += c;
                    break;
            }
        }
        int ind = buffer.indexOf(":");
        if(ind == -1) {return time;}
        
        try{
            String hour = buffer.substring(0, ind);
            String minute = buffer.substring(ind+1, ind+3);
            String period = buffer.substring(buffer.length()-2);
            
            int numHour = Integer.parseInt(hour);
            if(Integer.parseInt(minute)/60 > 0) {return time;}
            if(period.equals("pm")){
                if(numHour != 12){
                    numHour += 12;
                }
            }else if(period.equals("am")){
                if(numHour == 12){
                    numHour = 0;
                }
            }else{return time;}
                
            return String.format("%02d", numHour) + minute;
        }catch(Exception e) {return time;}
    }
    public static String toRegularTime(String time){
        try{
            int milTime = Integer.parseInt(time);
            boolean morning = (milTime/1200 == 0);
            String buffer = "" + (  (morning) ? milTime : milTime - 1200 );
            while(buffer.length() < 4){ buffer = "0" + buffer;}
            buffer = buffer.substring(0,2) + ":" + buffer.substring(2, 4) + " ";
            buffer += (morning) ? "A.M." : "P.M.";
            return buffer;
        }catch(Exception e){return time;}
    }
    public static String gatheringFromInit(String init){
        switch(init.toLowerCase().replace(" ", "")){
            case "pm": return "Prayer Meeting";
            case "tg": return "Thanksgiving";
            case "ws": return "Worship";
            /* Add SPM, SPBB, Doctrination, etc. */
            default: return init;
        }
    }
    public static String gatheringToInit(String str){
        switch(str.toLowerCase()){
            case "pm": case "spm": case "prayer meeting": case "special prayer meeting":
                return "PM";
            case "tg": case "pbb": case "thanksgiving": case "pasalamat":
            case "pasalamat ng buong bayan":
                return "TG";
            case "ws": case "worship":
                return "WS";
            /* Add SPBB, Doctrination, etc */
            default: return str;
        }
    }
    public static int closestSched(String[][] sched){
        try{
            int ind = 0;
            int now = 2400*Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                + 100*Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            for(int i=0; i<2400; i+=100){
                for(; ind<sched.length-1; ind++){
                    int val = 2400*Integer.parseInt(sched[ind][0])
                        + Integer.parseInt(sched[ind][1]);
                    int EPSILON_L = 100;
                    int EPSILON_R = (sched[ind][2].equals("TG")) ? 800 : 200;
                    if( val - EPSILON_L - i < now && val + EPSILON_R + i > now )
                        return ind;
                }
                ind = 0;
            }
            return 0;
        }catch(Exception e){return 0;}
    }
    public static String filenameToLocale(String str, String defaultStr){
        String strTmp = "";
        try{
            for(String strTmp3: str.split("_")){
                strTmp += strTmp3.substring(0,1).toUpperCase() + strTmp3.substring(1) + " "; 
            }
            strTmp = strTmp.substring(0,strTmp.length()-1);
        }catch(Exception e2){
            strTmp = defaultStr;
        }
        return strTmp;
    }
    public static String localeToInit(String locale){
        /**Replace with dynamic file reading in the future **/
        LocaleAbbvHandler obj = new LocaleAbbvHandler("res" + File.separator + "na-locales.tsv");
        String str = obj.get(locale);
        if(str == null) {return locale;} else {return str;}
    }
    
    /**
     *  $$$Document
     * Write a description of class LocaleAbbvHandler here.
     *
     * @author (your name)
     * @version (a version number or a date)
     */
    public static class LocaleAbbvHandler
    {
        private HashMap<String, String> map;
        public LocaleAbbvHandler(String filename){
            map = new HashMap<String, String>();
            try{
                Scanner io = new Scanner(new File(filename));
                ArrayList<String> li;
                while(io.hasNext()){
                    li = split(io.nextLine());
                    for(int i=1; i<li.size(); i++){
                        map.put(li.get(i).toUpperCase(), li.get(1).toUpperCase());
                    }
                }
                io.close();
            }catch(Exception e){
                
            }
        }
        public LocaleAbbvHandler(){
            this("res"+File.separator+"na-locales.tsv");
        }
        
        public String get(String key) {return map.get(key.toUpperCase());}
        
        /**
         * Splits string by key value
         * @param   str - String to be split
         *          key - key string to split by
         * @ret     String array of all substrings obtained from splitting str by key.
         */
        private ArrayList<String> split(String str){
            if(str == null || str.isEmpty()){return null;}
            ArrayList<String> list = new ArrayList<String>();
            int i=0, j=str.indexOf("\t");
            while(j != -1){
                list.add(str.substring(i, j));
                i=j+1;  j=str.indexOf("\t", i);
            }
            try{list.add(str.substring(i));}catch(Exception e){}
            return list;
        }
    }
}