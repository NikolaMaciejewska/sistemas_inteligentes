package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class UserRecipeBookAgent extends Agent {

    private List<String> userRecipes = new ArrayList<>();

    protected void setup() {
        System.out.println(getLocalName() + ": started!");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " received: " + msg.getContent());

                    String content = msg.getContent();

                    if (content.startsWith("add:")) {
                        String recipe = content.substring(4);
                        userRecipes.add(recipe);
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.CONFIRM);
                        reply.setContent("Recipe added to your book.");
                        send(reply);

                    } else if (content.equals("get-recipes")) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(userRecipes.toString());
                        send(reply);
                    }

                } else {
                    block();
                }
            }
        });
    }
}
