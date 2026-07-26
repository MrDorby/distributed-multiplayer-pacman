package it.unibo.view.screens.matchmaker;

import it.unibo.view.screens.matchmaker.panels.MatchmakerFailurePanel;
import it.unibo.view.screens.matchmaker.panels.MatchmakerMenuPanel;
import it.unibo.view.screens.matchmaker.panels.MatchmakerSearchingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MatchmakerScreen extends JPanel {
    private static final String MENU_CARD = "Menu";
    private static final String SEARCHING_CARD = "Searching";
    private static final String FAILURE_CARD = "Failure";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final MatchmakerMenuPanel menuPanel;
    private final MatchmakerSearchingPanel searchingPanel;
    private final MatchmakerFailurePanel failurePanel;

    public MatchmakerScreen(List<String> maps) {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        this.menuPanel = new MatchmakerMenuPanel(maps);
        this.searchingPanel = new MatchmakerSearchingPanel();
        this.failurePanel = new MatchmakerFailurePanel();
        cardPanel.setOpaque(false);
        cardPanel.add(menuPanel, MENU_CARD);
        cardPanel.add(searchingPanel, SEARCHING_CARD);
        cardPanel.add(failurePanel, FAILURE_CARD);
        add(cardPanel);
        showMenuView();
    }

    public MatchmakerMenuPanel getMenuPanel() {
        return menuPanel;
    }

    public MatchmakerSearchingPanel getSearchingPanel() {
        return searchingPanel;
    }

    public MatchmakerFailurePanel getFailurePanel() {
        return failurePanel;
    }

    public void showMenuView() {
        cardLayout.show(cardPanel, MENU_CARD);
    }

    public void showSearchingView() {
        cardLayout.show(cardPanel, SEARCHING_CARD);
    }

    public void showFailureView(String errorMessage) {
        failurePanel.setErrorText(errorMessage);
        cardLayout.show(cardPanel, FAILURE_CARD);
    }
}