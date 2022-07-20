import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth(), height = screenSize.getHeight();
    int SCREEN_WIDTH = (int) (width/1.5), SCREEN_HEIGHT = (int) (height/1.1);
    int UNIT_SIZE = (int) (SCREEN_WIDTH*SCREEN_HEIGHT*0.00004);
    int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
    static int speed = 200;
    private Image panel, apple;
    final int []x = new int[GAME_UNITS];
    final int []y = new int[GAME_UNITS];
    int m, n, bodyParts = 3, applesEaten, level = 1, appleX, appleY;
    char direction = 'R';
    boolean running = false, MENU = true, PAUSE = false, END = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(resizeWidth(),resizeHeight()));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.loadImages();
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(speed,this);
        timer.start();
    }
    Image resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, this);
        graphics2D.dispose();
        return resizedImage;
    }
    int resizeWidth() {
        int mod = SCREEN_WIDTH % 50;
        SCREEN_WIDTH -= mod;
        return SCREEN_WIDTH;
    }
    int resizeHeight() {
        int mod = SCREEN_HEIGHT % 50;
        SCREEN_HEIGHT -= mod;
        return SCREEN_HEIGHT;
    }
    public void loadImages(){
        ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\components\\apple.png")));
        apple = img.getImage();
        Image newimg = apple.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
        img = new ImageIcon(newimg);
        apple = img.getImage();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(MENU){
            menu(g);
        }
        else {
            try {
                panel = ImageIO.read(Objects.requireNonNull(getClass().getResource("\\images\\background\\grass.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(resizeImage((BufferedImage) panel,SCREEN_WIDTH,SCREEN_HEIGHT), 0, 0, this);
            draw(g);
            if(PAUSE) pause(g);
            if(applesEaten == 100) {
                END = true;
                winner(g);
                running = false;
            }
        }
    }
    public void draw(Graphics g) {
        if(running) {
            g.drawImage(apple,appleX,appleY,this);
            for(int i = 0; i < bodyParts;i++) {
                Image tail;
                Image turn;
                Image body;
                //head
                if(i == 0) {
                    ImageIcon img = null;
                    if(direction == 'R') img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\head\\head_right.png")));
                    if(direction == 'L') img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\head\\head_left.png")));
                    if(direction == 'U') img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\head\\head_up.png")));
                    if(direction == 'D') img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\head\\head_down.png")));
                    assert img != null;
                    Image head = img.getImage();
                    Image newimg = head.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    head = img.getImage();
                    g.drawImage(head,x[i],y[i],this);
                }

                //tail body
                else if(i+1 == bodyParts && x[i]+(UNIT_SIZE*2) == x[i-2] && y[i] == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_right.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2]+(UNIT_SIZE*2) == x[i] && y[i] == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_left.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2] == x[i] && y[i]-(UNIT_SIZE*2) == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_up.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2] == x[i] && y[i-2]-(UNIT_SIZE*2) == y[i]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_down.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }

                //tail turn
                else if(i+1 == bodyParts && x[i]+UNIT_SIZE == x[i-2] && y[i-1]+UNIT_SIZE == y[i]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_up.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2]+UNIT_SIZE == x[i] && y[i-1]+UNIT_SIZE == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_left.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2]-UNIT_SIZE == x[i] && y[i-1]+UNIT_SIZE == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_right.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i]-UNIT_SIZE == x[i-2] && y[i-1]+UNIT_SIZE == y[i]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_up.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2]-UNIT_SIZE == x[i] && y[i-1]-UNIT_SIZE == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_right.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i]-UNIT_SIZE == x[i-2] && y[i]+UNIT_SIZE == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_down.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i-2]-UNIT_SIZE == x[i] && y[i-1] == y[i]+UNIT_SIZE){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_down.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }
                else if(i+1 == bodyParts && x[i]-UNIT_SIZE == x[i-2] && y[i-1]-UNIT_SIZE == y[i-2]){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\tail\\tail_left.png")));
                    tail = img.getImage();
                    Image newimg = tail.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    tail = img.getImage();
                    g.drawImage(tail,x[i],y[i],this);
                }

                //body
                else if((x[i] == 0 && y[i] == 0 && x[i-1] == UNIT_SIZE && y[i-1] == 0 && y[i+1] != UNIT_SIZE) || ((x[i+1]+(UNIT_SIZE*2) == x[i-1] && y[i+1] == y[i-1])||(x[i-1]+(UNIT_SIZE*2) == x[i+1] && y[i+1] == y[i-1]))){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\body\\body_horizontal.png")));
                    body = img.getImage();
                    Image newimg = body.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    body = img.getImage();
                    g.drawImage(body,x[i],y[i],this);
                }
                else if((x[i-1] == x[i+1] && y[i+1]-(UNIT_SIZE*2) == y[i-1])||(x[i-1] == x[i+1] && y[i-1]-(UNIT_SIZE*2) == y[i+1])){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\body\\body_vertical.png")));
                    body = img.getImage();
                    Image newimg = body.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    body = img.getImage();
                    g.drawImage(body,x[i],y[i],this);
                }

                //turn
                else if((x[i-1]+UNIT_SIZE == x[i+1] && y[i]+UNIT_SIZE == y[i-1])||(x[i+1]+UNIT_SIZE == x[i-1] && y[i]+UNIT_SIZE == y[i+1])){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\turn\\turn_down_right.png")));
                    turn = img.getImage();
                    Image newimg = turn.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    turn = img.getImage();
                    g.drawImage(turn,x[i],y[i],this);
                }
                else if((x[i-1]-UNIT_SIZE == x[i+1] && y[i]+UNIT_SIZE == y[i-1])||(x[i+1]-UNIT_SIZE == x[i-1] && y[i]+UNIT_SIZE == y[i+1])){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\turn\\turn_left_down.png")));
                    turn = img.getImage();
                    Image newimg = turn.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    turn = img.getImage();
                    g.drawImage(turn,x[i],y[i],this);
                }
                else if((x[i-1]-UNIT_SIZE == x[i+1] && y[i]-UNIT_SIZE == y[i-1])||(x[i+1]-UNIT_SIZE == x[i-1] && y[i+1]+UNIT_SIZE == y[i-1])){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\turn\\turn_left_up.png")));
                    turn = img.getImage();
                    Image newimg = turn.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    turn = img.getImage();
                    g.drawImage(turn,x[i],y[i],this);
                }
                else if((x[i-1]-UNIT_SIZE == x[i+1] && y[i] == y[i+1]+UNIT_SIZE)||(x[i+1]-UNIT_SIZE == x[i-1] && y[i]-UNIT_SIZE == y[i-1])){
                    ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("\\images\\turn\\turn_up_right.png")));
                    turn = img.getImage();
                    Image newimg = turn.getScaledInstance(UNIT_SIZE, UNIT_SIZE,  java.awt.Image.SCALE_SMOOTH);
                    img = new ImageIcon(newimg);
                    turn = img.getImage();
                    g.drawImage(turn,x[i],y[i],this);
                }
            }
            g.setColor(Color.RED);
            g.setFont( new Font("",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Level: "+level+"   Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Level: "+level+"   Score: "+applesEaten))/2, g.getFont().getSize());
        }
        else {
            END = true;
            gameOver(g);
        }
    }
    public void newApple(){
        boolean OK = false;
        while (!OK) {
            OK = true;
            int tempX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
            int tempY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
            for (int i = 0; i < bodyParts; i++) {
                if (tempX == x[i] && tempY == y[i]) {
                    OK = false;
                    break;
                }
            }
            if (OK) {
                appleX = tempX;
                appleY = tempY;
            }
        }
    }
    public void move(){
        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            if(applesEaten == 25) {
                level++;
                speed -=50;
            }
            if(applesEaten == 50) {
                level++;
                speed -=50;
            }
            if(applesEaten == 75) {
                level++;
                speed -=50;
            }
            newApple();
        }
    }
    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts;i>0;i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //check if head touches left border
        if(x[0] < 0) {
            running = false;
        }
        //check if head touches right border
        if(x[0] == SCREEN_WIDTH || x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //check if head touches top border
        if(y[0] < 0) {
            running = false;
        }
        //check if head touches bottom border
        if(y[0] == SCREEN_HEIGHT || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running) {
            timer.stop();
        }
    }
    public void menu(Graphics g) {
        super.paintComponent(g);
        timer.stop();
        try {
            panel = ImageIO.read(Objects.requireNonNull(getClass().getResource("\\images\\background\\start.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(resizeImage((BufferedImage) panel,SCREEN_WIDTH,SCREEN_HEIGHT), 0, 0, this);
        g.setColor(Color.RED);
        g.setFont( new Font("Wide Latin",Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Snake", ((SCREEN_WIDTH - metrics1.stringWidth("Snake"))/2), (SCREEN_HEIGHT/2)-100);
        g.setColor(Color.GREEN);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press ENTER to start", ((SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to start"))/2), (SCREEN_HEIGHT/2));
        g.setColor(Color.GREEN);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to pause", ((SCREEN_WIDTH - metrics4.stringWidth("Press SPACE to pause"))/2), (SCREEN_HEIGHT/2)+50);
    }
    public void gameOver(Graphics g) {
        super.paintComponent(g);
        try {
            panel = ImageIO.read(Objects.requireNonNull(getClass().getResource("\\images\\background\\game_over.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(resizeImage((BufferedImage) panel,SCREEN_WIDTH,SCREEN_HEIGHT), 0, 0, this);
        //Score
        g.setColor(Color.RED);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.RED);
        g.setFont( new Font("",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, (SCREEN_HEIGHT/2)-100);
        //Restart and exit text
        g.setColor(Color.GREEN);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press ENTER to restart or ESCAPE to exit", ((SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to restart or ESCAPE to exit"))/2), (SCREEN_HEIGHT/2));
    }
    public void winner(Graphics g) {
        super.paintComponent(g);
        try {
            panel = ImageIO.read(Objects.requireNonNull(getClass().getResource("\\images\\background\\winner.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(resizeImage((BufferedImage) panel,SCREEN_WIDTH,SCREEN_HEIGHT), 0, 0, this);
        //Score
        g.setColor(Color.RED);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.RED);
        g.setFont( new Font("",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("YOU WINNER", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, (SCREEN_HEIGHT/2)-100);
        //Restart and exit text
        g.setColor(Color.GREEN);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press ENTER to restart or ESCAPE to exit", ((SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to restart or ESCAPE to exit"))/2), (SCREEN_HEIGHT/2));
    }
    public void pause(Graphics g) {
        super.paintComponent(g);
        timer.stop();
        try {
            panel = ImageIO.read(Objects.requireNonNull(getClass().getResource("\\images\\background\\grass.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(resizeImage((BufferedImage) panel,SCREEN_WIDTH,SCREEN_HEIGHT), 0, 0, this);
        g.setColor(Color.RED);
        g.setFont( new Font("",Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("PAUSE", ((SCREEN_WIDTH - metrics1.stringWidth("PAUSE"))/2), (SCREEN_HEIGHT/2)-100);
        g.setColor(Color.GREEN);
        g.setFont( new Font("",Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to continue", ((SCREEN_WIDTH - metrics3.stringWidth("Press Space to continue"))/2), (SCREEN_HEIGHT/2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
        boolean error = false;
        @Override
        public void keyPressed(KeyEvent e) {
            if(x[0] == m && y[0] == n && !PAUSE && !MENU && !END) return;
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R' && !PAUSE && !MENU && !END) {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L' && !PAUSE && !MENU && !END) {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D' && !PAUSE && !MENU && !END) {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U' && !PAUSE && !MENU && !END) {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if(!MENU){
                        if(PAUSE){
                            PAUSE = false;
                            timer.start();
                        }
                        else PAUSE = true;
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if(MENU){
                        MENU = false;
                        startGame();
                        return;
                    }
                    if(!running){
                        bodyParts = 3;
                        applesEaten = 0;
                        direction = 'R';
                        x[0] = 0;
                        y[0] = 0;
                        END = false;
                        startGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if(!running){
                        System.exit(0);
                    }
                    break;
                default:
                    error = true;
                    break;
            }
            m = x[0];
            n = y[0];
        }
    }
}