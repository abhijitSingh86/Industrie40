package com.dik.ui;


import com.dik.models.Component;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {


    private final ComponentRepository repo;

//    private final CustomerEditor editor;

    final Grid grid;

    final TextField filter;

    private final Button addNewBtn;

    @Autowired
    public VaadinUI(ComponentRepository repo) {
        this.repo = repo;
//        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid);
        setContent(mainLayout);

        // Configure layouts and components
        actions.setSpacing(true);
//        mainLayout.setMargin(true);
//        mainLayout.setSpacing(true);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("id", "name");

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(e -> listCustomers(e.getText()));

        // Connect selected Customer to editor or hide if none is selected
//        grid.addSelectionListener(e -> {
//            if (e.getSelected().isEmpty()) {
//                editor.setVisible(false);
//            }
//            else {
//                editor.editCustomer((Customer) grid.getSelectedRow());
//            }
//        });

        // Instantiate and edit new Customer the new button is clicked
//        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
//        editor.setChangeHandler(() -> {
//            editor.setVisible(false);
//            listCustomers(filter.getValue());
//        });

        // Initialize listing
        listCustomers(null);
    }

    // tag::listCustomers[]
    void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(
                    new BeanItemContainer(Component.class, repo.findAll()));
        } else {
            grid.setContainerDataSource(new BeanItemContainer(Component.class,
                    repo.findAll()));
        }
    }
    // end::listCustomers[]

}