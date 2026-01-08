package proiect;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.*;
import java.util.Properties;

public class BankAgent extends Agent {
    private Properties accounts = new Properties();
    private final String DB_FILE = "bank_vault.properties";

    protected void setup() {
        loadData();
        registerInDF();

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        String[] parts = msg.getContent().split(":");
                        String op = parts[0];
                        String user = parts[1];
                        String pin = parts[2];
                        double amount = Double.parseDouble(parts[3]);
                        
                        String response = processTransaction(op, user, pin, amount);

                        ACLMessage reply = msg.createReply();
                        reply.setContent(response);
                        send(reply);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else block();
            }
        });
    }

    private String processTransaction(String op, String user, String pin, double amount) {
        String data = accounts.getProperty(user);

        if (op.equals("OPEN")) {
            if (data != null) return "EROARE: Contul exista deja!";
            accounts.setProperty(user, pin + ":" + amount);
            saveData();
            return "SUCCESS: Cont deschis pentru " + user;
        }

        if (data == null) return "EROARE: Cont inexistent!";
        
        String[] accountData = data.split(":");
        String storedPin = accountData[0];
        double currentBalance = Double.parseDouble(accountData[1]);

        if (!storedPin.equals(pin)) return "EROARE: PIN Incorect!";

        if (op.equals("DEPOZIT")) {
            currentBalance += amount;
            accounts.setProperty(user, storedPin + ":" + currentBalance);
            saveData();
            return "SUCCESS: Depunere reusita. Sold nou: " + currentBalance;
        } 
        
        if (op.equals("RETRAGERE")) {
            if (currentBalance < amount) return "EROARE: Fonduri insuficiente!";
            currentBalance -= amount;
            accounts.setProperty(user, storedPin + ":" + currentBalance);
            saveData();
            return "SUCCESS: Retragere efectuata. Sold nou: " + currentBalance;
        }

        return "EROARE: Operatie invalida!";
    }

    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("banking-service");
        sd.setName("Central-Bank-Service");
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (FIPAException fe) { fe.printStackTrace(); }
    }

    private void loadData() {
        File file = new File(DB_FILE);
        if(!file.exists()) return;
        try (InputStream is = new FileInputStream(DB_FILE)) { accounts.load(is); } 
        catch (IOException e) { e.printStackTrace(); }
    }

    private void saveData() {
        try (OutputStream os = new FileOutputStream(DB_FILE)) { accounts.store(os, "Bank Data Storage"); } 
        catch (IOException e) { e.printStackTrace(); }
    }
}