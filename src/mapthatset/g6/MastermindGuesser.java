package mapthatset.g6;

import java.util.ArrayList;
import java.util.Collections;
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
	ArrayList<Integer> currentGuess;
	String strID = "Mastermind";

	/**
	 * Initializes a new mapping.
	 */
	@Override
	public void startNewMapping(int mapLength) {
		this.mapLength = mapLength;
		possibilities = new HashMap<Integer, List<Integer>>();
		for (int i = 1; i <= mapLength; i++) {
			possibilities.put(i, new ArrayList<Integer>());
		}
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
		System.out.println("\n********\n" + todo + ":" + currentGuess+"\n*******\n");
		return new GuesserAction(todo, currentGuess);
	}

	public ArrayList<Integer> createGuessFromThoseWithManyPossibilities() {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for (Integer i : possibilities.keySet()) {
			int count = 0;
			for (List<Integer> j : rules.keySet())
				if( j.contains(i))
					count++;
					int size = possibilities.get(i).size();
					if ((size == 0 || size > 1 || count < 2))// nothing seen, might want to add!
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

	/**
	 *   updateRules updates rules recursively
	 *         By creating a new rule considering intersection with previous rules
	 *          
	 *  
	 *  @param domainSet
	 *                   query of the rule
	 *  @param  rangeSet                 
	 *                    result of the query
	 */
	public void updateRules (ArrayList<Integer> domainSet,ArrayList<Integer> rangeSet) {
		Collections.sort(domainSet);
		Collections.sort(rangeSet);
		if(rules.size()==0) {
			if(domainSet.size()>0 && rangeSet.size()>0) {
				if(! ( rules.containsKey(domainSet) && rules.get(domainSet).equals(rangeSet) ) ) {
					rules.put(domainSet, rangeSet);
					updatePossibilities(domainSet, rangeSet);
				}
			}
		} else {
			if(! ( rules.containsKey(domainSet) && rules.get(domainSet).equals(rangeSet) ) ) {
				if(domainSet.size()>0 && rangeSet.size()>0) {
					rules.put(domainSet, rangeSet);
					updatePossibilities(domainSet, rangeSet);
				}
			}
			// currently only adding rule, though in some cases some rules can be removed, whose partitioning is exhibited 
			// by the already present rules.
			Map<List<Integer>, List<Integer>> rulesToBeAdded=new HashMap<List<Integer>,List<Integer>>();
			for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
				ArrayList<Integer> domainList = (ArrayList<Integer>)singleRule.getKey();
				ArrayList<Integer> rangeList = (ArrayList<Integer>)singleRule.getValue();

				ArrayList<Integer> domainIntersection =new ArrayList<Integer>();
				domainIntersection.addAll(domainSet);  domainIntersection.retainAll(domainList);
				ArrayList<Integer> rangeIntersection =new ArrayList<Integer>();
				rangeIntersection.addAll(rangeSet);  rangeIntersection.retainAll(rangeList);
				// domainIntersection and rangeList is the intersection of the new rule 
				if (domainIntersection.size()>0 && rangeIntersection.size()>0) {
					if(! ( 
							(rules.containsKey(domainIntersection) && rules.get(domainIntersection).equals(rangeIntersection))
							||
							(rulesToBeAdded.containsKey(domainIntersection) && rulesToBeAdded.get(domainIntersection).equals(rangeIntersection))
							) ) {
						rulesToBeAdded.put(domainIntersection, rangeIntersection);
					}
				}
				if (domainList.size()==rangeList.size() && domainSet.size()==rangeSet.size()
						&& !(domainList.size() == domainIntersection.size() && domainSet.size() == domainIntersection.size())
						&& domainIntersection.size()>0
						) {
					// this section is for UMI - union minus intersection valid id domain and range have same number of elements
					ArrayList<Integer> domainUMI =new ArrayList<Integer>();
					domainUMI.addAll(domainSet);  domainUMI.addAll(domainList);domainUMI.removeAll(domainIntersection);
					ArrayList<Integer> rangeUMI =new ArrayList<Integer>();
					rangeUMI.addAll(rangeSet);  rangeUMI.addAll(rangeList);rangeUMI.removeAll(rangeIntersection);
					// domainUMI and rangeUMI is the new inference wrt new rule 
					if (domainUMI.size()>0 && rangeUMI.size()>0) {
						if(! ( 
								( rules.containsKey(domainUMI) && rules.get(domainUMI).equals(rangeUMI)  ) 
								||
								( rulesToBeAdded.containsKey(domainUMI) && rulesToBeAdded.get(domainUMI).equals(rangeUMI)  )
								)) {
							rulesToBeAdded.put(domainUMI, rangeUMI);
						}
					}
				}
			}//end of rules loop
			if (rulesToBeAdded.size()>=1){
				for (Map.Entry<List<Integer>,List<Integer>> singleRule : rulesToBeAdded.entrySet()) {
					List<Integer> domainList = singleRule.getKey();
					List<Integer> rangeList = singleRule.getValue();
					if( !(rules.containsKey(domainList) && rules.get(domainList).equals(rangeList) ) ) {
						Collections.sort(domainList);
						Collections.sort(rangeList);
						rules.put(domainList,rangeList);
						updatePossibilities(domainList, rangeList);

					}
				}

			}

		}

	}


	/**
	 * Update the rules for this game based on possibilities
	 */
	public void updateRulesFromPossibilities(){
		// using possibilities
		// update rules where possibilities are only 1
		if(rules.size()==0)
			return;
		for(Map.Entry<Integer,List<Integer>> domainValidPossibility :possibilities.entrySet() ){
			Integer domainElementP=domainValidPossibility.getKey();
			List<Integer> rangeListP=domainValidPossibility.getValue();
			if(rangeListP.size() == 1){
				Map<List<Integer>, List<Integer>> rulesToBeAdded=new HashMap<List<Integer>,List<Integer>>();
				for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
					List<Integer> domainList = new ArrayList<Integer>(singleRule.getKey());
					List<Integer> rangeList = new ArrayList<Integer>(singleRule.getValue());
					if (rangeList.size()<=1)
						continue;
					for(Integer domainElementR : domainList ) {
						if (domainElementR == domainElementP  && domainList.size() == rangeList.size()){
							rangeList.remove(rangeListP.get(0));
							domainList.remove(domainElementR);
							if(domainList.size()>=1 && !rules.containsKey(domainList)) {
								rulesToBeAdded.put(domainList, rangeList);
								// rules.put(domainList,rangeList);
								updatePossibilities(domainList,rangeList);
								// as it is a new rule it might be a good idea to call updatePossibilities
							}
							break; 
							// to move to the next rule
						}
					}
				}//end of rules loop
				if (rulesToBeAdded.size()>=1){
					for (Map.Entry<List<Integer>,List<Integer>> singleRule : rulesToBeAdded.entrySet()) {
						List<Integer> domainList = singleRule.getKey();
						List<Integer> rangeList = singleRule.getValue();
						if(domainList.size()==0 || rangeList.size()==0)
							continue;
						if( !(rules.containsKey(domainList) && rules.get(domainList).equals(rangeList) ) ) {
							Collections.sort(domainList);
							Collections.sort(rangeList);
							rules.put(domainList,rangeList);
							//	updateQueryHistory((ArrayList<Integer>)domainList);
							updatePossibilities(domainList, rangeList);
						}

					}
				}


			}// end of if size is 1
		}//end of parsing over possibilities
	}//end of updateRulesFromPossibilities


	public void __updateRulesFromPossibilities() {
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

	public void _updateRulesFromPossibilities() {
		// using possibilities // update rules where possibilities are only 1
		System.out.println("rules changed");
		for (int i = 1; i <= possibilities.size(); i++) {
			if (possibilities.get(i).size() == 1) {
				Iterator<List<Integer>> itr = rules.keySet().iterator();
				while (itr.hasNext()) {
					List<Integer> k = itr.next();
					if (k.contains(i)) {
						List<Integer> inf = rules.get(k);
						inf.remove(possibilities.get(i));
						k.remove(i);
						rules.remove(k);
						rules.put(k, inf);
					}
				}
			}
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
			if (poss.size() == 0) {
				poss.addAll(result);
			} else {
				for (Iterator<Integer> itr = poss.iterator(); itr.hasNext();) {
					if (!result.contains(itr.next())) {
						itr.remove();
					}
				}
			}
		}
	}

	/*
	 *  To print the current state of rules table
	 *  (supporting method)
	 */
	public void printCurrentRules(String message) {
		System.out.println("\n\tRules Table  "+message);
		for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
			List<Integer> domainList = singleRule.getKey();
			List<Integer> rangeList = singleRule.getValue();
			if(domainList.size()>=1)
				System.out.println("\t"+domainList+"  --> "+rangeList);
		}
		System.out.println("\tRules END  \n");
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

	/**
	 * Returns true if the final solution has been reached. That is, if each
	 * number maps to only one possibility, a solution has been found.
	 */
	public boolean solutionReached() {
		for (Integer i : possibilities.keySet()) {
			if (possibilities.get(i).size() != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Upon response from the game, we update possibilities based on our
	 * currentGuess and then update the rules appropriately
	 */
	@Override
	public void setResult(ArrayList<Integer> alResult) {
		System.out.println("\t###########\n\t"+currentGuess+" ->> "+alResult+"\n\t###########");
		updateRules(currentGuess,alResult);
		//rules.put(currentGuess, alResult);

		updatePossibilities(currentGuess, alResult);
		updateRulesFromPossibilities();
		printCurrentRules("After");


	}
}
