package mapthatset.g6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser3 extends Guesser {
	Map<Integer, List<Integer>> possibilities;
	Map<List<Integer>, List<Integer>> rules;

	int mapLength;

	ArrayList<Integer> currentGuess;
	ArrayList<Integer> randIndex;
	ArrayList<ArrayList<Integer>> leftTodo;
	ArrayList<Integer> leftToExplore;
	String strID = "Mastermind";

	/**
	 * Initializes a new mapping.
	 */
	@Override
	public void startNewMapping(int mapLength) {
		this.mapLength = mapLength;
		this.randIndex = getRandomIndex(mapLength);

		leftToExplore = new ArrayList<Integer>();
		for (int i = 1; i <= mapLength; i++)
			leftToExplore.add(i);

		possibilities = new HashMap<Integer, List<Integer>>();
		for (int i = 1; i <= mapLength; i++)
			possibilities.put(i, (ArrayList<Integer>) leftToExplore.clone());

		rules = new HashMap<List<Integer>, List<Integer>>();

		leftTodo = new ArrayList<ArrayList<Integer>>();

		leftTodo.add((ArrayList<Integer>) leftToExplore.clone());

		currentGuess = null;
	}

	/**
	 * Gives us our next action.
	 */
	@Override
	public GuesserAction nextAction() {
		String todo = "q";
		// System.out.println(possibilities);
		if (!solutionReached() && !leftTodo.isEmpty())
			while (wasSeen(currentGuess = leftTodo.remove(0)))
				;

		else {
			todo = "g";
			currentGuess = (ArrayList<Integer>) makeGuess();
		}

		System.out.println();
		// System.out.println(currentGuess);
		return new GuesserAction(todo, currentGuess);
	}

	public ArrayList<Integer> makeGuess() {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for (Integer i : possibilities.keySet())
			toReturn.add(0);
		for (Integer i : possibilities.keySet())
			toReturn.set(i - 1, possibilities.get(i).get(0));
		return toReturn;
	}

	public boolean solutionReached(Map<Integer, List<Integer>> possibilities) {
		boolean toReturn = true;
		for (Integer i : possibilities.keySet())
			toReturn &= possibilities.get(i).size() == 1;
		return toReturn;
	}

	public boolean wasSeen(ArrayList<Integer> options, int i, int j) {
		ArrayList<Integer> choice = new ArrayList<Integer>();
		choice.add(options.get(i));
		choice.add(options.get(j));
		return wasSeen(choice);
	}

	public boolean wasSeen(ArrayList<Integer> choice) {
		for (List<Integer> rule : rules.keySet())
			if (rule.containsAll(choice) && choice.containsAll(rule))
				return true;
		return false;
	}

	public boolean solutionReached() {
		return solutionReached(possibilities);
	}

	public String getID() {
		return strID;
	}

	public static ArrayList<Integer> getRandomIndex(int n) {
		ArrayList<Integer> start = new ArrayList<Integer>();
		ArrayList<Integer> end = new ArrayList<Integer>();
		Random r = new Random();
		int i = 1;
		while (start.size() < n)
			start.add(i++);
		while (start.size() > 0)
			end.add(start.remove(r.nextInt(start.size())));
		return end;
	}

	public ArrayList<ArrayList<Integer>> makeSmallGuesses(
			ArrayList<Integer> options) {
		// randomly pick 2 elements from options
		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();
		int i = 0, j = 0;
		while (i < options.size()
				&& possibilities.get(options.get(i)).size() == 1)
			i++;
		while (j < options.size()
				&& (j <= i || possibilities.get(options.get(j)).size() == 1 || wasSeen(
						options, i, j)))
			j++;
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		if (i < options.size())
			tmp.add(options.get(i));
		if (j < options.size())
			tmp.add(options.get(j));
		if (tmp.size() > 0)
			toReturn.add(tmp);
		return toReturn;
	}

	@Override
	public void setResult(ArrayList<Integer> alResult) {
		rules.put((List<Integer>) currentGuess.clone(),
				(List<Integer>) alResult.clone());
		// System.out.println(possibilities);
		for (Integer i : currentGuess)
			possibilities.get(i).retainAll(alResult);
		for (Iterator<Integer> itr = leftToExplore.iterator(); itr.hasNext();)
			if (possibilities.get(itr.next()).size() == 1)
				itr.remove();
		limitPossibilities(rules, possibilities);

		if (currentGuess.size() == alResult.size() && currentGuess.size() > 2) {
			ArrayList<ArrayList<Integer>> perms = solvePermutation(currentGuess);
			if (leftToExplore.containsAll(currentGuess)) {
				leftTodo.addAll(perms);
				leftToExplore.removeAll(currentGuess);
			}
		} else {
			// figure out what to do.
			// if left in todo, ignore
			if (leftTodo.size() > 1)
				return;
			// else, try to come up with something clever...
			/*if (rules.keySet().size() < 2 && possibilities.keySet().size() > 10) {
				// try splitting!
				ArrayList<Integer> first = new ArrayList<Integer>();
				ArrayList<Integer> second = new ArrayList<Integer>();
				for (int i = 0; i < leftToExplore.size(); i++)
					if (i > leftToExplore.size() / 2)
						first.add(leftToExplore.get(i));
					else
						second.add(leftToExplore.get(i));
				leftTodo.add(first);
				leftTodo.add(second);
			} else {*/
				ArrayList<ArrayList<Integer>> guess = makeSmallGuesses(leftToExplore);
				leftTodo.addAll(guess);

			//}
		}
	}

	public void limitPossibilities(Map<List<Integer>, List<Integer>> mapping,
			Map<Integer, List<Integer>> poss) {
		for (List<Integer> r1 : mapping.keySet())
			for (List<Integer> r2 : mapping.keySet())
				if (!r1.equals(r2)) {
					List<Integer> g1 = new ArrayList<Integer>();
					List<Integer> g2 = new ArrayList<Integer>();
					g1.addAll(r1);
					g1.retainAll(r2);
					g2.addAll(mapping.get(r1));
					g2.retainAll(mapping.get(r2));

					if (g1.size() != 0
							&& r1.size() - g1.size() == mapping.get(r1).size()
									- g2.size())
						for (Integer choice : r1) {
							poss.get(choice).retainAll(mapping.get(r1));
							if (g1.contains(choice))
								poss.get(choice).retainAll(g2);
							else
								poss.get(choice).removeAll(g2);
						}
				}
	}

	/**
	 * Given a random permutation, returns the (log n) queries that will solve
	 * it.
	 * 
	 * @param list
	 *            The permutation
	 * @return The queries which will solve the permutation.
	 */
	public ArrayList<ArrayList<Integer>> solvePermutation(
			ArrayList<Integer> list) {
		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();

		int n = list.size();
		for (int k = 1; k <= n; k *= 2) {
			ArrayList<Integer> partitions = new ArrayList<Integer>();
			int j = 0;
			while (j < n) {
				for (int i = 0; i < k && j < n; i++)
					partitions.add(list.get(j++));
				j += k;
			}
			if (partitions.size() != n)
				toReturn.add((ArrayList<Integer>) partitions.clone());
		}
		return toReturn;
	}
}

class TestGuesser {
	MastermindGuesser2 guesser;

	public TestGuesser() {
		guesser = new MastermindGuesser2();
	}

	public void testSolutionReached() {
		int n = 10;
		Map<Integer, List<Integer>> tmp = new HashMap<Integer, List<Integer>>();
		for (int i = 1; i <= n; i++) {
			tmp.put(i, new ArrayList<Integer>());
			tmp.get(i).add(i);
		}
		assert guesser.solutionReached(tmp);
	}

	public void testLimitPossibilities1() {
		HashMap<List<Integer>, List<Integer>> mapping = new HashMap<List<Integer>, List<Integer>>();
		HashMap<Integer, List<Integer>> poss = new HashMap<Integer, List<Integer>>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for (int i = 1; i <= 5; i++) {
			tmp.add(i);
			if (i < 5) {
				ArrayList<Integer> t1 = new ArrayList<Integer>();
				ArrayList<Integer> t2 = new ArrayList<Integer>();
				t1.add(i);
				t1.add(i + 1);
				t2.addAll(t1);
				mapping.put(t1, t2);
			}
		}
		for (Integer i : tmp)
			poss.put(i, (List<Integer>) tmp.clone());
		guesser.limitPossibilities(mapping, poss);

		for (Integer i : poss.keySet()) {
			assert (poss.get(i).size() == 1);
			assert (i == poss.get(i).get(0));
		}
	}

	@SuppressWarnings("unchecked")
	public void testLimitPossibilities2() {
		HashMap<List<Integer>, List<Integer>> mapping = new HashMap<List<Integer>, List<Integer>>();
		HashMap<Integer, List<Integer>> poss = new HashMap<Integer, List<Integer>>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for (int i = 1; i <= 200; i++)
			tmp.add(i);
		for (Integer i : tmp)
			poss.put(i, (List<Integer>) tmp.clone());
		mapping.put((List<Integer>) tmp.clone(), (List<Integer>) tmp.clone());
		ArrayList<ArrayList<Integer>> perms = guesser.solvePermutation(tmp);
		// System.out.println(perms);
		for (ArrayList<Integer> perm : perms)
			mapping.put((List<Integer>) perm.clone(),
					(List<Integer>) perm.clone());
		guesser.limitPossibilities(mapping, poss);
		System.out.println(mapping.keySet().size() + " for " + tmp.size()
				+ " elements");
		for (Integer i : poss.keySet()) {
			assert (poss.get(i).size() == 1);
			assert (i == poss.get(i).get(0));
		}
	}

	public void testGetRandomIndex() {
		int n = 10;
		ArrayList<Integer> r = guesser.getRandomIndex(n);
		assert r.size() == n;
		for (int i = 1; i <= n; i++)
			assert r.contains(i);
	}

	public static void main(String args[]) {
		TestGuesser mmg2 = new TestGuesser();
		mmg2.testSolutionReached();
		mmg2.testGetRandomIndex();
		mmg2.testLimitPossibilities1();
		mmg2.testLimitPossibilities2();
	}

}