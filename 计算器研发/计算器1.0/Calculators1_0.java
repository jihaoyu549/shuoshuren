import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

 class Calculators1_0  {
	public static void main(String[] args) {
		Frame frame =new Frame("计算器");
		Panel panel =new Panel();
		panel.add(new TextField(40));
		frame.add(panel,BorderLayout.NORTH);
		Panel gridPanel = new Panel(); //定义面板
		gridPanel.setLayout(new GridLayout(6,4,3,3)); //并设置为网格布局
		
		String name[] = {"%","开根","平方","1/x","CE","C","X","/","7","8","9","*","4","5","6","-","1","2","3","+","+/-","0",".","="}; 
		for(int i=0;i<name.length;i++)  //用循环将数添加到面板中
		{
			gridPanel.add(new Button(name[i]));
		}
		
		frame.add(gridPanel,BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter()
				{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
				});
		frame.setVisible(true);
	}
}
