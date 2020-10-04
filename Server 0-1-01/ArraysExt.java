/**
 * Helper class with static methods for array manipulation. Cases handled are only those
 * within this Attendance project. An extent of ordering is expected of arrays, and only
 * String arrays are considered.
 * 
 * Not all methods are efficient. Please optimize in the future, when the full use case
 * is discovered.
 *
 * @author:     Josh Ibad
 * @contact:    joshcibad@gmail.com
 * @Version:    24-Feb-2020     0745
 */
public class ArraysExt
{
    /**
     * Appends a String element to an array.
     * 
     * @param   old - Old string array
     *          str - String element to append
     * @ret     Appended array
     */
    public static String[] append(String[] old, String str){
        String[] retVal = new String[old.length+1];
        int i=0;
        while(i<old.length && old[i].compareTo(str) < 0){ retVal[i] = old[i]; i++;}
        retVal[i] = str;
        while(i < old.length){retVal[i+1] = old[i]; i++;}
        return retVal;
    }
    
    /**
     * Appends a String element to an array, sorting by a specified key column.
     * 
     * @param   old - Old string array
     *          str - String element to append
     *          keyCol  - column to sort by
     * @ret     Appended array
     */
    public static String[] append(String[] old, String str, int keyCol){
        String[] retVal = new String[old.length+1];
        int i=0;
        while(i<old.length && old[i].split(" ")[keyCol].compareTo(str.split(" ")[keyCol]) < 0){
            retVal[i] = old[i]; i++;
        }
        retVal[i] = str;
        while(i < old.length){retVal[i+1] = old[i]; i++;}
        return retVal;
    }
    
    /**
     * Remove a String element from an array.
     * 
     * @param   old - Old string array
     *          str - String element to remove
     * @ret     Element-removed array
     */
    public static String[] remove(String[] old, String str){
        String[] retVal = new String[old.length-1];
        int j=0;
        for(String strTmp: old){
            if(!strTmp.equals(str)){
                retVal[j] = strTmp;
                j++;
            }
        }
        return retVal;
    }
    
    /**
     * Checks if an array contains a String.
     * 
     * @param   arr - String array to check
     *          key - String to check for
     * @ret     true if array contains string, false otherwise
     */
    public static boolean arrayContains(String[] arr, String key){
        for(String strTmp:  arr){
            if(strTmp.equals(key)) {return true;}
        }
        return false;
    }
    
    /**
     * Expands inputted string to contain additional specified number of elements
     * @param   arr - Array to be duplicated and expanded
     *          numCols - number of elements by which to expand the array
     * @ret     Duplicate string array of arr with additional numCols elements.
     */
    public static String[] expand(String[] arr, int numCols){
        String[] retVal = new String[arr.length+numCols];
        int i;
        for(i=0; i<arr.length; i++) {retVal[i] = arr[i];}
        for(i=arr.length; i<retVal.length; i++) {retVal[i] = "";}
        return retVal;
    }
    
    /**
     * Performs a linear search for specified key, with exact match rules
     * @param   arr - String array to search through for key
     *          key - String to search for
     * @ret     Index of key in arr, -1 if not found
     */
    public static int linearSearch(String[] arr, String key){
        for(int i=0; i<arr.length; i++) {
            if(arr[i].equals(key)) { return i; }
        }
        return -1;
    }
}