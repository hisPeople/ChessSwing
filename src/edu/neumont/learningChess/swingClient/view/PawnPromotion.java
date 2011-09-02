package edu.neumont.learningChess.swingClient.view;

import edu.neumont.learningChess.api.IPromotionListener;
import edu.neumont.learningChess.api.PieceType;

public class PawnPromotion implements IPromotionListener {

	@Override
	public PieceType getPromotionPieceType() {
		return PieceType.QUEEN;
	}

}
