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

	// ������ ����
	private void connectServer() {
		try {
			appendMessage("������");
			socket = new Socket("127.0.0.1", 8080);
			appendMessage("���� ����");

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			new Thread(this).start();

			writer.println(BingoProtocol.SETUSERNAME + userName);
		} catch (Exception e) {
			appendMessage("���ӽ���");
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
					appendMessage("��⿭�� ã���ֽ��ϴ� ��ø� ��ٷ��ּ��� ");
				}

				if (msg.contains(BingoProtocol.STARTGAME)) {
					appendMessage("��⿭�� ã�ҽ��ϴ�! ");
					sendMessage(BingoProtocol.STARTEDGAME);
				}

				if (msg.contains(BingoProtocol.YOURTURN)) {
					showMessage("����� �����Դϴ�.");
					gui.enableComponents(gui.getPanel3(), true);
					String str = msg.substring(BingoProtocol.YOURTURN.length()); // ������  ���� ����
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
						System.out.println("�� " + bingoResult + "���� ���� �ϼ� �Ǿ����ϴ�.");
					}
					if(bingoResult ==5 ){
						sendMessage(BingoProtocol.WIN);
					}
					
				}

				if (msg.contains(BingoProtocol.WAITYOURTURN)) {
					showMessage("���� �����Դϴ�.");
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
	
	//���� ���
	public int bingoResult(){
		int totalCount = 0;
		int xCount = 0;
		int yCount = 0;
		int zCount = 0;
		int checkX = 0;
		int checkY = 0;
		int checkZ = 0;
		JButton buttons[] = gui.getButtons();
		for (int x = 0; x <= 20; x = x+5) {//���� Ȯ��
			for(int i = x; i < x + 5; i++){
				if(gui.getCheckedNumbers().contains(buttons[i].getText())){
					checkX++; // �����ϸ� üũ ����
					if(checkX == 5){ // 5�� ���� ������
						xCount++; // ���� ���� �߰�
						checkX = 0; // ���� �Ǹ� �ʱ�ȭ
					}
				}
			}
			checkX = 0; // ���� �� Ȯ�� �ϱ� ���� �ʱ�ȭ
		}
		for (int x = 0; x < 5; x++) {//���� Ȯ��
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
		for (int i = 0; i < 25; i = i + 6) { // '\' �밢�� Ȯ�� 
			if(gui.getCheckedNumbers().contains(buttons[i].getText())){
				checkZ++;
				if (checkZ == 5) {
					zCount++;
				}
			}
		}
		checkZ = 0; // '/' �밢�� Ȯ���� ���� �ʱ�ȭ
		
		for (int i = 4; i <= 20; i = i + 4) {// '/' �밢�� Ȯ��
			if(gui.getCheckedNumbers().contains(buttons[i].getText())){
				checkZ++;
				if (checkZ == 5) {
					zCount++;
				}
			}
		}
		totalCount = xCount + yCount + zCount; // ���� �� ���� Ȯ��
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
