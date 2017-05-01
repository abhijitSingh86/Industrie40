/**
 * Created by billa on 14.04.17.
 */

import React from 'react';
// import ReactDataGrid from "rdg/react-data-grid.js";
const ReactDataGrid = require('rdg');

 const TableView = React.createClass({



    getInitialState() {
        this._columns = [{
            key: 'id',
            name: 'ID',
            width: 80
        }, {
            key: 'Name',
            name: 'Name',
            editable: true
        }]
        return {
            rows: [{
                id: "1",
                Name: "sdfsdf"
            }]
        };
    }
,
    handleGridRowsUpdated({
                              fromRow,
                              toRow,
                              updated
                          }) {
        let rows = this.state.rows;

        for (let i = fromRow; i <= toRow; i++) {
            let rowToUpdate = rows[i];
            let updatedRow = React.addons.update(rowToUpdate, {
                $merge: updated
            });
            rows[i] = updatedRow;
        }

        this.setState({
            rows
        });
    },

    rowGetter(i) {
        if(this.state.rows != undefined)
        return this.state.rows[i];
    },

    render() {
        return ( < ReactDataGrid enableCellSelect = {
            true
        }
                                 columns = {
                                     this._columns
                                 }
                                 rowGetter = {
                                     this.rowGetter
                                 }
                                 rowsCount = {
                                     this.state.rows.length
                                 }
                                 minHeight = {
                                     200
                                 }
                                 onGridRowsUpdated = {
                                     this.handleGridRowsUpdated
                                 }
        />);
    }
})

export default TableView;