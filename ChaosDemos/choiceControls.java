package ChaosDemos;
import java.awt.*;
//*********************************************************************
/**  Class to install n labelled yes/no choices.<br>  
* @version March 15 1997
* @author Michael Cross
*/
//*********************************************************************

public class choiceControls extends Panel {
        private dynamicGraph outerparent;
        private CheckboxGroup[] cbg;
        private Label[] l;
        private  Checkbox[] cbYes, cbNo;
        private Panel panel1,panel2;
        private int n_ch;


//*********************************************************************
/** 
* @param target parent of type dynamicGraph
* @param intext vector of button labels
*/  
//*********************************************************************
        
        public choiceControls (dynamicGraph target, String[] intext) {
            
             outerparent = target;
             n_ch=intext.length;
             setLayout( new GridLayout(1,2,0,0));
             panel1 = new Panel();
             panel2 = new Panel();
             add(panel1);
             add(panel2);

             cbg = new CheckboxGroup[n_ch];
             cbYes = new Checkbox[n_ch];
             cbNo = new Checkbox[n_ch];
             l = new Label[n_ch];
             
            panel1.setLayout(new GridLayout(n_ch,1,0,0));  
            panel2.setLayout(new GridLayout(n_ch,2,0,0));             
             for(int i=0;i<n_ch;i++) {
             
                    l[i]= new Label(intext[i]);
                    panel1.add(l[i]);
                    cbYes[i]=new  Checkbox("Yes");
                    cbNo[i]=new  Checkbox("No ");
                    cbg[i]=new CheckboxGroup();
                    cbYes[i].setCheckboxGroup(cbg[i]);
                    cbNo[i].setCheckboxGroup(cbg[i]);
                    panel2.add(cbYes[i]);
                    panel2.add(cbNo[i]);
                    cbYes[i].setState(true);
             }
        
         }
//*********************************************************************
/** Sets ith yes/no
* @param i index
* @param yesno true for yes; false for no
*/  
//*********************************************************************
         public void setState(int i, boolean yesno) {
            if(i<n_ch) {
                  cbYes[i].setState(yesno);
            }      cbNo[i].setState(!yesno);
         }
//*********************************************************************
/** Gets ith yes/no
* @param i index
* @return true for yes; false for no
*/  
//*********************************************************************         
         public boolean getState(int i) {
            if(i < n_ch) return cbYes[i].getState();
            else return false;           
         }         
         
//*********************************************************************
/**  Handle button events: calls respondToButtons(int i) in
* parent.  
*/ 
//*********************************************************************
      
      public boolean action(Event evt, Object arg) {             
          if(evt.target instanceof Checkbox) {
            outerparent.respondToChoices();
            return true;
          }
          else return false;
      }
}

//*********************************************************************
//*********************************************************************
