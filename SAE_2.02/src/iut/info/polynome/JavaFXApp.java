package iut.info.polynome;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class JavaFXApp extends Application {

    private TextField txtPolyP, txtPolyQ, txtScalaire;
    private TextArea areaResult;
    private Label lblStatus;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IR[X] - Explorateur de Polynômes");

        String cssBtn = "-fx-background-color: #38bdf8; -fx-text-fill: #020617; -fx-font-weight: bold; -fx-cursor: hand;";

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        Label title = new Label("IR[X] CALCULATOR");
        title.setStyle("-fx-font-size: 28; -fx-text-fill: #38bdf8; -fx-font-weight: bold;");
        lblStatus = new Label("Entrez vos polynômes pour commencer");
        lblStatus.setStyle("-fx-text-fill: #94a3b8;");
        header.getChildren().addAll(title, lblStatus);

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

        txtScalaire = new TextField();
        txtScalaire.setPromptText("Ex: 3.5");
        txtScalaire.setPrefWidth(300);

        inputGrid.add(new Label("Polynôme P(x) :"), 0, 0);
        inputGrid.add(txtPolyP, 1, 0);
        inputGrid.add(new Label("Polynôme Q(x) :"), 0, 1);
        inputGrid.add(txtPolyQ, 1, 1);
        inputGrid.add(new Label("Scalaire k :"), 0, 2);
        inputGrid.add(txtScalaire, 1, 2);

        FlowPane actions = new FlowPane(10, 10);
        actions.setAlignment(Pos.CENTER);

        Button btnInfos    = new Button("Infos de P");
        Button btnAdd      = new Button("P + Q");
        Button btnMult     = new Button("P * Q");
        Button btnDiv      = new Button("Division (P/Q)");
        Button btnScalaire = new Button("P · k");
        Button btnDeriv    = new Button("Dérivée P'");
        Button btnInteg    = new Button("Primitive P");
        Button btnSturm    = new Button("Racines (Sturm)");
        Button btnInterp   = new Button("Interpolation");
        Button btnCourbe   = new Button("Tracer courbe");
        Button btnSave     = new Button("Sauvegarder P");
        Button btnLoad     = new Button("Charger Fichier");

        for (Button b : new Button[]{btnInfos, btnAdd, btnMult, btnDiv, btnScalaire, btnDeriv, btnInteg, btnSturm, btnInterp, btnCourbe, btnSave, btnLoad}) {
            b.setStyle(cssBtn);
            b.setPrefWidth(120);
        }

        actions.getChildren().addAll(btnInfos, btnAdd, btnMult, btnDiv, btnScalaire, btnDeriv, btnInteg, btnSturm, btnInterp, btnCourbe, btnSave, btnLoad);

        areaResult = new TextArea();
        areaResult.setEditable(false);
        areaResult.setWrapText(true);
        areaResult.setPromptText("Les résultats s'afficheront ici...");
        areaResult.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #f1f5f9; -fx-font-family: 'Consolas';");

        btnInfos.setOnAction(e -> afficherInfosDetaillees());
        btnAdd.setOnAction(e -> executeOp("ADD"));
        btnMult.setOnAction(e -> executeOp("MULT"));
        btnDiv.setOnAction(e -> executeOp("DIV"));
        btnScalaire.setOnAction(e -> executerProduitScalaire());
        btnDeriv.setOnAction(e -> executeOp("DERIV"));
        btnInteg.setOnAction(e -> executeOp("INTEG"));
        btnSturm.setOnAction(e -> searchRoots());
        btnInterp.setOnAction(e -> executerInterpolation());
        btnCourbe.setOnAction(e -> tracerCourbe());
        btnSave.setOnAction(e -> saveFile(primaryStage));
        btnLoad.setOnAction(e -> loadFile(primaryStage));

        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #020617;");
        root.getChildren().addAll(header, inputGrid, actions, areaResult);

        Scene scene = new Scene(root, 840, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void afficherInfosDetaillees() {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            if (p.estNul()) {
                areaResult.setText("Polynôme nul\nDegré : -1\nNombre de termes : 0\nPlus haut coefficient : 0");
                return;
            }
            double maxCoeff = p.getTermes().get(0).getCoefficient();
            String sb = "--- Fiche d'identité de P(X) ---\n" +
                    "Degré : " + p.getDegre() + "\n" +
                    "Nombre de termes non nuls : " + p.getTermes().size() + "\n" +
                    "Plus haut coefficient (dominant) : " + maxCoeff + "\n" +
                    "Limite en -oo : " + p.getLimiteEnMoinsInfini() + "\n" +
                    "Limite en +oo : " + p.getLimiteEnPlusInfini();
            areaResult.setText(sb);
            lblStatus.setText("Informations de P(x) extraites.");
        } catch (Exception ex) {
            areaResult.setText("Erreur : " + ex.getMessage());
        }
    }

    private void executerInterpolation() {
        try {
            TextInputDialog dialog = new TextInputDialog("1,2; 2,4; 3,9");
            dialog.setTitle("Interpolation Polynomiale");
            dialog.setHeaderText("Modélisation par interpolation de Lagrange");
            dialog.setContentText("Entrez les points (format: x1,y1; x2,y2; ...) :");
            
            dialog.showAndWait().ifPresent(val -> {
                try {
                    String[] pointsStr = val.split(";");
                    double[] x = new double[pointsStr.length];
                    double[] y = new double[pointsStr.length];
                    
                    for (int i = 0; i < pointsStr.length; i++) {
                        String[] coord = pointsStr[i].split(",");
                        x[i] = Double.parseDouble(coord[0].trim());
                        y[i] = Double.parseDouble(coord[1].trim());
                    }
                    
                    Polynome p = Polynome.interpoler(x, y);
                    txtPolyP.setText(p.toString());
                    areaResult.setText("Polynôme d'interpolation calculé et injecté dans P(x) :\n" + p);
                    lblStatus.setText("Interpolation réussie.");
                } catch (Exception ex) {
                    areaResult.setText("Erreur d'interpolation : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            areaResult.setText("Erreur : " + ex.getMessage());
        }
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
        }
    }

    private void executerProduitScalaire() {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            double k = Double.parseDouble(txtScalaire.getText().trim());
            Polynome resultat = p.multiplierParScalaire(k);
            areaResult.setText("Produit scalaire P · " + k + " :\n" + resultat);
            lblStatus.setText("Produit scalaire réussi.");
        } catch (NumberFormatException ex) {
            areaResult.setText("Erreur : le scalaire k doit être un nombre (ex: 3.5)");
        } catch (Exception ex) {
            areaResult.setText("Erreur : " + ex.getMessage());
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
                try {
                    String[] bornes = val.split(",");
                    double a = Double.parseDouble(bornes[0].trim());
                    double b = Double.parseDouble(bornes[1].trim());
                    int nombreRacines = p.getNombreRacinesReelles(a, b);

                    StringBuilder message = new StringBuilder();
                    message.append("Suite de Sturm sur [").append(a).append(", ").append(b).append("]\n");
                    message.append("Nombre de racines réelles détectées : ").append(nombreRacines).append("\n");

                    if (nombreRacines == 1) {
                        double racine;
                        if (p.evaluer(a) * p.evaluer(b) < 0) {
                            double gauche = a, droite = b;
                            for (int i = 0; i < 60; i++) {
                                double milieu = (gauche + droite) / 2.0;
                                if (p.evaluer(gauche) * p.evaluer(milieu) <= 0) droite = milieu;
                                else gauche = milieu;
                            }
                            racine = (gauche + droite) / 2.0;
                            message.append("Racine trouvée par dichotomie : x ≈ ").append(racine);
                        } else {
                            Polynome derivee = p.deriver();
                            double x = (a + b) / 2.0;
                            for (int i = 0; i < 60; i++) {
                                double valeurDerivee = derivee.evaluer(x);
                                if (Math.abs(valeurDerivee) < 1e-9) break;
                                x -= p.evaluer(x) / valeurDerivee;
                            }
                            racine = x;
                            message.append("Racine trouvée par Newton (multiplicité paire) : x ≈ ").append(racine);
                        }
                    }

                    areaResult.setText(message.toString());
                    lblStatus.setText("Sturm terminé.");
                } catch (Exception ex) {
                    areaResult.setText("Erreur : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            areaResult.setText("Erreur Sturm : " + ex.getMessage());
        }
    }

    private void tracerCourbe() {
        try {
            Polynome p = Polynome.parser(txtPolyP.getText());
            TextInputDialog dialog = new TextInputDialog("-5, 5");
            dialog.setTitle("Courbe représentative");
            dialog.setHeaderText("Tracé de P(x)");
            dialog.setContentText("Intervalle [a, b] (format: a, b) :");

            dialog.showAndWait().ifPresent(val -> {
                try {
                    String[] bornes = val.split(",");
                    double a = Double.parseDouble(bornes[0].trim());
                    double b = Double.parseDouble(bornes[1].trim());
                    if (a >= b) throw new IllegalArgumentException("a doit être strictement inférieur à b.");

                    NumberAxis xAxis = new NumberAxis(a, b, (b - a) / 10.0);
                    xAxis.setLabel("x");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("P(x)");

                    LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
                    chart.setTitle("P(x) = " + p);
                    chart.setCreateSymbols(false);
                    chart.setLegendVisible(false);

                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    int nbPoints = 300;
                    double pas = (b - a) / (nbPoints - 1);
                    for (int i = 0; i < nbPoints; i++) {
                        double x = a + i * pas;
                        series.getData().add(new XYChart.Data<>(x, p.evaluer(x)));
                    }
                    chart.getData().add(series);

                    Stage fenetre = new Stage();
                    fenetre.setTitle("Courbe de P(x)");
                    fenetre.setScene(new Scene(chart, 700, 450));
                    fenetre.show();
                    lblStatus.setText("Courbe tracée sur [" + a + ", " + b + "].");
                } catch (Exception ex) {
                    areaResult.setText("Erreur tracé : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            areaResult.setText("Erreur : " + ex.getMessage());
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
                lblStatus.setText("Sauvegardé : " + file.getName());
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