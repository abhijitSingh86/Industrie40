
import React from "react"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/registrationaction';

class SimulationForm extends React.Component{
  constructor(props){
    super(props);
    this.nextStep = this.nextStep.bind(this);
  }
  render() {
    return (
      <div>
        <h2>Simulation Details</h2>
        <ul className="form-fields">
          <li>
            <label>Name</label>
            <input type="text" ref={(simulationName)=>{this.simulationName = simulationName}} defaultValue={this.props.fieldValues.simulationName} />
          </li>
          <li>
            <label>Description</label>
            <textarea rows="5" cols="47" ref={(simulationDesc)=>{this.simulationDesc = simulationDesc}} defaultValue={this.props.fieldValues.simulationDesc} />
          </li>
          <li className="form-footer">
            <button className="btn -primary pull-right" onClick={this.nextStep.bind(this)}>Save &amp; Continue</button>
          </li>
        </ul>
      </div>
    );
  }

  nextStep(ev) {
    try {
      var data = {
        simulationName: this.simulationName.value,
        simulationDesc: this.simulationDesc.value,
      }
    }catch(e){
      console.log("Error "+e)
    }
      this.props.actions.incrementStep();
      this.props.actions.saveSimulationFormData(data);
  }
}

function mapStateToProps(state) {
    return {
        step:state.registration.step,
        fieldValues:state.registration.fieldValues
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(SimulationForm);


