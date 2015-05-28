import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Arrays;
import java.awt.*; 
import java.util.ArrayList;

import javax.swing.*;

public class TSP {

    /**
     * How many cities to use.
     */
    protected static int cityCount;

    /**
     * How many chromosomes to use.
     */
    protected static int populationSize;

    /**
     * The part of the population eligable for mating.
     */
    protected static int matingPopulationSize;

    /**
     * The part of the population selected for mating.
     */
    protected static int selectedParents;

    /**
     * The current generation
     */
    protected static int generation;

    /**
     * The list of cities.
     */
    protected static City[] cities;

    /**
     * The list of chromosomes.
     */
    protected static Chromosome[] chromosomes;

    /**
    * Frame to display cities and paths
    */
    private static JFrame frame;

    /**
     * Integers used for statistical data
     */
    private static double min;
    private static double avg;
    private static double max;
    private static double sum;

    /**
     * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
     */
    private static int width = 600;
    private static int height = 600;


    private static Panel statsArea;
    private static TextArea statsText;


    /*
     * Writing to an output file with the costs.
     */
    private static void writeLog(String content) {
        String filename = "results.out";
        FileWriter out;

        try {
            out = new FileWriter(filename, true);
            out.write(content + "\n");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     *  Deals with printing same content to System.out and GUI
     */
    private static void print(boolean guiEnabled, String content) {
        if(guiEnabled) {
            statsText.append(content + "\n");
        }

        System.out.println(content);
    }

    public static void evolve() {
        //Write evolution code here.
        matingPopulationSize = populationSize/5*4;
        selectedParents = populationSize/2;
        Chromosome.sortChromosomes(chromosomes, populationSize);
        Chromosome[] temp = new Chromosome[selectedParents];
        //Choose random number to decide selection method
        double randomNum = Math.random();
                
        //Selection        
        //????Linear scaling? f(z) = az + b (a,b = max,min fitness)
        	//Roulette Wheel: Fitter genotypes get larger slices of the wheel than less fit ones
            //populationSize/5
            //1= 40%, 2 = 25%, 3 = 20%, 4 = 10%, 5 = 5%
            //1/5 = 0-40, 2/5 = 41-65, 3/5 = 66-85, 4/5 = 86-95, 5/5 = 96-100	
        //Tournament: Subsets of genotypes randomly selected, fittest genotypes selected
        //2<K<populationSize tournament genotypes randomly selected
        //Deterministic vs Probabilistic
        //D: Fittest individual selected and removed from tournament set
        //P: Fittest individual selected and removed with probability p.
        //Repeated for as many genotypes must be recombined  
        if (randomNum<0.1)	
        {
        //Elitist: Choose Chromosones with min cost            
            for (int i=0; i<selectedParents; i++)
            {
    			temp[i] = chromosomes[i];
    		}
        }
        else
        {
        //Rank: Genotypes ranked by fitness. Selection probablity calculated based on rank
            double p = 0;
            int count = 0;            
            ArrayList<Integer> chosen = new ArrayList<Integer>();
            //Select ith genotype with probability p[i] - linear selection
            double selectionPressure = 1.5; //for medium selection pressure (1<sP<2)
            double worstGenotype = 2 - selectionPressure;
            //p[i] = (1/populationsize)*((2-selectionPressure)+(selectionPressure - (2 - selectionPressure))*((i-1)/(populationsize-1)))
            while (count<selectedParents) //REPEAT UNTIL count-1 == selectedParents
            {
                for (int i=0; i<matingPopulationSize; i++)
                {
                    if (!(chosen.contains(i)))
                    {
                        //Linear Ranking
                        p = (1/populationSize)*(worstGenotype + ((selectionPressure - worstGenotype)*((populationSize - i - 1)/(populationSize-1))));
                        randomNum = Math.random();
                        if ((randomNum < p)&&(count < selectedParents))
                        {
                            temp[count] = chromosomes[i];
                            //remove chromosome from chromosomes
                            chosen.add(i);
                            count++;
                        }
                    }                   
                }
            }
        }       
       
        //Recombination **method call to Chromosone.java  
        
        
        //Mutation **method call to Chromosone.java **with Probability

        //Replacement *Survival Selection
        //Elitist?
    }

    /**
     * Update the display
     */
    public static void updateGUI() {
        Image img = frame.createImage(width, height);
        Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (true && (cities != null)) {
            g.setColor(Color.green);
            for (int i = 0; i < cityCount; i++) {
                int xpos = cities[i].getx();
                int ypos = cities[i].gety();
                g.fillOval(xpos - 5, ypos - 5, 10, 10);
            }

            g.setColor(Color.gray);
            for (int i = 0; i < cityCount; i++) {
                int icity = chromosomes[0].getCity(i);
                if (i != 0) {
                    int last = chromosomes[0].getCity(i - 1);
                    g.drawLine(
                        cities[icity].getx(),
                        cities[icity].gety(),
                        cities[last].getx(),
                        cities[last].gety());
                }
            }
        }
        frame.getGraphics().drawImage(img, 0, 0, frame);
    }


    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String currentTime  = df.format(today);

        int runs;
        boolean display = false;
        String formatMessage = "Usage: java TSP 100 500 1 [gui] \n java TSP [NumCities] [PopSize] [Runs] [gui]";

        if (args.length < 3) {
            System.out.println("Please enter the arguments");
            System.out.println(formatMessage);
            display = false;
        } else {

            if (args.length > 3) {
                display = true; 
            }

            try {
                cityCount = Integer.parseInt(args[0]);
                populationSize = Integer.parseInt(args[1]);
                runs = Integer.parseInt(args[2]);

                if(display) {
                    frame = new JFrame("Traveling Salesman");
                    statsArea = new Panel();

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setSize(width + 300, height);
                    frame.setResizable(false);
                    frame.setLayout(new BorderLayout());
                    
                    statsText = new TextArea(35, 35);
                    statsText.setEditable(false);

                    statsArea.add(statsText);
                    frame.add(statsArea, BorderLayout.EAST);
                    
                    frame.setVisible(true);
                }


                min = 0;
                avg = 0;
                max = 0;
                sum = 0;

                // create a random list of cities
                // Note: This is outside the run loop so that the multiple runs
                // are tested on the same city set
                cities = new City[cityCount];
                for (int i = 0; i < cityCount; i++) {
                    cities[i] = new City(
                        (int) (Math.random() * (width - 10) + 5),
                        (int) (Math.random() * (height - 50) + 30));
                }

                writeLog("Run Stats for experiment at: " + currentTime);
                for (int y = 1; y <= runs; y++) {
                    print(display,  "Run " + y + "\n");

                // create the initial population of chromosomes
                    chromosomes = new Chromosome[populationSize];
                    for (int x = 0; x < populationSize; x++) {
                        chromosomes[x] = new Chromosome(cities);
                    }

                    generation = 0;
                    double thisCost = 0.0;

                    while (generation < 100) {
                        evolve();
                        generation++;

                        Chromosome.sortChromosomes(chromosomes, populationSize);
                        double cost = chromosomes[0].getCost();
                        thisCost = cost;

                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);

                        print(display, "Gen: " + generation + " Cost: " + (int) thisCost);

                        if(display) {
                            updateGUI();
                        }
                    }

                    writeLog(thisCost + "");

                    if (thisCost > max) {
                        max = thisCost;
                    }

                    if (thisCost < min || min == 0) {
                        min = thisCost;
                    }

                    sum +=  thisCost;

                    print(display, "");
                }

                avg = sum / runs;
                print(display, "Statistics after " + runs + " runs");
                print(display, "Solution found after " + generation + " generations." + "\n");
                print(display, "MIN: " + min + " AVG: " + avg + " MAX: " + max + "\n");

            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }
}