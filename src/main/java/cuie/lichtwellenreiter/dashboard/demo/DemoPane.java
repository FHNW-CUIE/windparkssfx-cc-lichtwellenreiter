package cuie.lichtwellenreiter.dashboard.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.lichtwellenreiter.dashboard.rotarychart.RotaryChart;
import javafx.util.converter.NumberStringConverter;

public class DemoPane extends BorderPane {

    private final PresentationModel pm;

    // declare the custom control
    private RotaryChart cc;

    // all controls

    private ComboBox station;
    private TextField chart;


    private TextField title;
    private ComboBox canton;
    private TextField performance;
    private TextField production2015;
    private TextField production2016;
    private TextField production2017;
    private TextField production2018;

    private Label totalProduction;

    public DemoPane(PresentationModel pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
        setupEventHandlers();
    }

    private void initializeControls() {
        setPadding(new Insets(10));
        cc = new RotaryChart();

        chart = new TextField();

        title = new TextField();

        ObservableList<String> stationOptions = FXCollections.observableArrayList("Haldenstein", "Peuchapatte");
        station = new ComboBox(stationOptions);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "AG", "AI", "AR", "BE",
                        "BL", "BS", "FR", "GE",
                        "GL", "GR", "JU", "LU",
                        "NE", "NW", "OW", "SG",
                        "SH", "SO", "SZ", "TG",
                        "TI", "UR", "VD", "VS",
                        "ZG", "ZH"
                );
        canton = new ComboBox(options);

        performance = new TextField();
        production2015 = new TextField();
        production2016 = new TextField();
        production2017 = new TextField();
        production2018 = new TextField();

        totalProduction = new Label(String.valueOf(pm.total()));
    }

    private void layoutControls() {
        VBox controlPane = new VBox(new Label("Dashboard Properties"),
                new Label("Station"), station,
                new Label("Chart Title"), chart,
                new Label("Title"), title,
                new Label("Canton"), canton,
                new Label("Performance"), performance,
                new Label("Production 2015 (MWh)"), production2015,
                new Label("Production 2016 (MWh)"), production2016,
                new Label("Production 2017 (MWh)"), production2017,
                new Label("Production 2018 (MWh)"), production2018);

        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        station.valueProperty().bindBidirectional(pm.stationProperty());
        chart.textProperty().bindBidirectional(pm.chartLabelProperty());
        title.textProperty().bindBidirectional(pm.titleProperty());
        canton.valueProperty().bindBidirectional(pm.cantonProperty());
        performance.textProperty().bindBidirectional(pm.installedPowerProperty(), new NumberStringConverter());
        production2015.textProperty().bindBidirectional(pm.production2015Property(), new NumberStringConverter());
        production2016.textProperty().bindBidirectional(pm.production2016Property(), new NumberStringConverter());
        production2017.textProperty().bindBidirectional(pm.production2017Property(), new NumberStringConverter());
        production2018.textProperty().bindBidirectional(pm.production2018Property(), new NumberStringConverter());

        totalProduction.textProperty().bindBidirectional(pm.totalProductionProperty(), new NumberStringConverter());


        // Bind all properties from RotaryDash to the PM
        cc.stationProperty().bind(pm.titleProperty());
        cc.cantonProperty().bind(pm.cantonProperty());
        cc.productionLabelProperty().bind(pm.installedPowerLabelProperty());
        cc.productionProperty().bind(pm.installedPowerProperty());
        cc.chartTitleProperty().bind(pm.chartLabelProperty());

        cc.value2015Property().bind(pm.production2015Property());
        cc.value2016Property().bind(pm.production2016Property());
        cc.value2017Property().bind(pm.production2017Property());
        cc.value2018Property().bind(pm.production2018Property());
    }

    private void setupEventHandlers() {

        station.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch ((String) newValue) {

                case "Haldenstein":
                    pm.setTitle("Haldenstein");
                    pm.setCanton("GR");
                    pm.setInstalledPower(3000);
                    pm.setProduction2015(4278);
                    pm.setProduction2016(4372);
                    pm.setProduction2017(4137);
                    pm.setProduction2018(4920);
                    break;
                case "Peuchapatte":
                    pm.setTitle("Peuchapatte");
                    pm.setCanton("JU");
                    pm.setInstalledPower(6000);
                    pm.setProduction2015(14436);
                    pm.setProduction2016(13366);
                    pm.setProduction2017(13186);
                    pm.setProduction2018(12480);
                    break;
            }
        });
    }
}
