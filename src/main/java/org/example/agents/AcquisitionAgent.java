package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Recipe;
import org.example.utils.RecipeLoader;
import org.example.utils.SpoonacularFetcher;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class AcquisitionAgent extends Agent {
    private List<Recipe> recipeList;

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        recipeList = RecipeLoader.loadRecipes();  // Load from local file

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if ("get_recipes".equals(msg.getContent())) {

                        // If local file is empty, fetch from API
                        if (recipeList == null || recipeList.isEmpty()) {
                            System.out.println("No local recipes found. Fetching from Spoonacular...");
                            try {
                                recipeList = SpoonacularFetcher.fetchRecipesFromAPI("vegan", 5);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }

                        // Inform UI Agent
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("Sending recipes to ProcessingAgent...");
                        send(reply);

                        // Forward to ProcessingAgent as JSON string
                        Gson gson = new Gson();
                        String json = gson.toJson(recipeList);

                        ACLMessage toProc = new ACLMessage(ACLMessage.INFORM);
                        toProc.addReceiver(new AID("ProcessingAgent", AID.ISLOCALNAME));
                        toProc.setContent(json);
                        toProc.setConversationId("recipes-data");  // mark message type
                        send(toProc);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
