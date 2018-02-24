package chessai.Swing;

import javax.swing.JButton;

/**
 *
 * @author Pablo Alonso
 */
public class Square extends JButton{
    
    private final int position;
    private int value;
    
    public Square(int position){
        this.position = position;
        value = 0;
    }

    public int getPosition() {
        return position;
    }

    /**
     *
     * @return White positive, Black negative
        Pawn: 1; Knight: 3; Bishop: 4; Rook: 5; Queen: 9; King 10;
     */
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
