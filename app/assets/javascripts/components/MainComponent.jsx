import React from "react"
import Registration from "./Registration"
import SimulationMonitor from "./monitoring/SimulationMonitor"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions';

class MainComponent extends React.Component {
    constructor(props) {
        super(props);
        this.changeMainMode = this.changeMainMode.bind(this);
        this.getPanelForDisplay = this.getPanelForDisplay.bind(this);

    }


    changeMainMode(simulationId,mode) {
        console.log("change Recieved with "+simulationId+" mode: "+mode);
       this.props.actions.changeMainMode(simulationId,mode);
    }

    getPanelForDisplay() {
        if (this.props.monitor === false) {
            return <Registration changeHandler={this.changeMainMode} simulationMonitorError={this.props.simulationMonitorError}/>
        } else {
            return (
                    <SimulationMonitor/>
               )
        }
    }

    render() {

        return (
            <div>
                { this.getPanelForDisplay() }
            </div>
        )
    }

}
function mapStateToProps(state) {
    return {
        simulationId: state.mainMode.simulationId,
        monitor:state.mainMode.monitor,
        simulationMonitorError:state.mainMode.simulationMonitorError
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(MainComponent);