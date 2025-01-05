import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class MessageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String message = "Hello from codernaut";
        if (method.equalsIgnoreCase("GET")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            exchange.sendResponseHeaders(200, message.getBytes().length);
            writeMessage(message, exchange);
        }
    }

    private void writeMessage(String message, HttpExchange exchange) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(message.getBytes());
        outputStream.close();
    }
}
