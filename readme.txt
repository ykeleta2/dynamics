Modified from chaos3_19_97 to use graph2D version 2.4 (modified as before for
smoother graphic updates). V2.4 modifies the names of some methods and axis.title
becomes protected, so that setTitle... methods are used instead.

V2.4 includes RTextLine method for rotating the text on the y-axis. Unfortunately this
makes the screen update on redrawing the axes unpleasantly slow on a "slow" computer
(75MHz Pentium).

Uses Graph2d version 2.4 (modified). 

Changes 6/697

1) In graph2D paintBeforeData(lg,r) -> paintBeforeData(lg,datarect)

2) sliderControls: make textControls public

3) Add method addToGraph(Graphics g, Rectangle r) to dynamicGraph, called by superGraph
to allow other stuff to be added to graph (e.g. counting boxes)

4) Change void iterate() to boolean iterate() in dynamicGraph etc. and superGraph

5) Add enableAll() and disableAll to buttonControls

6) In Axis.java: String.valueOf(val) -> String.valueOf((float)val) since otherwise
jdk1_1 and related browsers make ugly format of numbers.

7) superGraph: add method addCurve(......., int marker, double markerscale) to allow
points only to be plotted.

8) superGraph: add method double[] getData(int n, int np)

Changes 6/9/97

1) Change handling of delay in movie for ode's (set toSleep false). In this case movie
calss updateSpeed in main program (via dynamicGraph) which allows delay to be incorporated
by changing iteration time step. Default is toSleep=true, when delay is done by sleeping
in movie.

2) Plotting time step and iterating time step set differently.

3) trans is now a time rather than a number of iterations.

4) Change buttonControls position in Scalemap

5) Various changes to layouts

6) Change ode to 5th order RK with embedded 4th order method to get error estimate.

7) Add TextField status to Lorenz and Chua to show time and error estimats.

8) Reduce number and simplify array of delay times in movie.

9) In graph.Axis.java and TesxtLine.java change SpecialFunction.log10 to internal log10
(as in 2.1) so whole SpecialFunction class does not need to be loaded.

Changes 6/14/97

1) Added choiceControls: use in Lorenz to choose whether to show ghost and whether to sleep
in thread. Rename the control class in Scalemap as scalemapControls.

2) Various cosmetic changes via insets.

Changes 6/19/97

1) Add choice controls to Chua. Add choice to turn off showing time, since flickers on
slow computer.

2) Add winding number calculation to circle map in Map2D.

3) Adjust starting with random i.c. in Map2D.

4) Disable and enable choice control in disableAll and enableAll.

Changes 8/5/97

1) Add javadoc comments

2) Make appropriate variables private and add methods to ChaosDemos classes

3) In movie.java update parent delay value when scroll bar is changed.

4) Get odes to throw ArithmeticException for NaN (x != x): catch exception in calling
methods.

5) Set number of buttons etc. within classes from length of input text array.

6) Add rubberbanding to supergraph2d. Turn on with allowDrag.

7) Synchronize data add and draw methods on DataSet

8) Move location of clearAll in graph2D

9) make restart boolean in Lorenz

Changes 9/97

1) In Axis.java make axes jump in size by 5% in ReserRange.
Also change != to < or >

