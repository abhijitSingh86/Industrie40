//import React  from "react";
//var ReactDom  = require("react-dom");

class Main extends React.Component{
  constructor(props){
    super(props);
    this.operations = [{name:"Abi",id:1},{name:"gh",id:2},{name:"jamun",id:3}];

    this.handleSaves  =  this.handleSaves.bind(this);

  }

  handleSaves(obj){
    console.log("save obj count:-"+obj.opCount);
    console.log("save obj op Seq:-"+obj.opSeq);
    this.setState({
      component:obj
    })

  }

  render(){
    return (
      <div>
        <Input ops={this.operations} save={this.handleSaves} />
        <div></div>
      </div>
    );
  }
}






























ReactDOM.render(<Main/>,document.getElementById("root"));
