import javax.swing.JFrame;

public class GameFrame extends JFrame{

    public static void main(String[] args) {
        new GameFrame();
    }

    GameFrame(){

        this.add(new GamePanel());
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}