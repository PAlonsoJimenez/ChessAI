package chessai;

import chessai.Swing.SwingInterface;
import chessai.Swing.SwingOptionMenu;
import chessai.Swing.Worker;

/**
 *
 * @author Pablo Alonso
 */
public class Referee {
    
    private final Game game;
    private final EnemyAI AI;
    private boolean AIturn;
    private boolean gameEnded;
    
    public Referee(){
        OptionMenu menu = new SwingOptionMenu();
        SwingInterface swingInterface = new SwingInterface(menu.getChosenColor(), menu.getTimeInSeconds());
        game = new Game(swingInterface, this);
        AI = new EnemyAI(game, !menu.getChosenColor());
        AIturn = false;
        gameEnded = false;
        swingInterface.startWhiteThread();
        if(!menu.getChosenColor()) nextTurn();
    }
    
    public boolean getAIColor(){
        return AI.getColor();
    }
    
    public void gameEnded(){
        gameEnded = true;
    }
    
    public void nextTurn(){
        if(gameEnded) return;
        AIturn = !AIturn;
        if(AIturn){
            Worker worker = new Worker(AI);
            worker.execute();
        }
    }
}
