var React = require('react')
var axios = require('axios')
var Success = React.createClass({
  submit(){
      axios.post('/simulation' , this.props.fieldValues).then(function(response){
          console.log(response)
      }).catch(function(error){
          console.log(error)
      })
    console.log("Saving Details Using Ajax Call");
  },
  render: function() {
    return (
      <div>
        <h2>Data Entered Successfully</h2>
        <p>Please Check and Submit </p>

        <div><pre>{JSON.stringify(this.props.fieldValues, null, 2) }</pre></div>
        <button className="btn -default pull-left" onClick={this.props.previousStep}>Back</button>

        <button className="btn -default pull-center" onClick={this.submit}>Submit</button>

      </div>
    )
  }
})

module.exports = Success