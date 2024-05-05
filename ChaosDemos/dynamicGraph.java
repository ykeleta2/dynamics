package ChaosDemos;
import java.awt.*;
import java.util.*;
/**
*  Stub class for iteration and plotting. 
*  To be subclassed by classes yielding implementations
*  of methods.<br>
*  Provides reference for controls to update calling class.
* @version     15 March 1997
* @author       Michael Cross 
*/
public class dynamicGraph extends Frame  {

//    superGraph2D graph;
//    movie theMovie;

/**
*     Default constructor
*/
    public dynamicGraph() {
//        graph = new superGraph2D(this);
//        theMovie = new movie(this);
    }

/**
*   Stub method to iterate equations and update graph.
*/  
    public boolean iterate(){return true;}

/**
*   Stub method to respond to mouse event on graph.
* @param xcoord x-position of event
* @param ycoord y-position of event
* @param xcoordValid true if x-position valid
* @param ycoordValid true if y-position valid
*/
    public void respondToMouse(double xcoord, boolean xcoordValid,
                 double ycoord, boolean ycoordValid){}

/**
*   Stub method to respond to buttonControls
* @see      buttonControls
* @param    buttonIndex index of button pushed
*/      
    public void respondToButtons(int buttonIndex){}

/**
*   Stub method to respond to textControls
* @see      textControls
*/       
    public void respondToText(){}
    
/**
*   Stub method to respond to choiceControls
* @see     choiceControls
*/       
    public void respondToChoices(){}    
/**
*   Stub method to respond to buttons in sliderControls
* @see      sliderControls
*/     
    public void respondToSliderButtons(){}

/**
*   Stub method to respond to text boxes in sliderControls
* @see      sliderControls
*/     
    public void respondToSliderText(){}
/**
*   Stub method to return derivatives for ode solver
* @param x[] vector of current value of dependent variables
* @param n number of dependent variables in array x[]
* @param t current value of independent variable
* @return n compoent vector giving derivatives of dependent variables
*/     
    public double[] derivs(double[] x, double t, int n){
        return x;
    }

/**
*   Stub method to allow painting to graph
* @param g Graphics context
* @param r Rectangle of data area
*/       
    public void addToGraph( Graphics g, Rectangle r)  {}
    public void updateSpeed(int delay) {}
}
