
import React from "react"
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/index';

class SimulationForm extends React.Component{
  constructor(props){
    super(props);
    this.nextStep = this.nextStep.bind(this);
    this.state = {name:"" , desc:""}
  }

    componentWillReceiveProps(nextProps){
    this.setState({
        name:nextProps.fieldValues.simulationName,
        desc:nextProps.fieldValues.simulationDesc
    });
    }

    handleChange(){
      this.setState({
          name:this.simulationName.value,
          desc:this.simulationDesc.value
      });
    }
  render() {
    return (
      <div>
        <h2>Simulation Details</h2>
        <ul className="form-fields">
          <li>
            <label>Name</label>
            <input type="text" ref={(simulationNameR)=>{this.simulationName = simulationNameR}}
                   value={this.state.name} onChange={this.handleChange.bind(this)}/>
          </li>
          <li>
            <label>Description</label>
            <textarea rows="5" cols="47" ref={(simulationDescR)=>{this.simulationDesc = simulationDescR}}
                      value={this.state.desc}/>
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


