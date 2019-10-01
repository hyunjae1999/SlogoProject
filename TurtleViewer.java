package GUI.TabbedGUI.Tabs.Windows.TurtleView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import model.ImmutableTurtle;
import model.Result;

import java.util.List;

public class TurtleViewer extends Pane {

    public static final double CENTER_POINT_MULTIPLIER = 0.5;

    private static final String BORDER_CSS = "-fx-border-color: black";
    private static String CSS_FILE = "TurtleViewer.css";
    public TurtleViewer(double width, double height) {
        setWidth(width);
        setHeight(height);
        this.getStylesheets().add(getClass().getResource(CSS_FILE).toExternalForm());
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        setStyle(BORDER_CSS);
    }

    public void addResult(Result result) {
        List<Node> children = getChildren();
        children.clear();
        for (ImmutableTurtle t : result.getTurtles().values()) {
            t = updateTurtleX(t);
            t = updateTurtleY(t);
            t.setRotate(ImmutableTurtle.DEFAULT_HEADING - t.getHeading());
            children.add(t);
            for (Line l : t.getTrail()) {
                l.setStartX(l.getStartX() + getWidth() * CENTER_POINT_MULTIPLIER);
                l.setStartY(getHeight() - (l.getStartY() + getHeight() * CENTER_POINT_MULTIPLIER));
                l.setEndX(l.getEndX() + getWidth() * CENTER_POINT_MULTIPLIER);
                l.setEndY(getHeight() - (l.getEndY() + getHeight() * CENTER_POINT_MULTIPLIER));
                children.add(l);
            }
        }
        setBackground(new Background(new BackgroundFill(result.getBackgroundColor(), new CornerRadii(0), new Insets(0))));
    }

    public ImmutableTurtle updateTurtleX(ImmutableTurtle t) {
        var newLeft = t.getX() + getWidth() * CENTER_POINT_MULTIPLIER - t.getImage().getWidth() * CENTER_POINT_MULTIPLIER;
        var maxLeft = this.getWidth() - t.getImage().getWidth();
        newLeft = getLoc(newLeft, maxLeft);
        t.setX(newLeft);
        return t;
    }

    public ImmutableTurtle updateTurtleY(ImmutableTurtle t) {
        var newTop = getHeight() - (t.getY() + getHeight() * CENTER_POINT_MULTIPLIER) - t.getImage().getHeight() * CENTER_POINT_MULTIPLIER;
        var maxTop = this.getHeight() - t.getImage().getHeight();
        newTop = getLoc(newTop, maxTop);
        t.setY(newTop);
        return t;
    }

    public double getLoc(double newLoc, double maxLoc) {
        if (newLoc <= 0) {
            newLoc = 0;
        } else if (newLoc >= maxLoc) {
            newLoc = maxLoc;
        }
        return newLoc;
    }

    public Paint getBackgroundColor() {
        return getBackground().getFills().get(getBackground().getFills().size()-1).getFill();
    }

    public int numTurtles() {
        return getChildren().size();
    }
}
