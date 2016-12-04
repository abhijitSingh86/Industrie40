package com.dik.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;

/**
 * Created by billa on 2016-12-01.
 */

@SpringView(name = Login.VIEW_NAME)
@UIScope
public class Login  extends UI {
    public static final String VIEW_NAME = "login";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("Login");
        TextField field = new TextField();
        field.setCaption("Enter user name");
        TextField password = new TextField();
        password.setCaption("Enter password");

        Button submitBtn= new Button("Submit");

        HorizontalLayout userRow = new HorizontalLayout(
                new Label("Username") , field
        );
        HorizontalLayout passwordRow = new HorizontalLayout(
                new Label("Password"),password
        );
        VerticalLayout layout=new VerticalLayout(userRow,passwordRow,submitBtn);

        setContent(layout);
    }
}
