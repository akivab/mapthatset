package mapthatset.sim;

import java.util.ArrayList;
import java.util.Scanner;

public class ManualMapper extends Mapper 
{
	int intMappingLength;
	String strID = "Manual Mapper";

	private ArrayList<Integer> getNewMapping() 
	{
		// TODO Auto-generated method stub
		ArrayList< Integer > alReturn = new ArrayList< Integer >();
		
		while ( alReturn.size() != intMappingLength )
		{
			System.out.println( "Input a new mapping of length " + intMappingLength + ", separated by ',', no space" );
			Scanner scnNewMapping = new Scanner( System.in );
			String strNewMapping = scnNewMapping.nextLine();
			System.out.println();
			String[] astrNewMapping = strNewMapping.split( "," );
			for ( String strMappingElement : astrNewMapping )
			{
				alReturn.add( Integer.parseInt( strMappingElement ) );
			}
		}
		return alReturn;
	}

	@Override
	public void updateGuesserAction(GuesserAction gsaGA)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Integer> startNewMapping( int intMappingLength ) 
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
