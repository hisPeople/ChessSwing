package edu.neumont.learningChess.swingClient.view;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;


import edu.neumont.learningChess.api.Move;
import edu.neumont.learningChess.api.Location;
import edu.neumont.learningChess.model.ChessBoard;


@SuppressWarnings("serial")
public class BoardDisplay extends JFrame implements MouseListener, MouseMotionListener, IDisplay {
	
	public static final int N_ROWS = ChessBoard.NUMBER_OF_ROWS;
	public static final int N_COLS = ChessBoard.NUMBER_OF_COLUMNS;
	
	JLayeredPane layeredPane;
	JPanel chessBoard;
	BoardDisplayPiece chessPiece;
	int xAdjustment;
	int yAdjustment;
	
	private ArrayList<IDisplay.IMoveHandler> moveHandlers = new ArrayList<IDisplay.IMoveHandler>();

	public BoardDisplay() {
		this.setTitle("Chess");
		Dimension boardSize = new Dimension(600, 600);

		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane);
		layeredPane.setPreferredSize(boardSize);
		layeredPane.addMouseListener(this);
		layeredPane.addMouseMotionListener(this);

		chessBoard = new JPanel();
		layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
		chessBoard.setLayout(new GridLayout(N_ROWS, N_COLS));
		chessBoard.setPreferredSize(boardSize);
		chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

		Color squareColor = Color.blue;
		
		for (int row = 0; row < N_ROWS; row++) {
			
			squareColor = getOtherColor(squareColor);
			
			for (int col = 0; col < N_COLS; col++) {				
				JPanel square = new JPanel(new BorderLayout());
				square.setBackground(squareColor);
				chessBoard.add(square);

				squareColor = getOtherColor(squareColor);
			}
		}
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setResizable(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void addMoveHandler(IDisplay.IMoveHandler handler) {
		moveHandlers.add(handler);
	}
	
	public void removeMoveHandler(IDisplay.IMoveHandler handler) {
		moveHandlers.remove(handler);
	}
	
	private boolean notifyHandlers(Move move) {
		boolean canHandle = true;
		for (Iterator<IDisplay.IMoveHandler> i = moveHandlers.iterator(); canHandle && i.hasNext(); ) {
			IDisplay.IMoveHandler handler = i.next();
			canHandle = handler.handleMove(move);
		}
		return canHandle;
	}
	
	public void placePiece(IDisplay.Piece piece, Location location) {
		piece.setVisible(false);
		JPanel panel = (JPanel) chessBoard.getComponent(getIndexFromLocation(location));
		piece.setPieceLocation(location);
		panel.add((Component) piece);		
		piece.setVisible(true);
	}
	
	public IDisplay.Piece removePiece(Location location) {
		JPanel panel = (JPanel) chessBoard.getComponent(getIndexFromLocation(location));
		IDisplay.Piece piece = (IDisplay.Piece) panel.getComponent(0);
		piece.setVisible(false);
		panel.remove(0);		
		piece.setPieceLocation(null);
		piece.setVisible(true);
		return piece;
	}
	
	private int getIndexFromLocation(Location location) {
		return ((N_ROWS - 1 - location.getRow()) * N_COLS) + location.getColumn();
	}
	
	private Color getOtherColor(Color squareColor) {
		return (squareColor == Color.white)? Color.blue: Color.white;
	}
	
	
	public void notifyCheck(boolean isWhite) {
		this.setTitle((isWhite?"White's move":"Black's move")+" check");
		//JOptionPane.showMessageDialog(this.getParent(), ((isWhite)?"White":"Black") + " is in check");
	}
	
	public void notifyCheckmate(boolean isWhite) {
		this.setTitle("Checkmate!  "+((isWhite)?"White":"Black")+" wins");
		//JOptionPane.showMessageDialog(this.getParent(), "Checkmate!  "+((isWhite)?"White":"Black")+" wins");
	}
	
	public void notifyStalemate() {
		this.setTitle("Stalemate");
		//JOptionPane.showMessageDialog(this.getParent(), "Stalemate");
	}
	
	public void promptForMove(boolean isWhite) {
		this.setTitle(isWhite?"White's move":"Black's move");
	}
	
	public void mousePressed(MouseEvent e) {
		chessPiece = null;
		Component component = chessBoard.findComponentAt(e.getX(), e.getY());

		if (component instanceof BoardDisplayPiece) {

			Container parent = component.getParent();
			Point parentLocation = parent.getLocation();
			xAdjustment = parentLocation.x - e.getX();
			yAdjustment = parentLocation.y - e.getY();
			chessPiece = (BoardDisplayPiece) component;
			chessPiece.setLocation(e.getX() + xAdjustment, e.getY()+ yAdjustment);
			chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
			parent.remove(0);
			layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
		}
	}

	public void mouseDragged(MouseEvent me) {
		if (chessPiece != null) {
			chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (chessPiece != null) {
			if (releaseIsInBounds(e)) {

				chessPiece.setVisible(false);
				Component component = chessBoard.findComponentAt(e.getX(), e.getY());
				Container parent = (component instanceof BoardDisplayPiece)?component.getParent(): (Container) component;
				
				int row = (parent == null)? -1: N_ROWS-1 - (e.getY() / parent.getHeight());
				int col = (parent == null)? -1: e.getX() / parent.getWidth();
				Location targetLocation = new Location(row, col);
				
				Move move = new Move(chessPiece.getPieceLocation(), targetLocation);
				
				chessPiece.getParent().remove(chessPiece);
				replacePiece();
				
				boolean canHandle = notifyHandlers(move);
				if (!canHandle) {
//					replacePiece();
				}
			} else { // release was out of bounds
				replacePiece();
			}

			chessPiece.setVisible(true);
			chessPiece = null;
		}
	}
	
	private void replacePiece() {
		JPanel panel = (JPanel) chessBoard.getComponent(getIndexFromLocation(chessPiece.getPieceLocation()));
		panel.add(chessPiece);		
	}
	
	private boolean releaseIsInBounds(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		return (x>=0) && (x<getWidth()) && (y>=0) && (y<getHeight());
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}