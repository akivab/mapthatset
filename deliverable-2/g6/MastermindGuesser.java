package mapthatset.g6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser2 extends Guesser {
	Map<Integer, List<Integer>> possibilities;
	Map<List<Integer>, List<Integer>> rules;
	
	int mapLength;
	int count;
	
	Map<Integer, Integer> finalGuess;
	ArrayList<Integer> currentGuess;
	ArrayList<Integer> randIndex;
	String strID = "Mastermind";

	/**
	 * Initializes a new mapping.
	 */
	@Override
	public void startNewMapping(int mapLength) {
		this.mapLength = mapLength;
		this.randIndex = getRandomIndex(mapLength);
		this.finalGuess = new HashMap<Integer, Integer>();
		this.count = 0;
		possibilities = new HashMap<Integer, List<Integer>>();
		for (int i = 1; i <= mapLength; i++) {
			possibilities.put(i, new ArrayList<Integer>());
			finalGuess.put(i, -1);
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
			currentGuess = (ArrayList<Integer>) makeQuery();
		} else {
			todo = "g";
			currentGuess = (ArrayList<Integer>) makeGuess();
		}
		count+=2;
		////System.out.println();
		//System.out.println(todo + ": " + currentGuess);
		return new GuesserAction(todo, currentGuess);
	}
	
	public static void main(String args[]){
		MastermindGuesser2 mmg2 = new MastermindGuesser2();
		mmg2.testSolutionReached();
		mmg2.testGetRandomIndex();
		//System.out.println("Helper methods work.");
	}

	public ArrayList<Integer> makeQuery() {
		System.out.println();
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		if(count+1 < mapLength){
			////System.out.println(randIndex + "\t" + count);
			toReturn.add(randIndex.get(count));
			toReturn.add(randIndex.get(count+1));
			return toReturn;
		}
		else{
			int p1 = -1, p2 = -1;
			for(Integer i : possibilities.keySet())
				if(finalGuess.get(i) == -1){
					if(p1 < 0)
						p1 = i;
					else if(p2 < 0){
						boolean seen = false;
						for(List<Integer> l : rules.keySet())
							if(l.contains(p1) && l.contains(i))
								seen = true;
						if(!seen)
							p2 = i;
					}
				}
			if(p1 > 0) toReturn.add(p1);
			if(p2 > 0) toReturn.add(p2);
			return toReturn;
		}
	}
	

	public ArrayList<Integer> makeGuess() {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for(int i = 1; i <= mapLength; i++)
			toReturn.add(finalGuess.get(i));
		return toReturn;
	}
	
	public boolean solutionReached(Map<Integer, List<Integer>> possibilities){
		boolean toReturn = true;
		for(Integer i : finalGuess.keySet())
			toReturn &= finalGuess.get(i)!=-1;
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

	public ArrayList<Integer> getRandomIndex(int n) {
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
	
	public void setupPossibilities(){
		
		int count = -1;
		while (count != 0) {
			count = 0;
			for (List<Integer> rule : rules.keySet())
				if (rules.get(rule).size() == 2) {
					int r1 = rule.get(0);
					int r2 = rule.get(1);
					int f1 = finalGuess.get(r1);
					int f2 = finalGuess.get(r2);
					int p0 = rules.get(rule).get(0);
					int p1 = rules.get(rule).get(1);
					if (f1 != -1 && f2 == -1){
						finalGuess.put(r2, p0 == f1 ? p1 : p0);
						print(r2, finalGuess.get(r2));
					}
					else if (f1 == -1 && f2 != -1){
						finalGuess.put(r1, p0 == f2 ? p1 : p0);
						print(r1, finalGuess.get(r1));
					}
					else
						count--;
					count++;
				}
		}
	}
	
	public void print(int i, int j){
		if(j!=0)
			System.out.println(i+"->"+j);
	}

	@Override
	public void setResult(ArrayList<Integer> alResult) {
		// any new information will come from alResult
		if(alResult.size() == 1)
			for(Integer i : currentGuess){
				finalGuess.put(i, alResult.get(0));
				print(i, alResult.get(0));
			}
		
		// try to limit information about other guesses		
		for(Integer poss : currentGuess){
			if(possibilities.get(poss).size() == 0)
				possibilities.get(poss).addAll(alResult);
			else
				for(Iterator<Integer> itr = possibilities.get(poss).iterator(); itr.hasNext(); )
					if(!alResult.contains(itr.next()))
						itr.remove();
			if(finalGuess.get(poss) == -1 && possibilities.get(poss).size() == 1){
				Integer soln = possibilities.get(poss).get(0);
				//System.out.println("Found another that works! " + poss + " -> " + soln + "\n");
				finalGuess.put(poss, soln);
				print(poss, soln);
			}
		}
		rules.put(currentGuess, alResult);
		//System.out.println(possibilities);
		setupPossibilities();
	}
}
