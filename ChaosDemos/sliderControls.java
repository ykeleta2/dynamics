package ChaosDemos;
import java.awt.*;
//*********************************************************************
/**
* Class for ntext labelled text fields updated by slider controls
* @version 2 August, 1997
* @author Michael Cross  
*/
//*********************************************************************
public class sliderControls extends Panel {

        private TextField[] t;
        private Panel[] p;
        private TextField increment;
        private Button bUp;
        private Button bDown;
        private Panel buttonPanel;
        private Panel topPanel;
        private Checkbox[] check;
        private CheckboxGroup cbg;
        private int ntext;           // number of text fields
        private int textLength;
        private double inc=0.01;
//        Frame parent=null;   // Parent frame for dialog boxes
        private dynamicGraph parent;

//*********************************************************************
/** 
* Adds n text fields of size length with values text[]
* labelled by l[]
* @param target parent class of type dynamicGraph
* @param text vector of fields of textboxes
* @param l vector of labels of textboxes
* @param n number of textboxes
* @param length length of textboxes
*/
//*********************************************************************
        
       public sliderControls (dynamicGraph target, String[] text,
           String[] l, int n, int length) {             
             parent = target;
             textLength = length;  
             ntext = n;
             setLayout(new BorderLayout());
             topPanel = new Panel();
             topPanel.setLayout(new GridLayout(n,1,0,0));
             bUp = new Button(">>");
             bDown = new Button("<<");
             buttonPanel = new Panel();
             t = new TextField[ntext];
             p = new Panel[ntext];
             cbg = new CheckboxGroup();
             check = new Checkbox[ntext];
             increment = new TextField(String.valueOf(inc),textLength);
             GridBagLayout gridbag = new GridBagLayout();
             GridBagConstraints constraints = new GridBagConstraints(); 
             constraints.insets = new Insets(0,0,10,0); 
             constraints.fill = GridBagConstraints.HORIZONTAL;
             constraints.gridx=GridBagConstraints.RELATIVE;
             constraints.gridheight = 1; 
             constraints.weightx=1.;              
             for(int i=0;i<n;i++) {
                t[i] = new TextField(text[i],textLength);
                p[i] = new Panel();
                check[i] = new Checkbox(l[i], cbg,false);
                p[i].setLayout(gridbag);
                topPanel.add(p[i]);
                constraints.gridwidth=1;                           
                gridbag.setConstraints(check[i], constraints);
                p[i].add(check[i]);
                constraints.gridwidth=GridBagConstraints.REMAINDER;;
                gridbag.setConstraints(t[i], constraints);
                p[i].add(t[i]);
             }
             add("Center",topPanel);
             
             cbg.setCurrent(check[0]);
             buttonPanel.setLayout(gridbag);
             add("South",buttonPanel);
             constraints.gridwidth=1;
             constraints.insets = new Insets(0,0,0,0);              
             gridbag.setConstraints(bDown, constraints);
             buttonPanel.add(bDown);
             constraints.gridwidth=GridBagConstraints.REMAINDER;
             gridbag.setConstraints(bUp, constraints);
             buttonPanel.add(bUp);
//             Label l1 = new Label("Change: ");
             constraints.weightx=0.;
             constraints.fill = GridBagConstraints.NONE;
             constraints.anchor = GridBagConstraints.EAST;

//             gridbag.setConstraints(l1, constraints);
//             buttonPanel.add(l1);
//             constraints.gridwidth=GridBagConstraints.REMAINDER;
             gridbag.setConstraints(increment, constraints);
             buttonPanel.add(increment);                                                               
      }      

/** 
* Adds n text fields of size length with values text[]
* labelled by l[]
* @param target parent class of type dynamicGraph
* @param text vector of fields of textboxes
* @param l vector of labels of textboxes
* @param n number of textboxes
* @param length length of textboxes
* @param inInc value of increment applied by sliders
*/
      public sliderControls (dynamicGraph target, String[] text, String[]
           l, int n, int length, double inInc) {
            this(target, text, l, n, length);
            inc=inInc;
      }
//**********************************************************************
//**  Constructor with parent frame target
//** 
//**********************************************************************

/*      sliderControls
         (dynamicGraph target, Frame frame_target, String[] text,
                String[] l, int n, int length, double inInc) {
            this(target, text, l, n, length, inInc);
            parent = frame_target;
      }
*/
//**********************************************************************
//**  Constructors with default size 8
//** 
//**********************************************************************      
/*      sliderControls (dynamicGraph target, Frame frame_target,
              String[] text, String[] l, int n, double inInc) {
          this(target, frame_target, text,l,n,8, inInc);
      }
*/      
/** 
* Adds n text fields of default size 8 with values text[]
* labelled by l[]
* @param target parent class of type dynamicGraph
* @param text vector of fields of textboxes
* @param l vector of labels of textboxes
* @param n number of textboxes
* @param inInc value of increment applied by sliders
*/

      sliderControls (dynamicGraph target, String[] text, String[] l,
               int n, double inInc) {
          this(target, text,l,n,8, inInc);
      }
      
//*********************************************************************
/**  Event handler: responds to slider events<br>
*    Calls <i>respondToSliderButtons()</i> and <i>respondToSliderText()</I>
*    methods in parent.
* @see dynamicGraph
*/   
//*********************************************************************

      public boolean action(Event evt, Object arg) {
         double d,dNew;
         int chosen=0;

         if(evt.target instanceof Button) {
           for(int i=0;i<ntext;i++) {
               if(check[i].getState()) chosen=i;
           }
           if (evt.target==bDown) {               
                d=(new Double(t[chosen].getText())).doubleValue();
                try {
                    inc=(new Double(increment.getText())).doubleValue();
                }
                catch (NumberFormatException e) {
                    alertDialog alert = new alertDialog
                                           (parent,"Increment must be a number");
                    inc=0.;                
                }
                t[chosen].setText(String.valueOf(d-inc));
           }
           else if(evt.target==bUp){
                d=(new Double(t[chosen].getText())).doubleValue();
                try {
                    inc=(new Double(increment.getText())).doubleValue();
                }
                catch (NumberFormatException e) {
                    alertDialog alert = new alertDialog
                                           (parent,"Increment must be a number");
                    inc=0.;                
                }
                t[chosen].setText(String.valueOf(d+inc));
           }
           parent.respondToSliderButtons();
           return true;
         }
         else if(evt.target instanceof TextField) {
            parent.respondToSliderText();
            return true;
         }
         else return false;    
      }
      

//*********************************************************************
/**
* Method to give number of controls
* @return number of controls
*/ 
//*********************************************************************
    
      public int ncontrols() {
           return ntext;
      }

//*********************************************************************
/**
* Parses text field known to be integer.  
* Resets old value of corresponding variable if input format
* is incorrect and brings up alertDialog warning box.
* @param n index of textbox to read
* @param i old value of variable
* @return new value of parameter if textbox format is correct,
* otherwise old value.
* @see alertDialog
*/
//*********************************************************************

    public int parseTextField(int n, int i) {
            int iNew;
            if(n>ntext) return i;
            try {
                iNew=(new Integer(t[n].getText())).intValue();
            }
            catch (NumberFormatException e) {
                t[n].setText(String.valueOf(i));
                alertDialog alert = new alertDialog(parent,"Try an integer");
                return i;
            }
            return iNew;
   }
//*********************************************************************
/**
* Parses text field known to be integer of known sign.  
* Resets old value of corresponding variable if input format
* is incorrect or wrong sign and brings up alertDialog warning box.
* @param n index of textbox to read
* @param i old value of variable
* @param positive true if value should be positive
* @return new value of parameter if textbox format is correct,
* and value of correct sign, otherwise old value.
* @see alertDialog
*/
//*********************************************************************

    public int parseTextField(int n, int i, boolean positive) {
            int iNew;
            if(n>ntext) return i;
            try {
                iNew=(new Integer(t[n].getText())).intValue();
            }
            catch (NumberFormatException e) {
                t[n].setText(String.valueOf(i));
                alertDialog alert = new alertDialog(parent,"Try an integer");
                return i;
            }
            if(((iNew < 0) && positive) || ((iNew>0) && !positive)) {
                t[n].setText(String.valueOf(i));
                alertDialog alert = new alertDialog(parent,"Must be positive");
                return i;
            }
            return iNew;
   }
//*********************************************************************
/**
* Parses text field known to be integer in known range. 
* Resets old value of corresponding variable if input format
* is incorrect orout of range and brings up alertDialog warning box.
* @param n index of textbox to read
* @param i old value of variable
* @param min minimum value of allowed range
* @param max maximum value of allowed range
* @return new value of parameter if textbox format is correct,
* and value in of range, otherwise old value.
* @see alertDialog
*/
//*********************************************************************

    public int parseTextField(int n, int i, int min, int max) {
            int iNew;
            if(n>ntext) return i;
            try {
                iNew=(new Integer(t[n].getText())).intValue();
            }
            catch (NumberFormatException e) {
                t[n].setText(String.valueOf(i));
                alertDialog alert = new alertDialog(parent,"Try an integer");
                return i;
            }
            if((iNew < min) || (iNew > max)) {
                t[n].setText(String.valueOf(i));
                alertDialog alert = new alertDialog(parent,"Must be between" + min 
                        +" and "+max);
                return i;
            }
            return iNew;
   }   
//*********************************************************************
/**
* Parses text field known to be double. 
* Resets old value of corresponding variable if input format
* is incorrect and brings up alertDialog warning box.
* @param n index of textbox to read
* @param d old value of variable
* @return new value of parameter if textbox format is correct,
* otherwise old value.
* @see alertDialog
*/
//*********************************************************************
   
    public double parseTextField(int n, double d) {
            double dNew;
            if(n>ntext) return d;
            try {
                dNew=(new Double(t[n].getText())).doubleValue();
            }
            catch (NumberFormatException e) {
                t[n].setText(String.valueOf(d));
                alertDialog alert = new alertDialog
                                           (parent,"Must be a number");
                return d;
            }
            return dNew;
   }

//*********************************************************************
/**
* Parses text field known to be double and in known range 
* Resets old value of corresponding variable if input format
* is incorrect or out of range and brings up alertDialog warning box.
* @param n index of textbox to read
* @param d old value of variable
* @param min minimum value of allowed range
* @param max maximum value of allowed range
* @return new value of parameter if textbox format is correct,
* and value in range, otherwise old value .
* @see alertDialog
*/
//*********************************************************************
   
    public double parseTextField (int n, double d, double min, double max) {
            double dNew;
            if(n>ntext) return d;
            try {
                dNew=(new Double(t[n].getText())).doubleValue();
            }
            catch (NumberFormatException e) {
                t[n].setText(String.valueOf(d));
                alertDialog alert = new alertDialog
                                           (parent,"Must be a number");
                return d;
            }
            if( (dNew < min) || (dNew > max) ) {
                t[n].setText(String.valueOf(d));
                alertDialog alert = new alertDialog
                     (parent,"Must be between " + min + " and " + max);
                return d;
            }
            return dNew;
   }   

//*********************************************************************
/**
* Sets value of ith textbox
* @param i index of textbox
* @param text value to be set
*/ 
//********************************************************************* 
   public void setText(int i, String text) {
      t[i].setText(text);
   }

//*********************************************************************
/**
* Gets value of ith textbox
* @param i index of textbox
* @return content of textbox
*/ 
//*********************************************************************   
   public String getText(int i) {
      return t[i].getText();
   }   

}


//*********************************************************************
//*********************************************************************
