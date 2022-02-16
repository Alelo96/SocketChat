package UDP;

import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        final int SERVER_PORT = 5000;
        byte[] buffer = new byte[1024];

        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramSocket scoketUdp = new DatagramSocket();

            String data = "Hello from the CLIENT";
            buffer = data.getBytes();

            DatagramPacket question = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            scoketUdp.send(question);

            DatagramPacket petition = new DatagramPacket(buffer, buffer.length);
            scoketUdp.receive(petition);

            System.out.println(petition.getData());

            scoketUdp.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
