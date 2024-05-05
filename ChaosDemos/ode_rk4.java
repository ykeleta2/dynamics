package ChaosDemos;
/**
* Class to advance ODE's through single time step.<br>
* Uses 4th order Runga-Kutta method with given timestep
* @version March 15 1997
* @author Michael Cross
*/
public class ode {
       int i;
/**
* Number of ODEs and dependent variables
*/
       int nVariables;
       double[] k1;
       double[] k2;
       double[] k3;
       double[] k4;
       double[] xp;
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
       public double timeStep(double[] x, double t, double dt) {
       
            k1=parent.derivs(x,t,nVariables);
            for(i=0;i<nVariables;i++) xp[i]=x[i]+0.5*dt*k1[i];
            k2=parent.derivs(xp,t+0.5*dt,nVariables);
            for(i=0;i<nVariables;i++) xp[i]=x[i]+0.5*dt*k2[i];
            k3=parent.derivs(xp,t+0.5*dt,nVariables);
            for(i=0;i<nVariables;i++) xp[i]=x[i]+dt*k3[i];
            k4=parent.derivs(xp,t+dt,nVariables);
            for(i=0;i<nVariables;i++)     
               x[i]=x[i]+dt*(0.5*k1[i]+k2[i]+k3[i]+0.5*k4[i])/3.;
            t = t + dt;
            return t;
       }
}
