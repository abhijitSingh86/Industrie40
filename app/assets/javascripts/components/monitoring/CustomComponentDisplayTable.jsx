/**
 * Created by billa on 17/04/17.
 */

var React =require("react")


class CustomComponentDisplayTable extends React.Component {

  constructor(props){
    super(props)
    // console.log(this.props.row)
    this.ed = this.ed.bind(this)
    this.colFun = this.colFun.bind(this)
  }

  colFun(arr){
    var out = [];
    for(var i=0;i<arr.length;i++){
      out.push(<td>{arr[i].label}</td>)
    }
    // console.log(out);
    return out;
  };
  render() {
    var label = this.props.row.name;

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
    <table className="tablecontainer">
      <thead>
      <tr>
        <th>Name</th>
        <th>Operation Details</th>
      </tr>
      </thead>
      <tbody><tr >
        <td>{label}</td>
        <td>
          <table>
            <tbody>
            {otable}
            </tbody>
          </table>
        </td>

      </tr>
      </tbody>
    </table>
          );
  }


}

// class CustomComponentDisplayTable extends React.Component {
//   render() {
//     var rows = [];
//     this.props.data.forEach(function(eachData) {
//       rows.push(<CustomComponentRow row={eachData} key={eachData.id+eachData.name}  />);
//     });
//     return (

//     );
//   }
// }

module.exports =  CustomComponentDisplayTable