package org.phypo.Jixmu;

import java.awt.Color;

import org.phypo.PPg.PPgUtils.*;
import org.phypo.PPg.Sql.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.GrayFilter;

import java.net.*;
import java.io.*;

//***************************************
class Conf{
	
	static String sStrConfIni  = "Config.ini";
	static String sStrParamIni = "Param.ini";

	//---------------------
	static PPgIniFile  sIniConf ;	
	static PPgIniFile  sIniParam ;	

	public static PPgIniFile  GetConf()     { return sIniConf;}
	public static PPgIniFile  GetParam()    { return sIniParam; }
	public static PPgIniFile  GetDyn()      { return sIniParam; }
	public static PPgIniFile  GetConnect()  { return sIniConf; }
	public static PPgIniFile  GetWin()      { return sIniParam; }
	
	
	public static String GetOsStr( String iVar ) { return GetConf().get( Main.GetOs(), iVar );} 
	//---------------------

	public static int sHeartbeatOkDuration     = 10;
	public static int sHeartbeatWarnDuration   = 20;
	public static int sHeartbeatErrorDuration  = 40;
	public static int sHeartbeatCriticDuration = 60;
	public static int sHeartbeatFatalDuration  = 180;

//	public static Color sColorOk      = Color.green;
	public static Color sColorOk      = new Color(153,232,65  );
	public static Color sColorWarn    = Color.yellow;
	public static Color sColorError   = Color.orange;
	public static Color sColorCritic  = Color.red;
	public static Color sColorFatal   = Color.black;

	public static Color sColorBusy     = Color.cyan;
	public static Color sColorUnknown  = Color.lightGray;

	// -----------------------------------
	public static final Color GetColor( Log.Type iType ){

		switch( iType ){

		case Unknown : 
			return sColorUnknown;

		case Busy : 
			return sColorBusy;

		case Warn : 
			return sColorWarn;

		case Error : 
			return sColorWarn;

		case Critic :  
			return sColorCritic;

		case Fatal :  
			return sColorFatal;

		case Void : 
		default: ;
		return sColorOk;
		}
	}
	// -----------------------------------


	static String sIconeApplication = "JizAd.png";
	public static ImageIcon sBackGroundImage=null;
	public static ImageIcon sIconeAppli=null;
	public static ImageIcon sSesamItLogo=null;
	public static ImageIcon sDownloadImg=null;
	
	
	public static ImageIcon sPayloadPrintImg = null;
	public static ImageIcon sPayloadHex      = null;
	public static ImageIcon sPacketHex       = null;
	
	
	public static ImageIcon sCmdReset=null;
	public static ImageIcon sCmdAdd=null;
	public static ImageIcon sCmdDel=null;

	public static ImageIcon sReloadImg=null;
	public static ImageIcon sTrashImg=null;

	public static ImageIcon sOkImg=null;
	public static ImageIcon sCancelImg=null;
	
	public static ImageIcon sServerHeartbeatImg=null;
	
	public static SqlServer sSqlServer=null;
	public static SqlServer sSqlServerBackup=null;
	public static String sSelectServer;
	public static String sSelectConnex;

	public static int  sMenuMaxLine = 40;

	public static String  sUser      = null; 
	public static String  sPassword  = null; 

	public static int    sAutoReloadTimeSecond=5;

	//------------------------------------------------
	public static boolean OpenIni( String[] args){
		
		sStrConfIni  = PPgParam.GetString( args, "-I=", sStrConfIni );
		sStrParamIni = PPgParam.GetString( args, "-i=", sStrParamIni );
		Log.Dbg( "ini file <" + sStrConfIni +"> <"+sStrParamIni+'>');

		sIniConf = PPgIniFile.GetIni( sStrConfIni);    
		sIniParam = PPgIniFile.GetIni( sStrParamIni);    
		if( sIniConf == null || sIniParam == null) {
			return false;
		}

		return true;
	}
	//------------------------------------------------
	public static boolean ReadIni( String[] args){
		
		if( OpenIni( args ) == false ) return false;

		sIconeAppli  = PPgIniFile.ReadIcon( GetWin(), "Application", "Icon", null );	
		sSesamItLogo = PPgIniFile.ReadIcon( GetWin(), "Application", "SesamItLogo", null );	
		sReloadImg   = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconReload", null );	
		sTrashImg    = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconTrash", null );	
		sServerHeartbeatImg= PPgIniFile.ReadIcon( GetWin(), "Draw", "IconServerHeartbeat", null );
		sDownloadImg   = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconDownload", null );	

		
		sPayloadPrintImg   = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconPayloadPrintable", null );	
		sPayloadHex   = PPgIniFile.ReadIcon( GetWin(),      "Draw", "IconPayloadHex", null );	
		sPacketHex   = PPgIniFile.ReadIcon( GetWin(),       "Draw", "IconPacketHex", null );	

		
		sCmdReset= PPgIniFile.ReadIcon( GetWin(), "Draw", "IconCmdReset", null );
		sCmdAdd= PPgIniFile.ReadIcon( GetWin(), "Draw", "IconCmdAdd", null );
		sCmdDel= PPgIniFile.ReadIcon( GetWin(), "Draw", "IconCmdTrash", null );
		
		sOkImg     = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconOk",     null );
		sCancelImg = PPgIniFile.ReadIcon( GetWin(), "Draw", "IconCancel", null );
		
			
		sAutoReloadTimeSecond  = GetDyn().getint( "Application", "AutoReloadDataTimeSecond", sAutoReloadTimeSecond);
		sAutoReloadTimeSecond *= 1000; // passage en millieme de second
/*	
		COLOR_ACTIF    = pIni.getColor( "Draw", "ColorActive", COLOR_ACTIF );
			COLOR_INACTIF  = pIni.getColor( "Draw", "ColorInactive", COLOR_INACTIF );
		COLOR_PROBLEM  = pIni.getColor( "Draw", "ColorProblem", COLOR_PROBLEM );
		COLO	R_NO_INFO  = pIni.getColor( "Draw", "ColorNoInfo", COLOR_NO_INFO );
		COLOR_DEAD     = pIni.getColor( "Draw", "ColorDead", COLOR_DEAD );
		COLOR_PROCESSING_BUSY  = pIni.getColor( "Draw", "ColorBusy", COLOR_PROCESSING_BUSY );
*/
		return true;
	}		
	// -----------------------------------		
	public static void SaveIni() {
		GetParam().writeIni(	sStrParamIni	);
		Log.Dbg(2,"saveIni " + sStrParamIni + " done");
	}
}
//***************************************
