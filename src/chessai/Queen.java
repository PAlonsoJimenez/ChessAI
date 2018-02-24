package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Queen extends Piece{

    public Queen(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 9, gameItBelongsTo);
    }
    
    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        //Same as Bishop and Rook
        //Upper Right
        int wall = 0;
        for(int i = 1; i<8; i++){
            if(position[0]-i < 0 || position[1]+i > 7 || wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]-i,position[1]+i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Upper Left
        wall = 0;        
        for(int i = 1; i<8; i++){
            if(position[0]-i < 0 || position[1]-i < 0 || wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]-i,position[1]-i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Lower Right
        wall = 0;
        for(int i = 1; i<8; i++){
            if(position[0]+i > 7|| position[1]+i > 7|| wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]+i,position[1]+i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Lower Left
        wall = 0;
        for(int i = 1; i<8; i++){
            if(position[0]+i > 7||position[1]-i < 0|| wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]+i,position[1]-i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Up
        wall = 0;
        for(int i = 1; i<8; i++){
            if(position[0]-i < 0 || wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]-i,position[1]};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Down
        wall = 0;        
        for(int i = 1; i<8; i++){
            if(position[0]+i > 7|| wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0]+i,position[1]};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Left
        wall = 0;
        for(int i = 1; i<8; i++){
            if(position[1]-i < 0|| wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0],position[1]-i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
        
        //Right
        wall = 0;
        for(int i = 1; i<8; i++){
            if(position[1]+i > 7|| wall > 0){
                break;
            }
            int[] nextMove = new int[]{position[0],position[1]+i};
            wall = possibleMoveOrPossibleDefend(nextMove, wall);
        }
    }
    
    private int possibleMoveOrPossibleDefend (int[] nextMove, int wall){
        if(!playing.isSquareOcuppied(nextMove)){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
        }else if(playing.isThisSquareOcuppiedByAWhitePiece(nextMove)!= color){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
            return ++wall;
        }else{
            defendedPiecesPositions.add(Position.getAbsolutePosition(nextMove));
            return ++wall;
        }
        return wall;
    }
    
    @Override
    public Piece clone(Game gameItBelongsTo) {
        int[] clonePosition = new int []{position[0], position[1]};
        Queen clone = new Queen(color, clonePosition, gameItBelongsTo);
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }
    
}
