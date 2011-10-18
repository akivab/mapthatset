package mapthatset.g6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser2 extends Guesser {
	Map<Integer, List<Integer>> possibilities;
	Map<List<Integer>, List<Integer>> rules;

	int mapLength;
	boolean debuggingMode=false;

	boolean guessed=false;

	/*
	 *  This query History will keep a collection of all the queries so that we don't make any redundant queries
	 *    for example if we have queried for [1,2] and [3] then we should not query [1,2,3]
	 */
	Set <ArrayList<Integer>> queryHistory;

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
		queryHistory=new HashSet <ArrayList<Integer>>();


		leftToExplore = new ArrayList<Integer>();
		for(int i = 1 ; i <= mapLength; i++)
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
		if (!solutionReached() ) {
			printIfDebugging("\n\nAt begin leftTodo="+leftTodo);
			printIfDebugging("At begin leftToExplore="+leftToExplore);

			cleanLeftToDo();
			Collection 	<ArrayList<Integer>> leftTodoNoDups=new LinkedHashSet<ArrayList<Integer>>(leftTodo);
			if(!leftTodoNoDups.isEmpty()) {
				leftTodo.clear();
				leftTodo.addAll(leftTodoNoDups);
			}
			//else
			if(!rules.isEmpty())
				leftTodo.removeAll(rules.keySet());
			//printIfDebugging("\n\nAt begin After Rule leftTodo="+leftTodo);
			if(!queryHistory.isEmpty())
				leftTodo.removeAll(queryHistory);
			
			//printIfDebugging("\n\nAt begin After query  leftTodo="+leftTodo);
			updateLeftToExplore();

			if(leftTodo.isEmpty() && !leftToExplore.isEmpty()){
				leftTodo.add(new ArrayList<Integer>(leftToExplore));
				ArrayList<ArrayList<Integer>> perms = makeBalancedGuess(leftToExplore);
				if(perms.size()>0 && perms.get(0).size()>0) {
					leftTodo.addAll(perms);
				}

				//leftTodo.add(new ArrayList<Integer>(leftToExplore));
				//leftToExplore.clear();
			}
			//			if(leftTodo.size()>2*leftToExplore.size()) {
			//				leftTodo.clear();
			//				for(Integer lTe :leftToExplore) {
			//					ArrayList<Integer> temp =new ArrayList<Integer>();
			//					temp.add(lTe);
			//					leftTodo.add(temp);
			//				}
			//				printIfDebugging("At exchange leftTodo="+leftTodo);
			//
			//			}
			printIfDebugging("At Mid leftTodo="+leftTodo);
			printIfDebugging("At Mid leftToExplore="+leftToExplore);
			if(!leftTodo.isEmpty()){
				currentGuess = makeAGuess();
				while( (!leftTodo.isEmpty() ) &&( rules.containsKey(currentGuess) ||queryHistory.contains(currentGuess))) {
					printIfDebugging("== currentGuess ==="+currentGuess);
					/*printIfDebugging("\t  !isNewRule(currentGuess)="+!isNewRule(currentGuess)+
							"  !leftTodo.isEmpty()="+!leftTodo.isEmpty()+"    rules.containsKey(currentGuess)="+
							rules.containsKey(currentGuess));*/
					if (leftTodo.isEmpty()) {
						ArrayList<ArrayList<Integer>> perms = makeBalancedGuess(leftToExplore);
						if(perms.size()>0 && perms.get(0).size()>0) {
							leftTodo.addAll(perms);
						}
						//printIfDebugging("== At inferno adding="+leftToExplore);
					}
					currentGuess = makeAGuess();
				}
			}
			printIfDebugging("At End leftTodo="+leftTodo);
			printIfDebugging("At End leftToExplore="+leftToExplore);
			//else
			//System.exit(1);
		} else {
			todo = "g";
			currentGuess = (ArrayList<Integer>) makeGuess();
			guessed=true;
		}

		printIfDebugging("");

		return new GuesserAction(todo, currentGuess);
	}

/*
 * This works on leftTodo and removes the first element from it
 */
	
	public ArrayList<Integer> makeAGuess(){
		ArrayList<Integer> guessToReturn=new ArrayList<Integer>();
		ArrayList<Integer> currentRange=new ArrayList<Integer>();
		ArrayList<Integer> inLineRange=new ArrayList<Integer>();
		printIfDebugging("== makeAGuess === on entry leftTodo="+leftTodo);

		ArrayList<ArrayList<Integer>> leftTodoRemove = new ArrayList<ArrayList<Integer>>();
		guessToReturn=leftTodo.remove(0);
		printIfDebugging("\t== guessToReturn ==="+guessToReturn);
		currentRange= findRange(guessToReturn);
		for(ArrayList<Integer> guessInLine :leftTodo) {
			ArrayList<Integer> intersectionDomain=new ArrayList<Integer>();
			intersectionDomain.addAll(guessToReturn);
			intersectionDomain.retainAll(guessInLine);
			if(intersectionDomain.isEmpty()) {
				// domains are disjoint
				inLineRange= findRange(guessInLine);
				ArrayList<Integer> intersectionRange=new ArrayList<Integer>();
				intersectionRange.addAll(currentRange);
				intersectionRange.retainAll(inLineRange);
				if(intersectionRange.isEmpty()) {
					leftTodoRemove.add(guessInLine);
					
					Set<Integer> unionSet = new HashSet<Integer>();
					unionSet.addAll(guessToReturn);unionSet.addAll(guessInLine);
					guessToReturn.clear();guessToReturn.addAll(unionSet); 
					printIfDebugging("\t\t== adding guessInLine ==="+guessInLine);
					printIfDebugging("\t== +guessToReturn ==="+guessToReturn);
					
					currentRange= findRange(guessToReturn);
				}
			}
		}
		if(!leftTodoRemove.isEmpty())
			leftTodo.removeAll(leftTodoRemove);
		printIfDebugging("== \t chosen sets ->"+leftTodoRemove);
		printIfDebugging("== makeAGuess === on exit leftTodo="+leftTodo);
		return guessToReturn;
	}
	
	public ArrayList<Integer> findRange(ArrayList<Integer> domainInQuestion) {
		ArrayList<Integer> rangeToReturn =new ArrayList<Integer>();
		Set<Integer> unionRangeSet = new HashSet<Integer>();
		for(Integer i :domainInQuestion) {
			unionRangeSet.addAll(possibilities.get(i));
		}
		rangeToReturn.addAll(unionRangeSet);
		
		printIfDebugging("\t*** findRange input ===domainInQuestion="+domainInQuestion);
		printIfDebugging("\t*** findRange output ===rangeToReturn="+rangeToReturn);
		printIfDebugging("\t*** Possibilities"+possibilities);

		return rangeToReturn;
	}

	/*
	 * This function will clean leftToDo of all domain elements whose value is known
	 */
	public void cleanLeftToDo() {
		ArrayList<ArrayList<Integer>> leftTodoRemove = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> leftTodoAdd = new ArrayList<ArrayList<Integer>>();
		for(ArrayList<Integer> toQuery : leftTodo) {
			ArrayList<Integer> toReplace =new  ArrayList<Integer>();
			boolean replace=false;
			for(Integer element :toQuery) {
				if (possibilities.get(element).size()==1) {
					replace=true;
				} else {
					toReplace.add(element);
				}
			}
			if(replace){
				leftTodoRemove.add(toQuery);
				if(!toReplace.isEmpty())
					leftTodoAdd.add(toQuery);
			}
		}

		if(!leftTodoRemove.isEmpty())
			leftTodo.removeAll(leftTodoRemove);
		if(!leftTodoAdd.isEmpty())
			leftTodoAdd.addAll(leftTodoAdd);


	}

	public static void main(String args[]){
		MastermindGuesser2 mmg2 = new MastermindGuesser2();
		mmg2.testSolutionReached();
		mmg2.testGetRandomIndex();
		mmg2.testLimitPossibilities1();
		mmg2.testLimitPossibilities2();

		//printIfDebugging("Helper methods work.");
	}

	public ArrayList<ArrayList<Integer>> trySplitting(List<Integer> toChooseFrom) {
		ArrayList<Integer> t1 = new ArrayList<Integer>();
		ArrayList<Integer> t2 = new ArrayList<Integer>();
		for(int i = 0; i < toChooseFrom.size(); i++)
			if(possibilities.get(toChooseFrom.get(i)).size()!=1)
				if(i < toChooseFrom.size()/2)
					t1.add(toChooseFrom.get(i));
				else
					t2.add(toChooseFrom.get(i));
		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();
		toReturn.add(t1);
		toReturn.add(t2);
		return toReturn;
	}

	public boolean isNewRule(ArrayList<Integer> obj){
		boolean b = false;
		for(List<Integer> rule : rules.keySet())
			b |= obj.containsAll(rule);
				return !b;
	}

	public boolean isNewRule(List<Integer> chooseFrom, int i, int j){
		return isNewRule(getArr(chooseFrom, i, j));
	}

	public ArrayList<Integer> getArr(List<Integer> chooseFrom, int i, int j){
		ArrayList<Integer> t = new ArrayList<Integer>();
		t.add(chooseFrom.get(i));
		t.add(chooseFrom.get(j));
		return t;
	}

	/*
	 * This function will update leftToExplore 
	 * if the possibility of some element is one then it will be removed from leftToExplore
	 */
	public void updateLeftToExplore() {
		ArrayList<Integer> toRemove =new ArrayList<Integer>();
		for (Integer i : leftToExplore)
			if(possibilities.get(i).size() == 1 ) {
				toRemove.add(i);
			}
		leftToExplore.removeAll(toRemove);
	}



	public ArrayList<ArrayList<Integer>> makeBalancedGuess(List<Integer> toChooseFrom){
		// currently taking n^0.5 , where n is total size of the toChooseFrom
		/*
		 * It is toChooseFrom - elements whose value is already known
		 */
		ArrayList<Integer> condensedChoices =new ArrayList<Integer>();
		ArrayList<Integer> firstElement =new ArrayList<Integer>();

		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> toReturnEnd = new ArrayList<ArrayList<Integer>>();
		for(Integer i :toChooseFrom)
			if(possibilities.get(i).size() > 1 ) {
				if(firstElement.isEmpty())
					firstElement.add(i);
				condensedChoices.add(i);
			}
		int n = condensedChoices.size();
		if(n<=2) {
			toReturn.add(condensedChoices);
			toReturn.add(firstElement);
			return toReturn;
		}
		int first=condensedChoices.get(0);
		condensedChoices.add(first);
		int rootN =(int)Math.ceil(Math.sqrt(n));
		int lowerIndex=0;
		int upperIndex=rootN;
		boolean carryOn =true;
		while(carryOn) {
			//printIfDebugging("\ntoChooseFrom "+toChooseFrom+"   lowerIndex="+lowerIndex+"  upperIndex="+upperIndex);
			ArrayList<Integer> toAdd=new ArrayList<Integer>();
			toAdd.clear();

			if(upperIndex>=n) {
				if(lowerIndex<=n-1)
					toAdd.addAll(condensedChoices.subList(lowerIndex,n));
				carryOn=false;
			}
			else
				toAdd.addAll(condensedChoices.subList(lowerIndex, upperIndex));

			if(!rules.containsKey(toAdd) && !leftTodo.contains(toAdd) && !toAdd.isEmpty())
				toReturn.add(toAdd);
			//else {
			ArrayList<ArrayList<Integer>> subGuess=new ArrayList<ArrayList<Integer>>(makeBalancedGuess(toAdd));
			if(!subGuess.isEmpty() && !subGuess.get(0).isEmpty())
				toReturnEnd.addAll(subGuess);
			//	}
			lowerIndex=upperIndex;
			upperIndex+=rootN;
		}
		if(!toReturnEnd.isEmpty())
			toReturn.addAll(toReturnEnd);

		return toReturn;
	}

	public ArrayList<ArrayList<Integer>> makeBestGuessSets(List<Integer> toChooseFrom){
		// currently taking n^0.5 , where n is total size of the toChooseFrom
		/*
		 * It is toChooseFrom - elements whose value is already known
		 */
		ArrayList<Integer> condensedChoices =new ArrayList<Integer>();
		ArrayList<Integer> firstElement =new ArrayList<Integer>();

		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> toReturnEnd = new ArrayList<ArrayList<Integer>>();
		for(Integer i :toChooseFrom)
			if(possibilities.get(i).size() > 1 ) {
				if(firstElement.isEmpty())
					firstElement.add(i);
				condensedChoices.add(i);
			}
		int n = condensedChoices.size();
		if(n<=2) {
			toReturn.add(condensedChoices);
			toReturn.add(firstElement);
			return toReturn;
		}
		int first=condensedChoices.get(0);
		condensedChoices.add(first);
		int rootN =(int)Math.ceil(Math.sqrt(n));
		int lowerIndex=0;
		int upperIndex=rootN;
		boolean carryOn =true;
		while(carryOn) {
			//printIfDebugging("\ntoChooseFrom "+toChooseFrom+"   lowerIndex="+lowerIndex+"  upperIndex="+upperIndex);
			ArrayList<Integer> toAdd=new ArrayList<Integer>();
			toAdd.clear();

			if(upperIndex>=n) {
				if(lowerIndex<=n-1)
					toAdd.addAll(condensedChoices.subList(lowerIndex,n));
				carryOn=false;
			}
			else
				toAdd.addAll(condensedChoices.subList(lowerIndex, upperIndex));

			if(!rules.containsKey(toAdd) && !leftTodo.contains(toAdd) && !toAdd.isEmpty())
				toReturn.add(toAdd);
			//else {
			ArrayList<ArrayList<Integer>> subGuess=new ArrayList<ArrayList<Integer>>(makeBalancedGuess(toAdd));
			if(!subGuess.isEmpty() && !subGuess.get(0).isEmpty())
				toReturnEnd.addAll(subGuess);
			//	}
			lowerIndex=upperIndex-1;
			upperIndex+=rootN;
		}
		if(!toReturnEnd.isEmpty())
			toReturn.addAll(toReturnEnd);

		return toReturn;
	}


	public ArrayList<ArrayList<Integer>> makeSmallGuess(List<Integer> toChooseFrom){
		int j = 0;
		int n = toChooseFrom.size();
		int k = 0;
		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();
		while(k<n){
			while(j < n && possibilities.get(toChooseFrom.get(j)).size() == 1) j++;
			k = j+1;
			while(k < n && (possibilities.get(toChooseFrom.get(k)).size()==1 || !isNewRule(toChooseFrom, j, k) )  ) k++;
			if(k<n) toReturn.add(getArr(toChooseFrom,j,k));
			else if(j < n){
				ArrayList<Integer> t = new ArrayList<Integer>();
				t.add(j);
				toReturn.add(t);
			}
			j=k+1;
		}
		return toReturn;
	}


	public ArrayList<Integer> makeGuess() {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for(Integer i : possibilities.keySet())
			toReturn.add(0);
				for(Integer i : possibilities.keySet())
					toReturn.set(i-1,possibilities.get(i).get(0));
						return toReturn;
	}

	public boolean solutionReached(Map<Integer, List<Integer>> possibilities){
		boolean toReturn = true;
		for(Integer i : possibilities.keySet())
			toReturn &= possibilities.get(i).size()==1;
				return toReturn;
	}

	public boolean solutionReached(){
		return solutionReached(possibilities);
	}

	public void testSolutionReached(){
		int n = 10;
		Map<Integer, List<Integer>> tmp = new HashMap<Integer, List<Integer>>();
		for(int i = 1; i<= n; i++)
		{
			tmp.put(i, new ArrayList<Integer>());
			tmp.get(i).add(i);
		}
		assert solutionReached(tmp);
	}


	public String getID() {
		return strID;
	}

	public static ArrayList<Integer> getRandomIndex(int n) {
		ArrayList<Integer> start = new ArrayList<Integer>();
		ArrayList<Integer> end = new ArrayList<Integer>();
		Random r = new Random();
		int i = 1;
		while(start.size() < n)
			start.add(i++);
		while(start.size() > 0)
			end.add(start.remove(r.nextInt(start.size())));
		return end;
	}

	public void testGetRandomIndex(){
		int n = 10;
		ArrayList<Integer> r = getRandomIndex(n);
		assert r.size() == n;
		for(int i = 1; i <= n; i++)
			assert r.contains(i);
	}

	@Override
	public void setResult(ArrayList<Integer> alResult) {
		//rules.put((List<Integer>) currentGuess.clone(), (List<Integer>) alResult.clone());
		//	printIfDebugging(possibilities);
		printIfDebugging("\t###########\n\t"+currentGuess+" ->> "+alResult+"\n\t###########");
		//	if(guessed)
		//		return;
		updateRules(currentGuess,alResult);
		//limitPossibilities(rules, possibilities);
		printCurrentRules("After");
		printIfDebugging("\n\tPossibilities"+possibilities);
		printQueryHistory(" ");


		if(currentGuess.size() == alResult.size()){
			ArrayList<ArrayList<Integer>> perms = solvePermutation(currentGuess);
			//if(leftToExplore.containsAll(currentGuess)){
			if(perms.size()>0 && perms.get(0).size()>0) {
				leftTodo.addAll(perms);
				printIfDebugging("\n\t++ log leftTodo+="+perms);
			}
			//leftToExplore.removeAll(currentGuess);
			//	}
		}
		else{
			ArrayList<ArrayList<Integer>> perms = makeBalancedGuess(currentGuess);
			if(perms.size()>0 && perms.get(0).size()>0) {
				leftTodo.addAll(perms);
				printIfDebugging("\n\t++ leftTodo+="+perms);
			}
			//	leftTodo.addAll(makeBalancedGuess(leftToExplore));
			// should this be commented ? leftToExplore.removeAll(currentGuess);
		}
	}

	public void  updatePossibilities(ArrayList<Integer> domain, ArrayList<Integer> range) {
		//printIfDebugging("\n updatePosin="+domain+"  range="+range);
		for(Integer choice : domain){
			possibilities.get(choice).retainAll(range);
		}
	}

	/*
	 *  To print the current state of rules table
	 *  (supporting method)
	 */
	public void printCurrentRules(String message) {
		printIfDebugging("\n\tRules Table  "+message);
		for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
			List<Integer> domainList = singleRule.getKey();
			List<Integer> rangeList = singleRule.getValue();
			if(domainList.size()>=1)
				printIfDebugging("\t"+domainList+"  --> "+rangeList);
		}
		printIfDebugging("\tRules END  \n");
	}

	public void limitPossibilities(Map<List<Integer>, List<Integer>> mapping, Map<Integer, List<Integer>> poss){
		for(List<Integer> r1 : mapping.keySet())
			for(List<Integer> r2 : mapping.keySet())
				if(!r1.equals(r2)){
					List<Integer> g1 = new ArrayList<Integer>();
					List<Integer> g2 = new ArrayList<Integer>();
					g1.addAll(r1);
					g1.retainAll(r2);
					g2.addAll(mapping.get(r1));
					g2.retainAll(mapping.get(r2));

					if(g1.size()!= 0 && r1.size() - g1.size() == mapping.get(r1).size() - g2.size())
						for(Integer choice : r1){
							poss.get(choice).retainAll(mapping.get(r1));
							if(g1.contains(choice))
								poss.get(choice).retainAll(g2);
							else
								poss.get(choice).removeAll(g2);
						}

				}
	}

	public void testLimitPossibilities1(){
		HashMap<List<Integer>, List<Integer>> mapping = new HashMap<List<Integer>, List<Integer>>();
		HashMap<Integer, List<Integer>> poss  = new HashMap<Integer, List<Integer>>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(int i = 1 ; i <= 5; i++){
			tmp.add(i);
			if( i < 5 ){
				ArrayList<Integer> t1 = new ArrayList<Integer>();
				ArrayList<Integer> t2 = new ArrayList<Integer>();
				t1.add(i);
				t1.add(i+1);
				t2.addAll(t1);
				mapping.put(t1,t2);
			}
		}
		for(Integer i : tmp)
			poss.put(i, (List<Integer>) tmp.clone());
				limitPossibilities(mapping, poss);

				for(Integer i : poss.keySet()){
					assert(poss.get(i).size()==1);
					assert(i == poss.get(i).get(0));
				}
	}

	@SuppressWarnings("unchecked")
	public void testLimitPossibilities2(){
		HashMap<List<Integer>, List<Integer>> mapping = new HashMap<List<Integer>, List<Integer>>();
		HashMap<Integer, List<Integer>> poss  = new HashMap<Integer, List<Integer>>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(int i = 1; i<=200; i++)
			tmp.add(i);
		for(Integer i : tmp)
			poss.put(i,  (List<Integer>) tmp.clone());
				mapping.put((List<Integer>)tmp.clone(), (List<Integer>)tmp.clone());
				ArrayList<ArrayList<Integer>> perms = solvePermutation(tmp);
				//printIfDebugging(perms);
				for(ArrayList<Integer> perm : perms)
					mapping.put((List<Integer>) perm.clone(), (List<Integer>) perm.clone());
						limitPossibilities(mapping, poss);
						printIfDebugging(mapping.keySet().size() + " for " + tmp.size() + " elements");
						for(Integer i : poss.keySet()){
							assert(poss.get(i).size()==1);
							assert(i == poss.get(i).get(0));
						}
	}

	/**
	 * Given a random permutation, returns the (log n) queries that will solve it.
	 * @param list The permutation
	 * @return The queries which will solve the permutation.
	 */
	public ArrayList<ArrayList<Integer>> solvePermutation(ArrayList<Integer> list) {
		ArrayList<ArrayList<Integer>> toReturn = new ArrayList<ArrayList<Integer>>();

		int n = list.size();
		for(int k = 1; k <= n; k*=2){
			ArrayList<Integer> partitions = new ArrayList<Integer>();
			int j = 0;
			while(j < n){
				for(int i = 0; i < k && j < n; i++)
					partitions.add(list.get(j++));
				j+=k;
			}
			if(partitions.size() != n)
				toReturn.add((ArrayList<Integer>) partitions.clone());
		}
		return toReturn;
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
				if(newRuleDomain.size()==newRuleRange.size() && newRuleRange.size() ==1)
					possibilities.put(newRuleDomain.get(0), newRuleRange);
				else
					updatePossibilities(newRuleDomain, newRuleRange);
				updateQueryHistory(newRuleDomain);
			}
		} else {
			if(! ( rules.containsKey(newRuleDomain) && rules.get(newRuleDomain).equals(newRuleRange) ) ) {
				// if the new rule is not in in the rule set - add it 
				if(newRuleDomain.size()>0 && newRuleRange.size()>0) {
					rules.put(newRuleDomain, newRuleRange);
					if(newRuleRange.size() ==1){
						for(Integer singleDomain :newRuleDomain ) {
							possibilities.put(singleDomain, newRuleRange);
							ArrayList<Integer> dummy = new ArrayList<Integer>();
							dummy.add(singleDomain);
							if(!rules.containsKey(dummy))
								rules.put(dummy,newRuleRange);
						}
					}
					else
						updatePossibilities(newRuleDomain, newRuleRange);
					updateQueryHistory(newRuleDomain);
				}
			}
			Map<ArrayList<Integer>, ArrayList<Integer>> rulesToBeAdded=new HashMap<ArrayList<Integer>,ArrayList<Integer>>();
			for (Map.Entry<List<Integer>,List<Integer>> singleRule : rules.entrySet()) {
				ArrayList<Integer> ruleDomain = (ArrayList<Integer>)singleRule.getKey();
				ArrayList<Integer> ruleRange = (ArrayList<Integer>)singleRule.getValue();

				/* domainIntersection and rangeIntersection are the intersections of the new rule
				 * with  already present rule
				 * example -> ([1,3,6]->[2,3]) intersection ([1,4,9]->[3,4])
				 *          ==>  [1]->[3]
				 * */
				ArrayList<Integer> domainIntersection =new ArrayList<Integer>();
				domainIntersection.addAll(newRuleDomain);  domainIntersection.retainAll(ruleDomain);
				ArrayList<Integer> rangeIntersection =new ArrayList<Integer>();
				rangeIntersection.addAll(newRuleRange);  rangeIntersection.retainAll(ruleRange);
				if (domainIntersection.size()>0 && rangeIntersection.size()>0) {
					if(! ( 
							(rules.containsKey(domainIntersection) && rules.get(domainIntersection).equals(rangeIntersection))
							||
							(rulesToBeAdded.containsKey(domainIntersection) && rulesToBeAdded.get(domainIntersection).equals(rangeIntersection))
							) ) {
						// if the result of intersection is a new rule -add it in the rule list
						if(domainIntersection.size()>=rangeIntersection.size())
							rulesToBeAdded.put(domainIntersection, rangeIntersection);
						else
							printIfDebugging("ERROR -");
						printIfDebugging("domainIntersection="+domainIntersection+" rangeIntersection="+rangeIntersection+
								"\n\tnewRuleDomain="+newRuleDomain+" newRuleRange="+newRuleRange+
								"\n\truleDomain="+ruleDomain+"  ruleRange="+ruleRange);
					}
				}

				/* domainUnion and rangeUnion are the union of the new rule
				 * with  already present rule
				 * example -> ([1,3,6]->[2,3]) union ([1,4,9]->[3,4])
				 *          ==>  [1,3,4,6,9]->[2,3,4]
				 * */
				Set<Integer> unionSet = new HashSet<Integer>();
				ArrayList<Integer> domainUnion =new ArrayList<Integer>();
				unionSet.addAll(newRuleDomain);unionSet.addAll(ruleDomain);
				domainUnion.addAll(unionSet); unionSet.clear();
				ArrayList<Integer> rangeUnion =new ArrayList<Integer>();
				unionSet.addAll(newRuleRange);unionSet.addAll(ruleRange);
				rangeUnion.addAll(unionSet); 
				if (domainUnion.size()>0 && rangeUnion.size()>0) {
					if(! ( 
							(rules.containsKey(domainUnion) && rules.get(domainUnion).equals(rangeUnion))
							||
							(rulesToBeAdded.containsKey(domainUnion) && rulesToBeAdded.get(domainUnion).equals(rangeUnion))
							) ) {
						// if the result of union is a new rule -add it in the rule list
						if(domainUnion.size()>=rangeUnion.size())
							rulesToBeAdded.put(domainUnion, rangeUnion);
						else
							printIfDebugging("ERROR --");
						printIfDebugging("domainUnion="+domainUnion+" rangeUnion="+rangeUnion+
								"\n\tnewRuleDomain="+newRuleDomain+" newRuleRange="+newRuleRange+
								"\n\truleDomain="+ruleDomain+"  ruleRange="+ruleRange);
					}
				}


				/* this section is for R1MI -  rule 1 ( the newRule) minus intersection 
				 * if  the two rules( rule and intersections) have same difference in their respective domain and range
				 * then this applies 
				 * example -> ([3,5,7,8,9]->[1,2,3,4]) R1MI  ([5,6,7,8]->[1,2])
				 *           (Rule1  [3,5,7,8,9]->[1,2,3,4]) - (Intersection - [5,7,8] ->[1,2]) 
				 *         ==>[3,9]->[3,4] 
				 */ 				
				if (domainIntersection.size()>=rangeIntersection.size() && newRuleDomain.size()-newRuleRange.size() == domainIntersection.size()-rangeIntersection.size()
						&& domainIntersection.size()>0
						) {

					ArrayList<Integer> domainR1MI =new ArrayList<Integer>();
					domainR1MI.addAll(newRuleDomain); domainR1MI.removeAll(domainIntersection);
					ArrayList<Integer> rangeR1MI =new ArrayList<Integer>();
					rangeR1MI.addAll(newRuleRange);rangeR1MI.removeAll(rangeIntersection);
					if (domainR1MI.size()>0 && rangeR1MI.size()>0) {
						if(! ( 
								( rules.containsKey(domainR1MI) && rules.get(domainR1MI).equals(rangeR1MI)  ) 
								||
								( rulesToBeAdded.containsKey(domainR1MI) && rulesToBeAdded.get(domainR1MI).equals(rangeR1MI)  )
								)) {
							if(domainR1MI.size()>=rangeR1MI.size())
								rulesToBeAdded.put(domainR1MI, rangeR1MI);
							else
								printIfDebugging("ERROR");
							printIfDebugging("domainR1MI="+domainR1MI+" rangeR1MI="+rangeR1MI+
									"\n\tnewRuleDomain="+newRuleDomain+" newRuleRange="+newRuleRange+
									"\n\truleDomain="+ruleDomain+"  ruleRange="+ruleRange);
						}
					}
				}

				/* this section is for R2MI -  rule 2 ( the rule) minus intersection 
				 * if  the two rules( rule and intersections) have same difference in their respective domain and range
				 * then this applies 
				 * example ->    ([5,6,7,8]->[1,2]) R2MI ([3,5,7,8,9]->[1,2,3,4])
				 *           (Rule2  [3,5,7,8,9]->[1,2,3,4]) - (Intersection - [5,7,8] ->[1,2]) 
				 *         ==>[3,9]->[3,4] 
				 */ 				
				if (domainIntersection.size()>=rangeIntersection.size() && ruleDomain.size()-ruleRange.size() == domainIntersection.size()-rangeIntersection.size()
						&& domainIntersection.size()>0
						) {

					ArrayList<Integer> domainR2MI =new ArrayList<Integer>();
					domainR2MI.addAll(ruleDomain); domainR2MI.removeAll(domainIntersection);
					ArrayList<Integer> rangeR2MI =new ArrayList<Integer>();
					rangeR2MI.addAll(ruleRange);rangeR2MI.removeAll(rangeIntersection);
					if (domainR2MI.size()>0 && rangeR2MI.size()>0) {
						if(! ( 
								( rules.containsKey(domainR2MI) && rules.get(domainR2MI).equals(rangeR2MI)  ) 
								||
								( rulesToBeAdded.containsKey(domainR2MI) && rulesToBeAdded.get(domainR2MI).equals(rangeR2MI)  )
								)) {
							if(domainR2MI.size()>=rangeR2MI.size())
								rulesToBeAdded.put(domainR2MI, rangeR2MI);
							else
								printIfDebugging("ERROR ");
							printIfDebugging("domainR2MI="+domainR2MI+" rangeR2MI="+rangeR2MI+
									"\n\tnewRuleDomain="+newRuleDomain+" newRuleRange="+newRuleRange+
									"\n\truleDomain="+ruleDomain+"  ruleRange="+ruleRange);
						}
					}
				}


				/* this section is for UMI - union minus intersection 
				 * if  the two rules( their union  and intersections) have same difference in their respective domain and range
				 * then this applies 
				 * example -> ([3,5,7,8]->[1,2,4]) UMI  ([5,6,7,8]->[1,2,3])
				 *           Union - [3,5,6,7,8]->[1,2,3,4]   Intersection - [5,7,8] ->[1,2] 
				 *         ==>[3,6]->[3,4] ( which will ultimately solve for 3 and 6 etc..)
				 */ 				
				if (domainIntersection.size()>=rangeIntersection.size() && domainUnion.size()-rangeUnion.size() == domainIntersection.size()-rangeIntersection.size()
						&& domainIntersection.size()>0
						) {
					ArrayList<Integer> domainUMI =new ArrayList<Integer>();
					domainUMI.addAll(domainUnion); domainUMI.removeAll(domainIntersection);
					ArrayList<Integer> rangeUMI =new ArrayList<Integer>();
					rangeUMI.addAll(rangeUnion);rangeUMI.removeAll(rangeIntersection);
					if (domainUMI.size()>0 && rangeUMI.size()>0) {
						if(! ( 
								( rules.containsKey(domainUMI) && rules.get(domainUMI).equals(rangeUMI)  ) 
								||
								( rulesToBeAdded.containsKey(domainUMI) && rulesToBeAdded.get(domainUMI).equals(rangeUMI)  )
								)) {
							if(domainUMI.size()>=rangeUMI.size())
								rulesToBeAdded.put(domainUMI, rangeUMI);
							else
								printIfDebugging("ERROR");
							printIfDebugging("domainUMI="+domainUMI+" rangeUMI="+rangeUMI+
									"\n\tnewRuleDomain="+newRuleDomain+" newRuleRange="+newRuleRange+
									"\n\truleDomain="+ruleDomain+"  ruleRange="+ruleRange);
						}
					}
				}
			}//end of rules loop
			if (rulesToBeAdded.size()>=1){
				// add all the new rules
				for (Map.Entry<ArrayList<Integer>,ArrayList<Integer>> singleRule : rulesToBeAdded.entrySet()) {
					ArrayList<Integer> ruleDomain = singleRule.getKey();
					ArrayList<Integer> ruleRange = singleRule.getValue();
					if( !(rules.containsKey(ruleDomain) && rules.get(ruleDomain).equals(ruleRange) ) ) {
						Collections.sort(ruleDomain);
						Collections.sort(ruleRange);
						//rules.put(ruleDomain,ruleRange);
						if(ruleDomain.size()==ruleRange.size() && ruleRange.size() ==1)
							possibilities.put(ruleDomain.get(0), ruleRange);
						else
							updatePossibilities(ruleDomain, ruleRange);
						updateQueryHistory((ArrayList<Integer>)ruleDomain);
					}
				}
			}
		}
	}

	public  void printIfDebugging(String stringToPrint) {
		if(debuggingMode) {
			System.out.println(stringToPrint);
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
		/*
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
		}*/
		if(!queryHistory.contains(queryToBeAdded))
			queryHistory.add(queryToBeAdded);

	}

	/*
	 *  To print the current query History 
	 *  (supporting method)
	 */
	public void printQueryHistory(String message) {
		printIfDebugging("\n\t\t\t\tQuery History  "+message);
		for (ArrayList<Integer> singleQuery : queryHistory) {
			printIfDebugging("\t\t\t\t"+singleQuery);
		}
		printIfDebugging("\n\t\t\t\tQuery END  "+message+"\n");
	}


}
