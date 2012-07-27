package org.wyona.gwt.trials.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.user.client.ui.RootPanel;

public class ScrollTableTrial implements EntryPoint {

    @Override
    public void onModuleLoad() {
        int n = 10;

        FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
        headerTable.setText(0, 0, "header");
        //headerTable.setHTML(0, 1, "<span style='visibility:hidden'></span>");
        FixedWidthGrid dataTable = new FixedWidthGrid();

        int m = 2; // need at least 2 columns else IE cannot fill all the available space!!! 
        dataTable.resize(n, m); // needed to be able to use dataTable.setHTML/setText on the corresponding cells
        for (int i = 0; i < n; i++) {
            dataTable.setText(i, 0, "data #"+i);
        }

        ScrollTable scrollTable = new ScrollTable(dataTable, headerTable);

        scrollTable.setMaximumColumnWidth(1, 0); // needed so that the dummy column is as narrow as possible
        scrollTable.setResizePolicy(ResizePolicy.FILL_WIDTH); // needed so that the columns take all the available space
        scrollTable.setHeight("7ex"); // needed else table is invisible (!)

        RootPanel.get("scroll-table-slot").add(scrollTable);
    }
}
