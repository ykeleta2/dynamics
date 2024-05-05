import ChaosDemos.*;
import java.awt.*;
import java.util.*;

/**                    
* Iterates and plots the Chua model <br> 
* Uses the "Java Graph Class Library" by Leigh Brookshaw
* @version 3 August, 1997
* @author Michael Cross
*/

public class Chua extends dynamicGraph  {
/** number of curves */
      int ncurve=-1;  
/** index of data curve */
      int ncurve1;
/** variable to be plotted on x-axis */
      int plot_x=1;
/** variable to be plotted on x-axis */      
      int plot_y=2;
/** delay in graph update , and factor reducing time step */
      int delay=50;
/** transient time */
      double trans=50.;
/** number of iterations to eliminate transient = trans/dt */
      int ntrans;
/** number of variables (icluding time) */
      int nVariables=4;
/** true if transient is to be run on call to restart() */
      boolean runTrans; 
/** true if time to be shown. Set by choices */
      boolean showTime;
/** evolving time */
      double t=0.;
/** time step (after reduction by delay) */
      double dt=1.0;
/** input time step */
      double dtp=1.0;
/** evolving variables */      
      double[] x={0.,0.,0.,0.};
/** parameter of equations R1/R */   
      double a;                
/** parameter of equations (R2-R1)/R2 */      
      double b;
/** parameter of equations R1*R1*C1/L */        
      double c;
/** parameter of equations C1/C2 */        
      double r;
/** parameter of equations  r/R1 */        
      double rl;
/** inital values of R,R1,R2,C1,C2,L,r */        
      double[] params={1.3,1.2,3.3,4.6,69.0,8.5,0.085};
/** starting values of x */      
      double[] x0={0.,0.1,0.15,0.05};  
/** error estimates from ode solver */      
      double err,errp;      
/** axis labels */      
      String[] axisLabel={"Time","  X ","  Y ","  Z "};
/** Color of ghost */      
      Color transientColor;  
/** Thread to animate movie*/      
      private Thread aThread=null;
/** parent class */      
      private startChua outerparent;           
/** GUI item */      
      private textControls variables;
/** number of parameters set by sliders */      
      int numSliders=7;
/** GUI item */            
      private sliderControls parameters;
/** GUI item */   
      private buttonControls buttons;
/** GUI item */   
      private Panel leftPanel;
/** GUI item */         
      private Panel bottomPanel; 
/** GUI item */         
      private superGraph2D graph;
/** GUI item */         
      private movie theMovie;
/** GUI item */         
      private ode solver;
/** GUI item */         
      Label status;
/** GUI item */         
      private choiceControls choices;     



/**
* @param target starting class
* @see startChua
*/ 
        public Chua(startChua target) {
        graph = new superGraph2D(this);
        theMovie = new movie(this);
        theMovie.toSleep=true;
        solver = new ode((dynamicGraph) this, nVariables);

        this.outerparent = target;
//        addNotify();   
        setBackground(new Color(255,255,255));
        
//        setLayout( new GridLayout(1,2) );
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill=GridBagConstraints.BOTH;

        setLayout(gridbag);
        constraints.gridheight = 1;
        constraints.gridwidth=1;
        constraints.weighty = 1.0;        
        constraints.insets = new Insets(10,10,10,0);

        graph.borderRight=35;
        graph.borderTop=80;  // MCC 8-10
        graph.borderBottom=10;
        
        leftPanel = new Panel();
        leftPanel.setLayout(new BorderLayout());
        Panel rightPanel = new Panel();
        rightPanel.setLayout(new BorderLayout());

        constraints.weightx=0.75;                
        constraints.gridwidth=2;
        gridbag.setConstraints(leftPanel, constraints);
        add(leftPanel);
        constraints.weightx=0.25;
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(10,0,15,10);                
        gridbag.setConstraints(rightPanel, constraints);
        add(rightPanel);        

        Panel topRightPanel = new Panel();
        topRightPanel.setLayout(gridbag);    
        rightPanel.add("Center",topRightPanel);
               
        leftPanel.add("Center",graph);
        bottomPanel = new Panel();
        bottomPanel.setLayout( new GridLayout(2,1));
        bottomPanel.add(theMovie);
        
        theMovie.borderBottom=0;
        
        String[] buttonLabels={" Reset "," Clear ",
                    " Start ","  Stop "};
        buttons = new buttonControls((dynamicGraph) this, 
                        buttonLabels,buttonLabels.length, true);
        buttons.b_init[0] = true;
        buttons.b_stopped[3] = false;
        buttons.b_started[1] = true;
        buttons.b_started[3] = true;
        buttons.setup();
        bottomPanel.add(buttons);
        leftPanel.add("South",bottomPanel);              
                
        constraints.fill=GridBagConstraints.NONE;
        constraints.insets = new Insets(0,0,0,0);
        constraints.anchor = GridBagConstraints.CENTER;

        constraints.gridheight = 3;
        constraints.gridwidth=1;
        constraints.weightx=1.;
        constraints.weighty = 0.75;

        String[] textboxes = new String[numSliders];
        for (int i=0;i<numSliders;i++) {
             textboxes[i]=String.valueOf(params[i]);
        } 
        
        String[] labels = {"  R","  R1","  R2",
                             "  C1","  C2",
                             "   L","   r" };
        parameters = new 
            sliderControls((dynamicGraph) this, textboxes,labels,
                     numSliders,5,0.01);        
        gridbag.setConstraints(parameters, constraints);
        topRightPanel.add(parameters);


        String[] textboxes1 = {String.valueOf(x0[1]),String.valueOf(x0[2]),
                    String.valueOf(x0[3]),String.valueOf(dtp),
                    String.valueOf(trans)};
        String[] labels1 = {"   X0","   Y0","   Z0",
                             "   dt"," tran"};
        variables = new 
              textControls((dynamicGraph) this, textboxes1,
                       labels1,textboxes1.length,5); 
        constraints.insets = new Insets(0,0,0,0);
        constraints.gridwidth=GridBagConstraints.REMAINDER;                              
        gridbag.setConstraints(variables, constraints);        
        topRightPanel.add(variables);
        String[] choiceLabels={"Show Ghost: ","Allow Sleep:","Show Time:  "};
        choices = new choiceControls((dynamicGraph) this, 
                        choiceLabels);
        constraints.weighty = 0.25;
        constraints.gridheight = 1;
        gridbag.setConstraints(choices, constraints);
        topRightPanel.add(choices);
        transientColor = Color.lightGray;
        choices.setState(0,true);          
        theMovie.toSleep=true;
        choices.setState(1,true);      
        showTime=true;             
        choices.setState(2,true);          
        status=new Label("                    ");
//        gridbag.setConstraints(status, constraints);        
        rightPanel.add("South",status);        

        graph.clearAll=true;
        repaint();
        
        buttons.enableGo();
        updateParameters();
        updateVariables();
        restart();          
      }

//**********************************************************************
/**
* Responds to textControls
* @see textControls
*/      
//**********************************************************************
      public void respondToText() {
            updateParameters();
            updateVariables();
            restart();
            buttons.enableGo();
      }

//**********************************************************************
/**
* Responds to buttons in sliderControls
* @see sliderControls
*/      
//**********************************************************************
      public void respondToSliderButtons() {
          updateParameters();
      }

//**********************************************************************
/**
* Responds to text boxes in sliderControls
* @see sliderControls
*/      
//**********************************************************************
      public void respondToSliderText() {
          updateParameters();
      } 

//**********************************************************************
/*
 Responds to mouse event
      
      public void respondToMouse(double xcoord, boolean xcoordValid,
                 double ycoord, boolean ycoordValid){}
*/
//**********************************************************************
                       
      public Insets insets() {
//            return new Insets(10,0,20,0);
            return new Insets(0,0,20,0);     // MCC Changed 8/10
      }

//************************************************************************
/**
* Stop movie thread 
*/
//************************************************************************

      public void movieStop() {
                if(aThread!=null) {
                    aThread.stop();
                    aThread=null;
                }
      }

//************************************************************************
/**
* Start movie thread 
*/
//************************************************************************
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
       
//************************************************************************
/**
* Disables text input in variables
*/
//************************************************************************
      
      public void disableAll() {
          int i;
          for(i=0;i<variables.ncontrols();i++)
                 variables.disableText(i);
//          for(i=0;i<parameters.ncontrols();i++) 
//                 parameters.disableText(i);
      }

//*********************************************************************
/** 
* Enables text input in variables
*/ 
//*********************************************************************

      public void enableAll() {
          for(int i=0;i<variables.ncontrols();i++)
                variables.enableText(i);
//          for(int i=0;i<parameters.ncontrols();i++)
//                parameters.enableText(i);
      }

//*********************************************************************
/**
* Returns RHS of differential equations for Chua model
* @param x[] vector of current value of dependent variables
* @param n number of dependent variables in array x[]
* @param t current value of independent variable
* @return n compoent vector giving derivatives of dependent variables
*/
//*********************************************************************     
      public double[] derivs(double[] x, double t, int n){
           double f=0;
           double[] rhs=new double[n];
           for(int i=0;i<n;i++) rhs[i]=0.;
           if(x[1]<1. && x[1] > -1.) f=-x[1];
           else if(x[1]>10.) f=10.*(x[1]-10.)-9.*b+1.;
           else if(x[1]<-10.)f=10.*(x[1]+10.)+9.*b-1. ;
           else if(x[1]>1.) f=-b*x[1]-1.+b;
           else if(x[1]<-1.)f=-b*x[1]+1.-b;
           else System.out.println("Error in derivs");
           rhs[0]=1.;
           rhs[1]=a*(x[2]-x[1])-f;
           rhs[2]=r*(a*(x[1]-x[2])+x[3]);
           rhs[3]=-c*(x[2]+rl*x[3]);

           return rhs;
     }

//********************************************************************
/**
* Updates parameters from the sliderControls 
*/ 
//********************************************************************
      
      public void updateParameters() {
            int i;
            
            graph.setTitle("Chua's Circuit");

            for(i=0;i<numSliders;i++) {
                params[i]= parameters.parseTextField(i, params[i]);
            }
            a=params[1]/params[0];
            b=(params[2]-params[1])/params[2];
            r=params[3]/params[4];
            c=params[1]*params[1]*params[3]/params[5];
            rl=params[6]/params[1];
      }

//********************************************************************      
/**
* Update parameters form the textControls 
*/
//********************************************************************      
      public void updateVariables() {
            int i;
//            plot_x=parameters.parseTextField(4, plot_x,0,3);
//            plot_y=parameters.parseTextField(5, plot_y,0,3);
            graph.setXAxisTitle(axisLabel[plot_x]);
            graph.setYAxisTitle(axisLabel[plot_y]);            
            x[0]=0.;
            for(i=1;i<=3;i++) {
                x0[i]=variables.parseTextField(i-1,x0[i]);
                x[i]=x0[i];
            }
            
            dtp=variables.parseTextField(3,dtp);
            delay=theMovie.delay;
            dt=dtp/((double) delay);
            trans=variables.parseTextField(4,trans);
            ntrans=(int) (trans/dtp);
//            System.out.println("dt= "+dt);
            runTrans=true;
//          restart();
      }      
                
//*********************************************************************
/**  Resets plot by deleting all curves and restarting
*/
//*********************************************************************
      
      public void restart() {
            double data[] = new double[2*ntrans];
            double data1[] = new double[4];
            double data2[] = new double[4];
            int i,j;
            double xSave[] = new double[nVariables];
            
            if(ncurve>=0) ncurve = graph.deleteAllCurves();           
            t=0;
            err=0.;
            status.setText("");
            if(ntrans>0) {                
                if(runTrans) {
                    for(i=0;i<ntrans;i++) {
//                        update(nVariables);
                    t=solver.timeStep(x,t,dtp);
                    }
                }
                for(i=0;i<nVariables;i++) {
                    xSave[i]=x[i];
                }
                data[0]=x[plot_x];
                data[1]=x[plot_y];
                for(i=1,j=2;i<ntrans;i++) {
//                    update(nVariables);
                    t=solver.timeStep(x,t,dtp);
                    data[j]=x[plot_x];
                    j++;
                    data[j]=x[plot_y];
                    j++;
                }        
                ncurve = graph.addCurve(data,ntrans,transientColor);
                for(i=0;i<nVariables;i++) {
                     x[i]=xSave[i];
                }
            }
            runTrans=false;
            data1[0]=x[plot_x];
            data1[1]=x[plot_y];
//            update(nVariables);
            t=solver.timeStep(x,t,dt);
            t=t-dt;
            data1[2]=x[plot_x];
            data1[3]=x[plot_y];
            ncurve = graph.addCurve(data1,2,Color.blue); 
            ncurve1=ncurve;
//            graph.clearAll= false ;                              
            graph.paintAll=true; 
            graph.repaint();
      }

//*********************************************************************
/**
* Iterates Chua equations and updates graph  
*/
//*********************************************************************
      
      public boolean iterate() {
            
            double[] moredata = new double[2];
//            update(nVariables);
            for(int i=0;i<delay;i++)
                t=solver.timeStep(x,t,dt);
            errp=solver.err[plot_x]+solver.err[plot_y];
            if(errp>err) err=errp;
            if(showTime)
                  status.setText("Time= "+(float)t);

            moredata[0]=x[plot_x];
            moredata[1]=x[plot_y];


            graph.paintAll=false;     // Don't paint while updating data
//            if(graph.nPoints(ncurve)>200) {
//                  graph.deleteFromCurve(100,ncurve);
//            }       
            graph.appendToCurve(moredata,1,ncurve1);
            graph.paintAll=true;
//            graph.clearAll=false;
//            graph.repaint(delay/2);
              graph.clearAll=false;
              graph.repaint();      
              return true;

      }

//**********************************************************************
/**
* Stop thread
*/
//**********************************************************************
      
      public void stop() {
            movieStop();
            enableAll();
      }
//**********************************************************************
/**
* Responds to choiceControls
* @see choiceControls
*/      
//**********************************************************************      
      public void respondToChoices() {
            if(choices.getState(0)) 
                  transientColor = Color.lightGray;
            else
                  transientColor=graph.getBackground();
            if(choices.getState(1))
                  theMovie.toSleep=true;
            else
                  theMovie.toSleep=false;
            if(choices.getState(2))
                  showTime=true;
            else
                  showTime=false; 
                  status.setText("");                 
      }
         
      
//**********************************************************************      
/**
* Respond to buttonControls
* @see      buttonControls
* @param    buttonIndex index of button pushed
*/          
//**********************************************************************
      public void respondToButtons(int buttonIndex) {
            if(buttonIndex==0) {
                 buttons.enableGo();
                 updateParameters();
                 updateVariables();
                 restart();  
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
            else if(buttonIndex==2) {
                buttons.disableGo();
                disableAll();
                status.setText("");
                movieStart();
                err=0.;
            }
            else if(buttonIndex==3) {
                buttons.enableGo();
                movieStop();
                enableAll();
                status.setText("Time= "+(float)t+" Error= "+(float)err);                
            }
      }      
      
      public void updateSpeed(int in_delay) {
            delay=in_delay;
            dt=dtp/((double) delay);
            ntrans=(int) (trans/dtp);
//          System.out.println("dt= "+dt);
      }
                                  
}   
/**********************************************************************/
