/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.contexts.SessionContext;
import client.contexts.UserContext;
import client.di.MyFXML;
import client.models.TaskListModel;
import client.scenes.*;
import commons.models.BoardInfo;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import javax.inject.Inject;
import java.util.Objects;

public class App {
    private final MyFXML fxmlLoader;
    private Stage primaryStage;

    private Scene add;
    private Scene helloScene;
    private Scene adminLoginScene;
    private Scene userLoginScene;
    private Scene boardScene;
    private HelloWorldScene helloCtrl;
    private AdminLoginScene adminLoginCtrl;
    private UserLoginScene userLoginCtrl;
    private BoardSceneController boardSceneCtrl;
    private Scene workScene;
    private WorkspaceSceneController workCtrl;


    private UserContext userContext;
    private SessionContext sessionContext;

    @Inject
    TaskListModel.Factory taskListFactory;

    @Inject
    public App(UserContext userContext, SessionContext sessionContext, MyFXML fxmlLoader) {
        this.userContext = userContext;
        this.sessionContext = sessionContext;
        this.fxmlLoader = fxmlLoader;
    }

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;

        //Sets the program logo
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/client/icons/logo.png"))));

        // Load scene templates
        var helloPair = fxmlLoader.load(HelloWorldScene.class, "client", "scenes", "HelloWorld.fxml");
        var adminLoginSceneParentPair = fxmlLoader.load(AdminLoginScene.class, "client", "scenes", "AdminLogin.fxml");
        var userLoginSceneParentPair = fxmlLoader.load(UserLoginScene.class, "client", "scenes", "UserLogin.fxml");
        var work = fxmlLoader.load(WorkspaceSceneController.class, "client", "scenes", "Workspace.fxml");
        var boardSceneParentPair = fxmlLoader.load(BoardSceneController.class, "client", "scenes", "BoardScene.fxml");

        // Get controllers
        this.helloCtrl = helloPair.getKey();
        this.adminLoginCtrl = adminLoginSceneParentPair.getKey();
        this.userLoginCtrl = userLoginSceneParentPair.getKey();
        this.boardSceneCtrl = boardSceneParentPair.getKey();
        this.workCtrl = work.getKey();

        // Create scenes
        this.helloScene = new Scene(helloPair.getValue());
        this.adminLoginScene = new Scene(adminLoginSceneParentPair.getValue());
        this.userLoginScene = new Scene(userLoginSceneParentPair.getValue());
        this.boardScene = new Scene(boardSceneParentPair.getValue());
        this.workScene = new Scene(work.getValue());

        // Init fonts
        Font regular = Font.loadFont(getClass().getResourceAsStream("/client/fonts/Inter-Regular.ttf"), 16);

        Font medium = Font.loadFont(getClass().getResourceAsStream("/client/fonts/Inter-Medium.ttf"), 16);

        Font semiBold = Font.loadFont(getClass().getResourceAsStream("/client/fonts/Inter-SemiBold.ttf"), 16);

        Font Bold = Font.loadFont(getClass().getResourceAsStream("/client/fonts/Inter-Bold.ttf"), 16);


        // Load stylesheet in scenes
        try {
            this.boardScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
            this.helloScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
            this.adminLoginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
            this.userLoginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
            this.workScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
            this.boardScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Warning: Couldn't load stylesheet. It's normal if you see this on the CI, sass building is disabled there.");
        }

        showUserLogin();
        primaryStage.show();



        boolean BYPASS_LOGIN = false;

        if (BYPASS_LOGIN) {
            userContext.login("ABC");
            showWorkspace();
        }
    }


    public void showWorkspace() {
        primaryStage.setTitle("Workspace Overview");
        primaryStage.setScene(workScene);
        workCtrl.loadBoards();
    }

    public void showHello() {
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(helloScene);
    }

    public void showAdminLogin() {
        System.out.println("Show admin login");
        primaryStage.setTitle("Admin Login");
        primaryStage.setScene(adminLoginScene);
    }

    public void showUserLogin() {
        System.out.println("Show user login");
        primaryStage.setTitle("User Login");
        primaryStage.setScene(userLoginScene);
    }

    public void showBoard(String boardKey) {
        // Verify board exists
        if (!userContext.getBoardExists(boardKey) || userContext.getIsAdmin()) return;

        var info = new BoardInfo();
        info.setJoinKey(boardKey);
        userContext.getKeychain().updateEntry(info);

        System.out.println("Show board");
        primaryStage.setTitle("Board");
        primaryStage.setScene(boardScene);
        boardSceneCtrl.loadBoard(boardKey);
    }

    public void createBoard() {
        System.out.println("Create board");
        var board = userContext.createBoard();
        userContext.getKeychain().updateEntry(board);
        showBoard(board.getJoinKey());
    }

    public BoardSceneController getBoardSceneCtrl() {
        return this.boardSceneCtrl;
    }


}
