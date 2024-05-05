import ChaosDemos.*;
import java.awt.*;
import java.util.*;

public class Lorenzh extends dynamicGraph {
/** number of curves */
      int ncurve=-1;                       
/** index of first data curve */
      int ncurve1;
/** index of second data curve */
      int ncurve2;
/** variable to be plotted on x-axis */
      int plot_x=1;
/** variable to be plotted on y-axis */
      int plot_y=3;            
/** delay in graph update , and factor reducing time step */
      int delay=50;
/** number of iterations to eliminate transient = trans/dt */
      int ntrans;  
/** transient time */
      double trans=0.0;
/** number of variables (icluding time) */
      int nVariables=8;
/** true if transient is to be run on call to restart() */
      boolean runTrans;   
/** true if time to be shown. Set by choices */
      boolean showTime;
/** true if second trace to be plotted */
      boolean curve2 = false;      
      
/** time step (after reduction by delay) */
      double dt=0.01;
/** input time step */
      double dtp=0.01;
/** evolving time */      
      double t=0.;
/** evolving variables */        
      double[] x={0.,0.,0.,0.,0.,0.,0.,0.};  
/** starting values of x */      
      double[] x0={0.,0.00001,0.,00.,0.,0.,0.,0.};   
/** perturbation for second trace */        
      double[] dx={0.,0.,0.,0.1};
/** parameter of equations */
      double a=29.78;          
/** parameter of equations */                     
      double b=2.6667;
/** parameter of equations */      
      double c=22;
/** error estimates from ode solver */   
      double err,errp;

/** Color of ghost */           
      Color transientColor;
/** axis labels */   
      String[] axisLabel={"Time","  X ","  Y ","  Z "};

/** GUI items */      
      private textControls variables,parameters;
      private buttonControls buttons;
      private choiceControls choices;
      private Label status;
      private superGraph2D graph;
      private movie theMovie;
      private ode solver;    
/** parent */
      private startLorh outerparent;  
/** thread for animation */
      private Thread aThread=null;            

/**
* @param target starting class
* @see startLorh
*/ 
        Lorenzh(startLorh target) {
        
        graph = new superGraph2D(this);
        theMovie = new movie(this);
        solver = new ode((dynamicGraph) this, nVariables);
        this.outerparent = target; 

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        setLayout(gridbag);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.gridheight = 1;
        constraints.gridwidth=1;
        constraints.weighty = 1.0; 
        constraints.weightx=0.75;       
        constraints.insets = new Insets(0,0,0,10);
        
        graph.borderRight=35;
        Panel leftPanel = new Panel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add("Center",graph);          
        Panel bottomLeftPanel = new Panel();
        bottomLeftPanel.setLayout( new GridLayout(2,1));
        bottomLeftPanel.add(theMovie); 
        String[] buttonLabels={"   Reset    ","   Clear   ",
                    "    Start   ","    Stop   "};
        buttons = new buttonControls((dynamicGraph) this, 
                        buttonLabels,buttonLabels.length,true);
        buttons.b_init[0] = true;
        buttons.b_stopped[3] = false;
        buttons.b_started[1] = true;
        buttons.b_started[3] = true;
        buttons.setup();
        bottomLeftPanel.add(buttons);               
        leftPanel.add("South",bottomLeftPanel); 
        constraints.insets = new Insets(0,0,10,0);
        gridbag.setConstraints(leftPanel, constraints);              
        add(leftPanel);    

        Panel rightPanel = new Panel();
        rightPanel.setLayout(new BorderLayout());
        constraints.weightx=0.25;
        constraints.insets = new Insets(0,0,15,10);
        gridbag.setConstraints(rightPanel, constraints);
        add(rightPanel);                
 
        Panel topRightPanel = new Panel();
        topRightPanel.setLayout(gridbag);

        constraints.gridheight = 3;
        constraints.gridwidth=1;
        constraints.weightx=1.;
        constraints.weighty = 0.75;
        constraints.insets = new Insets(0,0,0,10);
        constraints.fill=GridBagConstraints.NONE;             

        String[] textboxes = {String.valueOf(a),String.valueOf(b),
                    String.valueOf(c),String.valueOf(dtp),
                    String.valueOf(plot_x),
                    String.valueOf(plot_y),String.valueOf(trans)};
        String[] labels = {"  a","   b","   c",
                             "  dt","x-ax",
                             "y-ax","tran" };
        parameters = new 
              textControls((dynamicGraph)this,textboxes,labels,textboxes.length,5);        
        gridbag.setConstraints(parameters, constraints);
        topRightPanel.add(parameters);

        String[] textboxes1 = {String.valueOf(x0[1]),String.valueOf(x0[2]),
                    String.valueOf(x0[3]),String.valueOf(dx[1]),
                    String.valueOf(dx[2]),String.valueOf(dx[3])};
        String[] labels1 = {"   X0","   Y0","   Z0",
                             "  dX0","  dY0","  dZ0"};
        variables = new 
              textControls((dynamicGraph) this,textboxes1,labels1,textboxes1.length,5);        
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0,0,0,10);
        gridbag.setConstraints(variables, constraints);
        topRightPanel.add(variables);

        String[] choiceLabels={"Show Ghost:  ","Allow Sleep: ","Show Time:   "};
        choices = new choiceControls((dynamicGraph) this, 
                        choiceLabels);
        constraints.weighty = 0.25;
        constraints.gridheight = 1;
        gridbag.setConstraints(choices, constraints);
        topRightPanel.add(choices);
        transientColor = Color.lightGray;
        choices.setState(0,true);          
        theMovie.toSleep=false;
        choices.setState(1,false);      
        showTime=false;             
        choices.setState(2,false);                     

        rightPanel.add("Center",topRightPanel);
        status=new Label("                    ");
        rightPanel.add("South",status);
        graph.clearAll=true;
        repaint();
        /* Setup run */
        buttons.enableGo();
        updateParameters();
        boolean dummy=restart();          
      }

//**********************************************************************
/**
* Responds to textControls
* @see textControls
*/      
//**********************************************************************
      public void respondToText() {
            updateParameters();
            if(restart())
                 buttons.enableGo();
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
                 updateParameters();
                 if(restart()) 
                     buttons.enableGo();                  
            }
            else if(buttonIndex==1) {
                graph.clearAll=false; //6/27/96
                if(aThread != null) {
                  movieStop();
                  if(restart()) 
                       movieStart();
                }
                else if(restart()) {};
            }
            else if(buttonIndex==2) {
                buttons.disableGo();
                disableAll();
                status.setText("");
                movieStart();
            }
            else if(buttonIndex==3) {
                buttons.enableGo();
                movieStop();
                enableAll();                
                status.setText("Time= "+(float)t+" Error= "+(float)err);
            }
      }
      
//      public Insets insets() {
//            return new Insets(0,0,20,0);
//      }

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
//                if(aThread==null) {             8/2/97
                    aThread = new Thread(theMovie);
                    aThread.start();
//                }
             
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
* Disables text input and choice controls
*/
//************************************************************************
      
      public void disableAll() {
          int i;
          for(i=0;i<variables.ncontrols();i++)
                 variables.disableText(i);
          for(i=0;i<parameters.ncontrols();i++) 
                 parameters.disableText(i);
      }

//*********************************************************************
/**
* Enables text input and choice contorls
*/ 
//*********************************************************************

      public void enableAll() {
          for(int i=0;i<variables.ncontrols();i++)
                variables.enableText(i);
          for(int i=0;i<parameters.ncontrols();i++)
                parameters.enableText(i);
      }

//*********************************************************************
/**
* Returns RHS of differential equations for Lorenz model
* @param x[] vector of current value of dependent variables
* @param n number of dependent variables in array x[]
* @param t current value of independent variable
* @return n compoent vector giving derivatives of dependent variables
*/
//*********************************************************************       
      public double[] derivs(double[] x, double t, int n){
           double[] rhs=new double[n];
           for(int i=0;i<n;i++) rhs[i]=0.;
           rhs[0]=1.;
           rhs[1]=c*(x[2]-x[1]);
           rhs[2]=a*x[1]-x[2]-x[1]*x[3];
           rhs[3]=-b*(x[3]-x[1]*x[2]);
           rhs[4]=1.;
           rhs[5]=c*(x[6]-x[5]);
           rhs[6]=a*x[5]-x[6]-x[5]*x[7];
           rhs[7]=-b*(x[7]-x[5]*x[6]);
           return rhs;
     }

//********************************************************************
/**
* Updates parameters from the text controls 
*/ 
//********************************************************************      
      public void updateParameters() {
            int i;
            
            graph.setTitle("<Weird> Lorenz Attractor");

            a=parameters.parseTextField(0, a, true);
            b=parameters.parseTextField(1, b, true);
            c=parameters.parseTextField(2, c, true);
            dtp=parameters.parseTextField(3, dtp, true);
            dt=dtp/((double)delay);
            plot_x=parameters.parseTextField(4, plot_x,0,3);    //MCC 3/19/97
            plot_y=parameters.parseTextField(5, plot_y,0,3);    //MCC 3/19/97
            graph.setXAxisTitle(axisLabel[plot_x]);
            graph.setYAxisTitle(axisLabel[plot_y]);
            trans=parameters.parseTextField(6,trans, true);
            ntrans=(int)(trans/dtp);
            x[0]=0.;
            for(i=1;i<=3;i++) {
                x0[i]=variables.parseTextField(i-1,x0[i]);
                x[i]=x0[i];
            }
            curve2 = false;
            x[4]=0.;
            for(i=1;i<=3;i++) {
                dx[i]=variables.parseTextField(i+2,dx[i]);
                if(dx[i] > 0) curve2 = true;
                x[i+4]=x0[i]+dx[i];
            }    
            runTrans=true;
      }      
                
//*********************************************************************
/**
* Resets plot by deleting all curves and restarting
*/
//*********************************************************************
      
      public boolean restart() {
            double data[] = new double[2*ntrans];
            double data1[] = new double[4];
            double data2[] = new double[4];
            int i,j;
            double xSave[] = new double[nVariables];
            
            t=0.;
            err=0.;
            status.setText("");
            if(ncurve>=0) ncurve = graph.deleteAllCurves();           
            if(ntrans>0) {                
                if(runTrans) {
                    for(i=0;i<ntrans;i++) {
                         try {
                             t=solver.timeStep(x,t,dtp);
                         } catch (ArithmeticException e)  {
                              buttons.disableAll();
                              alertDialog alert = new alertDialog("Value has diverged!");                                                               
                              return false;
                        }      
                    }
                    for(i=1;i<=3;i++) {
                        x[i+4]=x[i]+dx[i];
                    }
                }
                for(i=0;i<nVariables;i++) {
                    xSave[i]=x[i];
                }
                data[0]=x[plot_x];
                data[1]=x[plot_y];
                for(i=1,j=2;i<ntrans;i++) {
                    try {
                        t=solver.timeStep(x,t,dtp);
                    } catch (ArithmeticException e) {
                              buttons.disableAll();
                              alertDialog alert = new alertDialog("Value has diverged!");                                                               
                              return false;                    
                    }
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
            data2[0]=x[4+plot_x];
            data1[1]=x[plot_y];
            data2[1]=x[4+plot_y];
//            update(nVariables);
            try {
                 t=solver.timeStep(x,t,dt);
            }catch (ArithmeticException e) {
                       buttons.disableAll();
                       alertDialog alert = new alertDialog("Value has diverged!");                                                               
                       return false;                    
            }            
            data1[2]=x[plot_x];
            data2[2]=x[4+plot_x];
            data1[3]=x[plot_y];
            data2[3]=x[4+plot_y];
            t=t-dt;
            ncurve = graph.addCurve(data1,2,Color.blue); 
            ncurve1=ncurve;
            if (curve2) {
                  ncurve = graph.addCurve(data2,2,Color.red);
                  ncurve2=ncurve;
            }      
//            graph.clearAll= false ;                              
            graph.paintAll=true; 
            graph.repaint();
            return true;
      }

//*********************************************************************
/**
* Iterates Lorenz equations and updates graph  
*/
//*********************************************************************
      
      public boolean iterate() {
            
            double[] moredata = new double[2];
            double[] moredata2 = new double[2];
//            update(nVariables);
            for(int i=0;i<delay;i++) {
                try {
                    t=solver.timeStep(x,t,dt);
                } catch (ArithmeticException e) {
                      alertDialog alert = new alertDialog("Value has diverged!");                                                               
                      buttons.enableGo();
                      enableAll();
                      return false;                    
                }
            }                                
            errp=solver.err[plot_x]+solver.err[plot_y];
            if(errp>err) err=errp;                
            moredata[0]=x[plot_x];
            moredata[1]=x[plot_y];
            moredata2[0]=x[4+plot_x];
            moredata2[1]=x[4+plot_y];

            graph.paintAll=false;     // Don't paint while updating data
//            if(graph.nPoints(ncurve)>200) {
//                  graph.deleteFromCurve(100,ncurve);
//            }       
            graph.appendToCurve(moredata,1,ncurve1);
            if(curve2) graph.appendToCurve(moredata2,1,ncurve2);
            graph.paintAll=true;
            graph.clearAll=false;
            graph.repaint();
            if(showTime)
                  status.setText("     Time ="+(float)t);  
            return true;
      }

//**********************************************************************
/**
*  Stop movie thread
*/ 
//**********************************************************************
      
      public void stop() {
            movieStop();
            enableAll();
      }                            

/**********************************************************************/
      public void updateSpeed(int in_delay) {
            delay=in_delay;
            dt=dtp/((double) delay);
            ntrans=(int) (trans/dtp);
//          System.out.println("dt= "+dt);
      }
            
}
//**********************************************************************
