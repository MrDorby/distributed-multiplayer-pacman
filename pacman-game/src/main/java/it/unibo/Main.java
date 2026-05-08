package it.unibo;

import it.unibo.model.game.GameContextFactory;
import it.unibo.view.GameView;
import it.unibo.view.StartView;

public class Main {
    static void main() {
        IO.println("Hello and welcome!");
        for (int i = 1; i <= 5; i++) {
            IO.println("i = " + i);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartView().setVisible();
                //new GameView(GameContextFactory.getTestContext()).show();
            }
        });
    }
}
