package chat;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerWindow mimarco = new ServerWindow();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
class ServerWindow extends JFrame implements Runnable{

    private JTextArea textArea;

    public ServerWindow() {
        setBounds(1200,300,280,350);
        JPanel BorderServerWindow = new JPanel();
        BorderServerWindow.setLayout(new BorderLayout());
        textArea = new JTextArea();
        BorderServerWindow.add(textArea, BorderLayout.CENTER);
        add(BorderServerWindow);
        setVisible(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(9999);

            String nickName, ip, message;
            SendPackage receivedPackage;


            while(true){
                Socket socket = server.accept();

                //Agafem les del client
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                receivedPackage = (SendPackage)in.readObject();
                nickName = receivedPackage.getNickName();
                ip = receivedPackage.getIp();
                message = receivedPackage.getMessage();

                textArea.append("\n"+nickName + ": "+message+" to "+ip);

                Socket sendMessage = new Socket(ip, 9090);

                //Enviem les dades al client
                ObjectOutputStream sendPackage = new ObjectOutputStream(sendMessage.getOutputStream());
                sendPackage.writeObject(receivedPackage);
                sendPackage.close();
                sendMessage.close();

                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}