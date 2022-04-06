package com.mygdx.claninvasion.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.claninvasion.ClanInvasion;
import com.mygdx.claninvasion.view.actors.GameButton;
import com.mygdx.claninvasion.view.renderer.IsometricTiledMapGameRendereder;
import com.mygdx.claninvasion.view.tiledmap.TiledMapStage;
import com.mygdx.claninvasion.view.utils.GameInputProcessor;

import java.awt.*;


public class MainGamePage implements GamePage, UiUpdatable {
    private TiledMap map;
    private IsometricTiledMapGameRendereder renderer;
    private GameInputProcessor inputProcessor;
    private final ClanInvasion app;
    private Stage entitiesStage;
    private Stage uiStage;
    private final OrthographicCamera camera;
    private GameButton soldierButton;
    private GameButton towerButton;
    private GameButton mineButton;


    public MainGamePage(ClanInvasion app) {
        this.app = app;
        camera = new OrthographicCamera();
        uiStage = new Stage();
    }

    private void addButtons() {
        TextureAtlas atlas = new TextureAtlas("skin/skin/uiskin.atlas");
        Skin skin = new Skin(atlas);
        Table table = new Table(skin);
        table.setBounds(-145, 150, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        soldierButton = new GameButton(skin, "Train soldiers");
        towerButton = new GameButton(skin, "Place towers");
        mineButton = new GameButton(skin, "Place goldmine");
        soldierButton.getButton().pad(2);
        towerButton.getButton().pad(2);
        mineButton.getButton().pad(2);
        table.add(soldierButton.getButton()).space(10);
        table.add(towerButton.getButton()).spaceLeft(10);
        table.add(mineButton.getButton()).spaceLeft(10);
        uiStage.addActor(table);
    }


    @Override
    public void show() {
//        app.getCamera().update();

        inputProcessor = new GameInputProcessor(app.getCamera());
        map = new TmxMapLoader().load(Gdx.files.getLocalStoragePath() + "/TileMap/Tilemap.tmx");
        renderer = new IsometricTiledMapGameRendereder(map, 1);
        entitiesStage = new TiledMapStage();
//        renderer.getActors().forEach(actor -> stage.addActor(actor));
//        stage = new TiledMapStage(viewport, map);
        // stage.getViewport().setCamera(app.getCamera());
        Gdx.input.setInputProcessor(entitiesStage);
        addButtons();

        // TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Layer1");
        TiledMapTileLayer layer2 = (TiledMapTileLayer) map.getLayers().get("Layer2");


//        Timer.schedule(new Timer.Task() {
//            int i = 7;
//
//            @Override
//            public void run() {
//                TiledMapTileLayer.Cell cell = layer2.getCell(i, 4);
//                layer2.setCell(i, 4, null);
//                layer2.setCell(i + 1, 4, cell);
//                i++;
//
//            }
//        }, 2, 1, 20);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        app.getCamera().update();
        inputProcessor.onRender();
        renderer.setView(app.getCamera());

        renderer.render();


        if (entitiesStage.getActors().size == 0) {
            renderer.getActors().forEach((s, actors) -> actors.forEach(a ->
                    entitiesStage.addActor(a)
            ));
        }

        update(delta);
        entitiesStage.act(delta);
        entitiesStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        app.getCamera().viewportHeight = height;
        app.getCamera().viewportWidth = width;

        renderer.setView(app.getCamera());
        renderer.render();

//        entitiesStage.setViewport(new FitViewport(width, height,  camera));
        entitiesStage.getViewport().setScreenBounds(
                (int)renderer.getViewBounds().x,
                (int)renderer.getViewBounds().y,
                width,
                height
                );
        System.out.println(renderer.getViewBounds().getWidth());
        System.out.println(entitiesStage.getViewport().getScreenWidth());
        System.out.println("========");
        entitiesStage.getActors().clear();
        renderer.getActors().forEach((s, actors) -> actors.forEach(a -> entitiesStage.addActor(a)
        ));
        entitiesStage.act();
        entitiesStage.draw();

//        System.out.println("Size: " + entitiesStage.getActors().size);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        entitiesStage.act(delta);
        entitiesStage.draw();
        uiStage.act(delta);
        uiStage.draw();
    }
}
