var React = require('react');
import {Tabs, Tab, Table , Button} from 'react-bootstrap'
import ComponentState from "./ComponentState";
import AssemblyState from "./AssemblyState";
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from '../redux/actions';

import Timeline from 'react-visjs-timeline'


class SimulationMonitor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            keyTab2:-1,
            keyTab3:-1,
            timelineDates:[]
        }
        this.start_simulation = this.start_simulation.bind(this);
        this.stop_simulation = this.stop_simulation.bind(this);
        this.completedComponentCount = this.completedComponentCount.bind(this);
        this.createTimeLine = this.createTimeLine.bind(this);
        this.getOperationLabel = this.getOperationLabel.bind(this);
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

    getOperationLabel(id) {
        var opd = this.props.completedComponents[0].operationDetails
        for (var i = 0; i < opd[0].length; i++) {
            // console.log(opd[0][i] +":"+id)
            if (opd[0][i].id === id)
                return opd[0][i].label;
        }
        return "No Label";
    }

    createTimeLine(){
        //fetch the details of component processing

        var comps = this.props.completedComponents;
        // var d=[];
        // comps.map((x) => x.schedulinginfo.pastOperations.forEach((y)=>d.push(y)))
        //
        // console.log(d);
        var myVal=[];
        var grps=[];
        comps.map((y)=>{
            grps.push({"id":y.name});
            var ro = y.schedulinginfo.pastOperations;


            ro.map((x)=> {
                var con = this.getOperationLabel(x.operationId)+"\n On Assembly "+x.assemblyName+"\n"+x.status;
                myVal.push({
                    "start": new Date(x.startTime), "end": new Date(x.endTime),  // end is optional
                    "content": con, "group": y.name
                })
            });
        })
       console.log(JSON.stringify(myVal));
       this.setState({
           timelineDates:myVal
           ,groups:grps
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

        const options = {
            width: '100%',
            height:'500px',
            showMajorLabels: true,
            showCurrentTime: true,
            zoomMin: 0,
            zoomMax:315360000000000,
            zoomable:true,
            moveable:true,
            type: 'background',
            format: {
                minorLabels: {
                    millisecond:'SSS'
                }
            }
        }
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
                            <tr>
                                <td className="rightAlign pull-left" colSpan={2} >
                                    <Button  bsStyle="primary" onClick={this.createTimeLine}>
                                        Create TimeLine
                                    </Button>

                                </td>

                            </tr>
                            </tbody>
                        </Table>
                        <Timeline
                            options={options}
                            items={this.state.timelineDates}
                            groups={this.state.groups}
                        />
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