package com.mygdx.claninvasion.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.claninvasion.ClanInvasion;
import com.mygdx.claninvasion.model.Globals;
import com.badlogic.gdx.audio.Sound;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * One of the screens which implement GamePage interface, shows loading animation icon
 * @author andreicristea
 * @author omarashour
 * @version 0.1
 * @see GamePage, UiUpdatable
 */
public class LoadingScreen implements GamePage {
    private Image animated;
    private final Stage stage;
    private final ClanInvasion app;
    private Sound Sound;
    private float elapsedTime = 0f;
    private final RepeatAction loadingAction = forever(sequence(alpha(0f), fadeIn(0.6f)));

    /**
     * @param app - app instance
     */
    public LoadingScreen(final ClanInvasion app) {
        this.app = app;
        stage = new Stage(new FillViewport(Globals.V_WIDTH, Globals.V_HEIGHT, app.getCamera()));
        Gdx.input.setInputProcessor(stage);
    }


    private void initAnimation() {
        Texture texture = new Texture(Gdx.files.internal("LoadingscreenAnimation/Loading-Screen-icon0.png"));
        animated = new Image(texture);
        animated.setSize(200, 200);
        animated.setPosition((stage.getWidth() / 2) - 100, stage.getHeight() / 4);

        stage.addActor(animated);
        Sound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/LoadingScreen.ogg"));
        long id = Sound.play(1.0f);
        Sound.setPitch(id, 2);
        Sound.setLooping(id, false);
    }

    /**
     * Is fired once the page becomes active in application
     * See GamePage interface
     */
    @Override
    public void show() {
        this.initAnimation();

        animated.addAction(loadingAction);
    }

    /**
     * Fired on every frame update
     * See GamePage interface
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        elapsedTime += delta;
        stage.draw();

        if (elapsedTime > 1.5f) {
            animated.removeAction(loadingAction);
            animated.addAction(fadeOut(0.5f, Interpolation.bounceIn));
        }

        if (elapsedTime > 2.2f) {
            app.changeScreen();
        }
    }

    /**
     * Used inside render method
     * See GamePage interface
     * @param delta - difference between two render calls
     */
    public void update(float delta) {
        stage.act(delta);
    }

    /**
     * Fired on every resize event by libgdx
     * See GamePage interface
     * @param width - resized width value
     * @param height - resized height value
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * See GamePage interface
     */
    @Override
    public void pause() {

    }

    /**
     * See GamePage interface
     */
    @Override
    public void resume() {

    }

    /**
     * See GamePage interface
     */
    @Override
    public void hide() {

    }

    /**
     * See GamePage interface
     * Flushes state and fires cleanup
     */
    @Override
    public void dispose() {
     Sound.dispose();
    }
}
