import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleHttpServer {
    private static final int port = 8080;
    private static final int queueLimit = 50;
    private static final int threadPoolSize = 4;

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadPoolSize,
                threadPoolSize,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueLimit));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Running server at port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            System.out.println("Error handling request");
        }
    }

    private static void handleRequest(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //The below line could be null. Therefore, composing this with Optional
            String line = bufferedReader.readLine();
            List<String> parts = Optional.ofNullable(line)
                    .stream()
                    .flatMap(x -> Arrays.stream(x.split(" ")))
                    .toList();

            //The first part is GET method and second part is the URL path. Consider it invalid if there are less than
            //two parts
            if (parts.size() >= 2) {
                String requestMethod = parts.get(0);
                String path = parts.get(1);
                if ("GET".equalsIgnoreCase(requestMethod) && "/messages".equalsIgnoreCase(path)) {
                    writeResponse(outputStream);
                }
            }

            //Uncomment the below lines if you would like to simulate a delay
//            try{
//                Thread.sleep(1000); //sleep duration in ms
//            }catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        } catch (IOException e) {
            System.out.println("Failed to handle request " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket" + e.getMessage());
            }
        }
    }

    private static void writeResponse(OutputStream outputStream) throws IOException {
        String message = "Hello from codernaut";
        //Don't forget the double new line char below. It's important to separate the header from body
        String httpResponse = """
                HTTP/1.1 200 OK
                Content-Type: text/plain
                Content-Length: """ + message.length() + "\n\n" +
                message;
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
    }
}
