package src;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;

public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterFrame() {
        setTitle("Sportiva Manager | Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        Font customFont = new Font("Arial", Font.BOLD, 14);
        Font customFont2 = new Font("SansSerif", Font.PLAIN, 12);

        BackgroundPanel backgroundPanel = new BackgroundPanel("resources/blur-bg.png");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);

        ImageIcon logoIcon = new ImageIcon("resources/sportiva_manager_transparent.png");
        Image scaledImg = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridwidth = 2;
        panel.add(logoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(customFont);
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridy++;
        emailField = new JTextField();
        emailField.setFont(customFont2);
        panel.add(emailField, gbc);

        gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(customFont);
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setFont(customFont2);
        panel.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(customFont);
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setFont(customFont2);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        registerButton.setFont(customFont);
        backButton.setFont(customFont);
        registerButton.setPreferredSize(new Dimension(120, 35));
        backButton.setPreferredSize(new Dimension(120, 35));

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, gbc);

        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        backgroundPanel.add(panel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void register() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"email\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", email, username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:6969/register_manager"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                JOptionPane.showMessageDialog(this, "Register successful! Please login.");
                dispose();
                new LoginFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Register failed: " + new JSONObject(response.body()).getString("message"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
