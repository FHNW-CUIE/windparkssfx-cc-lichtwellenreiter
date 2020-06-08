package cuie.lichtwellenreiter.dashboard.rotarychart;

import java.util.List;
import java.util.Locale;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.util.Duration;

/**
 * CustomControl welches eine RotaryChart zur Verfügung stellt für diverse Werte
 *
 * @author Florian Thiévent (lichtwellenreiter)
 */
//ToDo: Umbenennen.
public class RotaryChart extends Region {
    // wird gebraucht fuer StyleableProperties
    private static final StyleablePropertyFactory<RotaryChart> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    // Locale
    private static final Locale CH = new Locale("de", "CH");

    // Sizes
    private static final double ARTBOARD_WIDTH = 250;
    private static final double ARTBOARD_HEIGHT = 250;
    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;
    private static final double ARTBOARD_CENTER = ARTBOARD_WIDTH * 0.5;
    private static final double MINIMUM_WIDTH = 25;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;
    private static final double MAXIMUM_WIDTH = 800;

    // Parts
    private Pane parentPane;
    private Label stationLabel;
    private ImageView cantonImage;
    private Label powerLabel;
    private Label powerValueLabel;

    private Pane chartPane;
    private Label chartLabel;
    private BarShape outerLine;
    private BarShape bar2015;
    private BarShape bar2016;
    private BarShape bar2017;
    private BarShape bar2018;

    // Properties
    private final StringProperty station = new SimpleStringProperty();
    private final StringProperty canton = new SimpleStringProperty();
    private final ObjectProperty cImage = new SimpleObjectProperty();
    private final StringProperty productionLabel = new SimpleStringProperty("Performance (kW)");
    private final DoubleProperty production = new SimpleDoubleProperty();

    private final StringProperty chartTitle = new SimpleStringProperty();
    private final DoubleProperty value2015 = new SimpleDoubleProperty(1);
    private final DoubleProperty value2016 = new SimpleDoubleProperty(1);
    private final DoubleProperty value2017 = new SimpleDoubleProperty(1);
    private final DoubleProperty value2018 = new SimpleDoubleProperty(1);

    private final DoubleProperty totalProduction = new SimpleDoubleProperty();


    // Colors
    private Color RC_BG = Color.web("#CECECE");
    private Color RC_BLACK = Color.web("#1E1E1E");
    private Color RC_DARK_GREY = Color.web("#A5A5A5");
    private Color RC_BLUE = Color.web("#29A3CD");
    private Color RC_RED = Color.web("#B71540");
    private Color RC_GREEN = Color.web("#38ADA9");
    private Color RC_ORANGE = Color.web("#E55039");
    private Color RC_YELLOW = Color.web("#F6B93B");

    private static final CssMetaData<RotaryChart, Color> BASE_COLOR_META_DATA = FACTORY.createColorCssMetaData("-base-color", s -> s.baseColor);

    private final StyleableObjectProperty<Color> baseColor = new SimpleStyleableObjectProperty<Color>(BASE_COLOR_META_DATA) {
        @Override
        protected void invalidated() {
            setStyle(String.format("%s: %s;", getCssMetaData().getProperty(), colorToCss(get())));
            applyCss();
        }
    };

    // ToDo: Loeschen falls keine getaktete Animation benoetigt wird
    private final BooleanProperty blinking = new SimpleBooleanProperty(false);
    private final ObjectProperty<Duration> pulse = new SimpleObjectProperty<>(Duration.seconds(1.0));

    private final AnimationTimer timer = new AnimationTimer() {
        private long lastTimerCall;

        @Override
        public void handle(long now) {
            if (now > lastTimerCall + (getPulse().toMillis() * 1_000_000L)) {
                performPeriodicTask();
                lastTimerCall = now;
            }
        }
    };

    // ToDo: alle Animationen und Timelines deklarieren
    private final Timeline timeline = new Timeline();

    public RotaryChart() {
        initializeSelf();
        initializeParts();
        initializeDrawingPane();
        initializeAnimations();

        setupEventHandlers();
        setupValueChangeListeners();
        setupBindings();

        setupBindings();
        initializeBars();
        layoutParts();
    }

    private void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/Lato/Lato-Reg.ttf");
        addStylesheetFiles("style.css");
        getStyleClass().add("rotary-chart");
    }

    private void initializeParts() {

        parentPane = new Pane();
        parentPane.getStyleClass().add("rotarychart-pane");
        parentPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        parentPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        parentPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        parentPane.getStyleClass().add("rotarychart");
        parentPane.setPadding(new Insets(0, 10, 0, 10));

        stationLabel = new Label("Title");
        stationLabel.getStyleClass().add("rotarychart-station-label");
        stationLabel.setLayoutX(10);
        stationLabel.setLayoutY(10);

        cantonImage = new ImageView();
        cantonImage.getStyleClass().add("rotarychart-canton-image");
        cantonImage.setImage(getCantonImage("GR"));
        cantonImage.setFitHeight(30.35);
        cantonImage.setFitWidth(25);
        cantonImage.setLayoutX(ARTBOARD_WIDTH - 40);
        cantonImage.setLayoutY(15);
        cantonImage.setPreserveRatio(true);

        powerLabel = new Label();
        powerLabel.getStyleClass().add("rotarychart-production-label");
        powerLabel.setLayoutX(11);
        powerLabel.setLayoutY(35);

        powerValueLabel = new Label();
        powerValueLabel.getStyleClass().add("rotarychart-production-value-label");
        powerValueLabel.setLayoutX(100);
        powerValueLabel.setLayoutY(35);

        chartLabel = new Label();
        chartLabel.getStyleClass().add("rotarychart-chart-label");
        chartLabel.setLayoutX(10);
        chartLabel.setLayoutY(100);

        outerLine = new BarShape(null, 0, RC_BLACK, 150, 100, 75, 1, -1);
        bar2015 = new BarShape("2015", 0, RC_BLUE, 150, 100, 65, 10, 0);
        bar2016 = new BarShape("2016", 0, RC_GREEN, 150, 100, 50, 10, 1);
        bar2017 = new BarShape("2017", 0, RC_ORANGE, 150, 100, 35, 10, 2);
        bar2018 = new BarShape("2018", 0, RC_YELLOW, 150, 100, 20, 10, 3);


    }

    private void initializeBars() {

        bar2015.setValue(getValue2015());
        bar2016.setValue(getValue2016());
        bar2017.setValue(getValue2017());
        bar2018.setValue(getValue2018());


    }

    private void initializeDrawingPane() {
        chartPane = new Pane();
        chartPane.getStyleClass().add("parent-pane");
        chartPane.setLayoutX(0);
        chartPane.setLayoutY(ARTBOARD_HEIGHT - 200);
        chartPane.setMaxSize(ARTBOARD_WIDTH, 200);
        chartPane.setMinSize(ARTBOARD_WIDTH, 200);
        chartPane.setPrefSize(ARTBOARD_WIDTH, 200);
    }

    private Image getCantonImage(String canton) {

        switch (canton.toUpperCase()) {
            case "AG":
            case "AI":
            case "AR":
            case "BE":
            case "BL":
            case "BS":
            case "FR":
            case "GE":
            case "GL":
            case "GR":
            case "JU":
            case "LU":
            case "NE":
            case "NW":
            case "OW":
            case "SG":
            case "SH":
            case "SO":
            case "SZ":
            case "TG":
            case "TI":
            case "UR":
            case "VD":
            case "VS":
            case "ZG":
            case "ZH":
                return new Image(String.valueOf(getClass().getResource("coats_of_switzerland/" + canton.toUpperCase() + ".png")));
            default:
                return new Image(String.valueOf(getClass().getResource("coats_of_switzerland/UNKNOWN.png")));

        }
    }

    private void initializeAnimations() {
        //ToDo: alle deklarierten Animationen initialisieren
    }

    private void layoutParts() {
        chartPane.getChildren().addAll(chartLabel, outerLine);

        // Todo Add all LineArcs, if not null
        if (getValue2015() > 0) chartPane.getChildren().add(bar2015);
        if (getValue2016() > 0) chartPane.getChildren().add(bar2016);
        if (getValue2017() > 0) chartPane.getChildren().add(bar2017);
        if (getValue2018() > 0) chartPane.getChildren().add(bar2018);

        parentPane.getChildren().addAll(stationLabel, cantonImage, powerLabel, powerValueLabel, chartPane);
        getChildren().add(parentPane);
    }

    private void setupEventHandlers() {
        //ToDo: bei Bedarf ergänzen
    }

    private void setupValueChangeListeners() {
        canton.addListener((observable, oldValue, newValue) -> cantonImage.setImage(getCantonImage(newValue)));

        value2015.addListener((observable, oldValue, newValue) -> {
            System.out.println("2015 changed to " + newValue);
            bar2015.setValue(newValue.intValue());
        });

    }

    private void setupBindings() {
        stationLabel.textProperty().bind(stationProperty());
        chartLabel.textProperty().bindBidirectional(chartTitleProperty());
        powerLabel.textProperty().bind(productionLabelProperty());
        powerValueLabel.textProperty().bind(productionProperty().asString());

        //bar2015.valueProperty().bindBidirectional(value2015Property());

        value2015.bindBidirectional(bar2015.valueProperty());
    }

    private void updateUI() {
        //ToDo : ergaenzen mit dem was bei einer Wertaenderung einer Status-Property im UI upgedated werden muss
    }

    private void performPeriodicTask() {
        //ToDo: ergaenzen mit dem was bei der getakteten Animation gemacht werden muss
        //normalerweise: den Wert einer der Status-Properties aendern
    }

    private void startClockedAnimation(boolean start) {
        if (start) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    private void resize() {
        Insets padding = getPadding();
        double availableWidth = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width = Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            relocateDrawingPaneCentered();
            parentPane.setScaleX(scalingFactor);
            parentPane.setScaleY(scalingFactor);
        }
    }

    private void relocateDrawingPaneCentered() {
        parentPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, (getHeight() - ARTBOARD_HEIGHT) * 0.5);
    }

    // Sammlung nuetzlicher Funktionen
    private void loadFonts(String... font) {
        for (String f : font) {
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    private void addStylesheetFiles(String... stylesheetFile) {
        for (String file : stylesheetFile) {
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

    /**
     * Umrechnen einer Prozentangabe, zwischen 0 und 100, in den tatsaechlichen Wert innerhalb des angegebenen Wertebereichs.
     *
     * @param percentage Wert in Prozent
     * @param minValue   untere Grenze des Wertebereichs
     * @param maxValue   obere Grenze des Wertebereichs
     * @return value der akuelle Wert
     */
    private double percentageToValue(double percentage, double minValue, double maxValue) {
        return ((maxValue - minValue) * percentage) + minValue;
    }

    /**
     * Umrechnen des angegebenen Werts in eine Prozentangabe zwischen 0 und 100.
     *
     * @param value    der aktuelle Wert
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return Prozentangabe des aktuellen Werts
     */
    private double valueToPercentage(double value, double minValue, double maxValue) {
        return (value - minValue) / (maxValue - minValue);
    }

    /**
     * Berechnet den Winkel zwischen 0 und 360 Grad, 0 Grad entspricht "Nord", der dem value
     * innerhalb des Wertebereichs zwischen minValue und maxValue entspricht.
     *
     * @param value    der aktuelle Wert
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return angle Winkel zwischen 0 und 360 Grad
     */
    private double valueToAngle(double value, double minValue, double maxValue) {
        return percentageToAngle(valueToPercentage(value, minValue, maxValue));
    }

    /**
     * Umrechnung der Maus-Position auf den aktuellen Wert.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     * <p>
     * Lineare Controls wie Slider müssen auf andere Art die Mausposition auf den value umrechnen.
     *
     * @param mouseX   x-Position der Maus
     * @param mouseY   y-Position der Maus
     * @param cx       x-Position des Zentrums des radialen Controls
     * @param cy       y-Position des Zentrums des radialen Controls
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return value der dem Winkel entspricht, in dem die Maus zum Mittelpunkt des radialen Controls steht
     */
    private double radialMousePositionToValue(double mouseX, double mouseY, double cx, double cy, double minValue, double maxValue) {
        double percentage = angleToPercentage(angle(cx, cy, mouseX, mouseY));

        return percentageToValue(percentage, minValue, maxValue);
    }

    /**
     * Umrechnung eines Winkels, zwischen 0 und 360 Grad, in eine Prozentangabe.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param angle der Winkel
     * @return die entsprechende Prozentangabe
     */
    private double angleToPercentage(double angle) {
        return angle / 360.0;
    }

    /**
     * Umrechnung einer Prozentangabe, zwischen 0 und 100, in den entsprechenden Winkel.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param percentage die Prozentangabe
     * @return der entsprechende Winkel
     */
    private double percentageToAngle(double percentage) {
        return 360.0 * percentage;
    }

    /**
     * Berechnet den Winkel zwischen einem Zentrums-Punkt und einem Referenz-Punkt.
     *
     * @param cx x-Position des Zentrums
     * @param cy y-Position des Zentrums
     * @param x  x-Position des Referenzpunkts
     * @param y  y-Position des Referenzpunkts
     * @return winkel zwischen 0 und 360 Grad
     */
    private double angle(double cx, double cy, double x, double y) {
        double deltaX = x - cx;
        double deltaY = y - cy;
        double radius = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx = deltaX / radius;
        double ny = deltaY / radius;
        double theta = Math.toRadians(90) + Math.atan2(ny, nx);

        return Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
    }

    /**
     * Berechnet den Punkt auf einem Kreis mit gegebenen Radius im angegebenen Winkel
     *
     * @param cX     x-Position des Zentrums
     * @param cY     y-Position des Zentrums
     * @param radius Kreisradius
     * @param angle  Winkel zwischen 0 und 360 Grad
     * @return Punkt auf dem Kreis
     */
    private Point2D pointOnCircle(double cX, double cY, double radius, double angle) {
        return new Point2D(cX - (radius * Math.sin(Math.toRadians(angle - 180))),
                cY + (radius * Math.cos(Math.toRadians(angle - 180))));
    }

    /**
     * Erzeugt eine Text-Instanz in der Mitte des CustomControls.
     * Der Text bleibt zentriert auch wenn der angezeigte Text sich aendert.
     *
     * @param styleClass mit dieser StyleClass kann der erzeugte Text via css gestyled werden
     * @return Text
     */
    private Text createCenteredText(String styleClass) {
        return createCenteredText(ARTBOARD_WIDTH * 0.5, ARTBOARD_HEIGHT * 0.5, styleClass);
    }

    /**
     * Erzeugt eine Text-Instanz mit dem angegebenen Zentrum.
     * Der Text bleibt zentriert auch wenn der angezeigte Text sich aendert.
     *
     * @param cx         x-Position des Zentrumspunkt des Textes
     * @param cy         y-Position des Zentrumspunkt des Textes
     * @param styleClass mit dieser StyleClass kann der erzeugte Text via css gestyled werden
     * @return Text
     */
    private Text createCenteredText(double cx, double cy, String styleClass) {
        Text text = new Text();
        text.getStyleClass().add(styleClass);
        text.setTextOrigin(VPos.CENTER);
        text.setTextAlignment(TextAlignment.CENTER);
        double width = cx > ARTBOARD_WIDTH * 0.5 ? ((ARTBOARD_WIDTH - cx) * 2.0) : cx * 2.0;
        text.setWrappingWidth(width);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setY(cy);
        text.setX(cx - (width / 2.0));

        return text;
    }

    /**
     * Erzeugt eine Group von Lines, die zum Beispiel fuer Skalen oder Zifferblaetter verwendet werden koennen.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param cx            x-Position des Zentrumspunkts
     * @param cy            y-Position des Zentrumspunkts
     * @param radius        radius auf dem die Anfangspunkte der Ticks liegen
     * @param numberOfTicks gewuenschte Anzahl von Ticks
     * @param startingAngle Wickel in dem der erste Tick liegt, zwischen 0 und 360 Grad
     * @param overallAngle  gewuenschter Winkel zwischen den erzeugten Ticks, zwischen 0 und 360 Grad
     * @param tickLength    Laenge eines Ticks
     * @param styleClass    Name der StyleClass mit der ein einzelner Tick via css gestyled werden kann
     * @return Group mit allen Ticks
     */
    private Group createTicks(double cx, double cy, double radius, int numberOfTicks, double startingAngle, double overallAngle, double tickLength, String styleClass) {
        Group group = new Group();

        double degreesBetweenTicks = overallAngle == 360 ?
                overallAngle / numberOfTicks :
                overallAngle / (numberOfTicks - 1);
        double innerRadius = radius - tickLength;

        for (int i = 0; i < numberOfTicks; i++) {
            double angle = startingAngle + i * degreesBetweenTicks;

            Point2D startPoint = pointOnCircle(cx, cy, radius, angle);
            Point2D endPoint = pointOnCircle(cx, cy, innerRadius, angle);

            Line tick = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
            tick.getStyleClass().add(styleClass);
            group.getChildren().add(tick);
        }

        return group;
    }

    private String colorToCss(final Color color) {
        return color.toString().replace("0x", "#");
    }


    // compute sizes

    @Override
    protected double computeMinWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return MINIMUM_WIDTH + horizontalPadding;
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return MINIMUM_HEIGHT + verticalPadding;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return ARTBOARD_WIDTH + horizontalPadding;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return ARTBOARD_HEIGHT + verticalPadding;
    }

    // alle getter und setter  (generiert via "Code -> Generate... -> Getter and Setter)
    public Color getBaseColor() {
        return baseColor.get();
    }

    public StyleableObjectProperty<Color> baseColorProperty() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor.set(baseColor);
    }

    public boolean isBlinking() {
        return blinking.get();
    }

    public BooleanProperty blinkingProperty() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking.set(blinking);
    }

    public Duration getPulse() {
        return pulse.get();
    }

    public ObjectProperty<Duration> pulseProperty() {
        return pulse;
    }

    public void setPulse(Duration pulse) {
        this.pulse.set(pulse);
    }

    public String getStation() {
        return station.get();
    }

    public StringProperty stationProperty() {
        return station;
    }

    public void setStation(String station) {
        this.station.set(station);
    }

    public double getProduction() {
        return production.get();
    }

    public DoubleProperty productionProperty() {
        return production;
    }

    public void setProduction(double production) {
        this.production.set(production);
    }

    public String getProductionLabel() {
        return productionLabel.get();
    }

    public StringProperty productionLabelProperty() {
        return productionLabel;
    }

    public void setProductionLabel(String productionLabel) {
        this.productionLabel.set(productionLabel);
    }

    public String getCanton() {
        return canton.get();
    }

    public StringProperty cantonProperty() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton.set(canton);
    }

    public double getValue2015() {
        return value2015.get();
    }

    public DoubleProperty value2015Property() {
        return value2015;
    }

    public void setValue2015(double value2015) {
        this.value2015.set(value2015);
    }

    public double getValue2016() {
        return value2016.get();
    }

    public DoubleProperty value2016Property() {
        return value2016;
    }

    public void setValue2016(double value2016) {
        this.value2016.set(value2016);
    }

    public double getValue2017() {
        return value2017.get();
    }

    public DoubleProperty value2017Property() {
        return value2017;
    }

    public void setValue2017(double value2017) {
        this.value2017.set(value2017);
    }

    public double getValue2018() {
        return value2018.get();
    }

    public DoubleProperty value2018Property() {
        return value2018;
    }

    public void setValue2018(double value2018) {
        this.value2018.set(value2018);
    }

    public String getChartTitle() {
        return chartTitle.get();
    }

    public StringProperty chartTitleProperty() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle.set(chartTitle);
    }
}
