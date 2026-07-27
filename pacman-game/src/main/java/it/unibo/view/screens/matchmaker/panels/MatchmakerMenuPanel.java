package it.unibo.view.screens.matchmaker.panels;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;

import java.awt.*;
import java.util.List;

public class MatchmakerMenuPanel extends JPanel {

    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float LABEL_FONT_SIZE = 16f;
    private static final float INPUT_FONT_SIZE = 14f;
    private static final float BUTTON_FONT_SIZE = 14f;
    private static final int THICKNESS = 2;

    private final JLabel selectMapLabel = new JLabel("Map you want to play:");
    private final JComboBox<String> gameMapsDropdown = new JComboBox<>();
    private final JButton queueButton = new JButton("Queue");
    private final JButton backButton = new JButton("Go Back");

    public MatchmakerMenuPanel(List<String> maps) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        selectMapLabel.setFont(FontManager.addingFont(LABEL_FONT_SIZE, FONT_NAME));
        gameMapsDropdown.setFont(FontManager.addingFont(INPUT_FONT_SIZE, FONT_NAME));
        gameMapsDropdown.setBackground(Color.WHITE);
        gameMapsDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS));
        for (String map : maps) {
            gameMapsDropdown.addItem(map);
        }
        styleButton(queueButton);
        styleButton(backButton);
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        dropdownPanel.add(selectMapLabel);
        dropdownPanel.add(gameMapsDropdown);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(queueButton);
        buttonPanel.add(backButton);
        add(dropdownPanel);
        add(buttonPanel);
    }

    private void styleButton(JButton button) {
        button.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, THICKNESS),
                BorderFactory.createEmptyBorder(15, 25, 15, 25))
        );
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public String getSelectedMap() {
        return (String) gameMapsDropdown.getSelectedItem();
    }

    public void setOnQueue(Runnable action) {
        queueButton.addActionListener(_ -> action.run());
    }

    public void setOnGoBack(Runnable action) {
        backButton.addActionListener(_ -> action.run());
    }
}