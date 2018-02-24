package chessai.Swing;

import chessai.OptionMenu;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Pablo Alonso
 */
public class SwingOptionMenu extends JDialog implements OptionMenu{
    
    private int minutes;
    private int seconds;
    private boolean chosenColor;
    
    public SwingOptionMenu(){
        minutes = 10;
        seconds = 0;
        chosenColor = true;
        
        setModal(true);
        createUI();
        setTitle("Menu");
        setIconImage(new ImageIcon("Option icon 200.png").getImage());
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public int getTimeInSeconds() {
        return minutes*60 + seconds;
    }

    @Override
    public boolean getChosenColor() {
        return chosenColor;
    }

    private void createUI() {
        this.setLayout(new BorderLayout());
        createNorthPanel();
        createCenterPanel();
        createSouthPanel();
    }

    private void createNorthPanel() {
        JPanel north = new JPanel();
        JLabel label = new JLabel("Select the color you want to play: ");
        JRadioButton white = new JRadioButton("White");
        white.setSelected(true);
        JRadioButton black = new JRadioButton("Black");
        white.addActionListener(whiteRadioButtonActionListener(black));
        black.addActionListener(blackRadioButtonActionListener(white));
        
        north.add(label);
        north.add(white);
        north.add(black);
        this.add(north, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel center = new JPanel();
        JLabel minutesLabel = new JLabel(""+minutes);
        JLabel secondsLabel = new JLabel("0"+seconds);
        JButton plusMinutes = new JButton("▲");
        JButton minusMinutes = new JButton("▼");
        plusMinutes.addActionListener(minutesActionlistener(true, minutesLabel));
        minusMinutes.addActionListener(minutesActionlistener(false, minutesLabel));
        JButton plusSeconds = new JButton("▲");
        JButton minusSeconds = new JButton("▼");
        plusSeconds.addActionListener(secondsActionlistener(true, secondsLabel));
        minusSeconds.addActionListener(secondsActionlistener(false, secondsLabel));
        
        center.add(minutesLabel);
        center.add(plusMinutes);
        center.add(minusMinutes);
        center.add(secondsLabel);
        center.add(plusSeconds);
        center.add(minusSeconds);
        this.add(center, BorderLayout.CENTER);
    }
    
    private void createSouthPanel() {
        JPanel south = new JPanel();
        JButton accept = new JButton("Accept");
        accept.addActionListener(acceptActionListener());
        
        south.add(accept);
        this.add(south, BorderLayout.SOUTH);
    }

    private ActionListener whiteRadioButtonActionListener(final JRadioButton black) {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                ((JRadioButton) ae.getSource()).setSelected(true);
                black.setSelected(false);
                chosenColor = true;
            }
            
        };
    }

    private ActionListener blackRadioButtonActionListener(final JRadioButton white) {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                ((JRadioButton) ae.getSource()).setSelected(true);
                white.setSelected(false);
                chosenColor = false;
            }
            
        };
    }

    private ActionListener minutesActionlistener(final boolean operation, final JLabel minutesLabel) {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                if(operation){
                    if(minutes < 99)minutes++;
                }else{
                    if(minutes > 0)minutes--;
                }
                minutesLabel.setText(""+minutes);
                if(minutes < 10) minutesLabel.setText("0"+minutes);
            }
            
        };
    }
    
    private ActionListener secondsActionlistener(final boolean operation, final JLabel secondsLabel) {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                if(operation){
                    if(seconds < 59)seconds++;
                }else{
                    if(seconds > 0)seconds--;
                }
                secondsLabel.setText(""+seconds);
                if(seconds < 10) secondsLabel.setText("0"+seconds);
            }
            
        };
    }

    private ActionListener acceptActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                SwingOptionMenu.this.dispose();
            }
            
        };
    }
    
}
