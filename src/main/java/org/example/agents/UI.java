package org.example.agents;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	public JTextField dietField;
    public JTextField amountField;
    public JTextField maxCaloriesField;
    public JTextField minRatingField;
    public JTextField maxTotalTimeField;
    public JButton sendButton;

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
		setTitle("Recipe Finder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(95, 158, 160));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		dietField = new JTextField("vegan");
		dietField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		dietField.setBounds(225, 15, 180, 25);
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

        JLabel label = new JLabel("Dish type:");
        label.setLabelFor(dietField);
        label.setBackground(UIManager.getColor("TextArea.selectionBackground"));
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        label.setBounds(45, 15, 180, 25);
        contentPane.add(label);
        contentPane.add(dietField);

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
		sendButton.setBounds(100, 215, 250, 45);
		contentPane.add(sendButton);
		
		//sendButton.addActionListener(new ActionListener() {
			//TODO
		//});
	}

}
