import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestAsServer {

    /**
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        try (ServerSocket server= new ServerSocket(3345)){
            Socket client = server.accept();

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());

            while(!client.isClosed()){
                String entry = in.readUTF();
                System.out.println("Получено от клиента сообщение - "+entry);
                out.writeUTF("Ответ от бота: " + entry);
                out.flush();

            }
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}