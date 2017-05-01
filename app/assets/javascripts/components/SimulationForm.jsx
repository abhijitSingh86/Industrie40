var React = require('react')

var SimulationForm = React.createClass({


  render: function() {
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
            <textarea rows="5" cols="63" ref={(simulationDesc)=>{this.simulationDesc = simulationDesc}} defaultValue={this.props.fieldValues.simulationDesc} />
          </li>
          <li className="form-footer">
            <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>
          </li>
        </ul>
      </div>
    )
  },

  nextStep: function(e) {
    e.preventDefault()

    // Get values via this.refs
    try {
      var data = {
        simulationName: this.simulationName.value,
        simulationDesc: this.simulationDesc.value,
      }
    }catch(e){
      console.log("Error "+e)
    }


    this.props.saveValues(data)
    this.props.nextStep()
  }
})

module.exports = SimulationForm