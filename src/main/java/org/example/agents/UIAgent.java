package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.*;

public class UIAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");
        SwingUtilities.invokeLater(this::showUI);
    }

    private void showUI() {
        JFrame frame = new JFrame("Recipe Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField dietField = new JTextField("vegan");
        JTextField amountField = new JTextField("5");
        JTextField maxCaloriesField = new JTextField("600");
        JTextField minRatingField = new JTextField("3.5");
        JTextField maxTotalTimeField = new JTextField("30");

        JButton sendButton = new JButton("Search Recipes");

        // Labels and fields
        panel.add(new JLabel("Dish type:"));
        panel.add(dietField);

        panel.add(new JLabel("Number of recipes:"));
        panel.add(amountField);

        panel.add(new JLabel("Max calories:"));
        panel.add(maxCaloriesField);

        panel.add(new JLabel("Min rating:"));
        panel.add(minRatingField);

        panel.add(new JLabel("Max total time (min):"));
        panel.add(maxTotalTimeField);

        panel.add(new JLabel(""));
        panel.add(sendButton);

        frame.add(panel);
        frame.setVisible(true);

        sendButton.addActionListener(e -> {
            String diet = dietField.getText().trim();
            int number = Integer.parseInt(amountField.getText().trim());
            int maxCalories = Integer.parseInt(maxCaloriesField.getText().trim());
            double minRating = Double.parseDouble(minRatingField.getText().trim());
            int maxTime = Integer.parseInt(maxTotalTimeField.getText().trim());

            String message = String.join(",", diet, String.valueOf(number),
                    String.valueOf(maxCalories), String.valueOf(minRating), String.valueOf(maxTime));

            addBehaviour(new OneShotBehaviour() {
                @Override
                public void action() {
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(new AID("AcquisitionAgent", AID.ISLOCALNAME));
                    request.setContent(message);
                    request.setConversationId("user-query");
                    send(request);
                }
            });
        });
    }
}