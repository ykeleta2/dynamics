import ChaosDemos.*;
import java.awt.*;
import java.util.*;
import graph.*;
/**                    
* Iterates and plots one dimensional chaotic maps.<br>
* <UL><LI>Quadratic Map
* <LI>Sine Map
* <LI>Tent Map
* <LI>"Power Law" Map: map with (b+1) power law cusp at x=0.5
* <LI>Circle Map</UL><BR>
* Shows the map iteration, and calculates power spectrum and histogram.<BR> 
* Uses the "Java Graph Class Library" by Leigh Brookshaw
* @version 2 August 1997
* @author Michael Cross
*/

public class Map1D extends dynamicGraph {

/** functions */
      private static final int QUADRATIC=0;
      private static final int SINE=1;
      private static final int TENT=2;
      private static final int POWER=3;
      private static final int CIRCLE=4;      

/** plot types */      
      private static final int TIMESERIES=0; 
      private static final int FOURIER=1;
      private static final int BIN=2;

/** math constants */      
      private static final double Pi=Math.PI;
      private static final double Pi2=2*Pi;
      
      /* classes used */
      private startMap1D outerparent;
      private superGraph2D graph;
      private movie theMovie;
      private Thread aThread=null;      
      private powerSpectrum mySpectrum;      

      /* GUI classes */
      private textControls variables;
      private buttonControls buttons;
      private choiceControls choices;      
      private Panel topRightPanel;      
      private Choice plotChoice;
      private Choice functionChoice;
      private Label status;                                        
      
      /* flags */
/**  true if user has chosen range with mouse */
      boolean setAxesRange=false;  
/** true after one mouse click */
      boolean clicked=false;       
/** true if transient is to be run on call to restart() */
      boolean runTrans=false;    
/** true if iteration number to be shown. Set by choices */
      boolean showTime;          
/** true if second trace to be plotted */
      boolean curve2 = false;                

/** delay in graph update         */
      int delay=100;
      int binSkip=0;
/** number of iterations to eliminate transient   */
      int ntrans=0; 
/** windowing in power spectrum */
      int winNum=2;
/** Number of iterations to perform before processing  */
      int nf=1;
/** number of points used to plot function */      
      int functionPoints=256;               
      
//      int nplot;
//      int nplot1;
/** number of curves */
      int ncurve=-1;     
/** index of first data curve */
      int ncurve1;
/** index of second data curve */
      int ncurve2;
/** number of data points for power spectrum (must be power of 2!) and histogram */
      int dataLength ;
/** 2*dataLength */
      int dataLength2;
/** index point in accumulating Fourier data */      
      int fourierIndex;
/** plot number selected */
      int plot=0;
/** function number selected */
      int function=0;
/** number of iterations */      
      int iterations=0;
/** maximum bin content, for scaling histogram */      
      int binMax=0;         

/** parameter of map equations */
      double a=3.5;     
/** parameter of map equations */                          
      double b=0.;
/** florr for Fourier transform, so don't take log(0) */      
      double floor=1.e-12;      
                  
/** iteration variable */
      double x=0.;
/* starting value */      
      double x0=0.;
/* stored x for plotting */      
      double xp=0.;
/* second x variable */      
      double x1=0.;
/* stored x1 for plotting */      
      double xp1=0;
/* perturbation for second trace */      
      double dx=0.;

/** array for plotting power spectrum and histogram */
      double[] plotData;
/** array of accumulated numbers in bins */       
      int[] binData;               

/** Range of plot */
      double xmin=0,xmax=1,ymin=0,ymax=1;      
/** xcoord of first mouse click */
      double xmouse;
      
      /* Initialization data */      
/** default parameter a for each function */      
      String[] aArray={"3.5","3.5","1.7","1.7","0.5"};
/** default parameter b for each function */         
      String[] bArray={"0.","0.","0.","0.5","0.2"};
/** default parameter x0 for each function */        
      String[] x0Array={"0.2","0.2","0.2","0.2","0.2"};
/** default parameter ntrans for each function */        
      String[] transArray={"0","0","0","0","0"};
/** number of parameters for each function */      
      int[] parameterNumberArray={1,1,1,2,2};
/** Plot title for ecah function */      
      String[] titleArray={"Quadratic Map","Sine Map","Tent Map",
                              "Power law map","Circle Map"};
/** minimum allowed a for each function */         
      double[] aMinArray={0.,0.,0.,0.,0.};
/** true if minimum a enforced for each function */      
      boolean[] aMinTrueArray={true,true,true,true,false};
/** maximum allowed a for each function */          
      double[] aMaxArray={4.,4.,2.,4.,0.};
/** true if maximum a enforced for each function */          
      boolean[] aMaxTrueArray={true,true,true,true,false};

/** x-axis label for each plot type */                              
      String[] xTitleArray={"X_n","Frequency","X"} ;
/** y-axis label for each plot type */           
      String[] yTitleArray={"X_n+1","log(Power)","Number"};

/**
* @param target starting class
* @see startLor
*/ 
        Map1D(startMap1D target) {
        
        graph = new superGraph2D(this);
        graph.borderRight=35;                       

        
        theMovie = new movie(this);
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
        bottomLeftPanel.setLayout( new GridLayout(1,1));
        bottomLeftPanel.add(theMovie);
         
/*        String[] buttonLabels={"Reset","Clear",
                    "Start"," Step"," Stop"};
        buttons = new buttonControls((dynamicGraph) this, 
                        buttonLabels,buttonLabels.length,true);
        buttons.b_init[0] = true;
        buttons.b_stopped[4] = false;
        buttons.b_started[1] = true;
        buttons.b_started[4] = true;
        buttons.setup();
        bottomLeftPanel.add(buttons);
*/                       
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
        constraints.insets = new Insets(30,0,0,10);
        constraints.fill=GridBagConstraints.NONE;             

        String[] buttonLabels={"Reset","Clear",
                    "Start"," Step"," Stop"};
        buttons = new buttonControls((dynamicGraph) this, 
                        buttonLabels,buttonLabels.length,false);
        buttons.b_init[0] = true;
        buttons.b_stopped[4] = false;
        buttons.b_started[1] = true;
        buttons.b_started[4] = true;
        buttons.setup();
        gridbag.setConstraints(buttons, constraints); 
        topRightPanel.add(buttons);
                       

        String[] textboxes1 = {String.valueOf(aArray[0]),String.valueOf(bArray[0]),
                    String.valueOf(x0Array[0]),String.valueOf(ntrans),
                    "1","0.","0."};
        String[] labels1 = {"   a    ","   b   ","Start x","Transient","Compose",
                             "Delta-x","Window"};
        variables = new 
              textControls((dynamicGraph) this,textboxes1,labels1,textboxes1.length,5);        
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(30,0,0,10);
        gridbag.setConstraints(variables, constraints);
        topRightPanel.add(variables);
        
        functionChoice = new Choice();
        functionChoice.addItem("Quadratic");
        functionChoice.addItem("Sine");
        functionChoice.addItem("Tent");
        functionChoice.addItem("Power law");
        functionChoice.addItem("Circle");
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
        plotChoice.addItem("Bin");
        constraints.gridwidth=1;
        constraints.weighty = 0.2;       
        constraints.gridheight=1;          
        constraints.gridwidth=GridBagConstraints.REMAINDER;        
        constraints.insets = new Insets(0,0,0,0);
        gridbag.setConstraints(plotChoice, constraints);  
        topRightPanel.add(plotChoice);
        
        String[] choiceLabels={"Show Time:"};
        choices = new choiceControls((dynamicGraph) this, 
                        choiceLabels);
        constraints.weighty = 0.25;
        constraints.gridheight = 1;
        gridbag.setConstraints(choices, constraints);
        topRightPanel.add(choices);
        choices.setState(0,true);            
        showTime=true;             
        theMovie.toSleep=true;                     

        rightPanel.add("Center",topRightPanel);
        status=new Label("                    ");
        rightPanel.add("South",status);
        
        graph.clearAll=true;
        repaint();

        /* Setup run */
        buttons.enableGo();
        updateParameters();
        variables.hide(1);
        variables.hide(6);     
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

//**********************************************************************
/**
* Responds to choiceControls
* @see choiceControls
*/      
//**********************************************************************      
      public void respondToChoices() {
            if(choices.getState(0))
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
            }
            else if(buttonIndex==1) {
//                graph.clearAll=false; //6/27/96
                graph.clearAll=true;
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
                clicked=false;
                movieStart();
            }
            else if(buttonIndex==3) {
                iterate();
            }
            else if(buttonIndex==4) {
                buttons.enableGo();
                movieStop();
                enableAll();                
                status.setText("No. of Iterations = "+iterations);
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
                if(aThread==null) {
                    aThread = new Thread(theMovie);
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
                    return super.handleEvent(evt);                
                case Event.ACTION_EVENT:
                    if(evt.target == functionChoice || evt.target == plotChoice)  {
                        resetRange();
                        function = functionChoice.getSelectedIndex();            
                        if(evt.target == functionChoice) {
                              for(int i=0;i<parameterNumberArray[function];i++)
                                    variables.show(i);
                              for(int i=parameterNumberArray[function];i<2;i++)
                                    variables.hide(i);                               
                              setFunctionDefaults();
                        }
                        plot = plotChoice.getSelectedIndex();
                        setPlotDefaults();
                        updateParameters();
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
          plotChoice.enable();
          functionChoice.enable();      
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

            b=variables.parseTextField(1, b, true);
            if(function==POWER) aMaxArray[function]=Math.pow(2.,1+b);
            a=variables.parseTextField(0, a);
            if(aMinTrueArray[function])  {
                  if(a<aMinArray[function]) {
                        a=aMinArray[function];
                        variables.setText(0,String.valueOf(a));
                        alertDialog alert = new alertDialog
                              ("a must be bigger than "+String.valueOf(a));
                  }            
            }
            if(aMaxTrueArray[function])  {
                  if(a>aMaxArray[function]) {
                        a=aMaxArray[function];
                        variables.setText(0,String.valueOf(a));
                        alertDialog alert = new alertDialog
                              ("a must be smaller than "+String.valueOf((float)a));
                  }            
            }

                   
            x0=variables.parseTextField(2,x0);
            x=x0;

            ntrans=variables.parseTextField(3,ntrans);
            if(ntrans>0) runTrans=true;                                                
            curve2=false;

            if(plot==TIMESERIES) {
                  dx=variables.parseTextField(5,dx);                  
                  if(dx!=0.) curve2=true;
                  nf=variables.parseTextField(4,nf,true);
            }                 
            else if(plot==FOURIER) {
                  nf=variables.parseTextField(4,nf,true);
                  dataLength=variables.parseTextField(5,256,true);
                  dataLength2=2*dataLength;
                  // Check for power of 2
                  int test=dataLength;
                  int power=0;
                  while(test>1) {
                        test=test/2;
                        power++;
                  }
                  test=1;
                  for(i=0;i<power;i++) test=test*2;
                  if(test!=dataLength) {
                        alertDialog alert = new alertDialog(this," No. of points must be power of two ");
                        dataLength=test;
                        dataLength2=2*dataLength;
                        variables.setText(5,String.valueOf(dataLength));
                  }
                  winNum=variables.parseTextField(6,winNum,0,3);
            }
            else if(plot==BIN) {
                  nf=variables.parseTextField(4,nf,true);
                  dataLength=variables.parseTextField(5,10);            
                  dataLength2=2*dataLength;
            }                      
            graph.setXAxisTitle(xTitleArray[plot]);
            graph.setYAxisTitle(yTitleArray[plot]);

            restart();
      }      
                
//*********************************************************************
/**
* Resets plot by deleting all curves and restarting
*/
//*********************************************************************
      
      public void restart() {
            double data[] = new double[2*functionPoints];
            double data1[] = new double[6];
            double data2[] = new double[6];
            int i,j;
            double xSave;
            double tSave;
            double f;
            int ip=0;
            status.setText("");
            iterations=0;
            
            // Eliminate transient
            if(runTrans) {
                for(i=0;i<ntrans;i++) 
                    x=iterateFunction(x,nf);                   
                runTrans=false;
                iterations+=ntrans;        
            }
            xSave=x;                        
            
            // Reset graph
            if(ncurve>=0)
                  ncurve = graph.deleteAllCurves();                       
            if(plot==TIMESERIES) {       
                  data1[0]=xmin;
                  data1[1]=xmin;
                  data1[2]=xmax;
                  data1[3]=xmax;
                  ncurve = graph.addCurve(data1,2,Color.black);
  
                  // Plot function
                  for(i=j=0; i<functionPoints; i++) {
                      x =((double) i)/((double)(functionPoints-1));
                      f = iterateFunction(x,nf);
                      if((x>=xmin) && (x<=xmax) ){
                            data[j++] = x;
                            data[j++] =f;
                            ip++;
                      }
                      x=f;
                  }
                  if(ip > 2) {
                        ncurve = graph.addCurve(data,ip,Color.darkGray);
                  }
                  
                  // Plot initial point
                  x=xSave;
                  if(curve2)  x1=x+dx;                                    
                  runTrans=false;
//                  nplot=0;
                  xp=x;
                  data1[0]=x;
                  data1[1]=x;
                  x=iterateFunction(x,nf);
                  data1[2]=xp;
                  data1[3]=x;
                  xp=x;
                  iterations++;                                                         
                  ncurve = graph.addCurve(data1,2,Color.blue);
                  ncurve1=ncurve;
                  if(curve2) { 
//                        nplot1=0;
                        xp1=x1;
                        data2[0]=x1;
                        data2[1]=x1;
                        x1=iterateFunction(x1,nf);
                        data2[2]=xp1;
                        data2[3]=x1;
                        xp1=x1;                                      
                        ncurve = graph.addCurve(data2,2,Color.red);
                        ncurve2=ncurve;
                  } 
                  graph.clearAll=false;                      
            }
            else if(plot==FOURIER) {
                  // Set up power spectrum
                  plotData = new double[dataLength2];
                  double scale=2./(nf*dataLength);                                    
                  mySpectrum = new powerSpectrum(dataLength, winNum, scale);
                  mySpectrum.setFloor(floor);   
                  
                  // Plot first spectrum
                  fourierIndex=0;
                  status.setText("Calculating spectrum");
                  for(i=0;i<dataLength;i++) {
                     x=iterateFunction(x,nf);                   
                     plotData[fourierIndex]=x;
                     fourierIndex++;
                     plotData[fourierIndex]=0;
                     fourierIndex++;
                  }                                  
                  iterations+=nf*dataLength;
                  status.setText("No. of Iterations "+ iterations);                                                             
                  mySpectrum.transform(plotData);
                  graph.paintAll=false;
                  if(ncurve>=0) ncurve = graph.deleteAllCurves();
                  ncurve = graph.addCurve(plotData,1+dataLength/2,Color.blue); 
                  ncurve1=ncurve;
                  graph.paintAll=true; 
                  graph.repaint(delay/2);                              
//                  nplot++;
                  graph.clearAll=true; 
  

                  // Reset spectrum
                  mySpectrum = new powerSpectrum(dataLength, winNum, scale);
                  mySpectrum.setFloor(floor);
                  fourierIndex=0;                  
            }
            else if(plot==BIN) {
                  //Set up histogram
                  binData = new int[dataLength2];
                  plotData = new double[dataLength2];
                  binMax=0;
                  for(i=0,j=0;i<dataLength;i++) {
                        plotData[j]=xmin+(i+0.5)*(xmax-xmin)/dataLength;                        
                        j++;
                        binData[j]=0;
                        j++;
                  }
                  // Plot first histogram
                  for(i=0;i<dataLength;i++) {     
                        x=iterateFunction(x,nf);
                        int index=1+2*((int) ((dataLength)*(x-xmin)/(xmax-xmin)));
                        if(index>0 && index < dataLength2) {
                                binData[index]++;
                                if(binData[index] > binMax) binMax=binData[index];
                        }
                  }  
                  iterations+=dataLength;    
                  data[0]=xmin;
                  data[1]=0.;
                  data[2]=xmax;
                  data[3]=1.;
                  ncurve = graph.addCurve(data,2,Color.black,0,7,1);
                  for(j=1;j<dataLength2;j+=2)
                        plotData[j]=((double)binData[j])/((double)binMax);
                  ncurve = graph.addCurve(plotData,dataLength,Color.blue);
                  ncurve1=ncurve;
                  graph.clearAll=true;
            }      
//            graph.paintAll=true;         //Removed 9/30/97
            graph.clearAll=true;           //Added 9/30/97
            graph.repaint();
            status.setText("No. of Iterations "+ iterations);           
            
      }

//*********************************************************************
/**
* Iterates Map equations and updates graph according to value of plot 
*/
//*********************************************************************
      
      public boolean iterate() {
            int i,j;
            double[] moredata = new double[6];
            double[] moredata1 = new double[6];
              switch (plot) {
                  case TIMESERIES:
                     moredata[0]=xp;
                     moredata[1]=x;
                     moredata[2]=x;
                     moredata[3]=x;
                     moredata[4]=x;
                     x=iterateFunction(x,nf);
                     moredata[5]=x;                     
                     if(showTime)
                           status.setText("No. of Iterations "+ iterations);                     
                     iterations++;                     
                     xp=x;
                     if(curve2) {
                           moredata1[0]=xp1;
                           moredata1[1]=x1;
                           moredata1[2]=x1;
                           moredata1[3]=x1;
                           moredata1[4]=x1;
                           x1=iterateFunction(x1,nf);                                             
                           moredata1[5]=x1;
                           xp1=x1;                     
                     } 
                     graph.paintAll=false;   
                     graph.appendToCurve(moredata,3,ncurve1);
                     if(curve2)
                          graph.appendToCurve(moredata1,3,ncurve2);
                     graph.paintAll=true;
//                     graph.clearAll=false;
                     graph.repaint();                     
                     break;                
                  case FOURIER:
                     x=iterateFunction(x,nf);                   
                     plotData[fourierIndex]=x;
                     fourierIndex++;
                     plotData[fourierIndex]=0;
                     fourierIndex++;                               
                     iterations+=nf;
                     if(showTime)
                           status.setText("No. of Iterations "+ iterations);                                                             
                     if(fourierIndex>=dataLength2){
                        mySpectrum.transform(plotData);
                        graph.paintAll=false;
//                        if(ncurve>=0) ncurve = graph.deleteAllCurves();
                        graph.appendToCurve(plotData,1+dataLength/2,ncurve1); 
                        graph.deleteFromCurve(1+dataLength/2,ncurve1);
                        graph.paintAll=true; 
                        graph.repaint(delay/2);                              
//                        nplot++;
                        fourierIndex=0;
                     }                           
                     break;
                  case BIN:
                        int index=0;                        
                        for(i=0;i<binSkip;i++) {
                              x=iterateFunction(x,nf);                           
                              index=1+2*((int) ((dataLength)*(x-xmin)/(xmax-xmin)));
                              if(index >=0 && index <dataLength2) {
                                   binData[index]++;
                                   if(binData[index] > binMax) binMax=binData[index];                                                                          
                              }
                        }
                        iterations+=binSkip;
                        if(showTime)
                              status.setText("No. of Iterations "+ iterations);                                                     
                        graph.paintAll=false;
//                        if(ncurve>=0) ncurve = graph.deleteAllCurves();
//                        ncurve = graph.addCurve(moredata,2,Color.black,0,7,1);
                        
                        for(j=1;j<dataLength2;j+=2)
                                plotData[j]=((double)binData[j])/((double)binMax);                                                  
                        graph.deleteFromCurve(ncurve1);     
                        graph.appendToCurve(plotData,dataLength,ncurve1);  
                                               
                        graph.paintAll=true;
                        graph.repaint();
                  default:
                     break;
              }                                   

                  
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
            binSkip=1000/delay;
            if(binSkip==0) binSkip=1;
      }
            
//**********************************************************************
/**
* Sets default values of parameters depending on function number
*/
//**********************************************************************          
      private void setFunctionDefaults() {
           variables.setText(0,aArray[function]);
           variables.setText(1,bArray[function]);    
           variables.setText(2,x0Array[function]);
           variables.setText(3,transArray[function]);           
      }                 

//**********************************************************************
/**
* Sets default values of parameters depending on plot type
*/
//**********************************************************************          
      private void setPlotDefaults() {
               if(plot==TIMESERIES) {                     
                     variables.hide(6);  
                     variables.setText(4,"1");
                     variables.setLabelText(4,"Compose");                              
                     variables.setText(5,"0.");
                     variables.setLabelText(5,"Delta-x");                                                       
               }                                          
               else if(plot==FOURIER) {
                     variables.show(6);                                                    
                     variables.setText(4,"1");
                     variables.setLabelText(4,"Skip");
                     variables.setText(5,"256");
                     variables.setLabelText(5,"Points");
                     variables.setText(6,"0");
                     variables.setLabelText(6,"Window");                              
               }
               else if(plot==BIN) {
                     variables.hide(6);                                                   
                     variables.setText(4,"1");
                     variables.setLabelText(4,"Iterate");
                     variables.setText(5,"256");
                     variables.setLabelText(5,"Bins"); 
               }                                                                                          
               else variables.showAll();
               topRightPanel.validate();            
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
             if(plot==FOURIER) return;
             if(aThread != null) {
                   if(plot==TIMESERIES && xcoordValid ) {
                        x0=xcoord;
                        x=x0;
                        restart();
                   }     
                   clicked=false;                        
                   return;
             } 
             else {               
                   if(xcoordValid && ycoordValid) {          
                        if(!clicked) {
                             xmouse=xcoord;                
                             clicked=true;
                        }
                        else {
                             clicked=false;
                             xmax=xcoord;
                             xmin=xmouse;
                             if(xmin > xmax) {
                                    xChange=xmin;
                                    xmin=xmax;
                                    xmax=xChange;
                             }
                             setAxesRange=true;
                             restart(); 
                        }
                  }
                  else {
                        resetRange();
                        clicked=false;
                        restart();
                  }
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
//            if(!setAxesRange) return true;
            if(x>xmin && x<xmax) return true;
            else return false;
      }      
//********************************************************************** 

//**********************************************************************
/**
* Resets x-range to 0 < x < 1
*/
//**********************************************************************            
      private void resetRange() {
            xmin=0.;
            xmax=1.;
            ymin=0.;
            ymax=1.;
      }      
//********************************************************************** 

//*********************************************************************
/**
* Returns map function of class variable x 
* @param x current value
* @return new value
*/ 
//*********************************************************************

      public double iterateFunction(double x, int n) {
            double xit = x;
            for(int i=1;i<=n;i++) {
                  switch (function) {
                        case QUADRATIC: 
                              xit=a*xit*(1-xit);
                              break;
                        case SINE:
                              xit=0.25*a*Math.sin(Pi*xit);
                              break;
                        case TENT:
                              if(x<0.5) xit=a*x;
                              else xit=a*(1-x);
                              break;
                        case POWER:
                              xit=a*(Math.pow(0.5,(1.+b))-Math.pow((Math.abs(x-0.5)),(1.+b)));
                              break;                  
                        case CIRCLE:
                              xit=mod(x+b-a*Math.sin(Pi2*x)/Pi2,1.);
                        default:
                              break;
                  }
            }      
//          System.out.println(xit);
            return xit;
      }
      
/*      int plotIteration(double[] data, double[] x3, int pointer) {
            int pointer1=mod(pointer+1,3);
            int pointer2=mod(pointer+2,3);
            x=x3[pointer];
            data[0]=x3[pointer1];
            data[1]=x3[pointer2];
            data[2]=x3[pointer2];
            data[4]=x3[pointer2];
*/
}                        
                 
                        
