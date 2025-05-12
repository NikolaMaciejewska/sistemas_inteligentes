package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.Recipe;

import java.lang.reflect.Type;
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
                    if (!"recipes-data".equals(msg.getConversationId())) {
                        System.out.println("Ignored unrelated message: " + msg.getContent());
                        return;
                    }

                    try {
                        String json = msg.getContent();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Recipe>>() {}.getType();
                        List<Recipe> recipes = gson.fromJson(json, listType);

                        // Filter recipes
                        List<Recipe> filtered = recipes.stream()
                                .filter(r -> r.getTags() != null &&
                                        r.getTags().contains("vegan") &&
                                        r.getCalories() < 600)
                                .collect(Collectors.toList());

                        // Send results to UI
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(new AID("UIAgent", AID.ISLOCALNAME));
                        response.setContent("Filtered recipes:\n" +
                                filtered.stream()
                                        .map(Recipe::getRecipe_name)
                                        .collect(Collectors.joining(", ")));
                        send(response);

                    } catch (Exception e) {
                        System.err.println("Failed to parse recipe list: " + e);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
