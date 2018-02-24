package chessai;

/**
 *
 * @author Pablo Alonso
 */
public class Position {
     
    public static int getAbsolutePosition(int[] position){
        return position[0] * 8 + position[1];
    }
    
    public static int[] getRelativePosition(int position){
        return new int[]{position / 8, position % 8};
    }
}
