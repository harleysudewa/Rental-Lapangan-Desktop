package src;

import javax.swing.*;

public class Main {
    static String sessionCookie = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}