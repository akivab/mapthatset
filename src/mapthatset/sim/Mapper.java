package mapthatset.sim;
import java.util.ArrayList;

public abstract class Mapper 
{
	public abstract ArrayList< Integer > startNewMapping( int intMappingLength );
//	public abstract ArrayList< Integer > getNewMapping();
	public abstract void updateGuesserAction( GuesserAction gsaGA );
	public abstract String getID();
}
