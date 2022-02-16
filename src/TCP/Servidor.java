package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;
        final int PORT = 5000;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Initializing server...");

            while (true){
                sc = server.accept();

                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());

                String data = in.readUTF();
                System.out.println(data);

                out.writeUTF("Hello World from the SERVER");

                sc.close();
                System.out.println("TCP.Client has been desconnected");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
