package proiect;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;

public class ATMAgent extends Agent {
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    AID bankAID = searchBank();
                    if (bankAID != null) {
                        ACLMessage forward = new ACLMessage(ACLMessage.REQUEST);
                        forward.addReceiver(bankAID);
                        forward.setContent(msg.getContent());
                        forward.setReplyWith("req" + System.currentTimeMillis());
                        send(forward);
                        
                        ACLMessage bankReply = blockingReceive();
                        ACLMessage guiReply = msg.createReply();
                        guiReply.setContent(bankReply.getContent());
                        send(guiReply);
                    }
                } else block();
            }
        });
    }

    private AID searchBank() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("banking-service");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) return result[0].getName();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}