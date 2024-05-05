package ChaosDemos;
import java.lang.*;
import java.awt.*;
/**
* Calculates dimesion of 2D maps by box counting algorithm, uisng a fast
* sort algorithm to arrange array of finest boxes and then successively
* coarsening and weeding the array.<br>
* Stores number of boxes to cover points in divIndex[nDiv+1]
* @version 2 August 1997
* @author Michael Cross
*/
public class boxCalculate extends Panel implements Runnable {

/* private variables */
    private int[] index;         // array of current boxes in index form
    private int[] x;             // array of current boxes in x,y form
    private int number;          // numebr of current boxes
    private int divisions;       // Number of current boxes across axis
    private int nDiv;            // Highest number of subdivisions
    private int[] copy;          // Working array
    private int nOld;            // Position to write in output array
    private boolean stopRequested;
    private Button b1;
    private TextField status;
    
/* Public varibales */
/**
* Array of size nDiv+1 containing number of boxes covering attractor at each division scale
*/
    public int[] divIndex;
/**
* Array contining box locations: 
* <UL><LI>0 to divIndex[0]-1 for finest subdivision
*     <LI>divIndex[0] to divIndex[1]-1 for next etc.
* </UL>
* Position (i,j) is stored as i+j*N where there are N boxes across one direction of map at
* this scale, and i and j run from 0 to N-1.
*/        
    public int[] output;
/**
* True if calculation of box coverage is complete
*/    
    public boolean completed=false;
//    public int updateCount=10;            

/** Default constructor
* lays out button and textBox controls    
*/
    public boxCalculate() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(gridbag);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5,10,5,10);         
        b1 = new Button(" Stop ");
        gridbag.setConstraints(b1, constraints);
        add(b1);
        b1.disable();
        status = new TextField(25);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(status, constraints);
        add(status);
     }
/**
* Sets up calculation. Must be called before calculation thread is started.
* @param in_x Array of data in x,y pairs of ints given by box number on finest (2^nDiv)
* scale
* @param in_nDiv Number of subdivisions to make (from 0 to nDiv)
*/      
     public void setup (int[] in_x, int in_nDiv) {
        stopRequested=false;
        number = in_x.length/2;
        x= new int[2*number];
        System.arraycopy(in_x,0,x,0,2*number);        
        nDiv=in_nDiv;
        divIndex = new int[nDiv+1];
        nOld=0;
        index = new int[number];
        output = new int[1];
        divisions=(int) (Math.pow(2.,(double)nDiv));
        divIndex[nDiv]=0;     
     }
     
/**
* Thread to perform calculation
*/     
     public void run() {
        stopRequested=false;
        completed=false;
        b1.enable();
        status.setText("Calculating dimension....");        
        int i;
//      System.out.println("nDiv= "+nDiv+" divisions= "+divisions);
        for(int iDiv=nDiv-1; iDiv>=0; iDiv--) {
//          System.out.println("iDiv= "+iDiv+" Divisions= "+divisions);
            toIndex();
//            System.out.println("Unsorted");
//            for(int j=0;j<number;j++) {
//                System.out.println( index[j]);
//            }            
            sort(index,number);
//            System.out.println("Sorted");
//            for(int j=0;j<number;j++) {
//                System.out.println( index[j]);
//            }
            if(stopRequested) return;
            weed(index);
//            System.out.println("Weeded");
//            for(int j=0;j<number;j++) {
//                System.out.println( index[j]);
//            }
            fromIndex();
//            for(int j=0;j<number;j++) {
//                System.out.println("x= "+x[2*j]+" y= "+x[2*j+1]);
//            }
            divIndex[iDiv]=divIndex[iDiv+1]+2*number;
            copy = new int[nOld+2*number];
            System.arraycopy(output,0,copy,0,nOld);
            System.arraycopy(x,0,copy,nOld,2*number);
            nOld=nOld+2*number;
            output = new int[nOld];
            System.arraycopy(copy,0,output,0,nOld);
            for(i=0;i<number;i++) {
                x[2*i]=x[2*i]/2;
                x[2*i+1]=x[2*i+1]/2;
            }
            divisions = divisions/2;
        }
        completed=true;
        status.setText("Dimension calculation done!");        
        b1.disable();
     }
/**
* Converts x,y pair to index
*/     
     private void toIndex() {
        for(int i=0;i<number;i++) 
            index[i]=x[2*i]+divisions*x[2*i+1];
     
     }
/**
* Converts index to x,y pair
*/     
     private void fromIndex() {
        for(int i=0;i<number;i++) {
            x[2*i+1]=index[i]/divisions;
            x[2*i]=index[i]-x[2*i+1]*divisions;
        }
     }
/**
* Sorts index array using method from Numerical Recipes
*/
     private void sort(int ra[], int n)  {
      int i,ir,j,l;
      int rra;
      if (n<2) return;
      l=n/2;
      ir=n-1;
      while(true) {
//          System.out.println("l= "+l+" ir= "+ir);
            if(l > 0) {
                  l=l-1;
                  rra=ra[l];
            }      
            else {
                  rra=ra[ir];
                  ra[ir]=ra[0];
                  ir=ir-1;
                  if(ir == 0) {
                        ra[0]=rra;
                        return;
                  }
            }
            i=l;
            j=l+l+1;
            while(j <=ir) {
                  if(j < ir && ra[j] < ra[j+1]) j++;
                  if(rra <ra[j]) {
                        ra[i]=ra[j];
                        i=j;
                        j=j+j+1;
                  }
                  else
                        j=ir+1;
            }
            ra[i]=rra;
      }
     }
                          
     
/**
 * A bi-directional bubble sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version     1.6f, 31 Jan 1995
 */

/*    void sort(int a[], int length)  {
        int j;
        int limit = length;
        int st = -1;
        int pass=0;
        int count=0;        
        while (st < limit) {
            if(stopRequested) return;
            if(count==updateCount) {
                status.setText("Counting boxes...sort pass "+pass);
                count=0;
            }
            pass++;            
            count++;
            boolean flipped = false;
            st++;
            limit--;
            for (j = st; j < limit; j++) {
//                Thread.yield();
//                if(stopRequested) return;
                if (a[j] > a[j + 1]) {
                    int T = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = T;
                    flipped = true;
                }
            }
            if (!flipped) {
                return;
            }
            for (j = limit; --j >= st;) {
//                Thread.yield();
//                if(stopRequested) return;
                if (a[j] > a[j + 1]) {
                    int T = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = T;
                    flipped = true;
                }
            }
            if (!flipped) {
                return;
            }
        }
    }
*/    

/**
* Weeds out duplicate values
*/
    private void weed(int a[]) {
        int n=0;
        for(int i=1;i<number;i++) {
             if(a[i] != a[n]) {
                n++;
                a[n]=a[i];
             }
        }
        number=n+1;
    }
/**
* stops calculation
*/    
    public void stopRequest() {
        stopRequested=true;
    }     
/**
* Handles button event to stop calculation
*/    
    public boolean handleEvent(Event evt) {
        if(evt.id==Event.ACTION_EVENT && evt.target == b1) {
            status.setText("Calculation aborted!");            
            b1.disable();
            stopRequested=true;
        }
        return super.handleEvent(evt);         
    }  
      
/**
* Sets textBox contents
* @param text contents
*/    
    public void setText(String text) {
        status.setText(text);
    }
    
}         
