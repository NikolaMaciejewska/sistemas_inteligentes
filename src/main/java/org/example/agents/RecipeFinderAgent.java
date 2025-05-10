package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RecipeFinderAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + ": started!");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " received: " + msg.getContent());

                    // TODO: filter and searching logic
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Recipe: [output form json/api]");
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}
