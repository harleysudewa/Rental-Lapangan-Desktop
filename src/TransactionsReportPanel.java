package src;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.*;

import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

public class TransactionsReportPanel extends JPanel {

    public TransactionsReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false); // transparan agar bisa taruh di BackgroundPanel
        loadTransactions();
    }

    private void loadTransactions() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/transactions_report")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            HttpRequest incomeRequest = authorizedRequestBuilder(new URI("http://localhost:6969/total_income")).GET().build();
            HttpResponse<String> incomeResponse = client.send(incomeRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && incomeResponse.statusCode() == 200) {
                JSONArray transactions = new JSONObject(response.body()).getJSONObject("response").getJSONArray("results");
                String totalIncome = new JSONObject(incomeResponse.body()).getJSONObject("response").get("total_income").toString();

                String[] columnNames = {"Trx ID", "Date", "Time", "Duration", "Price", "Payment Method"};
                Object[][] data = new Object[transactions.length()][columnNames.length];

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                SimpleDateFormat localFormat = new SimpleDateFormat("dd MMM yyyy");

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                for (int i = 0; i < transactions.length(); i++) {
                    JSONObject t = transactions.getJSONObject(i);

                    String rawDate = t.getString("booking_date");
                    Date date = isoFormat.parse(rawDate);
                    String formattedDate = localFormat.format(date);

                    double amount = t.getDouble("payment_amount");
                    String formattedAmount = currencyFormat.format(amount);

                    String durationWithUnits = t.get("booking_duration") + " menit";

                    data[i][0] = t.get("id");
                    data[i][1] = formattedDate;
                    data[i][2] = t.get("booking_time");
                    data[i][3] = durationWithUnits;
                    data[i][4] = formattedAmount;
                    data[i][5] = t.get("payment_method");
                }

                JTable table = new JTable(data, columnNames);
                table.setOpaque(false);
                table.setBackground(new Color(0, 0, 0, 0));
                table.setForeground(Color.WHITE); // teks putih
                table.setFont(new Font("Arial", Font.PLAIN, 13));
                table.setGridColor(Color.LIGHT_GRAY);
                table.setShowGrid(true);

                // Header styling
                JTableHeader header = table.getTableHeader();
                header.setForeground(Color.WHITE);
                header.setBackground(new Color(0, 0, 0, 100));
                header.setFont(new Font("Arial", Font.BOLD, 13));
                header.setOpaque(false);

                // Set column widths
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.getColumnModel().getColumn(0).setPreferredWidth(70);
                table.getColumnModel().getColumn(1).setPreferredWidth(120);
                table.getColumnModel().getColumn(2).setPreferredWidth(120);
                table.getColumnModel().getColumn(3).setPreferredWidth(120);
                table.getColumnModel().getColumn(4).setPreferredWidth(130);
                table.getColumnModel().getColumn(5).setPreferredWidth(130);

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(300, 300));
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setBorder(new EmptyBorder(10, 30, 10, 30)); // beri margin kiri-kanan juga

                JLabel titleLabel = new JLabel("Transactions Report");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                titleLabel.setForeground(Color.WHITE);

                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                topPanel.setOpaque(false);
                topPanel.add(titleLabel, BorderLayout.CENTER);

                JLabel incomeLabel = new JLabel("Total Income: " + currencyFormat.format(Double.parseDouble(totalIncome)));
                incomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
                incomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                incomeLabel.setForeground(Color.WHITE);

                JPanel bottomPanel = new JPanel(new BorderLayout());
                bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                bottomPanel.setOpaque(false);
                bottomPanel.add(incomeLabel, BorderLayout.CENTER);

                add(topPanel, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
                add(bottomPanel, BorderLayout.SOUTH);

            } else {
                JLabel errorLabel = new JLabel("Failed to retrieve transactions or income data.");
                errorLabel.setForeground(Color.WHITE);
                add(errorLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error: " + e.getMessage());
            errorLabel.setForeground(Color.WHITE);
            add(errorLabel, BorderLayout.CENTER);
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