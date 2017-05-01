var React                   = require('react')
var getRadioOrCheckboxValue = require('../lib/radiobox-value')
var CustomTable  = require('./CustomTable')



var OperationForm = React.createClass({


  componentWillMount(){
    this.localOperationCounter = this.props.fieldValues.operationCounter;
  },
  componentWillReceiveProps(nextProps){
    this.localOperationCounter=nextProps.fieldValues.operationCounter;
  },
getInitialState:function(){
  this.localOperationCounter=this.props.fieldValues.operationCounter;

  return {
    operationArr : this.props.fieldValues.operations
  };
},
  render: function() {
    return (
      <div>
        <h2>Operation Details</h2>
        <ul className="form-fields">
          <li>
            <label>Name</label>
            <input type="text" ref={(operationName)=>{this.operationName= operationName;}}
                   />
          </li>
          <li className="form-footer">
            <button className="btn -default pull-left" onClick={this.add}>Add</button>
            <button className="btn -default pull-center" onClick={this.props.previousStep}>Back</button>
            <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>
          </li>
        </ul>
        <CustomTable data={this.state.operationArr} editrow={this.edit}/>

      </div>
    )
  },
  edit:function(c){
      var arr = this.state.operationArr;
      var removedoperation = this.remove(arr,c);
      // console.log(removedoperation.label)
    this.operationName.value = removedoperation.label;
      this.setState({
        operationArr:arr
      })  ;
  },
  remove(arr, id) {
    for(var i = arr.length; i--;) {
      if(arr[i].id === id) {
        var val = arr[i];
        arr.splice(i, 1);
        return val;
      }
    }
  },

  add:function(){
    var aArr = this.state.operationArr;
    aArr.push(
        {
          label:this.operationName.value,
          id:this.localOperationCounter
        }
      );

    this.localOperationCounter =this.localOperationCounter+1;
    this.setState(
        {
          operationArr : aArr,
          localOperationCounter:this.localOperationCounter
        }
      );

    this.operationName.value = "";
    this.operationName.focus();
    var data = {
      operations : this.state.operationArr
      ,operationCounter :this.localOperationCounter
    };
    this.props.saveValues(data);

  },
  nextStep: function() {
    var data = {
      operations : this.state.operationArr
      ,operationCounter :this.localOperationCounter
    };
    this.props.saveValues(data);
    this.props.nextStep();
  }
})

module.exports = OperationForm