package Bingo;
import java.net.ServerSocket;
import java.net.Socket;

public class BingoServer {
	private ServerSocket serverSocket;  //소캣 서버
	private UserManager userManager;  //유저 관리 클래스

	public static void main(String[] args) {
		BingoServer mainServer = new BingoServer();
		mainServer.startServer();
	}

	public void startServer() { 
		userManager = new UserManager();
		
		try {
			serverSocket = new ServerSocket(8080);
			System.out.println("서버 시작");
			
			//소캣의 서버 접근할때까지 대기, 무한 루프를 돌린다.
			while(true)
			{
				Socket socket = serverSocket.accept();  //클라이언트가 접속할때까지 대기
				
				BingoUser bingoUser = new BingoUser(socket, userManager);  //접속 했을경우 유저 생성
				bingoUser.start();
				
				userManager.addUser(bingoUser);	 //접속 했을시 유저를 리스트에 추가.				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
