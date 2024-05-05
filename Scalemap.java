import ChaosDemos.*;
import java.awt.*;
import java.util.*;

/**********************************************************************
***********************************************************************
/** Iterates and plots the quadratic and sine maps, and demonstrates
* functional composition and rescaling.<br>
* Uses the "Java Graph Class Library" by Leigh Brrokshaw
* @author Michael Cross
* @version 3 August, 1997
*/
/**********************************************************************/

public class Scalemap extends dynamicGraph {

/** Number of points in plot of f(x) */
      int np = 200;
/** nit = 2^nf -> functional composition */
      int nit=1;
/** number of curves */
      int ncurve=-1;
/** index of iteration curve */      
      int iterateCurve;
/** iterations to eliminate transients  */
      int trans=0;
/** compositions -> nit */
      int nf=0;
/** scaling factor: f and x are rescaled by (-alpha)^nsc */
      int nsc=0;
/** sleep delay in ms in dynamic iteration */
      int delay=50;
/** function: 0=Quadratic; 1=Sine */
      int nfun=0;
/** map variable */
      double x;               // x
/** iterate of x */      
      double f; 
/** map parameter */
      double a=3.7;
/** string value of a: used because of string truncation */    
      String atext;   
/** initial value of x - scale */
      double xmin=1.0;
/** rescaled value of x- scale  */
      double xscale;
/** starting value of x */
      double x0=0.35;

/** constants */      
      private static double alpha=-2.502907875;
      private static double Pi=3.141592654;

/** GUI items */      
      private textControls variables,acontrol;
      buttonControls buttons;
      private scalemapControls choices;
      private Panel leftPanel;

/** parent */      
      private startMap outerparent;      
/** animation thread */
      private Thread aThread=null;
/** classes used */            
      private superGraph2D graph;
      private movie theMovie;      

//*********************************************************************
/**
* @param target starting class
* @see startMap
*/
//*********************************************************************
        Scalemap(startMap target) {
        graph = new superGraph2D(this);
        theMovie = new movie(this);        
        xscale=xmin;
        x=(x0-0.5)*xscale; 
        this.outerparent = target;   

        setLayout( new GridLayout(1,2) );
        
        graph.setXAxisTitle("x");     
        graph.setYAxisTitle("f(x)");  
        leftPanel = new Panel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add("Center",graph);
        leftPanel.add("South",theMovie);
        theMovie.scrollStart = 4;
        theMovie.setScroll();
              
        add(leftPanel);           

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
 
        Panel rightPanel = new Panel();
        rightPanel.setLayout(gridbag);
        add(rightPanel);

//      c.fill = GridBagConstraints.BOTH;
        c.gridheight = 2;
        c.gridwidth=1;
        c.weightx=1.;
        c.weighty = 1.0;
        String[] buttonLabels={"   Reset    ","   Clear   ",
                   "    Step   ","    Start   ","    Stop   "};
        buttons = new buttonControls((dynamicGraph) this, 
                        buttonLabels,buttonLabels.length);
        buttons.b_init[0] = true;
        buttons.b_stopped[4] = false;
        buttons.b_started[1] = true;
        buttons.b_started[4] = true;
        buttons.setup();
        gridbag.setConstraints(buttons, c);
        rightPanel.add(buttons);

        String[] textboxes = {String.valueOf(nf),String.valueOf(nsc),
                    String.valueOf(x0),String.valueOf(xmin),
                    String.valueOf(trans)};
        String[] labels = {"    nf","   nsc","    x0",
                             "scale"," trans" };
        variables = new 
              textControls((dynamicGraph) this,textboxes,labels,textboxes.length);        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(variables, c);
        rightPanel.add(variables);
                           
        String[] abox={String.valueOf(a)};
        String[] alabel={" a = "};
        acontrol = new textControls((dynamicGraph) this,abox,alabel,1);
        c.gridheight = 1;
        c.gridwidth=GridBagConstraints.REMAINDER;        
        gridbag.setConstraints(acontrol, c);
        rightPanel.add(acontrol);

        choices = new scalemapControls(this,4);
        c.weightx = 0.0;
        c.weighty=0.0;
        c.gridheight=1; 
        gridbag.setConstraints(choices, c);
        rightPanel.add(choices);
        graph.clearAll=true;
        repaint();
        
        buttons.enableGo();
        updateParameters();        
      }

//**********************************************************************      
/**
* Respond to buttonControls
* @see      buttonControls
* @param    buttonIndex index of button pushed
*/          
//********************************************************************** 
      public void respondToButtons(int buttonIndex) {
            boolean dummy;
            if(buttonIndex==0) {
                 buttons.enableGo();
                 updateParameters();  
            }
            if(buttonIndex==2) {
                dummy=iterate();
                graph.clearAll=false;
                graph.repaint();
            }
            else if(buttonIndex==1) {
                graph.clearAll=false; //6/27/96
                if(aThread != null) {
                  movieStop();
                  restart();
                  movieStart();
                }
                else restart();                
            }
//            else if(buttonIndex==2) {
//                 graph.clearAll=true;
//                 graph.repaint();
//                 }
            else if(buttonIndex==3) {
                buttons.disableGo();
                disableAll();
                movieStart();
            }
            else if(buttonIndex==4) {
                buttons.enableGo();
                movieStop();
                enableAll();
            }
      }

//**********************************************************************
/**
* Responds to textControls
* @see textControls
*/      
//**********************************************************************      
      public void respondToText() {
          updateParameters();
          buttons.enableGo();
      }      

//*********************************************************************
/**
* Stop movie thread 
*/  
//*********************************************************************
      public void movieStop() {
                if(aThread!=null) {
                    aThread.stop();
                    aThread=null;
                } 
      }
//*********************************************************************
/**
* Start movie thread 
*/  
//*********************************************************************

      public void movieStart() {
                if(aThread==null) {
                    aThread = new Thread(theMovie);
//                    aThread.setPriority(Thread.MIN_PRIORITY);
                    aThread.start();                    
                }
      }

//************************************************************************
/**
* Event handler:
* Stops iteration on minimising and handles close window event<br>
* (May fail under Windows95)
*/
//************************************************************************     
        public boolean handleEvent(Event evt) {
            switch (evt.id) {
                case Event.WINDOW_DESTROY:
                    movieStop();
                    outerparent.hideWindow();
                    return super.handleEvent(evt);
                case Event.WINDOW_ICONIFY:
                    movieStop();
                    buttons.enableGo();
                    enableAll();
//                  System.out.println("Window minimized");
                    return super.handleEvent(evt);              
                default:
                    return super.handleEvent(evt);
            }
       }
       
//*********************************************************************
/**
* Disables text input and choice contorls
*/ 
//*********************************************************************
      
      public void disableAll() {
          for(int i=0;i<variables.ncontrols();i++)
                                     variables.disableText(i);
          for(int i=0;i<choices.ncontrols();i++) {
            choices.t[i].disable();
            choices.c[i].disable();
          }
          acontrol.disableText(0);
      }

//*********************************************************************
/**
* Enables text input and choice contorls
*/ 
//*********************************************************************

      public void enableAll() {
          for(int i=0;i<variables.ncontrols();i++)
                                    variables.enableText(i);
          for(int i=0;i<choices.ncontrols();i++) {
            choices.t[i].enable();
            choices.c[i].enable();
          }
          acontrol.enableText(0);
      }

//*********************************************************************
/**
* Resets the parameter a and function number. Stores atext 
* for later comparison (because string truncates accuracy of double)
*/
//*********************************************************************
      
      public void seta(double ain, int n) {
           nfun=n;
           a=ain;
           atext=String.valueOf(a);
           acontrol.setText(0,atext);
           atext=acontrol.getText(0);
      }

//*********************************************************************
/**
* Returns map function of class variable x 
* @param n number of iterations
*/ 
//*********************************************************************

      public double function(int n) {
            double xit = x;
            if(nfun==0) {
                for(int i=1;i<=n;i++) {
                    xit=a*(0.25-xit*xit)-0.5;
                }
            }
            else if (nfun==1) {
                for(int i=1;i<=n;i++) {
                    xit=0.25*a*Math.cos(Pi*xit)-0.5;
                }
            }
            return xit;
      }

//*********************************************************************
/**
* Updates parameters from the text controls 
*/ 
//*********************************************************************
      
      public void updateParameters() {
            int i;
            
            if(nfun==0) graph.setTitle("Quadratic Map");
            else if(nfun==1) graph.setTitle("Sine Map");           
//          Update a value only if entered directly into text box            
            if(! atext.equals(acontrol.getText(0))) {
                a=acontrol.parseTextField(0,a, 0., 4.);
                atext=acontrol.getText(0);
            }

            xmin=variables.parseTextField(3, xmin);
            x0=variables.parseTextField(2, x0, 0., 1.);
//            x0=x0-Math.rint(x0);
//            if(x0<0) x0++;
            variables.setText(2,String.valueOf(x0));
            trans=variables.parseTextField(4,trans,true);
            nf=variables.parseTextField(0,nf,true);
            for(i=1,nit=1;i<=nf;i++) {nit=nit*2;}
            nsc=variables.parseTextField(1,nsc,true);
            for(i=1,xscale=xmin;i<=nsc;i++) { 
                xscale=xscale/alpha;
            }
            if(xscale!=1.0) {
                graph.setXAxisTitle     //MCC 3/19/97
                       ("x: Full scale = " + String.valueOf(xscale));
            }
            else graph.setXAxisTitle("x");   //MCC 3/19/97
            x=(x0-0.5)*xscale;
            x=function(nit*trans);
            restart();
      }      

                
//*********************************************************************
/**
* Resets plot by deleting all curves and restarting
*/ 
//*********************************************************************
      
      public void restart() {
            int i,j,ip;
            double data[] = new double[2*np];
            double xb;      
            
            graph.clearAll=true;
            xb=x;
            if(ncurve>=0) ncurve = graph.deleteAllCurves();

            data[0]=-0.5+0.5;
            data[1]=-0.5+0.5;
            data[2]=0.5+0.5;
            data[3]=0.5+0.5;

            ncurve = graph.addCurve(data,2,Color.black);
            ip=0;
            for(i=j=0; i<np; i++) {
                x =xscale*(-0.5+((double) i)/((double)(np-1)));
                double xdum=x/xscale;
                f = function(nit);
                double fdum=f/xscale;
                if((xdum>=-0.5) && (xdum<=0.5) && (fdum>=-0.5) && (fdum<=0.5)){
                      data[j] = xdum+0.5;
                      data[j+1] =fdum+0.5;
                      j+=2;
                      ip++;
//                    System.out.println("ip= "+ip+" xdum= "+xdum+" fdum= "+fdum);
                }
/*                if(xdum>=-0.5) {
                  if (xdum<=0.5){
                     if(fdum>=-0.5){
                       if(fdum<=0.5){
                              data[j] = xdum+0.5;
                              data[j+1] =fdum+0.5;
                              j+=2;
                              ip++;
                              System.out.println("i[= "+ip+" xdum= "+xdum+" fdum= "+fdum);
                       }
                     }
                  }
                }    */
                x=f;
            }
            if(ip > 2) {
                  ncurve = graph.addCurve(data,ip,Color.red);
            }
            x=xb;            
            data[0]=x/xscale+0.5;
            data[1]=x/xscale+0.5;
            f = function(nit);
            data[2]=x/xscale+0.5;
            data[3]=f/xscale+0.5;
        
            ncurve = graph.addCurve(data,2,Color.blue);
            iterateCurve = ncurve;
//            graph.clearAll= false ;
            graph.paintAll=true; 
            graph.repaint();
      }

//*********************************************************************
/**
* Iterates class variables via map functions x->f f->f(x)  
*/ 
//*********************************************************************
      
      public boolean iterate() {
            
            double[] moredata = new double[6];
            moredata[0]=x/xscale+0.5;
            moredata[1]=f/xscale+0.5;
            moredata[2]=f/xscale+0.5;
            moredata[3]=f/xscale+0.5;
            moredata[4]=f/xscale+0.5;
            x=f;
            f = function(nit);
            moredata[5]=f/xscale+0.5;
            // Don't paint while updating data
            graph.paintAll=false;
//            To keep number of points in plot down            
//            if(graph.nPoints(iterateCurve)>200) {
//                  graph.deleteFromCurve(100,iterateCurve);
//            }       
            graph.appendToCurve(moredata,3,iterateCurve);
            graph.paintAll=true;
            graph.clearAll=false;
            graph.repaint();
            return true;
      }

//*********************************************************************
/**
* Stop thread
*/ 
//*********************************************************************
      
      public void stop() {
            movieStop();
            enableAll();
      }                            

//*********************************************************************
/**
* Restarts iteration on mouse click
* @param xcoord x-coordinate of event
* @param xcoordValid true if event within x-range set by axes
* @param ycoord y-coordinate of event
* @param ycoordValid true if event within y-range set by axes
*/  
//*********************************************************************
    public void respondToMouse(double xcoord, boolean xcoordValid,
                 double ycoord, boolean ycoordValid) {
         if(xcoordValid) {
             variables.setText(2,String.valueOf(xcoord));
             if(aThread != null) {
                  movieStop();
             }
             updateParameters();
             buttons.disableGo();
             disableAll();
             movieStart();
         }             
    }

}   

/*********************************************************************/
//*********************************************************************
/**
* Adds choices and associated text fields.
*/  
//*********************************************************************

class scalemapControls extends Panel {

/** parent */
        private Scalemap outerparent;
        Choice[] c;
        TextField[] t;        
/** critical a for each function */        
        private static double acA[]={3.5699456,3.4623171};
/** delta-a for accumulation and function choices */        
        private static double delaA[][]={{1.5561,0.570,0.485},
                                    {1.6821,0.614,0.5275}};
/** universal ratio */                                    
        private static double delta=4.6692016;        
/** parameter of map */        
        double a;        
/** critical value of a */        
        double ac;
/** delta a */        
        double dela;
/** approach to critical point */        
        int n;
/** sign giving above or below critical point */        
        int nsign;
/** number of choice controls */        
        int nchoice;
/** true if last ac set by choice not text field */
        boolean precise;
/** true if event handled */        
        boolean eventHandled;
        
//*********************************************************************
/**
* @param target parent of type Scalemap
* @param n number of controls
*/
//*********************************************************************
        
        scalemapControls(Scalemap target,int n) {
            eventHandled=false;
            outerparent=target;
            nchoice = n;
            setLayout(new GridLayout(2,1));
            Panel panelTop = new Panel();
            Panel panelBottom = new Panel();
            panelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
            panelBottom.setLayout(new FlowLayout(FlowLayout.LEFT));
            add(panelTop);
            add(panelBottom);
            
            c = new Choice[nchoice];
            t = new TextField[nchoice];
            c[0] = new Choice();
            c[0].addItem("Quadratic");
            c[0].addItem("Sine");
            t[0] = new TextField(String.valueOf(acA[0]),8);
            c[1]=new Choice ();
            c[1].addItem("+");
            c[1].addItem("-");
            c[1].select(1);
            nsign=-1;
            t[1] = new TextField("-",2);
            c[2]=new Choice();
            c[2].addItem("SuperStable");
            c[2].addItem("Instability");
            c[2].addItem("Bandmerge");
            t[2] = new TextField(String.valueOf(delaA[0][0]),5);
            c[3]=new Choice ();
            c[3].addItem("0");
            c[3].addItem("1");
            c[3].addItem("2");
            c[3].addItem("3");
            c[3].addItem("4");
            c[3].addItem("5");
            c[3].addItem("6");
            c[3].addItem("7");
            c[3].addItem("8");
            c[3].addItem("I");
            c[3].select(9);
            t[3] = new TextField("I",2);
            Label l1 = new Label("a=");
            Label l2 = new Label("x d ^ -");
            panelTop.add(t[0]);
            panelTop.add(t[1]);
            panelTop.add(t[2]);
            panelTop.add(l2);
            panelTop.add(t[3]);
            panelBottom.add(c[0]);
            panelBottom.add(c[1]);
            panelBottom.add(c[2]);
            panelBottom.add(c[3]);
            precise=true;
            outerparent.seta(acA[0],c[0].getSelectedIndex());
      }

//*********************************************************************
/**
* Insets for layout  
*/  
//*********************************************************************
      
      public Insets insets() {
            return new Insets(0,0,20,0);
      }
      
//*********************************************************************
/**  Respond to events.
*  Updates associated text field for choice and vice versa
*  Updates a and nfun in parent
*/
//*********************************************************************
      public boolean action(Event evt, Object arg) {
        eventHandled=false;
        if(evt.target instanceof Choice) {
            if(evt.target==c[0]) {
                ac=acA[c[0].getSelectedIndex()];
                t[0].setText(String.valueOf(ac));
                dela=delaA[c[0].getSelectedIndex()][c[2].getSelectedIndex()];
                t[2].setText(String.valueOf(dela));
                precise=true;
            }
            else if(evt.target==c[1]) {
                if(c[1].getSelectedIndex()==0) {
                    t[1].setText("+");
                    nsign=1;
                }
                else {
                    t[1].setText("-");
                    nsign=-1;
                }
            }    
            else if(evt.target==c[2]){
                dela=delaA[c[0].getSelectedIndex()]
                    [c[2].getSelectedIndex()];
                t[2].setText(String.valueOf(dela));
            }
            else if(evt.target==c[3]){
                  t[3].setText(c[3].getSelectedItem());
            }
            eventHandled=true;
        }
        else if(evt.target instanceof TextField) {
            if(evt.target==t[0]) precise = false;
            else if (evt.target==t[1]) {
                if(t[1].getText().trim().equals("+")) {
                    nsign=1;
                    t[1].setText("+");
                    c[1].select(0);                
                }
                else {
                    t[1].setText("-");
                    nsign=-1;
                    c[1].select(1);
                }
            }
            eventHandled=true;
        }      
        if(precise) ac=acA[c[0].getSelectedIndex()];
        else ac=(new Double(t[0].getText())).doubleValue();
        dela=(new Double(t[2].getText())).doubleValue();
        try {
                n=Integer.parseInt(t[3].getText());
                c[3].select(n);
                a=ac+nsign*dela*Math.pow(delta,(double)(-n));          
        }
        catch (NumberFormatException e) {
            c[3].select(9);
            t[3].setText("I");
            a=ac;
        }
        outerparent.seta(a,c[0].getSelectedIndex());
        outerparent.updateParameters();
        outerparent.buttons.enableGo();
        if(eventHandled) return true;
        else return false;
      }

//*********************************************************************
/**
* Number of controls
* @return number of controls
*/
//*********************************************************************     
        public int ncontrols() {
             return nchoice;
        }      


}
/*********************************************************************/
/*********************************************************************/
