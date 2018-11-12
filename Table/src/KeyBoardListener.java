import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoardListener implements KeyListener, FocusListener {
  public boolean[] keys = new boolean[120];

  private Play play;
  private boolean menuShow = true;


  public KeyBoardListener(Play play) {
    this.play = play;
  }


  public void keyPressed(KeyEvent event) {

    int keyCode = event.getKeyCode();

    if (keyCode < keys.length)
      keys[keyCode] = true;

    if (keys[KeyEvent.VK_CONTROL])
      play.handleCTRL(keys);

    if (keys[KeyEvent.VK_E])
      play.Interacting();

    if (keys[KeyEvent.VK_ESCAPE]) {
      play.creditShow = false;
      play.helpShow = false;
      play.menuShow = true;
      System.out.println("Menu Show");

    }

    if (keys[KeyEvent.VK_ENTER]) {
      System.out.println("Enter key");
      if (play.menuShow)
        play.MenuKey(play.menuSelect);
    }

    if (keys[KeyEvent.VK_UP]) {
      if (menuShow) {
        MenuSelector(0);
      }
    }

    if (keys[KeyEvent.VK_DOWN]) {
      if (menuShow) {
        MenuSelector(1);
      }
    }

  }

  public void keyReleased(KeyEvent event) {
    int keyCode = event.getKeyCode();

    if (keyCode < keys.length)
      keys[keyCode] = false;
  }

  public void focusLost(FocusEvent event) {
    for (int i = 0; i < keys.length; i++)
      keys[i] = false;
  }

  public void keyTyped(KeyEvent event) {
  }

  public void focusGained(FocusEvent event) {
  }

  public boolean up() {

    return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
  }

  public boolean e() {
    return keys[KeyEvent.VK_E];
  }

  public boolean down() {

    return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
  }

  public boolean left() {

    return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
  }

  public boolean right() {

    return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
  }

  public void MenuSelector(int i) {

    switch (i) {
      case 0: //UP
        if (play.menuSelect == 1) {
          play.menuSelect = 5;
        } else {
          play.menuSelect--;
        }
        break;
      case 1: //DOWN
        if (play.menuSelect == 5) {
          play.menuSelect = 1;
        } else {
          play.menuSelect++;
        }
        break;

      default:
        System.out.println("Something Wrong Happened Here 2");
    }

  }

}