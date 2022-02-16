package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        ClientWindow window = new ClientWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class ClientWindow extends JFrame{
    public ClientWindow(){
        setBounds(600,300,280,350);

        WindowBorderClient border = new WindowBorderClient();
        add(border);
        setVisible(true);
    }
}

class WindowBorderClient extends JPanel implements Runnable{
    private JTextField message, nickName, ip;
    private JButton myButton;
    private TextArea chatTextArea;

    public WindowBorderClient() {
        nickName = new JTextField(5);
        ip = new JTextField(8);
        chatTextArea = new TextArea(12,20);
        message = new JTextField(20);

        JLabel text = new JLabel("Chat");

        add(nickName);
        add(text);
        add(ip);
        add(chatTextArea);
        add(message);

        myButton = new JButton("Enviar");
        SendText myEvent = new SendText();
        myButton.addActionListener(myEvent);
        add(myButton);

        Thread thread = new Thread(this);
        thread.start();//Execució del run

    }

    public void run() {
        try {
            ServerSocket serverClient = new ServerSocket(9090);
            Socket client;
            SendPackage receivedSocket;

            while (true){
                client = serverClient.accept();

                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                receivedSocket = (SendPackage) in.readObject();

                chatTextArea.append("\n"+ receivedSocket.getNickName() + ": "+receivedSocket.getMessage());
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    private class SendText implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            try {
                Socket mySocket = new Socket("127.0.0.1",9999);

                SendPackage data = new SendPackage();

                data.setNickName(nickName.getText());
                data.setIp(ip.getText());
                data.setMessage(message.getText());

                ObjectOutputStream out = new ObjectOutputStream(mySocket.getOutputStream());
                out.writeObject(data);

                mySocket.close();

                /*DataOutputStream output = new DataOutputStream(mySocket.getOutputStream());
                output.writeUTF(field1.getText());


                output.close();*/

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

class SendPackage implements Serializable {
    private String nickName, ip, message;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}