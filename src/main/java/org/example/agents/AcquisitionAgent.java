package org.example.agents;

import jade.lang.acl.MessageTemplate;
import org.example.models.UserRecipePreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcquisitionAgent extends Agent {

    public static List<String> loadIngredientsFromCsv(String path) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.skip(1)
                    .map(line -> line.split(",")[1])
                    .map(String::toLowerCase)
                    .map(s -> s.trim())
                    .collect(Collectors.toList());
        }
    }

    List<String> knownIngredients;

    {
        try {
            knownIngredients = loadIngredientsFromCsv("dataprocessing/all_ingredients.csv");
            System.out.println(knownIngredients);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int levenshteinDistance = 2;

    MainGUI gui;
    public String ingredientsText;
    public File uploadedImage;
    public int amount;
    public int maxCalories;
    public double minRating;
    public int maxTotalTime;
    public boolean vegan;
    public boolean vegetarian;

    private List<String> ingredients;
    public List<String> selectedAllergens;

    @Override
    protected void setup() {
       System.out.println(getLocalName() + ": started");

        // Lanzar GUI
        gui = new MainGUI(this.getLocalName(), this);
        gui.run();

        // Comportamiento que espera doWake() y lanza el envío como OneShot
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                myAgent.doWait(); // Esperar señal desde GUI
                gui.getFrame().getArea().setText("");

                System.out.println(getLocalName() + ": Woken up by UI. Processing user input...");

                try {

                    //extract ingredients from text
                    IngredientExtractor extractor = new IngredientExtractor(knownIngredients, levenshteinDistance);
                    ingredients = extractor.extractAndMatch(ingredientsText);

                    UserRecipePreferences prefs = new UserRecipePreferences();
                    prefs.setIngredients(ingredients);
                    prefs.setSelectedAllergens(selectedAllergens);
                    prefs.setNumber_of_recipes(amount);
                    prefs.setMax_calories(maxCalories);
                    prefs.setMin_rating(minRating);
                    prefs.setMax_total_time(maxTotalTime);
                    prefs.setVegan(vegan);
                    prefs.setVegetarian(vegetarian);

                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(prefs);
                    System.out.println("Generated JSON:\n" + json);

                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("recipe-classification");
                    template.addServices(sd);

                    DFAgentDescription[] result = DFService.search(myAgent, template);

                    if (result.length > 0) {
                        AID recipient = result[0].getName();

                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(recipient);
                        msg.setLanguage("JSON");
                        msg.setContent(json);

                        send(msg);
                        System.out.println("Message sent to " + recipient.getLocalName());

                    } else {
                        System.err.println("No agent found offering 'Clasificacion de recetas'");
                    }

                } catch (FIPAException fe) {
                    System.err.println("Error searching DF: " + fe.getMessage());
                    fe.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error processing user input or sending data: " + e.getMessage());
                    e.printStackTrace();
                }

                ACLMessage reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                if (reply != null) {
                    String content = reply.getContent();
                    System.out.println(getLocalName() + ": Received response:");
                    System.out.println(content);

                    gui.getFrame().getArea().setText(content);

                } else {
                    block();
                }
            }
        });
    }
}