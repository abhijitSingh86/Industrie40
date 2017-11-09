var React = require('react')
var axios = require('axios');
import {DropdownButton,MenuItem,Button} from 'react-bootstrap';
import {connect}  from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions';
import {withRouter} from "react-router-dom";

var fileJson = {};
class StartPage extends React.Component {


    constructor(props) {
        super(props);
        console.log("Start page component construstor");
        this.state={};
        this.handleFileChange = this.handleFileChange.bind(this);
        this.startagain_simulation = this.startagain_simulation.bind(this);
        this.handleSelect = this.handleSelect.bind(this);
        this.createSimulationIdDropDown = this.createSimulationIdDropDown.bind(this);
        this.view_simulation = this.view_simulation.bind(this);
        this.clone_simulation = this.clone_simulation.bind(this);
        this.handleVersionSelectd = this.handleVersionSelectd.bind(this)

        this.props.history.listen((location,action)=>{
            console.log("Into browser listen");
            if(location.pathname === "/"){
                //clear two
                this.props.actions.resetStartPageState();
                this.props.actions.resetRegistrationPageState();
                this.props.actions.resetSimulationAndMainMode();
             }else if(location.pathname === "/register"){
                this.props.actions.resetStartPageState();
                this.props.actions.resetSimulationAndMainMode();
             }
             else if(location.pathname === "/monitor"){
                this.props.actions.resetStartPageState();
                this.props.actions.resetRegistrationPageState();
            }
            console.log(location);
            console.log(action);
        })
    }

    createSimulationIdDropDown(){
        var rows =[]
        if(this.state.simulations!=undefined){
            var sims = this.state.simulations;

            for(var i=0;i<sims.length;i++){
                rows.push(<MenuItem eventKey={sims[i].simulationId}>{sims[i].simulationName}</MenuItem> )
            }
        }else{
            rows.push(<MenuItem eventKey={9999}>Not Loaded</MenuItem> )
        }
        return rows;
    }

    handleSelect(evt,key){
        // console.log( evt)
        // console.log(key.target.innerHTML);
        // console.log(key)
        this.setState({
            selectedSimulationName : key.target.innerHTML,
            selectedSimulationId : evt
        });

        this.props.actions.clearVersionInfo();

    }
    startagain_simulation(){

        if(this.state.selectedSimulationId == undefined){
            this.setState({
                selectedSimulationName:"Please Select a simulation First."
            })
        }else {
            var key = this.state.selectedSimulationId;
            this.props.actions.setupforRerun(key);
            this.props.actions.changeMainMode(key,'start');
        }
    }


    componentWillReceiveProps(nextProps){
        if(nextProps.monitor){
            this.props.history.push("/monitor");
        }

        console.log("In startpahe component receive props");
        console.log(nextProps);

        if(nextProps.versionsFetched && nextProps.versions.length ===0){
            this.props.actions.changeMainMode(this.state.selectedSimulationId, 'view');
        }
    }

    clone_simulation(){
        if(this.state.selectedSimulationId == undefined){
            this.setState({
                selectedSimulationName:"Please Select a simulation First."
            })
        }else {
            var key = this.state.selectedSimulationId;
           this.props.actions.cloneSetup(key);
            this.props.history.push("/register");
        }
    }

    view_simulation(){
        if(this.state.selectedSimulationId == undefined){
            this.setState({
                selectedSimulationName:"Please Select a simulation First."
            })
        }else {
            //event fire to fetch versions,
            if(!this.props.versionsFetched) {
                this.props.actions.getVersionForSimulation(this.state.selectedSimulationId);
            }else {
                // if any then display each in new select box
                this.props.actions.changeMainMode(this.state.selectedSimulationId, 'view');
            }
        }
    }

    handleVersionSelectd(e,k){
        this.props.actions.changeMainMode(this.state.selectedSimulationId, 'view',e);
    }

    componentDidMount() {
        var _this = this;

        this.serverRequest1 = axios.get('/simulation').then(function (response) {
            _this.setState({
                simulations: response.data.body.simulations
            });


        }).catch(function (error) {
            _this.setState({
                response: "Error while retrieving Simulations. \n " + error,
                body: null
            });
        })
    }

    handleFileChange(e) {
        var file = e.target.files[0];
        var reader = new FileReader();
        reader.onload =  ()=> {
            var dataURL = reader.result;
            fileJson = JSON.parse(dataURL)
            this.props.actions.saveFieldValueData(fileJson);
            this.props.actions.recordRunningMode("registration");
            this.props.history.push("/register");
            console.log(dataURL)
        };
        console.log(file);
        console.log(reader.readAsText(file));
    }

    startSimulationMonitor(){
        this.props.actions.changeMainMode(this.props.simulationId,'start');
    }


    render() {
        var er="";
        if(this.props.simulationMonitorError != undefined){
            er= this.props.simulationMonitorError;
        }else{
            er=this.props.response;
        }

        var veriosnDropDown = <div/>
        if(this.props.versionsFetched) {

            var versionRows = this.props.versions.map((x) => {
                return <MenuItem eventKey={x}>{x}</MenuItem>
            });

            veriosnDropDown = <DropdownButton title="Select Version" onSelect={this.handleVersionSelectd}>
                {versionRows}
            </DropdownButton>
        }


        var rows = this.createSimulationIdDropDown();
        return (
            <div>
                <ul className="form-fields">
                    <li>
                        <label> Upload Json Data File</label>
                        <input type="file" onChange={this.handleFileChange}/>
                    </li>
                    <li>{er}
                        <table><tbody><tr>
                        <td>
                            <DropdownButton  title="Select Simulation" onSelect={this.handleSelect} >
                                {rows}
                            </DropdownButton>{this.state.selectedSimulationName} &nbsp;&nbsp;&nbsp;
                            {veriosnDropDown}
                        </td>
                        <td className="pull-right">
                            <Button  bsStyle="primary" onClick={this.clone_simulation}>
                                Clone
                            </Button>
                            <Button  bsStyle="primary" onClick={this.view_simulation}>
                               View
                            </Button>
                            <Button  bsStyle="primary" onClick={this.startagain_simulation}>
                                Re Run
                            </Button>
                        </td>
                        </tr></tbody></table>

                    </li>

                </ul>

            </div>

        )
    }


}

function mapStateToProps(state) {
    return {
        fieldValues:state.registration.fieldValues
        ,simulationMonitorError:state.mainMode.simulationMonitorError
        ,response:state.startPage.responseMsg
        ,monitor:state.mainMode.monitor
        ,versionsFetched:state.startPage.versionsFetched
        ,versions:state.startPage.versions
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default withRouter(connect(mapStateToProps,mapDispatchToProps)(StartPage));
