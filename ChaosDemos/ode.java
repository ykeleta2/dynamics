package ChaosDemos;
/**
* Class to advance ODE's through single time step.<br>
* Uses 5th order Runga-Kutta method with given timestep
* and embedded 4th order method for error checking.
* (See Numerical Recipes, 16.2)
* @version Jume 7, 1997
* @author Michael Cross
*/
public class ode {
       private int i;
       private static final double a2=1./5.;
       private static final double a3=3./10.;
       private static final double a4=3./5.;
       private static final double a5=1.;
       private static final double a6=7./8.;
       private static final double b21=1./5.;
       private static final double b31=3./40.;
       private static final double b41=3./10.;
       private static final double b51=-11./54.;
       private static final double b61=1631./55296.;       
       private static final double b32=9./40.;
       private static final double b42=-9./10.;
       private static final double b52=5./2.;
       private static final double b62=175./512.;
       private static final double b43=6./5.;
       private static final double b53=-70./27.;
       private static final double b63=575./13824.;
       private static final double b54=35./27.;
       private static final double b64=44275./110592.;
       private static final double b65=253./4096.;
       private static final double c1=37./378.;
       private static final double c2=0.;
       private static final double c3=250./621.;
       private static final double c4=125./594.;
       private static final double c5=0.;
       private static final double c6=512./1771.;
       private static final double d1=2825./27648.;
       private static final double d2=0.;
       private static final double d3=18575./48384.;
       private static final double d4=13525./55296.;
       private static final double d5=277./14336.;
       private static final double d6=1./4.;
       
/**
* Number of ODEs and dependent variables
*/
       private int nVariables;
       private double[] k1;
       private double[] k2;
       private double[] k3;
       private double[] k4;
       private double[] k5;
       private double[] k6;
       private double[] xp;
       
/**
* array of error estimates
*/      
       public double[] err;
/**
* Parent class of type dynamicGraph
*/
       dynamicGraph parent;
/**
* @param target calling class
* @param n number of OSDs
*/       
       public ode(dynamicGraph target, int n) {
                                 
            parent = target;
            nVariables = n;
            k1= new double[n];
            k2= new double[n];
            k3= new double[n];
            k4= new double[n];
            k5= new double[n];
            k6= new double[n];
            err=new double[n];
            
            xp= new double[n];
       }
/**
* Replaces input vector with vector after time increment dt<br>
* Calls derivs(double[x], double t, int n) method in
* parent class.
* @param x[] vector of variables
* @param t current time
* @param dt time increment desired
* @return the updated value of the time t=t+dt
*/     
       public double timeStep(double[] x, double t, double dt) throws ArithmeticException {
            k1=parent.derivs(x,t,nVariables);
            for(i=0;i<nVariables;i++)
                xp[i]=x[i]+dt*b21*k1[i];
            k2=parent.derivs(xp,t+a2*dt,nVariables);
            for(i=0;i<nVariables;i++)
                xp[i]=x[i]+dt*(b32*k2[i]+b31*k1[i]);
            k3=parent.derivs(xp,t+a3*dt,nVariables);
            for(i=0;i<nVariables;i++)
                xp[i]=x[i]+dt*(b43*k3[i]+b42*k2[i]+b41*k1[i]);
            k4=parent.derivs(xp,t+a4*dt,nVariables);
            for(i=0;i<nVariables;i++)     
               xp[i]=x[i]+dt*(b54*k4[i]+b53*k3[i]+b52*k2[i]+b51*k1[i]);
            k5=parent.derivs(xp,t+a5*dt,nVariables);
            for(i=0;i<nVariables;i++)     
               xp[i]=x[i]+dt*(b65*k5[i]+b64*k4[i]+b63*k3[i]+b62*k2[i]+b61*k1[i]);
            k6=parent.derivs(xp,t+a6*dt,nVariables);
            for(i=0;i<nVariables;i++) {
                x[i]=x[i]+dt*(c1*k1[i]+c2*k2[i]+c3*k3[i]+c4*k4[i]+c5*k5[i]+c6*k6[i]);
                if(x[i] != x[i]) throw new ArithmeticException();
                err[i]=dt*Math.abs((c1-d1)*k1[i]+(c2-d2)*k2[i]+(c3-d3)*k3[i]
                    +(c4-d4)*k4[i]+(c5-d5)*k1[i]+(c6-d6)*k6[i]);
            }                                                 
            t = t + dt;

            return t;
       }
}
