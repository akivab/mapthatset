package mapthatset.sim;

import java.util.ArrayList;
import java.util.Scanner;

public class ManualGuesser extends Guesser 
{
	int intMappingLength;
	String strID = "Manual Guesser";

	@Override
	public void startNewMapping(int intMappingLength) 
	{
		// TODO Auto-generated method stub
		this.intMappingLength = intMappingLength;
		for ( int i = 0; i < 100; i ++ )
		{
			System.out.println();
		}
	}

	@Override
	public GuesserAction nextAction() 
	{
		// TODO Auto-generated method stub
		GuesserAction gsaReturn = null;
		System.out.println();
		System.out.println( "Input your next action? (The mapping is of length " + intMappingLength + ".)" );
		while ( gsaReturn == null || !gsaReturn.getMappingValid() )
		{
			Scanner scnInput = new Scanner( System.in );
			String strQuery = scnInput.nextLine();
			gsaReturn = new GuesserAction( strQuery );
		}
		return gsaReturn;
	}

	@Override
	public void setResult( ArrayList<Integer> alResult ) 
	{
		// TODO Auto-generated method stub
//		if ( alResult.get( 0 ) == -1 )
//		{
//			System.out.println( "Your guess is wrong, the correct answer is: " + MapThatSet.getMapping() );
//		}
//		else if ( alResult.get( 0 ) == 0 )
//		{
//			System.out.println( "Correct! The mapping is: " + MapThatSet.getMapping() );
//		}
//		else
//		{
//			System.out.println( alResult );
//		}
	}

	@Override
	public String getID() 
	{
		// TODO Auto-generated method stub
		return strID;
	}

}
