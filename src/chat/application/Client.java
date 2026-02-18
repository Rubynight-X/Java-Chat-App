package chat.application;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

public class Client implements ActionListener{
    
    static JFrame f = new JFrame();
    JTextField textBar;
    static JPanel textArea;
    JLabel bgLabel;
    static JLabel arrowLabel;
    static DataOutputStream dataOut;
    static Box vertical = Box.createVerticalBox();
    static long lastMessageTime = System.currentTimeMillis();
    static int numOfMessage = 0;
    
    Client() {
        f.setLayout(null);
        f.setTitle("Chat Application");
        f.setSize(400, 720);
        f.setLocation(800, 50);
        f.getContentPane().setBackground(new Color(236, 236, 228));
        
        
        JPanel topBar = new JPanel();
        topBar.setLayout(null);
        topBar.setBackground(Color.WHITE);
        topBar.setBounds(0, 0, 400, 50);
        f.add(topBar);
        
                        
        // add back arrow
        ImageIcon arrow = new ImageIcon(ClassLoader.getSystemResource("pictures/backarrow.jpg"));
        Image scaledArrow = arrow.getImage().getScaledInstance(35,40, Image.SCALE_DEFAULT);
        arrowLabel = new JLabel(new ImageIcon(scaledArrow));
        arrowLabel.setBounds(8, 5, 35, 40);
        topBar.add(arrowLabel);
        
        arrowLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });
       
        ImageIcon dots = new ImageIcon(ClassLoader.getSystemResource("pictures/dots.jpg"));
        Image scaledDots = dots.getImage().getScaledInstance(37, 40, Image.SCALE_DEFAULT);
        JLabel dotsLabel = new JLabel(new ImageIcon(scaledDots));
        dotsLabel.setBounds(340, 10, 37, 40);
        topBar.add(dotsLabel);
        
                   
        
        JLabel title = new JLabel("Dasos");
        title.setBounds(160, 0, 90, 40);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("SAN_SERIF", Font.BOLD, 24));
        topBar.add(title);
        
        JLabel status = new JLabel("Active Now");
        status.setBounds(162, 30, 90, 20);
        status.setForeground(Color.GRAY);
        status.setFont(new Font("SAN_SERIF", Font.PLAIN ,12));
        topBar.add(status);
        
        
           
        
        // customize the background image
        ImageIcon background = new ImageIcon(ClassLoader.getSystemResource("pictures/background.jpg"));
        Image scaledBg = background.getImage().getScaledInstance(400, 750, Image.SCALE_DEFAULT);
        bgLabel = new JLabel(new ImageIcon(scaledBg));
        bgLabel.setBounds(0, -30, 400, 665);
        f.add(bgLabel);
        
        
        textArea = new JPanel();
        textArea.setBounds(10, 90, 367, 560);
        textArea.setOpaque(false);
        bgLabel.add(textArea);        
             
        textBar = new JTextField();
        textBar.setBounds(10, 642, 315, 35);
        textBar.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(textBar);
        
               
        ImageIcon sendIcon = new ImageIcon(ClassLoader.getSystemResource("pictures/send.jpg"));
        Image scaledSend = sendIcon.getImage().getScaledInstance(50,55, Image.SCALE_DEFAULT);
        JButton send = new JButton();
        send.setBackground(Color.WHITE);
        send.setIcon(new ImageIcon(scaledSend));
        send.setBounds(330, 642, 50, 34);
        send.addActionListener(this);
        f.add(send);
             
       
        f.setVisible(true);
    } 
    
    public void actionPerformed(ActionEvent ae) {
        try {
            String message = textBar.getText(); 
        
            if (!message.equals("")) {
                numOfMessage++;

                long currentTime = System.currentTimeMillis();
                long timeDifference = (currentTime - lastMessageTime) / 60000;

                if (timeDifference >= 2 || numOfMessage == 1) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    JLabel time = new JLabel();
                    time.setBounds(168, (numOfMessage - 1) * 60, 30, 10);
                    time.setForeground(new Color(236, 236, 228));
                    time.setText(sdf.format(cal.getTime()));  
                    textArea.add(time);
                }

                lastMessageTime = currentTime;

                JPanel msgPanel = formatLabel(message, "right");
                textArea.setLayout(new BorderLayout());

                JPanel right = new JPanel(new BorderLayout());
                right.add(msgPanel, BorderLayout.LINE_END);
                right.setOpaque(false);
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(8));

                textArea.add(vertical, BorderLayout.PAGE_START);

                dataOut.writeUTF(message);            

                textBar.setText("");

                f.revalidate();
                f.repaint();            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }         
    }
    
    
    public static JPanel formatLabel(String message, String side) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        
        JLabel output = new JLabel(message);
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(124, 250, 99));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(10, 15, 10, 15));
              
        textPanel.add(output);
        
        
        // add profile picture
        JLabel picLabel = new JLabel();
        if (side.equals("right")) {
            ImageIcon profilePic = new ImageIcon(ClassLoader.getSystemResource("pictures/profilePic2.jpg"));
            Image scaledPic = profilePic.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
            picLabel = new JLabel(new ImageIcon(scaledPic));  
            panel.add(textPanel);
            panel.add(picLabel);
        } else {
            ImageIcon profilePic = new ImageIcon(ClassLoader.getSystemResource("pictures/profilePic1.jpg"));
            Image scaledPic = profilePic.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
            picLabel = new JLabel(new ImageIcon(scaledPic)); 
            panel.add(picLabel);
            panel.add(textPanel);
        }
                    

        return panel;
    }
    
    public static void main(String[] args) {
        new Client();
        
        try {
            Socket s = new Socket("127.0.0.1", 6001);
            DataInputStream dataIn = new DataInputStream(s.getInputStream());
            dataOut = new DataOutputStream(s.getOutputStream());
            
            while (true) {
                String msg = dataIn.readUTF();
                numOfMessage++;
                
                if (!msg.equals("")) {
                    long currentTime = System.currentTimeMillis();
                    long timeDifference = (currentTime - lastMessageTime) / 60000;

                    if (timeDifference >= 2 || numOfMessage == 1) {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        JLabel time = new JLabel();
                        time.setBounds(168, (numOfMessage - 1) * 60, 30, 10);
                        time.setForeground(new Color(236, 236, 228));
                        time.setText(sdf.format(cal.getTime()));  
                        textArea.add(time);
                    }

                    lastMessageTime = currentTime;
                }
                
                JPanel panel = formatLabel(msg, "left");
                  
                JPanel left = new JPanel(new BorderLayout());
                left.add(panel, BorderLayout.LINE_START);
                left.setOpaque(false);
                vertical.add(left);
                
                vertical.add(Box.createVerticalStrut(8));
                textArea.setLayout(new BorderLayout());
                textArea.add(vertical, BorderLayout.PAGE_START);
                
            
                f.validate();

            }     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}