package src;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import org.json.JSONObject;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        ImageIcon logoIcon = new ImageIcon("resources/sportiva_manager_transparent.png");
        Image scaledImg = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(welcomeLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(logoLabel);
        add(Box.createVerticalGlue());

        // Background fetch username
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/profile"))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());
                    JSONObject responseData = json.getJSONObject("response");
                    String username = responseData.getString("username");

                    SwingUtilities.invokeLater(() -> {
                        welcomeLabel.setText("Welcome, " + username + "!");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        welcomeLabel.setText("Failed to load profile.");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    welcomeLabel.setText("An error occurred.");
                });
            }
        }).start();
    }

    private HttpRequest.Builder authorizedRequestBuilder(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
        if (Main.sessionCookie != null) {
            builder.header("Cookie", Main.sessionCookie);
        }
        return builder;
    }
}