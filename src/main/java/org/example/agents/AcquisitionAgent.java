package org.example.agents;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Recipe;
import org.example.utils.RecipeLoader;
import org.example.utils.SpoonacularFetcher;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AcquisitionAgent extends Agent {
	
	MainGUI gui;
    public String ingredientsText;
    public File uploadedImage;
    public int amount;
    public int maxCalories;
    public double minRating;
    public int maxTotalTime;
    public boolean vegan;
    public boolean vegetarian;

    //TODO
    //to be extracted from ingredientsText or uploadedImage????
    private List <String> ingredients;
    private List <String> allergic_information;

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        //launch GUI
        gui = new MainGUI(this.getLocalName(), this);
		gui.run();

        //main behaviour
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                block();  // Wait for doWake() from GUI
                System.out.println(getLocalName() + ": Woken up by UI. Processing user input...");
                try {
                    // fill object
                    UserRecipePreferences prefs = new UserRecipePreferences();
                    prefs.ingredients = ingredients;
                    prefs.allergic_information = allergic_information;
                    prefs.number_of_recipes = amount;
                    prefs.max_calories = maxCalories;
                    prefs.min_rating = minRating;
                    prefs.max_total_time = maxTotalTime;
                    prefs.vegan = vegan;
                    prefs.vegetarian = vegetarian;

                    System.out.println("Sending user preferences to ProcessingAgent...");

                    // make a json
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(prefs);
                    System.out.println("Generated JSON:\n" + json);

                    // create message
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(new AID("ProcessingAgent", AID.ISLOCALNAME));
                    msg.setLanguage("JSON");
                    msg.setContent(json);

                    // send message
                    send(msg);

                } catch (Exception e) {
                    System.err.println("Error processing user input or sending data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }
}
