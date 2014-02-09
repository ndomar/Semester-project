package edu.cmu.sphinx.demo.game.GUI;

@SuppressWarnings("serial")
public class TileOutOfBoundsException extends Exception {
	
	public TileOutOfBoundsException()
	{
		 super();
	}
	public TileOutOfBoundsException(String message)
	{
		super(message);
	}

}
