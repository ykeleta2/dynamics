package ChaosDemos;
import java.awt.*;
import java.util.*;
import java.net.URL;
import graph.*;

/**
* Plots data in separate frame and allows linear fit for range between two points
* selected with the mouse.
*/

public class linearFitPlot extends Frame {
    private int length;
    private Graph2D graph;
    private Label resultBox;    
    private DataSet data;
    private DataSet lineFit;
    private DataSet rangePlotted;
    private Axis xaxis,yaxis;
    private URL markerURL;
    private URL documentBase;
    private boolean clicked;
    private double xmin,xmax;
    private double[] graphData;
    private String slopeString="Slope = ";
    private String interceptString="Intercept = ";
    private String xAxisString="X";
    private String yAxisString="Y";
    private boolean showSlope=true;
    private boolean showIntercept=true;    

//************************************************************************    
/** 
* @param in_data array of data in x,y pairs
* @param in_documentBase URL giving location of marler.txt file for markers
*/
//************************************************************************
    public linearFitPlot(double[] in_data, URL in_documentBase) {
        lineFit=null;
        rangePlotted=null;
        length=in_data.length;
        graphData=new double[length];
        System.arraycopy(in_data,0,graphData,0,length); 
        documentBase=in_documentBase;       
        setLayout(new BorderLayout());

        graph=new Graph2D();
        add("Center",graph);
        
        resultBox=new Label("                    ",Label.CENTER);
        add("South",resultBox);        

        graph.drawzero = false;
        graph.drawgrid = false;
        graph.clearAll = true;
        graph.borderTop = 40;
        graph.borderRight=20;
        graph.borderLeft=20;
        graph.borderBottom=20;
        //MCC 3/19/97 change to new setTitleFont etc.
        // x-axis
        xaxis = graph.createAxis(Axis.BOTTOM);
        xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,20));
        xaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,15));
        xaxis.setTitleText(xAxisString);

        // yaxis
        yaxis = graph.createAxis(Axis.LEFT);
        yaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,20));
        yaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,15));
        yaxis.setTitleText(yAxisString);
        try {
           markerURL = new URL(documentBase,"marker.txt");          
           graph.setMarkers(new Markers(markerURL));
        } catch(Exception e) {
           System.out.println("Failed to create Marker URL!");
        }       
        
        data = graph.loadDataSet(in_data,length/2);
        data.linestyle = 1;
        data.linecolor = Color.red;
        data.marker    = 1;
        data.markerscale = 2;
        data.markercolor = Color.red;
        
        xaxis.attachDataSet(data);
        yaxis.attachDataSet(data);


        graph.setDataBackground(new Color(255,200,175));
        graph.setBackground(new Color(200,150,100));        
        
        clicked=false;
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
                    this.dispose();     
                    return super.handleEvent(evt);
                default:
                    return super.handleEvent(evt);
            }
       }           

//************************************************************************
/* Selects nearest point and on second click does linear fit to points between
* (and including) the clicked points.
*/
//************************************************************************
     public boolean mouseDown(Event evt, int x, int y) {
           double xcoord=0;
           double ycoord=0;
           boolean xcoordValid=false;
           boolean ycoordValid=false;
           Rectangle gr=graph.bounds();
           if (graph.datarect.width != 0.) {
          
               xcoord = xaxis.minimum + (xaxis.maximum - xaxis.minimum) *
                  ((float)(x - gr.x - graph.datarect.x))/( (float) graph.datarect.width);
               if(xcoord > xaxis.minimum & xcoord < xaxis.maximum) xcoordValid=true;
           }
           if (graph.datarect.height != 0.) {
               ycoord = yaxis.maximum - (yaxis.maximum - yaxis.minimum) *
                  ((float)(y - gr.y - graph.datarect.y))/( (float) graph.datarect.height);
               if(ycoord > yaxis.minimum & ycoord < yaxis.maximum) ycoordValid=true;
           }          
//           System.out.println("xcoord= "+ xcoord+"  "+xcoordValid);
//           System.out.println(y+" "+datarect.y+" "+datarect.height);
//           System.out.println("ycoord= "+ ycoord+"  "+ycoordValid);
//           System.out.println("r.y ="+ graph.datarect.y+" height= "+graph.datarect.height);
//           System.out.println("g.y ="+ gr.y+" g.height= "+gr.height);
//           System.out.println("r.x ="+ graph.datarect.x+" width= "+graph.datarect.width);
//           System.out.println("g.x ="+ gr.x+" g.width= "+gr.width);           
           
//           System.out.println("min= "+yaxis.minimum+" max= "+yaxis.maximum);
           if(xcoordValid && ycoordValid) {
                if(!clicked) {
                    xmin=xcoord;
                    clicked=true;
                }
                else {
                    xmax=xcoord;
                    clicked=false;
                    if(xmin > xmax) {
                            double xswap=xmin;
                            xmin=xmax;
                            xmax=xswap;
                    }
                    fit(xmin, xmax);
                }
           }
           return true;
     }    

//************************************************************************     
/**
*  Does fit (see Numerical Recipes)
*/
//************************************************************************
     private void fit(double xmin,double xmax) {
        int imin=0,imax=length/2;
        imin=findPoint(xmin);
        imax=findPoint(xmax);
        if(imax==0) imax=1;
        if(imin==length) imin=length-1;
        if(imin==imax) imax=imax+1;
        double Sx=0,S=0,Sy=0,Stt=0,ty=0;
        for(int i=imin;i<=imax;i++) {
           S++;
           Sx=Sx+graphData[2*i];
        }
        double xbar=Sx/S;
        for(int i=imin;i<=imax;i++) {
           double ti=graphData[2*i]-xbar;
           double yi=graphData[2*i+1];
           Stt=Stt+ti*ti;
           Sy=Sy+yi;
           ty=ty+ti*yi;
        }
        double b=ty/Stt;
        double a=(Sy-Sx*b)/S;
        double[] fitData = new double[4];
        fitData[0]=graphData[0];
        fitData[1]=a+b*fitData[0];
        fitData[2]=graphData[length-2];
        fitData[3]=a+b*fitData[2];
        if(lineFit!=null) graph.detachDataSet(lineFit);
        lineFit = graph.loadDataSet(fitData,2);
        lineFit.linestyle = 1;
        lineFit.linecolor = Color.blue;
        xaxis.attachDataSet(lineFit);
        yaxis.attachDataSet(lineFit);
        double[] rangeData=new double[4];
        rangeData[0]=graphData[2*imin];
        rangeData[1]=graphData[2*imin+1];
        rangeData[2]=graphData[2*imax];
        rangeData[3]=graphData[2*imax+1]; 
        if(rangePlotted!=null)graph.detachDataSet(rangePlotted);
        rangePlotted = graph.loadDataSet(rangeData,2);
        rangePlotted.linestyle = 0;
        rangePlotted.marker=1;
        rangePlotted.markerscale=2;
        rangePlotted.markercolor=Color.black;
        xaxis.attachDataSet(rangePlotted);
        yaxis.attachDataSet(rangePlotted);               
        graph.repaint();
        String result="";
        if(showSlope) result=result+slopeString+" "+b+"  ";
        if(showIntercept) result=result+interceptString+" "+a;
        resultBox.setText(result);                          
    }                               

//************************************************************************     
/**
* Finds nearst point
*/
//************************************************************************
     private int findPoint(double x) {   
        double dmin=Math.abs(x-graphData[0]);
        int imin=0;
        for (int i=1;i<length/2;i++) {
            double dp=Math.abs(x-graphData[2*i]);
            if(dp<dmin) {
                imin=i;
                dmin=dp;
            }
        }
        return imin;
     }   

//************************************************************************
/**
* Sets text to go with slope result
* @param text text to display
*/
//************************************************************************
    public void setSlopeString(String text) {
        slopeString=text;
    }

//************************************************************************    
/**
* Sets text to go with intercept result
* @param text text to display
*/
//************************************************************************
    public void setInterceptString(String text) {
        interceptString=text;
    }   
 
 //************************************************************************   
 /**
 * Sets axis labels
 * @param xText x-axis label
 * @param yText y-axis label
 */
 //************************************************************************
    public void setAxisStrings(String xText, String yText) {
        xAxisString=xText;
        xaxis.setTitleText(xAxisString);
        yAxisString=yText;
        yaxis.setTitleText(yAxisString);
    }    

//************************************************************************    
/**
* @param yesno true to display slope in answer
*/
//************************************************************************
    public void setShowSlope(boolean yesno) {
        showSlope=yesno;
    }

//************************************************************************    
/**
* @param yesno true to display intercept in answer
*/
//************************************************************************    
    public void setShowIntercept(boolean yesno) {
        showIntercept=yesno;
    }
          
}                
