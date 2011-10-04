package mapthatset.dumbplayer;

import java.util.ArrayList;
import java.util.Random;
import mapthatset.sim.*;

public class DumbMapper extends Mapper
{
	int intMappingLength;
	String strID = "DumbMapper";
	
	private ArrayList< Integer > getNewMapping() {
		ArrayList< Integer > alNewMapping = new ArrayList< Integer >();
		Random rdmGenerator = new Random();
		for ( int intIndex = 0; intIndex < intMappingLength; intIndex ++ )
		{
			alNewMapping.add( rdmGenerator.nextInt( intMappingLength ) + 1 );
		}
		System.out.println( "The mapping is: " + alNewMapping );
		return alNewMapping;
	}

	@Override
	public void updateGuesserAction(GuesserAction gsaGA) 
	{
		// dumb mapper do nothing here
	}

	@Override
	public ArrayList<Integer> startNewMapping(int intMappingLength) 
	{
		// TODO Auto-generated method stub
		this.intMappingLength = intMappingLength;
		return getNewMapping();
	}

	@Override
	public String getID() 
	{
		// TODO Auto-generated method stub
		return strID;
	}
}
