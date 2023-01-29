import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.lang.Thread;
import java.nio.charset.StandardCharsets;
public class Client {
    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 8888;
        try {
            Socket socket = new Socket(hostName, portNumber);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            InputStreamThread inputThread = new InputStreamThread(is);
            inputThread.start();
            Scanner sc = new Scanner(System.in);
            while(true) {
                String msg = sc.nextLine();
                if(msg.equals("quit"))
                    break;
                byte [] dataToSend = msg.getBytes();
                os.write(dataToSend);
            }

            socket.close();
        }catch(IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
}

class InputStreamThread extends Thread {
    private CustomInputStream cis;
    InputStreamThread(InputStream is) {
        this.cis = new CustomInputStream(is);
    }

    @Override
    public void run () {
        try {
            while(this.cis.readByString()){
                System.out.println(cis.getStr());
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
