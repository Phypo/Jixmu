package org.phypo.Jixmu;


import java.awt.Font;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 


public class InfoBar  extends HBox {
	
	HBox   cBox       = new HBox();
	VBox   cVBox      = new VBox();

	Button cTitle      = new Button("Title ");
	Button cArtist     = new Button("Artist");
	Button cAlbum      = new Button("Album ");
	Button cYear       = new Button("Year ");
	Button cTrack       = new Button("Track ");

	int cImgSize  = 64;
	Button cImg        = new Button("");

	//----------------------------------------
	InfoBar( MediaBar iBar ){
		cBox.getChildren().addAll( cTitle,cArtist,cAlbum,cYear,cTrack);	
		cVBox.getChildren().addAll( cBox, iBar);
		getChildren().addAll( cImg,  cVBox );
		cTitle.setStyle("-fx-font-size:18");
	}
	//----------------------------------------
	void setInfo( String iTitle, String iArtist, String iAlbum, String iYear,  String iGenre ) {
			cTitle.setText(iTitle);
			cArtist.setText(iArtist);
			cAlbum.setText( iAlbum );
			cYear.setText( iYear );
	}
	//----------------------------------------
	void setTrack( String iStr) {
		cTrack.setText( iStr);
	}
	//----------------------------------------
	void setImg(  Image iImg ) {
			cImgSize = 64;
			cImg.setMinHeight(cImgSize);
			cImg.setMinWidth(cImgSize);
			cImg.setMaxHeight(cImgSize);
			cImg.setMaxWidth(cImgSize);
			if( iImg != null ) {
				ImageView lView = new ImageView(iImg);
				lView.setPreserveRatio(true);
				lView.setFitHeight(cImgSize);
				lView.setFitWidth(cImgSize);
				cImg.setGraphic(lView);
			} else {
				cImg.setGraphic( new ImageView());
			}

	}
	//----------------------------------------
}
