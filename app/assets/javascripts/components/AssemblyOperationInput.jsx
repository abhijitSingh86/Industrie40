 var React = require('react');

 var CustomSelect = require('./CustomSelect');

class AssemblyOperationInput extends React.Component{




  constructor(props){
    super(props);

    this.handleCustomSelectChange = this.handleCustomSelectChange.bind(this);
    this.handleInputTimeChange= this.handleInputTimeChange.bind(this);
  }

  handleInputTimeChange(e){
    var value = e.target.value;
    if(!isNaN(value) && value != ""){
    var arr = this.props.focusArr;
    arr[e.target.id].time = e.target.value;
    this.props.saveHandler(arr);
    }else{
      e.target.value="";
    }
  }

  handleCustomSelectChange(id,valueId){
    if(id != undefined) {
      var arr = this.props.focusArr;
      arr[id].opid = valueId;
      this.props.saveHandler(arr);
    }
  }

  render(){

    console.log("logging AssemblyOperation Input");
    console.log(this.props.focusArr);
    var row1=[];
    var row2=[];
    for(var i=0;i<this.props.count;i++){
          row1.push(<td><CustomSelect fieldValues={this.props.fieldValues} id={i} focusId={this.props.focusArr[i].opid} saveHandler={this.handleCustomSelectChange} /></td>);
          row2.push(<td><input type="text"  id={i} onChange={this.handleInputTimeChange} value={this.props.focusArr[i].time}/></td>);
      }

      return (
        <div>
          <table>
            <tbody>
            <tr>
              {row1}
            </tr>
            <tr>
              {row2}
            </tr>
            </tbody>
          </table>
        </div>
      )
  }

}

module.exports = AssemblyOperationInput
