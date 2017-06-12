import React from "react"
import Registration from "./Registration"
import SimulationMonitor from "./monitoring/SimulationMonitor"

class MainComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            monitor: false
        };
        this.changeMainMode = this.changeMainMode.bind(this);
        this.getPanelForDisplay = this.getPanelForDisplay.bind(this);

    }


    changeMainMode(simulationId) {
        console.log("change Recieved with "+simulationId);
        this.setState({
            monitor: true,
            simulationId: simulationId
        });

    }

    getPanelForDisplay() {
        if (this.state.monitor === false) {
            return <Registration changeHandler={this.changeMainMode}/>
        } else {
            return <SimulationMonitor simulationId={this.state.simulationId}/>
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

module.exports = MainComponent