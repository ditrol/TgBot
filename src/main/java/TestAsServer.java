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

                System.out.println("Server reading from channel");


                String entry = in.readUTF();

                System.out.println("READ from client message - "+entry);

                System.out.println("Server try writing to channel");

                out.writeUTF("Server reply - "+entry + " - OK");
                System.out.println("Server Wrote message to client.");

                out.flush();

            }

            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            in.close();
            out.close();

            client.close();


            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}