package edu.nyu.cs.cs2580;

/**
 * Created by sanchitmehta on 15/10/16.
 */

import com.sun.net.httpserver.HttpExchange;
        import com.sun.net.httpserver.HttpHandler;

import java.io.*;

@SuppressWarnings("restriction")
public class StaticFileServer implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(" Inside Static File Server");
        String fileId = exchange.getRequestURI().getPath();
        File file = getFile(fileId);
        if (file == null) {
            String response = "Error 404 File not found.";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.flush();
            output.close();
        } else {
            exchange.sendResponseHeaders(200, 0);
            OutputStream output = exchange.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
            output.flush();
            output.close();
            fs.close();
        }
    }

    private File getFile(String fileId) throws IOException {
        // TODO retrieve the file associated with the id
        System.out.println(fileId);
        File f2 = new File(fileId.substring(1));
        File f = new File(fileId);

        return f2;
    }
}