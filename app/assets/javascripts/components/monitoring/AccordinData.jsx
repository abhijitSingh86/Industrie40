import React from "react"

var axios = require('axios')
import {Table, Grid, Row, Col} from 'react-bootstrap'
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as Actions from '../redux/actions';

class AccordinData extends React.Component {

    constructor(props) {
        super(props);
        this.colFun = this.colFun.bind(this)
        // this.updateComponentState = this.updateComponentState.bind(this);
        // this.startTimer = this.startTimer.bind(this);
        // this.stopTimer = this.stopTimer.bind(this);
        this.getComponentOperationColor = this.getComponentOperationColor.bind(this);

        var updatedState = this.getComponentUpdatedStateIfExist(props)
        var flag = false;
        if(!(updatedState === undefined)){
            console.log(updatedState);
            flag = updatedState.isComplete;
        }
        this.state = {component: updatedState, completed: flag}
    }

    getComponentUpdatedStateIfExist(props) {
        var cmps = props.components;
        var temp = undefined
        cmps.map(x => {
            if (x.id === props.data.id) {
                temp = x;
            }
        })
        return temp
    }

    // updateComponentState() {
    //     if (this.props.activeKey === this.props.index && this.state.completed === false) {
    //         this.props.actions.getComponentRunningStatus(this.props.data.id, this.props.simulationId);
    //     }
    // }
    //
    // startTimer() {
    //     clearInterval(this.timer);
    //     this.updateComponentState();
    //     this.timer = setInterval(this.updateComponentState.bind(this), 7000)
    // }
    //
    // stopTimer() {
    //     clearInterval(this.timer)
    // }
    //
    // componentDidMount() {
    //     console.log("starting"+this.props.completedComponents.length);
    //     this.startTimer()
    // }
    //
    // componentWillUnmount() {
    //     console.log("STopping "+this.props.completedComponents.length);
    //     this.stopTimer()
    // }


    colFun(arr) {
        var out = [];
        var color = this.getComponentOperationColor(arr);

        for (var i = 0; i < arr.length; i++) {
            var style = {color: color[i]}
            out.push(<td style={style}>{arr[i].label}</td>)
        }
        // console.log(color);
        return out;
    };

    getOperationLabel(id) {
        var opd = this.state.component.operationDetails
        for (var i = 0; i < opd[0].length; i++) {
            // console.log(opd[0][i] +":"+id)
            if (opd[0][i].id === id)
                return opd[0][i].label;
        }
        return "No Label";
    }

    getDateTimeDisplayPanel() {
        if (this.state.component != undefined && this.state.component.schedulinginfo.pastOperations.length != 0) {
            var past = this.state.component.schedulinginfo.pastOperations
            var rows = [];
            for (var i = 0; i < past.length; i++) {
                var std = new Date(past[i].startTime)
                var etd = new Date(past[i].endTime)
                var secondsSpent = (etd - std) / 1000

                var timeString = ""
                var failTime = past[i].failureWaitTime == 0 ? "" : past[i].failureWaitTime+" Sec"
                if(past[i].status === 'failed')
                    timeString = "Failed after "+secondsSpent + " Sec";
                else
                    timeString = secondsSpent +" Sec"+ failTime
                // console.log(secondsSpent);
                rows.push(<tr>
                    <td colSpan={2}>
                        <table>
                            <tbody>
                            <tr>
                                <td>
                                    {this.getOperationLabel(past[i].operationId)} on Assembly
                                </td>
                                <td>
                                    {past[i].assemblyName}
                                </td>
                                <td>
                                    {std.toLocaleString()}
                                </td>
                                <td>
                                    {etd.toLocaleString()}
                                </td>
                                <td>
                                    {timeString}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                  </td>
                </tr>)
            }
            // console.log(rows);
            return rows;
        } else {
            return <tr>
                <td colSpan={2}>No Past Processing Records</td>
            </tr>;
        }
    }

    getComponentOperationColor(arr) {
        var color = []
        if (this.state != null && this.state.component != undefined) {
            // console.log("Into state component")
            function isFailed(row){
                row.status === "failed"
            }
            var past = this.state.component.schedulinginfo.pastOperations.filter(isFailed);
            var subarr = arr.slice(0, past.length);
            // console.log(subarr);
            // console.log(past);
            if (past.length != 0 && subarr.length == past.length &&
                subarr.every((v, i) => v.id === past[i].operationId)) {

                for (var i = 0; i < past.length; i++) {
                    color.push("#00EE00")
                }
                for (var i = past.length; i < arr.length; i++) {
                    color.push("#000000")
                }
                // console.log(color);
                return color;
            }
        }
        for (var i = 0; i < arr.length; i++) {
            color.push("#000000")
        }
        return color;
    }

    getProgressDetailsContent() {
        if (this.state.completed === true) {
            try {
                var time = 0;
                for (var i = 0; i < this.state.component.schedulinginfo.pastOperations.length; i++) {
                    time = time + (this.state.component.schedulinginfo.pastOperations[i].endTime
                        - this.state.component.schedulinginfo.pastOperations[i].startTime);
                }
                return <td>Completed : Total Time Taken: {time / 1000} Sec</td>;
            } catch (e) {
                console.log(e);
            }
        }
        else if (!(this.state.component === undefined)) {
            var op = "In Progress "
            if (this.state.component.schedulinginfo.currentOperation != null) {
                var curr = this.state.component.schedulinginfo.currentOperation;
                op = op + ": Current Operation " + this.getOperationLabel(curr.operationId)  + " ON Assembly "+curr.assemblyName;
            }
            return <td>{op}</td>;
        }
        else {
            return <td/>;
        }
    }

    componentWillReceiveProps(nextProps) {
        var updatedState = this.getComponentUpdatedStateIfExist(nextProps)
       // console.log("Got new props in Accordin updating compoent" + updatedState);
        var flag = false;
        if(updatedState != undefined){
        flag = updatedState.isComplete;
        }
        this.setState({
            component: updatedState,
            completed :flag
        })
    }

    render() {
        var progressDetailsContent = this.getProgressDetailsContent()
        var opd = this.props.data.operationDetails;
        var prorows = this.getDateTimeDisplayPanel()
        var otable = []

        //populating operation Details
        for (var i = 0; i < opd.length; i++) {
            var label = "S" + i
            var st = {color: '#000000'}
            otable.push(
                <tr>
                    <td style={st}>{label}</td>
                    {
                        this.colFun(opd[i])
                    }
                </tr>
            )
        }
        return (
            <Table bordered condensed hover>
                <tbody>
                <tr>
                    {progressDetailsContent}
                </tr>
                <tr>
                    <td>
                        <Table bordered condensed hover>
                            <tbody>
                            {prorows}
                            </tbody>
                        </Table>
                    </td>
                </tr>

                <tr>
                    <td>
                        <Table striped bordered condensed hover>
                            <thead>
                            <tr>
                                <th>Operation Details</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr cellPadding={0}>
                                <td>
                                    <Table>
                                        <tbody>
                                        {otable}
                                        </tbody>
                                    </Table>
                                </td>
                            </tr>
                            </tbody>
                        </Table>
                    </td>
                </tr>
                </tbody>
            </Table>

        );
    }
}


function mapStateToProps(state) {
    return {
        completedComponents: state.simulation.completedComponents
        , components: state.simulation.components
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AccordinData);

// module.exports = AccordinData