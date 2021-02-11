package org.phypo.Jixmu;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.*;

import javafx.scene.image.Image;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

//***************************************
class Conf{
	
	public static String sFileSavePlayList="JixmuCurrentPlayList.jixmu";
	static String sStrConfIni  = "Config.ini";

	static PPgIniFile  sIniConf ;	

	public static PPgIniFile  GetConf()     { return sIniConf;}

	// -----------------------------------

	//static String sIconeApplication = "Jizmu.png";
	
	public static Image sBackGroundImage=null;
	public static Image sIconeAppli=null;
	
	public static Image sIconeRepeatAll  = null;
	public static Image sIconeRandom     = null;
	public static Image sIconeMute       = null;
	public static Image sIconeVolume     = null;
	public static Image sIconeLR         = null;
	
	public static Image sIconeCpy2BestOf = null;
	public static Image sIconeDestroy    = null;
	static String sGoodExtensions ="mp3 mp4";
	
	static Set<String> sSetGoodExtensions = new HashSet<>();
	
	public static boolean    sAutoPlay  = true;
	public static boolean    sRandom    = false;
	public static boolean    sRepeatAll = false;
	public static boolean    sMute      = false;
	
	static double sBalance = 0;
	static double sVolume = 0.5;
	
	public static double sCurrentMediaTime = 0;

	public static String sDirCopyBestOf=null;
	

	//------------------------------------------------
	public static boolean OpenIni( String[] args){
		
		sStrConfIni  = PPgParam.GetString( args, "-I=", sStrConfIni );
		Log.Dbg( "ini file <" + sStrConfIni +">");

		sIniConf = PPgIniFile.GetIni( sStrConfIni);    
		if( sIniConf == null ) {
			return false;
		}
		
		return true;
	}

	//------------------------------------------------
	static String GetFileExtension( File iFile ) {
		if( iFile == null ) return null;
		return GetFileExtension( iFile.getName());
	}
	//------------------------------------------------
	static String RemoveFileExtension(String iFile ) {
		if( iFile == null ) return null;
		
		int lIndex = iFile.lastIndexOf('.');
		if( lIndex <= 0 ) return iFile;	
		
		return iFile.substring( 0, lIndex );
	}
	//------------------------------------------------
	static String GetFileExtension( String iName ) {	
		if( iName == null ) return null;
		
		int lIndex = iName.lastIndexOf('.');
		if( lIndex <= 0 ) return null;		
		String lExtension = iName.substring( lIndex + 1);
		if( lExtension == null | lExtension.length() == 0)
			return null;
		return lExtension.toLowerCase();
	}
	//------------------------------------------------
	static boolean IsGoodExtension( String iExt) {
		return sSetGoodExtensions.contains(iExt);
	}
	//------------------------------------------------
	public static boolean ReadIni( String[] args){
		
		if( OpenIni( args ) == false ) return false;

		sIconeAppli     = FxHelper.ReadIcon(  sIniConf, "APPLICATION", "Icon",          null );	
		sIconeRepeatAll = FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconRepeatAll", null );	
		sIconeRandom    = FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconRandom",    null );	
		sIconeMute      = FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconMute",      null );	
		sIconeLR         = FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconLR",      null );	
		sIconeVolume     = FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconVolume",      null );	

		sIconeCpy2BestOf= FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconCopyBestOf",      null );	

		sIconeDestroy= FxHelper.ReadIcon(  sIniConf, "ICONES",      "IconDestroy",      null );	
		
		sAutoPlay  = sIniConf.getboolean( "USER_SETTINGS", "AutoPlay",  sAutoPlay);
		sRandom    = sIniConf.getboolean( "USER_SETTINGS", "Random",    sRandom);
		sRepeatAll = sIniConf.getboolean( "USER_SETTINGS", "RepeatAll", sRepeatAll);
		sMute      = sIniConf.getboolean( "USER_SETTINGS", "Mute",      sMute);
				
		sBalance = sIniConf.getdouble( "USER_SETTINGS", "Balance", sBalance);
		sVolume  = sIniConf.getdouble( "USER_SETTINGS", "Volume", sVolume);
	
		sDirCopyBestOf = sIniConf.get("USER_SETTINGS", "DirCopyBestOf", null );

		
		sCurrentMediaTime  = sIniConf.getdouble( "USER_SETTINGS", "CurrentMediaTime", sVolume);
		String lStr = sIniConf.get( "FILES", "GoodExtensions", sGoodExtensions );
		Log.Dbg( "Extensions Str:" + lStr );
		
		PPgToken lTok = new PPgToken(lStr);
		String lExt;
		while( (lExt = lTok.nextTokenStringTrim()).length() > 0) {			
			Log.Dbg("Ext:" + lExt);
			sSetGoodExtensions.add( lExt );
		}
	
		Log.Dbg( "ReadIni good extensions Set : " +sSetGoodExtensions );
				
		
		
		
		return true;
	}		
	// -----------------------------------		
	public static void SaveIni() {
		sIniConf.set( "USER_SETTINGS", "AutoPlay",  sAutoPlay );
		sIniConf.set( "USER_SETTINGS", "Random",    sRandom   );
		sIniConf.set( "USER_SETTINGS", "RepeatAll", sRepeatAll);
		sIniConf.set( "USER_SETTINGS", "Mute",      sMute     );
		sIniConf.set( "USER_SETTINGS", "Balance",   sBalance  );
		sIniConf.set( "USER_SETTINGS", "Volume",    sVolume   );
		sIniConf.set( "USER_SETTINGS", "CurrentMediaTime", sCurrentMediaTime );

		GetConf().writeIni(	sStrConfIni	);
		Log.Dbg(2,"saveIni " + sStrConfIni + " done");
	}
}
//***************************************
