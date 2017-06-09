import React from "react"
var axios = require('axios')
import {Table, Grid, Row, Col} from 'react-bootstrap'
class AccordinData extends React.Component {

    constructor(props) {
        super(props);
        this.colFun = this.colFun.bind(this)
        this.updateComponentState = this.updateComponentState.bind(this);
        this.startTimer = this.startTimer.bind(this);
        this.stopTimer = this.stopTimer.bind(this);
        this.getComponentOperationColor = this.getComponentOperationColor.bind(this);
        this.state = {component: undefined, completed: false}
    }

    updateComponentState() {
        if (this.props.activeKey === this.props.index && this.state.completed === false) {
            var _this = this;
            axios.get('/componentStatus/' + this.props.simulationId + '/' + this.props.data.id).then(function (response) {

                var flag = false;
                if (response.data.body.opCount === response.data.body.schedulinginfo.pastOperations.length) {
                    flag = true;
                }
                _this.setState({
                    component: response.data.body,
                    completed: flag
                });

            }).catch(function (error) {
                _this.setState({
                    error: "Error while Starting Simulation. \n " + error
                });
            })
        }
    }

    startTimer() {
        clearInterval(this.timer);
        this.updateComponentState();
        this.timer = setInterval(this.updateComponentState.bind(this), 7000)
    }

    stopTimer() {
        clearInterval(this.timer)
    }

    componentDidMount() {
        this.startTimer()
    }

    componentWillUnmount() {
        this.stopTimer()
    }


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
        if (this.state.component != undefined) {
            var past = this.state.component.schedulinginfo.pastOperations
            var rows = [];
            for (var i = 0; i < past.length; i++) {
                var std = new Date(past[i].startTime)
                var etd = new Date(past[i].endTime)
                var secondsSpent = (etd - std) / 1000
                console.log(secondsSpent);
                rows.push(<tr>
                    <td>{this.getOperationLabel(past[i].operationId)}</td>
                    <td>{secondsSpent}</td>
                </tr>)
            }
            console.log(rows);
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
            var past = this.state.component.schedulinginfo.pastOperations
            if (past.length > 0) {
                var flag = true
                for (var i = 0; i < past.length; i++) {
                    if (past[i].operationId != arr[i].id) {
                        flag = false;
                        break;
                    }
                }

                if (flag === true) {
                    for (var i = 0; i < past.length; i++) {
                        color.push("#00EE00")
                    }
                    for (var i = past.length; i < arr.length; i++) {
                        color.push("#000000")
                    }
                    // console.log(color)
                    return color;
                }

            }
        }
        for (var i = 0; i < arr.length; i++) {
            color.push("#000000")
        }
        // console.log(color)
        return color;

    }

    callCompletionEvent() {
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
        else if(this.state.component != undefined){
            return <td>In Progress : Current Operation {this.state.schedulinginfo.currentOperation}</td>;
        }
        else{
            return <td/>;
        }
    }

    render() {


        var data = this.callCompletionEvent()
        var opd = this.props.data.operationDetails;
        var prorows = this.getDateTimeDisplayPanel()
        var otable = []

        var onlineStyle = {color: '#00EE00'}
        if (this.state != null && this.state.component != undefined) {
            if (this.state.component.online == false)
                onlineStyle = {color: '#EE0000'}
        }
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
        console.log(otable)
        return (
            <Table  bordered condensed hover>
                <tbody>
                <tr>
                    {data}
                </tr>
                <tr>
                    <td>
                        <Table bordered condensed hover>
                            <tbody>
                            {  prorows}
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
                                    <Table >
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

module.exports = AccordinData