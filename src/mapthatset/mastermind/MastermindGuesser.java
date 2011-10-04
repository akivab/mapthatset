package mapthatset.mastermind;

import java.util.ArrayList;
import mapthatset.sim.*;

public class MastermindGuesser extends Guesser {
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
			gscReturn = new GuesserAction("g", alGuess);
		} else {
			ArrayList<Integer> alQueryContent = new ArrayList<Integer>();
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
	
	private int getRandom() {
		
	}

	public void setResult(ArrayList<Integer> alResult) {
		alGuess.add(alResult.get(0));
	}

	public String getID() {
		return strID;
	}
}
