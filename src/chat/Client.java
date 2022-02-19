package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

        addWindowListener(new OnlinePackage());
    }
}

//Classe que s'executa al obrir la finestra
class OnlinePackage extends WindowAdapter{
    public void windowOpened(WindowEvent e) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            SendPackage data = new SendPackage();

            data.setMessage(" online");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(data);

            socket.close();

        }catch (Exception ex) {
            System.out.println(e);
        }
    }
}

class WindowBorderClient extends JPanel implements Runnable{
    private JTextField message;
    private JComboBox ip;
    private JLabel nickName;
    private JButton myButton;
    private TextArea chatTextArea;

    //Creació interfície gràfica
    public WindowBorderClient() {
        String userNickname = JOptionPane.showInputDialog("Nick: ");
        JLabel n_nickname = new JLabel("Nickname: ");
        nickName = new JLabel();
        ip = new JComboBox();
        chatTextArea = new TextArea(12,20);
        message = new JTextField(20);

        JLabel text = new JLabel("  Online: ");

        nickName.setText(userNickname);

        add(n_nickname);
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

                //En cas de no haber escrit cap missatge es mostra el missatge
                if(!receivedSocket.getMessage().equals(" online"))  chatTextArea.append("\n"+ receivedSocket.getNickName() + ": "+receivedSocket.getMessage());
                //Si no mostra l'array amb les IP dels usuaris conectats
                else {
                    chatTextArea.append("\n" + receivedSocket.getIpList());

                    ArrayList<String> ipsMenu = new ArrayList<>();
                    ipsMenu = receivedSocket.getIpList(); //Agafem totes les IPs

                    ip.removeAllItems(); //Buidem el JComboBox

                    //Afegim les IPs al JComboBox amb format String
                    for(String ipString : ipsMenu){
                        ip.addItem(ipString);
                    }
                }

            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    //Funció que envia un missatge
    private class SendText implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            try {
                Socket mySocket = new Socket("127.0.0.1",9999);

                SendPackage data = new SendPackage();

                data.setNickName(nickName.getText());
                data.setIp(ip.getSelectedItem().toString());
                data.setMessage(message.getText());

                ObjectOutputStream out = new ObjectOutputStream(mySocket.getOutputStream());
                out.writeObject(data);

                mySocket.close();

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

class SendPackage implements Serializable {
    private String nickName, ip, message;
    private ArrayList <String> ipList;

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

    public ArrayList<String> getIpList() {
        return ipList;
    }

    public void setIpList(ArrayList<String> ipList) {
        this.ipList = ipList;
    }


}