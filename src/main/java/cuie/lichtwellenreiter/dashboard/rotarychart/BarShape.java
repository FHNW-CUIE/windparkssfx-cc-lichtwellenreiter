package cuie.lichtwellenreiter.dashboard.rotarychart;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;


public class BarShape extends Group {

    private Line line;
    private Arc arc;
    private Shape shape;
    private Text valueLabel;
    private Label barLabel;

    private String label;

    private final DoubleProperty value = new SimpleDoubleProperty();

    private Color color;

    // Constants
    private final int BAR_START_X = 0;
    private final int BAR_Y = 15;
    private final int BAR_END_X = 50;
    private final int SPACING = 15;

    private int centerX = 105;
    private int centerY = 105;
    private int radius = 90;
    private int strokeWidth = 10;
    private int position;

    public BarShape(){

    }

    public BarShape(String label, double value, Color color, int centerX, int centerY, int radius, int strokeWidth, int position) {
        super();

        setValue(value);
        this.label = label;
        this.color = color;

        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.position = position;

        initializeParts();
        setupBindings();
        setupEventHandler();
        setupValueChangeListener();
        getChildren().addAll(barLabel, shape, valueLabel);
    }

    private void initializeParts() {
        barLabel = new Label(label);
        barLabel.getStyleClass().addAll("bar-label", "label-" + label);
        barLabel.setLayoutX(centerX-(centerX/2.0)-40);
        barLabel.setLayoutY((centerY-radius)-8);


        line = new Line(centerX-(centerX/2), centerY-radius, centerX, centerY-radius);

        arc = new Arc(centerX, centerY, radius, radius, calcStartAngle(), calclength());
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.BLACK);
        arc.setFill(null);

        shape = Shape.union(line, arc);
        shape.getStyleClass().addAll("bar", "bar-" + label);
        shape.setFill(Color.TRANSPARENT);
        shape.setStroke(color);
        shape.setStrokeWidth(strokeWidth);

        valueLabel = new Text(String.valueOf(getValue()));

        valueLabel.getStyleClass().addAll("bar-text", "text-" + label);
        valueLabel.setX(centerX-(centerX/2.0));
        valueLabel.setY((centerY-radius) + 5);
        valueLabel.setVisible(false);
    }


    private void setupBindings(){
       //Todo
    }

    private void setupEventHandler() {
        shape.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (getValue() > 0) {
                valueLabel.setVisible(newValue);
            }
        });

    }

    private void setupValueChangeListener(){

        valueProperty().addListener((observable, oldValue, newValue) -> {

            System.out.println("New Value " + newValue);

            arc.setLength(calclength());
            arc.setStartAngle(calcStartAngle());



            System.out.println("length " + arc.getLength());
            System.out.println("startangle " + arc.getStartAngle());
        });
    }

    private double calcYPosition() {
        return BAR_Y + (SPACING * position);
    }

    private double calclength() {
        double length = 270;
        if (getValue() > 0) {
            length = (((100.0 / 17707.0) * getValue()) / 100.0) * 270.0;
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
}
