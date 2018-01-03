var React                   = require('react')
var CustomTable  = require('./CustomTable')
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/index';


class OperationForm extends React.Component{


  componentWillMount(){
    this.localOperationCounter = this.props.fieldValues.operationCounter;
  }
  componentWillReceiveProps(nextProps){
    this.localOperationCounter=nextProps.fieldValues.operationCounter;
  }
  constructor(props){
    super(props);
    this.localOperationCounter=this.props.fieldValues.operationCounter;
    this.state = {
    operationArr : this.props.fieldValues.operations
    };
  }
  render() {
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
            <button className="btn -default pull-left" onClick={this.add.bind(this)}>Add</button>
            <button className="btn -default pull-center" onClick={this.props.actions.decrementStep}>Back</button>
            <button className="btn -primary pull-right" onClick={this.nextStep.bind(this)}>Save &amp; Continue</button>
          </li>
        </ul>
        <CustomTable data={this.props.fieldValues.operations} editrow={this.edit.bind(this)}/>

      </div>
    )
  }
  edit(c){
      var arr = this.state.operationArr;
      var removedoperation = this.remove(arr,c);
    this.operationName.value = removedoperation.label;
      this.setState({
        operationArr:arr
      })  ;
  }
  remove(arr, id) {
    for(var i = arr.length; i--;) {
      if(arr[i].id === id) {
        var val = arr[i];
        arr.splice(i, 1);
        return val;
      }
    }
  }

  add(){
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
    this.props.actions.saveOperationFormData(data);

  }
  nextStep() {
    var data = {
      operations : this.state.operationArr
      ,operationCounter :this.localOperationCounter
    };
    this.props.actions.saveOperationFormData(data);
    this.props.actions.incrementStep();
  }
}



function mapStateToProps(state) {
    return {
        fieldValues:state.registration.fieldValues
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}
export default connect(mapStateToProps,mapDispatchToProps)(OperationForm);