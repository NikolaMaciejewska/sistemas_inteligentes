package org.example.agents;

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
                    // Send to ProcessingAgent
                    //TODO

                } catch (Exception e) {
                    System.err.println("Error processing user input or sending data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }
}
