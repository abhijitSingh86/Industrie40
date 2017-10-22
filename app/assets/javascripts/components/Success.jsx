var React = require('react')


import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/registrationaction';

class Success extends React.Component{
    constructor(props){
        super(props)
            this.state = {
                renderSuccess:false
                ,body :""
            }
    }
  submit(){
        this.props.actions.submitDataToServer(this.props.fieldValues);
  }
  render() {
    return (
        <div>
            <ToBeSubmittedValue render={!this.props.renderSuccess} fieldValues={this.props.fieldValues}
                                previousStep = {this.props.actions.decrementStep} submit={this.submit.bind(this)} />
            <SubmittedValue render={this.props.renderSuccess} changeHandler={this.props.changeHandler}
                            simulationId={this.props.simulationId}
                            response={this.props.responseMessage}
                            previousStep = {this.props.actions.decrementStep}/>
        </div>
    )
  }
}

class ToBeSubmittedValue extends React.Component{
    render(){
        if(this.props.render) return (
            <div>
                <h2>Data Entered Successfully</h2>
                <p>Please Check and Submit </p>
                <button className="btn -default pull-left" onClick={this.props.previousStep}>Back</button>
                <button className="btn -default pull-center" onClick={this.props.submit}>Submit</button>
                <div className="tablecontainer"><pre>{JSON.stringify(this.props.fieldValues, null, 2) }</pre></div>
            </div>
        );
        else{
            return (<div/>);
        }
    }
}


class SubmittedValue extends React.Component{

    constructor(props){
        super(props)
        this.startSimulationMonitor = this.startSimulationMonitor.bind(this);
    }
    startSimulationMonitor(){
        this.props.changeHandler(this.props.simulationId,'start')
    }

    back(){
     this.props.previousStep();
    }
    render(){
         if(this.props.render && this.props.isError) {
             return  <div>
                 <p>Error during simulation data entry</p>
                 <p>{this.props.response}</p>
                 <p>
                     <button className="btn -default pull-left" onClick={this.back.bind(this)}>Back</button>
                 </p>
             </div>
         }else if(this.props.render && !this.props.isError){

             return (

                 <div>
                     <p>Simulation Details stored Successfully</p>
                     <p></p>
                     <p>
                         <input type="button" onClick={this.startSimulationMonitor} value="Go to Monitor Mode"/>
                     </p>
                 </div>
             );
         }
        else{
            return  (<div/>);
         }
    }
}

function mapStateToProps(state) {
    return {
        fieldValues:state.registration.fieldValues
        ,renderSuccess:state.registration.renderSuccess
        ,responseMessage:state.registration.responseMessage
        ,simulationId:state.registration.simulationId
        ,isError:state.registration.isError
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}
export default connect(mapStateToProps,mapDispatchToProps)(Success);