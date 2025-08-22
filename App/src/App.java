import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.geometry.Pos;
import javafx.scene.image.Image;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

// -------------------------- Application -------------------------- //
public class App extends Application {
    public final int TILE_SIZE = 60;   //سایز هر سلول
    public final int BOARD_SIZE = 10; // تعداد هر سلول در ردیف

    public HumanPalayer user;
    public ComputerPlayer computer;
    public Board board;
    public Dice<Integer> dice;
    public Pane boardPane;
    public Circle user_token;
    public Rectangle computerToken;
    public Label statusLabel;
    public Label statusLable_user_dice;
    public Label statusLable_com_dice;
    public Label statusLable_user_cell;
    public Label statusLable_com_cell;
    public Button rollButton;
    public VBox text_container;
    private MediaPlayer music_player;
    public boolean again = true;

    @Override
    public void start(Stage primaryStage){
        // set_background_music
        music_player = new MediaPlayer(new Media(getClass().getResource("/background_music.mp3").toExternalForm()));
        music_player.setCycleCount(MediaPlayer.INDEFINITE);
        music_player.play();

        boardPane = new Pane();
        boardPane.setPrefSize(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
        drawBoard();

        dice = new Dice<>();
        board = new Board();
        rollButton = new Button("🎲 انداختن تاس");
        user = new HumanPalayer("شخص", 0);
        computer = new ComputerPlayer("کامپوتر", 0);

        drawPlayers();

        rollButton.setOnAction(e -> move_calculate());

        statusLabel = new Label("نوبت شماست");
        text_container = new VBox(0);
        text_container.setAlignment(Pos.CENTER_RIGHT);
        text_container.setMinHeight(110);
        text_container.getChildren().add(statusLabel);

        VBox root = new VBox(25,boardPane, rollButton, text_container);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0f3d3edc");
        root.setPrefSize(TILE_SIZE * BOARD_SIZE + 10, TILE_SIZE * BOARD_SIZE + 190);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Image back_image = new javafx.scene.image.Image(getClass().getResource("/back1.png").toExternalForm());
        BackgroundImage background_Image = new BackgroundImage(back_image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );
        StackPane wrapper = new StackPane(root);
        wrapper.setBackground(new Background(background_Image));
        wrapper.setPrefSize(TILE_SIZE * BOARD_SIZE + 100, TILE_SIZE * BOARD_SIZE + 200);

        Image image = new javafx.scene.image.Image(getClass().getResource("/main_marpele1.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );
        boardPane.setBackground(new Background(bgImage));

        if(again){
            if(GameSaver.saveExists()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "بازی ذخیره‌شده‌ای وجود دارد. ادامه بدهیم؟", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if(response == ButtonType.YES){
                        try {
                            int[] positions = GameSaver.loadGame();
                            user.move(positions[0]);
                            computer.move(positions[1]);
                            updatePlayerPositions(0,0);
                        }catch(IOException ex){
                            System.out.println("a");
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
        again = true;
        Scene scene = new Scene(wrapper);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    // -------------------------- drawBoard -------------------------- //
    private void drawBoard(){
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col ++){
                int number = row * BOARD_SIZE + col + 1;
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setOpacity(0);

                drawBoard_secpart(row,col, tile);

                boardPane.getChildren().add(tile);

                // شماره‌گذاری خانه
                Label label = new Label(String.valueOf(number));
                label.setOpacity(0);
                label.setLayoutX(tile.getX() + 5);
                label.setLayoutY(tile.getY() + 5);
                label.setStyle("-fx-font-size: 12; -fx-text-fill: black;");
                boardPane.getChildren().add(label);
            }
        }
    }
    // -------------------------- drawBoard_secpart -------------------------- //
    private void drawBoard_secpart(int row,int col, Rectangle tile){
            if(row % 2 == 0){
                tile.setX(col * TILE_SIZE);
            }else{
                tile.setX((BOARD_SIZE -1 - col) * TILE_SIZE);
            }
            tile.setY((BOARD_SIZE - 1 - row) * TILE_SIZE);
    }

    // -------------------------- drawPlayers -------------------------- //
    private void drawPlayers() {
        user_token = new Circle(15, Color.BROWN);
        computerToken = new Rectangle(25, 25, Color.BLACK);
        updatePlayerPositions(0,0);
        boardPane.getChildren().addAll(user_token, computerToken);
    }

    // -------------------------- playTurn -------------------------- //
    public void move_calculate(){
        // حرکت انسان
        text_container.getChildren().clear();
        int roll = dice.roll();

        statusLable_user_dice = new Label();
        statusLable_user_dice.setText("🎲 شما انداختید: " + roll);
        statusLable_user_dice.setStyle("-fx-text-fill: #000");
        text_container.getChildren().add(statusLable_user_dice);

        int start = user.getPosition();
        int mid = Math.min(100, start + roll);
        int end = board.checkItem(mid);   // روی مار یا پله افتاده یا نه
        int _start = 0;

        if(end != mid){
            _start = board.getstrat(mid);
            user.setPosition(end);
        }else {
            user.setPosition(mid);
        }

        statusLable_user_cell = new Label();
        text_container.getChildren().add(statusLable_user_cell);
        if(end > mid) {
            statusLable_user_cell.setText("\n "+"نردبان ، از خانه ی" +_start+ "به خانه ی "+end+"رفتید");
            statusLable_user_cell.setTextFill(Color.GREEN);
        } else if(end < mid) {
            statusLable_user_cell.setText("\n" + "مار، از خانه " + _start + "به خانه ی "+ end+ "افول کردید😂");
            statusLable_user_cell.setTextFill(Color.RED);
        }

        // حرکت کامپیوتر بعد از انسان
        int compRoll = dice.roll();
        statusLable_com_dice = new Label();
        statusLable_com_dice.setText("\n کامپیوتر انداخت: " + compRoll);
        statusLable_com_dice.setStyle("-fx-text-fill: #000");
        text_container.getChildren().add(statusLable_com_dice);

        int compStart = computer.getPosition();
        int compMid = Math.min(100, compStart + compRoll);
        int compEnd = board.checkItem(compMid);
        int _start_com = board.getstrat(compMid);

        computer.setPosition(compMid);
        if (compEnd != compMid) {
            computer.setPosition(compEnd);
        }

        statusLable_com_cell = new Label();
        if (compEnd > compMid) {
            statusLable_com_cell.setText("کامپوتر از خانه ی "+_start_com+"به خانه ی "+ compEnd+"سعود کرد💪");
            statusLable_com_cell.setTextFill(Color.GREEN);
        } else if (compEnd < compMid) {
            statusLable_com_cell.setText("کامپوتر از خانه ی"+_start_com+"به خانه ی "+compEnd+ "سقوط کزد😂");
            statusLable_com_cell.setTextFill(Color.RED);
        }
        text_container.getChildren().add(statusLable_com_cell);

        updatePlayerPositions(start,compStart);

        if (user.getPosition() >= 100){
            statusLabel.setText("\n🏆 شما برنده شدید!");
            disableGame();
        }else if(computer.getPosition() >= 100){
            statusLabel.setText("\n💻 کامپیوتر برنده شد!");
            disableGame();
        }

        System.out.println("print_befor_call : "+ user.getPosition());
        saveGame();
    }


    // -------------------------- updatePlayerPositions -------------------------- //
    private void updatePlayerPositions(int start, int compstart){
        rollButton.setDisable(true);

        int humanPos = user.getPosition();
        System.out.println(humanPos);
        int computerPos = computer.getPosition();
        System.out.println(computerPos);

        SequentialTransition humanSequence = new SequentialTransition();

        if(start < humanPos){
            for (int i = start; i <= humanPos; i++) {
                int index = i - 1;
                int row = index / BOARD_SIZE;
                int col = index % BOARD_SIZE;

                double humanX;
                if (row % 2 == 0) {
                    humanX = col * TILE_SIZE + TILE_SIZE / 2;
                } else {
                    humanX = (BOARD_SIZE - 1 - col) * TILE_SIZE + TILE_SIZE / 2;
                }
                double humanY = (BOARD_SIZE - 1 - row) * TILE_SIZE + TILE_SIZE / 2;

                TranslateTransition humanAnim = new TranslateTransition(Duration.seconds(0.1), user_token);
                humanAnim.setToX(humanX - user_token.getCenterX());
                humanAnim.setToY(humanY - user_token.getCenterY());

                PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                humanSequence.getChildren().addAll(humanAnim, pause);
            }
        }else {
            for(int i = start; i >= humanPos; i--) {
                int index = i - 1;
                int row = index / BOARD_SIZE;
                int col = index % BOARD_SIZE;

                double humanX;
                if (row % 2 == 0) {
                    humanX = col * TILE_SIZE + TILE_SIZE / 2;
                } else {
                    humanX = (BOARD_SIZE - 1 - col) * TILE_SIZE + TILE_SIZE / 2;
                }
                double humanY = (BOARD_SIZE - 1 - row) * TILE_SIZE + TILE_SIZE / 2;

                TranslateTransition humanAnim = new TranslateTransition(Duration.seconds(0.1), user_token);
                humanAnim.setToX(humanX - user_token.getCenterX());
                humanAnim.setToY(humanY - user_token.getCenterY());

                PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                humanSequence.getChildren().addAll(humanAnim, pause);
            }
        }



        SequentialTransition compSequence = new SequentialTransition();
        if (computerPos > compstart){
            for (int i = compstart; i <= computerPos; i++) {
                int index = i - 1;
                int row = index / BOARD_SIZE;
                int col = index % BOARD_SIZE;

                double compX;
                if (row % 2 == 0) {
                    compX = col * TILE_SIZE + TILE_SIZE / 2 - 15;
                } else {
                    compX = (BOARD_SIZE - 1 - col) * TILE_SIZE + TILE_SIZE / 2 - 15;
                }
                double compY = (BOARD_SIZE - 1 - row) * TILE_SIZE + TILE_SIZE / 2 - 15;

                TranslateTransition compAnim = new TranslateTransition(Duration.seconds(0.1), computerToken);
                compAnim.setToX(compX - computerToken.getX());
                compAnim.setToY(compY - computerToken.getY());

                PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                compSequence.getChildren().addAll(compAnim, pause);
            }
        }else {
            for (int i = compstart; i >= computerPos; i--) {
                int index = i - 1;
                int row = index / BOARD_SIZE;
                int col = index % BOARD_SIZE;

                double compX;
                if (row % 2 == 0) {
                    compX = col * TILE_SIZE + TILE_SIZE / 2 - 15;
                } else {
                    compX = (BOARD_SIZE - 1 - col) * TILE_SIZE + TILE_SIZE / 2 - 15;
                }
                double compY = (BOARD_SIZE - 1 - row) * TILE_SIZE + TILE_SIZE / 2 - 15;

                TranslateTransition compAnim = new TranslateTransition(Duration.seconds(0.1), computerToken);
                compAnim.setToX(compX - computerToken.getX());
                compAnim.setToY(compY - computerToken.getY());

                PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                compSequence.getChildren().addAll(compAnim, pause);
            }
        }

        SequentialTransition fullSequence = new SequentialTransition(humanSequence, compSequence);
        fullSequence.setOnFinished(e -> rollButton.setDisable(false));
        fullSequence.play();
    }


    private void disableGame() {
        rollButton.setDisable(true); // غیرفعال کردن دکمه تاس

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "بازی تمام شد \n میخواهید دوباره بازی کنید؟" ,ButtonType.YES, ButtonType.NO);
        alert.setTitle("پایان بازی");
        alert.showAndWait().ifPresent(res -> {
            if(res == ButtonType.YES){
                user.setPosition(0);
                computer.setPosition(0);
                again = false;
                start(new Stage());
            }else {
                Platform.exit();
            }
        });
    }

    private void saveGame() {
        System.out.println("position for save : "+ user.getPosition());
        System.out.println("position for save : "+computer.getPosition());
        try {
            GameSaver.saveGame(user.getPosition(), computer.getPosition());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
