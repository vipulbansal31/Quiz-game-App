package gui;

import connection.DBConnect;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn;

    public LoginFrame() {
        setTitle("Login");
        setBounds(300,100,350,200);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("Password:"));
        passField = new JPasswordField();
        add(passField);

        loginBtn = new JButton("Login");
        add(new JLabel(""));
        add(loginBtn);

        loginBtn.addActionListener(e -> loginUser());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loginUser() {
        try (Connection con = DBConnect.getConnection()) {
            String sql = "SELECT user_id FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, userField.getText());
            pst.setString(2, new String(passField.getPassword()));
            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                int userId = rs.getInt(1);
                JOptionPane.showMessageDialog(this,"Login Successful!");
                dispose();
                new QuizFrame(userId, 1);
            } else {
                JOptionPane.showMessageDialog(this,"Invalid Credentials!");
            }
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
