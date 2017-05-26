package Bingo;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClientGUI implements ActionListener {
	private BingoClient client;
	private JFrame frame;
	private TextArea msgView = new TextArea("", 10, 10, 10);
	private TextArea userView = new TextArea("", 10, 10, 10);
	private TextField sendBox = new TextField("");
	private Button waitingButton = new Button("READY");
	private JButton buttons[] = new JButton[25];
	private String arr[] = new String[25];
	private Label infoView = new Label("", 1);
	private Label infoLabel = new Label("", 1);
	private JPanel panel = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel panel3 = new JPanel();
	private ArrayList<String> checkedNumbers = new ArrayList<String>();
	private Font font = new Font("serif", Font.BOLD, 25);
	private Font font2 = new Font("serif", Font.ITALIC, 25);
	private Font usernameFont = new Font("san-serif", Font.BOLD, 39);
	private Font fontUserView = new Font("san-serif", Font.BOLD, 15);
	private Font fontinfoView = new Font("san-serif", Font.BOLD, 15);

	
	public void SetNameInfo(BingoClient bc) {
		bc.setUserName(JOptionPane.showInputDialog(null, "이름을 설정하세요."));
		this.go(bc);
	}

	public void go(BingoClient bc) {
		frame = new JFrame("JAVA BINGO");
		client = bc;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(450, 550);

		msgView.setEditable(false);
		userView.setEditable(false);
		infoView.setBounds(0, 30, 30, 30);
		infoView.setBackground(new Color(235, 176, 53));
		infoView.setFont(fontinfoView);

		panel.setLayout(new BorderLayout());
		panel.add(infoView, BorderLayout.NORTH);
		panel.add(msgView, BorderLayout.CENTER);
		panel.add(userView, BorderLayout.WEST);
		panel.add(sendBox, BorderLayout.SOUTH);
		panel.setBounds(400, 300, 200,200);

		infoLabel.setBounds(0, 30, 30, 30);
		infoLabel.setFont(usernameFont);
		infoLabel.setText(client.getUserName());
		infoLabel.setBackground(new Color(6,162, 203));
		waitingButton.setFont(font);
		panel2.setLayout(new BorderLayout());
		panel2.add(waitingButton, BorderLayout.SOUTH);
		panel2.add(infoLabel, BorderLayout.EAST);
		panel2.setBounds(0, 0, 100, 300);

		panel3.setLayout(new GridLayout(5, 5));
		
		String arr[] = createNumbers();

		for (int i = 0; i < arr.length; i++) {
			buttons[i] = new JButton(arr[i]);
			buttons[i].setSize(10, 10);
			buttons[i].setFont(font);
			panel3.add(buttons[i]);
			buttons[i].addActionListener(this);
		}

		frame.getContentPane().add(panel, "South");
		frame.getContentPane().add(panel2, "East");
		frame.getContentPane().add(panel3, "West");

		sendBox.addActionListener(this);
		waitingButton.addActionListener(this);

		frame.setVisible(true);
	}

	public void showMessage(String msg) {
		infoView.setText(msg);
	}

	public void appendMessage(String msg) {
		msgView.setForeground(Color.DARK_GRAY);
		msgView.append(msg + "\n");
	}

	public void showUser(String msg) {
		userView.setText("");
		System.out.println(msg);
		userView.setFont(fontUserView);
		userView.setText(msg.replace(",", "\n"));
	}

	public JPanel getPanel3() {
		return panel3;
	}

	public void setPanel3(JPanel panel3) {
		this.panel3 = panel3;
	}

	public JButton[] getButtons() {
		return buttons;
	}

	public ArrayList<String> getCheckedNumbers() {
		return checkedNumbers;
	}
	
	public void winAlert() throws InterruptedException {
		JOptionPane.showMessageDialog(frame, "YOU WIN !", "게임 결과", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
	
	public void loseAlert() {
		JOptionPane.showMessageDialog(frame, "YOU LOSE !", "게임 결과", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

	public String[] createNumbers() {
		int num = 0;

		for (int i = 0; i < arr.length; i++) { // 랜덤수25개 생성
			num = (int) ((Math.random() * 50) + 1); // 랜덤함수 호출 (범위 1-50)
			arr[i] = num + "";
			for (int j = 0; j < i; j++) {
				if (arr[i].equals(arr[j])) { // 생성된 수와 이전에 저장된 수를 비교
					num = (int) ((Math.random() * 50) + 1);
					arr[i] = num + ""; // 다시 수를 생성
					i--; // 다시 첨부터 같은 숫자가 있는가 비교
					break;
				}
			}
		}
		return arr;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String bt = (String) e.getActionCommand();
		if (e.getSource() instanceof TextField) {
			String msg = sendBox.getText();

			try {
				client.sendMessage(BingoProtocol.SENDMESSAGE + client.getUserName() + " : " + msg);
				sendBox.setText("");
			} catch (Exception Ee) {
			}
		}

		if (e.getSource() instanceof Button) {
			// 자신을 대기열에 넣어달라는 프로토콜 헤더를 보냄.
			waitingButton.setEnabled(false);
			client.sendMessage(BingoProtocol.READYGAME);
		}

		if (e.getSource() instanceof JButton) {
			// 눌렀을 때의 값
			checkedNumbers.add(bt); // 체크된값추가
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i].getText() == bt) {
					buttons[i].setBackground(new Color(0, 0, 0));
					buttons[i].setEnabled(false);
					buttons[i].setFont(font2); 
					client.sendMessage(BingoProtocol.CLICKNUMBER + bt);
				}
			}
		}
	}

	//panel을 비활성화 하기 위한 메소드
	public void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

}
