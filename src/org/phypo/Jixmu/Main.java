package org.phypo.Jixmu;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Random;

import org.phypo.PPg.PPgFX.AppliFx;
import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;


// exemple https://www.geeksforgeeks.org/javafx-building-a-media-player/

//*********************************************************
public class Main  extends AppliFx {

	static public Main Instance() { return (Main) sInstance; } // hide the mother class fonction
	

	protected Random cRandom = new Random();
	public long getRandomLong() { return cRandom.nextLong(); }  
	

	//-------------------------------------
	Stage       cStage       = null;
	Scene       cPrimScene   = null;
	
	Player      cPlayer      = null; 
	//--------------------------------------
	public Stage       getPrimStage() { return cStage;}
	
	//--------------------------------------
	public void start(final Stage iStage) {
		init("Jixmu", "0.1", "phypo.org", "2020/12/05", "ppoupon@phypo.org");

		cStage = iStage;

		//	cPlayer = new Player("file:///home/phipo/Musique/MariSamuelsen–MaxRichter-November-LiveFromTheForbiddenCity_2018.mp3");		
		cPlayer    = new Player(); 
		cPrimScene = new Scene(cPlayer );  
		
		iStage.setOnCloseRequest( (WindowEvent iEv) -> {cPlayer.quit();});
		
		FxHelper.SetIcon( cStage, Conf.sIconeAppli );
		
		cStage.setScene(cPrimScene);
		cStage.show();

	} 
	//--------------------------------------
	public static void main(String[] iArgs){ 

		Log.SetDebug(1);
		Conf.ReadIni( iArgs );
		launch( iArgs ); 
	} 
} 
//*********************************************************
