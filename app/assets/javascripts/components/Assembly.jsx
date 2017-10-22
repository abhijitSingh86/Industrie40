var React = require('react')
var AssemblyOperationInput =require("./AssemblyOperationInput")
var CustomAssemblyTable = require("./CustomAssemblyTable");
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/registrationaction';



var Assembly = React.createClass({

  getInitialState(){
    return {
      localAssemblyCounter : this.props.fieldValues.assemblyCounter,
      assemblyArr:this.props.fieldValues.assemblies,
      operationDetails:[],
        isFailed:true
    }
  },
  handleOpCountInputChange(e){
    var value = e.target.value;
    if(!isNaN(value) && value != ""){
      var count = (parseInt(value));
      this.inputOperationCount = count;

      var arr=[];
      for(var i=0;i<count;i++){
        arr.push({
          time:10,
          id:this.props.fieldValues.operations[i%this.props.fieldValues.operations.length].id
        });
      }
      this.setState({
        opCount:count,
        operationDetails:arr
      })
    }else{
      this.setState({
        opCount:0,
      })
    }

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
    handleOperationSelectChange(focusArr){
        this.setState({
          operationDetails:focusArr
        })
    },
    handleEditCall(id){
      this.setState({
        operationDetails:[],
        opCount:0
      });
      var arr = this.state.assemblyArr;
      var removedNode = this.remove(arr,id);
      this.assemblyName.value =  removedNode.name;
      this.fcount.value = removedNode.fcount;
      this.ftime.value = removedNode.ftime;
      this.operationCount.value = removedNode.operationDetails.length;
      this.setState({
        assemblyArr:arr,
        opCount:removedNode.operationDetails.length,
        operationDetails:removedNode.operationDetails

      });
      console.log(removedNode);
    },

   failpanel(){
      return (<div>
            <li>
              <label>Failure Count</label>
              <input type="text" ref={(fcount)=>{this.fcount= fcount}}  />
            </li>
            <li>
              <label>Total Failure Time</label>
              <input type="text" ref={(ftime)=>{this.ftime= ftime}}  />
            </li>
          </div>
          );
    },
    render: function() {
      var inputRow = [];
      console.log("opCount"+this.state.opCount+"opndetails"+this.state.operationDetails.length);
        if (this.state.opCount > 0) {
          inputRow.push(<AssemblyOperationInput fieldValues={this.props.fieldValues}
            focusArr={this.state.operationDetails} count={this.state.opCount} saveHandler={this.handleOperationSelectChange} />
          );}
          var failureEntryPanel = "";
        console.log(this.state.isFailed)
          if(this.state.isFailed){
          failureEntryPanel =this.failpanel();
          }

    return (
      <div>
        <h2>Assembly Details</h2>
        <ul className="form-fields">
          <li>
            <label>Name</label>
            <input type="text" ref={(assemblyName)=>{this.assemblyName= assemblyName}}  />
          </li>
          <li>
            <label>Is Assembly Fail Allowed</label>
            <input type="checkbox" defaultChecked onChange={(isFailed)=>{this.setState({isFailed:isFailed.target.checked})}} />
          </li>
            {failureEntryPanel}

          <li>
            <label>Operation Count</label>
            <input type="text" ref={(o)=> this.operationCount=o} onChange={this.handleOpCountInputChange}/>
          </li>
          <li className="li">{inputRow}</li>
          <li className="form-footer">
            <button className="btn -default pull-left" onClick={this.add}>Add</button>
            <button className="btn -default pull-center" onClick={this.props.actions.decrementStep}>Back</button>
            <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>

          </li>
        </ul>
      <CustomAssemblyTable data={this.state.assemblyArr} editrow = {this.handleEditCall}/>

      </div>
    );
  },
  add(){
   var od = this.state.operationDetails
      var tempArr = []
      for(var i=0;i< od.length; i++) {
        var propOp =  this.props.fieldValues.operations;
          for(var j=0;j<propOp.length; j++) {
          if(od[i].id == propOp[j].id) {
            tempArr.push({
                time:od[i].time,
                id:propOp[j].id,
                label:propOp[j].label
            });
            }
          }
          }

          var fcount = 0 ;
          var ftime=0
        if(this.state.isFailed){
        fcount=  parseInt(this.fcount.value);
            ftime = parseInt(this.ftime.value);
        }
    var data = {
      id:this.state.localAssemblyCounter,
      name:this.assemblyName.value,
        ifFailAllowed:this.state.isFailed,
        fcount:fcount,
        ftime:ftime,
      operationDetails:tempArr
    }
    var arr=this.state.assemblyArr
    arr.push(data);
    var localAssemblyCounter = this.state.localAssemblyCounter+1;

    this.props.actions.saveAssemblyFormData({
      assemblies: arr,
      assemblyCounter:localAssemblyCounter
    });

    this.setState({
      assemblyArr: arr,
      opCount:0,
      localAssemblyCounter:localAssemblyCounter
    });

      if(this.state.isFailed) {
          this.fcount.value = "";
          this.ftime.value = "";
      }
    this.operationCount.value="";
    this.assemblyName.value = "";
  },
  nextStep: function() {
    var data = {
      assemblies : this.state.assemblyArr
      ,assemblyCounter :this.state.localAssemblyCounter
    }
    this.props.actions.saveAssemblyFormData(data);
    this.props.actions.incrementStep();
  }
})


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
export default connect(mapStateToProps,mapDispatchToProps)(Assembly);