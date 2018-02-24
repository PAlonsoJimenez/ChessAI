package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class King extends Piece{

    private boolean hasAlreadyMoved;
    private boolean isChecked;
    private boolean haveJustCastled;
    
    public King(boolean color, int[] position, Game gameItBelongsTo){
        super(color, position, 10, gameItBelongsTo);
        hasAlreadyMoved = false;
        isChecked = false;
        haveJustCastled = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean haveJustCastled() {
        return haveJustCastled;
    }
    
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    
    public void cancelCastleMovement(){
        int longCastlingMove = -1;
        int shortCastlingMove = -1;
        for (int move : possibleMovements) {
            if(Position.getAbsolutePosition(position) - move == 2) longCastlingMove = move;
            if(Position.getAbsolutePosition(position) - move == -2) shortCastlingMove = move;
        }
        if(longCastlingMove != -1) possibleMovements.remove((Integer)longCastlingMove);
        if(shortCastlingMove != -1) possibleMovements.remove((Integer)shortCastlingMove);
    }
    
    @Override
    public void setPosition(int position) {
        int oldPositionColumn = this.position[1];
        super.setPosition(position);
        hasAlreadyMoved = true;
        haveJustCastled = false;
        if(this.position[1] - oldPositionColumn == 2) {
            playing.castling(this.position[0]*8+7, this.position[0]*8+5);
            haveJustCastled = true;
        }
        if(this.position[1] - oldPositionColumn == -2) {
            playing.castling(this.position[0]*8+0, this.position[0]*8+3);
            haveJustCastled = true;
        }
    }
    
    @Override
    protected void calculateNewPossibleMovements() {
        possibleMovements.clear();
        defendedPiecesPositions.clear();
        //Same as Queen, but only one step, and the Castling
        //Up
        int [] nextMove = new int[]{position[0]-1,position[1]};
        if(position[0]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Down
        nextMove = new int[]{position[0]+1,position[1]};
        if(position[0]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
    
        //Right
        nextMove = new int[]{position[0],position[1]+1};
        if(position[1]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Left
        nextMove = new int[]{position[0],position[1]-1};
        if(position[1]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Upper Right
        nextMove = new int[]{position[0]-1,position[1]+1};
        if(position[0]-1 >= 0 && position[1]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Upper Left
        nextMove = new int[]{position[0]-1,position[1]-1};
        if(position[0]-1 >= 0 && position[1]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Lower right
        nextMove = new int[]{position[0]+1,position[1]+1};
        if(position[0]+1 <= 7 && position[1]+1 <= 7){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Lower Left
        nextMove = new int[]{position[0]+1,position[1]-1};
        if(position[0]+1 <= 7 && position[1]-1 >= 0){
            possibleMoveOrPossibleDefend(nextMove);
        }
        
        //Castling
        if(!hasAlreadyMoved){
            //I check if the cast is valid along with the other moves of the king in "playing".
            //X*8 + Y; is the same as Position.getAbsolutePosition(), but here is prettier without the method.
            if(color){
                if(playing.checkForCastling(7*8+2, 7*8+3, 7*8+0, color)) possibleMovements.add(7*8+2);
                if(playing.checkForCastling(7*8+6, 7*8+5, 7*8+7, color)) possibleMovements.add(7*8+6);
            }else{
                if(playing.checkForCastling(0*8+2, 0*8+3, 0*8+0, color)) possibleMovements.add(0*8+2);
                if(playing.checkForCastling(0*8+6, 0*8+5, 0*8+7, color)) possibleMovements.add(0*8+6);
            }
        }
        playing.checkKingMoves(possibleMovements, color, Position.getAbsolutePosition(position));
    }
    
    private void possibleMoveOrPossibleDefend (int[] nextMove){
        //Cambiar esto en checkKingMoves, añadir que no se pueda mover a ningun espacio cercano al rey rival
        if(!playing.isSquareOcuppied(nextMove)||playing.isThisSquareOcuppiedByAWhitePiece(nextMove)!= color){
            possibleMovements.add(Position.getAbsolutePosition(nextMove));
        }else{
            defendedPiecesPositions.add(Position.getAbsolutePosition(nextMove));
        }
    }
    
    @Override
    public Piece clone(Game gameItBelongsTo) {
        int[] clonePosition = new int []{position[0], position[1]};
        King clone = new King(color, clonePosition, gameItBelongsTo);
        clone.hasAlreadyMoved = hasAlreadyMoved;
        clone.isChecked = isChecked;
        clone.haveJustCastled = haveJustCastled;
        clone.possibleMovements = getClonedMovementList();
        clone.defendedPiecesPositions = getClonedDefendedPiecesPositions();
        return clone;
    }

}
