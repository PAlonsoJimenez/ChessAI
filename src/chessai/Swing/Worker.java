package chessai.Swing;

import chessai.EnemyAI;
import javax.swing.SwingWorker;

/**
 *
 * @author Pablo Alonso
 */
public class Worker extends SwingWorker<Void, Void>{
    
    private final EnemyAI AI;
    
    public Worker(EnemyAI AI){
        this.AI = AI;
    }

    @Override
    protected Void doInBackground() throws Exception {
        AI.yourTurn();
        return null;
    }
    
}
