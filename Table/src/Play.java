import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Play extends JFrame implements Runnable {
  public static int alpha = 0xFFFF00DC;
  public boolean menuShow = true;
  public boolean creditShow = false;
  public boolean helpShow = false;
  public boolean tableShow = false;
  public int menuSelect = 1;
  File sound1;
  File sound2;
  Clip menuc;
  Clip creditc;
  Clip clip;
  AudioInputStream menu;
  AudioInputStream credit;
  private ArrayList<Map.MappedTile> mappedTiles = new ArrayList<Map.MappedTile>();
  private Canvas canvas = new Canvas();
  private RenderHandler renderer;
  private SpriteSheet sheet;
  private SpriteSheet playerSheet;
  private int selectedTileID = 2;
  private int selectedLayer = 0;
  private Rectangle testRectangle = new Rectangle(30, 30, 80, 80);
  private Tiles tiles;
  private Map map;
  private ObjectLoader[] objects;
  private KeyBoardListener keyListener = new KeyBoardListener(this);
  private MouseEventListener mouseListener = new MouseEventListener(this);
  private Player player;
  private int xZoom = 3;
  private int yZoom = 3;


  public Play() {
    //Make our program shutdown when we exit out.

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Set the position and size of our frame.
    setBounds(0, 0, 800, 700);

    //Put our frame in the center of the screen.
    setLocationRelativeTo(null);

    //Add our graphics compoent
    add(canvas);

    //Make our frame visible.
    setVisible(true);

    //Create our object for buffer strategy.
    canvas.createBufferStrategy(3);

    renderer = new RenderHandler(getWidth(), getHeight());

    //Load Assets
    BufferedImage sheetImage = loadImage("./Tiles1.png");
    sheet = new SpriteSheet(sheetImage);
    sheet.loadSprites(16, 16);

    BufferedImage playerSheetImage = loadImage("./Player.png");
    playerSheet = new SpriteSheet(playerSheetImage);
    playerSheet.loadSprites(20, 26);

    //Player Animated Sprites
    AnimatedSprite playerAnimations = new AnimatedSprite(playerSheet, 5);

    //Load Tiles
    tiles = new Tiles(new File("./src/Tiles.txt"), sheet);

    //Load Map

    map = loadMapTxt();

    testRectangle.generateGraphics(2, 12234);

    objects = new ObjectLoader[2];


    player = new Player(playerAnimations, xZoom, yZoom);
    objects[0] = player;

    //Load SDK GUI
    GUIButton[] buttons = new GUIButton[tiles.size()];
    Sprite[] tileSprites = tiles.getSprites();

    for (int i = 0; i < buttons.length; i++) {
      Rectangle tileRectangle = new Rectangle(0, i * (16 * xZoom + 2), 16 * xZoom, 16 * yZoom);

      buttons[i] = new SDKButton(this, i, tileSprites[i], tileRectangle);
    }

    GUI gui = new GUI(buttons, 5, 5, true);
    objects[1] = gui;


    //Add Listeners
    canvas.addKeyListener(keyListener);
    canvas.addFocusListener(keyListener);
    canvas.addMouseListener(mouseListener);
    canvas.addMouseMotionListener(mouseListener);

    addComponentListener(new ComponentListener() {
      public void componentResized(ComponentEvent e) {
        int newWidth = canvas.getWidth();
        int newHeight = canvas.getHeight();

        if (newWidth > renderer.getMaxWidth())
          newWidth = renderer.getMaxWidth();

        if (newHeight > renderer.getMaxHeight())
          newHeight = renderer.getMaxHeight();

        renderer.getCamera().w = newWidth;
        renderer.getCamera().h = newHeight;
        canvas.setSize(newWidth, newHeight);
        pack();
      }

      public void componentHidden(ComponentEvent e) {
      }

      public void componentMoved(ComponentEvent e) {
      }

      public void componentShown(ComponentEvent e) {
      }
    });
    canvas.requestFocus();
  }

  public static void main(String[] args) {


    Play play = new Play();
    Thread gameThread = new Thread(play);
    gameThread.start();


  }

  public Map loadMapTxt() {
    this.map = new Map(new File("./src/Map.txt"), tiles);
    return map;
  }

  public void update() {
    BufferStrategy bufferStrategy = canvas.getBufferStrategy();
    Graphics graphics = bufferStrategy.getDrawGraphics();
    for (int i = 0; i < objects.length; i++)
      objects[i].update(this, menuShow);
    super.paint(graphics);
  }

  private BufferedImage loadImage(String path) {
    try {
      BufferedImage loadedImage = ImageIO.read(Play.class.getResource(path));
      BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
      formattedImage.getGraphics().drawImage(loadedImage, 0, 0, null);

      return formattedImage;
    } catch (IOException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public void handleCTRL(boolean[] keys) {
    if (keys[KeyEvent.VK_F]) {   //Save Map
      map.saveMap();
    }

    if (keys[KeyEvent.VK_L]) { //Change layers
      if (selectedLayer == 1)
        selectedLayer = 0;
      if (selectedLayer == 0)
        selectedLayer = 1;

    }

  }

  public void Interacting() {
    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    Date dateobj = new Date();
    System.out.println("Keypressed e");
    LoadDB db = new LoadDB();
    String date = (String) df.format(dateobj);
    db.PushDB(date);

  }

  public void leftClick(int x, int y) {

    Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
    boolean stoppedChecking = false;

    for (int i = 0; i < objects.length; i++)
      if (!stoppedChecking)
        stoppedChecking = objects[i].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);

    if (!stoppedChecking) {
      x = (int) Math.floor((x + renderer.getCamera().x) / (16.0 * xZoom));
      y = (int) Math.floor((y + renderer.getCamera().y) / (16.0 * yZoom));
      map.setTile(selectedLayer, x, y, selectedTileID);
    }
  }

  public void rightClick(int x, int y) {
    x = (int) Math.floor((x + renderer.getCamera().x) / (16.0 * xZoom));
    y = (int) Math.floor((y + renderer.getCamera().y) / (16.0 * yZoom));
    map.removeTile(selectedLayer, x, y);

  }

  public void render() {
    BufferStrategy bufferStrategym = canvas.getBufferStrategy();
    Graphics graphicsm = bufferStrategym.getDrawGraphics();
    super.paint(graphicsm);

    if (menuShow) {

      Font fnt = new Font("arial", 1, 50);
      Font fnt2 = new Font("arial", 1, 30);

      graphicsm.setColor(Color.black);
      graphicsm.fillRect(0, 0, getWidth(), getHeight());
      graphicsm.fillRect(290, 150, 200, 64);
      graphicsm.fillRect(290, 250, 200, 64);
      graphicsm.fillRect(290, 350, 200, 64);

      graphicsm.setFont(fnt);
      graphicsm.setColor(Color.white);
      graphicsm.drawString("Menu", 330, 70);

      graphicsm.setFont(fnt2);
      graphicsm.drawString("Play", 360, 190);
      graphicsm.drawString("Help", 360, 290);
      graphicsm.drawString("About", 350, 390);
      graphicsm.drawString("Load", 360, 490);
      graphicsm.drawString("Quit", 360, 590);

      graphicsm.drawRect(290, 150, 200, 64);
      graphicsm.drawRect(290, 250, 200, 64);
      graphicsm.drawRect(290, 350, 200, 64);
      graphicsm.drawRect(290, 450, 200, 64);
      if (menuSelect == 1)
        graphicsm.setColor(Color.red);
      graphicsm.drawRect(290, 150, 200, 64);

      graphicsm.setColor(Color.white);

      if (menuSelect == 2)
        graphicsm.setColor(Color.red);
      graphicsm.drawRect(290, 250, 200, 64);

      graphicsm.setColor(Color.white);

      if (menuSelect == 3)
        graphicsm.setColor(Color.red);
      graphicsm.drawRect(290, 350, 200, 64);

      graphicsm.setColor(Color.white);

      if (menuSelect == 4)
        graphicsm.setColor(Color.red);
      graphicsm.drawRect(290, 450, 200, 64);

      graphicsm.setColor(Color.white);

      if (menuSelect == 5)
        graphicsm.setColor(Color.red);
      graphicsm.drawRect(290, 550, 200, 64);

      graphicsm.dispose();
      bufferStrategym.show();
      renderer.clear();
    } else if (creditShow) {

      Font fnt = new Font("arial", 1, 50);
      Font fnt2 = new Font("arial", 1, 20);
      Font fnt3 = new Font("arial", 1, 20);

      graphicsm.setColor(Color.black);
      graphicsm.fillRect(0, 0, getWidth(), getHeight());

      graphicsm.setFont(fnt);
      graphicsm.setColor(Color.white);
      graphicsm.drawString("About", 320, 70);

      graphicsm.setFont(fnt3);
      graphicsm.drawString("Created By:", 200, 200);

      graphicsm.setFont(fnt3);
      graphicsm.drawString("Gliston", 355, 200);
      graphicsm.drawRect(333, 174, 110, 40);

      graphicsm.drawString("Ashley", 358, 260);
      graphicsm.drawRect(333, 234, 110, 40);

      graphicsm.drawString("Ian", 370, 320);
      graphicsm.drawRect(333, 294, 110, 40);


      graphicsm.dispose();
      bufferStrategym.show();

    } else if (helpShow) {


      Font fnt = new Font("arial", 1, 50);
      Font fnt2 = new Font("arial", 1, 20);
      Font fnt3 = new Font("arial", 1, 20);

      graphicsm.setColor(Color.black);
      graphicsm.fillRect(0, 0, getWidth(), getHeight());

      graphicsm.setFont(fnt);
      graphicsm.setColor(Color.white);
      graphicsm.drawString("Help", 320, 70);

      graphicsm.setFont(fnt3);
      graphicsm.drawString("Use WSAD or Arrow keys to move the player", 200, 200);
      graphicsm.drawString("Place a block by LeftClick", 200, 260);
      graphicsm.drawString("Remove a block by RightClick", 200, 320);
      graphicsm.drawString("Change Layers by Ctrl+L", 200, 380);
      graphicsm.drawString("Save Map By Ctrl+F", 200, 440);
      graphicsm.drawString("Press ESC for Main Menu", 200, 500);
      graphicsm.drawString("Press E to Upload Map to Database", 200, 550);

      graphicsm.dispose();
      bufferStrategym.show();
      renderer.clear();

    } else {
      BufferStrategy bufferStrategy = canvas.getBufferStrategy();
      Graphics graphics = bufferStrategy.getDrawGraphics();
      super.paint(graphics);

      map.render(renderer, objects, xZoom, yZoom);

      renderer.render(graphics);

      graphics.dispose();
      bufferStrategy.show();
      renderer.clear();
    }


  }

  public void changeTile(int tileID) {
    selectedTileID = tileID;
  }

  public int getSelectedTile() {
    return selectedTileID;
  }

  public void run() {
    try {
      sound1 = new File("./src/menu2.wav");
      sound2 = new File("./src/credit.wav");
      menuc = AudioSystem.getClip();
      creditc = AudioSystem.getClip();
      clip = AudioSystem.getClip();
      menu = AudioSystem.getAudioInputStream(sound1);
      credit = AudioSystem.getAudioInputStream(sound2);
      creditc.open(credit);
      menuc.open(menu);

    } catch (Exception e) {

    }

    BufferStrategy bufferStrategy = canvas.getBufferStrategy();
    int i = 0;
    int x = 0;

    long lastTime = System.nanoTime(); //long 2^63
    double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
    double changeInSeconds = 0;

    while (true) {
      try {

        if (creditShow) {
          if (menuc.isRunning()) {
            menuc.stop();
          }
          if (!(creditc.isRunning())) {
            creditc.setMicrosecondPosition(0);
            creditc.loop(-1);
          }
        }

        if (menuShow) {
          if (creditc.isRunning()) {
            creditc.stop();
          }
          if (!(menuc.isRunning())) {
            menuc.setMicrosecondPosition(0);
            menuc.loop(-1);
          }
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      long now = System.nanoTime();

      changeInSeconds += (now - lastTime) / nanoSecondConversion;
      while (changeInSeconds >= 2) {
        update();
        changeInSeconds--;

      }

      render();
      lastTime = now;

    }

  }

  public void PlaySoundM() throws Exception {

    File file = new File("./src/menu2.wav");
    Clip clip = AudioSystem.getClip();
    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
    clip.open(ais);
    clip.loop(-1);

  }

  public void PlaySoundC() throws Exception {

    File file = new File("./src/credit.wav");
    Clip clip = AudioSystem.getClip();
    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
    clip.open(ais);
    clip.loop(-1);

  }

  public void MenuKey(int menuSelect) {


    switch (menuSelect) {
      case 1:
        menuShow = false;
        creditShow = false;
        helpShow = false;
        tableShow = false;

        break;
      case 2:
        menuShow = false;
        creditShow = false;
        helpShow = true;
        tableShow = false;

        break;

      case 3:
        menuShow = false;
        creditShow = true;
        helpShow = false;
        tableShow = false;
        break;

      case 4:
        tableShow = true;
        LoadMap();
        break;

      case 5:
        System.out.println("Goodbye.. Exiting");
        System.exit(1);

      default:
        System.out.println("Something Wrong Happened Here");
    }

  }

  public void LoadMap() {
    Table1 frame = new Table1();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        loadMapTxt();

      }
    });
  }

  public KeyBoardListener getKeyListener() {
    return keyListener;
  }


  public MouseEventListener getMouseListener() {
    return mouseListener;
  }

  public RenderHandler getRenderer() {
    return renderer;
  }

  public Map getMap() {
    return map;
  }

  public int getXZoom() {
    return xZoom;
  }

  public int getYZoom() {
    return yZoom;
  }
}