package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Bishop extends Piece{
    
    public Bishop(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 3, gameItBelongsTo);
    }

    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        //Upper Right
        int wall = 0;
        for(int i = 1; i<8; i++){
            //When we run out of chess board, or when we find something in the way
            if(position[0]-i < 0 || position[1]+i > 7 || wall > 0){
                break;
            }
            /*
            if the square is free, it is added as a new move.
            if not, and the piece ocuping that square is the opposite color,
            it is added as a new move and seted a wall.
            */
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
    }
    
    private int possibleMoveOrPossibleDefend (int[] nextMove, int wall){
        if(!playing.isSquareOcuppied(nextMove)){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
        }else if(playing.isThisSquareOcuppiedByAWhitePiece(nextMove)!= color){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
            //Just learn that I can't return wall++
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
        Bishop clone = new Bishop(color, clonePosition, gameItBelongsTo);
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }
    
}
