var React = require('react')

var fileJson = {};
class StartPage extends React.Component {


    constructor(props) {
        super(props);
        console.log("Start page component construstor");
        this.handleFileChange = this.handleFileChange.bind(this);
        this.nextStep = this.nextStep.bind(this);

    }

    nextStep() {
        this.props.saveValues(fileJson);
        this.props.nextStep();
    }

    handleFileChange(e) {
        var file = e.target.files[0];
        var reader = new FileReader();
        reader.onload = function () {
            var dataURL = reader.result;
            fileJson = JSON.parse(dataURL)
            console.log(dataURL)
        };
        console.log(file);
        console.log(reader.readAsText(file));
    }

    render() {
        return (
            <div>
                <ul className="form-fields">
                    <li>
                        <label> Upload Json Data File</label>
                        <input type="file" onChange={this.handleFileChange}/>
                    </li>
                    <li>
                        <label>Select Existing Simulation to run(WILL COME NEXT)</label>

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