public class Player implements ObjectLoader {
  private final int xCollisionOffset = 14;
  private final int yCollisionOffset = 20;
  private Rectangle playerRectangle;
  private Rectangle collisionCheckRectangle;
  private int speed = 10;
  private boolean menuCheck;
  //0 = Right, 1 = Left, 2 = Up, 3 = Down
  private int direction = 0;
  private int layer = 0;
  private Sprite sprite;
  private AnimatedSprite animatedSprite = null;

  public Player(Sprite sprite, int xZoom, int yZoom) {
    this.sprite = sprite;

    if (sprite != null && sprite instanceof AnimatedSprite)
      animatedSprite = (AnimatedSprite) sprite;

    updateDirection();
    playerRectangle = new Rectangle(-90, 0, 20, 26);
    playerRectangle.generateGraphics(3, 0xFF00FF90);
    collisionCheckRectangle = new Rectangle(0, 0, 10 * xZoom, 15 * yZoom);
  }

  private void updateDirection() {
    if (animatedSprite != null) {
      // animatedSprite.setAnimationRange(direction * 8, (direction * 8) + 7);
      animatedSprite.setAnimationRange(direction * 8, (direction * 8) + 7);
    }
  }

  //Call every time physically possible.
  public void render(RenderHandler renderer, int xZoom, int yZoom) {
    if (animatedSprite != null)
      renderer.renderSprite(animatedSprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
    else if (sprite != null)
      renderer.renderSprite(sprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
    else
      renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);

  }

  //Call at 60 fps rate.
  public void update(Play play, boolean menuShow) {

    this.menuCheck = !menuShow;
    KeyBoardListener keyListener = play.getKeyListener();

    boolean didMove = false;
    int newDirection = direction;

    collisionCheckRectangle.x = playerRectangle.x;
    collisionCheckRectangle.y = playerRectangle.y;

    if (keyListener.left() && menuCheck) {
      newDirection = 1;
      didMove = true;
      collisionCheckRectangle.x -= speed;
    }
    if (keyListener.right() && menuCheck) {
      newDirection = 0;
      didMove = true;
      collisionCheckRectangle.x += speed;
    }
    if (keyListener.up() && menuCheck) {
      collisionCheckRectangle.y -= speed;
      didMove = true;
      newDirection = 2;
    }
    if (keyListener.down() && menuCheck) {
      newDirection = 3;
      didMove = true;
      collisionCheckRectangle.y += speed;
    }

    if (newDirection != direction) {
      direction = newDirection;
      updateDirection();
    }


    if (!didMove) {
      animatedSprite.reset();
    }

    if (didMove) {

      // collisionCheckRectangle.x += xCollisionOffset;
      // collisionCheckRectangle.y += yCollisionOffset;

      // if(!play.getMap().checkCollision(collisionCheckRectangle, layer, play.getXZoom(), play.getYZoom()) &&
      // 	!play.getMap().checkCollision(collisionCheckRectangle, layer + 1, play.getXZoom(), play.getYZoom())) {
      // 	playerRectangle.x = collisionCheckRectangle.x - xCollisionOffset;
      // }


      // animatedSprite.update(play);


      collisionCheckRectangle.x += xCollisionOffset;
      collisionCheckRectangle.y += yCollisionOffset;

      Rectangle axisCheck = new Rectangle(collisionCheckRectangle.x, playerRectangle.y + yCollisionOffset, collisionCheckRectangle.w, collisionCheckRectangle.h);

      //Check the X axis
      if (!play.getMap().checkCollision(axisCheck, layer, play.getXZoom(), play.getYZoom()) &&
              !play.getMap().checkCollision(axisCheck, layer + 1, play.getXZoom(), play.getYZoom())) {
        playerRectangle.x = collisionCheckRectangle.x - xCollisionOffset;
      }

      axisCheck.x = playerRectangle.x + xCollisionOffset;
      axisCheck.y = collisionCheckRectangle.y;
      axisCheck.w = collisionCheckRectangle.w;
      axisCheck.h = collisionCheckRectangle.h;
      //axisCheck = new Rectangle(playerRectangle.x, collisionCheckRectangle.y, collisionCheckRectangle.w, collisionCheckRectangle.h);

      //Check the Y axis
      if (!play.getMap().checkCollision(axisCheck, layer, play.getXZoom(), play.getYZoom()) &&
              !play.getMap().checkCollision(axisCheck, layer + 1, play.getXZoom(), play.getYZoom())) {
        playerRectangle.y = collisionCheckRectangle.y - yCollisionOffset;
      }


      animatedSprite.update(play, false);
    }

    updateCamera(play.getRenderer().getCamera());
  }

  public void updateCamera(Rectangle camera) {
    camera.x = playerRectangle.x - (camera.w / 2);
    camera.y = playerRectangle.y - (camera.h / 2);
  }

  public int getLayer() {
    return layer;
  }

  public Rectangle getRectangle() {
    return playerRectangle;
  }

  //Call whenever mouse is clicked on Canvas.
  public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
    return false;
  }
}