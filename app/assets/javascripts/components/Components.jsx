var React = require('react')
var SelectDiv = require('./SelectDiv')
var CustomComponentTable = require('./CustomComponentTable')

var Components = React.createClass({

  getInitialState(){
    this.localComponentCounter = this.props.fieldValues.componentCounter;
    return {
      componentArr:this.props.fieldValues.components
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
    },


  handleInputChange(e){
      var value = e.target.value;
    if(!isNaN(value) && value != ""){
      var count = (parseInt(value));
      this.inputOperationCount = value;

      var opSeqArr=[];
      var defaultSelectArr=[];
      for(var i=0;i<count;i++){
        defaultSelectArr[i] = i % this.props.fieldValues.operations.length;
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
        rowCount:0
      })
    }

  },
  handleSelectChange(id,opSeq){
    var arr=this.state.opSeqArr;
    arr[id]=opSeq;
    this.setState({
      opSeqArr:arr
    })
  },


  render: function() {
    //populate Select Div Instances
    var rows = [];
    if(this.state.opCount > 0){
      var display = this.state.rowCount>0 ?'block':'none';

      for(var i=0;i<this.state.rowCount;i++)
        rows.push(<SelectDiv fieldValues={this.props.fieldValues}
                             count={this.state.opCount}
                             id={i}
                             selectArr={this.state.opSeqArr[i]}
                             propagateSelectChange = {this.handleSelectChange}
                             style={{display}} />);
    }


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
            <input type="text" ref={(o)=>{this.inputOperationCount=o}} onChange = {this.handleInputChange} />
          </li>
          {rows}
          <button onClick={this.handleOperationsSequenceAddClick} style={{display}}>Add</button>

          <li className="form-footer">
            <button className="btn -default pull-left" onClick={this.add}>Add</button>
            <button className="btn -default pull-center" onClick={this.props.previousStep}>Back</button>
            <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>

          </li>
        </ul>

        <CustomComponentTable data={this.state.componentArr} editrow={this.edit}/>

      </div>
    )
  },
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
  },
  add(){
    var data = {
      id:this.localComponentCounter,
      name:this.componentName.value,
      opCount:this.state.opCount,
      operationDetails:this.state.opSeqArr
    }
    var arr=this.state.componentArr
    arr.push(data);
    this.localComponentCounter = this.localComponentCounter+1;

    this.props.saveValues({
      components: arr,
      componentCounter:this.localComponentCounter
    });

    this.setState({
      componentArr: arr,
      removedComponent:undefined,
      rowCount:0
    });


    this.componentName.value = "";
    this.inputOperationCount.value=0;
  },
  nextStep: function() {
    var data = {
      components : this.state.componentArr
      ,componentCounter :this.localComponentCounter
    }
    this.props.saveValues(data)
    this.props.nextStep()
  }
})

module.exports = Components