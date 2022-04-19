package com.mygdx.claninvasion.view.applicationlistener;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.claninvasion.ClanInvasion;
import com.mygdx.claninvasion.model.entity.*;
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
    private final Label phaseLabel;
    private final Label turnLabel;
    private final ClanInvasion app;
    private EntitySymbol chosenSymbol;
    private final Table tableOne = new Table(atlasSkin);
    private final Table tableTwo = new Table(atlasSkin);

    private static final String[] dropdownItems = new String[]{
            "Train Barbarian " + Barbarian.COST + "$",
            "Train Dragon " + Dragon.COST + "$",
            "Building Tower " + Tower.COST + "$",
            "Build Goldmine " + MiningFarm.COST + "$" ,
            "Upgrade Level 1000$"
    };

    public MainGamePageUI(ClanInvasion app) {
        this.app = app;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        timeLabel = new Label(getTimerText(), jsonSkin);
        turnLabel = new Label(getPlayerTopBar() , jsonSkin);
        phaseLabel = new Label(getPlayerPhase() , jsonSkin);
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

    private String getPlayerTopBar() {
        return "Turn: " + app.getCurrentPlayer().getName();
    }

    private String getPlayerPhase() {
        return "Phase: " + app.getModel().getPhase();
    }

    private String getTimerText() {
        return ((Building)app.getModel().getState()).getCounter() + " seconds";
    }

    private void addTopBar() {
        Table topTable = new Table(atlasSkin);
        topTable.setBounds(-10, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
                new Pair<>(player.getTrainingSoldiers().size() + " soldiers", Color.BLACK),
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

    private void updateActiveDropdown() {
        if (app.getModel().getActivePlayer().equals(app.getModel().getPlayerOne())) {
            playerOneDropdown.setDisabled(false);
            playerTwoDropdown.setDisabled(true);
        } else {
            playerOneDropdown.setDisabled(true);
            playerTwoDropdown.setDisabled(false);
        }
    }

    private void updateTopBar() {
        phaseLabel.setText(getPlayerPhase());
        turnLabel.setText(getPlayerTopBar());
    }

    private void addPlayerListener(Player player, SelectBox<String> selectBox) {
        Table table = app.getModel().getActivePlayer().equals(app.getModel().getPlayerOne()) ?
                tableOne : tableTwo;
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!player.equals(app.getCurrentPlayer())) {
                    return;
                }
                System.out.println(selectBox.getSelected().split(" ")[1]);
                switch (selectBox.getSelected().split(" ")[1]) {
                    case "Tower":
                        if (player.canCreateTower()) {
                            chosenSymbol = EntitySymbol.TOWER;
                            InputClicker.enabled = true;
                        } else {
                            System.out.println("Not enough money for this action");
                        }
                        break;
                    case "Goldmine":
                        if (player.canCreateMining()) {
                            chosenSymbol = EntitySymbol.MINING;
                            InputClicker.enabled = true;
                        } else {
                            System.out.println("Not enough money for this action");
                        }
                        break;
                    case "Level":
                        System.out.println("You are about to update");
                        if(player.canUpdateLevel()) {
                            System.out.println("You have funds to update");
                            player.levelUp();
                        }
                        break;
                    case "Dragon":
                        if (player.canCreateBarbarian()) {
                            player.trainSoldiers(EntitySymbol.DRAGON, () -> {
                                System.out.println("New dragon trained");
                                updatePlayerData(player, table);
                            });
                        } else {
                            System.out.println("Not enough money for this action");
                        }
                        break;
                    case "Barbarian":
                        if (player.canCreateDragon()) {
                            player.trainSoldiers(EntitySymbol.BARBARIAN, () -> {
                                System.out.println("New barbarian trained");
                                updatePlayerData(player, table);
                            });
                        } else {
                            System.out.println("Not enough money for this action");
                        }
                        break;
                }
            }
        });
    }

    private void addButtonListeners() {
        addPlayerListener(app.getModel().getPlayerOne(), playerOneDropdown);
        addPlayerListener(app.getModel().getPlayerTwo(), playerTwoDropdown);

        uiStage.setDebugUnderMouse(true);
        Gdx.input.setInputProcessor(uiStage);
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
        app.getModel().updateState(Gdx.graphics.getDeltaTime(), () -> {
            if (app.getModel().getState() instanceof Building) {
                timeLabel.setText(getTimerText());
            }
        });
        updatePlayerData(app.getModel().getPlayerOne(), tableOne);
        updatePlayerData(app.getModel().getPlayerTwo(), tableTwo);
        updateTopBar();
        updateActiveDropdown();
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
