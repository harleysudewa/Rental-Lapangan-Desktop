package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardFrame extends JFrame {

    BackgroundPanel backgroundPanel = new BackgroundPanel("resources/blur-bg.png");

    private JPanel contentPanel;

    public DashboardFrame() {
        setTitle("Sportiva Manager | Dashboard");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuHome = new JMenu("Home");
        JMenu menuCourts = new JMenu("Courts Management");
        JMenu menuTransactions = new JMenu("Transactions Report");
        JMenu menuBookings = new JMenu("Bookings");
        JMenu menuLogout = new JMenu("Logout");

        menuBar.add(menuHome);
        menuBar.add(menuCourts);
        menuBar.add(menuTransactions);
        menuBar.add(menuBookings);
        menuBar.add(menuLogout);

        setJMenuBar(menuBar);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel);

        menuHome.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showHome();
            }
        });

        menuCourts.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showCourts();
            }
        });

        menuTransactions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showTransactions();
            }
        });

        menuBookings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showBookings();
            }
        });

        menuLogout.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });

        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
        contentPanel.setOpaque(false);
        setVisible(true);
        showHome();
    }

    private void showHome() {
        contentPanel.removeAll();
        contentPanel.add(new HomePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCourts() {
        contentPanel.removeAll();
        contentPanel.add(new CourtManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
}

    private void showTransactions() {
        contentPanel.removeAll();
        contentPanel.add(new TransactionsReportPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
    }

    private void showBookings() {
        contentPanel.removeAll();
        contentPanel.add(new BookingsPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = authorizedRequestBuilder(new URI("http://localhost:6969/logout"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
                    HttpClient client = HttpClient.newHttpClient();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Logout successful.");
                        Main.sessionCookie = null;
                        dispose();
                        new LoginFrame().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Logout failed: " + response.body());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error during logout: " + e.getMessage());
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