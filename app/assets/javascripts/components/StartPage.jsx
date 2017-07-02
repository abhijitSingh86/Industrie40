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
            axios.post('/simualtion/'+key+'/clear').then(function (response) {
                _this.setState({
                    response: "Previous run data cleared successfully."
                });
                _this.props.changeHandler(_this.state.selectedSimulationId)

            }).catch(function (error) {
                _this.setState({
                    response: "Error while clearing previous simulation data. \n " + error
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
            var key = this.state.selectedSimulationId;
            // var _this = this;
            // axios.post('/simualtion/'+key+'/clear').then(function (response) {
            //     _this.setState({
            //         response: "Previous run data cleared successfully."
            //     });
                this.props.changeHandler(this.state.selectedSimulationId)

            // }).catch(function (error) {
            //     _this.setState({
            //         response: "Error while clearing previous simulation data. \n " + error
            //     });
            // })
        }
    }

    nextStep() {
        this.props.saveValues(fileJson);
        this.props.nextStep();
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

    componentWillUnmount() {
        // this.serverRequest1.abort();
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
        var rows = this.createSimulationIdDropDown();
        return (
            <div>
                <ul className="form-fields">
                    <li>
                        <label> Upload Json Data File</label>
                        <input type="file" onChange={this.handleFileChange}/>
                    </li>
                    <li>{this.state.response}
                        <table><tbody><tr>
                        <td>
                            <DropdownButton  title="Select Simulation" onSelect={this.handleSelect} >
                                {rows}
                            </DropdownButton>{this.state.selectedSimulationName}
                        </td>
                        <td className="pull-right">
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
                        <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>

                    </li>
                </ul>

            </div>

        )
    }


}

module.exports = StartPage