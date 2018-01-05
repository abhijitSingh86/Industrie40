/**
 * Created by billa on 17/04/17.
 */

var React =require("react")


class CustomRow extends React.Component {

  constructor(props){
    super(props)
    this.ed = this.ed.bind(this)
  }

  render() {
    var label = this.props.row.label;
    let fun = this.ed.bind(this,this.props.row.id);
    return (
      <tr >
        <td>{label}</td>
        <td>{this.props.row.id}</td>
        <td>
          <button onClick={fun} value={this.props.row.id}>Edit </button>
        </td>
      </tr>
    );
  }

  ed(e) {
    console.log(e);
    this.props.editrow(e)
  }
}

// onClick={this.props.editrow(this.props.row.id)}
class CustomTable extends React.Component {
  render() {
    var rows = [];
    var callback = this.props.editrow;
    this.props.data.forEach(function(eachData) {
      rows.push(<CustomRow row={eachData} key={eachData.id+eachData.label} editrow={callback} />);
    });
    return (
      <table className="container">
        <thead>
        <tr>
          <th>Label</th>
          <th>ID</th>
            <th></th>
        </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  }
}

module.exports =  CustomTable