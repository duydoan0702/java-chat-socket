package controller;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SignUp extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField nameTF;
    private JTextField passTF;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SignUp frame = new SignUp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SignUp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 549, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel titleLB = new JLabel("CREATE AN ACCOUNT");
        titleLB.setFont(new Font("Tahoma", Font.BOLD, 14));
        titleLB.setBounds(170, 32, 182, 13);
        contentPane.add(titleLB);
        
        JLabel nameLB = new JLabel("User Name");
        nameLB.setFont(new Font("Tahoma", Font.BOLD, 13));
        nameLB.setBounds(25, 82, 81, 13);
        contentPane.add(nameLB);
        
        nameTF = new JTextField();
        nameTF.setBounds(131, 73, 310, 33);
        contentPane.add(nameTF);
        nameTF.setColumns(10);
        
        JLabel passLB = new JLabel("Pass Word");
        passLB.setFont(new Font("Tahoma", Font.BOLD, 13));
        passLB.setBounds(25, 140, 81, 13);
        contentPane.add(passLB);
        
        passTF = new JTextField();
        passTF.setBounds(131, 131, 310, 33);
        contentPane.add(passTF);
        passTF.setColumns(10);
        
        JButton signUpBT = new JButton("Sign Up");
        signUpBT.setFont(new Font("Tahoma", Font.BOLD, 13));
        signUpBT.setBounds(190, 204, 110, 33);
        contentPane.add(signUpBT);
        
        signUpBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameTF.getText();
                String pass = passTF.getText();
                try {
                    handleSignUp(name, pass);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        
        JButton exitBT = new JButton("Exit");
        exitBT.setFont(new Font("Tahoma", Font.BOLD, 13));
        exitBT.setBounds(398, 204, 110, 33);
        contentPane.add(exitBT);
        
        exitBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                Login loginFrame = new Login();
                loginFrame.main(null);
            }
        });
        
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void handleSignUp(String name, String pass) throws ClassNotFoundException, SQLException {
        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Some fields are empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Băm mật khẩu trước khi lưu
        String hashedPassword = hashPassword(pass);
        
        Connection connection = JDBCConnection.getJDBCConnection();
        String sqlInsert = "INSERT INTO user.users(FULLNAME, PASSWORD) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, hashedPassword);
        preparedStatement.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "SIGN UP SUCCESSFUL", "NOTIFICATION", JOptionPane.INFORMATION_MESSAGE);
    }
}
