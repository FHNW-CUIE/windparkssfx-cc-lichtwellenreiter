package cuie.lichtwellenreiter.dashboard.demo;

import javafx.beans.property.*;

public class PresentationModel {


    private final StringProperty station = new SimpleStringProperty("Haldenstein");
    private final StringProperty chartLabel = new SimpleStringProperty("Produktion (MWh):");

    private final StringProperty title = new SimpleStringProperty("Haldenstein");
    private final StringProperty canton = new SimpleStringProperty("GR");

    private final StringProperty installedPowerLabel = new SimpleStringProperty("Leistung (kW):");

    private final DoubleProperty installedPower = new SimpleDoubleProperty(3000.00);
    private final DoubleProperty production2015 = new SimpleDoubleProperty(4278.00);
    private final DoubleProperty production2016 = new SimpleDoubleProperty(4372.00);
    private final DoubleProperty production2017 = new SimpleDoubleProperty(4137.00);
    private final DoubleProperty production2018 = new SimpleDoubleProperty(4920.00);

    private final DoubleProperty totalProduction = new SimpleDoubleProperty();


    public double getTotalProduction() {
        setTotalProduction(total());
        return totalProduction.get();
    }

    public DoubleProperty totalProductionProperty() {
        setTotalProduction(total());
        return totalProduction;
    }

    public void setTotalProduction(double totalProduction) {
        this.totalProduction.set(totalProduction);
    }

    public double total() {
        return getProduction2015() + getProduction2016() + getProduction2017() + getProduction2018();
    }


    public String getChartLabel() {
        return chartLabel.get();
    }

    public StringProperty chartLabelProperty() {
        return chartLabel;
    }

    public void setChartLabel(String chartLabel) {
        this.chartLabel.set(chartLabel);
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

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
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

    public String getInstalledPowerLabel() {
        return installedPowerLabel.get();
    }

    public StringProperty installedPowerLabelProperty() {
        return installedPowerLabel;
    }

    public void setInstalledPowerLabel(String installedPowerLabel) {
        this.installedPowerLabel.set(installedPowerLabel);
    }

    public double getInstalledPower() {
        return installedPower.get();
    }

    public DoubleProperty installedPowerProperty() {
        return installedPower;
    }

    public void setInstalledPower(double installedPower) {
        this.installedPower.set(installedPower);
    }

    public double getProduction2015() {
        return production2015.get();
    }

    public DoubleProperty production2015Property() {
        return production2015;
    }

    public void setProduction2015(double production2015) {
        this.production2015.set(production2015);
    }

    public double getProduction2016() {
        return production2016.get();
    }

    public DoubleProperty production2016Property() {
        return production2016;
    }

    public void setProduction2016(double production2016) {
        this.production2016.set(production2016);
    }

    public double getProduction2017() {
        return production2017.get();
    }

    public DoubleProperty production2017Property() {
        return production2017;
    }

    public void setProduction2017(double production2017) {
        this.production2017.set(production2017);
    }

    public double getProduction2018() {
        return production2018.get();
    }

    public DoubleProperty production2018Property() {
        return production2018;
    }

    public void setProduction2018(double production2018) {
        this.production2018.set(production2018);
    }
}
