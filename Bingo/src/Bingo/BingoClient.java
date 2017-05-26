package Bingo;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;

public class BingoClient extends Frame implements Runnable {
	private String userName; 
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientGUI gui;

	public static void main(String[] args) {
		BingoClient client = new BingoClient();
		client.gui = new ClientGUI();
		client.gui.SetNameInfo(client);
		client.connectServer();
	}

	// 서버에 접속
	private void connectServer() {
		try {
			appendMessage("접속중");
			socket = new Socket("127.0.0.1", 8080);
			appendMessage("접속 성공");

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			new Thread(this).start();

			writer.println(BingoProtocol.SETUSERNAME + userName);
		} catch (Exception e) {
			appendMessage("접속실패");
		}
	}

	public void run() {
		String msg = "";
		try {
			while ((msg = reader.readLine()) != null) {
				if (msg.contains(BingoProtocol.CONNECT)) {
					showMessage(msg.substring(BingoProtocol.CONNECT.length()));
				}

				if (msg.contains(BingoProtocol.DISCONNECT)) {
					appendMessage(msg.substring(BingoProtocol.DISCONNECT.length()));
				}

				if (msg.contains(BingoProtocol.USERLIST)) {
					showUserList(msg.substring(BingoProtocol.USERLIST.length()));
				}

				if (msg.contains(BingoProtocol.RECEIVEMESSAGE)) {
					appendMessage(msg.substring(BingoProtocol.RECEIVEMESSAGE.length()));
				}

				if (msg.contains(BingoProtocol.WAITSTART)) {
					appendMessage("대기열을 찾고있습니다 잠시만 기다려주세요 ");
				}

				if (msg.contains(BingoProtocol.STARTGAME)) {
					appendMessage("대기열을 찾았습니다! ");
					sendMessage(BingoProtocol.STARTEDGAME);
				}

				if (msg.contains(BingoProtocol.YOURTURN)) {
					showMessage("당신의 차례입니다.");
					gui.enableComponents(gui.getPanel3(), true);
					String str = msg.substring(BingoProtocol.YOURTURN.length()); // 상대방이  누른 숫자
					JButton buttons[] = gui.getButtons();
					int bingoResult = 0;
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].getText().equals(str)) {
							buttons[i].setFont(new Font("serif", Font.ITALIC, 10));
							buttons[i].setBackground(new Color(0,0,0));
							buttons[i].setEnabled(false);
							gui.getCheckedNumbers().add(str);
						}
					}
					for (int i = 0; i < gui.getCheckedNumbers().size(); i++) {
						for (int j = 0; j < buttons.length; j++) {
							if(buttons[j].getText().equals(gui.getCheckedNumbers().get(i))){
								buttons[j].setEnabled(false);
							}
						}
					}
					bingoResult = bingoResult();
					if(bingoResult > 0){
						System.out.println("총 " + bingoResult + "개의 빙고가 완성 되었습니다.");
					}
					if(bingoResult ==5 ){
						sendMessage(BingoProtocol.WIN);
					}
					
				}

				if (msg.contains(BingoProtocol.WAITYOURTURN)) {
					showMessage("상대방 차례입니다.");
					gui.enableComponents(gui.getPanel3(), false);
				}
				
				if (msg.contains(BingoProtocol.WIN)) {
					gui.winAlert();
				}
				
				if (msg.contains(BingoProtocol.LOSE)) {
					gui.loseAlert();
				}

			}

		} catch (

		Exception e)

		{
			System.out.println(e);
		}

	}
	
	//빙고 결과
	public int bingoResult(){
		int totalCount = 0;
		int xCount = 0;
		int yCount = 0;
		int zCount = 0;
		int checkX = 0;
		int checkY = 0;
		int checkZ = 0;
		JButton buttons[] = gui.getButtons();
		for (int x = 0; x <= 20; x = x+5) {//가로 확인
			for(int i = x; i < x + 5; i++){
				if(gui.getCheckedNumbers().contains(buttons[i].getText())){
					checkX++; // 존재하면 체크 증가
					if(checkX == 5){ // 5개 전부 있으면
						xCount++; // 가로 빙고 추가
						checkX = 0; // 빙고가 되면 초기화
					}
				}
			}
			checkX = 0; // 다음 줄 확인 하기 위해 초기화
		}
		for (int x = 0; x < 5; x++) {//세로 확인
			for(int i = x; i <= x + 20; i = i + 5){
				if(gui.getCheckedNumbers().contains(buttons[i].getText())){
					checkY++; 
					if(checkY == 5){ 
						yCount++; 
						checkY = 0;
					}
				}
			}
			checkY = 0;
		}
		for (int i = 0; i < 25; i = i + 6) { // '\' 대각선 확인 
			if(gui.getCheckedNumbers().contains(buttons[i].getText())){
				checkZ++;
				if (checkZ == 5) {
					zCount++;
				}
			}
		}
		checkZ = 0; // '/' 대각선 확인을 위한 초기화
		
		for (int i = 4; i <= 20; i = i + 4) {// '/' 대각선 확인
			if(gui.getCheckedNumbers().contains(buttons[i].getText())){
				checkZ++;
				if (checkZ == 5) {
					zCount++;
				}
			}
		}
		totalCount = xCount + yCount + zCount; // 빙고 총 개수 확인
		return totalCount;
	}

	public void sendMessage(String msg) {
		writer.println(msg);
	}

	public void appendMessage(String msg) {
		getGUI().appendMessage(msg);
	}

	public void showMessage(String msg) {
		getGUI().showMessage(msg);
	}

	public void showUserList(String msg) {
		getGUI().showUser(msg);
	}

	public ClientGUI getGUI() {
		return gui;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String name) {
		userName = name;
	}


}
