package mapthatset.g6;

import mapthatset.sim.Mapper;
import mapthatset.sim.GuesserAction;

import java.util.ArrayList;
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
  		
  		Random rand = new Random();
  		
  		//TODO: check that we haven't used this mapping before
  		for(int i = 0; i < length; i++) {
			  int randomInt = rand.nextInt(length) + 1;
			  mapping.add(randomInt);
  		}
  		
  		mappingHistory.add(mapping);
  		System.out.println("The mapping is:\n"+mapping);
  		return mapping;
  	}
  	
  	@Override
  	public void updateGuesserAction(GuesserAction gsaGA) {
  	  
  	}
  	
  	@Override
  	public String getID() {
  	  return id;
  	}
}
