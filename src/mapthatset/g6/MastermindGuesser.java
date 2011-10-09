package mapthatset.g6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser extends Guesser {
	Map<Integer, List<Integer>> possibilities;
	Map<List<Integer>, List<Integer>> rules;

	int mapLength;
	ArrayList<Integer> currentGuess;
	String strID = "Mastermind";
	/*
	 *  This query History will keep a collection of all the queries so that we don't make any redundant queries
	 *    for example if we have queried for [1,2] and [3] then we should not query [1,2,3]
	 */
	Set <ArrayList<Integer>> queryHistory;

	/**
	 * Initializes a new mapping.
	 */
	@Override
	public void startNewMapping(int mapLength) {
		this.mapLength = mapLength;
		queryHistory=new HashSet <ArrayList<Integer>>();

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
		/*
		 * The loop below should be present 
		 * to avoid making a redundant guess 
		 * 
		 */
		
		while(queryHistory.contains(randomList) || randomList.size()==0 ) {
			randomList = returnRandomNumbers(mapLength, 1);
			int numOfDomainElements = randomList.get(0);
			randomList=returnRandomNumbers(mapLength, numOfDomainElements);
		}
		updateQueryHistory((ArrayList<Integer>)randomList);
		return randomList;
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
	 *         By creating a new rule considering 
	 *         intersection and UMI (union minus intersection )with previous rules
	 *          
	 *  
	 *  @param newRuleDomain
	 *                   query of the rule
	 *  @param  newRuleRange                 
	 *                    result of the query
	 */
	public void updateRules (ArrayList<Integer> newRuleDomain,ArrayList<Integer> newRuleRange) {
		Collections.sort(newRuleDomain);
		Collections.sort(newRuleRange);
		if(rules.size()==0) {
			if(newRuleDomain.size()>0 && newRuleRange.size()>0) {
				rules.put(newRuleDomain, newRuleRange);
				updatePossibilities(newRuleDomain, newRuleRange);
			}
		} else {
			if(! ( rules.containsKey(newRuleDomain) && rules.get(newRuleDomain).equals(newRuleRange) ) ) {
				// if the new rule is not in in the rule set - add it 
				if(newRuleDomain.size()>0 && newRuleRange.size()>0) {
					rules.put(newRuleDomain, newRuleRange);
					updatePossibilities(newRuleDomain, newRuleRange);
				}
			}
			// currently only adding rule, though in some cases some rules can be removed, whose partitioning is exhibited 
			// by the already present rules.
			Map<List<Integer>, List<Integer>> rulesToBeAdded=new HashMap<List<Integer>,List<Integer>>();
			for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
				ArrayList<Integer> ruleDomain = (ArrayList<Integer>)singleRule.getKey();
				ArrayList<Integer> ruleRange = (ArrayList<Integer>)singleRule.getValue();

				ArrayList<Integer> domainIntersection =new ArrayList<Integer>();
				domainIntersection.addAll(newRuleDomain);  domainIntersection.retainAll(ruleDomain);
				ArrayList<Integer> rangeIntersection =new ArrayList<Integer>();
				rangeIntersection.addAll(newRuleRange);  rangeIntersection.retainAll(ruleRange);
				/* domainIntersection and rangeIntersection are the intersections of the new rule
				 * with  already present rule
				 * example -> ([1,3,6]->[2,3]) intersection ([1,4,9]->[3,4])
				 *          ==>  [1]->[3]
				 * */

				if (domainIntersection.size()>0 && rangeIntersection.size()>0) {
					if(! ( 
							(rules.containsKey(domainIntersection) && rules.get(domainIntersection).equals(rangeIntersection))
							||
							(rulesToBeAdded.containsKey(domainIntersection) && rulesToBeAdded.get(domainIntersection).equals(rangeIntersection))
							) ) {
						// if the result of intersection is a new rule -add it in the rule list
						rulesToBeAdded.put(domainIntersection, rangeIntersection);
					}
				}
				if (ruleDomain.size()==ruleRange.size() && newRuleDomain.size()==newRuleRange.size()
						&& !(ruleDomain.size() == domainIntersection.size() && newRuleDomain.size() == domainIntersection.size())
						&& domainIntersection.size()>0
						) {
					/* this section is for UMI - union minus intersection 
					 * if  domain and range have same number of elements
					 * example -> ([3,5,6]->[2,5,7]) UMI  ([5,6,8]->[2,3,7])
					 *         ==>[3,8]->[3,5]
					 */ 
					ArrayList<Integer> domainUMI =new ArrayList<Integer>();
					domainUMI.addAll(newRuleDomain);  domainUMI.addAll(ruleDomain);domainUMI.removeAll(domainIntersection);
					ArrayList<Integer> rangeUMI =new ArrayList<Integer>();
					rangeUMI.addAll(newRuleRange);  rangeUMI.addAll(ruleRange);rangeUMI.removeAll(rangeIntersection);
					// domainUMI and rangeUMI is the new inference wrt new rule 
					if (domainUMI.size()>0 && rangeUMI.size()>0) {
						if(! ( 
								( rules.containsKey(domainUMI) && rules.get(domainUMI).equals(rangeUMI)  ) 
								||
								( rulesToBeAdded.containsKey(domainUMI) && rulesToBeAdded.get(domainUMI).equals(rangeUMI)  )
								)) {
							// if the result of UMI is a new rule -add it in the rule list
							rulesToBeAdded.put(domainUMI, rangeUMI);
						}
					}
				}
			}//end of rules loop
			if (rulesToBeAdded.size()>=1){
				// add all the new rules
				for (Map.Entry<List<Integer>,List<Integer>> singleRule : rulesToBeAdded.entrySet()) {
					List<Integer> ruleDomain = singleRule.getKey();
					List<Integer> ruleRange = singleRule.getValue();
					if( !(rules.containsKey(ruleDomain) && rules.get(ruleDomain).equals(ruleRange) ) ) {
						Collections.sort(ruleDomain);
						Collections.sort(ruleRange);
						rules.put(ruleDomain,ruleRange);
						updateQueryHistory((ArrayList<Integer>)ruleDomain);
						updatePossibilities(ruleDomain, ruleRange);
					}
				}
			}
		}
	}

	/*
	 *  This method will update the queryHistory
	 *   the Idea is that if we are having rules like -
	 *   ([1,2,3]->[2,4]) and ([3,9]->2,8]) then
	 *   we should not query for [1,2,3,9] as it will give no additional information 
	 *   so Query History is the set of all those set of domains
	 *   whose information we already have in the rule set or can drawn from he rule set
	 */
	public void updateQueryHistory(ArrayList<Integer> queryToBeAdded) {

		if(queryHistory.size()>=1) {
			Set<ArrayList<Integer>> queriesToAdd =new HashSet <ArrayList<Integer>>(); 
			for(ArrayList<Integer> previousQuery :queryHistory) {
				ArrayList<Integer> combinedQuery =new ArrayList<Integer>();
				combinedQuery.addAll(previousQuery);
				combinedQuery.addAll(queryToBeAdded);
				combinedQuery=new ArrayList<Integer>(new TreeSet<Integer>(combinedQuery));
				Collections.sort(combinedQuery);
				if(!queryHistory.contains(combinedQuery)) 
					queriesToAdd.add(combinedQuery);
			}
			queryHistory.addAll(queriesToAdd);
		}
		if(!queryHistory.contains(queryToBeAdded))
			queryHistory.add(queryToBeAdded);

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
					List<Integer> ruleDomain = new ArrayList<Integer>(singleRule.getKey());
					List<Integer> ruleRange = new ArrayList<Integer>(singleRule.getValue());
					if (ruleRange.size()<=1)
						continue;
					for(Integer domainElementR : ruleDomain ) {
						if (domainElementR == domainElementP  && ruleDomain.size() == ruleRange.size()){
							/* example -> if ([1,2,3]->[3,4]) is a rule 
							 *          and from possibilities we have [2]->[3]
							 *          then we can NOT say that ([1,3]->[4]) as  ruleDomain.size() != ruleRange.size()
							 *          
							 *          But if it was ([1,2,3]->[3,4,9]) and Possibilities say [2]->[3]
							 *          then we can infer that ([1,3]->[4,9]) 
							 * 
							 * 
							 */
							ruleRange.remove(rangeListP.get(0));
							ruleDomain.remove(domainElementR);
							if(ruleDomain.size()>=1 && !rules.containsKey(ruleDomain)) {
								rulesToBeAdded.put(ruleDomain, ruleRange);
								// rules.put(ruleDomain,ruleRange);
								updatePossibilities(ruleDomain,ruleRange);
								// as it is a new rule it might be a good idea to call updatePossibilities
							}
							break; 
							// to move to the next rule
						}
					}
				}//end of rules loop
				if (rulesToBeAdded.size()>=1){
					for (Map.Entry<List<Integer>,List<Integer>> singleRule : rulesToBeAdded.entrySet()) {
						List<Integer> ruleDomain = singleRule.getKey();
						List<Integer> ruleRange = singleRule.getValue();
						if(ruleDomain.size()==0 || ruleRange.size()==0)
							continue;
						if( !(rules.containsKey(ruleDomain) && rules.get(ruleDomain).equals(ruleRange) ) ) {
							Collections.sort(ruleDomain);
							Collections.sort(ruleRange);
							rules.put(ruleDomain,ruleRange);
							updateQueryHistory((ArrayList<Integer>)ruleDomain);
							updatePossibilities(ruleDomain, ruleRange);
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
	
	/*
	 *  To print the current query History 
	 *  (supporting method)
	 */
	public void printQueryHistory(String message) {
		System.out.println("\n\t\t\t\tQuery History  "+message);
		for (ArrayList<Integer> singleQuery : queryHistory) {
			System.out.println("\t\t\t\t"+singleQuery);
		}
		System.out.println("\n\t\t\t\tQuery END  "+message+"\n");
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
		printQueryHistory(" ");
		System.out.println("\n\t\t\t\t\t\tPossibilities"+possibilities);


	}
}
