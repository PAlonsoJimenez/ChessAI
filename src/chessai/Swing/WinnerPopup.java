package chessai.Swing;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author Pablo Alonso
 */
public class WinnerPopup extends JDialog{
    
    // 0: White won, 1:Black won, 2: Draw
    public WinnerPopup(int result, String message){
        setModal(true);
        
        JLabel imageHolder = new JLabel();
        switch(result){
            case 0:
                imageHolder.setIcon(new ImageIcon("White Won 400x278.jpg"));
                break;
            case 1:
                imageHolder.setIcon(new ImageIcon("Black Won 400x278.jpg"));
                break;
            case 2:
                imageHolder.setIcon(new ImageIcon("Stale Mate 400x240.jpg"));
                break;
        }
        
        add(imageHolder);
        setTitle(message);
        setIconImage(new ImageIcon("Winner Icon.png").getImage());
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
}
