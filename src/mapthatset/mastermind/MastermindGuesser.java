package mapthatset.mastermind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
			currentGuess = (ArrayList<Integer>) createGuess();
		} else {
			todo = "g";
			currentGuess = (ArrayList<Integer>) finalGuess();
		}
		System.out.println("\n" + todo + ":" + currentGuess);
		return new GuesserAction(todo, currentGuess);
	}

	public String getID() {
		return strID;
	}

	// TODO(najaf)
	/**
	 * Creates a guess (for input into the mapper) Output of mapper will be sent
	 * to setResult.
	 */
	public List<Integer> createGuess() {
		// stupid guesser for testing
		// ArrayList<Integer> guess = new ArrayList<Integer>();
		// guess.add(currInd % mapLength);
		// guess.add(currInd % mapLength + 1);
		// return guess;
		return null;
	}

	// TODO(riddhi)
	/**
	 * Update the rules for this game based on possibilities
	 */
	public void updateRules() {
		// using possibilities
		// update rules where possibilities are only 1
		return;
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
		return false;
		
		// for (Integer i : possibilities.keySet()) {
		// if(possibilities.get(i).size() != 1)
		// return false;
		// }
		// return true;
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
