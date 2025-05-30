package src;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;
import java.util.List;

public class BookingsPanel extends JPanel {

    public BookingsPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        loadAvailability();
    }

    private void loadAvailability() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/availability"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray availability = new JSONObject(response.body())
                        .getJSONObject("response")
                        .getJSONArray("availability");

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setOpaque(false);
                contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

                Map<String, List<JSONObject>> groupedByCourt = new LinkedHashMap<>();
                for (int i = 0; i < availability.length(); i++) {
                    JSONObject item = availability.getJSONObject(i);
                    String courtType = item.getString("court_type");

                    groupedByCourt.putIfAbsent(courtType, new ArrayList<>());
                    groupedByCourt.get(courtType).add(item);
                }

                for (Map.Entry<String, List<JSONObject>> entry : groupedByCourt.entrySet()) {
                    String courtType = entry.getKey();
                    List<JSONObject> courtAvailabilities = entry.getValue();

                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setOpaque(false);

                    TitledBorder titledBorder = BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(Color.WHITE, 1),
                            courtType,
                            TitledBorder.LEFT,
                            TitledBorder.TOP,
                            new Font("Arial", Font.BOLD, 16),
                            Color.WHITE
                    );
                    card.setBorder(BorderFactory.createCompoundBorder(
                            titledBorder,
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));

                    for (JSONObject slot : courtAvailabilities) {
                        String rawDate = slot.getString("date");
                        JSONArray availableTimes = slot.getJSONArray("available_times");
                        String openTime = slot.getString("open_time");
                        String closeTime = slot.getString("close_time");

                        int openHour = Integer.parseInt(openTime.split(":")[0]);
                        int closeHour = Integer.parseInt(closeTime.split(":")[0]);

                        Date date = inputFormat.parse(rawDate);
                        String formattedDate = outputFormat.format(date);

                        JLabel dateLabel = new JLabel("📅 " + formattedDate);
                        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        dateLabel.setForeground(Color.WHITE);
                        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        card.add(dateLabel);

                        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                        timePanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
                        timePanel.setOpaque(false);
                        timePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                        for (int hour = openHour; hour < closeHour; hour++) {
                            String timeSlot = String.format("%02d:00", hour);
                            JLabel timeLabel = new JLabel(timeSlot);
                            timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                            timeLabel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                                BorderFactory.createEmptyBorder(2, 6, 2, 6) // padding dalam label dikurangi
                            ));

                            if (availableTimes.toList().contains(timeSlot)) {
                                timeLabel.setForeground(Color.WHITE); // tersedia
                            } else {
                                timeLabel.setForeground(Color.RED);   // terbooking
                            }

                            timePanel.add(timeLabel);
                        }

                        card.add(timePanel);
                        card.add(Box.createVerticalStrut(5));
                    }

                    contentPanel.add(Box.createVerticalStrut(10));
                    contentPanel.add(card);
                }

                JScrollPane scrollPane = new JScrollPane(contentPanel);
                scrollPane.setBorder(null);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                add(scrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();

            } else {
                add(new JLabel("Failed to fetch availability."), BorderLayout.CENTER);
            }

        } catch (Exception e) {
            e.printStackTrace();
            add(new JLabel("Error: " + e.getMessage()), BorderLayout.CENTER);
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