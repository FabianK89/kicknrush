package de.fmk.kicknrush.views;

import de.fmk.kicknrush.helper.ResourceHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.NotificationPane;


/**
 * @author FabianK
 */
public class Notification extends NotificationPane {
	private static final String STYLE_BLACK   = "black";
	private static final String STYLE_ERROR   = "error";
	private static final String STYLE_WARNING = "warn";


	public Notification(Node content) {
		super(content);

		setShowFromTop(false);
	}


	public void showError(final String text, final AppImage image) {
		removeStyleClasses();
		getStyleClass().add(STYLE_ERROR);
		setCloseButtonVisible(true);
		setGraphic(initContent(text, image.getName()));
		show();
	}


	public void showLoading(final String text) {
		removeStyleClasses();
		getStyleClass().add(STYLE_BLACK);
		setCloseButtonVisible(false);
		setGraphic(initContent(text, AppImage.LOADING.getName()));
		show();
	}


	public void showWarning(final String text, final AppImage image) {
		removeStyleClasses();
		getStyleClass().add(STYLE_WARNING);
		setCloseButtonVisible(true);
		setGraphic(initContent(text, image.getName()));
		show();
	}


	private HBox initContent(String text, String imageName) {
		final HBox      box;
		final ImageView image;
		final Label     label;

		label = new Label(text);
		label.setWrapText(true);

		image = new ImageView(ResourceHelper.getImage(imageName));
		image.setFitHeight(18.0);
		image.setFitWidth(18.0);

		box = new HBox(5.0, image, label);
		box.setAlignment(Pos.CENTER_LEFT);

		return box;
	}


	private void removeStyleClasses() {
		getStyleClass().removeAll(STYLE_BLACK, STYLE_ERROR, STYLE_WARNING);
	}
}
