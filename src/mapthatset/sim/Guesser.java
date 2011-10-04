package mapthatset.sim;
import java.util.ArrayList;


public abstract class Guesser 
{
	public abstract void startNewMapping( int intMappingLength );
	public abstract GuesserAction nextAction();
	// both query and guess result will be passed back to the guesser by this method.
	// the guesser should interpret the result according to its former action.
	// if it was a query, it is an arraylist with mapping value in sorted order.
	// if it was a guess, the result is an arraylist with single element, -1 means fail, 0 means guess correct
	public abstract void setResult( ArrayList< Integer > alResult );		
	public abstract String getID();
}
