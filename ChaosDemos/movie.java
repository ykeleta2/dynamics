package ChaosDemos;
import java.awt.*;
//**********************************************************************
/**
* Class to implement movie thread<br>
* Calls <i>iterate()</i> in parent to update equations and graph.
* @version March 15 1997
* @author Michael Cross
*/  
//**********************************************************************

public class movie extends Panel implements Runnable {

        // variables setting scroll properties
        Scrollbar speedScroll;  
/**
* set true for sleep delay
*/
        public boolean toSleep;           
        
/**
* delay value
*/
        public int delay=0;        

/**
* maximum of scrollbar
*/
        public int scrollMax;
/**
* range of scrollbar
*/
        public int scrollRange;
/**
* start of scrollbar
*/
        public int scrollStart;
/**
* increment of scrollbar
*/
        public int scrollPageIncrement;
/**
* lineIncrement of scrollbar
*/
        public int scrollLineIncrement;
/**
* 1 for scrollbar visible
*/
        public int scrollVisible;
/**
* top inset
*/
 
        public int borderTop;
/**
* left inset
*/
        public int borderLeft;
/**
* bottom inset
*/
        public int borderBottom;
/**
* right inset
*/
        public int borderRight;

/**
* array of delay values selected by slider
*/
        public int[] delayValue = {5000,  2000,1000,500,200,100,
                         50,  20, 10,    5,    2,     1};

/**
* Array of strings displayed by slider
*/
        public String[] delayString = {" 1/5"," 1/2"," 1"," 2"," 5"," 10",
                      " 20"," 50"," 100"," 200"," 500"," 1000"};                      

        private Label t;
        private String speed;
        private dynamicGraph outerparent;                

//**********************************************************************
/**
* @param target parent of type dynamicGraph
*/  
//**********************************************************************
        
       public movie(dynamicGraph target) {

            outerparent = target;
            toSleep=true;             
            scrollRange = delayValue.length - 1;
            scrollStart = 5;
            scrollPageIncrement = 4;
            scrollLineIncrement = 1;
            scrollVisible = 2; 
            borderTop=0;
            borderLeft=50;
            borderBottom=10;
            borderRight=50;            
//            setLayout(new GridLayout(1,1));
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            setLayout(gridbag);

            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridwidth = 1;
//            constraints.anchor = GridBagConstraints.EAST;

            Label l1 = new Label(" Speed ");
            gridbag.setConstraints(l1, constraints);
            add(l1);

            speedScroll = new Scrollbar(Scrollbar.HORIZONTAL,
                  scrollStart,scrollVisible,0,scrollRange);
            speedScroll.setLineIncrement(scrollLineIncrement);
            speedScroll.setPageIncrement(scrollPageIncrement);
            delay = delayValue[scrollStart];
            speedScroll.setValue(scrollStart);
            outerparent.updateSpeed(delay);
            
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 5.0;
            constraints.gridwidth=4;
            gridbag.setConstraints(speedScroll, constraints);
            add(speedScroll);
           
            constraints.weightx = 1.0;
            constraints.gridwidth=GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.EAST;
            t = new Label(delayString[scrollStart]);
            gridbag.setConstraints(t, constraints);
            add(t);            
        }

//**********************************************************************
/**
* Change parameters of scroll bar according to value of scroll.. 
* variables
*/
//**********************************************************************
        
        public void setScroll() {
            delay = delayValue[scrollStart];
            speedScroll.setValues(scrollStart,scrollVisible,0,scrollRange);
            t.setText(delayString[scrollStart]);
            outerparent.updateSpeed(delay);
        }    

//**********************************************************************
/**
* Handle scrollbar events to change delay with scroll bar  
*/  
//**********************************************************************

        public boolean handleEvent(Event evt) {
            if(evt.target instanceof Scrollbar) {
                int v =((Scrollbar) evt.target).getValue();
                delay = delayValue[v];
//              System.out.println("Scroll event: v= "+ v
//                  +" delay= "+delay);
                ((Scrollbar) evt.target).setValue(v);
                t.setText(delayString[v]);
                outerparent.updateSpeed(delay);     // dummy routine if no zction needed
                return super.handleEvent(evt);
            }
            else return false;
       }        
//**********************************************************************
/**
* Thread run. Sleep time set by delay, usually set by scroller 
*/ 
//**********************************************************************

        public void run() {
           boolean iterated=true;
           while (iterated) { 
                iterated=outerparent.iterate();
                Thread.yield();
//                System.out.println(it + " " + outerparent.isVisible() + 
//                              " " + outerparent.isShowing());
//                if(! outerparent.isVisible()) Thread.stop();

//                outerparent.graph.clearAll=false;
//                outerparent.graph.repaint();      
               if(delay > 0 && toSleep) {
                    try { 
                        Thread.sleep(delay);}
                    catch (InterruptedException e) {
                        return;
                    }
                }   
            }           
        }

//**********************************************************************
/**
*  Sets delay. (Note: scroll bar is not updated)
*/
//**********************************************************************        
        public void setDelay(int in_delay) {
            delay=in_delay;
        }
        
        
//**********************************************************************
/**
*  Sets insets for scroll bar
*/
//**********************************************************************        
        public Insets insets() {
            return new Insets(borderTop,borderLeft,
                borderBottom,borderRight);
       }
        }

//**********************************************************************
//**********************************************************************
