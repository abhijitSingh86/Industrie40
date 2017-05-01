
var React = require('react');


class CustomSelect extends React.Component{

  componentWillMount(){
    this.props.saveHandler(this.props.id,this.props.focusId);
  }
  constructor(props){
    super(props);
    this.state = {
      value:this.props.focusId
    }
    this.handleChange = this.handleChange.bind(this);
  }
  handleChange(e) {
    this.props.saveHandler(this.props.id,e.target.value);
    this.setState({
      value:e.target.value
    });
  }
  render(){
    var rows=[];
    this.props.fieldValues.operations.forEach(function(rowk) {
      rows.push(<option value={rowk.id} >{rowk.label}</option>);
    });

    return (
      <select value={this.state.value} onChange = {this.handleChange}>
        {rows}
      </select>
    );
  }
}

module.exports = CustomSelect