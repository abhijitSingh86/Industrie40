var React = require('react')
var axios = require('axios');
import {DropdownButton,MenuItem,Button} from 'react-bootstrap';

var fileJson = {};
class StartPage extends React.Component {


    constructor(props) {
        super(props);
        console.log("Start page component construstor");
        this.state = {};
        this.handleFileChange = this.handleFileChange.bind(this);
        this.nextStep = this.nextStep.bind(this);
        this.startagain_simulation = this.startagain_simulation.bind(this);
        this.handleSelect = this.handleSelect.bind(this);
        this.createSimulationIdDropDown = this.createSimulationIdDropDown.bind(this);
        this.view_simulation = this.view_simulation.bind(this);
        this.clone_simulation = this.clone_simulation.bind(this);
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
            var _this = this;
            axios.post('/simulation/'+key+'/clear').then(function (response) {
                _this.setState({
                    response: "Previous run data cleared successfully."
                });
                _this.props.changeHandler(_this.state.selectedSimulationId,"start");

            }).catch(function (error) {
                _this.setState({
                    response: "Error while clearing previous simulation data. \n " + error
                });
            })
        }
    }

    clone_simulation(){
        if(this.state.selectedSimulationId == undefined){
            this.setState({
                selectedSimulationName:"Please Select a simulation First."
            })
        }else {

            var key = this.state.selectedSimulationId;
            var _this = this;
            axios.get('/simulation/'+key+'/clone').then(function (response) {
                _this.setState({
                    response: "Previous json data retrieved successfully."
                });
                console.log(response);
                console.log(response.data);
                _this.props.saveValues(response.data);
                _this.props.changeMode();


            }).catch(function (error) {
                _this.setState({
                    response: "Error retrieving previous simulation data. \n " + error
                });
            })

        }
    }

    view_simulation(){
        if(this.state.selectedSimulationId == undefined){
            this.setState({
                selectedSimulationName:"Please Select a simulation First."
            })
        }else {
                this.props.changeHandler(this.state.selectedSimulationId,"view");
        }
    }

    nextStep() {

        this.props.saveValues(fileJson);
        this.props.changeMode();

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
        reader.onload = function () {
            var dataURL = reader.result;
            fileJson = JSON.parse(dataURL)

            console.log(dataURL)
        };
        console.log(file);
        console.log(reader.readAsText(file));
    }

    startSimulationMonitor(){
        this.props.changeHandler(this.state.selectedSimulationId)
    }
    render() {
        var er="";
        if(this.props.simulationMonitorError != undefined){
            er= this.props.simulationMonitorError;
        }else{
            er=this.state.response;
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
                    <li className="form-footer">
                        <button className="btn -primary pull-right" onClick={this.nextStep}>Create New Simulation</button>

                    </li>
                </ul>

            </div>

        )
    }


}

export default StartPage