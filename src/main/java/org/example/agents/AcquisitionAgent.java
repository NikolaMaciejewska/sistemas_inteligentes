package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Recipe;
import org.example.utils.RecipeLoader;
import org.example.utils.SpoonacularFetcher;

import java.util.List;
import java.util.stream.Collectors;

public class AcquisitionAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && "user-query".equals(msg.getConversationId())) {
                    System.out.println(getLocalName() + ": Received user preferences");

                    try {
                        String[] parts = msg.getContent().split(",");
                        String diet = parts[0];
                        int number = Integer.parseInt(parts[1]);
                        int maxCalories = Integer.parseInt(parts[2]);
                        double minRating = Double.parseDouble(parts[3]);
                        int maxTime = Integer.parseInt(parts[4]);

                        List<Recipe> recipes = RecipeLoader.loadRecipes();

                        if (recipes == null || recipes.isEmpty()) {
                            System.out.println(getLocalName() + ": No local recipes found, calling Spoonacular API...");
                            recipes = SpoonacularFetcher.fetchRecipesFromAPI(diet, number);
                        }

                        List<Recipe> filtered = recipes.stream()
                                .filter(r ->
                                        (r.getTags() != null && r.getTags().contains(diet)) &&
                                                r.getCalories() <= maxCalories &&
                                                r.getRating() >= minRating &&
                                                r.getTotal_time() <= maxTime
                                )
                                .limit(number)
                                .collect(Collectors.toList());

                        // 3. Forward to ProcessingAgent
                        ACLMessage toProc = new ACLMessage(ACLMessage.INFORM);
                        toProc.addReceiver(new AID("ProcessingAgent", AID.ISLOCALNAME));
                        toProc.setContentObject((java.io.Serializable) filtered);
                        send(toProc);

                        System.out.println(getLocalName() + ": Sent " + filtered.size() + " filtered recipes to ProcessingAgent");

                    } catch (Exception e) {
                        System.err.println("Error processing user input or sending data: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }
}
