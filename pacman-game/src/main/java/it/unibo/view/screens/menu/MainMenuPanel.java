package it.unibo.view.screens.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.Border;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

class MainMenuPanel extends JPanel {
    private final static String TITLE = "Pacman";
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private static final float BUTTON_FONT_SIZE = 30f;
    private static final int THICKNESS_BORDER = 4;
    private static final Color BACKGROUND = Color.CYAN;

    private final JButton playButton = new JButton("Find Match");
    private final JButton statsButton = new JButton("My Stats");
    private final JButton logoutButton = new JButton("Logout");
    private final Border panelBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);

    public MainMenuPanel() {
        this.setBackground(BACKGROUND);
        this.setLayout(new GridBagLayout());
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        titlePanel.setBackground(BACKGROUND);
        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.BLACK);
        title.setFont(FontManager.addingFont(80f, FONT_NAME));
        titlePanel.add(title);

        GridBagConstraints titleCons = new GridBagConstraints();
        titleCons.fill = GridBagConstraints.HORIZONTAL;
        titleCons.anchor = GridBagConstraints.PAGE_START;
        titleCons.gridwidth = 3;
        titleCons.gridheight = 1;
        titleCons.weightx = 1;
        this.add(titlePanel, titleCons);

        emptyPanel(0, 1);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        manageButtonFeatures(playButton, buttonPanel);
        manageButtonFeatures(statsButton, buttonPanel);
        manageButtonFeatures(logoutButton, buttonPanel);

        GridBagConstraints containerCons = new GridBagConstraints();
        containerCons.fill = GridBagConstraints.BOTH;
        containerCons.anchor = GridBagConstraints.CENTER;
        containerCons.gridx = 1;
        containerCons.gridy = 1;
        containerCons.gridwidth = 1;
        containerCons.gridheight = 1;
        containerCons.weightx = 1;
        containerCons.weighty = 0.8;
        this.add(buttonPanel, containerCons);

        emptyPanel(2, 1);
        emptyPanel(1, 2);
    }

    public void setOnPlay(Runnable action) {
        playButton.addActionListener(_ -> action.run());
    }

    public void setOnStats(Runnable action) {
        statsButton.addActionListener(_ -> action.run());
    }

    public void setOnLogout(Runnable action) {
        logoutButton.addActionListener(_ -> action.run());
    }

    public ReconnectionChoice showReconnectionDialog() {
        Object[] options = {"Reconnect", "Decline"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "You have a match ongoing!\nWould you like to reconnect or abandon it?",
                "Ongoing Match Found",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                null
        );
        return choice == JOptionPane.YES_OPTION ? ReconnectionChoice.RECONNECT : ReconnectionChoice.DECLINE;
    }

    private void manageButtonFeatures(JButton button, JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(panelBorder);
        panel.setBackground(BACKGROUND);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        button.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.ORANGE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        panel.add(button);
        container.add(panel);
    }

    private void emptyPanel(int gridx, int gridy) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(BACKGROUND);
        GridBagConstraints emptyCons = new GridBagConstraints();
        emptyCons.fill = GridBagConstraints.BOTH;
        emptyCons.gridx = gridx;
        emptyCons.gridy = gridy;
        emptyCons.gridwidth = 1;
        emptyCons.gridheight = 1;
        emptyCons.weightx = 1;
        emptyCons.weighty = 0.2;
        this.add(emptyPanel, emptyCons);
    }

    public enum ReconnectionChoice {
        RECONNECT,
        DECLINE
    }
}