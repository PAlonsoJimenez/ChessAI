package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Knight extends Piece{

    public Knight(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 3, gameItBelongsTo);
    }
    
    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        //Upper Left
        int [] nextMove = new int[]{position[0]-2,position[1]-1};
        if(position[0]-2 >= 0 && position[1]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Upper Right
        nextMove = new int[]{position[0]-2,position[1]+1};
        if(position[0]-2 >= 0 && position[1]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Right Upper
        nextMove = new int[]{position[0]-1,position[1]+2};
        if(position[0]-1 >= 0 && position[1]+2 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Right Lower
        nextMove = new int[]{position[0]+1,position[1]+2};
        if(position[0]+1 <= 7 && position[1]+2 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Lower Right
        nextMove = new int[]{position[0]+2,position[1]+1};
        if(position[0]+2 <= 7 && position[1]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Lower Left
        nextMove = new int[]{position[0]+2,position[1]-1};
        if(position[0]+2 <= 7 && position[1]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Left Lower
        nextMove = new int[]{position[0]+1,position[1]-2};
        if(position[0]+1 <= 7 && position[1]-2 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Left Upper
        nextMove = new int[]{position[0]-1,position[1]-2};
        if(position[0]-1 >= 0 && position[1]-2 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
    }
    
    private void possibleMoveOrPossibleDefend (int[] nextMove){
        if((!playing.isSquareOcuppied(nextMove) || playing.isThisSquareOcuppiedByAWhitePiece(nextMove) != color)){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
        }else{
            defendedPiecesPositions.add(Position.getAbsolutePosition(nextMove));
        }
    }
    
    @Override
    public Piece clone(Game gameItBelongsTo) {
        int[] clonePosition = new int []{position[0], position[1]};
        Knight clone = new Knight(color, clonePosition, gameItBelongsTo);
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }
    
}
