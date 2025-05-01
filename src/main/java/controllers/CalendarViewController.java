package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import services.ServiceCompetition;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Map;
import javafx.collections.ObservableList;
public class CalendarViewController {

    @FXML private BarChart<String, Number> densityChart;
    @FXML private Label suggestionLabel;

    @FXML
    public void initialize() {
        try {
            ServiceCompetition sc = new ServiceCompetition();
            Map<YearMonth, Integer> density = sc.getCompetitionDensity();
            populateChart(density);
            generateSuggestions(density);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateChart(Map<YearMonth, Integer> density) {
        densityChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Densité de compétitions");

        double average = density.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        for (Map.Entry<YearMonth, Integer> entry : density.entrySet()) {
            String month = entry.getKey().toString(); // ex: "2025-04"
            int count = entry.getValue();
            XYChart.Data<String, Number> data = new XYChart.Data<>(month, count);

            // Couleur selon densité
            if (count > average * 1.5) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) newNode.setStyle("-fx-bar-fill: #f85c5c;");
                });
            } else if (count < average * 0.5) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) newNode.setStyle("-fx-bar-fill: #2fc4dc;");
                });
            } else {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) newNode.setStyle("-fx-bar-fill: #78e76d;");
                });
            }

            series.getData().add(data);
        }

        densityChart.getData().add(series);
    }

    private void generateSuggestions(Map<YearMonth, Integer> density) {
        if (density.isEmpty()) {
            suggestionLabel.setText("Aucune donnée disponible");
            return;
        }

        double average = density.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        StringBuilder suggestions = new StringBuilder("Analyse des périodes :\n");

        density.entrySet().stream()
                .sorted(Map.Entry.<YearMonth, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String categorie;
                    if(entry.getValue() > average * 1.5) {
                        categorie = "🚨 Surcharge";
                    } else if(entry.getValue() < average * 0.5) {
                        categorie = "✅ Creux idéal";
                    } else {
                        categorie = "➖ Période normale";
                    }

                    suggestions.append(String.format("%s : %s (%d compétitions)\n",
                            entry.getKey(),
                            categorie,
                            entry.getValue()));
                });

        suggestionLabel.setText(suggestions.toString());
    }
}