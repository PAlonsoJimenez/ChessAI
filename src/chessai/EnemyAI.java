package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class EnemyAI {
    
    private final Game playing;
    private final boolean color;
    
    public EnemyAI(Game playing, boolean color){
        this.playing = playing;
        this.color = color;
    }
    
    public boolean getColor(){
        return color;
    }
    
    public void yourTurn(){
        int[] nextMove = SearchTree.getNextMove(playing, color);
        playing.move(nextMove[0], nextMove[1], true);
    }
}
