package mapthatset.sim;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;


public class MapThatSet 
{
	static int intMode = 0;
	static Mapping mpnCurrent;
	static int intMaxQuery;
	static int intMaxQueryOverLength = 10;
	static int intWrongOverMax = 1;
	static int intLinesToScrollAfterNewMapping = 100;
	static int intMappingLength = 0;
	static int intRoundToPlay = 3;
	
	static Guesser gsrGuesser;
	static Mapper mppMapper;
	
	static ArrayList< Integer > alMapping;
	static ArrayList< Guesser > alGuessers = new ArrayList< Guesser >();
	static ArrayList< Mapper > alMappers = new ArrayList< Mapper >();
	
	
//	static Map< 
	
	public static void main( String[] args )
	{
		Scanner scnMappingLength = new Scanner( System.in );
		System.out.println( "Please input the length of the mapping" );
		String strMappingLength = scnMappingLength.nextLine();
		System.out.println();
		try
		{
			intMappingLength = Integer.parseInt( strMappingLength );
			intMaxQuery = intMaxQueryOverLength * intMappingLength;
		}
		catch ( NumberFormatException e )
		{
			System.out.println( "Length has to be integer" );
		}
		
		initialize();
		
		for ( String strArg : args )
		{
			if ( strArg.equals( "MM" ) )			// manual mapper
			{
				useManualMapper();
			}
			else if ( strArg.equals( "MG" ) )		// mannual guesser
			{
				useManualGuesser();
			}
			else
			{
				System.out.println( "Unknow Parameter" );
				return;
			}
		}
		// use manual player if the player class list is empty
		if ( alMappers.isEmpty() )
		{
			useManualMapper();
		}
		if ( alGuessers.isEmpty() )
		{
			useManualGuesser();
		}
		
//		Map< String, Map< String, Integer > > mpScore = new Map< String, Map< String, Integer > >(); 
		for ( Guesser gsrCurrent : alGuessers )
		{
			for ( Mapper mprCurrent : alMappers )
			{
				ArrayList< Mapping > alMappings = new ArrayList< Mapping >();
				for ( int intRound = 0; intRound < intRoundToPlay; intRound ++ )
				{
					boolean blnMappingValid = false;
					while ( !blnMappingValid )
					{
						blnMappingValid = true;
						alMapping = mprCurrent.startNewMapping( intMappingLength );
						for ( int intMapping : alMapping )
						{
							if ( intMapping > intMappingLength || intMapping < 1 )
							{
								System.out.println( "Number out of range.\n" );
								blnMappingValid = false;
							}
						}
					}
					gsrCurrent.startNewMapping( intMappingLength );
					
					mpnCurrent = new Mapping( alMapping );
					
					GuesserAction gsaGA = null;
					String strActionType = null;
					ArrayList< Integer > alActionContent = null;
					
					int intRoundScore = 0;			// the score for exceeding maximum steps. this is changed only if a guess is made.
					for ( int intScore = 0; intScore < intMaxQuery; intScore ++ )
					{
						gsaGA = gsrCurrent.nextAction();
						strActionType = gsaGA.getType();
						alActionContent = gsaGA.getContent();
						
						mprCurrent.updateGuesserAction( gsaGA );
						
						if ( strActionType.equals( "g" ) )
						{
							ArrayList< Integer > alGuessResult = new ArrayList< Integer >();
							boolean blnGuessCorrect = mpnCurrent.guess( alActionContent );
							String strGuessResult = blnGuessCorrect ? "Correct" : "Wrong";
							System.out.println();
							System.out.println( "Guess: " + alActionContent + " ::: It is " + strGuessResult );
							System.out.println();
							if ( !blnGuessCorrect )			// guess wrong
							{
								intRoundScore = intWrongOverMax * intMaxQuery;
								alGuessResult.add( -1 );			// the result is arraylist with single element -1
							}
							else
							{
								intRoundScore = intScore;
								alGuessResult.add( 0 );			// the result is arraylist with single element 0
							}
							gsrCurrent.setResult( alGuessResult );
							break;
						}
						else if ( strActionType.equals( "q" ) )
						{
							ArrayList< Integer > alQueryResult = mpnCurrent.query( alActionContent );
							gsrCurrent.setResult( alQueryResult );					
							System.out.print( alActionContent + " --> " + alQueryResult + "\t" );
						}
					}
					mpnCurrent.setScore( intRoundScore );
					alMappings.add( mpnCurrent );
				}
				
				System.out.println( "The scores between " + gsrCurrent.getID() + " and " + mprCurrent.getID() + " are:" );
				for ( Mapping mpnMapping : alMappings )
				{
					System.out.print( mpnMapping.getMapping() + " : " + mpnMapping.getScore() + "\t" );
				}
			}
		}	
	}

	private static void useManualMapper()
	{
		alMappers.clear();
		Mapper mprManual = new ManualMapper();
		alMappers.add( mprManual );
		intRoundToPlay = 1;
	}
	
	private static void useManualGuesser()
	{
		alGuessers.clear();
		Guesser gsrManual = new ManualGuesser();
		alGuessers.add( gsrManual );
		intRoundToPlay = 1;
	}
	
	private static void initialize()
	{
		// load all mappers classes
		File filMappersClasses = new File( "MappersClasses.txt" );
		Scanner scnMappersClasses = null;
		try 
		{
			scnMappersClasses = new Scanner( filMappersClasses );
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			System.out.println( "Failed to load Mappers Classes File" );
			e1.printStackTrace();
		}
		while ( scnMappersClasses.hasNextLine()) 
		{
			String strMapperClass = scnMappersClasses.nextLine();
			
			if ( strMapperClass.startsWith( "//" ) )
			{
				continue;
			}
			
			try 
			{
				Mapper mprNew = ( Mapper ) Class.forName( strMapperClass ).newInstance();
//				mprNew.startNewMapping( intMappingLength );
				alMappers.add( mprNew );
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println( "Problem loading mappers' classes" );
//				log.error("[Configuration] Class not found: " + t);
			} 
			catch (InstantiationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// load all guesser classes
		File filGuessersClasses = new File( "GuessersClasses.txt" );
		Scanner scnGuessersClasses = null;
		try 
		{
			scnGuessersClasses = new Scanner( filGuessersClasses );
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			System.out.println( "Failed to load Guessers Classes File" );
			e1.printStackTrace();
		}
		while ( scnGuessersClasses.hasNextLine()) 
		{
			String strGuesserClass = scnGuessersClasses.nextLine();
			
			if ( strGuesserClass.startsWith( "//" ) )
			{
				continue;
			}
			
			try 
			{
				Guesser gsrNew = ( Guesser ) Class.forName( strGuesserClass ).newInstance();
//				gsrNew.startNewMapping( intMappingLength );
				alGuessers.add( gsrNew );
			} 
			catch (ClassNotFoundException e) 
			{
//				log.error("[Configuration] Class not found: " + t);
			} 
			catch (InstantiationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}	
	
	static ArrayList< Integer > getMapping()
	{
		return alMapping;
	}
	
//	private static GuesserAction whatNext()
//	{
//		GuesserAction gsaReturn = null;
//		if ( ( intMode % 2 ) == 1 )			// mannual guesser
//		{
//			System.out.println( "What do you want? (The mapping is of length " + mpnCurrent.getMappingLength() + ".)" );
//			while ( gsaReturn == null || !gsaReturn.getMappingValid() )
//			{
//				Scanner scnInput = new Scanner( System.in );
//				String strQuery = scnInput.nextLine();
//				gsaReturn = new GuesserAction( strQuery );
//			}
//		}
//		else 
//		{
//			gsaReturn = gsrGuesser.nextAction();
//		}
//		
//		if ( intMode < 2 )					// pass the guesser action to non-manual-mapper
//		{
//			mppMapper.updateGuesserAction( gsaReturn );
//		}
//		return gsaReturn;
//	}
	
//	private static void processResult( ArrayList< Integer > alResult )
//	{
//		if ( ( intMode % 2 ) == 0 )						// not manual guesser mode
//		{
//			gsrGuesser.setResult( alResult );			// if it was a query, the query result arraylist is passed to the guesser
//		}
//		else
//		{
//			if ( alResult.get( 0 ) == -1 )
//			{
//				System.out.println( "Your guess is wrong, the correct answer is: " + alMapping );
//			}
//			else if ( alResult.get( 0 ) == 0 )
//			{
//				System.out.println( "Correct! The mapping is: " + alMapping );
//			}
//			else
//			{
//				System.out.println( alResult );
//			}
//		}
//	}
	
//	private static void printScore( int intRoundScore )
//	{
//		if ( ( intMode % 2 ) == 1 )
//		{
//			System.out.println( "This round's score is: " + intRoundScore );
//		}
//	}
	
}

