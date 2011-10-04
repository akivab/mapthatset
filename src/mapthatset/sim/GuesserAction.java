package mapthatset.sim;
import java.util.ArrayList;


public class GuesserAction		
{
	String strActionType;			// can be either "q" for query or "g" for guess
	ArrayList< Integer > alActionContent;		// either the query indices or the mapping guessing
	boolean blnValidMappingInput = true;
	
	public GuesserAction( String strAction )
	{
		String[] astrAction = strAction.split( ":" );
		strActionType = astrAction[ 0 ];
		if ( !( strActionType.equals( "q" ) || strActionType.equals( "g" ) ) )
		{
			System.out.println( "Action type has to be either 'q' or 'g'. Retry please." );
			blnValidMappingInput = false;
		}
		String[] astrActionContent =astrAction[ 1 ].split( "," );
		alActionContent = new ArrayList< Integer >();
		try
		{
			for ( String strActionContent : astrActionContent )
			{
				alActionContent.add( Integer.parseInt( strActionContent ) );
			}
		}
		catch (NumberFormatException e )
		{
			System.out.println( "The action content part is not valid. Please check and retry." );
			blnValidMappingInput = false;
		}
	}
	
	public GuesserAction( String strActionType, ArrayList< Integer > alActionContent )
	{
		this.strActionType = strActionType;
		this.alActionContent = alActionContent;
	}
	
	public String getType()
	{
		return strActionType;
	}
	
	public ArrayList< Integer > getContent()
	{
		return alActionContent;
	}
	
	public boolean getMappingValid()
	{
		return blnValidMappingInput;
	}
}
