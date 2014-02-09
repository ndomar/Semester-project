package edu.cmu.sphinx.demo.game.engine;
public interface BoardInterface {
	
	 int getScore(int player);
	 boolean isGameOver(int player);
	 boolean fixTile();
	 boolean switchTile(int tileType);
	 boolean rotateTileClockwise();
	 boolean rotateTileAntiClockwise();

}
