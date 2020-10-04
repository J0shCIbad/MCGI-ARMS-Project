import java.util.HashMap;
import java.io.File;
import java.io.IOException;
/**
 * Write a description of class InternalFilesHandler here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class InternalFilesHandler
{
    private HashMap<String, HashMap<String, TsvIO>> ordner;
    private String[] directories = {"res", "usr", "sched", "files"};
    public InternalFilesHandler(){
        ordner = new HashMap<String, HashMap<String, TsvIO>>();
        try{
            initDirectories();
        }catch(Exception e){}
    }
    private void initDirectories() throws IOException{
        for(String str: directories){
            File dir = new File(str);
            dir.createNewFile();
            HashMap<String, TsvIO> tmpMap = new HashMap<String, TsvIO>();
            for(File f : dir.listFiles()){
                tmpMap.put(f.getName(), new TsvIO(dir.getName() + File.separator + f.getName(),
                    "skel" + File.separator + dir.getName() + ".tsv"));
            }
            ordner.put(str, tmpMap);
        }
    }
    public TsvIO get(String dir, String file){
        HashMap<String, TsvIO> tmp1 = ordner.get(dir);
        if(tmp1 == null) return null;
        return tmp1.get(file);
    }
}
