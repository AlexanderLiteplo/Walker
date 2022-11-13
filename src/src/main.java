package src;

import aima.core.logic.propositional.kb.data.Clause;
import aima.core.logic.propositional.kb.data.Literal;
import aima.core.logic.propositional.kb.data.Model;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.*;

public class main {

    public static String[] literalsString = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                                                         "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};

    public static void main(String[] args) {
        double probabilityOfRandomWalk = 0.5;
        long duration = 10000;
        WalkSAT walk = new WalkSAT();
        ArrayList<ArrayList<Literal>> allPossibleLiterals = initializeAllPossibleLiterals();
        double[] solved = new double[10];
        double[] medianFlipsWhenSolved = new double[10];
        double[] clausesPerVariables = new double[10];
        int iteration = 0;
        for (int numberOfClauses = 20; numberOfClauses <= 200; numberOfClauses += 20) {
            int numOf3SATProblemsSolved = 0;
            ArrayList<Integer> flipsWhenSolved = new ArrayList<Integer>();
            for (int i = 0; i < 50; i++) {
                Set<Clause> random3SATproblem = generateRandom3SATProblem(numberOfClauses, allPossibleLiterals);
                Model result = walk.walkSAT(random3SATproblem, probabilityOfRandomWalk, duration);
                if (result != null) {
                    numOf3SATProblemsSolved++;
                    flipsWhenSolved.add(walk.numOfFlips);
                }
            }
            clausesPerVariables[iteration] = numberOfClauses / 20;
            solved[iteration] =  numOf3SATProblemsSolved;
            int medianFlips = findMedian(flipsWhenSolved);
            medianFlipsWhenSolved[iteration] = medianFlips;
            iteration++;
        }
        System.out.println(Arrays.toString(solved));
        System.out.println(Arrays.toString(medianFlipsWhenSolved));
        XYSeries solvedSeries = new XYSeries("Solved");
        XYSeries medianFlipsSeries = new XYSeries("Median Flips");
        for (int i = 0; i < 10; i++) {
            solvedSeries.add(clausesPerVariables[i], solved[i]);
            if (medianFlipsWhenSolved[i] != -1) {
                medianFlipsSeries.add(clausesPerVariables[i], medianFlipsWhenSolved[i]);
            }
        }
        XYSeriesCollection solvedDataset = new XYSeriesCollection();
        XYSeriesCollection medianFlipsDataset = new XYSeriesCollection();
        solvedDataset.addSeries(solvedSeries);
        medianFlipsDataset.addSeries(medianFlipsSeries);
        JFreeChart solvedChart = ChartFactory.createXYLineChart("Number of Random 3SAT Problems Solved",
                "C/N", "Random 3SAT problems solved", solvedDataset, PlotOrientation.VERTICAL,
                true, true, false);
        JFreeChart medianFlipsChart = ChartFactory.createXYLineChart("Median Flips when solved", "C/N",
                "Median number of flips when solved", medianFlipsDataset, PlotOrientation.VERTICAL,
                true, true, false);
        try {
            ChartUtilities.saveChartAsJPEG(new File("./solvedGraph.JPEG"), solvedChart, 500, 300);
            ChartUtilities.saveChartAsJPEG(new File("./medianFlipsGraph.JPEG"), medianFlipsChart, 500, 300);
        } catch (Exception e) {
            System.err.println("Error in Chart generation " + e.getMessage());
        }
    }

    private static ArrayList<ArrayList<Literal>> initializeAllPossibleLiterals() {
        ArrayList<ArrayList<Literal>> result = new ArrayList<ArrayList<Literal>>();
        for (int i = 0; i < 20; i++) {
            PropositionSymbol symbol = new PropositionSymbol(literalsString[i]);
            Literal literalTrue = new Literal(symbol, true);
            Literal literalFalse = new Literal(symbol, false);
            ArrayList<Literal> literals = new ArrayList<Literal>();
            literals.add(literalTrue);
            literals.add(literalFalse);
            result.add(literals);
        }
        return result;
    }

    private static Set<Clause> generateRandom3SATProblem(int numberOfClauses,
                                                         ArrayList<ArrayList<Literal>> allPossibleLiterals) {
        Set<Clause> result = new LinkedHashSet<>();
        while (result.size() < numberOfClauses) {
            Clause random3SATClause = generateRandom3SATClause(allPossibleLiterals);
            result.add(random3SATClause);
        }
        return result;
    }

    private static Clause generateRandom3SATClause(ArrayList<ArrayList<Literal>> allPossibleLiterals) {
        ArrayList<Literal> randomLiterals = new ArrayList<>(3);
        ArrayList<Integer> randomIndexes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(20);
            while (true) {
                if (!randomIndexes.contains(randomIndex)) {
                    randomIndexes.add(randomIndex);
                    break;
                }
                randomIndex = random.nextInt(20);
            }
            int randomBoolIndex = random.nextInt(2);
            Literal literal1 = allPossibleLiterals.get(randomIndex).get(randomBoolIndex);
            randomLiterals.add(literal1);
        }
        Clause result = new Clause(randomLiterals.get(0), randomLiterals.get(1), randomLiterals.get(2));
        return result;
    }

    private static int findMedian(ArrayList<Integer> flips) {
        if (flips.size() == 0) {
            return -1;
        }
        Collections.sort(flips);
        int index = flips.size() / 2;
        return flips.get(index);
    }



}
