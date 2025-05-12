package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.models.Recipe;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessingAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        Object content = msg.getContentObject();

                        if (content instanceof List<?>) {
                            List<?> list = (List<?>) content;

                            List<Recipe> recipes = list.stream()
                                    .filter(o -> o instanceof Recipe)
                                    .map(o -> (Recipe) o)
                                    .collect(Collectors.toList());

                            List<Recipe> filtered = recipes.stream()
                                    .filter(r -> r.getTags() != null &&
                                            r.getTags().contains("vegan") &&
                                            r.getCalories() < 600)
                                    .collect(Collectors.toList());

                            ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                            response.addReceiver(new AID("UserInterfaceAgent", AID.ISLOCALNAME));
                            response.setContent("Filtered recipes:\n" +
                                    filtered.stream()
                                            .map(Recipe::getRecipe_name)
                                            .collect(Collectors.joining(", ")));
                            send(response);
                        } else {
                            System.err.println("Received content is not a List<Recipe>");
                        }

                    } catch (UnreadableException e) {
                        System.err.println("Unreadable message content: " + e.getMessage());
                    }
                } else {
                    block();
                }
            }
        });
    }
}
