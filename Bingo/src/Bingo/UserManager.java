package Bingo;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//유저를 관리하는 클래스 
public class UserManager {

	private ArrayList<BingoUser> userList; // 접속중인 유저를 관리
	private ArrayList<BingoUser> matchingList; // 대기열 리스트

	public UserManager() {
		userList = new ArrayList<BingoUser>();
		matchingList = new ArrayList<BingoUser>();
	}

	public ArrayList<BingoUser> getUserList() {
		return userList;
	}

	public ArrayList<BingoUser> getMatchingList() {
		return matchingList;
	}

	public void addUser(BingoUser bingoUser) {
		userList.add(bingoUser);
		SetUserIndex();
		sendAllUser(BingoProtocol.CONNECT + userList.size() + "명이 접속중입니다.");

		System.out.println(userList.size() + "명이 접속중입니다.");
	}

	public void removeUser(BingoUser bingoUser) {
		userList.remove(bingoUser);
		SetUserIndex();
		sendAllUser(BingoProtocol.CONNECT + userList.size() + "명이 접속중입니다.");

		System.out.println(bingoUser.getUserName() + "님이 접속이 끊어졌습니다.");
	}

	// 유저리스트의 size()만큼 반복문을 돌려 메세지를 전송.
	public void sendAllUser(String Protocol) {
		for (int i = 0; i < userList.size(); i++)
		{
			//게임중인 사람에게는 보내지 않는다.
			if(userList.get(i).enemy == null)
				sendTo(i, Protocol);
		}
	}
	
	public void sendAllUserNotice(String Protocol) {
		for (int i = 0; i < userList.size(); i++)
		{
			sendTo(i, Protocol);
		}
	}

	// BingoUser를 제외한 나머지 사람에게 메세지를 전송.
	public void sendAllUserButNotMe(String Protocol, BingoUser user) {
		for (int i = 0; i < userList.size(); i++) {
			// 유저가 자신일경우 continue;
			if (userList.get(i) == user)
				continue;
			
			if(userList.get(i).enemy == null)
				sendTo(i, Protocol);
		}
	}

	public Socket getSocket(int index) {
		return userList.get(index).getSocket();
	}

	public void sendUser() {
		String msg = "";
		for (int i = 0; i < userList.size(); i++) {
			msg = msg + userList.get(i).getUserName() + ",";
		}
		sendAllUserNotice(BingoProtocol.USERLIST + msg);
	}

	public void SetUserIndex()
	{
		for(int i = 0 ; i < userList.size(); i++)
		{
			userList.get(i).index = i;
		}
		
	}
	// 소캣의 스트림을 받아와 메세지를 전송하는 함수.
	public void sendTo(int i, String msg) {
		try {
			PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);
			pw.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
