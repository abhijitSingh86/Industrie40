/**
 * Created by billa on 06.04.17.
 */
const DataTable = React.createClass({
    getInitialState() {
        this._columns = [{
            key: 'id',
            name: 'ID',
            width: 80
        }, {
            key: 'Name',
            name: 'Name',
            editable: true
        }]
        return {
            rows: [{
                id: "1",
                Name: "sdfsdf"
            }]
        };
    },

    handleGridRowsUpdated({
        fromRow,
        toRow,
        updated
    }) {
        let rows = this.state.rows;

        for (let i = fromRow; i <= toRow; i++) {
            let rowToUpdate = rows[i];
            let updatedRow = React.addons.update(rowToUpdate, {
                $merge: updated
            });
            rows[i] = updatedRow;
        }

        this.setState({
            rows
        });
    },

    rowGetter(i) {
        return this.state.rows[i];
    },

    render() {
        return ( < ReactDataGrid enableCellSelect = {
            true
        }
        columns = {
            this._columns
    }
        rowGetter = {
            this.rowGetter
    }
        rowsCount = {
            this.state.rows.length
    }
        minHeight = {
            200
        }
        onGridRowsUpdated = {
            this.handleGridRowsUpdated
    }
    />);
    }
});

class Input extends React.Component {

    constructor(props) {
        super(props);
        //const minLen = parseInt(this.props.minLen) !=NaN ? parseInt(this.props.minLen)
        this.state = {
            value: ' '
        }
        this.handleChange = this.handleChange.bind(this);
        //this.handleBtnClick = this.handleBtnClick.bind(this);
        this.handleFocus = this.handleFocus.bind(this);
        this.handleBlur = this.handleBlur.bind(this);
    }

    handleFocus(e) {
        //console.log(e);
    }




    handleBlur(e) {
        if (e.target.value.length < parseInt(this.props.minLen)) {
            this.setState({
                error: true,
                errorMsg: "Minimum length not met"
            });
        } else {
            this.setState({
                error: false,
                errorMsg: ""
            });
        }
    }


    handleChange(e) {
        this.setState({
            value: e.target.value
        })
    }

    render() {
        return ( < div className = "form-group" >
            < label
        for = {
            this.props.name
            } > {
            this.props.label
        }: < /label> < input type = "text"
        ref="Name"
        className = "form-control"
        id = {
            this.props.name
    }
        placeholder = "Enter"
        value = {
            this.state.value
    }
        onChange = {
            this.handleChange
    }
        onFocus = {
            this.handleFocus
    }
        onBlur = {
            this.handleBlur
    }
    />
    <input type="button" value="Submit" onClick={this.handleBtnClick}></input>
        {
            this.state.error && < p > {
            this.state.errorMsg
    } < /p>
    } < /div>
    )
    }


}
class MainForm extends React.Component {
    constructor(props){
        super(props);
        this.handleBtnClick = this.handleBtnClick.bind(this);
        this.state = {
            rows:[]
        }
    }

    handleBtnClick(e){
        console.log(this.refs.Name.defaultValue);
        console.log(e);
    }



    render() {
        return ( < div >
            < Input minLen = "4"
        name = "Name"
        label = "Operation Name"
        onClick={this.handleBtnClick}/ >
    < DataTable / >
        < /div >
    );
    }
}




ReactDOM.render( < MainForm / > ,
    document.getElementById('container')
);
