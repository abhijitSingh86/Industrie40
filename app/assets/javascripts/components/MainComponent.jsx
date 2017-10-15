import React from "react"
import Registration from "./Registration"
import SimulationMonitor from "./monitoring/SimulationMonitor"
import StartPage from "./StartPage"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions';
import {Grid,Col,Button,Modal} from 'react-bootstrap'
import assign        from 'object-assign'


class MainComponent extends React.Component {

    constructor(props) {
        super(props);
        this.changeMainMode = this.changeMainMode.bind(this);
        this.getPanelForDisplay = this.getPanelForDisplay.bind(this);
        this.saveValues = this.saveValues.bind(this);
        this.changeToRegistrationMode = this.changeToRegistrationMode.bind(this);
        // this.changeMainMode = this.changeMainMode.bind(this);
        this.state = {
            modal:false,
            fieldValues : {
                simulationName     : null,
                simulationDesc    : null,
                operations : [{id:0,label:"fixed in Registration omdule"}],
                components      : [],
                assemblies   : [],
                operationCounter:1,
                componentCounter:0,
                assemblyCounter:0,
                assemblyTT:[],
                componentTT:[]
            }
        }
    }

    saveValues(field_value) {
       this.setState({
           fieldValues: assign({}, this.state.fieldValues, field_value)
       });
    }

    changeMainMode(simulationId,mode) {
        // this.props.actions.recordRunningMode("monitor")
        console.log("change Recieved with "+simulationId+" mode: "+mode);
       this.props.actions.changeMainMode(simulationId,mode);
    }

    changeToRegistrationMode(){
        console.log("In change registration module");
        //this.saveValues(fieldvalues)
        this.props.actions.recordRunningMode("registration")
    }

    getPanelForDisplay() {
        console.log("page mode is "+this.props.pagemode);



        if(this.props.pagemode === "registration"){
            return <Registration changeHandler={this.changeMainMode}
                                 simulationMonitorError={this.props.simulationMonitorError}
                                 fieldValues={this.state.fieldValues}
                                 saveValues={this.saveValues}/>
        } else if(this.props.pagemode === "monitor") {
            return (
                    <SimulationMonitor/>
               )
        }else{
            return <StartPage
                changeMode={this.changeToRegistrationMode}
                changeHandler={this.changeMainMode}
                simulationMonitorError={this.props.simulationMonitorError}
                saveValues={this.saveValues}/>
        }
    }

    handleHomeClick(){
        if(this.props.status != undefined && this.props.status === "view"){
            this.props.actions.recordRunningMode("reset")
        }else {
            this.setState({
                modal: true
            });
        }

    }

    handleCloseClick(){
        this.setState({
            modal:false
        });
    }

    handleContinueClick(){
        this.props.actions.recordRunningMode("reset")

        this.setState({
            modal:false
        });
    }

    render() {

        return (<div>
            <Grid>
                <Col md={1.5}>
                    <img src="/assets/images/home.jpg" width={100} height={50} onClick={this.handleHomeClick.bind(this)}/>
                </Col>
           <Col md={10.5}>
                { this.getPanelForDisplay() }
           </Col>
            </Grid>
                <Modal show={this.state.modal} onHide={this.handleCloseClick.bind(this)}>
                    <Modal.Header closeButton>
                        <Modal.Title>Alert!</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        You are leaving current window, All progress will be lost.
                        Current mode is {this.props.status}
                    </Modal.Body>

                    <Modal.Footer>
                        <Button onClick={this.handleCloseClick.bind(this)}>Cancel</Button>
                        <Button bsStyle="primary" onClick={this.handleContinueClick.bind(this)}>Continue</Button>
                    </Modal.Footer>

                </Modal>
            </div>

        );
    }

}
function mapStateToProps(state) {
    return {
        simulationId: state.mainMode.simulationId,
        monitor:state.mainMode.monitor,
        pagemode:state.mainMode.pagemode,
        status:state.mainMode.mode,
        simulationMonitorError:state.mainMode.simulationMonitorError
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(MainComponent);