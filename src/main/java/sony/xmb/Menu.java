package sony.xmb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sony.Main;
import sony.XMB;

public class Menu extends Pane {
	private class Item extends Icon {

		public void update() {
			int selectedCategory = XMB.selectedCategory;
			Config.Category category = (Config.Category) XMB.list.get(selectedCategory);
			int position = category.getSelected() + index;
			if (position >= 0 && position < ((Config.Category) XMB.list.get(selectedCategory)).getItems().size()) {
				Config.Item item = ((Config.Category) XMB.list.get(selectedCategory)).getItem(position);
				setImage(new Image(getClass().getResourceAsStream(item.getImage())));
				visibleProperty().setValue(Boolean.valueOf(true));
				((Title) titles.get(1 + index)).setText(item.getTitle());
				if (index == 0)
					if (!item.getValue().equals("")) {
						value.setVisible(true);
						value.setText(Main.getValueOf(item.getValue()));
					} else {
						value.setVisible(false);
						value.setText("");
					}
			} else {
				setImage(new Image(getClass().getResourceAsStream("/sony/img/empty.png")));
				visibleProperty().setValue(Boolean.valueOf(false));
				((Title) titles.get(1 + index)).setText("");
			}
		}

		public Item(int index) {
			super(index);
		}
	}

	private class Category extends Icon {

		public void update() {
			int selectedCategory = XMB.selectedCategory;
			int position = selectedCategory + index;
			if (position >= 0 && position < XMB.list.size()) {
				Config.Category category = (Config.Category) XMB.list.get(selectedCategory + index);
				setImage(new Image(getClass().getResourceAsStream(category.getImage())));
				visibleProperty().setValue(Boolean.valueOf(true));
				if (index == 0)
					categoryText.setText(category.getTitle());
			} else {
				setImage(new Image(getClass().getResourceAsStream("/sony/img/empty.png")));
				visibleProperty().setValue(Boolean.valueOf(false));
			}
		}

		public Category(int index) {
			super(index);
		}
	}

	private abstract class Icon extends ImageView {

		public abstract void update();

		protected int index;

		public Icon(int index) {
			super();
			this.index = index;
			update();
		}
	}

	private class Title extends Label {

		public int getIndex() {
			return index;
		}

		private int index;

		public Title(int index) {
			super();
			this.index = index;
		}
	}

	public Menu() {
		categoryBar = new HBox();
		itemBar = new VBox();
		previous = new StackPane();
		categoryText = new Label();
		titles = new ArrayList<Title>();
		value = new Label();
		Title oldTitle = new Title(-1);
		oldTitle.setOpacity(0.5D);
		titles.add(oldTitle);
		titles.add(new Title(0));
		titles.add(new Title(1));
		titles.add(new Title(2));
		Title title;
		for (Iterator<Title> i$ = titles.iterator(); i$.hasNext(); getChildren().add(title)) {
			title = i$.next();
			title.setTextFill(Color.WHITE);
			title.setAlignment(Pos.CENTER_LEFT);
		}

		value.setTextFill(Color.WHITE);
		value.setAlignment(Pos.CENTER_LEFT);
		getChildren().add(value);
		previous.getChildren().add(new Item(-1));
		previous.setAlignment(Pos.CENTER);
		previous.setOpacity(0.5D);
		getChildren().add(previous);
		Category old = new Category(-1);
		old.setOpacity(0.5D);
		categoryBar.getChildren().add(old);
		Category current = new Category(0);
		DropShadow effect = new DropShadow();
		effect.setOffsetX(0.0D);
		effect.setOffsetY(0.0D);
		effect.setSpread(0.5D);
		effect.setColor(Color.WHITE);
		current.setEffect(effect);
		categoryBar.getChildren().add(current);
		categoryBar.getChildren().add(new Category(1));
		categoryBar.getChildren().add(new Category(2));
		categoryBar.getChildren().add(new Category(3));
		categoryBar.getChildren().add(new Category(4));
		categoryBar.getChildren().add(new Category(5));
		categoryBar.setAlignment(Pos.CENTER);
		getChildren().add(categoryBar);
		categoryText.setTextFill(Color.WHITE);
		categoryText.setAlignment(Pos.CENTER);
		getChildren().add(categoryText);
		Item selected = new Item(0);
		final DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(0.0D);
		dropShadow.setOffsetY(0.0D);
		dropShadow.setColor(Color.WHITE);
		KeyFrame kf = new KeyFrame(Duration.millis(80D), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				if (direction) {
					dropShadow.setRadius(dropShadow.getRadius() - 10D);
					dropShadow.setSpread(dropShadow.getSpread() - 0.10000000000000001D);
				} else {
					dropShadow.setRadius(dropShadow.getRadius() + 10D);
					dropShadow.setSpread(dropShadow.getSpread() + 0.10000000000000001D);
				}
				if (dropShadow.getSpread() > 0.5D)
					direction = true;
				if (dropShadow.getSpread() < 0.0D)
					direction = false;
			}

			private boolean direction = true;
		});
		Timeline repeat = new Timeline( kf );
		repeat.setCycleCount(Timeline.INDEFINITE);
		repeat.play();
		selected.setEffect(dropShadow);
		value.setEffect(dropShadow);
		itemBar.getChildren().add(selected);
		itemBar.getChildren().add(new Item(1));
		itemBar.getChildren().add(new Item(2));
		itemBar.setAlignment(Pos.CENTER);
		getChildren().add(itemBar);
	}

	public void update() {
		resize(categoryBar, 1.0D);
		resize(itemBar, 0.6D);
		resize(previous, 0.6D);
		if (getScene() != null) {
			double box = getScene().getWidth() / 7D;
			categoryText.setLayoutX(box);
			categoryText.setLayoutY(1.6D * box);
		}
		resize(categoryText, 1.0D);
		if (getScene() != null) {
			double box = getScene().getWidth() / 7D;
			for (Title title : titles) {
				title.setLayoutX(2D * box);
				switch (title.getIndex()) {
				case -1:
					title.setLayoutY(0.3D * box);
					break;

				default:
					title.setLayoutY(2D * box + (double) title.getIndex() * 0.6D * box);
					break;
				}
				title.setPrefWidth(5D * box);
				title.setPrefHeight(box / 5D);
				title.setFont(new Font(box / 5D));
			}

			value.setLayoutX(6D * box);
			value.setLayoutY(2D * box);
			value.setPrefWidth(box);
			value.setPrefHeight(box / 5D);
			value.setFont(new Font(box / 5D));
		}
	}

	private void resize(Label label, double scale) {
		if (getScene() != null) {
			double box = getScene().getWidth() / 7D;
			label.setPrefWidth(scale * box);
			label.setPrefHeight((scale * box) / 5D);
			label.setFont(new Font((scale * box) / 5D));
		}
	}

	private void resize(Pane pane, double scale) {
		if (getScene() != null) {
			double fit = (scale * getScene().getWidth()) / 7D;
			int size = pane.getChildren().size();
			double box = getScene().getWidth() / 7D;
			if (pane instanceof StackPane) {
				pane.setLayoutX(box);
				pane.setLayoutY(0.0D);
				pane.setPrefWidth(box);
				pane.setPrefHeight(box);
			}
			if (pane instanceof HBox) {
				pane.setLayoutX(0.0D);
				pane.setLayoutY(0.75D * box);
				pane.setPrefWidth((double) size * box);
				pane.setPrefHeight(box);
			}
			if (pane instanceof VBox) {
				pane.setLayoutX(box);
				pane.setLayoutY(1.25D * box);
				pane.setPrefWidth(box);
				pane.setPrefHeight((double) size * box);
			}
			Icon item;
			for (Node node : pane.getChildren()) {
				item = (Icon) node;
				item.update();
				item.setFitHeight(fit);
				item.setFitWidth(fit);
			}

		}
	}

	private HBox categoryBar;
	private VBox itemBar;
	private StackPane previous;
	private Label categoryText;
	private List<Title> titles;
	private Label value;

}
