package com.financemanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Контроллер для экрана приветствия приложения
 */
public class WelcomeController {
    
    @FXML
    private Button launchButton;
    
    private DataAccess dataAccess;
    
    /**
     * Инициализация контроллера
     */
    @FXML
    private void initialize() {
        // Инициализация базы данных при загрузке приветственного экрана
        try {
            dataAccess = new DataAccess();
            dataAccess.connect();
            dataAccess.createTablesIfNotExist();
        } catch (Exception e) {
            System.err.println("Ошибка инициализации базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Обработчик нажатия кнопки запуска приложения
     */
    @FXML
    private void launchApplication() {
        try {
            // Получаем текущую сцену и окно
            Stage currentStage = (Stage) launchButton.getScene().getWindow();
            
            // Загружаем основное окно приложения
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/financemanager/main_window.fxml"));
            Parent root = loader.load();
            
            // Получаем контроллер основного окна и передаем ему доступ к данным
            Controller controller = loader.getController();
            controller.setDataAccess(dataAccess);
            
            // Создаем новую сцену для основного окна
            Scene mainScene = new Scene(root);
            mainScene.getStylesheets().add(getClass().getResource("/com/financemanager/styles.css").toExternalForm());
            
            // Заменяем сцену в текущем окне
            currentStage.setScene(mainScene);
            currentStage.setTitle("Финансовый менеджер - Главное окно");
            currentStage.setMinWidth(800);
            currentStage.setMinHeight(600);
            
            // Центрируем окно на экране
            currentStage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("Ошибка запуска основного приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}