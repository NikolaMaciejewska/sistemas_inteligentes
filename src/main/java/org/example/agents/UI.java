package org.example.agents;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

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

    private JList<CheckableItem> allergenList;

    private static final String[] COMMON_ALLERGENS = {
            "Almonds", "Dairy", "Eggs", "Fish", "Nuts", "Oats", "Peanuts", "Shellfish", "Soybeans", "Wheat"
    };


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

        resultArea = new JTextArea(30, 30);
        resultArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBounds(440, 80, 420, 610);
        resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(resultScroll);

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
