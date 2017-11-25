"use strict";

import React from "react"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from '../redux/actions';
import {withRouter} from "react-router-dom";
import {Table } from 'react-bootstrap'

class ComponentOverviewTab extends React.Component {

    render() {

        var rows =<tr/>;

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

                var startTime = obj.schedulinginfo.pastOperations.sort((x, y) => CompareForSort(x.startTime, y.startTime))[0].startTime
                var sortedByEndTimeArr = obj.schedulinginfo.pastOperations.sort((x, y) => CompareForSort(x.endTime, y.endTime))

                var endTime = sortedByEndTimeArr[sortedByEndTimeArr.length - 1].endTime


                return (<tr>
                    <td>{obj.name}</td>
                    <td>{(new Date(startTime)).toLocaleString()}</td>
                    <td>{(new Date(endTime)).toLocaleString()}</td>
                    <td>{(endTime - startTime) / 60000}</td>

                </tr>);
            });
        }

        return (

            <Table striped bordered condensed hover>
                <thead>
                <tr>
                    <th>Component Name</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Time Taken</th>
                </tr>
                </thead>
               <tbody>{rows}</tbody>
            </Table>

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
