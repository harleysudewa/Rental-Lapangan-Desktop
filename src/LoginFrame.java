package src;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Sportiva Manager | Login");
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

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        ImageIcon logoIcon = new ImageIcon("resources/sportiva_manager_transparent.png");
        Image scaledImg = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(logoLabel, gbc);
        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Username or Email:");
        userLabel.setFont(customFont);
        userLabel.setForeground(Color.WHITE);
        gbc.gridy++;
        panel.add(userLabel, gbc);

        usernameField.setFont(customFont2);
        usernameField.setBackground(Color.WHITE);
        gbc.gridy++;
        panel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(customFont);
        passLabel.setForeground(Color.WHITE);
        gbc.gridy++;
        panel.add(passLabel, gbc);

        passwordField.setFont(customFont2);
        passwordField.setBackground(Color.WHITE);
        gbc.gridy++;
        panel.add(passwordField, gbc);

        gbc.gridy++;
        JPanel loginButtonPanel = new JPanel();
        loginButton.setFont(customFont);
        loginButton.setPreferredSize(new Dimension(180, 35));
        loginButtonPanel.add(loginButton);
        loginButtonPanel.setOpaque(false);
        panel.add(loginButtonPanel, gbc);
        loginButton.addActionListener(e -> login());

        gbc.gridy++;
        JLabel noAccountLabel = new JLabel("Don't have a Manager Account? Register below.");
        noAccountLabel.setFont(customFont2);
        noAccountLabel.setForeground(Color.WHITE);
        panel.add(noAccountLabel, gbc);

        gbc.gridy++;
        JPanel registerButtonPanel = new JPanel();
        registerButton.setFont(customFont);
        registerButton.setPreferredSize(new Dimension(180, 35));
        registerButtonPanel.add(registerButton);
        registerButtonPanel.setOpaque(false);
        panel.add(registerButtonPanel, gbc);
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        gbc.gridy++;
        JPanel forgotPasswordButtonPanel = new JPanel();
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(customFont);
        forgotPasswordButton.setPreferredSize(new Dimension(180, 35));
        forgotPasswordButtonPanel.add(forgotPasswordButton);
        forgotPasswordButtonPanel.setOpaque(false);
        panel.add(forgotPasswordButtonPanel, gbc);
        forgotPasswordButton.addActionListener(e -> {
            dispose();
            new ForgotPasswordFrame();
        });

        backgroundPanel.add(panel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:6969/login_manager"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Optional<String> setCookie = response.headers().firstValue("set-cookie");
                setCookie.ifPresent(cookie -> Main.sessionCookie = cookie.split(";", 2)[0]);
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new DashboardFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Login failed: " + new JSONObject(response.body()).getString("message"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}