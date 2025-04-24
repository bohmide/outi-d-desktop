package controllers.tests;

import entities.Sponsors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import services.SponsorsService;

public class ListSponsorController {

    @FXML private TableView<Sponsors> sponsorTable;
    @FXML private TableColumn<Sponsors, Integer> idCol;
    @FXML private TableColumn<Sponsors, String> nameCol;
    @FXML private TableColumn<Sponsors, String> descCol;
    @FXML private TableColumn<Sponsors, String> imageCol;
    @FXML private TableColumn<Sponsors, java.time.LocalDate> dateCol;
    @FXML private TableColumn<Sponsors, Void> actionCol;

    private final SponsorsService service = new SponsorsService();
    private ObservableList<Sponsors> sponsorList;

    @FXML
    public void initialize() {
        sponsorList = FXCollections.observableArrayList(service.listSponsor());

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nomSponsor"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        addActionButtons();
        sponsorTable.setItems(sponsorList);
    }

    private void addActionButtons() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = new Button("Delete");
            final Button modifyButton = new Button("Modify");

            {
                deleteButton.setOnAction(event -> {
                    Sponsors sponsor = getTableView().getItems().get(getIndex());
                    service.deleteSponsorById(sponsor.getId());
                    sponsorList.remove(sponsor);
                });

                modifyButton.setOnAction(event -> {
                    // Optionally show a popup or navigate to modify screen
                    System.out.println("Modify clicked for " + getTableView().getItems().get(getIndex()).getNomSponsor());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, modifyButton, deleteButton);
                    setGraphic(box);
                }
            }
        });
    }
}
