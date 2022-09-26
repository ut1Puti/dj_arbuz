package httpserver.server;

import java.io.*;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread{
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder r = new StringBuilder();
            String line;
            while (!(line = br.readLine()).isBlank()){
                r.append(line + HttpServerConst.CRLF);
            }
            System.out.println(r);
            String[] arr = r.toString().split("\n");
            String file = arr[0].split(" ")[1];

            StringBuilder sb = new StringBuilder();
            boolean g = false;
            StringBuilder codeBuilder = new StringBuilder();
            for (char ch : file.toCharArray()){
                if (ch == '?'){
                    g = true;
                }
                if (g){
                    codeBuilder.append(ch);
                }
                else {
                    sb.append(ch);
                }
            }
            StringBuilder html = new StringBuilder();
            FileInputStream e = new FileInputStream(HttpServerConst.WEB_SRC + sb);
            int b;
            while ((b = e.read()) != -1){
                html.append((char)b);
            }
            System.out.println(html);

            String response = "HTTP/1.1 200 OK" + HttpServerConst.CRLF +
                    "Content-Length:" + html.toString().getBytes().length + HttpServerConst.CRLF +
                    HttpServerConst.CRLF + html + HttpServerConst.CRLF + HttpServerConst.CRLF;

            outputStream.write(response.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}
