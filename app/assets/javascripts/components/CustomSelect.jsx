
var React = require('react');


class CustomSelect extends React.Component{

  constructor(props){
    super(props);
    this.handleChange = this.handleChange.bind(this);
      this.state = {
          value:this.props.focusId
      };
  }
  handleChange(e) {
    this.props.saveHandler(this.props.index,e.target.value);
    this.setState({
      value:e.target.value
    });

    console.log("in handle change custom select");
    console.log(e);
  }
  render(){
    var rows=[];
    this.props.fieldValues.operations.forEach(function(rowk) {
      rows.push(<option value={rowk.id} >{rowk.label}</option>);
    });

    return (
      <select value={this.props.focusId} onChange = {this.handleChange}>
        {rows}
      </select>
    );
  }
}

module.exports = CustomSelect