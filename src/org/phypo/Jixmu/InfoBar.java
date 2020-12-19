package org.phypo.Jixmu;


import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 


public class InfoBar  extends HBox {
	
	VBox   cVBox       = new VBox();
	
	Button cTitle      = new Button("Title ");
	Button cArtist     = new Button("Artist");
	Button cAlbum      = new Button("Album ");
	
	int cImgSize  = 64;
	Button cImg        = new Button("");

	//----------------------------------------
	InfoBar( Player iPlayer ){
		getChildren().addAll( cImg, cVBox  );
		cVBox.getChildren().addAll( cTitle,cArtist, cAlbum);	
	}
	//----------------------------------------
	void setInfo( String iTitle, String iArtist, String iAlbum, Image iImg, String iGenre ) {
			cTitle.setText(iTitle);
			cArtist.setText(iArtist);
			cAlbum.setText( iAlbum );
			
			cImgSize = (int) cVBox.getHeight();
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
