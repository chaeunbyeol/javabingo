package Bingo;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//������ �����ϴ� Ŭ���� 
public class UserManager {

	private ArrayList<BingoUser> userList; // �������� ������ ����
	private ArrayList<BingoUser> matchingList; // ��⿭ ����Ʈ

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
		sendAllUser(BingoProtocol.CONNECT + userList.size() + "���� �������Դϴ�.");

		System.out.println(userList.size() + "���� �������Դϴ�.");
	}

	public void removeUser(BingoUser bingoUser) {
		userList.remove(bingoUser);
		SetUserIndex();
		sendAllUser(BingoProtocol.CONNECT + userList.size() + "���� �������Դϴ�.");

		System.out.println(bingoUser.getUserName() + "���� ������ ���������ϴ�.");
	}

	// ��������Ʈ�� size()��ŭ �ݺ����� ���� �޼����� ����.
	public void sendAllUser(String Protocol) {
		for (int i = 0; i < userList.size(); i++)
		{
			//�������� ������Դ� ������ �ʴ´�.
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

	// BingoUser�� ������ ������ ������� �޼����� ����.
	public void sendAllUserButNotMe(String Protocol, BingoUser user) {
		for (int i = 0; i < userList.size(); i++) {
			// ������ �ڽ��ϰ�� continue;
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
	// ��Ĺ�� ��Ʈ���� �޾ƿ� �޼����� �����ϴ� �Լ�.
	public void sendTo(int i, String msg) {
		try {
			PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);
			pw.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
