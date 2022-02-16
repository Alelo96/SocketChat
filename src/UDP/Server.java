package UDP;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
        final int PORT = 5000;
        byte[] buffer = new byte[1024];

        try {
            System.out.println("Initializing server...");
            DatagramSocket scoketUdp = new DatagramSocket(PORT);

            while (true){
                DatagramPacket petition = new DatagramPacket(buffer, buffer.length);

                scoketUdp.receive(petition);

                String data = new String(petition.getData());
                System.out.println(data);

                int clientPort = petition.getPort();
                InetAddress clientAddress = petition.getAddress();

                data = "Hello from the SERVER";
                buffer = data.getBytes();

                DatagramPacket answer = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);

                scoketUdp.send(answer);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
