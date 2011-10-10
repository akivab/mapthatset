package mapthatset.g6;

import mapthatset.sim.Mapper;
import mapthatset.sim.GuesserAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class MastermindMapper extends Mapper {
    String id = "G6 Mapper";
    int length;
  	ArrayList<GuesserAction> guesserHistory;
  	ArrayList<Integer> mapping;
  	ArrayList<ArrayList<Integer>> mappingHistory;
    
    public MastermindMapper() {
  		guesserHistory = new ArrayList<GuesserAction>();
  		mappingHistory = new ArrayList<ArrayList<Integer>>();
    }
    
    @Override
  	public ArrayList<Integer> startNewMapping(int intMappingLength) {
  	  length = intMappingLength;
  		
  		mapping = new ArrayList<Integer>(length);

  		//TODO: check that we haven't used this mapping before
  		for(int i = 0; i < length; i++) mapping.add(i);
			
			mapping = shuffle(mapping);
  		
  		mappingHistory.add(mapping);
  		System.out.println("The mapping is:\n"+mapping);
  		return mapping;
  	}
		
    /**
     * An implementation of the Fisher-Yates random shuffle.
     */
    private ArrayList<Integer> shuffle(ArrayList<Integer> mapping) {
    	ArrayList<Integer> shuffled = new ArrayList<Integer>(mapping.size());
    	for(Integer i : mapping) shuffled.add(i);
    	
    	Random rand = new Random();
    	for(int i = 0; i < mapping.size(); i++) {
    		int randomInt = rand.nextInt(length) + 1;
    		if(randomInt != i) {
    			Integer temp = shuffled.get(i);
    			shuffled.set(i, shuffled.get(randomInt));
    			shuffled.set(randomInt, temp);
    		}
    	}
    	return shuffled;
    }
  	
  	@Override
  	public void updateGuesserAction(GuesserAction gsaGA) {
  	  
  	}
  	
  	@Override
  	public String getID() {
  	  return id;
  	}
}
