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
	public BingoUser enemy; // ��.

	// ��Ĺ�� �Ŵ����� �޾ƿ�.
	public BingoUser(Socket s, UserManager um) {
		this.socket = s;
		this.userManager = um;
		this.enemy = null;
	}

	// Ŭ���̾�Ʈ���� ������ �޼����� �������� ����� �´�.
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);

			String msg = ""; // Ŭ���̾�Ʈ�� �޽���

			while ((msg = reader.readLine()) != null) {
				if (msg.contains(BingoProtocol.SETUSERNAME)) {
					userName = msg.substring(BingoProtocol.SETUSERNAME.length());
					userManager.sendAllUserButNotMe(BingoProtocol.RECEIVEMESSAGE + getUserName() + "���� �����Ͽ����ϴ�.", this);
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
					System.out.println(getUserName() + "���� ��⿭�� ����Ͽ����ϴ�.");

					if (userManager.getMatchingList().size() >= 2) {
						// ������ �ʰ� �غ��ư�� ���� Ŭ���̾�Ʈ�� �����´�.
						userManager.getMatchingList().get(0).enemy = userManager.getMatchingList().get(1);
						userManager.getMatchingList().get(1).enemy = userManager.getMatchingList().get(0);

						// ��⸮��Ʈ���� ���� �����. ���浵 ����ϱ� ��.
						userManager.getMatchingList().remove(1);
						userManager.getMatchingList().remove(0);

						// ��ο��� ������ ���۵Ǿ��ٰ� ������.
						sendToClient(BingoProtocol.STARTGAME);
						sendToEnemy(BingoProtocol.STARTGAME);
					} else
						sendToClient(BingoProtocol.WAITSTART);
				}

				// ���� ���۽�
				if (msg.contains(BingoProtocol.STARTEDGAME)) {
					// ������ ���Ѵ�. 1 : ��, -1 : ��
					
					int user1Index = index;
					int user2Index = enemy.index;
					
					{
						userManager.getUserList().get((user1Index < user2Index)? user1Index : user2Index ).turn = 1;
						userManager.getUserList().get((user1Index > user2Index)? user1Index : user2Index).turn = -1;
						// 1�� ���� �����ϰ� �ϴ� ��� ���������� ������.
						if (turn == 1) {
							sendToClient(BingoProtocol.YOURTURN);
							userManager.sendTo(0, msg);
						}
						// -1�� ����ϰ� �ϴ� ��� ���������� ������.
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

		// ����
		catch (Exception e) {
			e.printStackTrace();
		}
		// ���� ����
		finally {
			try {
				userManager.removeUser(this);
				userManager.sendAllUser(BingoProtocol.DISCONNECT + getUserName() + "���� ������ �����Ͽ����ϴ�.");
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
