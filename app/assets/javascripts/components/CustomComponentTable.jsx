/**
 * Created by billa on 17/04/17.
 */

var React =require("react")


class CustomComponentRow extends React.Component {

  constructor(props){
    super(props)
    console.log(this.props.row)
    this.ed = this.ed.bind(this)
    this.colFun = this.colFun.bind(this)
  }

  colFun(arr){
    var out = [];
    for(var i=0;i<arr.length;i++){
      out.push(<td>{arr[i]}</td>)
    }
    console.log(arr);
    return out;
  };
  render() {
    var label = this.props.row.name;
    let fun = this.ed.bind(this,this.props.row.id);

    var opd = this.props.row.operationDetails;
    var otable =[]




    for(var i=0;i<opd.length;i++){
      otable.push(
        <tr>
          <td>S{i}</td>
          {
            this.colFun(opd[i])
          }
        </tr>
      )
    }

    return (
      <tr >
        <td>{label}</td>
        <td>
          <table>
            <tbody>
            {otable}
            </tbody>
          </table>
        </td>
        <td>
          <button onClick={fun} value={this.props.row.id}>Edit </button>
        </td>
      </tr>
    );
  }

  ed(e) {
    this.props.editrow(e)
  }
}

class CustomComponentTable extends React.Component {
  render() {
    var rows = [];
    var callback = this.props.editrow;
    this.props.data.forEach(function(eachData) {
      rows.push(<CustomComponentRow row={eachData} key={eachData.id+eachData.name} editrow={callback} />);
    });
    return (
      <table class="container">
        <thead>
        <tr>
          <th>Name</th>
          <th>Operation Details</th>
        </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  }
}

module.exports =  CustomComponentTable