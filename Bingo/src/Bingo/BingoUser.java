package Bingo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BingoUser extends Thread {
	public int index = 0;
	private int turn = 0;
	private Socket socket;
	private UserManager userManager;
	private String userName;
	private BufferedReader reader;
	private PrintWriter writer;
	public BingoUser enemy; // 적.

	// 소캣과 매니저를 받아옴.
	public BingoUser(Socket s, UserManager um) {
		this.socket = s;
		this.userManager = um;
		this.enemy = null;
	}

	// 클라이언트에서 서버로 메세지를 보냈을떄 여길로 온다.
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);

			String msg = ""; // 클라이언트의 메시지

			while ((msg = reader.readLine()) != null) {
				if (msg.contains(BingoProtocol.SETUSERNAME)) {
					userName = msg.substring(BingoProtocol.SETUSERNAME.length());
					userManager.sendAllUserButNotMe(BingoProtocol.RECEIVEMESSAGE + getUserName() + "님이 접속하였습니다.", this);
					userManager.sendUser();
				}

				if (msg.contains(BingoProtocol.SENDMESSAGE)) {
					
					if(enemy!= null)
					{
						sendToEnemy(BingoProtocol.RECEIVEMESSAGE + msg.substring(BingoProtocol.SENDMESSAGE.length()));
						sendToClient(BingoProtocol.RECEIVEMESSAGE + msg.substring(BingoProtocol.SENDMESSAGE.length()));
					}
					else
					userManager.sendAllUser(
							BingoProtocol.RECEIVEMESSAGE + msg.substring(BingoProtocol.SENDMESSAGE.length()));
					
					
				}

				if (msg.contains(BingoProtocol.READYGAME)) {
					userManager.getMatchingList().add(this);
					System.out.println(getUserName() + "님이 대기열에 등록하였습니다.");

					if (userManager.getMatchingList().size() >= 2) {
						// 나보다 늦게 준비버튼을 누른 클라이언트를 가져온다.
						userManager.getMatchingList().get(0).enemy = userManager.getMatchingList().get(1);
						userManager.getMatchingList().get(1).enemy = userManager.getMatchingList().get(0);

						// 대기리스트에서 나를 지운다. 상대방도 지우니깐 빔.
						userManager.getMatchingList().remove(1);
						userManager.getMatchingList().remove(0);

						// 모두에게 게임이 시작되었다고 보낸다.
						sendToClient(BingoProtocol.STARTGAME);
						sendToEnemy(BingoProtocol.STARTGAME);
					} else
						sendToClient(BingoProtocol.WAITSTART);
				}

				// 게임 시작시
				if (msg.contains(BingoProtocol.STARTEDGAME)) {
					// 순서를 정한다. 1 : 선, -1 : 후
					
					int user1Index = index;
					int user2Index = enemy.index;
					
					{
						userManager.getUserList().get((user1Index < user2Index)? user1Index : user2Index ).turn = 1;
						userManager.getUserList().get((user1Index > user2Index)? user1Index : user2Index).turn = -1;
						// 1을 먼저 시작하게 하는 헤더 프로토콜을 보낸다.
						if (turn == 1) {
							sendToClient(BingoProtocol.YOURTURN);
							userManager.sendTo(0, msg);
						}
						// -1은 대기하게 하는 헤더 프로토콜을 보낸다.
						else {
							sendToClient(BingoProtocol.WAITYOURTURN);
							userManager.sendTo(1, msg);
						}
					}
				}

				if (msg.contains(BingoProtocol.CLICKNUMBER)) {
					sendToEnemy(BingoProtocol.YOURTURN + (msg.substring(BingoProtocol.CLICKNUMBER.length())));
					sendToClient(BingoProtocol.WAITYOURTURN);
				}
				
				if (msg.contains(BingoProtocol.WIN)) {
					sendToClient(BingoProtocol.WIN);
					sendToEnemy(BingoProtocol.LOSE);
					
					turn = 0;
					enemy.turn = 0;
					enemy = null;
					enemy.enemy = null;
				}
			}
		}

		// 에러
		catch (Exception e) {
			e.printStackTrace();
		}
		// 접속 해제
		finally {
			try {
				userManager.removeUser(this);
				userManager.sendAllUser(BingoProtocol.DISCONNECT + getUserName() + "님이 접속을 종료하였습니다.");
				userManager.sendUser();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getTurn() {
		return turn;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getUserName() {
		return userName;
	}

	void sendToClient(String msg) {
		try {
			PrintWriter pw = new PrintWriter(getSocket().getOutputStream(), true);
			pw.println(msg);
		} catch (Exception e) {
		}
	}

	void sendToEnemy(String msg) {
		try {
			PrintWriter pw = new PrintWriter(enemy.getSocket().getOutputStream(), true);
			pw.println(msg);
		} catch (Exception e) {
		}
	}

}
