

import React from 'react';
import SimulationForm from './SimulationForm'
import Components  from  './Components'
import Assembly from './Assembly'
import Success       from './Success'
import OperationForm  from './OperationForm'
import assign        from 'object-assign'
import StartPage from './StartPage'
import TransportTime from './TransportTime'


// Idealy, these form values would be saved in another
// sort of persistence, like a Store via Flux pattern
var fieldValues = {
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

var Registration = React.createClass({
  getInitialState: function() {
    return {
      step : 0
    }
  },

  saveValues: function(field_value) {
    return function() {
      fieldValues = assign({}, fieldValues, field_value)
    }.bind(this)()
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
      case 0: return <StartPage
        fieldValues={fieldValues}
        nextStep={this.nextStep}
        saveValues={this.saveValues}
        changeHandler={this.props.changeHandler}/>

      case 1:
        return <SimulationForm fieldValues={fieldValues}
                              nextStep={this.nextStep}
                              previousStep={this.previousStep}
                              saveValues={this.saveValues} />
      case 2:
        return <OperationForm fieldValues={fieldValues}
                             nextStep={this.nextStep}
                             previousStep={this.previousStep}
                             saveValues={this.saveValues} />
      case 3:
        return <Components fieldValues={fieldValues}
                             previousStep={this.previousStep}
                             saveValues={this.saveValues}
                           nextStep={this.nextStep}/>
      case 4:
        return <Assembly fieldValues={fieldValues}
                         previousStep={this.previousStep}
                         saveValues={this.saveValues}
                         nextStep={this.nextStep}/>
        case 5:
          return <TransportTime fieldValues={fieldValues}
                                nextStep={this.nextStep}
                                previousStep={this.previousStep}
                                saveValues={this.saveValues} />
        case 6:
        return <Success fieldValues={fieldValues}
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