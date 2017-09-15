var React = require('react')
var axios = require('axios')



var Success = React.createClass({
    getInitialState(){
            return {
                renderSuccess:false
                ,body :""
            }
    },
  submit(){
      var _this = this;
      axios.post('/simulation' , this.props.fieldValues).then( function(response){
          console.log( "entered into success")
          _this.setState({
              body:JSON.stringify(response.data.body),
              renderSuccess: true
              });
          console.log( "entered into after success"+_this.state.renderSuccess);

      }).catch(function(error){
          console.log(error)
      })
    console.log("Saving Details Using Ajax Call");
  },
  render: function() {
    return (
        <div>
            <ToBeSubmittedValue render={!this.state.renderSuccess} fieldValues={this.props.fieldValues} previousStep = {this.props.previousStep} submit={this.submit} />
            <SubmittedValue render={this.state.renderSuccess} content={this.state.body} changeHandler={this.props.changeHandler}/>
        </div>
    )
  }
})

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
        var content = JSON.parse(this.props.content);
        console.log("calling change handler to display monintor mode"+content.s)
        this.props.changeHandler(content.s,'start')
    }

    render(){
         if(this.props.render) {
             var content = JSON.parse(this.props.content);
		var counter=3;
             var arr = [];
             for (var i = 0; i < content.c.length; i++) {
                 arr.push( <div>xterm -hold -e  scala -classpath "*.jar" componentClient.jar -c {content.c[i]} -s {content.s} &</div> );
		 arr.push( <div>sleep {counter}</div>);
			// counter =counter + .5;
             }
             for (var i = 0; i < content.a.length; i++) {
                 arr.push( <div>xterm -hold -e scala -classpath "*.jar" assemblyClient.jar -a {content.a[i]} -s {content.s} &</div> );
		arr.push( <div>sleep {counter}</div>);
		// counter =counter + .5;
             }
             return (

                 <div>
                     <p>Simulation Details stored Successfully</p>
                     <p>Copy the content below in sh file.</p>
                     {arr}

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

module.exports = Success
