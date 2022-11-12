package src;/*
 * Created on Feb 9, 2005
 *
 */

import aima.core.logic.propositional.algorithms.Model;
import aima.core.logic.propositional.parsing.PEParser;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.parsing.ast.Symbol;
import aima.core.logic.propositional.visitors.CNFClauseGatherer;
import aima.core.logic.propositional.visitors.CNFTransformer;
import aima.core.logic.propositional.visitors.SymbolCollector;
import aima.core.util.Converter;
import aima.core.util.Util;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author Ravi Mohan
 *
 */
public class WalkSAT {
    private Model myModel;
    // long startTime = System.nanoTime();


    private Random random = new Random();

    public Model findModelFor(String logicalSentence, int numberOfFlips, double probabilityOfRandomWalk) {
        myModel = new Model();
        Sentence s = (Sentence) new PEParser().parse(logicalSentence);
        CNFTransformer transformer = new CNFTransformer();
        CNFClauseGatherer clauseGatherer = new CNFClauseGatherer();
        SymbolCollector sc = new SymbolCollector();

        List symbols = new Converter<Symbol>().setToList(sc.getSymbolsIn(s));
        Random r = new Random();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol sym = (Symbol) symbols.get(i);
            myModel = myModel.extend(sym, Util.randomBoolean());
        }
        List<Sentence> clauses
                = new Converter<Sentence>().setToList(clauseGatherer.getClausesFrom(transformer.transform(s)));

        for (int i = 0; i < numberOfFlips; i++) {
            if (getNumberOfClausesSatisfiedIn(new Converter<Sentence>().listToSet(clauses),
                    myModel) == clauses.size()) {
                return myModel;
            }
            Sentence clause = (Sentence) clauses.get(random.nextInt(clauses
                    .size()));

            List<Symbol> symbolsInClause = new Converter<Symbol>()
                    .setToList(sc.getSymbolsIn(clause));
            if (random.nextDouble() >= probabilityOfRandomWalk) {
                Symbol randomSymbol = (Symbol) symbolsInClause.get(random
                        .nextInt(symbolsInClause.size()));
                myModel = myModel.flip(randomSymbol);
            } else {
                Symbol symbolToFlip = getSymbolWhoseFlipMaximisesSatisfiedClauses(
                        new Converter<Sentence>().listToSet(clauses), symbolsInClause, myModel);
                myModel = myModel.flip(symbolToFlip);
            }

        }
        return null;
    }

    private Symbol getSymbolWhoseFlipMaximisesSatisfiedClauses(Set<Sentence> clauses,
                                                               List<Symbol> symbols, Model model) {
        if (symbols.size() > 0) {
            Symbol retVal = (Symbol) symbols.get(0);
            int maxClausesSatisfied = 0;
            for (int i = 0; i < symbols.size(); i++) {
                Symbol sym = (Symbol) symbols.get(i);
                if (getNumberOfClausesSatisfiedIn(clauses, model.flip(sym)) > maxClausesSatisfied) {
                    retVal = sym;
                    maxClausesSatisfied = getNumberOfClausesSatisfiedIn(
                            clauses, model.flip(sym));
                }
            }
            return retVal;
        } else {
            return null;
        }

    }

    private List<Symbol> extractSymbols(Sentence sentence) {
        SymbolCollector sc = new SymbolCollector();
        return new Converter<Symbol>().setToList(sc.getSymbolsIn(sentence));
    }

    private int getNumberOfClausesSatisfiedIn(Set clauses, Model model) {
        int retVal = 0;
        Iterator i = clauses.iterator();
        while (i.hasNext()) {
            Sentence s = (Sentence) i.next();
            if (model.isTrue(s)) {
                retVal += 1;
            }
        }
        return retVal;
    }
}
