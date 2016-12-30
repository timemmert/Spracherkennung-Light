import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GUI extends JFrame implements KeyListener{
	private String word;
	private JTextField text;
	static {
	    System.loadLibrary("ccode");
	    
	  }
	
	public static void main(String [] args)
	{
		
		GUI gui=new GUI("Clientside Application");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setBounds(100,100,500,300);
		gui.setVisible(true);
		
		
	}
	public GUI(String title)
	{
		super(title);
		word="";
		text=new JTextField();
		text.setBounds(0, 0, 500, 300);
		text.setVisible(true);
		text.addKeyListener(this);
		add(text);
	}
	
	public native void c_send(String s);
	public native String c_receive();
	
	
	public String networking(String toSend)
	{
		c_send(toSend);
		System.out.println("gesendet...");
		toSend=c_receive();
		return toSend;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
		if(e.getKeyChar()==' ')
		{
			String zwischenspeicher=word;
			word=networking(word);
			String t=text.getText();
			
			//System.out.println(t.replaceAll(zwischenspeicher,word));
			text.setText(t.replaceAll(zwischenspeicher,word));
			System.out.println(word);
			word="";
			
		}
		else
		{
			word+=e.getKeyChar();
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
