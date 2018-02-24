package chessai;

import java.util.LinkedList;

/**
 *
 * @author Pablo Alonso
 */
public class SearchTree {
    
    private static TreeNode root;
    
    public static int[] getNextMove(Game playing, boolean color){
        root = new TreeNode(null, playing, color, -1, -1);
        TreeNode nextMove = minMaxWithAlphaBeta(root, 0);
        return new int[]{nextMove.getOriginSquarePosition(), nextMove.getDestinationSquarePosition()};
    }
    
    private static TreeNode minMaxWithAlphaBeta(TreeNode currentNode, int depth) {
        //I can't initialize it to null...
        TreeNode chosenNode = currentNode;
        //Let's say the depth limit is 3
        if(depth == 3 || isTerminalNode(currentNode)){
            calculateHeuristicValue(currentNode);
            return chosenNode;
        }
        
        expandChilds(currentNode);
        
        if(root.isColor() == currentNode.isColor()){
            //Max
            if(currentNode.getHeuristicValue() == 0) currentNode.setHeuristicValue(-100000);

            for (TreeNode child : currentNode.getChilds()) {
                minMaxWithAlphaBeta(child, depth+1);
                if(child.getHeuristicValue() > currentNode.getHeuristicValue()){
                    currentNode.setHeuristicValue(child.getHeuristicValue());
                    chosenNode = child;
                }
                if(currentNode.getFather() != null){
                    int fatherHeuristicValue = currentNode.getFather().getHeuristicValue();
                    if(fatherHeuristicValue < currentNode.getHeuristicValue()){
                        break;
                    }
                }
            }
        }else{
            //Min
            if(currentNode.getHeuristicValue() == 0) currentNode.setHeuristicValue(100000);
            
            for (TreeNode child : currentNode.getChilds()) {
                minMaxWithAlphaBeta(child, depth+1);
                if(child.getHeuristicValue() < currentNode.getHeuristicValue()){
                    currentNode.setHeuristicValue(child.getHeuristicValue());
                    chosenNode = child;
                }
                if(currentNode.getFather() != null){
                    int fatherHeuristicValue = currentNode.getFather().getHeuristicValue();
                    if(fatherHeuristicValue > currentNode.getHeuristicValue()){
                        break;
                    }
                }
            }
        }
        return chosenNode;
    }
    
    private static boolean isTerminalNode(TreeNode currentNode) {
        return currentNode.getCurrentGameState().BlackWins() || 
                currentNode.getCurrentGameState().WhiteWins() ||
                currentNode.getCurrentGameState().isDraw();
    }
    
    private static void expandChilds(TreeNode node){
        for (Piece piece : node.getCurrentGameState().getPieces()) {
            if(piece != null && node.isColor() == piece.isWhite()) {
                for (int move : piece.getPossibleMovements()) {
                    if(node.getCurrentGameState().isALegalMove(Position.getAbsolutePosition(piece.getPosition()), move)){
                        Game childGameState = node.getCurrentGameState().clone(node.isColor());
                        childGameState.move(Position.getAbsolutePosition(piece.getPosition()), move, true);
                        TreeNode child = new TreeNode(node, childGameState, !node.isColor(), Position.getAbsolutePosition(piece.getPosition()), move);
                        node.addChild(child);
                    }
                }
            }
        }
    }
    
    private static void calculateHeuristicValue(TreeNode node){
        boolean mainColor = root.isColor();
        Game currentGame = node.getCurrentGameState();
        
        if(isEndGameState(node, mainColor, currentGame)) return;
        int fieldControl = 0;
        int attack = 0;
        int defense = 0;
        int piecesValue = 0;
        Piece[] pieces = currentGame.getPieces();
        LinkedList<Piece> myPieces = new LinkedList<>();
        LinkedList<Piece> oponentPieces = new LinkedList<>();
        
        //attackField values
        int[] attackField = new int[64];
        for(Piece piece : pieces){
            if(piece == null) continue;
            if(piece.isWhite() == mainColor){
                myPieces.add(piece);
            }else{
                oponentPieces.add(piece);
            }
            int valid = (piece.isWhite() == mainColor) ? 1 : -1;
            for(int possibleMovement : piece.getPossibleMovements()){
                attackField[possibleMovement] += 1*valid;
            }
        }
        
        fieldControl += endangeredPieces(pieces, attackField, mainColor);
        
        //FieldControl values;
        for (int i = 0; i < attackField.length; i++) {
            if(attackField[i] > 0)fieldControl += 2;
        }
        
        //Piece value part 1
        for (Piece oponentPiece : oponentPieces) {
            piecesValue -= oponentPiece.getValue()*10;
        }
        for (Piece myPiece : myPieces) {
            piecesValue += myPiece.getValue()*10;
        }
        
        for(Piece piece : pieces){
            if(piece == null) continue;
            //Piece Value part 2
            int pieceAttaked = attackField[Position.getAbsolutePosition(piece.getPosition())];
            if( pieceAttaked > 0 && piece.isWhite() != mainColor){
                piecesValue += piece.getValue() * 2;
            }else if(pieceAttaked < 0 && piece.isWhite() == mainColor){
                piecesValue -= piece.getValue() * 3;
            }
            
            //Attack and Defense values;
            //General Attack and Defense;
            int valid = (piece.isWhite() == mainColor) ? 1 : -1;
            fieldControl += piece.getPossibleMovements().size() * valid;
            for(int possibleMovement : piece.getPossibleMovements()){
                if(currentGame.isSquareOcuppied(Position.getRelativePosition(possibleMovement))){
                    int value = pieces[possibleMovement].getValue();
                    defense += addDefense(mainColor, piece.color, pieces[possibleMovement].isWhite(), value);
                    attack += addAttack(mainColor, piece.color, pieces[possibleMovement].isWhite(), value);
                }
            }
            //Castling And checking
            if(piece instanceof King){
                if(piece.isWhite() == mainColor){
                    if(((King)piece).isChecked()) defense -= 7;
                    if(((King) piece).haveJustCastled()) defense += 15;
                }else{
                    if(((King)piece).isChecked()) attack += 7;
                }
            }
        }
        node.setHeuristicValue(fieldControl * 2 + defense * 2 + attack * 2 + piecesValue * 3);
    }
    
    private static boolean isEndGameState(TreeNode node, boolean mainColor, Game currentGame){
         if(currentGame.WhiteWins()){
            int heuristicValue = (mainColor) ? 10000 : -10000;
            node.setHeuristicValue(heuristicValue);
            return true;
         }else if(currentGame.BlackWins()){
            int heuristicValue = (mainColor) ? -10000 : 10000;
            node.setHeuristicValue(heuristicValue);
            return true;
        }else if(currentGame.isDraw()){
            node.setHeuristicValue(0);
            return true;
        }else{
            return false;
        }
    }

    private static int addDefense(boolean mainColor, boolean pieceColor, boolean pieceAttackedColor, int value) {
        if(mainColor == pieceColor && mainColor == pieceAttackedColor) return value;
        if(mainColor == !pieceColor && mainColor == pieceAttackedColor) return (value * - 1);
        return 0;
    }

    private static int addAttack(boolean mainColor, boolean pieceColor, boolean pieceAttackedColor, int value) {
        if(mainColor == pieceColor && mainColor == !pieceAttackedColor) return value;
        if(mainColor == !pieceColor && mainColor == !pieceAttackedColor) return (value * - 1);
        return 0;
    }
    
    private static int endangeredPieces(Piece[] pieces, int[] attackField, boolean mainColor){
        int dangerLevel = 0;
        for (Piece piece : pieces) {
            if(piece != null && piece.isWhite() == mainColor){
                boolean isEndangered = false;
                for (Piece otherPiece : pieces) {
                    if(otherPiece != null && otherPiece.isWhite() != mainColor && otherPiece.value < piece.value){
                        for (int possibleMove : otherPiece.getPossibleMovements()) {
                            if(possibleMove == Position.getAbsolutePosition(piece.getPosition())){
                                isEndangered = true;
                                break;
                            }
                        }
                    }
                    if(isEndangered) break;
                }
                if(attackField[Position.getAbsolutePosition(piece.getPosition())] < 0) isEndangered = true;
                if(isEndangered) dangerLevel -= piece.value * 2;
            }
        }
        return dangerLevel;
    }
}
