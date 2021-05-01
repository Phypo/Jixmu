package org.phypo.Jixmu;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;


import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Track;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

//**********************************************************
public class Player extends BorderPane // Player class extend BorderPane 
// in order to divide the media 
// player into regions 
{ 
	static File sDir = new File( "/home/phipo/Musique");
	//--------------------------------------

	FileChooser      cFileChooser     = null; 
	DirectoryChooser cDirChooser      = null; 
	String           cCurrentPlayList = "Default.jixmu";

	public enum Error { NO_ERROR, MEDIAPLAYER, MALFORMED_URL, MEDIA_UNSUPPORTED, MEDIA_ERROR};



	VBox        cTopBox  = null;
	MenuBar     cMenu    = null; 

	MediaBar    cMedBar     = null;
	InfoBar     cInfoBar    = null;
	CmdBar      cCmdBar     = null;

	Media       cMedia   = null; 
	MediaPlayer cMedPlayer  = null;
	Viewer      cViewer      = null;
	TableRecords cTableRecords    = null;


	enum   RepeatMode{ NO_REPEAT, REPEAT_TRACK, REPEAT_ALL };
	RepeatMode cRepeatMode = RepeatMode.NO_REPEAT;
	RepeatMode getRepeatMode() { return cRepeatMode ;}
	void       setRepeatMode( RepeatMode iRepeatMode) { cRepeatMode= iRepeatMode; }



	public MediaPlayer getPlayer() { return cMedPlayer;} 

	int cCurrentRecordPos = 0;

	//--------------------------------------
	public Player() {


		cTopBox = new VBox(2);
		setTop(cTopBox);  

		cTableRecords = new TableRecords( Player.this );
		cTableRecords.getPane().setVisible(false);					
		setCenter( cTableRecords.getPane());

		// ==== Menu =====
		cMenu              = new MenuBar(); 
		cTopBox.getChildren().add( cMenu );
		// --=== FILE ===--
		Menu     lMenuFile = new Menu("File"); 

		FxHelper.AddMenuItem( lMenuFile,"Clear", (ActionEvent e) -> { clearAll(); });

		FxHelper.AddMenuSeparator( lMenuFile);

		cMenu.getMenus().add(lMenuFile); 
		initReadFileChooser(lMenuFile  );
		initDirChooser( lMenuFile  );

		//===================
		FxHelper.AddMenuItem( lMenuFile,"Add playlist ...", (ActionEvent e) -> { 
			cFileChooser = new FileChooser();
			cFileChooser.setInitialDirectory(sDir);
			cFileChooser.setTitle( "Adding playlist");
			cFileChooser.getExtensionFilters().add( new ExtensionFilter("Jixmu playlist", "*.jixmu"));
			// A FAIRE showOpenMultipleDialog
			List<File> lFiles = cFileChooser.showOpenMultipleDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFiles != null) { 	
				for( File lFile : lFiles) {
					//Platform.runLater(() -> { cTableRecords.readPlayList(lFile, false); });
					cTableRecords.readPlayList(lFile, false);
				}
				cTableRecords.save();
			}
		});


		FxHelper.AddMenuSeparator( lMenuFile);
		//===================
		FxHelper.AddMenuItem( lMenuFile,"Save playlist", (ActionEvent e) ->{ 
			cTableRecords.writePlaylist( cCurrentPlayList, false);
		});
		FxHelper.AddMenuItem( lMenuFile,"Save playlist As ...", (ActionEvent e) ->{ 
			cFileChooser = new FileChooser();
			cFileChooser.setInitialDirectory(sDir);
			cFileChooser.setTitle( "Save playlist");
			cFileChooser.getExtensionFilters().add( new ExtensionFilter("Jixmu playlist", "*.jixmu"));
			File lFile = cFileChooser.showSaveDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFile != null) { 	
				String lName=null;
				try {
					lName = Conf.RemoveFileExtension( lFile.getCanonicalPath() );
					Log.Dbg( "Save playlist Name : " + lName);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if( lName == null ) {
					Alert lAlert = new Alert(AlertType.ERROR, "Bad file name" );
					lAlert.showAndWait();	   
					return;
				}
				lName += ".jixmu";
				cTableRecords.writePlaylist( lName, false);
				cCurrentPlayList = lName;
			}	
		});	
		//===================

		FxHelper.AddMenuSeparator( lMenuFile);
		//===================
		FxHelper.AddMenuItem(lMenuFile, "Quit", (ActionEvent)->{ 
			quit();
		});	

		// --=== Util ===--
		Menu     lMenuUtil= new Menu("Util");
		cMenu.getMenus().add(lMenuUtil); 

		FxHelper.AddMenuItem( lMenuUtil, "new Randomize", (ActionEvent e) -> {
			cTableRecords.randomize(); 								
		});

		cTableRecords = new TableRecords(this);
		cTableRecords.getPane().setVisible(true);					
		setCenter( cTableRecords.getPane() );


		cTableRecords.load();
		// --=== VIEW ===--
		Menu lMenuView= new Menu("View");
		cMenu.getMenus().add(lMenuView); 

		FxHelper.AddMenuItem( lMenuView, "Records", (ActionEvent e) -> {
			if( cTableRecords.getPane().isVisible() ) {
				cTableRecords.getPane().setVisible(false);
				//setCenter(null);
				getTop().setManaged(false);
				cTableRecords.getPane().setManaged(false);
				//		Main.Instance().cStage.sizeToScene();
			} else {
				cTableRecords.getPane().setVisible(true);					
				cTableRecords.getPane().setManaged(true);
				getTop().setManaged(true);
				//		Main.Instance().cStage.sizeToScene();
				//setCenter( cTableRecords );
			}
		});

		cTableRecords = new TableRecords(this);
		cTableRecords.getPane().setVisible(true);					
		setCenter( cTableRecords.getPane());


		cTableRecords.load();
		//--------------------------------------


		// ==== Menu =====



		cMedBar  = new MediaBar(this);
		cCmdBar = new CmdBar(this);
		cInfoBar = new InfoBar( cMedBar );
		cTopBox.getChildren().addAll(cCmdBar, cInfoBar ); // Setting the MediaBar at bottom 
		//		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 

		if( Conf.sAutoPlay ) {
			if( play( cTableRecords.getCurrentRecord(), Conf.sCurrentMediaTime ) == false ) {
				//next();
			}
		}

	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	void quit() {
		cTableRecords.save();				
		Conf.SaveIni();
		Platform.exit(); //	System.exit(0);			
	}
	//--------------------------------------
	void setVolume(double iVol) {
		if( iVol < 0 ) iVol = 0;
		else if( iVol > 1) iVol =1;

		Conf.sVolume = iVol;
		if( getPlayer() !=null )
			getPlayer().setVolume( Conf.sVolume );		
	}
	//--------------------------------------
	double getVolume() { return Conf.sVolume;}
	//--------------------------------------
	void setBalance(double iBal) {
		if( iBal < -1 ) iBal = -1;
		else if( iBal > 1) iBal =1;

		Conf.sBalance = iBal;
		if( getPlayer() !=null )
			getPlayer().setBalance( Conf.sBalance );		
	}	





	double getBalance() { return Conf.sBalance; }
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	/*
	MyRecord addFile2Records( File iFile ) {
		MyRecord lRecord = new MyRecord( iFile );
		return add2Records( lRecord );
	}
	//--------------------------------------
	MyRecord add2Records( MyRecord iRecord ) {

		cTableRecords.addLine( iRecord);	
		return iRecord;
	}*/
	//--------------------------------------

	//--------------------------------------
	void initReadFileChooser( Menu iMenu ) {
		cFileChooser = new FileChooser();
		cFileChooser.setInitialDirectory(sDir);
		cFileChooser.setTitle( "Adding file");
		cFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.m4a"),
				new ExtensionFilter("Video Files", "*.mp4"));
		//         new ExtensionFilter("All Files", "*.*"));

		FxHelper.AddMenuItem(iMenu,"Add file ...",(ActionEvent e) -> { 
			List<File> lFiles = cFileChooser.showOpenMultipleDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFiles != null) { 
				for( File lFile : lFiles) {					
					cTableRecords.addFile(lFile, 0 );
				}				
				cTableRecords.writeSize2Foot("");
				cTableRecords.save();				
			} 
		});
	}
	//--------------------------------------
	void initDirChooser( Menu iMenu ) {
		cDirChooser = new DirectoryChooser();
		cDirChooser.setInitialDirectory(sDir);
		cDirChooser.setTitle( "Adding folder ...");

		FxHelper.AddMenuItem(iMenu,"Add folder", (ActionEvent e) ->{ 

			Log.Dbg( "Player lItemOpen.setOnAction ");
			File lFile = cDirChooser.showDialog( Main.Instance().getPrimStage()); 
			sDir = cDirChooser.getInitialDirectory();		        
			Log.Dbg(sDir.getAbsolutePath());
			// Choosing the file to play 
			if (lFile != null) { 
				//play( addFile2Records(lFile ) );
				cTableRecords.addFile(lFile, 0 );
				Platform.runLater(() -> { cTableRecords.save(); });
				cTableRecords.writeSize2Foot("");
			} 
		});
	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	public void clearAll() {
		pause();
		cMedPlayer.stop();
		cMedPlayer.dispose();
		cMedPlayer = null;
		cTableRecords.clearAll();
	}
	//--------------------------------------
	public boolean play( MyRecord iRecord, double iPos ) {

		if( iRecord == null ) {
			return false;
		} 

		URI iURI = iRecord.cURI;
		try {
			cMedia  = new Media(iURI.toURL().toExternalForm());
			cCmdBar.setInfo( iRecord.getName());
			cInfoBar.setInfo( null , null, null, null, null );

			//		String lDuration = ((Double)cMedia.getDuration().toSeconds()).toString();

			ObservableMap<String, Object> lMeta = cMedia.getMetadata();
			ObservableList<Track> lTracks = cMedia.getTracks();
			StringBuilder lStr = new StringBuilder();
			for( Track lTrack : lTracks) {
				lStr.append( lTrack.toString() );
				lStr.append(" ");
			}
			cInfoBar.setTrack( lStr.toString());
			
			Main.Instance().getPrimStage().setTitle( lStr.toString());
			cInfoBar.setInfo(  lStr.toString(),"", "", "",  "" );

			lMeta.addListener( (MapChangeListener.Change<? extends String, ? extends Object> chg) -> {
				//				StringBuilder lLabelTxt = new StringBuilder();				
				String lArtist = (String) lMeta.get("artist");
				String lAlbum  = (String) lMeta.get("album");
				String lGenre  = (String) lMeta.get("genre");
				String lTitle  = (String) lMeta.get("title");
				Image lImage   = (Image) lMeta.get("image");
				Integer lIntYear = (Integer)lMeta.get("year");
				String lYear    = null;
				if( lIntYear != null ) {
					lYear = lIntYear.toString();
				}

				Main.Instance().getPrimStage().setTitle( lTitle +" - "+ lArtist+" - "+lAlbum+" - "+ lYear+" - "+  lGenre);

				cInfoBar.setInfo(  lTitle,lArtist, lAlbum, lYear,  lGenre );
				cInfoBar.setImg(  lImage  );
			});								

		} catch (MalformedURLException e) {
			iRecord.setError( Error.MALFORMED_URL, e.getMessage());
			// TODO Auto-generated catch block
			String lErr = e.getMessage();
			Log.Err( "Error.MALFORMED_URL - " + lErr );
			//	Alert lAlert = new Alert(AlertType.ERROR, lErr );
			//lAlert.showAndWait();	   
			return false;
		}
		catch (MediaException e ){
			iRecord.setError( Error.MEDIA_UNSUPPORTED, e.getMessage());
			String lErr = e.getMessage();
			Log.Err( "Error.MEDIA_UNSUPPORTED - " + lErr );
			//		Alert lAlert = new Alert(AlertType.ERROR, lErr );
			//		lAlert.showAndWait();	   
			return false;
		}
		catch (Exception e ){
			iRecord.setError( Error.MEDIA_ERROR, e.getMessage());
			String lErr = e.getMessage();
			Log.Err( "Error.MEDIA_ERROR - " + lErr );
			//	Alert lAlert = new Alert(AlertType.ERROR, lErr );
			//	lAlert.showAndWait();	   		
			return false;
		}


		if( cMedPlayer != null){
			cMedPlayer.stop();
			cMedPlayer.dispose();
			cMedPlayer = null;
		}


		cMedPlayer = new MediaPlayer(cMedia);

		cMedPlayer.setOnError( ()-> {
			System.out.println("Media error occurred: " + cMedPlayer.getError());
			iRecord.setError( Error.MEDIAPLAYER, cMedPlayer.getError().toString() );			
		});

		cMedPlayer.setVolume(Conf.sVolume);
		cMedPlayer.setBalance(Conf.sBalance);
		cMedPlayer.setMute(Conf.sMute);

		StringBuilder lStr = new StringBuilder();
		lStr.append( iRecord.getName() );
		lStr.append(  " ");
		lStr.append( cMedPlayer.getTotalDuration().toMinutes() + "mn ");
		lStr.append(  " ");
		lStr.append( cMedia.getWidth() );
		lStr.append(  "x");
		lStr.append( cMedia.getHeight() );

		cMedBar.newMedia();

		if( iRecord.getExtension().equalsIgnoreCase("mp4") ) {
			if( cViewer == null ) {
				cViewer = new Viewer( this );				
			}

			cViewer.newMedia( cMedia, cMedPlayer );
			cViewer.show(true);

		} else {
			if( cViewer != null ) {
				cViewer.show( false );
			}
		}

		cCmdBar.play();
		cMedPlayer.play();
		return true;
	} 
	//--------------------------------------	
	public void destroyCurrent() {
		cTableRecords.removeRecord( cTableRecords.getCurrentRecord(), true);
		next();
		cTableRecords.writePlaylist( cCurrentPlayList, false);
	}
	//--------------------------------------	
	public void copyCurrent2BestOf() {
		if( Conf.sDirCopyBestOf == null || Conf.sDirCopyBestOf.length()<1) {
			Log.Err("No current record");
			return;
		}
		MyRecord lRecord = cTableRecords.getCurrentRecord();
		
		if( lRecord != null ) {		
			try {
				Path lSrc  = Paths.get( lRecord.getPath() );//lSrc.getFileName()
				Path lDest = Paths.get( Conf.sDirCopyBestOf ).resolve(lSrc.getFileName());
				
		//		Log.Info("Copy " + lSrc + " -> " + lDest);
				
				Files.copy( lSrc, lDest, StandardCopyOption.REPLACE_EXISTING  ); //.resolve( lSrc.getFileName()));
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}
	//--------------------------------------	
	public void scrollToCurrent() {
		cTableRecords.scrollToItem( cTableRecords.getCurrentRecord());

	}
	//--------------------------------------	
	public void mute() {
		if( cMedPlayer != null ) {			
			cMedPlayer.setMute(Conf.sMute);
		}
	}
	//--------------------------------------	
	public void pause() {
		if( cMedPlayer != null ) {			
			cMedPlayer.pause();
			cCmdBar.pause();
		}
	}
	//--------------------------------------	
	public void play() {

		if( cMedPlayer != null ) {			
			cCmdBar.play();
			cMedPlayer.play();
		}else {
			next();
		}
	}
	//--------------------------------------	
	public void next() {	

		while( true ) {
			boolean oFlagStop = false;
			MyRecord lRecord = null;		
			lRecord = cTableRecords.getNextRecord(oFlagStop);		

			if( Conf.sRepeatAll == false  && oFlagStop == true)
				break ;

			if( lRecord == null || lRecord.onError() 
					|| play( lRecord, 0 ) == false ) {
				continue;
			}
			break;	
		}
	}
	//--------------------------------------	
	public void previous() {
		MyRecord lRecord = null;

		lRecord = cTableRecords.getPreviousRecord();

		if( lRecord != null && lRecord.onError() == false) {
			play( lRecord, 0 );	 
			return;
		}
		//	else {
		//		previous();
		//	}
	}
	//--------------------------------------	
	public void endOfTrack() {
		Log.Dbg("endOfTrack" );

		if(  getRepeatMode() == Player.RepeatMode.REPEAT_TRACK) {
			getPlayer().seek( getPlayer().getStartTime()); 
			getPlayer().play();
		} else {
			next();
		}
	}
} 
//**********************************************************

