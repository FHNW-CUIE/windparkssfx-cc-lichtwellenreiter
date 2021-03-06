package cuie.lichtwellenreiter.dashboard.rotarychart;

import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;


public class BarShape extends Group {

    private Line line;
    private Arc arc;
    private Shape shape;
    private Text valueLabel;
    private Label barLabel;

    private String label;

    private final DoubleProperty value = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();

    private Color color;

    // Constants
    private final int centerX;
    private final int centerY;
    private final int radius;
    private final int strokeWidth;

    private boolean visible;
    private int level;

    public BarShape(String label, double value, double total, Color color, int centerX, int centerY, int radius, int strokeWidth, boolean visible, int level) {
        super();

        setTotal(total);
        setValue(value);
        this.label = label;
        this.color = color;
        this.visible = visible;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.level = level;

        initializeParts();
        setupValueChangeListener();
        getChildren().addAll(barLabel, shape, valueLabel);
    }

    private void initializeParts() {
        barLabel = new Label(label);
        barLabel.getStyleClass().addAll("rotarychart-bar-label", "rotarychart-bar-label-" + label);
        barLabel.setLayoutX(centerX - (centerX / 2.0) - 40);
        barLabel.setLayoutY((centerY - radius) - 8);

        line = new Line(centerX - (centerX / 2), centerY - radius, centerX, centerY - radius);

        arc = new Arc(centerX, centerY, radius, radius, calcStartAngle(), calclength());
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.BLACK);
        arc.setFill(null);

        valueLabel = new Text();
        valueLabel.getStyleClass().addAll("rotarychart-bar-text", "text-" + label);
        valueLabel.setX(centerX - (centerX / 2.0));
        valueLabel.setY((centerY - radius) + 4);
        valueLabel.setVisible(visible);

        shape = createShape();

    }

    private Shape createShape() {

        if (level == 0 || getValue() > 0) {
            Shape shape = Shape.union(line, arc);
            shape.getStyleClass().addAll("rotarychart-bar", "rotarychart-bar-" + label);
            shape.setFill(Color.TRANSPARENT);
            shape.setStroke(color);
            shape.setStrokeWidth(strokeWidth);

            if (!visible) {
                shape.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (getValue() > 0) {
                        valueLabel.setVisible(newValue);
                    }
                });
            }
            return shape;
        } else {
            return Shape.union(new Rectangle(0, 0, 0, 0), new Rectangle(0, 0, 0, 0));
        }
    }

    private void setupValueChangeListener() {
        value.addListener((observable, oldValue, newValue) -> updateShape());
        total.addListener((observable, oldValue, newValue) -> updateShape());
    }

    private void updateShape() {
        valueLabel.setText(Double.toString(getValue()));
        arc.setLength(calclength());
        arc.setStartAngle(calcStartAngle());

        shape = createShape();

        getChildren().clear();
        getChildren().addAll(barLabel, shape, valueLabel);
    }

    private double calclength() {
        double length = 270;
        if (getValue() > 0) {
            length = (((100.0 / getTotal()) * getValue()) / 100.0) * 270.0;
        }
        return length;
    }

    private double calcStartAngle() {
        return 90.0 - calclength();
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }
}
