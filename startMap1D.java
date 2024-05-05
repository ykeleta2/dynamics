import java.awt.*;
import java.applet.*;
/*************************************************************************
**************************************************************************
**
**
*************************************************************************/

public class startMap1D extends Applet {

/** true if window showing */ 
        public boolean showing=false;
        private Map1D win;
        private Label l1;
        private Button b1;
        private String loading = "Please wait...loading!";
        private String startApp= "Click here to start application: ";
        private String stopApp=  "Click here to stop application: ";
        
        public void init() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            l1 = new Label(startApp);
            add(l1);
            b1 = new Button("Start");
            add(b1);
        }
        
        public void showWindow() {
                b1.disable();
                l1.setText(loading);
                win = new Map1D (this);
                win.setTitle("1D Maps");
                win.setBackground(Color.white);
                win.resize(640,480);
                win.show();
                showing=true;
                b1.setLabel("Stop");
                b1.enable();
                l1.setText(stopApp);            
        }
        
        public void hideWindow() {
                win.movieStop();
                win.dispose();
                b1.setLabel("Start");
                l1.setText(startApp);
                showing=false;        
        }
/**********************************************************************/
      
      public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Button) {
            if(showing) {
                 hideWindow();
            }
            else {
                 showWindow();
            }
            return true;
       }
       else return false;          
    }    
}

/**********************************************************************/
/**********************************************************************/
