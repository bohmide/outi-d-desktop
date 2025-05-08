package controllers;

import entities.OrganisationStats;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import services.ServiceCompetition;
import javafx.util.StringConverter; // <-- Ajoutez cette ligne
import javafx.scene.Node; // <-- Ajoutez cette ligne
import controllers.CalendarViewController;
import javafx.scene.control.Label;
public class TopOrganisationsController {
    @FXML private BarChart<String, Number> chart;
    @FXML private Label suggestionLabel;

    private ServiceCompetition serviceCompetition = new ServiceCompetition();

    @FXML
    public void initialize() {
        setupChart();
        loadData();
    }

    private void setupChart() {
        chart.setCategoryGap(20);
        chart.setLegendVisible(false);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.intValue() + " équipes";
            }

            @Override
            public Number fromString(String string) {
                return null; // Non nécessaire pour l'affichage
            }
        });    }

    private void loadData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        serviceCompetition.getTopOrganisationsWithTeamCount(5).forEach(stat -> {
            String orgName = stat.getOrganisation().getNomOrganisation();
            series.getData().add(new XYChart.Data<>(
                    orgName,
                    stat.getTeamCount()
            ));
        });

        chart.getData().add(series);

        for (int i = 0; i < chart.getData().size(); i++) {
            XYChart.Series<String, Number> s = chart.getData().get(i);
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node bar = d.getNode();
                bar.setStyle("-fx-bar-fill: #81de6f;"); // Style statique
                bar.setOnMouseEntered(e -> bar.setStyle("-fx-bar-fill: #FFC107;"));
                bar.setOnMouseExited(e -> bar.setStyle("-fx-bar-fill: #81de6f;"));
            }
        }
    }
}