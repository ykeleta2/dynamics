import ChaosDemos.*;
import java.awt.*;
import java.util.*;
import java.net.URL;
import graph.*;

/**                    
* Iterates and plots various 2D maps <br> 
* Uses the "Java Graph Class Library" by Leigh Brookshaw
* @version 3 August, 1997
* @author Michael Cross
*/

public class Map2D extends dynamicGraph  {

/** number of curves */
      int ncurve=-1; 
/** index of first data curve */      
      int ncurve1;
/** number of iterations to eliminate transient  */   
      int ntrans =10; 
/** accumulated number of iterations */        
      int iterations;
/** number of iterations to do before plotting */      
      int jump=1;
/** function chosen: 0=HENON 1=CIRCLE 2=DUFFING 3=BAKERS 4=YORKE 5=STANDARD */      
      int function=0;
/** Number of subdivisions for box counting */      
      int nDiv=4;      
/** box size in dimesnion algorithm */
      double boxSize;
/** number of boxes across each dimension */      
      int nBoxes;
/** nuber of subdivisions of boxes done */      
      int divided=-1;
/** number of variabels controlled by sliders */      
      int numSliders;      
/** number of data points accumulated in plotData */      
      int nData=0;
/** winding number for circle map       */
      int winding;   
/** set to zero in transients so winding number not updated  */
      int windingAdd=1; 
/** number of iterations contributing to winding */      
      int total=0;

/** true if box-counting thread running */      
      boolean running=false;
/** true after first mouse click */      
      boolean clicked=false;
/** true if boxes should be painted on graph */      
      boolean paintBoxes=false;
/** true on new run */
      boolean newRun;

/** iteration variable */      
      double[] x={0.,0.};
/** mouse position */            
      double[] xmouse={0.,0.};
/** map parameter */      
      double a;     
/** map parameter */                           
      double b;
/** map parameter */       
      double c;
/** plot ranges */      
      double xRange,yRange;
/** reciprocal of plot ranges */      
      double xReduce,yReduce;
/** map parameters a,b,c */ 
      double[] params={1.4,0.3,0.};
/**  starting value of x */
      double[] x0={0.,0.};    
/**  bottom-left corner */
      double[] cmin={-1.,-1.};  
/**  top-right corner */
      double[] cmax={1.,1.};
/** size of marker to be plotted */      
      double marker=0.5;
/** data for pltting */
      double[] plotData;      
      
/** axis labesl */
      String[] axisLabel={"  X ","  Y "};
      
/** GUI elements */      
      private textControls variables;
      private sliderControls parameters;
      private buttonControls buttons;
      private Button dimButton;
      private Choice functionChoice;

/** classes used */
      private boxCalculate myBox;      
      private superGraph2D graph;
      private movie theMovie;
      private linearFitPlot win=null;

/** parent */
      private startMap2D outerparent;
/** animation thread */      
      private Thread aThread=null;
/** box counting thread */      
      private Thread bThread=null;      
      
/** for location of marker.txt */      
      private URL markerURL;
/** location of marker.txt */      
      URL documentBase;
      
/** functions */
      private static final int HENON=0;
      private static final int CIRCLE=1;      
      private static final int DUFFING=2;
      private static final int BAKERS=3;
      private static final int YORKE=4; 
      private static final int STANDARD=5;           
/* log e -> log 10 */      
      private static final double le=0.43429;
/** maximum number of points to be appended to plot */      
      private static int MAX_APPEND=1024;
/** maximum number of attempts to find good initial condition */       
      private static int MAX_TRY=1024;
/** value for testing divergence of iteration */
      private static double MAX_VALUE = 100.;      
/*    arrays for setting parameters based on function choice */
      
/** default values depending on function */
      private String[] xminArray={"-1.0","0.0","-2.0","0.0","0.0","0.0"};
      private String[] yminArray={"-0.5","-3.14159","-2.0","0.0","-2.0","-3.14159"};
      private String[] xmaxArray={"1.5","1.0","2.0","1.0","1.0","1.0","1.0"};
      private String[] ymaxArray={"0.5","3.14159","2.0","1.0","2.0","3.14159"};
      private String[] titleArray={"Henon Map","Circle Map","Duffing Map",
                          "Bakers' Map","Yorke Map","Standard Map"};
      private String[] aArray={"1.4","1.0","2.75","0.4","3.0","0.97"};
      private String[] bArray={"0.3","0.5","0.2","0.6","0.25","1.0"};
      private String[] cArray={"0.0","0.6144","0.0","0.2","0.0","0.0"};                             


/**
* @param target starting class
* @see startChua
*/ 
        public Map2D(startMap2D target, URL in_documentBase) {
        documentBase=in_documentBase;
        graph = new superGraph2D(this);
        
        /*
**      Load a file containing Marker definitions
*/
        try {
           markerURL = new URL(documentBase,"marker.txt");
           graph.setMarkers(new Markers(markerURL));
        } catch(Exception e) {
           System.out.println("Failed to create Marker URL!");
        }

        theMovie = new movie(this);
        
        this.outerparent = target;
        addNotify();   
        setBackground(Color.lightGray);
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill=GridBagConstraints.BOTH;

        setLayout(gridbag);

        graph.borderRight=35;
        graph.borderTop=80;  // MCC 8-10
        
        Panel leftPanel = new Panel();
        leftPanel.setLayout(new BorderLayout());
        
        constraints.gridheight = 1;
        constraints.gridwidth=1;
        constraints.weightx=0.75;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(10,0,10,0); 
        gridbag.setConstraints(leftPanel, constraints);
        add(leftPanel); 
              
        leftPanel.add("Center",graph);
        Panel bottomLeftPanel = new Panel();
        bottomLeftPanel.setLayout( new GridLayout(2,1));
        bottomLeftPanel.add(theMovie);
        
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
        bottomLeftPanel.add(buttons);
        leftPanel.add("South",bottomLeftPanel);              

        Panel rightPanel = new Panel();       
        rightPanel.setLayout(new BorderLayout()); 
           
        Panel topRightPanel = new Panel();
        topRightPanel.setLayout(gridbag);
        rightPanel.add("Center",topRightPanel);                 

        numSliders = params.length;      
        String[] textboxes = new String[numSliders];
        for (int i=0;i<numSliders;i++) {
             textboxes[i]=String.valueOf(params[i]);
        } 
        
        String[] labels = {"  a","   b","   c"};
        parameters = new 
            sliderControls((dynamicGraph) this, textboxes,labels,
                     numSliders,5,0.01);                          
        constraints.fill=GridBagConstraints.NONE;
        constraints.gridheight = 1;
        constraints.gridwidth=1;
        constraints.weightx=1.;
        constraints.weighty = 0.8;
        constraints.gridheight=4;         
        constraints.insets = new Insets(0,0,0,0); 
        gridbag.setConstraints(parameters, constraints);
        topRightPanel.add(parameters);

        String[] textboxes1 = {String.valueOf(cmin[0]),String.valueOf(cmin[1]),
                    String.valueOf(cmax[0]),String.valueOf(cmax[1]),
                    String.valueOf(ntrans),
                    String.valueOf(jump),String.valueOf(nDiv),
                    String.valueOf(marker)};       

        String[] labels1 = {"x_min","y_min","x_max","y_max","trans",
                             " jump","N_div"," mark"};       
        variables = new 
              textControls((dynamicGraph) this, textboxes1,
                       labels1,textboxes1.length,5); 
        constraints.gridwidth=GridBagConstraints.REMAINDER;                                                      
        gridbag.setConstraints(variables, constraints);        
        topRightPanel.add(variables);              

        functionChoice = new Choice();
        functionChoice.addItem("Henon Map");
        functionChoice.addItem("Circle Map");
        functionChoice.addItem("Duffing Map");        
        functionChoice.addItem("Bakers' Map");  
        functionChoice.addItem("Yorke Map");
        functionChoice.addItem("Standard Map"); 
        constraints.gridwidth=1;
        constraints.weighty = 0.2;       
        constraints.gridheight=1;          
        constraints.gridwidth=GridBagConstraints.REMAINDER;        
        constraints.insets = new Insets(0,0,10,0);
        gridbag.setConstraints(functionChoice, constraints);  
        topRightPanel.add(functionChoice);

        Panel bottomRightPanel = new Panel();        
        bottomRightPanel.setLayout(gridbag);        
        dimButton = new Button("Calculate Dimension");
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.weighty = 0.5;       
        constraints.gridheight=2; 
        constraints.insets = new Insets(5,0,0,0);                             
        gridbag.setConstraints(dimButton, constraints);  
        bottomRightPanel.add(dimButton); 
        dimButton.disable();                    

        myBox = new boxCalculate();
        constraints.insets = new Insets(5,0,0,0);         
        gridbag.setConstraints(myBox, constraints);                 
        bottomRightPanel.add(myBox);

        rightPanel.add("South",bottomRightPanel);
        constraints.insets = new Insets(20,0,0,10); 
        constraints.gridheight = 1;
        constraints.weighty = 1.0;             
        constraints.weightx=0.25;
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.fill=GridBagConstraints.BOTH;
        constraints.insets = new Insets(10,0,10,0); 
        gridbag.setConstraints(rightPanel, constraints);        
        add(rightPanel);        

        graph.clearAll=true;
        repaint();
        
        /* Start off run */
        setDefaults();
        updateParameters();
        updateVariables();
        if(randomInitialCondition()) {
            restart();
            buttons.enableGo();
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
            updateVariables();
            if (randomInitialCondition()) {
                restart();
                buttons.enableGo();
            }    
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
 Responds to mouse event    */
      
      public void respondToMouse(double xcoord, boolean xcoordValid,
                 double ycoord, boolean ycoordValid){
             double x1[] = new double[2];
             if(xcoordValid && ycoordValid) {       
                  if(!clicked) {
                       xmouse[0]=xRange*xcoord+cmin[0];
                       xmouse[1]=yRange*ycoord+cmin[1];                  
                       clicked=true;
                       if(aThread != null) {
                            clicked = false;
                            if(ntrans>0) {                                                
                                for(int i=0;i<ntrans;i++) {    
                                    xmouse=iterateMap(xmouse);
                                }    
                            }
                            x[0]=xmouse[0];
                            x[1]=xmouse[1];
//                          System.out.println("x[0]= " + x[0] + " x[1]= " + x[1]);
                       }
                  }
                  else {
                       clicked=false;
                       xcoord=xRange*xcoord+cmin[0];
                       ycoord=yRange*ycoord+cmin[1];
                       if(xcoord > xmouse[0]) {
                            variables.setText(0,String.valueOf(xmouse[0]));
                            variables.setText(2,String.valueOf(xcoord));
                       }
                       else if(xcoord < xmouse[0]) {
                            variables.setText(2,String.valueOf(xmouse[0]));
                            variables.setText(0,String.valueOf(xcoord));
                       }
                       if(ycoord > xmouse[1]) {     
                            variables.setText(1,String.valueOf(xmouse[1]));                     
                            variables.setText(3,String.valueOf(ycoord));
                       }
                       else if (ycoord < xmouse[1]){
                            variables.setText(3,String.valueOf(xmouse[1]));                     
                            variables.setText(1,String.valueOf(ycoord));
                       
                       }     
                       updateParameters();
                       updateVariables();
                       if(randomInitialCondition()) {
                            restart();
                            buttons.enableGo();
                       }    
                  }
//                  if(checkBounds(x) > -1) 
//                    restart();
//                  else iterationBox.setText("Invalid i.c.!");

            }
            else {
                  if(aThread==null) {
                       clicked=false;
                       function=functionChoice.getSelectedIndex();
                       variables.setText(0,xminArray[function]);
                       variables.setText(1,yminArray[function]);
                       variables.setText(2,xmaxArray[function]);
                       variables.setText(3,ymaxArray[function]);  
                       updateParameters();
                       updateVariables();
                       if(randomInitialCondition()) {
                            restart();
                            buttons.enableGo();
                       }
                   }
             }                                                 
      }        

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
                    if(win!=null) win.dispose();                    
                    outerparent.hideWindow();
                    return super.handleEvent(evt);
                case Event.WINDOW_ICONIFY:
                    movieStop();
                    buttons.enableGo();
                    enableAll();
                    return super.handleEvent(evt); 
                case Event.ACTION_EVENT:
                    if(evt.target == functionChoice)  {
                         function = functionChoice.getSelectedIndex();            
                         setDefaults();
                         updateParameters();
                         updateVariables();
                         if(randomInitialCondition()) restart();
                         return super.handleEvent(evt); 
                     }    
//                        if (randomInitialCondition()) {
//                            restart();
//                            buttons.enableGo();
//                        }                             
//                     if(evt.target == xFunctionBox || evt.target == yFunctionBox)  {
//                            updateParameters();
//                            updateVariables();
//                     }


                     else if(evt.target == dimButton) {
                        if(divided<0 ) {
                             plotData = new double[2*nDiv];
                             nData=0;
                             running=true;
                             int np=graph.nPoints(ncurve1); 
                             myBox.setText("Finding dimension for "+np+" points");                         
                             double[] xdata=graph.getData(ncurve1,np);
                             int ixdata[]= new int[2*np];
                             nBoxes=(int) (Math.pow(2.,(double)nDiv));                       

                             for(int i=0;i<2*np;i++) {  
                                  ixdata[i]=(int) (nBoxes*xdata[i]);
                             }     
                             buttons.disableAll();
                             dimButton.enable();
                             disableAll();
                             myBox.setup(ixdata, nDiv);
                             if(bThread!=null) {
                                bThread.stop();
                                bThread=null; 
                             }
                             bThread = new Thread(myBox);
                             bThread.start();                
                             boxSize=1.0;
                             nBoxes=1;
                             divided++;
                             dimButton.setLabel("Continue");                                          
    
                      }
                     else if(divided < nDiv) {
                         if(bThread.isAlive()) {
                              myBox.setText("Push Stop to end calculation");

                         }
                         else {
                             if(!myBox.completed) {
                              myBox.stopRequest();
                              dimButton.setLabel("Dimension");
                              dimButton.disable();
                              myBox.setText("");
                              buttons.enableGo();
                              enableAll();
                              paintBoxes=false;
                              graph.repaint();
                              divided=-1;
                              running=false;
                             }
                             else{                                                                   
                                 divided++;
                                 boxSize=boxSize/2.;
                                 nBoxes=nBoxes*2;                                 
                                 int range= myBox.divIndex[divided-1]-myBox.divIndex[divided];
                                 if(range>0) {
                                    plotData[nData++]=le*Math.log((double)nBoxes);                                 
                                    plotData[nData++]=le*Math.log((double)range);
                                 }                        
                                
                                 if(divided<nDiv)
                                    dimButton.setLabel("Continue");
                                 else
                                    dimButton.setLabel("Plot it");   
                                 graph.clearAll=true;
                                 paintBoxes=true;
                                 graph.paintAll=true;
                                 graph.repaint();
                             }     
                         }    
                     }                            
                     else if(divided == nDiv)  {
                          if(nData>0) { 
                            win = new linearFitPlot(plotData, documentBase);
                            win.setTitle("Dimension Plot");
                            win.setShowIntercept(false);
                            win.setAxisStrings("log(1/box size)","log(number of boxes)");
                            win.setSlopeString("Dimension=");                                                        
                            win.resize(400,400);
                            win.show();
                          }
                          else myBox.setText("No points to plot");
                          dimButton.setLabel("Dimension");
                          dimButton.disable();
                          myBox.setText("");
                          buttons.enableGo();
                          enableAll();
                          paintBoxes=false;
                          graph.repaint();
                          divided=-1;
                          running=false;
                     }     
                     return super.handleEvent(evt);           
                }                     
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
          functionChoice.disable();       
//          for(i=0;i<parameters.ncontrols();i++) 
//                 parameters.t[i].disable();
      }

//*********************************************************************
/** 
* Enables text input in variables
*/ 
//*********************************************************************

      public void enableAll() {
          for(int i=0;i<variables.ncontrols();i++)
                variables.enableText(i);
          functionChoice.enable();       
//          for(int i=0;i<parameters.ncontrols();i++)
//                parameters.enableText(i);
      }

//*********************************************************************
/**
* Returns RHS of 2D map
* @param x[] vector of current value of dependent variables
* @return 2 component vector giving derivatives of dependent variables
*/
//*********************************************************************     
      public double[] derivs(double[] x, double t, int n){


           return x;
     }

//********************************************************************
/**
* Updates parameters from the sliderControls 
*/ 
//********************************************************************
      
      public void updateParameters() {
            int i;
            
            graph.setTitle(titleArray[function]);

            for(i=0;i<numSliders;i++) {
                params[i]= parameters.parseTextField(i, params[i]);
            }
            
            a = params[0];
            b = params[1];
            c = params[2];              
      }

//********************************************************************      
/**
* Update parameters form the textControls 
*/
//********************************************************************      
      public void updateVariables() {
            int i;
            cmin[0] = variables.parseTextField(0,cmin[0]);
            cmin[1] = variables.parseTextField(1,cmin[1]);
            cmax[0] = variables.parseTextField(2,cmax[0]);
            cmax[1] = variables.parseTextField(3,cmax[1]);
            xRange=cmax[0]-cmin[0];
            xReduce=1./xRange;            
            yRange=cmax[1]-cmin[1];
            yReduce=1/yRange;
            ntrans = variables.parseTextField(4,ntrans);
            jump = variables.parseTextField(5,jump);
            nDiv = variables.parseTextField(6,nDiv);
            marker=variables.parseTextField(7,marker);             

      }      
                
//*********************************************************************
/**  Resets plot by deleting all curves and restarting
*/
//*********************************************************************
      
      public void restart() {                                          
            double data1[] = new double[4];
            double data2[] = new double[2];
            int i,j;
            int inBounds;
            int markerType;
            double markerScale;
            graph.clearAll=true;
            if(ncurve>=0) ncurve = graph.deleteAllCurves();                              
            clicked=false;
            newRun=true;

            iterations=0;
            winding=0;
            total=0;
            divided=-1;
            paintBoxes=false;
            data1[0]=0;
            data1[1]=0;
            data1[2]=1;
            data1[3]=1;
            ncurve = graph.addCurve(data1,2,Color.lightGray,0,7,0.5);

            data2[0]=(x[0]-cmin[0])*xReduce;
            data2[1]=(x[1]-cmin[1])*yReduce;
            if(marker<=0.01){
                markerType=7;
                markerScale=1.;
            }    
            else {
                markerType=1;
                markerScale=marker;
            }
            
            ncurve = graph.addCurve(data2,1,Color.blue,0,markerType,markerScale);            
            ncurve1=ncurve;
//            graph.clearAll= false ;                              
            graph.paintAll=true; 
            graph.repaint();
            iterations=0;
            myBox.setText("");                
      }

//*********************************************************************
/**
* Iterates map and updates graph  
*/
//*********************************************************************
      
      public boolean iterate() {
            
            int inBounds;
            int i,n,nplot;
            double[] moredata = new double[2*jump];
            n=0;
            nplot=0;
            for(i=0; i<2*jump; i=i+2) {
                    x=iterateMap(x);
                    inBounds = checkBounds(x);
                    
                    if(inBounds == 1) {
                        moredata[nplot++]=(x[0]-cmin[0])*xReduce;
                        moredata[nplot++]=(x[1]-cmin[1])*yReduce;
                        n=n+1;                    
                    }                    
                    else if (inBounds == -1) {  
                        alertDialog alert = new alertDialog(this, "Value diverged: stop and restart");
                        return false;                        
                    }               
                    if(n > MAX_APPEND) break;
            }    
            if(n==0) return true;
            
            graph.paintAll=false;     // Don't paint while updating data
//            if(graph.nPoints(ncurve)>200) {
//                  graph.deleteFromCurve(100,ncurve);
//            }     

            graph.appendToCurve(moredata,n,ncurve1);
            graph.paintAll=true;
              graph.clearAll=false;
              graph.repaint(); 
              iterations=iterations+n;
              if(function==1 && iterations > 0)
                 myBox.setText("Winding number "+String.valueOf((float) winding/(float) total));
              else
                 myBox.setText("Iteration number "+String.valueOf(iterations));     
            if (checkBounds(x) <0 ) {
                    return false;
            }
            else
                return true;  
      }

//**********************************************************************
/**
* Stop thread and close fit window
*/
//**********************************************************************
      
      public void stop() {
            movieStop();
            if(win!=null) win.dispose();            
            enableAll();
            buttons.enableGo();
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
                 setDefaults();
                 updateParameters();
                 updateVariables();
                 if(randomInitialCondition()) {
                    restart();
                    buttons.enableGo();  
                 }   
            }
            else if(buttonIndex==1) {
/*               updateParameters();
                 updateVariables();
                 if(randomInitialCondition()) {
                    restart();
                    buttons.enableGo();  
                 }       */
                graph.clearAll=false; //6/27/96
                if(aThread != null) {
                  movieStop();
                  restart();
                  newRun=false;
                  movieStart();
                }
                else {
                  updateParameters();
                  updateVariables();
                  boolean dummy=randomInitialCondition();                                
                  restart();
                }                   
            }
            else if(buttonIndex==2) {
                if(clicked) {
                    clicked=false;
                    if (!initialCondition(xmouse)) return;
                    else {
                        x[0]=xmouse[0];
                        x[1]=xmouse[1];
                    }    
                }
                else {
                   if(!newRun) {
                         if (!randomInitialCondition()) return;
                   }      
                   else newRun=false;      
                }     
                buttons.disableGo();
                disableAll();
                windingAdd=1;
                graph.allowDrag=false;
                movieStart();
            }
            else if(buttonIndex==3) {
                windingAdd=0;
                buttons.enableGo();
                movieStop();
                graph.allowDrag=true;
                enableAll();
                dimButton.enable();
            }

      }      

//************************************************
/**
* Iterates map with function given by function
* @param input value
* @return iterated value
*/
//************************************************ 
      
      private double[] iterateMap(double[] x) {        
        double[] x_new={0.,0.};
        switch (function) {
        case HENON :
            x_new[0] = x[1] + 1 -a*x[0]*x[0];
            x_new[1] = b*x[0];
            break;
        case DUFFING :    
          x_new[0] = x[1];
          x_new[1] = -b*x[0]+a*x[1]-x[1]*x[1]*x[1];
          break;
        case CIRCLE :
          x_new[1] = b*x[1]-a*Math.sin(2*Math.PI*x[0]);
          x_new[0] = mod(x[0]+c+x_new[1]/(2*Math.PI));
          break;
        case STANDARD :
          x_new[1] = b*x[1]+a*Math.sin(2*Math.PI*x[0]);
          x_new[0] = mod(x[0]+x_new[1]/(2*Math.PI));
          break;
        case BAKERS :
           if (x[1]<a){
                  x_new[0]=b*x[0];
                  x_new[1]=x[1]/a;
           }
           else{  
                x_new[0]=(1-c)+c*x[0];
                x_new[1]=(x[1]-a)/(1-a);
           }  
           break;
        case YORKE :
            x_new[0] = mod(a * x[0]);
            x_new[1] = b * x[1] + Math.cos(2*Math.PI * x[0]);
            break;        
        default :
          x_new[0]=x[0];
          x_new[1]=x[1];
          break;
        }          
        return x_new;
      }
//*********************************************
/**
* Checks that x is within bounds cmin, cmax
* @param input value
* @return -1 if "diverges" set by MAX_VALUE, 0 if within bounds,         
*  1 if outside bounds
*/
//*********************************************
      
      private int checkBounds(double[] x) {
        if(Math.abs(x[0]) > MAX_VALUE || Math.abs(x[1]) > MAX_VALUE) {
            return -1; 
        }
        if(x[0] < cmin[0] || x[0] > cmax[0] || x[1] < cmin[1] || x[1] > cmax[1])
                 return 0;
        else return 1;
      }

//**********************************************************************
/**
* Picks random intial condition, eliminates transient and test for
* divergences. Returns true if valid, false if cannot find a valid i.c.
* in MAX_TRY attempts.
* @return true if valid starting point found 
*/
//********************************************************************** 
       
      private boolean randomInitialCondition() {
           boolean diverged=true;
           int nTry=0;
           while(diverged && nTry < MAX_TRY) {
                nTry++;
                diverged = false;
                x[0]=cmin[0]+Math.random()*(cmax[0]-cmin[0]);
                x[1]=cmin[1]+Math.random()*(cmax[1]-cmin[1]);               
                if(ntrans>0) {                
                    for(int i=0;i<ntrans;i++) {    
                        x=iterateMap(x);
                        if(checkBounds(x) <0) {
                             diverged=true;
                             break;
                        }     
                    }
                }
                if(!diverged) {
                    while(checkBounds(x) <=0) {
                        x=iterateMap(x);
                        if(checkBounds(x) < 0 ) {
                            diverged = true;
                            break;
                        }    
                    }
                }
                if(!diverged) return true ;   

            } 
            alertDialog alert = new alertDialog(this, "Failed to find valid initial condition");         
            return false;                 
      }

//**********************************************************************
/**
* Eliminates transient from initial condition testing for divergence
* @param initial condition
* @return true if valid point found after transient
*/
//********************************************************************** 
      
      private boolean initialCondition(double[] xm) {
                boolean diverged = false;
                if(ntrans>0) {                
                    for(int i=0;i<ntrans;i++) {    
                        xm=iterateMap(xm);
                        if(checkBounds(xm) <0) {
                             diverged=true;
                             break;
                        }     
                    }
                }
                if (!diverged) return true;
                else {
                    alertDialog alert = new alertDialog
                            ("Invalid i.c., please try again");
                    return false;
                }
       }             

//**********************************************************************
/**
* Sets parameter and variables to default values for each function
*/
//**********************************************************************                                            
      
      private void setDefaults() {
           function=functionChoice.getSelectedIndex();
           variables.setText(0,xminArray[function]);
           variables.setText(1,yminArray[function]);
           variables.setText(2,xmaxArray[function]);
           variables.setText(3,ymaxArray[function]);
           parameters.setText(0,aArray[function]);
           parameters.setText(1,bArray[function]);
           parameters.setText(2,cArray[function]);
//           xFunctionBox.setText(xFunctionArray[function]);
//           yFunctionBox.setText(yFunctionArray[function]);        
      }     

//**********************************************************************
/**
* Shifts x to 0<x<1
* @param x input value
* @return value shifted to between 0 and 1
*/
//**********************************************************************      
      private double mod(double x) {
         while (x>1.) {
            winding=winding+windingAdd;
            x=x-1.;
         }   
         while (x<0.) {
            winding=winding-windingAdd;
            x=x+1.;
         }
         total=total+windingAdd;
         return x;
      }

//**********************************************************************
/**
* Adds boxes to graph
* @param g Graphics context
* @param r data rectangle of graph
*/
//**********************************************************************      
     
      public void addToGraph( Graphics g, Rectangle r)  {
           double xBoxSize, yBoxSize;

           if (paintBoxes) {
               g.setColor(Color.red);
               int start = myBox.divIndex[divided];
               int end = myBox.divIndex[divided-1];
               xBoxSize = r.width*boxSize;
               yBoxSize = r.height*boxSize;

               for (int i=start;i< end;i=i+2) 
                     g.drawRect(r.x+(int)(xBoxSize*myBox.output[i]),
                                r.y+r.height-(int) (yBoxSize*(myBox.output[i+1]+1)),
                                 (int)xBoxSize,(int)yBoxSize);           
               myBox.setText((end-start)/2+" boxes size "+(float)boxSize);                         
           }                  
      }
      
}   
/*******************************************************************/
