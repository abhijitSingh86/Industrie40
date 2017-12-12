"use strict";

import React from "react"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from '../redux/actions';
import {withRouter} from "react-router-dom";
import ReactTable from "react-table";
import "react-table/react-table.css";
class ComponentOverviewTab extends React.Component {

    render() {

        var rows =[];

        if(this.props.completedComponents.length >0) {
            rows = this.props.completedComponents.map(function (obj) {

                function CompareForSort(first, second) {
                    if (first == second)
                        return 0;
                    if (first < second)
                        return -1;
                    else
                        return 1;
                }

                var ptime = 0;

                obj.schedulinginfo.pastOperations.map(function(x){
                    ptime += x.endTime - x.startTime;
                });

                var startTime = obj.schedulinginfo.pastOperations.sort((x, y) => CompareForSort(x.startTime, y.startTime))[0].startTime
                var sortedByEndTimeArr = obj.schedulinginfo.pastOperations.sort((x, y) => CompareForSort(x.endTime, y.endTime))

                var endTime = sortedByEndTimeArr[sortedByEndTimeArr.length - 1].endTime

                var data = {
                    "name" : obj.name
                    ,"stime":(new Date(startTime)).toLocaleString()
                    ,"etime":(new Date(endTime)).toLocaleString()
                    ,"ptspent":parseFloat(ptime / 60000).toFixed(2)
                    ,"tspent":parseFloat((endTime - startTime) / 60000).toFixed(2)
                }

                return data;
            });
        }

        return (
            <ReactTable
                data={rows}
                columns={[
                    {
                        Header:"Component Name",
                        accessor:"name"
                    },
                    {
                        Header:"Start Time",
                        accessor:"stime"
                    },
                    {
                        Header:"End Time",
                        accessor:"etime"
                    },{
                        Header:"Processing Time Spent (In Min.)",
                        accessor:"ptspent"
                    },{
                        Header:"Total Time Spent (In Min.)",
                        accessor:"tspent"
                    }
                ]}
                defaultPageSize={10}
                className="-striped -highlight"
            />
        );
    }
}
function mapStateToProps(state) {
    // console.log("into map state to prop in simulation monitor");
    // console.log(state);
    return {
        completedComponents:state.simulation.completedComponents
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default withRouter(connect(mapStateToProps,mapDispatchToProps)(ComponentOverviewTab));
