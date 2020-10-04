import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Write a description of class MainServerHandler here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MainServerHandler
{
    private static ServerSocket server;
    private int port;
    private Interpreter interpreter;
    private boolean runFlag; //true while should run
    
    public static void main(String args[]) throws IOException, ClassNotFoundException{
        MainServerHandler server = new MainServerHandler();
        server.run();
    }
    
    public MainServerHandler(){
        try{
            loadConfig();
            server = new ServerSocket(port, 5);
                //, 5, InetAddress.getByName("attkeep.zapto.org"));
            System.out.println("Server started listening at: " +
                server.getInetAddress().getHostAddress() + " : " + server.getLocalPort());
            System.out.println(server.getInetAddress().getHostName());
            runFlag = true;
            server.setReuseAddress(true);
        }catch(IOException e){
            runFlag = false;
            e.printStackTrace();
        }
        interpreter = new Interpreter();
    }
    
    private void run(){
        Thread serverThread = new Thread(){
            @Override public void run(){
                try{
                    while(runFlag){
                        System.out.println("[Server]\tWaiting for the client request");
                        Socket socket = server.accept();
                        
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        int res = interpreter.interpret((String) in.readObject(), out);
                        
                        System.out.println(res);
                        switch(res){
                            case 0: runFlag = false;
                                break;
                            case -1:
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                        in.close();
                    }
                    runFlag = false;
                }catch(Exception e){
                    e.printStackTrace();
                    System.err.println("[Server]\tAttempting to close: ");
                }
                runFlag = false;
                close();
            }
        };
        serverThread.start();
    }
    
    private void close(){
        try{
            runFlag = false;
            if(server != null){
                server.close();
                System.out.println("[Server]\tServer Closed");
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void loadConfig(){
        TsvIO configReader = new TsvIO(".config", "skel" + java.io.File.separator + "config.tsv");
        port = Integer.parseInt(configReader.readField("PORT", 1));
    }
}
