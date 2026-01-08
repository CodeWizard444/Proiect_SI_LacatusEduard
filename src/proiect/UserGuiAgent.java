package proiect;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javax.swing.*;
import java.awt.*;

public class UserGuiAgent extends Agent {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private JTextField txtUser = new JTextField(15);
    private JTextField txtPin = new JTextField(15); 
    private JTextField txtSuma = new JTextField(15);
    private String currentOp = "";

    protected void setup() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("ATM JADE Simulator");
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JButton btnOpen = new JButton("Deschide Cont Nou");
            JButton btnDepozit = new JButton("Depunere");
            JButton btnRetragere = new JButton("Retragere Bani");
            JButton btnExit = new JButton("Inchidere Centralizata");
            
            menuPanel.add(btnOpen); menuPanel.add(btnDepozit); 
            menuPanel.add(btnRetragere); menuPanel.add(btnExit);

            JPanel userPanel = new JPanel(new BorderLayout(10,10));
            userPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            userPanel.add(new JLabel("Introduceti Nume Utilizator:"), BorderLayout.NORTH);
            userPanel.add(txtUser, BorderLayout.CENTER);
            JButton btnUserNext = new JButton("OK / Next");
            userPanel.add(btnUserNext, BorderLayout.SOUTH);

            JPanel dataPanel = new JPanel(new GridLayout(6, 1, 5, 5));
            dataPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            dataPanel.add(new JLabel("PIN:"));
            dataPanel.add(txtPin);
            dataPanel.add(new JLabel("Suma:"));
            dataPanel.add(txtSuma);
            JButton btnSubmit = new JButton("Confirma Tranzactia");
            JButton btnBack = new JButton("Inapoi la Meniu"); 
            dataPanel.add(btnSubmit);
            dataPanel.add(btnBack);

            mainPanel.add(menuPanel, "MENU");
            mainPanel.add(userPanel, "USER");
            mainPanel.add(dataPanel, "DATA");

            btnOpen.addActionListener(e -> { currentOp = "OPEN"; cardLayout.show(mainPanel, "USER"); });
            btnDepozit.addActionListener(e -> { currentOp = "DEPOZIT"; cardLayout.show(mainPanel, "USER"); });
            btnRetragere.addActionListener(e -> { currentOp = "RETRAGERE"; cardLayout.show(mainPanel, "USER"); });
            btnExit.addActionListener(e -> System.exit(0));
            btnBack.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

            btnUserNext.addActionListener(e -> {
                if(txtUser.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Eroare: Trebuie sa introduceti un nume!");
                } else {
                    cardLayout.show(mainPanel, "DATA");
                }
            });

            btnSubmit.addActionListener(e -> {
                String sumaStr = txtSuma.getText().trim();
                try {
                    if (sumaStr.isEmpty()) throw new Exception("Campul suma este gol!");
                    double val = Double.parseDouble(sumaStr);
                    if (val < 0) throw new Exception("Suma nu poate fi negativa!");

                    boolean success = trimiteCerere(currentOp);
                    
                    if (success) {
                        txtUser.setText(""); txtPin.setText(""); txtSuma.setText("");
                        cardLayout.show(mainPanel, "MENU");
                    }
                    
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(frame, "Eroare: Introduceti doar cifre in campul Suma!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Eroare: " + ex.getMessage());
                }
            });

            frame.add(mainPanel);
            frame.setSize(450, 400); 
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private boolean trimiteCerere(String tip) {
        String user = txtUser.getText().trim();
        String pin = txtPin.getText().trim(); 
        String suma = txtSuma.getText().trim();

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("ATM1", AID.ISLOCALNAME)); 
        msg.setContent(tip + ":" + user + ":" + pin + ":" + suma);
        send(msg);
        
        ACLMessage reply = blockingReceive();
        if (reply != null) {
            String content = reply.getContent();
            JOptionPane.showMessageDialog(frame, content);
            
            return content.startsWith("SUCCESS");
        }
        return false;
    }
}