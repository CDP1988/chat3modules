package com.cherevko.dmytro;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "localhost";
    private static final int PORT = 7777;
    private static int WIDTH = 600;
    private static int HEIGHT = 700;
    private JPanel jlistPanel;
    private final JTextArea log = new JTextArea();
    private final JTextField nickName = new JTextField("");
    private final JTextField fieldInput = new JTextField();
    private JButton loginButton;
    private JPanel loginPanel = new JPanel();
    private boolean isLogin;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }


    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setBorder(BorderFactory.createTitledBorder("Chat:"));
        nickName.setEditable(true);
        nickName.setColumns(20);
        fieldInput.setBorder(BorderFactory.createTitledBorder("Print your massage"));
        fieldInput.setEnabled(false);
        fieldInput.addActionListener(this);
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection.sendString(nickName.getText());
            }
        });
        jlistPanel = new JPanel(new GridLayout(3, 0, 5, 0));

        loginPanel.add(nickName);
        loginPanel.add(loginButton);
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login:"));
        add(loginPanel, BorderLayout.NORTH);
        add(log, BorderLayout.CENTER);
        add(jlistPanel, BorderLayout.EAST);
        add(fieldInput, BorderLayout.SOUTH);
        //pack();
        //add(password);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(nickName.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {

        if (!isLogin) {
            if (value.equals("OK")) {
                isLogin = true;
                loginPanel.setEnabled(false);
                fieldInput.setEnabled(true);
                return;
            }
        }
        printMsg(value);
    }

    @Override
    public void sendLoginInfo(TCPConnection tcpConnection, String value) {

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\r\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
