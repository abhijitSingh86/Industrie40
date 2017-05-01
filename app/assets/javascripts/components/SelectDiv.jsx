var React = require('react');
var CustomSelect = require('./CustomSelect')


class SelectDiv extends React.Component{
  constructor(props){
    super(props);

    console.log(props);
    this.state = {
      valueArr:this.props.selectArr
    };
    this.handleValueChange = this.handleValueChange.bind(this);
  }

  handleValueChange(id,valueId){
    this.state.valueArr[id]=valueId;
    this.props.propagateSelectChange(this.props.id,this.state.valueArr);
  }

  render(){
      var rows = [];
      for(var i=0;i<this.props.count;i++){
        rows.push(<CustomSelect fieldValues={this.props.fieldValues} focusId={this.state.valueArr[i]} id={i} saveHandler = {this.handleValueChange}/>);
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