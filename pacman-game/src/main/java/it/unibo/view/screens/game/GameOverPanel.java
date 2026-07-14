package it.unibo.view.screens.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

public class GameOverPanel {
    
    private static final Dimension FRAME_DIMENSION = new Dimension(900, 600);
    private static final int THICKNESS = 5;
    private final Border border = BorderFactory.createEmptyBorder(40, 140, 40, 140);
    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel(new GridLayout(3, 1));
    private final JPanel separator = new JPanel();

    public GameOverPanel(Runnable onExitRunnable) {
        this.frame.setLayout(new BorderLayout());
        this.frame.setResizable(false);
        this.frame.setSize(FRAME_DIMENSION);
        this.frame.setLocationRelativeTo(null);
        this.panel.setBackground(Color.BLACK);

        JLabel title = new JLabel("Game Over");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setOpaque(false);
        title.setFont(FontManager.addingFont(50f, FontName.S2P.getFontName()));
        JButton button = new JButton("Home");
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setFont(FontManager.addingFont(40f, FontName.S2P.getFontName()));
        //button.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS, false));
        button.addActionListener(_ -> onExitRunnable.run());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //this.panel.setBorder(border);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(border);
        buttonPanel.setOpaque(false);
        buttonPanel.add(button);
        this.panel.add(title);
        this.separator.setOpaque(false);
        // this.panel.add(this.separator);
        this.panel.add(buttonPanel);
        frame.getContentPane().add(panel);
        this.frame.setVisible(true);
    }
}
