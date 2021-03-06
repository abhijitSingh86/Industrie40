var React = require('react')
var SelectDiv = require('./SelectDiv')
var CustomComponentTable = require('./CustomComponentTable')
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions/index';

class Components extends React.Component{

  constructor(props){
      super(props);
    this.localComponentCounter = this.props.fieldValues.componentCounter;
    this.state = {
      componentArr:this.props.fieldValues.components,
        opSeqArr:[],
        rowCount:0
    }
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

  edit(id){

    var arr = this.state.componentArr;
    var removedNode = this.remove(arr,id);
    this.componentName.value =  removedNode.name;
    this.inputOperationCount.value = removedNode.opCount
    this.setState({
      componentArr:arr,
      opSeqArr:removedNode.operationDetails,
      opCount:removedNode.opCount,
      rowCount:removedNode.operationDetails.length

    });
  }


  handleInputChange(e){
      var value = e.target.value;
    if(!isNaN(value) && value != ""){
      var count = (parseInt(value));
      this.inputOperationCount.value = value;

      var opSeqArr=[];
      var defaultSelectArr=[];
      //load default changed list from past.. Code can be modified to include all the previous op seq with only the changed value.
        // Will add only if needed after confirmation
      if(this.state.opSeqArr.length >0){

          for (var i = 0; i < count; i++) {
              defaultSelectArr[i] = this.state.opSeqArr[0][i % this.state.opSeqArr.length];
          }

      }else{

          for (var i = 0; i < count; i++) {
              defaultSelectArr[i] = this.props.fieldValues.operations[i % this.props.fieldValues.operations.length];
          }
      }
      opSeqArr[0]=defaultSelectArr;

      this.setState({
        rowCount:1,
        opCount:count,
        opSeqArr:opSeqArr
      })
    }else{
      this.setState({
        opCount:0,
        rowCount:0,
          opSeqArr:[]
      })
    }

  }
  handleSelectChange(id,opSeq){
      console.log("Inside handle select change");
      console.log(id);
      console.log(opSeq);
    var arr=this.state.opSeqArr;
    arr[id]=opSeq;
    this.setState({
      opSeqArr:arr
    })
  }


    removeRow(id){
      console.log("remove row came for id "+id);
        var arr=this.state.opSeqArr;
        //immutable array slicing
        arr =[].concat(arr.slice(0,id),arr.slice(id+1));
        this.setState({
            opSeqArr:arr
        });
    }

  render() {
        console.log("Render called for component");
    //populate Select Div Instances
    var rows = [];
      var display = this.state.rowCount>0 ?'block':'none';

      for(var i=0;i<this.state.opSeqArr.length;i++)
        rows.push(<SelectDiv fieldValues={this.props.fieldValues}
                             index={i}
                             selectArr={this.state.opSeqArr[i]}
                             propagateSelectChange = {this.handleSelectChange.bind(this)}
                             style={{display}}
                             removeRow={this.removeRow.bind(this)}/>);


    return (
      <div>
        <h2>Component Details</h2>
        <ul className="form-fields">
          <li>
            <label>Name</label>
            <input type="text" ref={(componentName)=>{this.componentName= componentName}}  />
          </li>
          <li>
            <label>Operation Count</label>
            <input type="text" ref={(o)=>{this.inputOperationCount=o}} onChange = {this.handleInputChange.bind(this)} />
          </li>
          <li>
            <label>Component Count</label>
            <input type="text" ref={(o)=>{this.inputComponentCount=o}} onChange = {this.handleComponentCOuntInputChange.bind(this)} />
          </li>
          {rows}

          <li>
            <label>Custom Sequence Json</label>
            <input type="text" ref={(o)=>{this.inputCustomSequence=o}} onBlur= {this.handleInputCustomSequenceJson.bind(this)} />
          </li>

          <button onClick={this.handleOperationsSequenceAddClick.bind(this)} style={{display}}>Add</button>

          <li className="form-footer">
            <button className="btn -default pull-left" onClick={this.add.bind(this)}>Add</button>
            <button className="btn -default pull-center" onClick={this.props.actions.decrementStep}>Back</button>
            <button className="btn -primary pull-right" onClick={this.nextStep.bind(this)}>Save &amp; Continue</button>

          </li>
        </ul>

        <CustomComponentTable data={this.state.componentArr} editrow={this.edit.bind(this)}/>

      </div>
    )
  }

  addOperation(label,id){
        var aArr = this.props.fieldValues.operations;

      aArr.push(
          {
              label:label,
              id:id
          }
      );

      var data = {
          operations : aArr
          ,operationCounter :id+1
      };
      this.props.actions.saveOperationFormData(data);

  }
    handleInputCustomSequenceJson(e){
    var value = e.target.value;
    if(value !== ""){
        this.setState({
            rowCount:0,
            opCount:0,
            opSeqArr:[]
        });
        this.inputOperationCount.value = 0;
    }else{
      return;
    }

        var localOperationCounter =this.props.fieldValues.operationCounter;
        try{
            var json = JSON.parse(value)


            //setting the context
            var count =0
            if(json.length>0)
                count = json[0].length;

            var rowCount = json.length;

            this.inputOperationCount.value = count;


            var opSeqArr=[];
            for(var i=0;i<json.length;i++){
                  var defaultSelectArr=[];
                  for(var j=0;j<count;j++){
                    var ops = this.props.fieldValues.operations;
                    var flag=false;
                      for(var k=0;k<ops.length;k++) {

                        if(ops[k].label === json[i][j]) {
                            defaultSelectArr[j] = ops[k];
                            flag=true;
                            break;
                        }
                      }
                      if(!flag) {
                            var data =  {
                                label:json[i][j],
                                id:localOperationCounter
                            }

                          this.addOperation(json[i][j],localOperationCounter);
                            localOperationCounter +=1;
                            defaultSelectArr[j] = data;
                      }
                  }
                  opSeqArr[i]=defaultSelectArr;
              }
              console.log(opSeqArr);
            this.setState({
                rowCount:rowCount,
                opCount:count,
                opSeqArr:opSeqArr
            })
        }catch(e){
          console.log(e);
          console.log("into component.jsx");
          this.inputCustomSequence.value = "{'':'Please enter correct Json Value'}"
        }
    }
    handleComponentCOuntInputChange(e){
        var value = e.target.value;
        if(!isNaN(value) && value != ""){
            var count = (parseInt(value));
            this.inputComponentCount.value = value;
        }
  }
  handleOperationsSequenceAddClick(){
    var count =this.state.rowCount;
    if(count !=0){
      var arr = this.state.opSeqArr

      arr.push(arr[0].slice(0));

      this.setState({
        opSeqArr:arr,
        rowCount:count+1
      })
    }
  }
  add(){

      var arr = this.state.componentArr
      var cmpCount = 1
      if (!isNaN(this.inputComponentCount.value) && parseInt(this.inputComponentCount.value) > cmpCount){
          cmpCount = parseInt(this.inputComponentCount.value)
      }

      // console.log("--int print -- "+this.inputComponentCount)

    for(var i=0;i<cmpCount;i++){
        var data = {
            id:this.localComponentCounter,
            name:this.componentName.value+':'+i,
            opCount:this.state.opCount,
            operationDetails:this.state.opSeqArr
        }

        arr.push(data);
        this.localComponentCounter = this.localComponentCounter+1;

        // console.log("************************")
        // console.log(data)
        // console.log(this.localComponentCounter)
        // console.log("************************")
    }


    var data ={
             components: arr,
              componentCounter:this.localComponentCounter
      }
      this.props.actions.saveComponentFormData(data);
    this.setState({
      componentArr: arr,
      removedComponent:undefined,
      rowCount:0,
        opSeqArr:[]
    });


    this.componentName.value = "";
    this.inputOperationCount.value=0;
  }
  nextStep(){
    var data = {
      components : this.state.componentArr
      ,componentCounter :this.localComponentCounter
    }
    this.props.actions.saveComponentFormData(data);
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
export default connect(mapStateToProps,mapDispatchToProps)(Components);