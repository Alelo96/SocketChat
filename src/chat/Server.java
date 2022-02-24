package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        ServerWindow mimarco = new ServerWindow();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
class ServerWindow extends JFrame implements Runnable{
    private JTextArea textArea;
    private JComboBox online;
    private JButton ban;
    ArrayList <String> ipList = new ArrayList<>();

    public ServerWindow() { //Interfície gràfica
        setBounds(1200,300,280,350);
        JPanel BorderServerWindow = new JPanel();
//        BorderServerWindow.setLayout(new BorderLayout());
        textArea = new JTextArea(12,30);
        BorderServerWindow.add(textArea, BorderLayout.CENTER);
        online = new JComboBox();
        BorderServerWindow.add(online);
        ban = new JButton("Ban");
        BorderServerWindow.add(ban);
        ServerWindow.banIp myEvent = new ServerWindow.banIp();
        ban.addActionListener(myEvent);
        add(BorderServerWindow);
        setVisible(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    private class banIp implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String selectedItem = online.getSelectedItem().toString();
            ipList.remove(selectedItem);

            SendPackage receivedPackage = new SendPackage();
            receivedPackage.setIpList(ipList); //Posem les ip al arrayList
            receivedPackage.setConType(1);

            try {
                for (String ipStrng:ipList) {
                    Socket sendMessage = new Socket(ipStrng, 9090);

                    //Enviem les dades al client
                    ObjectOutputStream sendPackage = new ObjectOutputStream(sendMessage.getOutputStream());
                    sendPackage.writeObject(receivedPackage);
                    sendPackage.close();
                    sendMessage.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            SendPackage bannedPackage = new SendPackage();
            bannedPackage.setMessage("You have been banned");
            bannedPackage.setConType(3);

            try {
                Socket sendMessage = new Socket(selectedItem, 9090);

                //Enviem les dades al client
                ObjectOutputStream sendPackage = new ObjectOutputStream(sendMessage.getOutputStream());
                sendPackage.writeObject(bannedPackage); //Enviem a l'usuari a eliminar un paquet de tipus 3
                sendPackage.close();
                sendMessage.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(9999);

            String nickName, ip, message;
            SendPackage receivedPackage;
            int conType;

            while(true){
                Socket socket = server.accept();

                //Agafem les dades del client
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                receivedPackage = (SendPackage)in.readObject();
                nickName = receivedPackage.getNickName();
                ip = receivedPackage.getIp();
                message = receivedPackage.getMessage();
                conType = receivedPackage.getConType();

                switch (conType){
                    case 1:
                        //Online detection ------------------------------
                        InetAddress localization = socket.getInetAddress();
                        String remoteIp = localization.getHostAddress();//Agafem la ip del client

                        ipList.add(remoteIp);
                        online.removeAllItems();
                        online.addItem(remoteIp);
                        receivedPackage.setIpList(ipList); //Posem les ip al arrayList

                        for (String ipStrng:ipList) {
                            Socket sendMessage = new Socket(ipStrng, 9090);

                            //Enviem les dades al client
                            ObjectOutputStream sendPackage = new ObjectOutputStream(sendMessage.getOutputStream());
                            sendPackage.writeObject(receivedPackage);
                            sendPackage.close();
                            sendMessage.close();

                            socket.close();
                        }
                        break;
                    case 2:
                        textArea.append("\n" + nickName + ": " + message + " to " + ip);

                        Socket sendMessage = new Socket(ip, 9090);

                        //Enviem les dades al client
                        ObjectOutputStream sendPackage = new ObjectOutputStream(sendMessage.getOutputStream());
                        sendPackage.writeObject(receivedPackage);
                        sendPackage.close();
                        sendMessage.close();

                        socket.close();
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}