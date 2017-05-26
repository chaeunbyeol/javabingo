package Bingo;
import java.net.ServerSocket;
import java.net.Socket;

public class BingoServer {
	private ServerSocket serverSocket;  //��Ĺ ����
	private UserManager userManager;  //���� ���� Ŭ����

	public static void main(String[] args) {
		BingoServer mainServer = new BingoServer();
		mainServer.startServer();
	}

	public void startServer() { 
		userManager = new UserManager();
		
		try {
			serverSocket = new ServerSocket(8080);
			System.out.println("���� ����");
			
			//��Ĺ�� ���� �����Ҷ����� ���, ���� ������ ������.
			while(true)
			{
				Socket socket = serverSocket.accept();  //Ŭ���̾�Ʈ�� �����Ҷ����� ���
				
				BingoUser bingoUser = new BingoUser(socket, userManager);  //���� ������� ���� ����
				bingoUser.start();
				
				userManager.addUser(bingoUser);	 //���� ������ ������ ����Ʈ�� �߰�.				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
