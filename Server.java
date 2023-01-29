import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.lang.Thread;



public class Server {
    public static void main(String[] args){
        LinkedList<Socket> socketArray = new LinkedList<Socket>();
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            while(true){
                socketArray.addLast(serverSocket.accept());
                System.out.println(socketArray.getLast().getInetAddress().getHostAddress() + "is connected");

                (new InputStreamThread(socketArray.getLast().getInputStream(), socketArray)).start();
                if(System.in.equals("quit")) break;

            }

            serverSocket.close();
        }catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }   
    }
}
class InputStreamThread extends Thread {
    private CustomInputStream cis;
    private LinkedList<Socket> socketArray;
    InputStreamThread(InputStream is, LinkedList<Socket> socketArray) {
        this.cis = new CustomInputStream(is);
        this.socketArray = socketArray;
    }

    @Override
    public void run () {
        try {
            while(this.cis.readByString()){
                System.out.println(cis.getStr());
                for(Socket s: this.socketArray){
                    s.getOutputStream().write(this.cis.getStr().getBytes());
                }
            }
        }catch(IOException e){
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
}
class CustomInputStream  {
    private InputStream inputStream;
    private String str;
    public CustomInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    public boolean readByString() throws IOException{
        byte[] byteArr = new byte[50];
        int numBytes = this.inputStream.read(byteArr);
        this.str = new String(byteArr, StandardCharsets.UTF_8);
        if(numBytes < 0 || this.str.equals("quit"))  return false;
        return true;
    }
    public String getStr() {
        return this.str;
    }
}
