package com.esprit.utils;

import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import net.synedra.validatorfx.Validator;

import java.util.function.Supplier;

/**
 * One place for field validation messages. The message is shown directly under
 * the field using the field's position at that moment, and it is hidden when
 * the value becomes valid, the field loses focus, Escape is pressed, the field
 * leaves the scene, or a few seconds pass.
 */
public final class ValidationTooltips {
    private static final double GAP = 6;
    private static final Duration VISIBLE_FOR = Duration.seconds(5);

    private ValidationTooltips() {
    }

    /** Validates with a ValidatorFX {@link Validator} while the user types. */
    public static void bind(final TextField field, final Validator validator) {
        bind(field, () -> validator.containsErrors() ? validator.createStringBinding().getValue() : null);
    }

    /** {@code error} returns the message to show, or null when the value is valid. */
    public static void bind(final TextField field, final Supplier<String> error) {
        final Tooltip tip = new Tooltip();
        tip.getStyleClass().add("validation-tooltip");
        tip.setAutoHide(true);
        tip.setConsumeAutoHidingEvents(false);
        final PauseTransition timeout = new PauseTransition(VISIBLE_FOR);
        timeout.setOnFinished(event -> tip.hide());

        field.textProperty().addListener((observable, oldValue, newValue) -> {
            final String message = error.get();
            if (message == null || message.isBlank()) {
                timeout.stop();
                tip.hide();
                return;
            }
            tip.setText(message);
            showBelow(field, tip);
            timeout.playFromStart();
        });
        field.focusedProperty().addListener((observable, wasFocused, focused) -> {
            if (!focused) {
                timeout.stop();
                tip.hide();
            }
        });
        field.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {
                timeout.stop();
                tip.hide();
            }
        });
        field.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                timeout.stop();
                tip.hide();
            }
        });
    }

    private static void showBelow(final TextField field, final Tooltip tip) {
        if (field.getScene() == null || field.getScene().getWindow() == null) {
            return;
        }
        final Bounds bounds = field.localToScreen(field.getBoundsInLocal());
        if (bounds != null) {
            tip.show(field.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY() + GAP);
        }
    }
}
