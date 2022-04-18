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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.claninvasion.ClanInvasion;
import com.mygdx.claninvasion.model.entity.EntitySymbol;
import com.mygdx.claninvasion.model.gamestate.Building;
import com.mygdx.claninvasion.model.player.Player;
import com.mygdx.claninvasion.view.utils.InputClicker;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.*;
import java.util.List;

public final class MainGamePageUI implements ApplicationListener {
    private final Stage uiStage;
    private final Texture backgroundTexture = new Texture("background/background.jpg");
    private final SelectBox<String> playerOneDropdown;
    private final SelectBox<String> playerTwoDropdown;
    private final TextureAtlas atlas = new TextureAtlas("skin/skin/uiskin.atlas");
    private final Skin atlasSkin = new Skin(atlas);
    private final Skin jsonSkin = new Skin(Gdx.files.internal("skin/skin/uiskin.json"));
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final Label timeLabel;
    private Label phaseLabel;
    private final ClanInvasion app;
    private EntitySymbol chosenSymbol;
    private final Table tableOne = new Table(atlasSkin);
    private final Table tableTwo = new Table(atlasSkin);

    private static final String[] dropdownItems = new String[]{ "Train Barbarian 400$", "Train Dragon 600$", "Building Tower 500%", "Build Goldmine 800$" , "Upgrade Level 1000$"};

    public MainGamePageUI(ClanInvasion app) {
        this.app = app;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        timeLabel = new Label(createTimerText(), jsonSkin);
        shapeRenderer = new ShapeRenderer();
        playerOneDropdown = new SelectBox<>(jsonSkin);
        playerTwoDropdown = new SelectBox<>(jsonSkin);
        chosenSymbol = null;
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

    private List<Pair<String, Color>> getPlayerData(Player player) {
        return new ArrayList<>(Arrays.asList(
                new Pair<>(player.getName(), Color.RED),
                new Pair<>(Float.toString(player.getWealth()), Color.BLACK),
                new Pair<>("Health: " + player.getHealth(), Color.BLACK),
                new Pair<>(player.getTowers().size() + " towers", Color.BLACK),
                new Pair<>(player.getSoldiers().size() + " soldiers", Color.BLACK),
                new Pair<>("Level "+ player.getCastle().getLevel().getLevelName(), Color.BLACK)));
    }

    private void parsePlayerDataToView(Player player, Table table, SelectBox<String> box) {
        List<Pair<String, Color>> player1Data = getPlayerData(player);
        setPlayerData(table, box, player1Data);
    }

    private void updatePlayerData(Player player, Table table) {
        List<Pair<String, Color>> player1Data = getPlayerData(player);
        int counter = 0;
        for (int i = 0; i < table.getCells().size; i++) {
            if (table.getCells().get(i).getActor() instanceof Label) {
                ((Label) table.getCells().get(i).getActor()).setText(player1Data.get(counter).getValue0());
                counter++;
            }
        }
    }

    private void addBottomBar() {
        tableOne.setBounds(160, -200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Player player1 = app.getModel().getPlayerOne();
        parsePlayerDataToView(player1, tableOne, playerOneDropdown);

        tableTwo.setBounds(-160, -200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Player player2 = app.getModel().getPlayerTwo();
        parsePlayerDataToView(player2, tableTwo, playerTwoDropdown);
    }

    private void addButtonListeners() {
        playerOneDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StringBuilder selected = new StringBuilder(playerOneDropdown.getSelected().split(" ")[1]);
                System.out.println(selected);
                if(selected.toString().equals("Tower")){
                    chosenSymbol = EntitySymbol.TOWER;
                    InputClicker.enabled = true;
                }
                else if(selected.toString().equals("Goldmine")){
                    chosenSymbol = EntitySymbol.MINING;
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
        return ((Building)app.getModel().getState()).getCounter() + " seconds";
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
        app.getModel().getState().initState();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        app.getModel().updateState(Gdx.graphics.getDeltaTime(), () -> timeLabel.setText(createTimerText()));
        updatePlayerData(app.getModel().getPlayerOne(), tableOne);
        updatePlayerData(app.getModel().getPlayerTwo(), tableTwo);
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

    public EntitySymbol getChosenSymbol() {
        return chosenSymbol;
    }
}
