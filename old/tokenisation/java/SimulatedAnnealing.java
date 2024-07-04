//import javax.swing.*; import java.awt.*; import java.io.*; import java.util.Random;

class InstrumentationWindow {
    int physicalRange;
    int timePointCount; 
    int currentTime;

  class Channel {
    String name; 
    double maxValue; 
    double points[]; 
    Color colour;

    Channel() {this.maxValue = 1000; this.colour = Color.BLACK; points = new Double[timePointCount];}
    void colour(Color colour){this.colour = colour;}
    void maxValue(double maxValue) {this.maxValue = maxValue;}
    void name(String name) {this.name = name;}
  }

 Channel channels[];
 
    void colour(int channel, Color colour) {channels[channel].colour(colour);}
    void maxValue(int channel, double maxValue) {channels[channel].maxValue(maxValue);}
    void name(int channel, String name) {channels[channel].name(name);}
    void point(int channel, double point) {channels[channel].points[currentTime] = point;}
    void advance(){currentTime = (currentTime + 1) % channels[0].points.length;}
 
  class Frame extends JFrame {
    Frame() {
     setTitle("Instrumentation window");
      setContentPane(new GraphPanel());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(timePointCount + 50, physicalRange + 50);
      setLocation(0,430);
      setVisible(true);
    }
  }

  class GraphPanel extends JPanel {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (int i = 0; i < currentTime; i++)
	  for (int j = 0; j < channels.length; j++) {
          g.setColor(channels[j].colour); 
	  double yProportion = channels[j].points[i] / channels[j].maxValue;
          int y = physicalRange - (int) ((double) physicalRange * yProportion);
          g.drawRect(i, y, 3, 3);
	  }
    }
  }
  Frame frame;
  void repaint() {frame.repaint();}


  InstrumentationWindow(int channelCount, int timePointCount, int physicalRange){
      this.physicalRange = physicalRange;
      currentTime = 0;
      this.timePointCount = timePointCount;
      channels = new Channel[channelCount];
      for (int i = 0; i < channelCount; i++) channels[i] = new Channel();
      frame = new Frame();
  }
}

/*******************************************************************************/

class CombinationalProblemInstance {
  class RouteElement {double x; double y; String name; RouteElement(double x, double y, String name){this.x = x; this.y = y; this.name = name;}};
    RouteElement elements[];
    RouteElement startOrder[];
    double xExtent;
    double yExtent;
    double riverX;
    double riverCost;
    int routeLength;
  
  class MapFrame extends JFrame {
    MapFrame() {
     setTitle("Route map");
      setContentPane(new MapPanel());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize((int) xExtent+50, (int) yExtent+50);
      setVisible(true);
    }
  }

  class MapPanel extends JPanel {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      g.setColor(Color.BLUE); g.fillRect((int) riverX - 5, 0, 10, (int) yExtent);
      g.setColor(Color.RED); 
      for (int i = 0; i < elements.length; i++) g.fillRect((int) elements[i].x - 2, (int) elements[i].y - 2, 5, 5);
      g.setColor(Color.BLACK); 
      for (int i = 0; i < elements.length - 1 && i < routeLength; i++) g.drawLine((int) elements[i].x, (int) elements[i].y, (int) elements[i+1].x, (int) elements[i+1].y);
      g.drawLine((int) elements[elements.length - 1].x, (int) elements[elements.length - 1].y, (int) elements[0].x, (int) elements[0].y);
    }
  }

  MapFrame map;
  void repaint() {map.repaint();}

  CombinationalProblemInstance(int size, double xExtent, double yExtent, double riverX, double riverCost) { 
    this.xExtent = xExtent; this.yExtent = yExtent; this.riverX = riverX; this.riverCost = riverCost;
    Random random = new Random();
    System.out.println("Route constructor for " + size + " cities in region (" + xExtent + ", " + yExtent + ")\n");
    elements = new RouteElement[size];
    startOrder = new RouteElement[size];
    routeLength = size;
    for (int i = 0; i < elements.length; i++) {
      double xCoordinate; 
      do 
        xCoordinate = random.nextDouble() * xExtent;
      while ((int) xCoordinate > riverX - 10 && (int) xCoordinate < riverX + 10);

      startOrder[i] = elements[i] = new RouteElement(xCoordinate, random.nextDouble() * yExtent, "");
    }
    map = new MapFrame();
  }

  void swap(int a, int b){RouteElement temp = elements[a]; elements[a] = elements[b]; elements[b] = temp;}
  int size() {return elements.length;}

  void move(int start, int finish) {
    if (start > finish) {int swapTemp = start; start = finish; finish = swapTemp;}
    // Comment out the line below to use simple swaps
    while (start < finish) 
      swap(start++, finish--);
  }

  boolean spansRiver(int a, int b) {return (elements[a].x < riverX && elements[b].x > riverX) || (elements[a].x > riverX && elements[b].x < riverX);}

  double separation(int a, int b) {
    double dx = elements[a].x - elements[b].x, dy = elements[a].y - elements[b].y;
    return Math.sqrt(dx * dx + dy * dy) + (spansRiver(a, b) ? riverCost : 0);
  }

  double evaluate() {
    double result = separation(elements.length - 1, 0);
    for (int i = 1; i < elements.length; i++) result += separation(i, i-1);
    return result;
  }

    private int sp;
    private int expSize;
    private int eRoute[];
    private int count;

  void exhaustiveEval(int i, int mask)
{
    if (i > 20) {
      System.out.println("Full analysis of " + i + " cities would take to long");
      System.exit(1);
    }

  if ((mask & (1 << i)) != 0)
    return;

  mask += 1 << i;               /* mask out this city */
  if (i != eRoute.length) eRoute[sp++] = i;

  if ((mask & expSize) == expSize)  /* bottom of tree */
  {
      for (int city = 0; city < eRoute.length; city++) elements[city] = startOrder[eRoute[city]];
      
      double distance = evaluate();

      repaint();
      count++;
      if (count % 1000000 == 0) System.out.printf("%d%n", count);
  }
  else
    for (i = 0; i < eRoute.length; i++) exhaustiveEval(i, mask);

  --sp;                         /* unstack this city */
}

  void exhaustive() {
      count = 0;
    expSize = 1;
    for (int i = 0; i < elements.length; i++)
      expSize *= 2;
    expSize--;
    sp = 0;
    eRoute = new Int[elements.length];
    long startTime = System.currentTimeMillis();
    exhaustiveEval(eRoute.length, 0);
    long elapsedTimeMillis = System.currentTimeMillis()-startTime;
    float elapsedTimeSec = elapsedTimeMillis/1000;
    System.out.printf("%nTotal %d routes: %.3f seconds elapsed%n", count, elapsedTimeSec);
  }

  void nearest() {
    System.out.println("Nearest city heuristic for " + elements.length + " cities");
    Boolean inRoute[] = new Boolean[elements.length];
    for (int i = 0; i < elements.length; i++) inRoute[i] = false;

    elements[0] = startOrder[0]; inRoute[0] = true;
    for (int i = 1; i < elements.length; i++) {
      int nearest = i;
      double minSeparation = Double.MAX_VALUE;
      //      System.out.println("Checking city " + i);
      for (int j = 0; j < elements.length; j++) {
        if (!inRoute[j]) {
	  elements[i] = startOrder[j];
	  double distance = separation(i-1,i);
	  //	  System.out.println("Checking against city " + j + " distance " + distance);
          if (distance < minSeparation) {
	    minSeparation = distance;
	    nearest = j;
	  }
	}
      }
      elements[i] = startOrder[nearest]; inRoute[nearest] = true;
      /*
      System.out.println("Added city " + nearest);
      routeLength = i;
      try {Thread.sleep(500); }
      catch (InterruptedException ex) {}
      */
    }
    double distance = evaluate();
    System.out.println("Route length " + distance);

    repaint();
  }
}

class Annealer {
  CombinationalProblemInstance cpi;
  Annealer(CombinationalProblemInstance cpi) {this.cpi = cpi;}

  void anneal(double temperature, double temperatureStepFactor, int goodLimit, int badLimit) {
    int good, bad, step = 0;
    double currentCost = cpi.evaluate();
    Random random = new Random();
    InstrumentationWindow iw = new InstrumentationWindow(4, 1500, 300);
    iw.name(0, "Temp"); iw.colour(0, Color.BLUE); iw.maxValue(0, temperature);
    iw.name(1, "Cost"); iw.colour(1, Color.BLACK);  iw.maxValue(1, cpi.evaluate() * 1.3);
    iw.name(2, "Good"); iw.colour(2, Color.GREEN);  iw.maxValue(2, goodLimit);
    iw.name(3, "Bad"); iw.colour(3, Color.RED);  iw.maxValue(3, badLimit);
  
    long startTime = System.currentTimeMillis();

    do {
      good = bad = 0;
      do {
        double newCost, delta;
        int left = random.nextInt(cpi.size()), right = random.nextInt(cpi.size());

        cpi.move(left, right);
        newCost = cpi.evaluate();
        delta = newCost - currentCost;

        if (delta < 0 || random.nextDouble() < Math.exp(-delta / temperature)) {
          good++;                 /* accept */
          currentCost = newCost;
        }
        else {
          bad++;
	  cpi.move(left, right);
        }
      } while (good < goodLimit && bad < badLimit);

      System.out.printf("%d, %.3f, %.3f, %d, %d%n", step++, temperature, currentCost, good, bad);
      iw.point(0, temperature); iw.point(1, currentCost); iw.point(2, good); iw.point(3, bad); iw.advance();
      cpi.repaint();
      iw.repaint();
      temperature *= temperatureStepFactor;
    } while (temperature > 0.01 && good >0);
    long elapsedTimeMillis = System.currentTimeMillis()-startTime;
    float elapsedTimeSec = elapsedTimeMillis/1000;
    System.out.printf("%.3f seconds elapsed%n", elapsedTimeSec);
  }
}

class SimulatedAnnealing {
  public static void main(String[] args) throws IOException, FileNotFoundException {
      if (args.length == 1 && (args[0].equals("?") || args[0].equals("-?"))) {
      System.out.printf("Usage: -cities cityCount xExtent yExtent%n" +
                        "       -river riverX riverCost%n" +
                        "       -SA initialTemperature temperatureReduction goodLimit badLimit%n" +
                        "       -curve displayX displayY points%n" +
                        "       -full%n" +
                        "       -nearest%n");
      System.exit(1);
    }

    int cityCount = 100;
    int xExtent = 1000;
    int yExtent = 400;
    int riverX = -10;
    int riverCost = 0;
    double initialTemperature = 10000;
    double temperatureReduction = 0.9;
    int goodLimit = 100;
    int badLimit = 300;
    int curveX = 100;
    int curveY = 100;
    int curvePoints = 1000;
    boolean SA = true, full = false, nearest = false;

    for (int i = 0; i < args.length; ) {
      System.out.println("Processing argument " + i + " of " + args.length);
      if (args[i].equals("-cities")) {
        if (i+3 > args.length)
	  i = args.length;
        else {
	  i++;
          cityCount = Integer.parseInt(args[i++]);
          xExtent = Integer.parseInt(args[i++]);
          yExtent = Integer.parseInt(args[i++]);
	}
      }
      else if (args[i].equals("-river")) {
        if (i+2 > args.length)
	  i = args.length;
        else {
	  i++;
          riverX = Integer.parseInt(args[i++]);
          riverCost = Integer.parseInt(args[i++]);
	}
      }
      else if (args[i].equals("-SA")) {
	SA = true; full = false; nearest = false;
        if (i+4 > args.length)
	  i = args.length;
        else {
	  i++;
          initialTemperature = Double.parseDouble(args[i++]);
          temperatureReduction = Double.parseDouble(args[i++]);
          goodLimit = Integer.parseInt(args[i++]);
          badLimit = Integer.parseInt(args[i++]);
        }
      }
      else if (args[i].equals("-curve")) {
        if (i+3 > args.length)
	  i = args.length;
        else {
	  i++;
          curveX = Integer.parseInt(args[i++]);
          curveY = Integer.parseInt(args[i++]);
          curvePoints = Integer.parseInt(args[i++]);
        }
      }
      else if (args[i].equals("-full")) {
	SA = false; full = true; nearest = false;
        i++;
      }
      else if (args[i].equals("-nearest")) {
	SA = false; full = false; nearest = true;
        i++;
      }
    }

    CombinationalProblemInstance route = new CombinationalProblemInstance(cityCount, xExtent, yExtent, riverX, riverCost);
    Annealer travellingSalesThing = new Annealer(route);
    
    if (full)
      route.exhaustive();

    if (SA)
      travellingSalesThing.anneal(initialTemperature, temperatureReduction, goodLimit, badLimit);

    if (nearest)
      route.nearest();
  }
}
