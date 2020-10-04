import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
/**
 * $$$DOCUMENT
 * Basic I/O class for plaintext TSV files.
 * 
 * Specifications:
 * No tabs and newlines in fields, fields are tab delimited
 * 
 * @author:     Josh Ibad
 * @contact:    joshcibad@gmail.com
 * 
 * @Version:    01-Mar-2020     19:15
 */
public class TsvIO
{
    private static long tempFileCounter;
    private File file;
    private Scanner in;
    private int height=0, width=0;
    private String[] fieldHeaders;
    private int currRow, currCol;
    private ArrayList<String[]> tsv;
    public TsvIO(String filename, String skeleton){
        try{
            file = new File(filename);
            if(!fileCheck()){/*Terminate gracefully*/}

            tsv = new ArrayList<String[]>();
            in = new Scanner(file);
            String buffer = "";
            if(in.hasNext()){ 
                while(in.hasNext()) {tsv.add( split(in.nextLine(), "\t") );}
            }else{ //Empty file
                if(skeleton == null){//Default preset
                    String[] strArrTmp = {"", "ID NO", "NAME"};
                    tsv.add(strArrTmp);
                }else{//copy skeleton
                    in.close();
                    absoluteCopy(new File(skeleton), file);
                    in = new Scanner(file);
                    while(in.hasNext()) {tsv.add( split(in.nextLine(), "\t") );}
                }
            }
            in.close();
        }catch(IOException e){
            System.out.println(e.getStackTrace());
            try{in.close();}catch(Exception e2){}
        }
    }
    public TsvIO(String filename){this(filename, null);}
    
    /**
     *      $$ Work in Progress $$
     */
    private boolean fileCheck() throws IOException{
        boolean retVal = true;
        file.createNewFile();
        if(!file.isFile()) {return false;}
        /** Run other checks **/
        return retVal;
    }

    public void close(){//
        try{
            save();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /** Main Operation methods **/
        /**Input operations**/
    /**
     * Read field defined by column and row in
     * human format.
     * 
     * @param   col - Column of field (C-like index)
     *          row - Row of field (C-like index)
     * @ret     Human format field value
     */
    public String readField(int col, int row, boolean format){
        return (format) ? toTsvField(tsv.get(row)[col]) : tsv.get(row)[col];
    }
    public String readField(String str_col, int row, boolean format){//document
        int col = fieldHeaderToCol(str_col);
        if( col == -1) { addCols(1); col = width()-1; tsv.get(0)[col] = str_col;}
        String strTmp = tsv.get(row)[col];
        return (format) ? toTsvField(strTmp) : strTmp;
    }
    public String readField(int col, int row){return readField(col,row,false);}//document
    public String readField(String str_col, int row) {return readField(str_col,row,false);}//document
    
        /**Output operations**/
    /**
     * Replaces field defined by column and row
     * with input string
     * 
     * @param   str - Human format input string
     *          col - Column of field (C-like index)
     *          row - Row of field (C-like index)
     *          format - True if input should be formatted
     */
    public void replaceField(String str, int col, int row, boolean format){//document
        tsv.get(row)[col] = (format) ? toTsvField(str) : str;
    }
    public void replaceField(String str, int col, int row){replaceField(str,col,row,true);}//document
    public void replaceField(String str, String str_col, int row, boolean format){//document
        tsv.get(row)[ fieldHeaderToCol(str_col) ] = (format) ? toTsvField(str) : str;
    }
    public void replaceField(String str, String str_col, int row){replaceField(str,str_col,row,true);}//document
    
    /**
     * Performs an absolute copy of the target file to the destination file
     * @param   target  - target file to be copied
     *          destination - destination file to copy to
     */
    public static void absoluteCopy(File target, File destination){
        try{
            java.nio.file.Files.copy(target.toPath(), destination.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){System.out.println(e);}
    }
    
    /** Helper methods **/
    /**
     * Converts a human format input string to a csv format
     * field string. The definition used here is to be followed
     * throughout the program.
     * 
     * @param   str - Human format input string
     * @ret     Csv format field string
     */
    public static String toTsvField(String str){
        String buffer = "";
        for(char c: str.toCharArray()){
            switch(c){
                case '\n':  case '\r':  case '\t':
                case '"':   break;
                default:    buffer += c;
                            break;
            }
        }
        return buffer;
    }
    /**
     * Converts a field header string to the respective
     * column (C-like indexing).
     * @param   str - Field header string
     * @ret     index or column of field header
     *          -1 if not found
     */
    public int fieldHeaderToCol(String str){
        return ArraysExt.linearSearch(tsv.get(0), str);
    }
    /**
     * Splits string by key value
     * @param   str - String to be split
     *          key - key string to split by
     * @ret     String array of all substrings obtained from splitting str by key.
     */
    public static String[] split(String str, String key){
        if(str == null || str.isEmpty()){String[] retVal = {""}; return retVal;}
        ArrayList<String> list = new ArrayList<String>();
        int i=0, j=str.indexOf(key);
        while(j != -1){
            list.add(str.substring(i, j));
            i=j+1;
            j=str.indexOf(key, i);
        }
        try{list.add(str.substring(i));}catch(Exception e){}
        
        String[] retVal = new String[list.size()];
        for(i=0; i<retVal.length; i++) {retVal[i] = list.get(i);}
        return retVal;
    }
    
        /** Miscelaneous row operations */
    /**
     * Inserts blank row at the specified index
     * @param   row - Row at which to insert new row, pushing other rows forward;
     */
    public void insertRow(int row){
        String[] buffer = new String[width()];
        for(int i=1; i<width(); i++) {buffer[i] = "";}
        tsv.add(row, buffer);
        for(int i=row+1; i<height(); i++){
            tsv.get(i)[0] = ""+i;
        }
    }
    /**
     * Removes row at specified index and returns removed row
     * @param   row - Row index to be removed
     * @ret     Row removed in tab-parsed String array form. null if invalid row index.
     */
    public String[] removeRow(int row){
        if(row < 0 || row >= height()) {return null;} 
        String[] strTmpArr = tsv.remove(row);
        for(int i=row; i<height(); i++){
            tsv.get(i)[0] = ""+i;
        }
        return strTmpArr;
    }
    /**
     * Swaps two rows specified by indices
     * @param   oldRow - First row index to be swapped
     *          newRow - Second row index to swap with
     */
    public void swapRow(int oldRow, int newRow){
        if(oldRow <0 || newRow < 0 || oldRow >= height() || newRow >= height()) {return;}
        String[] strArrTmp = tsv.get(oldRow);
        tsv.set(oldRow, tsv.get(newRow));   tsv.get(oldRow)[0] = ""+oldRow;
        tsv.set(newRow, strArrTmp); tsv.get(newRow)[0] = ""+newRow;
    }
    /**
     * Pushes specified row to the end
     * @param   row - Index of row to be pushed back
     */
    public void pushbackRow(int row){
        if(row <0 || row >= height()) {return;}
        String[] strTmpArr = removeRow(row);
        strTmpArr[0] = ""+height();
        tsv.add(strTmpArr);
    }
    /**
     * Add a set number of columns to the dataset
     * @param   numCols - number of columns to add
     */
    public void addCols(int numCols) { expand(numCols, 0); }
    /**
     * Add a set number of rows to the dataset
     * @param   numRows - number of rows to add
     */
    public void addRows(int numRows) { expand(0, numRows); }
    /**
     * Add a set number of columns and rows to the dataset
     * @param   numCols - number of columns to add
     *          numRows - number of rows to add
     */
    public void expand(int numCols, int numRows){
        if(numCols < 0 || numRows < 0) {return;}
        int i;
        if(numCols > 0){
            for(i=0; i<height(); i++){
                tsv.set(i, ArraysExt.expand(tsv.get(i), numCols));
            }
        }
        for(i=height(); i<numRows; i++){
            String[] strTmpArr = new String[width()];
            for(int j=0; j<width(); j++) {strTmpArr[j] = "";}
            tsv.add(strTmpArr);
        }
    }
    /**
     * Prints the outputs of the tsv file in memory into safe nonvolatile storage
     */
    public void save() {
        try{
            PrintWriter out = new PrintWriter(new FileWriter(file));
            String buffer;
            int i;
            for(int j=0; j<tsv.size(); j++){
                String[] tsvRow = tsv.get(j);
                buffer = "";
                for(i=0; i<tsvRow.length-1; i++){
                    buffer += tsvRow[i] + "\t";
                }
                buffer += tsvRow[tsvRow.length-1] + "\n";
                out.print(buffer);
            }
            out.close();
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
    }
    public void printAll(){
        System.out.println("Printing: "+file.getName());
        try{
            String buffer;
            int i;
            for(int j=0; j<tsv.size(); j++){
                String[] tsvRow = tsv.get(j);
                buffer = "";
                for(i=0; i<tsvRow.length-1; i++){
                    buffer += tsvRow[i] + "\t";
                }
                buffer += tsvRow[tsvRow.length-1] + "\n";
                System.out.print(buffer);
            }
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
    }
    
        /** Accessor methods */
    /**
     * Returns the height or number of rows in the tsv file.
     * @ret     Number of rows in tsv file
     */
    public int height()  {return tsv.size();}
    /**
     * Returns the width or number of columns in the tsv file.
     * @ret     Number of columns in tsv file
     */
    public int width()   {return tsv.get(0).length;}
    
    /**
     * Returns an array of all field headers of tsv file.
     * @ret    String array of all field headers of tsv file.
     */
    public String[] fieldHeaders()   {return tsv.get(0);}
    
    /**
     * Returns the specified row in string array form
     * @param   row - Index of row to be returned
     * @ret     Row in string array form. null if invalid row inputted.
     */
    public String[] row(int row){
        if(row < 0 || row >= height()) {return null;}
        return tsv.get(row);
    }
    
    /**
     * Returns the specified column in string array form
     * @param   col - Index of column to be returned
     * @ret     Column in string array form. null if invalid row inputted.
     */
    public String[] column(int col){
        String[] retVal = new String[height()];
        for(int i=0; i<retVal.length; i++){
            retVal[i] = tsv.get(i)[col];
        }
        return retVal;
    }
    
    public String path(){return file.getAbsolutePath();}
    public String filename(){return file.getName();}
}
