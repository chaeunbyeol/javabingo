package Bingo;
//서버에서 주고 받을 프로토콜 
public class BingoProtocol {
	// 접속
	public static String CONNECT = "CONNET";
	// 접속 해제
	public static String DISCONNECT = "DISCONNECT";
	// 접속 유저를 받아오기
	public static String USERLIST = "USERLIST";
	// 이름 설정
	public static String SETUSERNAME = "SETUSERNAME";
	// 메세지 보내기
	public static String SENDMESSAGE = "SENDMESSAGE";
	// 메세지를 받기
	public static String RECEIVEMESSAGE = "RECEIVEMESSAGE";

	public static String READYGAME = "READYGAME";
	
	public static String WAITSTART = "WAITSTART";

	public static String STARTGAME = "STARTGAME";

	public static String STARTEDGAME = "STARTEDGAME";
	//버튼누르고보냄
	public static String CLICKNUMBER = "CLICKNUMBER";
	//자기가 누를차례
	public static String YOURTURN = "YOURTURN";		
	//자기가 돌을 놓을 차례를 기다리게
	public static String WAITYOURTURN = "WAITYOURTURN";
	
	public static String WIN = "WIN";
	
	public static String LOSE = "LOSE";


}
