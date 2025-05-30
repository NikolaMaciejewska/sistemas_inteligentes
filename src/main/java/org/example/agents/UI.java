package org.example.agents;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private AcquisitionAgent agent;

    public JTextField dietField;
    public JTextField amountField;
    public JTextField maxCaloriesField;
    public JTextField minRatingField;
    public JTextField maxTotalTimeField;
    public JButton sendButton;
    private File uploadedImageFile;
    private JTextArea resultArea;
    private JButton prevButton, nextButton;
    private JLabel pageLabel;

    private List<String> recipes;
    private int currentRecipeIndex = 0;

    private JList<CheckableItem> allergenList;

    private static final String[] COMMON_ALLERGENS = {
            "Almonds", "Dairy", "Eggs", "Fish", "Milk", "Nuts", "Oats", "Peanuts", "Shellfish", "Soybeans", "Wheat"
    };

    public List<String> splitRecipes(String bigResult) {
        List<String> recipes = new ArrayList<>();

        // Zmieniamy punkt podziału z "Receta:" na "RECIPE n°"
        String[] parts = bigResult.split("(?=RECIPE n°)");

        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                recipes.add(trimmed);
            }
        }
        return recipes;
    }



    private void showRecipe(int index) {
        if (recipes == null || recipes.isEmpty()) {
            resultArea.setText("No recipes found.");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            pageLabel.setText("0/0");
            return;
        }

        if (index >= 0 && index < recipes.size()) {
            resultArea.setText(recipes.get(index));
            resultArea.setCaretPosition(0);

            currentRecipeIndex = index;
            pageLabel.setText((index + 1) + "/" + recipes.size());

            // Enable/disable navigation buttons
            prevButton.setEnabled(index > 0);
            nextButton.setEnabled(index < recipes.size() - 1);
        }
    }

    public void setResults(String bigResultString) {
        this.recipes = splitRecipes(bigResultString);
        showRecipe(0);
    }


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UI frame = new UI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public UI() {

    }

    /**
     * Create the frame.
     */
    public UI(AcquisitionAgent a) {
        this.agent = a;
        setTitle("Recipe Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 740);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(95, 158, 160));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        // Heading - centered
        JLabel headingLabel = new JLabel("Find Your Recipe!");
        headingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setBounds(0, 10, 900, 30);
        contentPane.add(headingLabel);

        // Left column - positions
        int labelX = 40;
        int fieldX = 200;
        int fieldWidth = 200;

        // Input fields
        contentPane.add(label("Number of recipes:", labelX, 55));
        amountField = textField("5", fieldX, 55, fieldWidth);
        contentPane.add(amountField);

        contentPane.add(label("Max calories:", labelX, 95));
        maxCaloriesField = textField("600", fieldX, 95, fieldWidth);
        contentPane.add(maxCaloriesField);

        contentPane.add(label("Min rating:", labelX, 135));
        minRatingField = textField("3.5", fieldX, 135, fieldWidth);
        contentPane.add(minRatingField);

        contentPane.add(label("Max total time (min):", labelX, 175));
        maxTotalTimeField = textField("30", fieldX, 175, fieldWidth);
        contentPane.add(maxTotalTimeField);

        // Ingredients text area (taller)
        contentPane.add(label("Ingredients (text):", labelX, 215));
        JTextArea ingredientsArea = new JTextArea(10, 20);  // Taller (10 rows)
        ingredientsArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScroll = new JScrollPane(ingredientsArea);
        ingredientsScroll.setBounds(labelX, 240, 360, 200);  // increased height
        ingredientsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(ingredientsScroll);

        // Upload button (below ingredients area)
        JButton uploadButton = new JButton("Upload ingredient image");
        uploadButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        uploadButton.setBounds(labelX, 450, 250, 50);
        contentPane.add(uploadButton);

        // Uploaded file label
        JLabel imageStatusLabel = new JLabel("No file selected");
        imageStatusLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        imageStatusLabel.setBounds(labelX + 260, 465, 200, 20);
        contentPane.add(imageStatusLabel);

        // Allergen list
        contentPane.add(label("Avoid allergens:", labelX, 510));
        CheckableItem[] items = new CheckableItem[COMMON_ALLERGENS.length];
        for (int i = 0; i < COMMON_ALLERGENS.length; i++) {
            items[i] = new CheckableItem(COMMON_ALLERGENS[i]);
        }
        allergenList = new JList<>(items);
        allergenList.setCellRenderer(new CheckListRenderer());
        allergenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allergenList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = allergenList.locationToIndex(e.getPoint());
                CheckableItem item = allergenList.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                allergenList.repaint();
            }
        });
        JScrollPane allergenScroll = new JScrollPane(allergenList);
        allergenScroll.setBounds(labelX, 535, 250, 100);
        contentPane.add(allergenScroll);

        // Diet checkboxes to the right of allergen list
        JLabel dietLabel = new JLabel("Diet:");
        dietLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        dietLabel.setBounds(310, 535, 100, 25);
        contentPane.add(dietLabel);

        JCheckBox veganCheckBox = new JCheckBox("Vegan");
        veganCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        veganCheckBox.setBackground(new Color(95, 158, 160));
        veganCheckBox.setBounds(310, 560, 100, 25);
        contentPane.add(veganCheckBox);

        JCheckBox vegetarianCheckBox = new JCheckBox("Vegetarian");
        vegetarianCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        vegetarianCheckBox.setBackground(new Color(95, 158, 160));
        vegetarianCheckBox.setBounds(310, 585, 120, 25);
        contentPane.add(vegetarianCheckBox);

        // Search button (bottom aligned with result area)
        sendButton = new JButton("Search Recipes");
        sendButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        sendButton.setBounds(labelX, 645, 360, 45);
        contentPane.add(sendButton);

        // Results field on the right
        JLabel resultLabel = new JLabel("Recipe Results:");
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        resultLabel.setBounds(440, 50, 300, 25);
        contentPane.add(resultLabel);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBounds(440, 80, 420, 530);
        resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(resultScroll);

        // Navigation panel at bottom of results
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBounds(440, 620, 420, 40);
        navPanel.setBackground(new Color(95, 158, 160));

        prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        prevButton.setEnabled(false);

        pageLabel = new JLabel("0/0");
        pageLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        pageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        nextButton.setEnabled(false);

        navPanel.add(prevButton);
        navPanel.add(pageLabel);
        navPanel.add(nextButton);
        contentPane.add(navPanel);

        setContentPane(contentPane);

        // File chooser for upload button
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                uploadedImageFile = fileChooser.getSelectedFile();
                imageStatusLabel.setText(uploadedImageFile.getName());
                System.out.println("Selected file: " + uploadedImageFile.getAbsolutePath());
            }
        });

        sendButton.addActionListener(e -> {
            try {
                // Obtener datos del formulario
                String ingredientsText = ingredientsArea.getText();
                int amount = Integer.parseInt(amountField.getText());
                int maxCalories = Integer.parseInt(maxCaloriesField.getText());
                double minRating = Double.parseDouble(minRatingField.getText());
                int maxTotalTime = Integer.parseInt(maxTotalTimeField.getText());
                boolean isVegan = veganCheckBox.isSelected();
                boolean isVegetarian = vegetarianCheckBox.isSelected();

                java.util.List<String> selectedAllergens = new java.util.ArrayList<>();
                ListModel<CheckableItem> model = allergenList.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = model.getElementAt(i);
                    if (item.isSelected()) {
                        selectedAllergens.add(item.toString());
                    }
                }

                // Asignar valores al agente
                this.agent.ingredientsText = ingredientsText;
                this.agent.uploadedImage = uploadedImageFile;
                this.agent.amount = amount;
                this.agent.maxCalories = maxCalories;
                this.agent.minRating = minRating;
                this.agent.maxTotalTime = maxTotalTime;
                this.agent.selectedAllergens = selectedAllergens;
                this.agent.vegan = isVegan;
                this.agent.vegetarian = isVegetarian;

                // Despertar al agente
                this.agent.doWake();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa valores válidos en los campos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al enviar datos al agente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        prevButton.addActionListener(e -> {
            showRecipe(currentRecipeIndex - 1);
        });

        nextButton.addActionListener(e -> {
            showRecipe(currentRecipeIndex + 1);
        });
    }

    public JTextArea getArea() {
        return resultArea;
    }

    private JLabel label(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        label.setBounds(x, y, 180, 25);
        return label;
    }

    private JTextField textField(String defaultText, int x, int y, int width) {
        JTextField field = new JTextField(defaultText);
        field.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        field.setBounds(x, y, width, 25);
        return field;
    }

    // Helper class to store checkbox state
    static class CheckableItem {
        private String label;
        private boolean isSelected = false;

        public CheckableItem(String label) {
            this.label = label;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String toString() {
            return label;
        }
    }

    // Renderer for checkbox list
    static class CheckListRenderer extends JCheckBox implements ListCellRenderer<CheckableItem> {
        public Component getListCellRendererComponent(JList<? extends CheckableItem> list, CheckableItem value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setEnabled(list.isEnabled());
            setSelected(value.isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(value.toString());
            return this;
        }
    }
}