package chessai;

import java.util.ArrayList;

/**
 *
 * @author Pablo Alonso
 */
public class Game implements BoardObserver{
    
    private final GameGUIInterface observableBoard;
    private final Referee referee;
    private final Piece[] pieces;
    private boolean whiteTurn;
    private boolean whiteWins, blackWins;
    private boolean draw;
    
    public Game(GameGUIInterface ObservableBoard, Referee referee){
        this.observableBoard = ObservableBoard;
        this.referee = referee;
        pieces = new Piece[64];
        initializePieces();
        whiteTurn = true;
        whiteWins = false;
        blackWins = false;
        draw = false;
        this.observableBoard.addObserver(this);
    }
    
    private Game(){
        observableBoard = null;
        referee = null;
        pieces = new Piece[64];
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public boolean WhiteWins() {
        return whiteWins;
    }

    public boolean BlackWins() {
        return blackWins;
    }

    public boolean isDraw() {
        return draw;
    }
    
    public boolean isSquareOcuppied(int[] position){
        return pieces[Position.getAbsolutePosition(position)] != null;
    }
    
    public boolean isThisSquareOcuppiedByAWhitePiece(int[] position){
        return pieces[Position.getAbsolutePosition(position)].isWhite();
    }
    
    public boolean checkForValidEnPassantMovement(int nextMove, int oppositeDirection) {
        int jumperPawnExpectedAbsolutePosition = nextMove + 8*oppositeDirection;
        return (pieces[jumperPawnExpectedAbsolutePosition] != null &&
                pieces[jumperPawnExpectedAbsolutePosition] instanceof Pawn &&
                ((Pawn) pieces[jumperPawnExpectedAbsolutePosition]).hasJustMovedTwoStepForward());
    }
    
    public boolean checkForCastling(int newKingAbsolutePosition, int middleSquare, int rookExpectedAbsolutePosition, boolean kingsColor) {
        if (pieces[rookExpectedAbsolutePosition] == null
                || !(pieces[rookExpectedAbsolutePosition] instanceof Rook)
                || pieces[rookExpectedAbsolutePosition].isWhite() != kingsColor
                || ((Rook) (pieces[rookExpectedAbsolutePosition])).hasAlredyMoved()) {
            return false;
        }
        
        if(pieces[newKingAbsolutePosition] != null ||
                pieces[middleSquare] != null ||
                (rookExpectedAbsolutePosition == 0 && pieces[1] != null) ||
                (rookExpectedAbsolutePosition == 56 && pieces[57] != null)) {
            return false;
        }
        
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() != kingsColor){
                for (int squareAttackedByEnemy : piece.getPossibleMovements()) {
                    if(squareAttackedByEnemy == newKingAbsolutePosition) return false;
                    if(squareAttackedByEnemy == middleSquare) return false;
                }
            }
        }
        return true;
    }
    
    public void checkKingMoves(ArrayList<Integer> possibleMovements, boolean kingsColor, int kingAbsolutePosition) {
        ArrayList<Integer> movementsToRemove = new ArrayList<>();
        
        for (Piece piece : pieces) {
            if (piece != null && piece.isWhite() != kingsColor){
                ArrayList<Integer> possibleEnemyMovements = new ArrayList<>();
                possibleEnemyMovements.addAll(piece.getPossibleMovements());
                possibleEnemyMovements.addAll(piece.getDefendedPiecesPositions());
                for (int possibleEnemyMovement : possibleEnemyMovements) {
                    
                    if(piece instanceof Pawn &&
                           (possibleEnemyMovement % 8 ==  piece.getPosition()[1])){
                        continue;
                    }
                    
                    for (int kingPossibleMove: possibleMovements) {
                        if(possibleEnemyMovement == kingPossibleMove) movementsToRemove.add(kingPossibleMove);
                    }
                }
            }
        }
        for (int movementToRemove : movementsToRemove) {
            possibleMovements.remove((Integer)movementToRemove);
        }
        
        /*
        the next code is for the situation when one king can't move to an
        adjacent square because this square it's attacked by an opponent piece,
        but the opponent king can. The opponent king shoudn't be able to move to
        the square mentioned because it will be adjacent to the other king.
        Example:
        Black king in D7, white Rook in H6 and the rest of row 6 empty, and
        White King in E5. White turn, White King shouldn't be able to move to
        D6 or E6, but he can.
        */
        movementsToRemove.clear();
        for (Piece piece : pieces) {
            if (piece != null && piece instanceof King && piece.isWhite() != kingsColor){
                int enemyKingAbsolutePostion = Position.getAbsolutePosition(piece.getPosition());
                /*
                I add everything, even the wrong absolute position like
                -7 or 70; it will not be removed from the king possibleMovements
                because it's not included in the array.
                */
                movementsToRemove.add(enemyKingAbsolutePostion - 9);
                movementsToRemove.add(enemyKingAbsolutePostion - 8);
                movementsToRemove.add(enemyKingAbsolutePostion - 7);
                movementsToRemove.add(enemyKingAbsolutePostion - 1);
                movementsToRemove.add(enemyKingAbsolutePostion + 1);
                movementsToRemove.add(enemyKingAbsolutePostion + 7);
                movementsToRemove.add(enemyKingAbsolutePostion + 8);
                movementsToRemove.add(enemyKingAbsolutePostion + 9);
            }
        }
        
        /*
        The following fifteen ugly lines are just for the case with the king in
        a position like:
        king in G6, being attacked by rook in G3. in this case, and cases
        similar to this one, king have as legal move G7, cause G7 is not
        attacked by the rook. So I have to look to the future move G7, and
        update the rook movements to see if it can reach G7 now, and therefor
        attaking and checking the king.
        */
        for (int movement : pieces[kingAbsolutePosition].getPossibleMovements()) {
            Game futureStateGame = this.clone(pieces[kingAbsolutePosition].isWhite());
            futureStateGame.pieces[kingAbsolutePosition].setPosition(movement);
            futureStateGame.pieces[movement] = pieces[kingAbsolutePosition];
            futureStateGame.pieces[kingAbsolutePosition] = null;
            for (Piece futurePiece : futureStateGame.pieces) {
                if(futurePiece != null && futurePiece.isWhite() != futureStateGame.whiteTurn &&
                        !(futurePiece instanceof King) && !(futurePiece instanceof Knight)){
                    futurePiece.calculateNewPossibleMovements();
                    for (int futurePieceNewPossibleMovement : futurePiece.getPossibleMovements()) {
                        if(futurePieceNewPossibleMovement == movement) movementsToRemove.add(movement);
                    }
                }
            }
        }
        
        for (int movementToRemove : movementsToRemove) {
            possibleMovements.remove((Integer)movementToRemove);
        }
        
    }
    
    public void castling(int oldRookPosition, int newRookPosition){
        if(observableBoard != null) observableBoard.moveApproved(oldRookPosition, newRookPosition, false);
        pieces[oldRookPosition].setPosition(newRookPosition);
        pieces[newRookPosition] = pieces[oldRookPosition];
        pieces[oldRookPosition] = null;
    }

    public void captureEnPassant(int[] pawnCapturedPosition, boolean enemyColor) {
        if(observableBoard != null) observableBoard.changeSquareImageIcon(Position.getAbsolutePosition(pawnCapturedPosition), "None", enemyColor);
        pieces[Position.getAbsolutePosition(pawnCapturedPosition)] = null;
    }
    
    public void pawnPromotion(int pawnOldPosition, int pawnNewPosition, boolean color) {
        if(observableBoard != null){
            boolean AIpromoting = referee.getAIColor() == color;
            observableBoard.promotePawn(pawnOldPosition, pawnNewPosition, color, AIpromoting);
        }
        //If is a look into the future move, I assume is going to choose a queen
        else pawnPromotedTo(pawnOldPosition, pawnNewPosition, "Queen", color);
    }
    
    public void move(int originSquarePosition, int destinationSquarePosition, boolean checkFinalStates){
        String notation = "";
        if(observableBoard != null){
            observableBoard.moveApproved(originSquarePosition, destinationSquarePosition, true);
            notation = findMoveNotationPreviousToMove(originSquarePosition, destinationSquarePosition);
        }
        
        //Must check for double jump pawn before set the new piece position,
        //because if I look for it after, I will negate his variable "hasJustMovedTwoStepForward" just after it moves two step forward.
        checkForDoubleJumpingPawn();
        pieces[originSquarePosition].setPosition(destinationSquarePosition);
        pieces[destinationSquarePosition] = pieces[originSquarePosition];
        pieces[originSquarePosition] = null;
        
        updateThePossibleMovements();
        checkForCheck();
        if(checkFinalStates){
            checkForCheckmate();
            if(!whiteWins && !blackWins) checkForStaleMate();
        }
        
        if(observableBoard != null){
            observableBoard.writeMoveNotation(completeNotation(notation));
        }
        
        whiteTurn = !whiteTurn;
        if(referee != null) referee.nextTurn();
    }
    
    public Game clone(boolean whiteTurn){
        Game clone = new Game();
        clone.whiteTurn = whiteTurn;
        clone.blackWins = blackWins;
        clone.whiteWins = whiteWins;
        clone.draw = draw;
        for (int i = 0; i < pieces.length; i++) {
            clone.pieces[i] = (pieces[i] == null) ? null : pieces[i].clone(clone);
        }
        return clone;
    }
    
    @Override
    public void tryingToMove(int originSquarePosition, int destinationSquarePosition) {
        if(pieces[originSquarePosition] == null) return;
        if(originSquarePosition == destinationSquarePosition) return;
        if(pieces[originSquarePosition].isWhite() != whiteTurn) return;
        if(referee.getAIColor() == pieces[originSquarePosition].isWhite()) return;
        if(!isALegalMove(originSquarePosition, destinationSquarePosition)) return;
        
        move(originSquarePosition, destinationSquarePosition, true);        
    }

    @Override
    public void pawnPromotedTo(int pawnOldAbsolutePosition, int pawnNewAbsolutePosition, String newPieceName, boolean color) {
        //I put the new pice on the old pawn position in "pieces" because when it
        //return to the method "tryingToMove", it will place the piece on the old pawn position,
        //on his new position.
        switch(newPieceName){
            case "Bishop":
                pieces[pawnOldAbsolutePosition] = new Bishop(color, Position.getRelativePosition(pawnNewAbsolutePosition), this);
                break;
            case "Knight":
                pieces[pawnOldAbsolutePosition] = new Knight(color, Position.getRelativePosition(pawnNewAbsolutePosition), this);
                break;
            case "Rook":
                pieces[pawnOldAbsolutePosition] = new Rook(color, Position.getRelativePosition(pawnNewAbsolutePosition), this);
                break;
            case "Queen":
                pieces[pawnOldAbsolutePosition] = new Queen(color, Position.getRelativePosition(pawnNewAbsolutePosition), this);
                break;
        }
    }
    
    @Override
    public void timeOver(boolean loser){
        whiteWins = !loser;
        blackWins = loser;
        referee.gameEnded();
        
        int materialCounter = 0;
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() != loser){
                if(piece instanceof Queen || piece instanceof Rook || piece instanceof Pawn){
                    materialCounter += 2;
                    break;
                }
                if(piece instanceof Bishop || piece instanceof Knight){
                    materialCounter++;
                }
                if(materialCounter > 1) break;
            }
        }
        
        if(materialCounter < 2){
            observableBoard.gameEnded(2, "Draw casue insufficient material!");
        }else{
            int result = loser ? 1 : 0;
            String message = loser ? "Congratulation Black player!, White player time over" : "Congratulation White player!, Black player time over";
            observableBoard.gameEnded(result, message);
        }
    }

    private void initializePieces() {
        pieces[0] = new Rook(false, new int[]{0,0}, this);
        pieces[1] = new Knight(false, new int[]{0,1}, this);
        pieces[2] = new Bishop(false, new int[]{0,2}, this);
        pieces[3] = new Queen(false, new int[]{0,3}, this);
        pieces[4] = new King(false, new int[]{0,4}, this);
        pieces[5] = new Bishop(false, new int[]{0,5}, this);
        pieces[6] = new Knight(false, new int[]{0,6}, this);
        pieces[7] = new Rook(false, new int[]{0,7}, this);
        pieces[8] = new Pawn(false, new int[]{1,0}, this);
        pieces[9] = new Pawn(false, new int[]{1,1}, this);
        pieces[10] = new Pawn(false, new int[]{1,2}, this);
        pieces[11] = new Pawn(false, new int[]{1,3}, this);
        pieces[12] = new Pawn(false, new int[]{1,4}, this);
        pieces[13] = new Pawn(false, new int[]{1,5}, this);
        pieces[14] = new Pawn(false, new int[]{1,6}, this);
        pieces[15] = new Pawn(false, new int[]{1,7}, this);
        pieces[48] = new Pawn(true, new int[]{6,0}, this);
        pieces[49] = new Pawn(true, new int[]{6,1}, this);
        pieces[50] = new Pawn(true, new int[]{6,2}, this);
        pieces[51] = new Pawn(true, new int[]{6,3}, this);
        pieces[52] = new Pawn(true, new int[]{6,4}, this);
        pieces[53] = new Pawn(true, new int[]{6,5}, this);
        pieces[54] = new Pawn(true, new int[]{6,6}, this);
        pieces[55] = new Pawn(true, new int[]{6,7}, this);
        pieces[56] = new Rook(true, new int[]{7,0}, this);
        pieces[57] = new Knight(true, new int[]{7,1}, this);
        pieces[58] = new Bishop(true, new int[]{7,2}, this);
        pieces[59] = new Queen(true, new int[]{7,3}, this);
        pieces[60] = new King(true, new int[]{7,4}, this);
        pieces[61] = new Bishop(true, new int[]{7,5}, this);
        pieces[62] = new Knight(true, new int[]{7,6}, this);
        pieces[63] = new Rook(true, new int[]{7,7}, this);
        updateThePossibleMovements();
    }
    
    public boolean isALegalMove(int originSquarePosition, int destinationSquarePosition) {
        boolean possibleMove = false;
        for (int movement : pieces[originSquarePosition].getPossibleMovements()) {
            if(movement == destinationSquarePosition){
                possibleMove = true;
                break;
            }
        }
        if(!possibleMove) return false;
        
        Game futureStateGame = clone(whiteTurn);
        futureStateGame.move(originSquarePosition, destinationSquarePosition, false);
        
        for (Piece piece : futureStateGame.pieces) {
            if(piece != null && piece instanceof King &&
                    piece.isWhite() == whiteTurn &&
                    ((King)piece).isChecked())
                return false;
        }
        return true;
    }
    
    private void checkForDoubleJumpingPawn() {
        for (Piece piece : pieces) {
            if(piece != null && piece instanceof Pawn) ((Pawn)piece).cancelCaptureEnPassantChance();
        }
    }

    private void updateThePossibleMovements() {
        //The kings have to be actualized the last ones cause they depends on the other pieces possibles movements.
        //first the king of the player who's moving
        int playerMovingKingAbsolutePosition = -1;
        int playerWaitingKingAbsolutePosition = -1;
        for (Piece piece : pieces) {
            if(piece != null){
                if(piece instanceof King){
                    if(piece.isWhite() == whiteTurn){
                        playerMovingKingAbsolutePosition = Position.getAbsolutePosition(piece.getPosition());
                    }else{
                        playerWaitingKingAbsolutePosition = Position.getAbsolutePosition(piece.getPosition());
                    }
                }else{
                    piece.calculateNewPossibleMovements();
                }
            }
        }
        
        pieces[playerMovingKingAbsolutePosition].calculateNewPossibleMovements();
        pieces[playerWaitingKingAbsolutePosition].calculateNewPossibleMovements();
    }

    private void checkForCheck() {
        //I have to check if any of both kings are in check, because if the king
        //moving is in check, is a wrong move.
        int kingPendragonAbsolutePosition = -1;
        int kingArthurAbsolutePosition = -1;
        for (Piece piece : pieces) {
            if(piece != null && piece instanceof King){
                if(kingPendragonAbsolutePosition == -1){
                    kingPendragonAbsolutePosition = Position.getAbsolutePosition(piece.getPosition());
                }else{
                    kingArthurAbsolutePosition = Position.getAbsolutePosition(piece.getPosition());
                    break;
                }
            }
        }
        
        boolean kingPendragonIsChecked = kingIsBeenAtacked(kingPendragonAbsolutePosition, !pieces[kingPendragonAbsolutePosition].isWhite());
        ((King)pieces[kingPendragonAbsolutePosition]).setIsChecked(kingPendragonIsChecked);
        if(kingPendragonIsChecked) ((King)pieces[kingPendragonAbsolutePosition]).cancelCastleMovement();
        
        boolean kingArthurIsChecked = kingIsBeenAtacked(kingArthurAbsolutePosition, !pieces[kingArthurAbsolutePosition].isWhite());
        ((King)pieces[kingArthurAbsolutePosition]).setIsChecked(kingArthurIsChecked);
        if(kingArthurIsChecked) ((King)pieces[kingArthurAbsolutePosition]).cancelCastleMovement();
    }
    
    private boolean kingIsBeenAtacked(int movingPlayerKingAbsolutePosition, boolean enemyColor){
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() == enemyColor){
                for (int move : piece.getPossibleMovements()) {
                    if(move == movingPlayerKingAbsolutePosition)
                        return true;
                }
            }
        }
        return false;
    }
    
    private void checkForCheckmate() {
        for (Piece piece : pieces) {
            if(piece instanceof King && piece.isWhite() != whiteTurn &&
                    piece.getPossibleMovements().isEmpty() &&
                    ((King) piece).isChecked() &&
                    !canSaveTheKing(Position.getAbsolutePosition(piece.getPosition()), piece.isWhite())){
                //Checkmate
                whiteWins = whiteTurn;
                blackWins = !whiteTurn;
                if(observableBoard != null){
                    referee.gameEnded();
                    int result = whiteTurn ? 0 : 1;
                    String message = whiteTurn ? "Congratulation White player!" : "Congratulation Black player!";
                    observableBoard.gameEnded(result, message);
                }
                break;
            }
        }
    }
    
    private boolean canSaveTheKing(int kingAttackedAbsolutePosition, boolean color){
        ArrayList<Integer> futureMovementOrigin = new ArrayList<>();
        ArrayList<Integer> futureMovementDestination = new ArrayList<>();
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() == color){
                for (int move : piece.getPossibleMovements()) {
                    futureMovementDestination.add(move);
                    futureMovementOrigin.add(Position.getAbsolutePosition(piece.getPosition()));
                }
            }
        }
        for(int i = 0; i < futureMovementDestination.size(); i++){
            Game futureStateGame = this.clone(color);
            futureStateGame.pieces[futureMovementOrigin.get(i)].setPosition(futureMovementDestination.get(i));
            futureStateGame.pieces[futureMovementDestination.get(i)] = pieces[futureMovementOrigin.get(i)];
            futureStateGame.pieces[futureMovementOrigin.get(i)] = null;
            futureStateGame.updateThePossibleMovements();
            futureStateGame.checkForCheck();
            if(!((King)futureStateGame.pieces[kingAttackedAbsolutePosition]).isChecked()) return true;
        }
        return false;
    }

    private void checkForStaleMate() {
        boolean stalemate = true;
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() != whiteTurn){
                if(!piece.getPossibleMovements().isEmpty()){
                    stalemate = false;
                    break;
                }
            }
        }
        if(stalemate){
            draw = true;
            if(observableBoard != null){
                referee.gameEnded();
                observableBoard.gameEnded(2, "It's a Draw!");
            }
        }
    }

    private String findMoveNotationPreviousToMove(int originSquarePosition, int destinationSquarePosition) {
        String notation = "";
        if(pieces[originSquarePosition] instanceof King){
            if(originSquarePosition - destinationSquarePosition == 2){
                notation += "0-0-0";
                return notation;
            }
            if(originSquarePosition - destinationSquarePosition == -2){
                notation += "0-0";
                return notation;
            }
        }
        
        if(pieces[originSquarePosition] instanceof Pawn) notation += "P";
        if(pieces[originSquarePosition] instanceof Knight) notation += "N";
        if(pieces[originSquarePosition] instanceof Bishop) notation += "B";
        if(pieces[originSquarePosition] instanceof Rook) notation += "R";
        if(pieces[originSquarePosition] instanceof Queen) notation += "Q";
        if(pieces[originSquarePosition] instanceof King) notation += "K";
        
        if(pieces[destinationSquarePosition] != null){
            notation += "x";
        }
        
        boolean enPassant = false;
        if (pieces[originSquarePosition] instanceof Pawn
                && Math.abs(originSquarePosition - destinationSquarePosition) != 8
                && Math.abs(originSquarePosition - destinationSquarePosition) != 16
                && pieces[destinationSquarePosition] == null) {
            notation += "x";
            enPassant = true;
        }
        
        int number = 8 - destinationSquarePosition/8;
        //a = 97
        int letter = 97;
        letter += destinationSquarePosition % 8;
        notation += (char)letter;
        notation = notation + number;
        
        if(enPassant) notation += " e.p.";
        
        return notation;
    }

    private String completeNotation(String notation) {
        if(whiteWins || blackWins){
            notation += "#";
            return notation;
        }
        
        if(draw){
            notation += " 1/2";
            return notation;
        }
        
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() != whiteTurn && piece instanceof King){
                if(((King)piece).isChecked()) notation += "+";
            }
        }
        return notation;
    }

}
