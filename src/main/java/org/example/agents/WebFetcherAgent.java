package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class WebFetcherAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + ": started!");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " received: " + msg.getContent());

                    String simulatedRecipe = "{name:'Risotto', ingredients:['rice','mushroom'], time:30}";

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(simulatedRecipe);
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}
