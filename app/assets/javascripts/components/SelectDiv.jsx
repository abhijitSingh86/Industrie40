var React = require('react');
var CustomSelect = require('./CustomSelect')


class SelectDiv extends React.Component{
  constructor(props){
    super(props);
    this.handleValueChange = this.handleValueChange.bind(this);
  }

  handleValueChange(id,valueId){
      console.log("handle Value change in select div");
      console.log(id);
      console.log(valueId);
      for(var i=0;i<this.props.fieldValues.operations.length;i++){

          var op = this.props.fieldValues.operations[i];
          console.log(op);
          console.log("In value change ::"+(valueId == op.id  )+" :: "+op.id+" :: "+valueId);
          if(valueId == op.id){
              var varr = this.props.selectArr;
              varr[id]=op;
              this.props.propagateSelectChange(this.props.index,varr);
                break;
          }
      }
  }

    removeRow(){
      this.props.removeRow(this.props.index);
    }
  render(){

        var count=0;
      var r=  this.props.selectArr.map(function (id) {
          return <CustomSelect fieldValues={this.props.fieldValues} focusId={id.id} index={count++}
                               saveHandler = {this.handleValueChange}/>

      },this);

      if(this.props.selectArr.length > 0){
          r.push(<img src="/assets/images/delete1.ico"  width={30} height={30} onClick={this.removeRow.bind(this)}/>);
      }

      return (
        <div>
          Select Operation Sequence
          {r}
        </div>
      );

  }
}



module.exports = SelectDiv