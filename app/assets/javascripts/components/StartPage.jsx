var React = require('react')

class StartPage extends React.Component{

  constructor(props){
    super(props);

    this.handleFileChange = this.handleFileChange.bind(this);
    this.nextStep = this.nextStep.bind(this);
  }

  nextStep(){
    this.props.nextStep();
  }
  handleFileChange(e){
      var file = e.target.files[0];
      var reader = new FileReader();
      reader.onload = function(){
        var dataURL = reader.result;
        console.log(dataURL)
      };
      console.log(file);
      console.log(reader.readAsText(file));
  }

  render(){
    return (
      <div>
      <ul>
        <li>
        <input type="file" onChange={this.handleFileChange}/>
        </li>
        <li className="form-footer">
          <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>

        </li>
      </ul>

      </div>

    )
  }


}

module.exports = StartPage