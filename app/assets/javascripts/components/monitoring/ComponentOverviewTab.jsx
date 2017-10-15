"use strict";

import React from "react"

var FixedDataTable = require('fixed-data-table');

const {Table, Column, Cell} = FixedDataTable;

const TextCell = ({rowIndex, data, col, ...props}) => (
    <Cell {...props} >
        {data.getObjectAt(rowIndex)[col]}
    </Cell>
);

class DataListWrapper {
    constructor(indexMap, data) {
        this._indexMap = indexMap;
        this._data = data;
    }

    getSize() {
        return this._indexMap.length;
    }

    getObjectAt(index) {
        return this._data.getObjectAt(
            this._indexMap[index],
        );
    }
}

class ComponentOverviewTab extends React.Component {
    constructor(props) {
        super(props);

        this._dataList = [{
            "componentId":10,
            "componentName":"c1"
        },
            {
                "componentId":12,
                "componentName":"c2"
            }];
        this.state = {
            rows: this._dataList,
        };

        this._onFilterChange = this._onFilterChange.bind(this);
    }

    _onFilterChange(e) {
        console.log("in fileter ")
        if (!e.target.value) {
            this.setState({
                rows: this._dataList,
            });
        }

        var filterBy = e.target.value.toLowerCase();
        var size = this._dataList.getSize();
        var filteredIndexes = [];
        for (var index = 0; index < size; index++) {
            var {component} = this._dataList.getObjectAt(index);
            if (component.toLowerCase().indexOf(filterBy) !== -1) {
                filteredIndexes.push(index);
            }
        }

        this.setState({
            rows: new DataListWrapper(filteredIndexes, this._dataList),
        });
    }

    render() {
        var {rows} = this.state;
        console.log("in Component Overview");
        console.log(rows);
        return (

            <Table
                rowsCount={this.state.rows.length}
                rowHeight={50}
                headerHeight={50}
                width={1000}
                height={500}>
                <Column
                    header={<Cell>Component Id</Cell>}
                    cell={props => (
                        <Cell {...props}>
                            {this.state.rows[props.rowIndex].componentId}
                        </Cell>
                    )}
                    width={200}
                />
                <Column
                    header={<Cell>Component Name</Cell>}
                    cell={props => (
                        <Cell {...props}>
                            {this.state.rows[props.rowIndex].componentName}
                        </Cell>
                    )}
                    width={200}
                />
            </Table>

        );
    }
}


export default ComponentOverviewTab;