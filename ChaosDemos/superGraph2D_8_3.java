package ChaosDemos;
import graph.*;
import java.util.*;
import java.awt.*;
//**********************************************************************
/**
*  Class to make and modify 2D graph <br> 
*  Subclass of Graph2D by Leigh Brookshaw <br>
*  Maximum number of curves is 10 <br>
* @version 15 March 1997
* @author Michael Cross
*/
//**********************************************************************

public class superGraph2D extends Graph2D {

      private Vector datac = new Vector(10);
/**
* The x-axis of the graph
*/      
      public Axis    xaxis;
/**
* The y-axis of the graph
*/ 
      public Axis    yaxis;

      private int ncurve;               // Number of curves plotted - 1
      private TextLine text;
      private String title;
      private dynamicGraph outerparent=null;

//**********************************************************************
/**
* Default Constructor
*/
//**********************************************************************

      public superGraph2D () {
        
        // Set up parameters of Graph2D
        drawzero = false;
        drawgrid = false;
        clearAll = true;
        borderTop = 60;
        borderRight=20;
        borderLeft=20;
        borderBottom=20;
        //MCC 3/19/97 change to new setTitleFont etc.
        // x-axis
        xaxis = createAxis(Axis.BOTTOM);
        xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,20));
        xaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,15));

        // yaxis
        yaxis = createAxis(Axis.LEFT);
        yaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,20));
        yaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,15));
        
        // ncurve = -1 for no curves added
        ncurve=-1;
        
        text = new TextLine();
        title=" ";
      }
//**********************************************************************
/**
*  Constructor given dynamicGraph parent 
* @param target parent of type dynamicGraph
*/   
//**********************************************************************
      
      public superGraph2D(dynamicGraph target) {
        this();
        outerparent=target;
      }
      

//**********************************************************************
/**
*  Constructor givin minimum and maximum coordinates 
* (can be rescaled later)
* @param xmin minimum value of x
* @param xmax maximum value of x
* @param ymin minimum value of y  
* @param ymax maximum value of y
*/
//**********************************************************************
            
      public superGraph2D (double xmin, double ymin, double xmax,
        double ymax){        
        
        this();             // Call default constructor
        ncurve=0;           // Number of curves - 1 will be 1
        double data[] = new double[6];
        data[0]=xmin;
        data[1]=ymin;
        data[2]=xmax;
        data[3]=ymin;
        data[4]=xmax;
        data[5]=ymax;

        
        datac.addElement(loadDataSet(data,3));
        ((DataSet)datac.elementAt(ncurve)).linecolor = new Color(0,0,0);
        
        xaxis.attachDataSet((DataSet)datac.elementAt(ncurve));
        yaxis.attachDataSet((DataSet)datac.elementAt(ncurve));
     }

//**********************************************************************
/**
* Adds an additional curve to be plotted<br> 
* Returns index number of curve.
* @param indata data to be plotted indata[0]-[2n-1]
* @param n data points used are indata[0]-[2n-1]
* @param c color of new curve
* @return index number of curves
*/         
//**********************************************************************
     
     public int addCurve(double indata[], int n, Color c) {
        
        ncurve++;
        datac.addElement(loadDataSet(indata,n));
        ((DataSet)datac.elementAt(ncurve)).linecolor   =  c;
        
        xaxis.attachDataSet((DataSet)datac.elementAt(ncurve));
        yaxis.attachDataSet((DataSet)datac.elementAt(ncurve));
        
        return ncurve;
     }
     
//**********************************************************************
/**
* Adds an additional curve to be plotted<br> 
* Returns index number of curve.
* @param indata data to be plotted indata[0]-[2n-1]
* @param n data points used are indata[0]-[2n-1]
* @param c color of new curve
* @param linestyle 0 for points, 1 for line
* @return index number of added curve
*/         
//**********************************************************************
     
     public int addCurve(double indata[], int n, Color c, int linestyle,
        int marker, double markerscale) {
        
        ncurve++;
        datac.addElement(loadDataSet(indata,n));
        ((DataSet)datac.elementAt(ncurve)).linecolor   =  c;
        ((DataSet)datac.elementAt(ncurve)).linestyle   =  linestyle;
        if(linestyle == 0) {
              ((DataSet)datac.elementAt(ncurve)).markercolor = c;
              ((DataSet)datac.elementAt(ncurve)).marker = marker;
              ((DataSet)datac.elementAt(ncurve)).markerscale = markerscale;
        }
        
        xaxis.attachDataSet((DataSet)datac.elementAt(ncurve));
        yaxis.attachDataSet((DataSet)datac.elementAt(ncurve));        
        
        return ncurve;
     }     

//**********************************************************************
/**
* Adds npts from indata[] to curve n  
* @param indata[] data to add from
* @param npt nunmer of points to add
* @param n index of curve to add to
*/
//**********************************************************************
     
     public void appendToCurve(double indata[], int npt, int n) {
         
       if(npt==1) {                    
            ((DataSet)datac.elementAt(n)).appendPoint(indata[0],indata[1]);
       }
       else {
         try {
                ((DataSet)datac.elementAt(n)).append(indata,npt);
              }
         catch (Exception e) {
                   System.out.println("Error appending Data!");
                   }
       }  
     }

//**********************************************************************
/**
* Deletes points from BEGINNING of curve
* @param npt number of points to delete
* @param n curve to delete from
*/
//**********************************************************************
     
     public void deleteFromCurve(int npt, int n) {
            ((DataSet)datac.elementAt(n)).delete(0,npt-1);
     }
     
     public void deleteFromCurve(int n) {
            ((DataSet)datac.elementAt(n)).deleteData();
     }     

//**********************************************************************
/**  Returns number of points in curve n  
* @param n index of curve
* @return number of points in curve
*/
//**********************************************************************

     public int nPoints(int n) {
            return ((DataSet)datac.elementAt(n)).dataPoints();
     }

//**********************************************************************
/**  Returns first np points in curve n  
* @param n index of curve
* @param np number of points to return (must be less than number in curve)
* @return array of points in x,y pairs
*/
//**********************************************************************

     public double[] getData(int n, int np) {
            int nData=((DataSet)datac.elementAt(n)).dataPoints();
            if(np>nData) np=nData;
            double[]array = new double[2*np];
            double[] point = new double[2];
            for(int i=0; i<np;i++) {
               point=((DataSet)datac.elementAt(n)).getPoint(i);
               array[2*i]=point[0];
               array[2*i+1]=point[1];
            }
            return array;
     }

//**********************************************************************
/**
* Deletes all curves
* @return -1 (for number of curves)
*/
//**********************************************************************

     public int deleteAllCurves() {
          if(ncurve>-1) {
            for(int i=0;i<=ncurve;i++) {                
                detachDataSet(((DataSet)datac.elementAt(i)));//MCC 3/19/97
            }
            datac.removeAllElements();
          }
          ncurve = -1;
//        clearAll=true;    6/27/96
          return ncurve;
     }

//**********************************************************************
/**
* Interface to Graph2D that is called before cruves are drawn in paint
* @param g reference to Graphics object
* @param r reference to Rectangle object
*/
//**********************************************************************

     public void paintFirst(Graphics g, Rectangle r) {

           int x = r.x+r.width/2 + 10;
           int y = r.y-30;

           text.setFont(new Font("TimesRoman",Font.PLAIN,20));
           text.setText(title);
           text.draw(g,x,y,TextLine.CENTER);
     }

//**********************************************************************
/**
* Sets title string
* @param intitle text for title
*/
//**********************************************************************
     
     public void setTitle(String intitle) {
            title = intitle;
     }     
//**********************************************************************
/**
* Respond to mouse click in graph<br>
* calls <i>respondToMouse(xcoord, xcoordValid, ycoord, ycoordValid)</i>
* with <i>(xcoord,ycoord)</i> the coordinates of the event (in units of the
* axes) and xcoordValid true if coordinate within range of axis etc.
*/
//**********************************************************************
     
     public boolean mouseDown(Event evt, int x, int y) {
           double xcoord=0;
           double ycoord=0;
           boolean xcoordValid=false;
           boolean ycoordValid=false;
           if (datarect.width != 0.) {
               xcoord = xaxis.minimum + (xaxis.maximum - xaxis.minimum) *
                  ((float)(x - datarect.x))/( (float) datarect.width);
               if(xcoord > xaxis.minimum & xcoord < xaxis.maximum) xcoordValid=true;
           }
           if (datarect.height != 0.) {
               ycoord = yaxis.maximum - (yaxis.maximum - yaxis.minimum) *
                  ((float)(y - datarect.y))/( (float) datarect.height);
               if(ycoord > yaxis.minimum & ycoord < yaxis.maximum) ycoordValid=true;
           }          
//           System.out.println("xcoord= "+ xcoord+"  "+xcoordValid);
//           System.out.println(y+" "+datarect.y+" "+datarect.height);
//           System.out.println("ycoord= "+ ycoord+"  "+ycoordValid);
           if(outerparent != null) 
           outerparent.respondToMouse(xcoord, xcoordValid,ycoord, ycoordValid);
           return true;
     }
     
     public void paintBeforeData( Graphics g, Rectangle r)   {
        outerparent.addToGraph(  g,  r);
     }     

//**********************************************************************
/**
* Sets x-axis title
* @param title text for x-axis title
*/     
//**********************************************************************
     public void setXAxisTitle(String title) {
     
/*  For graph verion 2.1            
            xaxis.title.setText(title);
*/           
            xaxis.setTitleText(title);
     }

//**********************************************************************     
/**
* Sets y-axis title
* @param title text for y-axis title
*/
//**********************************************************************     
     public void setYAxisTitle(String title) {
     
/*  For graph verion 2.1            
            yaxis.title.setText(title);
*/     
     
            yaxis.setTitleText(title);
     }
           
}   
//**********************************************************************
//**********************************************************************


