package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Rook extends Piece{

    private boolean hasAlreadyMoved;
    
    public Rook(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 5, gameItBelongsTo);
        hasAlreadyMoved = false;
    }
    
    public boolean hasAlredyMoved(){
        return hasAlreadyMoved;
    }
    
    @Override
    public void setPosition(int position) {
        super.setPosition(position);
        hasAlreadyMoved = true;
    }
    
    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        //Up
        int wall = 0;
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
        Rook clone = new Rook(color, clonePosition, gameItBelongsTo);
        clone.hasAlreadyMoved = hasAlreadyMoved;
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }
    
}
