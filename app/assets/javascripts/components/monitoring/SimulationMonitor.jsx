var React = require('react');
var axios = require('axios')
import {Tabs, Tab, Table , Button} from 'react-bootstrap'

import ComponentState from "./ComponentState"

class SimulationMonitor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            response: undefined,
            body: {
                components: []
            }
        }

        this.start_simulation = this.start_simulation.bind(this);
        this.stop_simulation = this.stop_simulation.bind(this);

    }

    componentDidMount() {
        var _this = this;
        this.serverRequest = axios.get('/simulation/' + this.props.simulationId).then(function (response) {
            _this.setState({
                body: response.data.body
            });

        }).catch(function (error) {
            _this.setState({
                response: "Error while retrieving Simulation details. \n " + error,
                body: null
            });
        })
    }

    componentWillUnmount() {
        this.serverRequest.abort();
    }

    start_simulation() {
        var _this = this;
        axios.post('/start/' + this.props.simulationId).then(function (response) {
            _this.setState({
                response: "Simulation started successfully"
            });

        }).catch(function (error) {
            _this.setState({
                response: "Error while Starting Simulation. \n " + error
            });
        })
    }

    stop_simulation() {
        var _this = this;
        axios.post('/stop/' + this.props.simulationId).then(function (response) {
            _this.setState({
                response: "Simulation stopped successfully"
            });

        }).catch(function (error) {
            _this.setState({
                response: "Error while Stopping Simulation. \n " + error
            });
        })
    }

    render() {

        var ele = ""
        if (this.state.response != undefined) {
            ele = <div>{this.state.response}</div>
        }
        var data = [];
        if (this.state.body != undefined)
            data = this.state.body.components

        return (
            <Tabs>
                <Tab eventKey="1" title="Simulation">
                    <div>
                        <Table >
                            <tbody>
                            <tr>
                                <td colSpan={2}>
                                    <p>Simulation Monitoring Panel</p>
                                    {ele}
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
                    Components
                    <ComponentState data={data} simulationId={this.props.simulationId}/>
                </Tab>
                <Tab eventKey="3" title="Assemblies">Assemblies</Tab>
            </Tabs>

        );
    }
}

module.exports = SimulationMonitor