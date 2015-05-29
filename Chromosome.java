import java.util.ArrayList;
import java.util.Random;

class Chromosome {

    /**
     * The list of cities, which are the genes of this chromosome.
     */
    protected int[] cityList;

    /**
     * The cost of following the cityList order of this chromosome.
     */
    protected double cost;

    protected double fitness;
    double getFitness()
    {
        return fitness;
    }
    void setFitness(double value)
    {
        fitness = value;
    }

    /**
     * @param cities The order that this chromosome would visit the cities.
     */
    Chromosome(City[] cities) {
        Random generator = new Random();
        cityList = new int[cities.length];
        //cities are visited based on the order of an integer representation [o,n] of each of the n cities.
        for (int x = 0; x < cities.length; x++) {
            cityList[x] = x;

        }

        //shuffle the order so we have a random initial order
        for (int y = 0; y < cityList.length; y++) {
            int temp = cityList[y];
            int randomNum = generator.nextInt(cityList.length);
            cityList[y] = cityList[randomNum];
            cityList[randomNum] = temp;
        }

        calculateCost(cities);
    }

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    void calculateCost(City[] cities) {
        cost = 0;
        for (int i = 0; i < cityList.length - 1; i++) {
            double dist = cities[cityList[i]].proximity(cities[cityList[i + 1]]);
            cost += dist;
        }
    }

    /**
     * Get the cost for this chromosome. This is the amount of distance that
     * must be traveled.
     */
    double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    int getCity(int i) {
        return cityList[i];
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    void setCities(int[] list) {
        for (int i = 0; i < cityList.length; i++) {
            cityList[i] = list[i];
        }
    }

    /**
     * Set the index'th city in the city list.
     *
     * @param index The city index to change
     * @param value The city number to place into the index.
     */
    void setCity(int index, int value) {
        cityList[index] = value;
    }

    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    public static void sortChromosomes(Chromosome chromosomes[], int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < num - 1; i++) {
                if (chromosomes[i].getCost() > chromosomes[i + 1].getCost()) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i + 1];
                    chromosomes[i + 1] = ctemp;
                    swapped = true;
                }
            }
        }
    }

    public static Chromosome[] uniformOrderBasedCrossover(Chromosome parent1, Chromosome parent2, City[] cities)
    {
        double probability = 0.5;
        double randomNum;
        ArrayList<Integer> notSwappedIndices = new ArrayList<Integer>();
        ArrayList<Integer> child1Values = new ArrayList<Integer>();
        ArrayList<Integer> child2Values = new ArrayList<Integer>();
       
        Chromosome[] children = new Chromosome[2];
            
        children[0] = new Chromosome(cities);
        children[1] = new Chromosome(cities);
            
            
        for (int j=0; j<parent1.cityList.length; j++)
        {
            randomNum = Math.random();
            if (randomNum < probability)
            {
                children[0].setCity(j, parent1.getCity(j));
                child1Values.add(parent1.getCity(j));
                
                children[1].setCity(j, parent2.getCity(j));
                child2Values.add(parent2.getCity(j));
               
            }
            else
            {
                notSwappedIndices.add(j);
            }
        } 
        int index1 = 0;
        int index2 = 0;
        for (int j=0; j<notSwappedIndices.size(); j++)
        {
            while (child1Values.contains(parent2.getCity(index1)))
            {
                index1++;
            }
            
            children[0].setCity(notSwappedIndices.get(j), parent2.getCity(index1));
            child1Values.add(parent2.getCity(index1));

            while (child2Values.contains(parent1.getCity(index2)))
            {
                index2++;
            }
            children[1].setCity(notSwappedIndices.get(j), parent1.getCity(index2));
            child2Values.add(parent1.getCity(index2));
        
        }   
        return children;
    }

    public void mutate()
    {
        double randomNum = Math.random();

        //Inversion
        if (randomNum<0.2)
            inversion();

        randomNum = Math.random();

        //Translocation (insertion)
        if (randomNum<0.25)
            translocation();

        randomNum = Math.random();

        //Transposition (2-exchange)
        if (randomNum<0.4)
            transposition();

        randomNum = Math.random();
        //3-point exchange (shifting)
        if (randomNum<0.25)
            shifting();
        
    }

    public void shifting()
    {
        // Shift randomly chosen segment (between two points) to a third point.
        Random generator = new Random();
        int randomLength = generator.nextInt(cityList.length/10);
        int randomNum = generator.nextInt(cityList.length);
        int randomNum2 = generator.nextInt(cityList.length);
        int min = Math.min(randomNum,randomNum2);
        int max = Math.max(randomNum,randomNum2);
        int temp = 0;
        while (max-min<2)
        {
            max = generator.nextInt(cityList.length);
            temp = Math.min(min,max);
            max = Math.max(min,max);
            min = temp;
        }
        if (min+randomLength>=max)
        {
            randomLength = max-min-1;
        }
        Integer[] tour = new Integer[randomLength];
        int index = 0;
        int index2 = 0;
        for (int i=min; i<max; i++)
        {
            if (index < randomLength)
            {
                tour[index] = getCity(i);
                index++;
            }
            if (randomLength + i < max)
            {
                setCity(i, getCity(randomLength+i));
            }
            else
            {
                setCity(i, tour[index2]);
                index2++;
            }
        }
    }

    public void transposition()
    {
        //Exchange two randomly chosen points
        Random generator = new Random();
        int randomNum = generator.nextInt(cityList.length);
        int randomNum2 = generator.nextInt(cityList.length);
        int tempCity = getCity(randomNum);
        setCity(randomNum, getCity(randomNum2));
        setCity(randomNum2, tempCity);
    }

    public void translocation()
    {
        //Select random position and insert into new random position
        Random generator = new Random();
        int randomNum = generator.nextInt(cityList.length);
        int randomNum2 = generator.nextInt(cityList.length);
        int min = Math.min(randomNum,randomNum2);
        int max = Math.max(randomNum,randomNum2);
        int tempCity = getCity(min);
        for (int i=min; i<max; i++)
        {
            setCity(i, getCity(i+1));
        }
        setCity(max, tempCity);
    }

    public void inversion()
    {
        //cut out random segment, and re-insert in opposite direction
        Random generator = new Random();
        int randomNum = generator.nextInt(cityList.length);
        int randomNum2 = generator.nextInt(cityList.length);
        int min = Math.min(randomNum,randomNum2);
        int max = Math.max(randomNum,randomNum2);
        if (max-min>cityList.length/50)
        {
            max=min+cityList.length/50;
        }
        Integer[] tour = new Integer[max-min];
        int index = 0;
        for (int i=min; i<max; i++)
        {
            tour[index] = getCity(i);
            index++;
        }
        index--;
        for (int i=min; i<max; i++)
        {
            setCity(i, tour[index]);
            index--;
        }
    }

}
