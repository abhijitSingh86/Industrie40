var React = require('react');
import {Tabs, Tab, Table , Button} from 'react-bootstrap'
import ComponentState from "./ComponentState";
import AssemblyState from "./AssemblyState";
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from '../redux/actions';
import ComponentOverviewTab from "./ComponentOverviewTab"
import {withRouter} from "react-router-dom";


var axios = require('axios');

import Timeline from 'react-visjs-timeline'


class SimulationMonitor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            keyTab2:-1,
            keyTab3:-1,
            timelineDates:[],
            autoStart:true
        }
        this.start_simulation = this.start_simulation.bind(this);
        this.stop_simulation = this.stop_simulation.bind(this);
        this.completedComponentCount = this.completedComponentCount.bind(this);
        this.createTimeLine = this.createTimeLine.bind(this);
        this.getOperationLabel = this.getOperationLabel.bind(this);
        this.createAssemblytimeline = this.createAssemblytimeline.bind(this);
        this.createTimeLines = this.createTimeLines.bind(this);
    }



    doSomething(){
        console.log("logs for doSomething"+this.props.isSimulationComplete+":"+this.props.isLoadingComplete+":"+this.props.isStarted);
        if(!this.props.isSimulationComplete && this.props.mode != "view") {
            if(this.props.isLoadingComplete){
                this.props.actions.getSimulationRunningStatus(this.props.simulation.simulationId );
                    if(!this.props.isStarted )
                        this.start_simulation();
            }

        }else {
            this.props.actions.getSimulationRunningStatus(this.props.simulation.simulationId );
            this.props.actions.stopSimulation(this.props.simulation.simulationId, this.props.mode);
            this.stopTimer()
        }
    }
    startTimer() {
        if(this.props.mode == "view") {
            this.props.actions.getSimulationRunningStatus(this.props.simulation.simulationId );
        }

        clearInterval(this.timer);
        this.timer = setInterval(this.doSomething.bind(this), 7000)

    }

    stopTimer() {
        clearInterval(this.timer)
    }

    // componentWillMount(){
        // console.log("Into monitor Mount");
        // console.log(this.props.simulationMonitorError);
        // if(this.props.simulationMonitorError !=undefined && this.props.simulationMonitorError != "")
        // {
        //     console.log("Into monitor Mount sending back to startpage");
        //     this.props.history.push("/");
        //
        // }
    // }

    componentDidMount() {

        this.startTimer()
    }

    componentWillUnmount() {
        this.stopTimer()
    }



    start_simulation() {
        this.props.actions.startSimulation(this.props.simulation.simulationId , this.props.simulationVersionId);
    }

    stop_simulation() {
        this.props.actions.stopSimulation(this.props.simulation.simulationId,this.props.mode);
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

    createTimeLines(){
        this.createAssemblytimeline();
        this.createTimeLine();
    }
    createAssemblytimeline(){
        var simulationId = this.props.simulation.simulationId;

        var _this = this;
        axios.get('/simulation/'+simulationId+'/assemblytimeline').then(function (response) {
            function compare(a,b) {
                if (a < b)
                    return -1;
                if (a > b)
                    return 1;
                return 0;
            }
            //response.data.group.sort(compare);
            console.log(response.data);
            console.log(response.data.body);
            _this.setState({
                assemblytimeline: response.data
            });

        }).catch(function (error) {
            _this.setState({
                response: "Error retrieving the Assembly Time line data \n " + error
            });
        })

    }

    compareByGroupId(a,b) {
        if (a.id < b.id)
            return -1;
        if (a.id > b.id)
            return 1;
        return 0;
    }

    compareByGroupName(a,b) {
        if (a.group < b.group)
            return -1;
        if (a.group > b.group)
            return 1;
        return 0;
    }

    compare(a,b) {
        if (a < b)
            return -1;
        if (a > b)
            return 1;
        return 0;
    }
    createTimeLine(){
        //fetch the details of component processing

        var comps = this.props.completedComponents;
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


        myVal = myVal.sort(this.compareByGroupName.bind(this));
        grps = grps.sort(this.compareByGroupId.bind(this));

       console.log(JSON.stringify(myVal));
       this.setState({
           timelineDates:myVal
           ,groups:grps
       });
    }

    getSimulationTimeData(){
        if(!this.props.isLoadingComplete){
            return <LoadingModal interval={1} checkforstatus={this.props.actions.simulationLoadingCheck}/>;
        }else if(!(this.props.simulationTime.sttime === 0) && !(this.props.simulationTime.ettime === 0 ) ) {
            return (
                <div>
                    <tr>
                        <td>
                            Start Time
                        </td>
                        <td>
                            {(new Date(this.props.simulationTime.sttime)).toLocaleString()}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            End Time
                        </td>
                        <td>
                            {(new Date(this.props.simulationTime.ettime)).toLocaleString()}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Difference
                        </td>
                        <td>
                            {(this.props.simulationTime.ettime - this.props.simulationTime.sttime) / (60000)}
                        </td>
                    </tr>
                </div>
            );
        }else if(this.props.simulationTime.sttime ==0 && this.props.simulationTime.ettime ==0 ) {
           // if(this.props.mode == 'view')
                return <div><img src="/assets/images/loading.gif" width="5%" height="3%"/></div>;
            //else
              //  return <div></div>;
        }

        // else{
        //     return <div>Simulation is in Progress<img src="/assets/images/dots_2.gif" width="5%" height="3%"/></div>;
        // }
    }

    handleCheckBox(){
        const autoStart = this.state.autoStart
        this.setState({
           autoStart:!autoStart
        });
    }

    render() {

        console.log("Simulation Mode");
        console.log(this.props.mode);
        const options = {
            width: '100%',
            maxHeight:'500px',
            showMajorLabels: true,
            showCurrentTime: true,
            zoomMin: 10,
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


        var assemblytimeLine =<div/>;
        var componenttimeLine =<div/>;

        if(this.state.assemblytimeline){
            console.log("got into assembly Timeline");
            var aVal = this.state.assemblytimeline.data;
            aVal = aVal.sort(this.compareByGroupName.bind(this));
            var arps = this.state.assemblytimeline.groups
            arps = arps.sort(this.compareByGroupId.bind(this));
            assemblytimeLine =  <div> Assembly Timeline<Timeline
                options={options}
                items={aVal}
                groups={arps}
            /></div>;
            componenttimeLine = <div> Component Timeline <Timeline
                options={options}
                items={this.state.timelineDates}
                groups={this.state.groups}
            /></div>;
        }

        //check the mode and remove if view
        var btns = <div/>;
        if(this.props.mode != "view" && (this.props.mode == "start" && this.props.isLoadingComplete && !this.state.autoStart) ) {
            btns = <tr>
                <td className="rightAlign pull-right">
                    <Button bsStyle="primary" onClick={this.start_simulation}>
                        Start
                    </Button>
                </td>
                <td>
                    <Button bsStyle="primary" onClick={this.stop_simulation}>
                        Stop
                    </Button>
                </td>
            </tr>;
        }

        //check if completed or not. If complete then only make the btn visible
        var timelineBtn = <div/>;
        if(this.props.simulationTime.ettime !=0) {
            timelineBtn = <tr>
                <td className="rightAlign pull-left" colSpan={2}>
                    <Button bsStyle="primary" onClick={this.createTimeLines}>
                        Create TimeLines
                    </Button>
                </td>
            </tr>
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
                                    Simulation Name
                                </td>
                                <td>
                                    {this.props.simulation.simulationName}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Auto Start
                                </td>
                                <td>
                                    <input
                                        name="autostart"
                                        type="checkbox"
                                        checked={this.state.autoStart}
                                        onChange={this.handleCheckBox.bind(this)} />
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
                                <td colspan="2">
                                    {this.getSimulationTimeData()}
                                </td>
                            </tr>


                            {btns}

                            {timelineBtn}


                            </tbody>
                        </Table>
                        {componenttimeLine}
                        {assemblytimeLine}
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
                <Tab eventKey="4" title="Component Overview">
                    <ComponentOverviewTab/>
                </Tab>

            </Tabs>
        );
    }
}

class LoadingModal extends React.Component{
    constructor(props) {
        super(props);
    }



    componentDidMount() {

     this.interval = setInterval(() => {
         this.props.checkforstatus();
        }, this.props.interval*1000);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    render() {
        return <div>
            Waiting for components and assemblies
            <img src="/assets/images/dots_2.gif" width="5%" height="3%"/>
        </div>;
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
        ,simulationTime:state.simulation.simulationTime
        ,simulationVersionId:state.mainMode.simulationVersionId
        ,mode:state.mainMode.mode
        ,isLoadingComplete:state.mainMode.isLoadingComplete
        ,isStarted:state.simulation.isStarted
        ,simulationMonitorError:state.mainMode.simulationMonitorError
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default withRouter(connect(mapStateToProps,mapDispatchToProps)(SimulationMonitor));