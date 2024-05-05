package ChaosDemos;
//*********************************************************************
import java.awt.*;
/**
* Class to implement warning dialog box.<br>
* Uses Frame instead of Dialog since some java machines
* do not implement the Dialog class yet.<br>
* If the parent Frame is known, window pops up in pleasing 
* relationship to parent window
* @version March 15 1997
* @author Michael Cross
*/
public class alertDialog extends Frame {

        private Button b;
        private Frame parent=null;

//**********************************************************************
/**
*   Constructor: default window position is used
* @param message message to be displayed
*/
//**********************************************************************
        public alertDialog(String message) {
              addNotify();
              reshape(100,100,300,120);
              showWindow(message);
        }

//**********************************************************************
/**  Constructor with parent Frame. Window pops up in pleasign 
* relationship to parent. If null is passed a default
* position is used for the popup window.
* @param target reference to parent window
* @param message text to be displayed
*/
//**********************************************************************
        
        public alertDialog(Frame target, String message) {        

            parent = target;
            addNotify();
            if(parent != null) {
                Rectangle r = parent.bounds();
                if(r.width > 0 && r.height >0)
                    reshape(r.x+r.width/3,r.y+r.height/3,300,120);
                else reshape(100,100,300,120);
            }
            else reshape(100,100,300,120);
            showWindow(message);
        }
//**********************************************************************
//**  Create window with error message
//**********************************************************************
        
        void showWindow(String message) {
            setTitle("Data Entry Alert!");
            setLayout(new FlowLayout(FlowLayout.CENTER));
            TextField t =new TextField(" "+message+" "); 
            add(t);
            Color c = getBackground();
            t.setBackground(c);
            t.setEditable(false);

            b = new Button("OK");
            add("South",b);
            show();        
        }
    
//**********************************************************************
//**  Button disposes of window
//**********************************************************************

    public boolean action(Event evt, Object arg) {
         if(evt.target instanceof Button) {
              dispose();
              return true;
         }
         else return false;
    }
//**********************************************************************
//**  Implement close window. Also dispose on iconify.
//**********************************************************************

    public boolean handleEvent(Event evt) {
         switch (evt.id) {
           case Event.WINDOW_DESTROY:
            dispose();
            return true;
           case Event.WINDOW_ICONIFY:
            dispose();
            return true;            
           default:
            return super.handleEvent(evt);
        }
    }
}
//**********************************************************************
//**********************************************************************
