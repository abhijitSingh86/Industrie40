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
        this.getPanelForDisplay = this.getPanelForDisplay.bind(this);
        this.state = {
            modal:false,
        }
    }
    getPanelForDisplay() {
        console.log("page mode is "+this.props.pagemode);
        if(this.props.pagemode === "registration"){
            return <Registration simulationMonitorError={this.props.simulationMonitorError}

                               />
        } else if(this.props.pagemode === "monitor") {
            return (
                    <SimulationMonitor/>
               )
        }else{
            return <StartPage
                simulationMonitorError={this.props.simulationMonitorError}
                />
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
        ,fieldValues:state.registration.fieldValues
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(MainComponent);