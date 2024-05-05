package ChaosDemos;
import java.awt.*;
//*********************************************************************
/**  Class to install n labelled buttons.<br>  
*    Enables and disables buttons depending on   
*    b_init, b_started and b_stoped.
* @version March 15 1997
* @author Michael Cross
*/
//*********************************************************************

public class buttonControls extends Panel {
        private dynamicGraph outerparent;
        private Button[] b;
        private Panel panel1;
/**
* vector: set element to true for this button to be enabled on
* initializing
*/
        public boolean[] b_init;
/**
* vector: set element to true for this button to be enabled on
* starting iteration
*/
        public boolean[] b_started;
/**
* vector: set element to true for this button to be enabled on 
* stopping iteration
*/     
        public boolean[] b_stopped;
/**
* set to true for horizontal arrangement of buttons
*/        
        public boolean horizontal=false;
        private String text[];
        private int n_but;

//*********************************************************************
/** 
* @param target parent of type dynamicGraph
* @param intext vector of button labels
* @param n number of buttons
*/  
//*********************************************************************
        
        public buttonControls (dynamicGraph target, String[] intext,
              int n) {
            
             outerparent = target;
             n_but=n;
             panel1 = new Panel();
             add(panel1);
             text = new String[n];
//             if(horizontal) panel1.setLayout(new GridLayout(1,n,10,10));
//             else panel1.setLayout(new GridLayout(n,1,10,10));
             b = new Button[n];
             b_stopped = new boolean[n];
             b_started = new boolean[n];
             b_init=new boolean[n];
             for(int i=0;i<n_but;i++) {
                    text[i]=intext[i];
                    b_init[i] = false;
                    b_started[i] = false;
                    b_stopped[i] = true;
             }
         }

//*********************************************************************
/**
* @param target parent of type dynamicGraph
* @param intext vector of button labels
* @param n number of buttons
* @param inHorizontal true for horizontal arrangement of buttons
*/
//*********************************************************************          
         public buttonControls (dynamicGraph target, String[] intext,
                int n, boolean inHorizontal) {
                this(target, intext, n);
                horizontal = inHorizontal;
         }

//*********************************************************************
/**  Initial setup of buttons  
*  Call after setting b_init
*/
//*********************************************************************
         
         public void setup() {    
             if(horizontal) panel1.setLayout(new GridLayout(1,n_but,10,0)); // Chnaged from 10,10
             else panel1.setLayout(new GridLayout(n_but,1,0,10));           // 8/10                
             for(int i=0;i<n_but;i++) {
                  b[i] = new Button(text[i]);
                  panel1.add(b[i]);
                  if( b_init[i]) b[i].enable();
                  else b[i].disable();
             }
         }
//*********************************************************************
/**  Enable all buttons
*/  
//*********************************************************************

      public void enableAll() {
           for(int i=0;i<n_but;i++) {
                b[i].enable();
           }
      }

//*********************************************************************
/**  Disable all buttons
*/  
//*********************************************************************

      public void disableAll() {
           for(int i=0;i<n_but;i++) {
                b[i].disable();
           }
      }
            
//*********************************************************************
/**  Set buttons to stopped configuration  
*/  
//*********************************************************************

      public void enableGo() {
           for(int i=0;i<n_but;i++) {
                if(b_stopped[i]) b[i].enable();
                else b[i].disable();
           }
      }

//*********************************************************************
/**  Set buttons to iterating configuration
*/ 
//*********************************************************************
      
      public void disableGo() {
           for(int i=0;i<n_but;i++) {
                    if(b_started[i]) b[i].enable();
                    else b[i].disable();
           }
      }
//*********************************************************************
/**  Handle button events: calls respondToButtons(int i) in
* parent.  
*/ 
//*********************************************************************
      
      public boolean action(Event evt, Object arg) {             
          if(evt.target instanceof Button) {
            for(int i=0;i<n_but;i++) {
                if(evt.target==b[i]) {
                    outerparent.respondToButtons(i);
                }
            }
            return true;
          }
          else return false;
      }
}

//*********************************************************************
//*********************************************************************
