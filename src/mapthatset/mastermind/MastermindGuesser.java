package mapthatset.mastermind;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class MastermindGuesser extends Guesser {
	Map<Integer, Set<Integer>> possibilities;
	Map<Set<Integer>, Set<Integer>> rules;
	
	int intMappingLength;
	int intLastQueryIndex = 0;
	ArrayList<Integer> alGuess = new ArrayList<Integer>();
	String strID = "Mastermind";

	public void startNewMapping(int intMappingLength) {
		this.intMappingLength = intMappingLength;
		intLastQueryIndex = 0;
		alGuess = new ArrayList<Integer>();
	}

	public GuesserAction nextAction() {
		intLastQueryIndex++;
		GuesserAction gscReturn = null;
		if (intLastQueryIndex > intMappingLength) {
			String strGuessing = "";
			for (int intGuessingElement : alGuess) {
				strGuessing += intGuessingElement + ", ";
			}
			System.out.println( "Guessing: " + strGuessing.substring( 0, strGuessing.length() - 1 ) );
			gscReturn = new GuesserAction("g", alGuess);
		} else {
			ArrayList<Integer> alQueryContent = new ArrayList<Integer>();
			alQueryContent.add(intLastQueryIndex);
			System.out.println( "Querying: " + intLastQueryIndex );
			if(intLastQueryIndex == 1) {
				for(int i=1; i<=intMappingLength; i++) {
					alQueryContent.add(i);
				}
			} else {
				//TODO: change this to query randomly, or in pairs
				alQueryContent.add(intLastQueryIndex-1);
			}
			gscReturn = new GuesserAction("q", alQueryContent);
		}
		return gscReturn;
	}
	
	public void setResult(ArrayList<Integer> alResult) {
		alGuess.add(alResult.get(0));
	}

	public String getID() {
		return strID;
	}
	
	//TODO(najaf)
	public Set<Integer> createGuess(){
		return null;
	}
	
	//TODO(riddhi)
	public void updateRules(){
		// using possibilities
		// update rules where possibilities are only 1
		return;
	}
	
	//TODO(akivab)
	public void updatePossibilities(Map<Set<Integer>, Set<Integer>> guess){
		// using the results of some guess, we update possibilities
	}
	
	//TODO(hans)
	public boolean isSolutionReached(){
		return false;
	}
}
