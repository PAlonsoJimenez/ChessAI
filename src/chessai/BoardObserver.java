package chessai;

/**
 *
 * @author Pablo Alonso
 */
public interface BoardObserver {
    
    public void tryingToMove(int OriginSquarePosition, int DestinationSquarePosition);
    public void pawnPromotedTo(int pawnOldAbsolutePosition, int pawnNewAbsolutePosition, String newPieceName, boolean color);
    public void timeOver(boolean loser);
    
}
