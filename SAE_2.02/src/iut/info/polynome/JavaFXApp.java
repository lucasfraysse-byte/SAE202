package iut.info.polynome;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

/**
 * Interface Graphique JavaFX pour la SAE Polynômes IR[X].
 * Regroupe Calculs, Analyse, Sturm et Persistance.
 */
public class JavaFXApp extends Application {

    private TextField txtPolyP, txtPolyQ;
    private TextArea areaResult;
    private Label lblStatus;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IR[X] - Explorateur de Polynômes");

        // --- Styles ---
        String cssPanel = "-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-padding: 20;";
        String cssBtn = "-fx-background-color: #38bdf8; -fx-text-fill: #020617; -fx-font-weight: bold; -fx-cursor: hand;";

        // --- Header ---
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        Label title = new Label("IR[X] CALCULATOR");
        title.setStyle("-fx-font-size: 28; -fx-text-fill: #38bdf8; -fx-font-weight: bold;");
        lblStatus = new Label("Entrez vos polynômes pour commencer");
        lblStatus.setStyle("-fx-text-fill: #94a3b8;");
        header.getChildren().addAll(title, lblStatus);

        // --- Saisie ---
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(15);
        inputGrid.setVgap(10);
        inputGrid.setAlignment(Pos.CENTER);
        
        txtPolyP = new TextField();
        txtPolyP.setPromptText("Ex: x^2 + 3x - 5");
        txtPolyP.setPrefWidth(300);
        
        txtPolyQ = new TextField();
        txtPolyQ.setPromptText("Optionnel (pour +, *, /)");
        txtPolyQ.setPrefWidth(300);

        inputGrid.add(new Label("Polynôme P(x) :"), 0, 0);
        inputGrid.add(txtPolyP, 1, 0);
        inputGrid.add(new Label("Polynôme Q(x) :"), 0, 1);
        inputGrid.add(txtPolyQ, 1, 1);

        // --- Boutons d'actions ---
        FlowPane actions = new FlowPane(10, 10);
        actions.setAlignment(Pos.CENTER);

        Button btnAdd = new Button("P + Q");
        Button btnMult = new Button("P * Q");
        Button btnDiv = new Button("Division (P/Q)");
        Button btnDeriv = new Button("Dérivée P'");
        Button btnInteg = new Button("Primitive P");
        Button btnSturm = new Button("Racines (Sturm)");
        Button btnSave = new Button("Sauvegarder P");
        Button btnLoad = new Button("Charger Fichier");

        for (Button b : new Button[]{btnAdd, btnMult, btnDiv, btnDeriv, btnInteg, btnSturm, btnSave, btnLoad}) {
            b.setStyle(cssBtn);
            b.setPrefWidth(120);
        }

        actions.getChildren().addAll(btnAdd, btnMult, btnDiv, btnDeriv, btnInteg, btnSturm, btnSave, btnLoad);

        // --- Zone de Résultats ---
        areaResult = new TextArea();
        areaResult.setEditable(false);
        areaResult.setWrapText(true);
        areaResult.setPromptText("Les résultats s'afficheront ici...");
        areaResult.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #f1f5f9; -fx-font-family: 'Consolas';");

        // --- Logique des événements ---
        btnAdd.setOnAction(e -> executeOp("ADD"));
        btnMult.setOnAction(e -> executeOp("MULT"));
        btnDiv.setOnAction(e -> executeOp("DIV"));
        btnDeriv.setOnAction(e -> executeOp("DERIV"));
        btnInteg.setOnAction(e -> executeOp("INTEG"));
        btnSturm.setOnAction(e -> searchRoots());
        btnSave.setOnAction(e -> saveFile(primaryStage));
        btnLoad.setOnAction(e -> loadFile(primaryStage));

        // --- Layout Principal ---
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #020617;");
        root.getChildren().addAll(header, inputGrid, actions, areaResult);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void executeOp(String op) {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            Polynome q = txtPolyQ.getText().isEmpty() ? null : Polynome.parser(txtPolyQ.getText());
            
            String res = switch (op) {
                case "ADD" -> "Somme P + Q :\n" + p.additionner(q).toString();
                case "MULT" -> "Produit P * Q :\n" + p.multiplier(q).toString();
                case "DIV" -> {
                    DivisionEuclidienneResultat d = p.diviser(q);
                    yield "Division de P par Q :\nQuotient : " + d.getQuotient() + "\nReste : " + d.getReste();
                }
                case "DERIV" -> "Dérivée P'(x) :\n" + p.deriver().toString();
                case "INTEG" -> "Primitive P(x) :\n" + p.integrer().toString() + " + C";
                default -> "";
            };
            areaResult.setText(res);
            lblStatus.setText("Opération " + op + " réussie");
        } catch (Exception ex) {
            areaResult.setText("Erreur : " + ex.getMessage());
            lblStatus.setText("Erreur lors du calcul");
        }
    }

    private void searchRoots() {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            TextInputDialog dialog = new TextInputDialog("-10, 10");
            dialog.setTitle("Suite de Sturm");
            dialog.setHeaderText("Recherche de racines");
            dialog.setContentText("Entrez l'intervalle [a, b] (format: a, b) :");
            
            dialog.showAndWait().ifPresent(val -> {
                String[] bounds = val.split(",");
                double a = Double.parseDouble(bounds[0].trim());
                double b = Double.parseDouble(bounds[1].trim());
                int count = p.getNombreRacinesReelles(a, b);
                areaResult.setText("Suite de Sturm sur [" + a + ", " + b + "]\n" +
                                 "Nombre de racines réelles détectées : " + count);
            });
        } catch (Exception ex) {
            areaResult.setText("Erreur Sturm : " + ex.getMessage());
        }
    }

    private void saveFile(Stage stage) {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                PolynomeIO.sauvegarder(p, file.getAbsolutePath(), FormatPolynome.COEFFICIENTS);
                lblStatus.setText("Sauvegardé dans : " + file.getName());
            }
        } catch (Exception ex) {
            areaResult.setText("Erreur sauvegarde : " + ex.getMessage());
        }
    }

    private void loadFile(Stage stage) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Polynome p = PolynomeIO.charger(file.getAbsolutePath());
                txtPolyP.setText(p.toString());
                lblStatus.setText("Chargé : " + file.getName());
                areaResult.setText("Polynôme chargé avec succès !");
            }
        } catch (Exception ex) {
            areaResult.setText("Erreur chargement : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}