package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

public class PreferenceAgent extends Agent {

    private Map<String, String> preferences = new HashMap<>();

    protected void setup() {
        System.out.println(getLocalName() + ": started!");

        // example preferences
        preferences.put("diet", "vegetarian");
        preferences.put("allergy", "gluten");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " received: " + msg.getContent());

                    if (msg.getContent().equals("get-preferences")) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(preferences.toString());
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
