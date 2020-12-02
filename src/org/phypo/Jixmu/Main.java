package org.phypo.Jixmu;


import javafx.scene.Scene;
import javafx.stage.Stage;


import org.phypo.PPg.PPgFX.AppliFx;
import org.phypo.PPg.PPgUtils.Log;


// exemple https://www.geeksforgeeks.org/javafx-building-a-media-player/

//*********************************************************
public class Main  extends AppliFx {

	static public Main Instance() { return (Main) sInstance; } // hide the mother class fonction

	//-------------------------------------
	Stage       cStage       = null;
	Scene       cPrimScene   = null;
	
	Player      cPlayer      = null; 
	//--------------------------------------
	public Stage       getPrimStage() { return cStage;}
	
	//--------------------------------------
	public void start(final Stage iStage) {

		cStage = iStage;

		//	cPlayer = new Player("file:///home/phipo/Musique/MariSamuelsen–MaxRichter-November-LiveFromTheForbiddenCity_2018.mp3");		
		cPlayer    = new Player(); 
		cPrimScene = new Scene(cPlayer );  

		
		cStage.setScene(cPrimScene);
		cStage.show();

	} 
	//--------------------------------------
	public static void main(String[] iArgs){ 
		Log.SetDebug(1);
		Conf.OpenIni( iArgs );
		launch( iArgs ); 
	} 
} 
//*********************************************************
