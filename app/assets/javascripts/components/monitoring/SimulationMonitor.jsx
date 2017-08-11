var React = require('react');
import {Tabs, Tab, Table , Button} from 'react-bootstrap'
import ComponentState from "./ComponentState";
import AssemblyState from "./AssemblyState";
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from '../redux/actions';

class SimulationMonitor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            keyTab2:-1,
            keyTab3:-1
        }
        this.start_simulation = this.start_simulation.bind(this);
        this.stop_simulation = this.stop_simulation.bind(this);
        this.completedComponentCount = this.completedComponentCount.bind(this);
    }

    doSomething(){
        if(!this.props.isSimulationComplete) {
            this.props.actions.getSimulationRunningStatus(this.props.simulation.simulationId);
        }else{
            this.props.actions.stopSimulation(this.props.simulation.simulationId);
            this.stopTimer()
        }
    }
    startTimer() {
        clearInterval(this.timer);
        this.timer = setInterval(this.doSomething.bind(this), 7000)
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

    componentWillReceiveProps(nextProps) {
    // console.log("Getting new Props");
    // console.log(nextProps);
    }

    start_simulation() {
        this.props.actions.startSimulation(this.props.simulation.simulationId);
    }

    stop_simulation() {
        this.props.actions.stopSimulation(this.props.simulation.simulationId);
        clearInterval(this.timer)
    }

    completedComponentCount(count){
        this.setState({
            completedComponentCount : count
        });
    }


    render() {
        var counter=3;
        var arr = [];
        for (var i = 0; i < this.props.simulation.components.length; i++) {
            arr.push( <div>xterm -hold -e script compLog{this.props.simulation.components[i].id} -c " scala -classpath '*.jar' componentClient.jar -c {this.props.simulation.components[i].id} -s {this.props.simulation.simulationId}" &</div> );
            arr.push( <div>sleep {counter}</div>);
            // counter =counter + .5;
        }
        for (var i = 0; i < this.props.simulation.assemblies.length; i++) {
            arr.push( <div>xterm -hold -e script assembly{this.props.simulation.assemblies[i].id} -c "scala -classpath '*.jar' assemblyClient.jar -a {this.props.simulation.assemblies[i].id} -s {this.props.simulation.simulationId}" &</div> );
            arr.push( <div>sleep {counter}</div>);
            // counter =counter + .5;
        }

        // console.log(this.props.completedComponents);
        return (
            <Tabs >
                <Tab eventKey="1" title="Simulation">
                    <div>
                        <Table >
                            <tbody>
                            <tr>
                                <td colSpan={2}>
                                    <p>Simulation Monitoring Panel</p>
                                    {this.props.response}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Total Assemblies
                                </td>
                                <td>
                                    {this.props.simulation.assemblies.length}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Total Components
                                </td>
                                <td>
                                    {this.props.simulation.components.length}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Total Completed Components
                                </td>
                                <td>
                                    {this.props.completedComponents.length}
                                </td>
                            </tr>
                            <tr>
                                <td className="rightAlign pull-right">
                                    <Button  bsStyle="primary" onClick={this.start_simulation}>
                                        Start
                                    </Button>
                                </td>
                            <td >
                                <Button  bsStyle="primary" onClick={this.stop_simulation}>
                                    Stop
                                </Button>
                            </td>
                            </tr>
                            </tbody>
                        </Table>
                    </div>
                </Tab>
                <Tab eventKey="2" title="Components">
                    <ComponentState data={this.props.simulation.components}
                                    simulationId={this.props.simulation.simulationId}
                                    completedComponents = {this.props.completedComponents}
                    />
                </Tab>
                <Tab eventKey="3" title="Assemblies">
                    <AssemblyState data={this.props.simulation.assemblies}
                                   simulationId={this.props.simulation.simulationId}
                                   />
                </Tab>
                <Tab eventKey="4" title="Run Script">
                    {arr}
                </Tab>
            </Tabs>
        );
    }
}

function mapStateToProps(state) {
    // console.log("into map state to prop in simulation monitor");
    // console.log(state);
    return {
        simulation: state.mainMode.simulationObj
        ,completedComponents:state.simulation.completedComponents
        ,response:state.simulation.response
        ,isSimulationComplete:state.simulation.isSimulationComplete
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(SimulationMonitor);