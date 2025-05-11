package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class UIAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        addBehaviour(new Behaviour() {
            boolean done = false;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("[UI Agent] Received recipes:\n" + msg.getContent());
                    done = true;
                } else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return done;
            }
        });

        // Simulate a user query
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(getAID("AcquisitionAgent"));
        request.setContent("get_recipes");
        send(request);
    }
}