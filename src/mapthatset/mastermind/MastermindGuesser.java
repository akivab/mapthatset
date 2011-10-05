package mapthatset.mastermind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser extends Guesser {
	Map<Integer, List<Integer>> possibilities;
	Map<List<Integer>, List<Integer>> rules;

	int mapLength;
	// int currInd = 1;
	ArrayList<Integer> currentGuess;
	String strID = "Mastermind";

	/**
	 * Initializes a new mapping.
	 */
	@Override
	public void startNewMapping(int mapLength) {
		this.mapLength = mapLength;
		possibilities = new HashMap<Integer, List<Integer>>();
		for (int i = 1; i <= mapLength; i++)
			possibilities.put(i, new ArrayList<Integer>());
		rules = new HashMap<List<Integer>, List<Integer>>();
		currentGuess = null;
	}

	/**
	 * Gives us our next action.
	 */
	@Override
	public GuesserAction nextAction() {
		String todo = "q";

		if (!solutionReached()) {
			currentGuess = (ArrayList<Integer>) createGuessFromThoseWithManyPossibilities();
		} else {
			todo = "g";
			currentGuess = (ArrayList<Integer>) finalGuess();
		}
//		System.out.println();
//		System.out.println(todo + ":" + currentGuess);
		return new GuesserAction(todo, currentGuess);
	}

	public ArrayList<Integer> createGuessFromThoseWithManyPossibilities() {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for (Integer i : possibilities.keySet()) {
			int size = possibilities.get(i).size();
			if (size == 0 || size > 1) // nothing seen, might want to add!
				toReturn.add(i);
		}
		while (toReturn.size() > 1 && rules.get(toReturn) != null)
			toReturn.remove((int) (toReturn.size() * Math.random()));
		return toReturn;
	}

	public String getID() {
		return strID;
	}

	// TODO(najaf)
	/**
	 * Creates a guess (for input into the mapper) Output of mapper will be sent
	 * to setResult.
	 */
	public ArrayList<Integer> createGuess() {

		ArrayList<Integer> randomList = new ArrayList<Integer>();
		randomList = returnRandomNumbers(mapLength, 1);

		int numOfDomainElements = randomList.get(0);
		return returnRandomNumbers(mapLength, numOfDomainElements);

	}

	/*
	 * From range 1 to r generate n distinct random numbers
	 */
	public static ArrayList<Integer> returnRandomNumbers(int r, int n) {
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		int i = 0;
		Integer randomInt = 0;
		while (i < n) {
			Random randomGenerator = new Random();
			randomInt = randomGenerator.nextInt(r);
			randomInt++;
			if (!randomList.contains(randomInt)) {
				i++;
				randomList.add(randomInt);
			}
		}
		return randomList;
	}

	// TODO(riddhi)
	/**
	 * Update the rules for this game based on possibilities
	 */
	public void updateRules() {
		for (Integer i : possibilities.keySet())
			if (possibilities.get(i).size() == 1)
				for (List<Integer> guess : rules.keySet())
					if (guess.contains(i) && rules.get(guess) != null
							&& guess.size() > 1) {
						if (guess.size() == rules.get(guess).size())
							rules.get(guess)
									.remove(possibilities.get(i).get(0));
						guess.remove(i);
					}
	}

	// TODO(akivab)
	/**
	 * Updates the table of possibilities. If we got [1,2,3] -> [3,4], for
	 * example, we would update 1,2, and 3 in the possibility table to only have
	 * 3 and 4 as possibilities.
	 * 
	 * @param guess
	 *            what we're mapping from
	 * @param result
	 *            what we're mapping to
	 */
	public void updatePossibilities(List<Integer> guess, List<Integer> result) {
		for (Integer key : guess) {
			List<Integer> poss = possibilities.get(key);
			if (poss.size() == 0)
				poss.addAll(result);
			else
				for (Iterator<Integer> itr = poss.iterator(); itr.hasNext();)
					if (!result.contains(itr.next()))
						itr.remove();
		}
	}

	// TODO(akivab)
	/**
	 * Returns the final guess (if a solution has been reached)
	 */
	public List<Integer> finalGuess() {
		List<Integer> toReturn = new ArrayList<Integer>();
		for (int i = 1; i <= mapLength; i++) {
			toReturn.add(possibilities.get(i).get(0));
		}
		return toReturn;
	}

	// TODO(hans)
	/**
	 * Returns true if the final solution has been reached
	 */
	public boolean solutionReached() {
		for (Integer i : possibilities.keySet()) {
			if (possibilities.get(i).size() != 1)
				return false;
		}
		return true;
	}

	/**
	 * Upon response from the game, we update possibilities based on our
	 * currentGuess and then update the rules appropriately
	 */
	@Override
	public void setResult(ArrayList<Integer> alResult) {
		rules.put(currentGuess, alResult);
		updatePossibilities(currentGuess, alResult);
		updateRules();
	}
}
