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
		setBounds(100, 100, 450, 550);
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
		sendButton.setBounds(45, 450, 360, 45);
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

        // Vegan checkbox
        JCheckBox veganCheckBox = new JCheckBox("Vegan");
        veganCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        veganCheckBox.setBackground(new Color(95, 158, 160));
        veganCheckBox.setBounds(45, 410, 100, 25);
        contentPane.add(veganCheckBox);

        // Vegetarian checkbox
        JCheckBox vegetarianCheckBox = new JCheckBox("Vegetarian");
        vegetarianCheckBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        vegetarianCheckBox.setBackground(new Color(95, 158, 160));
        vegetarianCheckBox.setBounds(45, 370, 120, 25);
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
            // Collect all input values from the UI
            String ingredientsText = ingredientsArea.getText();
            String amount = amountField.getText();
            String maxCalories = maxCaloriesField.getText();
            String minRating = minRatingField.getText();
            String maxTotalTime = maxTotalTimeField.getText();
            boolean isVegan = veganCheckBox.isSelected();
            boolean isVegetarian = vegetarianCheckBox.isSelected();

            // Send to agent (assuming these fields exist in the agent)
            a.ingredientsText = ingredientsText;
            a.uploadedImage = uploadedImageFile;
            a.amount = Integer.parseInt(amount);
            a.maxCalories = Integer.parseInt(maxCalories);
            a.minRating = Double.parseDouble(minRating);
            a.maxTotalTime = Integer.parseInt(maxTotalTime);
            a.vegan = isVegan;
            a.vegetarian = isVegetarian;

            // Wake up the agent
            a.doWake();
        });
	}

}
