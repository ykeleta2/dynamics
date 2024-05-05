import java.awt.*;
import java.applet.*;
/*************************************************************************
**************************************************************************
**
**
*************************************************************************/

public class startMorioka extends Applet {

/** true if window showing */   
        public boolean showing=false;
        private Morioka win;
        private Label l1;
        private Button b1;
        private String loading = "Please wait...loading!";
        private String startApp= "Click to start application!: ";
        private String stopApp=  "Click to stop  application!: ";
        
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
                win = new Morioka (this);
                win.setTitle("Lorenz Attractor in Morioka-Shimizu model");
                win.setBackground(Color.white);
                win.resize(820,520);
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
//            System.out.println("Button down");
//            System.out.println("Showing = " + showing);
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
