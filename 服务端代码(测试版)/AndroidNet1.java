import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AndroidNet1 {

    public static void main(String[] args) {
     
        InputStream is;
        InputStreamReader reader;
        BufferedReader  bufReader;
        OutputStream os;
        try {
            ServerSocket serverSocket = new ServerSocket(12306);
            System.out.println("服务端监听开始了~~~~");
            Socket socket = serverSocket.accept();
            System.out.println("play1已连接");
            String s = null;
            StringBuffer sb = new StringBuffer();
            
            while (true) {
                
                is = socket.getInputStream();
                reader = new InputStreamReader(is);
                bufReader = new BufferedReader(reader);
                if ((s = bufReader.readLine()) != null) {
                    if(s.equals("getPuke")){
                        os = socket.getOutputStream();
                        os.write(("shufflePuke 1 6 9 17 18 19 27 28 33 34 35 39 48\n").getBytes());
			System.out.println("发牌:1 6 9 17 18 19 27 28 33 34 35 39 48");
                        os.flush();
                    }
                    else if(s.startsWith("play ")){
                        s=s.replaceFirst("play ", "");
                        System.out.println("play1:"+s);
                        BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));
                        String str = buf.readLine();
                        os = socket.getOutputStream();
                        os.write(("play "+str+"\n".toString()).getBytes());
                        os.flush();
                        System.out.println("play2:"+str);
                    }
                    else if(s.startsWith("pass")){
                        BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));
                        String str = buf.readLine();
                        os = socket.getOutputStream();
                        os.write(("play "+str+"\n".toString()).getBytes());
                        os.flush();
                        System.out.println("play2:"+str);
                    }
		    else if(s.equals("victory"))
                        System.out.println("play1:胜");
                    else if(s.equals("quit"))break;
                    else {
                        System.out.println(s);
                    }
                    sb.append(s);
                }
            }
            System.out.println("服务器：" + sb.toString());

            os = socket.getOutputStream();
            os.write("good bye".getBytes());
            os.flush();
            socket.shutdownOutput();
            os.close();

            
            bufReader.close();
            reader.close();
            is.close();
            
            socket.shutdownInput();

            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
