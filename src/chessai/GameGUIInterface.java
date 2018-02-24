package chessai;

/**
 *
 * @author Pablo Alonso
 */
public interface GameGUIInterface extends BoardObservable{

    public void moveApproved(int OriginSquarePosition, int DestinationSquarePosition, boolean capture);
    public void changeSquareImageIcon(int squarePosition, String newImageName, boolean color);
    public void promotePawn(int pawnOldAbsolutePosition, int pawnNewAbsolutePosition, boolean color, boolean AIpromoting);
    public void writeMoveNotation(String notation);
    public void gameEnded(int result, String message); // 0: White won, 1:Black won, 2: Draw
}
