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
            setBounds(100, 100, 450, 650);
            contentPane = new JPanel();
            contentPane.setBackground(new Color(95, 158, 160));
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
            amountField = new JTextField("5");
            amountField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            amountField.setBounds(225, 55, 180, 25);
            maxCaloriesField = new JTextField("600");
            maxCaloriesField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            maxCaloriesField.setBounds(225, 95, 180, 25);
            minRatingField = new JTextField("3.5");
            minRatingField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            minRatingField.setBounds(225, 135, 180, 25);
            maxTotalTimeField = new JTextField("30");
            maxTotalTimeField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            maxTotalTimeField.setBounds(225, 175, 180, 25);
            contentPane.setLayout(null);

            JLabel headingLabel = new JLabel("Find Your Recipe!");
            headingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headingLabel.setBounds(100, 10, 250, 30);
            contentPane.add(headingLabel);

            JLabel label_1 = new JLabel("Number of recipes:");
            label_1.setLabelFor(amountField);
            label_1.setBackground(UIManager.getColor("TextArea.selectionBackground"));
            label_1.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            label_1.setBounds(45, 55, 180, 25);
            contentPane.add(label_1);
            contentPane.add(amountField);

            JLabel label_2 = new JLabel("Max calories:");
            label_2.setLabelFor(maxCaloriesField);
            label_2.setBackground(UIManager.getColor("TextArea.selectionBackground"));
            label_2.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            label_2.setBounds(45, 95, 180, 25);
            contentPane.add(label_2);
            contentPane.add(maxCaloriesField);

            JLabel label_3 = new JLabel("Min rating:");
            label_3.setLabelFor(minRatingField);
            label_3.setBackground(UIManager.getColor("TextArea.selectionBackground"));
            label_3.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            label_3.setBounds(45, 135, 180, 25);
            contentPane.add(label_3);
            contentPane.add(minRatingField);

            JLabel label_4 = new JLabel("Max total time (min):");
            label_4.setLabelFor(maxTotalTimeField);
            label_4.setBackground(UIManager.getColor("TextArea.selectionBackground"));
            label_4.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            label_4.setBounds(45, 175, 180, 25);
            contentPane.add(label_4);
            contentPane.add(maxTotalTimeField);

                    setContentPane(contentPane);
                    sendButton = new JButton("Search Recipes");
                    sendButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
                    sendButton.setBounds(45, 550, 360, 45);
                    contentPane.add(sendButton);


            // Ingredients text area (scrollable)
            JLabel ingredientsLabel = new JLabel("Ingredients (text):");
            ingredientsLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            ingredientsLabel.setBounds(45, 215, 180, 25);
            contentPane.add(ingredientsLabel);

            JTextArea ingredientsArea = new JTextArea(5, 20);
            ingredientsArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
            ingredientsArea.setLineWrap(true);
            ingredientsArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(ingredientsArea);
            scrollPane.setBounds(225, 215, 180, 140); // Adjust size/position as needed
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            contentPane.add(scrollPane);

            // Image upload button
            JButton uploadButton = new JButton("<html><center>Upload<br>ingredient image</center></html>");
            uploadButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            uploadButton.setBounds(45, 260, 150, 95);
            contentPane.add(uploadButton);

            JLabel allergensLabel = new JLabel("Avoid allergens:");
            allergensLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            allergensLabel.setBounds(45, 360, 360, 25);
            contentPane.add(allergensLabel);


            // Allergen checkbox list
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
            allergenScroll.setBounds(45, 390, 360, 60);
            contentPane.add(allergenScroll);


            JLabel dietLabel = new JLabel("Diet:");
            dietLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            dietLabel.setBounds(45, 465, 360, 25);
            contentPane.add(dietLabel);

            // Vegan checkbox
            JCheckBox veganCheckBox = new JCheckBox("Vegan");
            veganCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
            veganCheckBox.setBackground(new Color(95, 158, 160));
            veganCheckBox.setBounds(45, 490, 100, 25);
            contentPane.add(veganCheckBox);

            // Vegetarian checkbox
            JCheckBox vegetarianCheckBox = new JCheckBox("Vegetarian");
            vegetarianCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
            vegetarianCheckBox.setBackground(new Color(95, 158, 160));
            vegetarianCheckBox.setBounds(225, 490, 120, 25);
            contentPane.add(vegetarianCheckBox);

            // File chooser for upload button
            uploadButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    uploadedImageFile = fileChooser.getSelectedFile();
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
