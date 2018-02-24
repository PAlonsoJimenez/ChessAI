package chessai.Swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Pablo Alonso
 */
public class MovementRecord extends JPanel{
    
    private int numberOfMovement;
    private int currentMovement;
    private final JPanel mainPanel;
    
    public MovementRecord(){
        numberOfMovement = -1;
        currentMovement = -1;
        mainPanel = new JPanel(new GridLayout(0,2));
        createUI();
    }

    public int getCurrentMovement() {
        return currentMovement;
    }
    
    public void previousMovement(){
        if(currentMovement > -1){
            mainPanel.getComponent(currentMovement).setForeground(Color.black);
            currentMovement--;
            if(currentMovement >= 0) mainPanel.getComponent(currentMovement).setForeground(new Color(218,165,32));
            moveVerticalScroll(false);
        }
    }
    
    public void nextMovement(){
        if(currentMovement < numberOfMovement){
            if(currentMovement >= 0) mainPanel.getComponent(currentMovement).setForeground(Color.black);
            currentMovement++;
            mainPanel.getComponent(currentMovement).setForeground(new Color(218,165,32));
            moveVerticalScroll(false);
        }
    }
    
    public void backToPresent(){
        if(currentMovement >= 0) mainPanel.getComponent(currentMovement).setForeground(Color.black);
        currentMovement = numberOfMovement;
        if(numberOfMovement >= 0) mainPanel.getComponent(numberOfMovement).setForeground(new Color(218,165,32));
        moveVerticalScroll(true);
    }
    
    public void addMovement(String movement){
        numberOfMovement++;
        mainPanel.add(new JLabel(movement));
        this.revalidate();
        backToPresent();
    }
    
    private void moveVerticalScroll(boolean toBottom){
        JScrollPane scrollPane = ((JScrollPane)MovementRecord.this.getComponent(0));
        double heightPercentage = mainPanel.getHeight();
        if(numberOfMovement > 1){
            heightPercentage = ((double) mainPanel.getHeight() * ( (double)((currentMovement)/2) / (double)((numberOfMovement)/2) ));
            if(toBottom){
                heightPercentage += 150;
            }else{
                if(heightPercentage - 150 > 0) heightPercentage -= 150;
            }
        }
        Point point = new Point(scrollPane.getViewport().getWidth(), (int)heightPercentage);
        scrollPane.getViewport().setViewPosition(point);
    }

    private void createUI() {
        ((GridLayout)mainPanel.getLayout()).setVgap(25);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(200,300));
        this.add(scrollPane);
    }
    
}
