package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Recipe;
import org.example.utils.RecipeLoader;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class AcquisitionAgent extends Agent {
    private List<Recipe> recipeList;

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");
        recipeList = RecipeLoader.loadRecipes();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if ("get_recipes".equals(msg.getContent())) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("Sending recipes to ProcessingAgent...");

                        // Forward recipes to processing agent
                        ACLMessage toProc = new ACLMessage(ACLMessage.INFORM);
                        toProc.addReceiver(new AID("ProcessingAgent", AID.ISLOCALNAME));
                        try {
                            toProc.setContentObject((Serializable) recipeList);  // recipesList = List<Recipe>
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        send(reply);

                    }
                } else {
                    block();
                }
            }
        });
    }
}

