package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Pawn extends Piece{
    
    private boolean hasAlreadyMoved;
    private boolean hasJustMovedTwoStepForward;

    public Pawn(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 1, gameItBelongsTo);
        hasAlreadyMoved = false;
        hasJustMovedTwoStepForward = false;
    }

    public boolean hasJustMovedTwoStepForward() {
        return hasJustMovedTwoStepForward;
    }
    
    public void cancelCaptureEnPassantChance(){
        hasJustMovedTwoStepForward = false;
    }
    
    @Override
    public void setPosition(int position) {
        int oldAbsolutePosition = Position.getAbsolutePosition(this.position);
        int oldPositionColumn = this.position[1];
        super.setPosition(position);
        hasAlreadyMoved = true;
        
        if(position - oldAbsolutePosition == 16 || position - oldAbsolutePosition == -16) hasJustMovedTwoStepForward = true;
        
        int oppositeDirection = color? 1 :  -1;
        if((this.position[1] - oldPositionColumn == 1 || this.position[1] - oldPositionColumn == -1) && !playing.isSquareOcuppied(this.position))
            playing.captureEnPassant(new int[]{this.position[0] + 1*oppositeDirection, this.position[1]}, !color);
        
        if(this.position[0] == 0 || this.position[0] == 7) playing.pawnPromotion(oldAbsolutePosition, position, color);
    }
    
    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        int direction = color? -1 :  1;
        
        //Two step forward
        int[] nextMove = new int[]{position[0]+2*direction, position[1]};
        if(!hasAlreadyMoved &&
                !playing.isSquareOcuppied(nextMove) &&
                !playing.isSquareOcuppied(new int[]{position[0]+1*direction, position[1]})){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
        }
        
        //One step forward
        //Theoretically, a pawn can never access outside the board, going straight forward, since it will be promoted when arriving to row 0 or 7
        nextMove = new int[]{position[0]+1*direction, position[1]};
        if(!playing.isSquareOcuppied(nextMove)) possibleMovements.add(Position.getAbsolutePosition(nextMove));
        
        //One step Upper Right (depend on the view)
        nextMove = new int[]{position[0]+1*direction, position[1]+1};
        if(nextMove[1] <= 7 && nextMove[1] >= 0){
            if (playing.isSquareOcuppied(nextMove)) {
                if (playing.isThisSquareOcuppiedByAWhitePiece(nextMove) != color) {
                    possibleMovements.add(Position.getAbsolutePosition(nextMove));
                } else {
                    defendedPiecesPositions.add(Position.getAbsolutePosition(nextMove));
                }
            } else {
                //En passant
                if (playing.checkForValidEnPassantMovement(Position.getAbsolutePosition(nextMove), direction * -1)) {
                    possibleMovements.add(Position.getAbsolutePosition(nextMove));
                }
            }
        }
        
        //One step Upper Left (depend on the view)
        nextMove = new int[]{position[0]+1*direction, position[1]-1};
        if(nextMove[1] <= 7 && nextMove[1] >= 0){
            if (playing.isSquareOcuppied(nextMove)) {
                if (playing.isThisSquareOcuppiedByAWhitePiece(nextMove) != color) {
                    possibleMovements.add(Position.getAbsolutePosition(nextMove));
                } else {
                    defendedPiecesPositions.add(Position.getAbsolutePosition(nextMove));
                }
            } else {
                //En passant
                if (playing.checkForValidEnPassantMovement(Position.getAbsolutePosition(nextMove), direction * -1)) {
                    possibleMovements.add(Position.getAbsolutePosition(nextMove));
                }
            }
        }
    }    

    @Override
    public Piece clone(Game gameItBelongsTo) {
        int[] clonePosition = new int []{position[0], position[1]};
        Pawn clone = new Pawn(color, clonePosition, gameItBelongsTo);
        clone.hasAlreadyMoved = hasAlreadyMoved;
        clone.hasJustMovedTwoStepForward = hasJustMovedTwoStepForward;
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }
}
