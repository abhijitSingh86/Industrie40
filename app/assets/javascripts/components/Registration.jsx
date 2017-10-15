

import React from 'react';
import SimulationForm from './SimulationForm'
import Components  from  './Components'
import Assembly from './Assembly'
import Success       from './Success'
import OperationForm  from './OperationForm'

import TransportTime from './TransportTime'


// Idealy, these form values would be saved in another
// sort of persistence, like a Store via Flux pattern


var Registration = React.createClass({
  getInitialState: function() {
    return {
      step : 0
    }
  },

  saveValues: function(field_value) {
    this.props.saveValues(field_value);
  },

  nextStep: function() {
    this.setState({
      step : this.state.step + 1
    })
  },

  previousStep: function() {
    this.setState({
      step : this.state.step - 1
    })
  },

  submitRegistration: function() {
    // Handle via ajax submitting the user data, upon
    // success return this.nextStop(). If it fails,
    // show the user the error but don't advance

    this.nextStep()
  },

  showStep: function() {
    switch (this.state.step) {
      case 0:
        return <SimulationForm fieldValues={this.props.fieldValues}
                              nextStep={this.nextStep}
                              previousStep={this.previousStep}
                              saveValues={this.saveValues} />
      case 1:
        return <OperationForm fieldValues={this.props.fieldValues}
                             nextStep={this.nextStep}
                             previousStep={this.previousStep}
                             saveValues={this.saveValues} />
      case 2:
        return <Components fieldValues={this.props.fieldValues}
                             previousStep={this.previousStep}
                             saveValues={this.saveValues}
                           nextStep={this.nextStep}/>
      case 3:
        return <Assembly fieldValues={this.props.fieldValues}
                         previousStep={this.previousStep}
                         saveValues={this.saveValues}
                         nextStep={this.nextStep}/>
        case 4:
          return <TransportTime fieldValues={this.props.fieldValues}
                                nextStep={this.nextStep}
                                previousStep={this.previousStep}
                                saveValues={this.saveValues} />
        case 5:
        return <Success fieldValues={this.props.fieldValues}
                        previousStep={this.previousStep}
                        changeHandler={this.props.changeHandler}/>
    }
  },

  render: function() {
    var style = {
      width : (this.state.step / 5 * 100) + '%'
    }

    return (
      <div>
        <span className="progress-step">Step {this.state.step}</span>
        <progress className="progress" style={style}></progress>
        {this.showStep()}
      </div>
    )
  }
})

export default Registration