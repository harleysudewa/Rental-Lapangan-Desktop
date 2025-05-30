package src;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import org.json.*;

public class CourtManagementPanel extends JPanel {
    public CourtManagementPanel() {
        setLayout(new BorderLayout());

        setOpaque(false);
        loadCourts();
    }

    private void loadCourts() {
        removeAll();
        setLayout(new BorderLayout());

        Font customFont = new Font("Arial", Font.BOLD, 14);

        JPanel courtsPanel = new JPanel();
        courtsPanel.setLayout(new BoxLayout(courtsPanel, BoxLayout.Y_AXIS));
        courtsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        courtsPanel.setOpaque(false);

        buildAddCourtForm(this);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/courts"))
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray courts = json.getJSONObject("response").getJSONArray("results");

                for (int i = 0; i < courts.length(); i++) {
                    JSONObject court = courts.getJSONObject(i);
                    int id = court.getInt("id");
                    String type = court.getString("court_type");
                    String open = court.getString("open_time");
                    String close = court.getString("close_time");
                    boolean active = court.getInt("is_active") == 1;

                    JPanel card = new JPanel(new GridLayout(0, 1));
                    TitledBorder border = BorderFactory.createTitledBorder("Court ID: " + id);
                    border.setTitleFont(customFont);
                    border.setTitleColor(Color.WHITE);
                    card.setBorder(border);
                    card.setOpaque(false);

                    String statusText = active
                        ? "<html>Status: <span style='color:#17fc03;'>Active</span></html>"
                        : "<html>Status: <span style='color:#fc0303;'>Inactive</span></html>";

                    JLabel typeLabel = new JLabel("Type: " + type);
                    JLabel openLabel = new JLabel("Open: " + open);
                    JLabel closeLabel = new JLabel("Close: " + close);
                    JLabel statusLabel = new JLabel(statusText);

                    typeLabel.setForeground(Color.WHITE);
                    openLabel.setForeground(Color.WHITE);
                    closeLabel.setForeground(Color.WHITE);
                    statusLabel.setForeground(Color.WHITE);
                    typeLabel.setFont(customFont);
                    openLabel.setFont(customFont);
                    closeLabel.setFont(customFont);
                    statusLabel.setFont(customFont);

                    card.add(typeLabel);
                    card.add(openLabel);
                    card.add(closeLabel);
                    card.add(statusLabel);

                    JButton statusBtn = new JButton(active ? "Deactivate" : "Activate");
                    statusBtn.setBackground(new Color(60, 63, 65));
                    statusBtn.setForeground(Color.BLACK);
                    statusBtn.setFont(customFont);
                    statusBtn.addActionListener(e -> toggleCourtStatus(id, !active));

                    JButton timeBtn = new JButton("Edit Hours");
                    timeBtn.setBackground(new Color(60, 63, 65));
                    timeBtn.setForeground(Color.BLACK);
                    timeBtn.setFont(customFont);
                    timeBtn.addActionListener(e -> updateCourtHours(id, open, close));

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setOpaque(false);
                    buttonPanel.add(statusBtn);
                    buttonPanel.add(timeBtn);

                    card.add(buttonPanel);
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                    courtsPanel.add(card);
                    courtsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                JScrollPane scrollPane = new JScrollPane(courtsPanel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                add(scrollPane, BorderLayout.CENTER);
            } else {
                JLabel errorLabel = new JLabel("Failed to retrieve courts data.");
                errorLabel.setForeground(Color.WHITE);
                add(errorLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error: " + e.getMessage());
            errorLabel.setForeground(Color.WHITE);
            add(errorLabel, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    private void buildAddCourtForm(JPanel parentPanel) {

        Font customFont = new Font("Arial", Font.BOLD, 14);
        Font customFont2 = new Font("SansSerif", Font.PLAIN, 12);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JTextField typeField = new JTextField(10);
        JTextField openField = new JTextField(5);
        JTextField closeField = new JTextField(5);
        typeField.setFont(customFont2);
        openField.setFont(customFont2);
        closeField.setFont(customFont2);
        JButton addBtn = new JButton("Add Court");
        addBtn.setBackground(new Color(60, 63, 65));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(customFont);

        JLabel typeLabel = new JLabel("Type:");
        JLabel openLabel = new JLabel("Open:");
        JLabel closeLabel = new JLabel("Close:");

        typeLabel.setForeground(Color.WHITE);
        openLabel.setForeground(Color.WHITE);
        closeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(customFont);
        openLabel.setFont(customFont);
        closeLabel.setFont(customFont);

        formPanel.add(typeLabel);
        formPanel.add(typeField);
        formPanel.add(openLabel);
        formPanel.add(openField);
        formPanel.add(closeLabel);
        formPanel.add(closeField);
        formPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            String type = typeField.getText();
            String open = openField.getText();
            String close = closeField.getText();

            if (type.isEmpty() || open.isEmpty() || close.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                HttpClient client = HttpClient.newHttpClient();
                String json = String.format("{\"court_type\":\"%s\",\"open_time\":\"%s\",\"close_time\":\"%s\"}", type, open, close);
                HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/add_courts"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JOptionPane.showMessageDialog(this, new JSONObject(response.body()).getString("message"));
                loadCourts();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        parentPanel.add(formPanel, BorderLayout.NORTH);
    }

    private void toggleCourtStatus(int courtId, boolean newStatus) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"is_active\": %b}", newStatus);
            HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/update_courts/" + courtId + "/status"))
                .header("Content-Type", "application/json")
                .method("PATCH", BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JOptionPane.showMessageDialog(this, new JSONObject(response.body()).getString("message"));
            loadCourts();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateCourtHours(int courtId, String currentOpen, String currentClose) {
        JTextField openField = new JTextField(currentOpen);
        JTextField closeField = new JTextField(currentClose);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Open:"));
        panel.add(openField);
        panel.add(new JLabel("Close:"));
        panel.add(closeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Operation Hours", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String json = String.format("{\"open_time\":\"%s\", \"close_time\":\"%s\"}", openField.getText(), closeField.getText());

                HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/update_courts/" + courtId + "/operation_hours"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", BodyPublishers.ofString(json))
                    .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JOptionPane.showMessageDialog(this, new JSONObject(response.body()).getString("message"));
                loadCourts();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private HttpRequest.Builder authorizedRequestBuilder(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
        if (Main.sessionCookie != null) {
            builder.header("Cookie", Main.sessionCookie);
        }
        return builder;
    }
}
