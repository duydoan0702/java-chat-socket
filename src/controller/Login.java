package controller;

import java.awt.EventQueue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import tags.Encode;
import tags.Tags;
import view.MainGui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Font;
import javax.swing.UIManager;

public class Login {
    private static String NAME_FAILED = "THIS NAME CONTAINS INVALID CHARACTER. PLEASE TRY AGAIN";
    private static String NAME_EXSIST = "THIS NAME IS ALREADY USED. PLEASE TRY AGAIN";
    private static String SERVER_NOT_START = "TURN ON SERVER BEFORE START";

    private Pattern checkName = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

    private JFrame frameLoginForm;
    private JTextField txtPort;
    private JLabel lblError;
    private String name = "", IP = "", pass = "";
    private JTextField txtIP;
    private JTextField txtUsername;
    private JButton btnLogin;
    private JTextField passTF;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login window = new Login();
                    window.frameLoginForm.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Login() {
        initialize();
    }

    private void initialize() {
        frameLoginForm = new JFrame();
        frameLoginForm.setTitle("Login Form");
        frameLoginForm.setResizable(false);
        frameLoginForm.setBounds(100, 100, 517, 390);
        frameLoginForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLoginForm.getContentPane().setLayout(null);

        JLabel lblWelcome = new JLabel("Connect With Server\r\n");
        lblWelcome.setForeground(UIManager.getColor("RadioButtonMenuItem.selectionBackground"));
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblWelcome.setBounds(27, 13, 312, 48);
        frameLoginForm.getContentPane().add(lblWelcome);

        JLabel lblHostServer = new JLabel("IP Server");
        lblHostServer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHostServer.setBounds(47, 74, 86, 20);
        frameLoginForm.getContentPane().add(lblHostServer);

        JLabel lblPortServer = new JLabel("Port Server");
        lblPortServer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPortServer.setBounds(349, 77, 79, 14);
        frameLoginForm.getContentPane().add(lblPortServer);

        txtPort = new JTextField();
        txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPort.setText("8080");
        txtPort.setEditable(false);
        txtPort.setColumns(10);
        txtPort.setBounds(429, 70, 65, 28);
        frameLoginForm.getContentPane().add(txtPort);

        JLabel lblUserName = new JLabel("User Name");
        lblUserName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUserName.setBounds(10, 134, 106, 38);
        frameLoginForm.getContentPane().add(lblUserName);
        lblUserName.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/user.png")));

        lblError = new JLabel("");
        lblError.setBounds(68, 323, 399, 20);
        frameLoginForm.getContentPane().add(lblError);

        txtIP = new JTextField();
        txtIP.setBounds(128, 70, 185, 28);
        frameLoginForm.getContentPane().add(txtIP);
        txtIP.setColumns(10);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setColumns(10);
        txtUsername.setBounds(128, 138, 366, 34);
        frameLoginForm.getContentPane().add(txtUsername);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogin.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/login.png")));
        btnLogin.setBounds(325, 259, 169, 48);
        frameLoginForm.getContentPane().add(btnLogin);
        
        JButton signupBT = new JButton("Sign Up");
        signupBT.setFont(new Font("Tahoma", Font.PLAIN, 13));
        signupBT.setBounds(10, 259, 169, 48);
        frameLoginForm.getContentPane().add(signupBT);
        
        passTF = new JTextField();
        passTF.setBounds(128, 190, 366, 38);
        frameLoginForm.getContentPane().add(passTF);
        passTF.setColumns(10);
        
        JLabel passLB = new JLabel("Pass Word");
        passLB.setFont(new Font("Tahoma", Font.PLAIN, 13));
        passLB.setBounds(47, 200, 65, 14);
        frameLoginForm.getContentPane().add(passLB);
        lblError.setVisible(false);
        
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                name = txtUsername.getText();
                lblError.setVisible(false);
                pass = passTF.getText();
                IP = txtIP.getText();
                
                if (checkName.matcher(name).matches() && !IP.equals("")) {
                    try {
                        boolean loginSuccess = handleLogin(name, pass);
                        if (!loginSuccess) {
                            return;
                        }

                        Random rd = new Random();
                        int portPeer = 10000 + rd.nextInt() % 1000;
                        InetAddress ipServer = InetAddress.getByName(IP);
                        int portServer = Integer.parseInt("8080");
                        Socket socketClient = new Socket(ipServer, portServer);
                    
                        String msg = Encode.getCreateAccount(name, Integer.toString(portPeer));
                        ObjectOutputStream serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
                        serverOutputStream.writeObject(msg);
                        serverOutputStream.flush();
                        ObjectInputStream serverInputStream = new ObjectInputStream(socketClient.getInputStream());
                        msg = (String) serverInputStream.readObject();
                    
                        socketClient.close();
                        if (msg.equals(Tags.SESSION_DENY_TAG)) {
                            lblError.setText(NAME_EXSIST);
                            lblError.setVisible(true);
                            return;
                        }
                        new MainGui(IP, portPeer, name, msg);
                        frameLoginForm.dispose();
                    } catch (Exception e) {
                        lblError.setText(SERVER_NOT_START);
                        lblError.setVisible(true);
                        e.printStackTrace();
                    }
                } else {
                    lblError.setText(NAME_FAILED);
                    lblError.setVisible(true);
                }
            }
        });
        
        signupBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUp();
                frameLoginForm.dispose();
            }
        });
    }
    
    public static boolean handleLogin(String username, String password) throws ClassNotFoundException {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Some Fields Are Empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String hashedPassword = SignUp.hashPassword(password);

        String sql = "SELECT * FROM user.users WHERE FULLNAME = ? AND PASSWORD = ?";

        try (Connection connection = JDBCConnection.getJDBCConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Login Successfully");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}