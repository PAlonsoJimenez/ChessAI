package chessai.Swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 *
 * @author Pablo Alonso
 */
public class PromotionSelector extends JDialog {

    private String pieceSelectedName;

    public PromotionSelector(boolean color) {
        pieceSelectedName = "";
        setLayout(new FlowLayout());
        String colorName = color ? "W 64.png" : "B 64.png";
        String[] iconNames = new String[]{"Bishop " + colorName, "Knight " + colorName, "Rook " + colorName, "Queen " + colorName};
        String[] pieceNames = new String[]{"Bishop", "Knight", "Rook", "Queen"};
        for (int i = 0; i < 4; i++) {
            JButton piece = new JButton();
            piece.setIcon(new ImageIcon(iconNames[i]));
            piece.setName(pieceNames[i]);
            piece.addActionListener(buttonActionListener());
            this.add(piece);
        }

        setTitle("Promotion Selector");
        setIconImage(new ImageIcon("Promoting Icon.png").getImage());
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    public String getPieceSelectedName() {
        return pieceSelectedName;
    }

    private ActionListener buttonActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                pieceSelectedName = ((JButton) ae.getSource()).getName();
                PromotionSelector.this.dispose();
            }

        };
    }
}
