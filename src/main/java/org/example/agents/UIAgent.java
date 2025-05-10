package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class UIAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + ": started!");

        addBehaviour(new OneShotBehaviour() {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("PreferenceAgent"));
                msg.setContent("get-preferences");
                send(msg);
            }
        });

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " received: " + msg.getContent());

                } else {
                    block();
                }
            }
        });
    }
}
