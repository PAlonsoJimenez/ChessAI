package chessai;

import java.util.ArrayList;

/**
 *
 * @author Pablo Alonso
 */
public abstract class Piece {
    
    //True: White; False: Black
    protected final boolean color;
    protected int[] position;
    protected final int value;
    protected final Game playing;
    protected ArrayList<Integer> possibleMovements;
    protected ArrayList<Integer> defendedPiecesPositions;
    
    public Piece(boolean color, int[] position,  int value, Game gameItBelongsTo){
        this.color = color;
        this.position = position;
        this.value = value;
        playing = gameItBelongsTo;
        possibleMovements = new ArrayList<>();
        defendedPiecesPositions = new ArrayList<>();
    }

    public boolean isWhite() {
        return color;
    }

    public int[] getPosition() {
        return position;
    }

    public int getValue() {
        return value;
    }

    public ArrayList<Integer> getPossibleMovements() {
        return possibleMovements;
    }

    public ArrayList<Integer> getDefendedPiecesPositions() {
        return defendedPiecesPositions;
    }
    
    public void setPosition(int position) {
        this.position = Position.getRelativePosition(position);
    }
    
    protected ArrayList<Integer> getClonedMovementList(){
        ArrayList<Integer> cloned = new ArrayList<>();
        for (int move : possibleMovements) {
            cloned.add(move);
        }
        return cloned;
    }
    
    protected ArrayList<Integer> getClonedDefendedPiecesPositions(){
        ArrayList<Integer> cloned = new ArrayList<>();
        for (Integer defendedPiecePosition : defendedPiecesPositions) {
            cloned.add(defendedPiecePosition);
        }
        return cloned;
    }

    public abstract Piece clone(Game gameItBelongsTo);
    
    protected abstract void calculateNewPossibleMovements();
    
}
