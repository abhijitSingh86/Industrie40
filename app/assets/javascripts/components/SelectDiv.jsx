var React = require('react');
var CustomSelect = require('./CustomSelect')


class SelectDiv extends React.Component{
  constructor(props){
    super(props);
    console.log(this.props.selectArr);
    this.state = {
      valueArr:this.props.selectArr
    };
    this.handleValueChange = this.handleValueChange.bind(this);
  }

  handleValueChange(id,valueId){
      for(var i=0;i<this.props.fieldValues.operations.length;i++){
          var op = this.props.fieldValues.operations[i]
          console.log(op.id == valueId+""+op.id+""+valueId)
          if(op.id == valueId){
              var varr = this.state.valueArr;
              varr[id]=op;
              console.log("modified value arr")
              console.log(varr)
              this.setState({
                  valueArr:varr
              });

          }
      }

      console.log("state value arr")
      console.log(this.state.valueArr)
    this.props.propagateSelectChange(this.props.id,this.state.valueArr);
  }

  render(){
      var rows = [];
      for(var i=0;i<this.props.count;i++){
        rows.push(<CustomSelect fieldValues={this.props.fieldValues} focusId={this.state.valueArr[i].id} id={i}
                                saveHandler = {this.handleValueChange}/>);
      }

      return (
        <div>
          Select Operation Sequence
          {rows}
        </div>
      );

  }
}



module.exports = SelectDiv