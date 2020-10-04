import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
/**
 * Write a description of class Interpreter here.
 *
 * @author:     Josh Ibad
 * @contact:    joshcibad@gmail.com
 * @version:    03-Mar-2020     1100
 */
public class Interpreter
{
    private InternalFilesHandler ordner;
    private final static int BUFFER_SIZE = 1024;
    
    public Interpreter(){
        ordner = new InternalFilesHandler();
    }
    
    public int interpret(String msg, ObjectOutputStream out){
        System.out.println("[Server]\tMessage Received: " + msg);
        int i = msg.indexOf("\t");
        String buffer = msg.substring(0, (i== -1) ? msg.length() : i).toLowerCase();
        try{msg = msg.substring(i+1, msg.length());}catch(Exception e){}
        switch(buffer){
            case "download": case "pull": 
                return upload(msg, out);
            case "upload": case "push":
                return download(msg, out);
            case "exit": return 0;
            default:
                return -1;
        }
    }
    
    private int upload(String msg, ObjectOutputStream out){
        try{
            int j = msg.indexOf("\t"), k= msg.indexOf("\t", j+1);
            FileInputStream in = new FileInputStream(
                new File(msg.substring(0, j), msg.substring(j+1, (k== -1) ? msg.length() : k)));
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while((len = in.read(buffer)) != -1){
                out.write(buffer, 0, len);
                for(byte b: buffer) {System.out.print((char) b);}
            }
            in.close();
            out.close();
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    
    private int download(String msg, ObjectOutputStream out){
        try{
            int j = msg.indexOf("\t"), k= msg.indexOf("\t", j+1);
            File dir = new File(msg.substring(0, j));
            if(!dir.exists())   {dir.mkdirs();}
            File file = new File(dir, msg.substring(j+1, (k== -1) ? msg.length() : k));
            System.out.println("'"+file.getAbsolutePath()+"'");
            file.createNewFile();
            msg = msg.substring(k+1);
            
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(msg.getBytes());
            
            fout.close();
            out.close();
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
