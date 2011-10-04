package mapthatset.dumbplayer;

import java.util.ArrayList;
import mapthatset.sim.*;

public class DumbGuesser extends Guesser
{
	int intMappingLength;
	int intLastQueryIndex = 0;
	ArrayList< Integer > alGuess = new ArrayList< Integer >();
	String strID = "DumbGuesser";
	
	public void startNewMapping( int intMappingLength )
	{
		this.intMappingLength = intMappingLength;
		intLastQueryIndex = 0;
		alGuess = new ArrayList< Integer >();
	}
	
	@Override
	public GuesserAction nextAction()
	{
		intLastQueryIndex ++;
		GuesserAction gscReturn = null;
		if ( intLastQueryIndex > intMappingLength )
		{
			String strGuessing = "";
			for ( int intGuessingElement : alGuess )
			{
				strGuessing += intGuessingElement + ", ";
			}
//			System.out.println( "Guessing: " + strGuessing.substring( 0, strGuessing.length() - 1 ) );
			gscReturn = new GuesserAction( "g", alGuess );
		}
		else
		{
//			System.out.println( "Querying: " + intLastQueryIndex );
			ArrayList< Integer > alQueryContent = new ArrayList< Integer >();
			alQueryContent.add( intLastQueryIndex );
			gscReturn = new GuesserAction( "q", alQueryContent );
		}
		return gscReturn;
	}
	
	@Override
	public void setResult( ArrayList< Integer > alResult )
	{
		alGuess.add( alResult.get( 0 ) );
	}

	@Override
	public String getID() 
	{
		// TODO Auto-generated method stub
		return strID;
	}
}
