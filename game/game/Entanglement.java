/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.game;

import edu.cmu.sphinx.demo.game.GUI.Game;
import edu.cmu.sphinx.demo.game.GUI.MainFrame;
import edu.cmu.sphinx.demo.game.GUI.PlayGamePanel;
import edu.cmu.sphinx.demo.game.engine.*;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * A simple HelloWorld demo showing a simple speech application built using
 * Sphinx-4. This application uses the Sphinx-4 endpointer, which automatically
 * segments incoming audio into utterances and silences.
 */
public class Entanglement {

	static boolean inGame = false;
	static MainFrame m = null;
	static Game game = null;

	public static int getNumberOfPlayers(String in) {
		if (in.toLowerCase().contains("one"))
			return 1;
		if (in.toLowerCase().contains("two"))
			return 2;
		if (in.toLowerCase().contains("three"))
			return 3;
		return 4;
	}

	public static void main(String[] args) {
		ConfigurationManager cm;

		if (args.length > 0) {
			cm = new ConfigurationManager(args[0]);
		} else {
			cm = new ConfigurationManager(
					Entanglement.class.getResource("game.config.xml"));
		}

		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();

		// start the microphone or exit if the programm if this is not possible
		Microphone microphone = (Microphone) cm.lookup("microphone");
		if (!microphone.startRecording()) {
			System.out.println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}

		System.out.println("Say Something");
		// loop the recognition until the programm exits.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {

				//String in = br.readLine();
				Result result = recognizer.recognize();
				// String result = "";
				if (result != null) {
					String in = result.getBestFinalResultNoFiller();
					System.out.println("You said: " + in + '\n');
					if (in.toLowerCase().contains("play")) {
						if (!inGame) {
							inGame = true;
							System.out.println("pressing");
							m = new MainFrame();
							m.hp.playGame.doClick(1000);
							m.hp.clearPanel();
							PlayGamePanel next = new PlayGamePanel(m.hp.frame,
									m.hp.bc);
							next.setBounds(0, 0, m.hp.frame.getWidth(),
									m.hp.frame.getHeight());
							m.hp.frame.setContentPane(next);
							m.hp.revalidate();
							int players = getNumberOfPlayers(in);
							game = new Game(m.hp.frame, players, m.hp.bc);
							game.setBounds(0, 0, m.hp.frame.getWidth(),
									m.hp.frame.getHeight());
							m.hp.frame.setContentPane(game);
							m.hp.revalidate();
							m.hp.repaint();
							
						}
					}
					if (in.equalsIgnoreCase("fix tile")) {
						if (inGame) {
							System.out.println("fixing tile");
							int activePlayer = game.activeGame.activeBoard
									.getActivePlayer();
							System.out.println("players = "
									+ game.activeGame.activeBoard
											.getNumberOfPlayers());
							int activeTile = game.activeGame.activeBoard
									.getPlayer(activePlayer).getActiveTile();
							int row = game.activeGame.activeBoard
									.getTilePosition(activeTile)[0];
							int column = game.activeGame.activeBoard
									.getTilePosition(activeTile)[1];

							int activeOpening = game.activeGame.activeBoard
									.getPlayer(activePlayer).getOpening();
							game.activeGame.tiles[row][column].fixDraw(
									activeOpening, activePlayer);
							game.activeGame.activeBoard.fixTile();
							java.awt.Toolkit.getDefaultToolkit().beep();

							game.activeGame.drawLoopBack(activePlayer);
							System.out.println("tile fixed");
							if (!game.activeGame.activeBoard
									.isGameOver(activePlayer)) {
								game.activeGame.drawActiveTiles();

							}

							game.activeGame.game.updateScores();
							game.activeGame.game.checkGameOver();
							inGame = !game.activeGame.getActiveBoard()
									.isAllGameOver();
							System.out.println("IN GAME? " + inGame);
						}
					}

					if (in.equalsIgnoreCase("rotate")) {
						int player = game.activeGame.getActiveBoard()
								.getActivePlayer();
						int tile = game.activeGame.getActiveBoard()
								.getPlayer(player).getActiveTile();
						int row = tile
								/ game.activeGame.getActiveBoard().getWidth();
						int column = tile
								% game.activeGame.getActiveBoard().getWidth();
						game.activeGame.getTile(new int[] { row, column })
								.rotateClockwise();
						game.activeGame.getActiveBoard().rotateTileClockwise();
					}
				} else {
					System.out.println("I can't hear what you said.\n");
				}

				// System.out.println("Start speaking. Press Ctrl-C to quit.\n");

				// Result result = recognizer.recognize();

				// if (result != null) {
				// String resultText = result.getBestFinalResultNoFiller();
				// System.out.println("You said: " + resultText + '\n');
				// Double output = sp.parse(resultText);
				// refresh values here
				// Iterator it = sp.variableList.entrySet().iterator();
				// int i = 0;
				// while (it.hasNext()) {
				// Map.Entry pairs = (Map.Entry)it.next();
				// theInterface.variables[i].setText(pairs.getKey() + " = " +
				// pairs.getValue());
				// it.remove(); // avoids a ConcurrentModificationException
				// i = i + 1;
				// }
				// theInterface.variables[26].setText("Output = " + output);
				// theInterface.result.setText("");
				// theInterface.result.setText(output + "");
				// } else {
				// System.out.println("I can't hear what you said.\n");
				// }
			}
		} catch (Exception e) {

		}

	}
}
