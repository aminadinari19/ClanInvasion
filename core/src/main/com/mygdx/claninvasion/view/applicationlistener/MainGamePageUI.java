package com.mygdx.claninvasion.view.applicationlistener;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.claninvasion.ClanInvasion;
import com.mygdx.claninvasion.model.entity.EntitySymbol;
import com.mygdx.claninvasion.view.utils.InputClicker;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.*;

public class MainGamePageUI implements ApplicationListener {
    private final Stage uiStage;
    private final Texture backgroundTexture = new Texture("background/background.jpg");
    private final SelectBox<String> playerOneDropdown;
    private final SelectBox<String> playerTwoDropdown;
    private final TextureAtlas atlas = new TextureAtlas("skin/skin/uiskin.atlas");
    private final Skin atlasSkin = new Skin(atlas);
    private final Skin jsonSkin = new Skin(Gdx.files.internal("skin/skin/uiskin.json"));
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private float timeSeconds = 0f;
    private float period = 1f;
    private Timer time;
    private int counter = 30;
    private int totalTime = 0;
    private final Label timeLabel;
    private Label phaseLabel;
    private final ClanInvasion app;
    private EntitySymbol choosenSymbol;

    private static final String[] dropdownItems = new String[]{ "Train Barbarian 400$", "Train Dragon 600$", "Building Tower 500%", "Build Goldmine 800$" , "Upgrade Level 1000$"};

    public MainGamePageUI(ClanInvasion app) {
        this.app = app;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        timeLabel = new Label(createTimerText(), jsonSkin);
        shapeRenderer = new ShapeRenderer();
        playerOneDropdown = new SelectBox<>(jsonSkin);
        playerTwoDropdown = new SelectBox<>(jsonSkin);
        choosenSymbol = null;
    }

    private void createRectangle(Color color, Quartet<Float, Float, Float, Float> region, float width) {
        shapeRenderer.setColor(color);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rectLine(region.getValue0(), region.getValue1(), region.getValue2(), region.getValue3(), width);
        shapeRenderer.end();
    }

    private void addSeparationLines() {
        createRectangle(Color.BLACK, new Quartet<>(-100f, 425f, 1100f, 425f), 2);
        createRectangle(Color.BLACK, new Quartet<>(-100f, 85f, 1100f, 85f), 2);
    }

    private void addTopBar() {
        Table topTable = new Table(atlasSkin);
        topTable.setBounds(-10, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Label turnLabel = new Label("Turn: " + app.getCurrentPlayer().getName() , jsonSkin);
        phaseLabel = new Label("Phase: " + app.getModel().getPhase() , jsonSkin);

        turnLabel.setColor(Color.BLACK);
        timeLabel.setColor(Color.BLACK);
        phaseLabel.setColor(Color.BLACK);

        topTable.add(turnLabel).spaceLeft(2);
        topTable.add(timeLabel).spaceLeft(100);
        topTable.add(phaseLabel).spaceLeft(100);
        uiStage.addActor(topTable);
    }

    private void initDropDown(SelectBox<String> box, String[] values) {
        box.setSize(5f , 5f);
        box.setItems(values);
        box.setTouchable(Touchable.enabled);
    }

    private void setPlayerData(Table table, SelectBox<String> box, List<Pair<String, Color>> playerData) {
        initDropDown(box, dropdownItems);
        List<Label> labels = new ArrayList<>();
        for (Pair<String, Color> data : playerData) {
            Label label = new Label(data.getValue0(), jsonSkin);
            label.setColor(data.getValue1());
            labels.add(label);
        }

        int index = 0;
        for (; index < 2; index++) {
            table.add(labels.get(index));
        }
        table.add(box);
        table.row();

        for ( ; index < 5; index++) {
            table.add(labels.get(index));
        }
        table.row();
        table.add(labels.get(index)).spaceLeft(0);
        uiStage.addActor(table);
    }

    private void addBottomBar() {
        Table tableOne = new Table(atlasSkin);
        tableOne.setBounds(160, -200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        List<Pair<String, Color>> player1Data = new ArrayList<>(Arrays.asList(
                new Pair<>("Player1", Color.RED),
                new Pair<>("$ 3000", Color.BLACK),
                new Pair<>("Healthy", Color.BLACK),
                new Pair<>("10 towers", Color.BLACK),
                new Pair<>("50 soldiers", Color.BLACK),
                new Pair<>("Level 0", Color.BLACK)));
        setPlayerData(tableOne, playerOneDropdown, player1Data);

        Table tableTwo = new Table(atlasSkin);
        tableTwo.setBounds(-160, -200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        List<Pair<String, Color>> player2Data = new ArrayList<>(Arrays.asList(
                new Pair<>("Player2", Color.BLUE),
                new Pair<>("$ 3000", Color.BLACK),
                new Pair<>("Healthy", Color.BLACK),
                new Pair<>("10 towers", Color.BLACK),
                new Pair<>("50 soldiers", Color.BLACK),
                new Pair<>("Level 0", Color.BLACK)));
        setPlayerData(tableTwo, playerTwoDropdown, player2Data);
    }

    private void setTimer() {
        time = new Timer();
        time.schedule(new TimerTask(){
            @Override
            public void run() {
                totalTime++;
            }
        }, 1);
    }

    private void addButtonListeners() {
        playerOneDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StringBuilder selected = new StringBuilder(playerOneDropdown.getSelected().split(" ")[1]);
                System.out.println(selected);
                if(selected.toString().equals("Tower")){
                    choosenSymbol = EntitySymbol.TOWER;
                    InputClicker.enabled = true;
                }
                else if(selected.toString().equals("Goldmine")){
                    choosenSymbol = EntitySymbol.MINING;
                    InputClicker.enabled = true;

                }
                else if (selected.toString().equals("Level")){


                }
                else if (selected.toString().equals("Barbarian ")){
                   // app.getCurrentPlayer().addSoldiers();

                }
                else if(selected.toString().equals("Dragon")){

                }
                else if(selected.toString().equals("Level")){

                }


            }
        });
        playerTwoDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Player2: " + " " + playerTwoDropdown.getSelected());
                InputClicker.enabled = true;
            }
        });

        uiStage.setDebugUnderMouse(true);
        Gdx.input.setInputProcessor(uiStage);
    }

    private String createTimerText() {
        return counter + " seconds";
    }

    private void updateTime() {
        if (counter > 0) {
            totalTime++;
            counter--;
            timeLabel.setText(createTimerText());
        } else if (counter == 0 && totalTime <= 60) {
            counter = 30;
        } else if (totalTime > 60) {
            changePhase();
        }
    }

    private void changePhase() {
        app.getModel().changePhase();
        phaseLabel.setText("Phase: " + app.getModel().getPhase());
    }

    private void createBarBackground() {
        SpriteBatch batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 1000, 85);
        batch.end();
        batch.begin();
        batch.draw(backgroundTexture, 0, 425, 1000, 85);
        batch.end();
    }

    @Override
    public void create() {
        addTopBar();
        addBottomBar();
        addButtonListeners();
        setTimer();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        timeSeconds += Gdx.graphics.getDeltaTime();
        if (timeSeconds > period) {
            timeSeconds -= period;
            updateTime();
        }

        createBarBackground();
        addSeparationLines();
        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }

    public EntitySymbol getChoosenSymbol() {
        return choosenSymbol;
    }
}