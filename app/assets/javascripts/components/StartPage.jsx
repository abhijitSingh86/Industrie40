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

        this.props.history.listen((location,action)=>{
            console.log("Into browser listen");
            if(location.pathname === "/"){
                //clear two
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
        })
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
            this.props.actions.changeMainMode(this.props.simulationId,'view');
        }
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
                            </DropdownButton>{this.state.selectedSimulationName}
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
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default withRouter(connect(mapStateToProps,mapDispatchToProps)(StartPage));
