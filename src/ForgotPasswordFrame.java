package src;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ForgotPasswordFrame extends JFrame {
    private JTextField emailField;

    public ForgotPasswordFrame() {
        setTitle("Sportiva Manager | Forgot Password");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        Font customFont = new Font("Arial", Font.BOLD, 14);
        Font customFont2 = new Font("SansSerif", Font.PLAIN, 12);

        BackgroundPanel backgroundPanel = new BackgroundPanel("resources/blur-bg.png");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Enter your registered email:");
        label.setFont(customFont);
        label.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        emailField = new JTextField();
        emailField.setFont(customFont2);
        gbc.gridy++;
        panel.add(emailField, gbc);

        gbc.gridy++;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");
        submitButton.setFont(customFont);
        submitButton.setPreferredSize(new Dimension(120, 35));
        backButton.setFont(customFont);
        backButton.setPreferredSize(new Dimension(120, 35));

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        panel.add(buttonPanel, gbc);
        
        submitButton.addActionListener(e -> submitEmail());
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        backgroundPanel.add(panel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void submitEmail() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"email\":\"%s\"}", email);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:6969/send_reset_password"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Password reset instructions sent to your email.");
                dispose();
                new LoginFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Failed: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}