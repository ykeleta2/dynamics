import ChaosDemos.*;
import java.awt.*;
import java.util.*;
import java.net.URL;
import graph.*;
/**                    
* Iterates and plots the chaos models described by coupled ODEs. <br> 
* Uses the "Java Graph Class Library" by Leigh Brookshaw
* @version 3 August, 1997
* @author Michael Cross
*/

public class Odes extends dynamicGraph {

/** functions */
      private static final int LORENZ=0;
      private static final int ROSSLER=1;
      private static final int DUFFING=2;
      private static final int PENDULUM=3;
      private static final int VANDERPOHL=4;      
      private static final int CHUA=5;
      
/** plot types */
      private static final int TIMESERIES=0; 
      private static final int FOURIER=1;
      private static final int POINCARE=2;
      private static final int RETURNMAP=3;
      private static final int MAXMAP=4;
      
      private static final double Pi2=2*Math.PI;      
      
      /* classes used */
      private startOdes outerparent;
      private superGraph2D graph;
      private movie theMovie;
      private ode solver;
      private powerSpectrum mySpectrum;      

/* animation thread */
      private Thread aThread=null;      

      /* GUI classes */
      private textControls variables,parameters;
      private buttonControls buttons;
      private choiceControls choices;      
      private Panel topRightPanel;      
      private Choice plotChoice;
      private Choice functionChoice;
      private Label status;                              

/* items for plotting points */
      private URL markerURL;
      
/** location of marker.txt */
      URL documentBase;
/** input marker size */
      double marker=1.;
/** marker size */      
      double markerScale;
/** type of marker (see marker.txt) */      
      int markerType;           
      
/* flags */
/** true if Poincare plane has been crossed */
      boolean crossed=true;
/** true if first data point */    
      boolean firstDataPoint=true;
/** true if user has chosen range */
      boolean setAxesRange=false;
/** true after first mouse click  */      
      boolean clicked=false; 
/** true if transient is to be run */      
      boolean runTrans=false;
/** true if time to be displayed */      
      boolean showTime; 
/** true if two curves are to be plotted  */
      boolean curve2 = false;

      private boolean dummy;                

/** number of variables (icluding time) */
      int nVariables=4;

/** variable to be plotted on x-axis */
      int plot_x=1;
/** variable to be plotted on y-axis */      
      int plot_y=2;
/** index for label on x-axis */
      int label_x=1;
/** index for label on y-axis */      
      int label_y=2;  
/** variable for Poincare section */                
      int secVar=3;
/** delay in graph update , and factor reducing time step */      
      int delay=50;
/** number of iterations to eliminate transient = trans/dt */      
      int ntrans;    
/** window type in power spectrum */       
      int winNum=2;
/** number of iterations between each point for power spectrum */      
      int FFTskip=1;               

/** updated number of points in plot */      
      int nplot;
/** updated number of points in plot of second curve */         
      int nplot1;
/** number of curves */      
      int ncurve=-1;      
/** index of first data curve */      
      int ncurve1;
/** index of second data curve */      
      int ncurve2;
/** Number of points in FFT */      
      int fourierLength=256;
/** index of point added to FFT */      
      int fourierIndex;
/** plot type */      
      int plot=0;
/** function type */      
      int function=0;
/** number of iterations since last FFT */      
      int skip=1;         

/* parameters */
/** function parameter */
      double a=28.;        
/** function parameter */                       
      double b=2.667;
/** function parameter */      
      double c=10;
/** function parameter */      
      double d=0;
/** transient time */    
      double trans=10;      
/** value for Poincare section */      
      double sec=20.;
/** time step (after reduction by delay) */
      double dt=0.02;
/** input time step */      
      double dtp=0.02;      
                  
/** iterated variable (icluding time) */
      double[] x={0.,0.,0.,0.};
/** starting value of x */
      double[] x0={0.,2.,5.,20.};
/** copy of x */       
      double[] xp={0.,0.,0.,0.};
/** x for second curve */      
      double[] x1={0.,0.,0.,0.};
/** perturbation */      
      double[] dx={0.,0.,0.,0.}; 
/** data for power spectrum */      
      double[] fourierData;                

/** time in evolution */
      double t=0.;
/** time for second curve */      
      double t1=0;
/** last time */      
      double tp;
/** error estimates from solver */      
      double err,errp;
/** last x[xecVar] for Poincare section calculation */      
      double zHold;
/** vasiables for return map amd max map */      
      double z1,z2;
/* range of ploe */      
      double xmin,xmax,ymin,ymax;      

/** color of ghost */      
      Color transientColor;

/* default values depending on function and/or plot */
      private String[] axisLabel={"Time","  X ","  Y ","  Z "};      
      private String[] aArray={"28.","10.","0.3","1.5","0.32","0.923"};
      private String[] bArray={"2.667","0.15","0.25","0.5","0.2","0.066"};
      private String[] cArray={"10.","0.2","1.","0.6667","1.05","0.779"};
      private String[] dArray={"0.","0.","0.","0.","0.","0.071"}; 
      private String[] dtArray={"0.01","0.1","0.2","0.2","0.2","1.0"};
      private String[] x0Array={"2.0","1.0","1.0","0.2","0.2","0.1"};
      private String[] y0Array={"5.0","2.0","2.0","0.1","0.1","0.15"};
      private String[] z0Array={"20.0","1.5","1.5","0.0","0.5","0.05"};
      private String[] FFTskipArray={"4","2","2","2","2","2"};
      private String[] fourierLengthArray={"256","256","256","256","256","256"};
      private String[] variableArray={"3", "3","3","3","3","2"};
      private String[] secArray={"31.","1.","3.14","3.14","3.14","0.0"};
      private String[] transArray={"10.","20.","100.","50.","100.","50."};
      private int[] parameterNumberArray={3,3,3,4,3,4};
      private String[] titleArray={"Lorenz Model","Rossler Model","Duffing Oscillator",
                              "Driven Pendulum","Van Der Pohl Oscillator","Chua Circuit"};
      private String[][] xTitleArray={{"Time","  X ","  Y ","  Z "},                            
                              {"Frequency","Frequency","Frequency","Frequency"},
                             {"Time","  X ","  Y ","  Z "},
                             {"","X_n","Y_n","Z_n"},
                             {"","X{^ max}_n","Y{^ max}_n","Z{^ max}_n"},
                             };
      private String[][] yTitleArray={{"Time","  X ","  Y ","  Z "},                
                              {"log(Power)","log(Power)","log(Power)","log(Power)"},
                             {"Time","  X ","  Y ","  Z "},
                             {"","X_n+1","Y_n+1","Z_n+1"},
                             {"","X{^ max}_n+1","Y{^ max}_n+1","Z{^ max}_n+1"},
                             };

/**
* @param target starting class
* @see startLor
*/ 
        Odes(startOdes target, URL in_documentBase) {
        
        documentBase=in_documentBase; 
        graph = new superGraph2D(this);
        graph.borderRight=35;                       
        try {
           markerURL = new URL(documentBase,"marker.txt");
           graph.setMarkers(new Markers(markerURL));
        } catch(Exception e) {
           System.out.println("Failed to create Marker URL!");
        }
        
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
 
        topRightPanel = new Panel();
        topRightPanel.setLayout(gridbag);

        constraints.gridheight = 3;
        constraints.gridwidth=1;
        constraints.weightx=1.;
        constraints.weighty = 0.75;
        constraints.insets = new Insets(0,0,0,10);
        constraints.fill=GridBagConstraints.NONE;             

        String[] textboxes = {String.valueOf(a),String.valueOf(b),
                    String.valueOf(c),String.valueOf(d),String.valueOf(dtp),
                    String.valueOf(plot_x),
                    String.valueOf(plot_y)};
        String[] labels = {"  a","   b","   c","   d",
                             "  dt","x-ax","y-ax"};
        parameters = new 
              textControls((dynamicGraph)this,textboxes,labels,textboxes.length,5);        
        gridbag.setConstraints(parameters, constraints);
        topRightPanel.add(parameters);

        String[] textboxes1 = {String.valueOf(x0Array[0]),String.valueOf(y0Array[0]),
                    String.valueOf(z0Array[0]),String.valueOf(trans),
                    "0.","0.","0."};
        String[] labels1 = {"     X0","     Y0","     Z0"," tran ","    dX0",
                             "    dY0","    dZ0"};
        variables = new 
              textControls((dynamicGraph) this,textboxes1,labels1,textboxes1.length,5);        
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0,0,0,10);
        gridbag.setConstraints(variables, constraints);
        topRightPanel.add(variables);
        
        functionChoice = new Choice();
        functionChoice.addItem("Lorenz");
        functionChoice.addItem("Rossler");
        functionChoice.addItem("Duffing");
        functionChoice.addItem("Pendulum");
        functionChoice.addItem("VanDerPohl");
        functionChoice.addItem("Chua");
        constraints.gridwidth=1;
        constraints.weighty = 0.2;       
        constraints.gridheight=1;          
        constraints.gridwidth=GridBagConstraints.REMAINDER;        
        constraints.insets = new Insets(0,0,0,0);
        gridbag.setConstraints(functionChoice, constraints);  
        topRightPanel.add(functionChoice);        

        plotChoice = new Choice();
        plotChoice.addItem("Time Series");
        plotChoice.addItem("Fourier");
        plotChoice.addItem("Poincare");
        plotChoice.addItem("Return Map");
        plotChoice.addItem("Max Map");        
        constraints.gridwidth=1;
        constraints.weighty = 0.2;       
        constraints.gridheight=1;          
        constraints.gridwidth=GridBagConstraints.REMAINDER;        
        constraints.insets = new Insets(0,0,0,0);
        gridbag.setConstraints(plotChoice, constraints);  
        topRightPanel.add(plotChoice);
        
        String[] choiceLabels={"Show Ghost:  ","Allow Sleep: ","Show Time:   "};
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

        rightPanel.add("Center",topRightPanel);
        status=new Label("                    ");
        rightPanel.add("South",status);
        
        graph.clearAll=true;
        repaint();

        /* Setup run */
        buttons.enableGo();
        updateParameters();
        dummy=restart();
        parameters.hide(3);     
      }

//**********************************************************************
/**
* Responds to textControls
* @see textControls
*/      
//**********************************************************************
      public void respondToText() {
            updateParameters();
            if(restart()) buttons.enableGo();
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
                 if(restart()) buttons.enableGo();
            }
            else if(buttonIndex==1) {
//                graph.clearAll=false; //6/27/96
                if(aThread != null) {
                  movieStop();
                  if (restart());
                       movieStart();
                }
                else dummy=restart();
            }
            else if(buttonIndex==2) {
                buttons.disableGo();
                disableAll();
                status.setText("");
                clicked=false;
                graph.allowDrag=false; 
                movieStart();
            }
            else if(buttonIndex==3) {
                buttons.enableGo();
                movieStop();
                enableAll();
                if(plot>1) graph.allowDrag=true;                
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
//                if(aThread==null) {                  8/3/97
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
                    return super.handleEvent(evt);                
                case Event.ACTION_EVENT:
                    if(evt.target == functionChoice || evt.target == plotChoice)  {
                        function = functionChoice.getSelectedIndex();            
                        if(evt.target == functionChoice) {
                              for(int i=0;i<parameterNumberArray[function];i++)
                                    parameters.show(i);
                              for(int i=parameterNumberArray[function];i<4;i++)
                                    parameters.hide(i);                               
                              setDefaults();
                        }
                        plot = plotChoice.getSelectedIndex();
                        if(plot==TIMESERIES) {
                              variables.show(4);
                              variables.show(5);                        
                              variables.show(6);  
                              variables.setText(4,"0.");
                              variables.setLabelText(4,"  dX0");
                              variables.setText(5,"0.");
                              variables.setLabelText(5,"  dY0");       
                              variables.setText(6,"0.");
                              variables.setLabelText(6,"  dZ0");                                                          
               
                              topRightPanel.validate();  
                        }                                          
                        else if(plot==FOURIER) {
                              variables.show(4);
                              variables.show(5);
                              variables.show(6);
                              topRightPanel.validate();                                                      
                              variables.setText(4,FFTskipArray[function]);
                              variables.setLabelText(4,"Interval");
                              variables.setText(5,fourierLengthArray[function]);
                              variables.setLabelText(5,"Points ");
                              variables.setText(6,"2");
                              variables.setLabelText(6,"Window");                              
                        }
                        else if(plot==POINCARE) {
                              variables.show(4);
                              variables.show(5);  
                              variables.show(6);   
                              topRightPanel.validate();                                                 
                              variables.setText(4,variableArray[function]);
                              variables.setLabelText(4,"Variable");
                              variables.setText(5,secArray[function]);
                              variables.setLabelText(5,"Section ");
                              variables.setText(6,"1.");
                              variables.setLabelText(6,"PlotMark");
                        }       
                        else if(plot==RETURNMAP) {
                              variables.show(4);
                              variables.show(5); 
                              variables.show(6);    
                              topRightPanel.validate();                                                 
                              variables.setText(4,variableArray[function]);
                              variables.setLabelText(4,"Variable");
                              variables.setText(5,secArray[function]);
                              variables.setLabelText(5,"Section ");
                              variables.setText(6,"1.");
                              variables.setLabelText(6,"PlotMark");                              
                        } 
                        else if(plot==MAXMAP) {
                              variables.show(4);                     
                              variables.hide(5);
                              variables.show(6);
                              topRightPanel.validate(); 
                              variables.setText(4,variableArray[function]);
                              variables.setLabelText(4,"Variable");                               
                              variables.setText(6,"1.");
                              variables.setLabelText(6,"PlotMark");                                                          
                        }                                                                               
                        else variables.showAll(); 
                        updateParameters();
                        dummy=restart();
                    }
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
          plotChoice.disable();
          functionChoice.disable();                         
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
          plotChoice.enable();
          functionChoice.enable();      
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
           double f=0,resRatio;
           double[] rhs=new double[n];
           for(int i=0;i<n;i++) rhs[i]=0.;
           resRatio=0.636;
           switch (function) {
                  case LORENZ:                  
                       rhs[0]=1.;
                       rhs[1]=c*(x[2]-x[1]);
                       rhs[2]=a*x[1]-x[2]-x[1]*x[3];
                       rhs[3]=-b*(x[3]-x[1]*x[2]);
                       break;
                  case ROSSLER:
                       rhs[0]=1.;
                       rhs[1]=x[2]-x[3];
                       rhs[2]=-x[1]+b*x[2];
                       rhs[3]=c+x[3]*(x[1]-a)  ;
                       break;
                  case DUFFING:
                       rhs[0]=1.;
                       rhs[1]=x[2];
                       rhs[2]=-b*x[2]+x[1]-x[1]*x[1]*x[1]+a*Math.cos(x[3]);
                       rhs[3]=c;
                       break;
                  case PENDULUM:
                       rhs[0]=1.;
                       rhs[1]=x[2];
                       rhs[2]=-b*x[2]-Math.sin(x[1])+a*Math.cos(x[3]);
                       rhs[3]=c;
                       break;
                  case VANDERPOHL:
                       rhs[0]=1.;
                       rhs[1]=x[2];
                       rhs[2]=b*(1-x[1]*x[1])*x[2]-x[1]+a*Math.cos(x[3]);
                       rhs[3]=c;
                       break;
                  case CHUA:
                       if(x[1]<=1. && x[1] >= -1.) f=-x[1];
                       else if(x[1]>10.) f=10.*(x[1]-10.)-9.*resRatio+1.;
                       else if(x[1]>1.) f=-resRatio*x[1]-1.+resRatio;
                       else if(x[1]<-10.)f=10.*(x[1]+10.)+9.*resRatio-1. ;
                       else if(x[1]<-1.)f=-resRatio*x[1]+1.-resRatio;                                              
                       else System.out.println("Error in derivs");
                       rhs[0]=1.;
                       rhs[1]=a*(x[2]-x[1])-f;
                       rhs[2]=b*(a*(x[1]-x[2])+x[3]);
                       rhs[3]=-c*(x[2]+d*x[3]);                                   
                       break;
            }                                                           
            return rhs;
     }

//********************************************************************
/**
* Updates parameters from the text controls 
*/ 
//********************************************************************      
      public void updateParameters() {
            int i;
            
            clicked=false;
            setAxesRange=false;
            graph.setTitle(titleArray[function]);
            plot=plotChoice.getSelectedIndex();
            function=functionChoice.getSelectedIndex();
            a=parameters.parseTextField(0, a);
            b=parameters.parseTextField(1, b);
            c=parameters.parseTextField(2, c);
            d=parameters.parseTextField(3, d);
            dtp=parameters.parseTextField(4, dtp, true);
            dt=dtp/((double)delay);
            plot_x=parameters.parseTextField(5, plot_x,0,3);
            plot_y=parameters.parseTextField(6, plot_y,0,3);
            if(plot>1) {
               if(plot_x==0 || plot_x==plot_y || plot_x+plot_y+secVar !=6) {
//                        plot_x=4-secVar;
//                        plot_y=6-plot_x-secVar;
//                        parameters.setText(5,String.valueOf(plot_x));
//                        parameters.setText(6,String.valueOf(plot_y));
                        plot_x=mod(secVar+1,3)+1;
                        plot_y=mod(plot_x+1,3)+1;
                        parameters.setText(5,String.valueOf(plot_x));
                        parameters.setText(6,String.valueOf(plot_y));                        
                  }                              
            }
                   
            x[0]=0.;
            for(i=1;i<=3;i++) {
                x0[i]=variables.parseTextField(i-1,x0[i]);
                x[i]=x0[i];
            }
            trans=variables.parseTextField(3,trans);
            ntrans=(int)(trans/dtp);
            if(ntrans>0) runTrans=true;                                                
            curve2=false;
            if(plot==TIMESERIES) {
                  label_x=plot_x;
                  label_y=plot_y;
                  
                  for(i=1;i<=3;i++) {
                      dx[i]=variables.parseTextField(i+3,dx[i]);
                      if(dx[i]!=0.) curve2=true;
                  }                  
            }                 
            if(plot==POINCARE) {
                  secVar=variables.parseTextField(4,secVar,1,3);
                  sec=variables.parseTextField(5,sec);
                  marker=variables.parseTextField(6,marker, true);                                    
                  label_x=plot_x;
                  label_y=plot_y;
            }               
            if(plot==RETURNMAP) {
                  secVar=variables.parseTextField(4,secVar,1,3);
                  sec=variables.parseTextField(5,sec);
                  marker=variables.parseTextField(6,marker,true);                                      
                  label_x=plot_x;
                  label_y=plot_x;
            }
            else if(plot==MAXMAP) {
                  secVar=variables.parseTextField(4,secVar,1,3);
                  marker=variables.parseTextField(6,marker,true);  
                  label_x=secVar;
                  label_y=secVar;
            }       
            else if(plot==FOURIER) {
                  FFTskip=variables.parseTextField(4,FFTskip,true);
                  fourierLength=variables.parseTextField(5,fourierLength,true);
                  int test=fourierLength;
                  int power=0;
                  while(test>1) {
                        test=test/2;
                        power++;
                  }
                  test=1;
                  for(i=0;i<power;i++) test=test*2;
                  if(test!=fourierLength) {
                        alertDialog alert = new alertDialog(this," No. of points must be power of two ");
                        fourierLength=test;
                        variables.setText(5,String.valueOf(fourierLength));
                  }
                  winNum=variables.parseTextField(6,winNum,0,3);
                  label_x=plot_x;
                  label_y=plot_x;                  
            }
            if(plot>1) {
               if(plot_x==0 || plot_x==plot_y || plot_x+plot_y+secVar !=6) {
                        plot_x=mod(secVar+1,3)+1;
                        plot_y=mod(plot_x+1,3)+1;
                        parameters.setText(5,String.valueOf(plot_x));
                        parameters.setText(6,String.valueOf(plot_y));
               }                              
            }             
            if(marker<=0.01){
                markerType=7;
                markerScale=1.;
            }    
            else {
                markerType=1;
                markerScale=marker;
            }                  
            graph.setXAxisTitle(xTitleArray[plot][label_x]);
            graph.setYAxisTitle(yTitleArray[plot][label_y]);

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
            double tSave;
            t=0.;
            t1=0;
            graph.clearAll=true;
            status.setText("");
            try {
                  if(runTrans) {
                      status.setText("Eliminating transient...");
                      for(i=0;i<ntrans;i++) 
                          t=solver.timeStep(x,t,dtp);   
                      if(curve2) {
                              for(i=1;i<=3;i++) 
                                    x1[i]=x[i]+dx[i];
                                    t1=t;
                      }
                      runTrans=false;
                      status.setText("");
                  }                        
            } catch (ArithmeticException e) {
                  alertDialog alert = new alertDialog("Value has diverged. Try again! ");
                  return false;
            }               
            err=0.;
            if(ncurve>=0) ncurve = graph.deleteAllCurves(); 
                    
            if(plot!=FOURIER) {

                  System.arraycopy(x,0,xp,0,nVariables);
                  z1=z2=x[secVar];
                  tp=t;            
                  if(setAxesRange) {
//                        if(plot==RETURNMAP) {
//                              ymin=xmin;
//                              ymax=xmax;
//                        }
                        data1[0]=xmin;
                        data1[1]=ymin;
                        data1[2]=xmax;
                        data1[3]=ymax;
                        ncurve = graph.addCurve(data1,2,Color.black,0,7,1);
                  }      
                  if(ntrans>0) {                       
                         for(i=0;i<nVariables;i++) {
                             xSave[i]=x[i];
                         }
                         tSave=t;
                 
                         nplot=0;
                         firstDataPoint=true;
                         for(i=0,j=0;i<ntrans;i++) {
                             try {
                                    t=solver.timeStep(x,t,dtp);
                             } catch (ArithmeticException e) {
                                    alertDialog alert = new alertDialog
                                          ("Value has diverged. Try again! ");
                             return false;
                             }                                 
                             nplot=process(x,data,nplot);
                         }        
                         if(nplot > 0) {
                                 if(plot==TIMESERIES)
                                       ncurve = graph.addCurve(data,nplot,transientColor);
                                 else ncurve = graph.addCurve(data,nplot,transientColor,0,
                                                markerType,markerScale);
                         }
                         for(i=0;i<nVariables;i++) {
                              x[i]=xSave[i];
                              if(curve2) x1[i]=x[i]+dx[i];
                         }
                         t=tSave;
                         t1=t;                      
                  }
                  runTrans=false;
                  nplot=0;
                  nplot1=0;
                  firstDataPoint=true;
                  System.arraycopy(x,0,xp,0,nVariables);
                  z1=z2=x[secVar];
                  tp=t;
                  int nit=0;
                  status.setText("Finding initial data...");
                  while(nplot < 2 && nit < 1000) {
                      try {
                            t=solver.timeStep(x,t,dtp);
                      } catch (ArithmeticException e) {
                             alertDialog alert = new alertDialog
                                    ("Value has diverged. Try again! ");
                             return false;
                      }                                
                      nplot=process(x,data1,nplot);
                      if(curve2) {
                              try {
                                    t1=solver.timeStep(x1,t1,dtp);
                              } catch (ArithmeticException e) {
                                    alertDialog alert = new alertDialog
                                          ("Value has diverged. Try again! ");
                              return false;
                              }                                        
                              nplot1=process(x1,data2,nplot1);
                      }        
                      nit++;
                  }
                  status.setText("");
                  if(nplot==0) {
                        alertDialog alert = new alertDialog(this,"Bad parameter values: no data found");
                        graph.paintAll=true; 
                        graph.repaint();
                        return false;
                  }                             
                  if(plot==TIMESERIES) {
                        ncurve = graph.addCurve(data1,nplot,Color.blue);
                        ncurve1=ncurve;
                        if(curve2) {         
                              ncurve = graph.addCurve(data2,nplot,Color.red);
                              ncurve2=ncurve;
                        }
                  }                        
                  else {
                        ncurve = graph.addCurve(data1,nplot,Color.blue,0,
                                    markerType,markerScale);
                        ncurve1=ncurve;
                  }
            }
            else {
                  skip=1;
                  fourierData = new double[2*fourierLength];
                  double scale=Pi2/(FFTskip*dtp*fourierLength);                                    
                  mySpectrum = new powerSpectrum(fourierLength, winNum, scale);
                  fourierIndex=0;
                  int nit=0;
                  status.setText("Calculating spectrum");
                  while(mySpectrum.getNumberOfSpectra()==0 && nit < 10000) {
                      try {
                        t=solver.timeStep(x,t,dtp);
                      } catch (ArithmeticException e) {
                          alertDialog alert = new alertDialog
                                    ("Value has diverged. Try again! ");
                      return false;
                      }                            
                      nplot=process(x,data1,nplot);
                      nit++;
                  }
                  if(mySpectrum.getNumberOfSpectra()==0) {
                        alertDialog alert = new alertDialog(this,"Bad parameter values: no data found");
                        graph.paintAll=true; 
                        graph.repaint();
                        return false;
                  }
                  status.setText("");
                  mySpectrum = new powerSpectrum(fourierLength, winNum, scale);                  
            }      
            graph.paintAll=true; 
            graph.repaint();
            return true;
            
      }

//*********************************************************************
/**
* Iterates ODE and updates graph  
*/
//*********************************************************************
      
      public boolean iterate() {
            double[] moredata = new double[2];
            double[] moredata1 = new double[2];  
            nplot=0;
            nplot1=0;
            try{
                  for(int i=0;i<delay;i++)  
                        t=solver.timeStep(x,t,dt);
            } catch (ArithmeticException e) {
                  alertDialog alert = new alertDialog("Value has diverged. Try again! ");
                  return false;
            }                                                
            nplot=process(x,moredata,nplot);
            if(curve2) {
                  try {
                        for(int i=0;i<delay;i++)                   
                              t1=solver.timeStep(x1,t1,dt);
                  } catch (ArithmeticException e) {
                        alertDialog alert = new alertDialog("Value has diverged. Try again! ");
                        return false;
                  }                               
                  nplot1= process(x1,moredata1,nplot1);            
            }      
            if(nplot > 0 && plot != FOURIER) {
                  graph.paintAll=false;     // Don't paint while updating data  
                  graph.appendToCurve(moredata,1,ncurve1);
                  if(curve2)
                        graph.appendToCurve(moredata1,1,ncurve2);
                  graph.paintAll=true;
                  graph.clearAll=false;
                  graph.repaint();
//                  graph.paint(graph.getGraphics());
            }
                  
            errp=solver.err[plot_x]+solver.err[plot_y];
            if(errp>err) err=errp;                       
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
      }
            

//**********************************************************************

      double poincare(double[] x1_in, double[] x2_in, double t1, double t2, double sec, int secVar) {
            double[] x1,x2;
            double t,dt;
            double precision=0.000001;
            int n=0;
            x1=new double[nVariables];
            x2=new double[nVariables];
            System.arraycopy(x1_in,0,x1,0,nVariables);
            System.arraycopy(x2_in,0,x2,0,nVariables);
            if(Math.abs(x1[secVar]-sec) < Math.abs(x2[secVar]-sec)) {
                  System.arraycopy(x1,0,x,0,nVariables);
                  System.arraycopy(x2,0,x1,0,nVariables);
                  t=t1;
                  t1=t2;
            }
            else {                  
                  System.arraycopy(x2,0,x,0,nVariables);
                  t=t2;
            }            

            while(true) {
                  n++;
                  dt=(t1-t)*(x[secVar]-sec)/(x[secVar]-x1[secVar]); 
                  System.arraycopy(x,0,x1,0,nVariables);
                  t1=t;
                  t=solver.timeStep(x,t,dt);
                  if(Math.abs(x[secVar]-sec) < precision) {
                       System.arraycopy(x,0,x2,0,nVariables);
                       return t;
                  }     
            }
     }
     
//**********************************************************************      
/**
* process data
* @param    x variable
* @param    data for plotting
* @param    nplot number of points indata
* @return   nplot new number of points in data
*/          
//**********************************************************************                        

      int process(double x[], double[] data,  int nplot) {
              int j=2*nplot;
              double z,zmax;
              if((function==PENDULUM) || (function==DUFFING) || (function==VANDERPOHL)) {
                 x[3]=mod(x[3],Pi2);
              }
              switch (plot) {
                  case TIMESERIES:
                     data[j]=x[plot_x];
                     j++;
                     data[j]=x[plot_y];
                     nplot++; 
                     break;                
                  case POINCARE:                    
                        if(function == PENDULUM) {
                                x[1]=mod(x[1],Pi2);
                        }
                        if(xp[secVar] < sec && x[secVar] > sec && !crossed)  {                        
                              t=poincare(xp,x,tp,t,sec,secVar);
                              crossed=true;
                              if(checkRange(x[plot_x],x[plot_y])) {
                                    data[j]=x[plot_x];                
                                    j++;                        
                                    data[j]=x[plot_y];
                                    nplot++;
                              }      
                        }
                        else crossed=false;
                        System.arraycopy(x,0,xp,0,nVariables);
                        tp=t;
                        break;
                  case RETURNMAP:                    
                        if(function == PENDULUM) {
                                x[1]=mod(x[1],Pi2);
                        }
                        if(xp[secVar] < sec && x[secVar] > sec && !crossed)  {                        
                              t=poincare(xp,x,tp,t,sec,secVar);
                              crossed=true;
                              if(firstDataPoint) {
                                      if(checkRange(x[plot_x])) {
                                          zHold=x[plot_x];
                                          firstDataPoint=false;
                                      }    
                              }
                              else{
                                    if(checkRange(zHold,x[plot_x])) {
                                          data[j]=zHold;
                                          j++;
                                          data[j]=x[plot_x];
                                          zHold=x[plot_x];
                                          nplot++;
                                    }
                                    else firstDataPoint=true;
                              }                                
                        }
                        else crossed=false;
                        System.arraycopy(x,0,xp,0,nVariables);
                        tp=t;
                        break;
                  case MAXMAP:                        
                        if(function == PENDULUM) {
                                x[1]=mod(x[1],Pi2);
                        }
                        z=x[secVar];
                        if(z1>z2 && z1>z) {
                              zmax=0.5*(-0.25*z2*z2-0.25*z*z-4.*z1*z1+0.5*z2*z+2*z2*z1+2*z1*z)/
                                      (z2-2*z1+z);     
                              if(firstDataPoint) {
                                      if(checkRange(zmax)) {
                                          zHold=zmax;
                                          firstDataPoint=false;
                                      }    
                              }
                              else{
                                    if(checkRange(zmax)) {
                                          data[j]=zHold;
                                          j++;
                                          data[j]=zmax;
                                          zHold=zmax;
                                          nplot++;
                                    }
                                    else firstDataPoint=true;
                              }
                        }                                                   
                        z2=z1;
                        z1=z;
                        break;
                  case FOURIER:
                     if(skip==FFTskip) {
                           fourierData[fourierIndex]=x[plot_y];
                           fourierIndex++;
                           fourierData[fourierIndex]=0;
                           fourierIndex++;
                           if(fourierIndex >= 2*fourierLength)  {
                              mySpectrum.transform(fourierData);
                              if(ncurve>=0) ncurve = graph.deleteAllCurves();
                              ncurve = graph.addCurve(fourierData,1+fourierLength/2,Color.blue); 
                              graph.paintAll=true; 
                              graph.repaint();                              
                              fourierIndex=0;
                           }
                           nplot++;
                           skip=1;
                     }
                     else skip++; 
                     break;                                                                       
                  default:
                        break;
              }                              
              return nplot;
      }                 


//**********************************************************************
/**
* Sets default values of parameters depending on function number
*/
//**********************************************************************          
      private void setDefaults() {
           parameters.setText(0,aArray[function]);
           parameters.setText(1,bArray[function]);
           parameters.setText(2,cArray[function]);
           parameters.setText(3,dArray[function]);
           parameters.setText(4,dtArray[function]);

           variables.setText(0,x0Array[function]);
           variables.setText(1,y0Array[function]);         
           variables.setText(2,z0Array[function]);
           variables.setText(3,transArray[function]);             
           variables.setText(6,secArray[function]);
      }                 
      
//**********************************************************************
/**
* Shifts x to 0<x<1
* @param x input value
* @return value shifted to between 0 and shift
*/
//**********************************************************************      
      private double mod(double x, double shift) {
         while (x>shift) {
            x=x-shift;
         }   
         while (x<0.) {
            x=x+shift;
         }
         return x;
      }
      
//**********************************************************************      
      private int mod(int x, int shift) {
         while (x>=shift) {
            x=x-shift;
         }   
         while (x<0) {
            x=x+shift;
         }
         return x;
      }      

//**********************************************************************
/**
* Responds to mouse events
* @param xcoord x-coordinate of mouse click
* @param xcoordValid true if x-coordinate of mouse click within graph
* @param ycoord y-coordinate of mouse click
* @param ycoordValid true if y-coordinate of mouse click within graph
*/
//**********************************************************************         
      public void respondToMouse(double xcoord, boolean xcoordValid,
                 double ycoord, boolean ycoordValid){
             double xChange;
             if(plot<2) return;
             if(aThread != null) {
                   clicked=false;                        
                   return;
             }                 
             if(xcoordValid && ycoordValid) {          
                  if(!clicked) {
                       xmin=xcoord;
                       ymin=ycoord;                  
                       clicked=true;
                  }
                  else {
                       clicked=false;
                       xmax=xcoord;
                       ymax=ycoord;
                       if(xmin > xmax) {
                              xChange=xmin;
                              xmin=xmax;
                              xmax=xChange;
                       }
                       if(ymin > ymax) {
                              xChange=ymin;
                              ymin=ymax;
                              ymax=xChange;
                       }       
                       if((xmin != xmax) && (ymin != ymax)) {
                              setAxesRange=true;
                              dummy=restart(); 
                       }       
                  }
            }
            else {
                  updateParameters();                               
                  setAxesRange=false;                  
                  clicked=false;
                  dummy=restart();
            }            
      }        


//**********************************************************************
/**
* Checks if point within plot range
* @param x x-coordinate of point
* @param y y-coordinate of point
* @return true if within range
*/
//**********************************************************************              
      private boolean checkRange(double x,double y) {
            if(!setAxesRange) return true;
            if(x>xmin && x<xmax && y>ymin && y<ymax) return true;
            else return false;
      }
//**********************************************************************
/**
* Checks if x-coordiante within plot range
* @param x x-coordinate of point
* @return true if within range
*/
//**********************************************************************            
      private boolean checkRange(double x) {
            if(!setAxesRange) return true;
            if(x>xmin && x<xmax) return true;
            else return false;
      }      
//********************************************************************** 
}                        
                 
                        
