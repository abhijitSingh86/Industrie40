import React from 'react';
import {Table ,Panel,Accordion} from 'react-bootstrap';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as Actions from '../redux/actions';

const TableSkelton = (body)=>{
    return (
        <table>
            <tbody>
            {body}
            </tbody>
        </table>
    )
}

const tabularData = (row , name)=>{
    return  (
        <tr>
            <td>
                {row.componentid}
            </td>
            <td>
                {name}
            </td>
            <td>
                {new Date(row.startTime).toLocaleString()}
            </td>
            <td>
                {new Date(row.endTime).toLocaleString()}
            </td>

        </tr>
    );
}

const OperationRow = (operation,assemblyObj,assemblyid )=>{

    var header = `${operation.name} `;
    var         data ='No Processing Details Found';
    // console.log("operation Row object");
    // console.log(assemblyObj);
    if(assemblyObj.length == 1){
        var opObj = assemblyObj[0].operations.filter(x=>x.op_id == operation.id)[0];

        data =opObj.currentOpDetails.past.length>0 ? TableSkelton(
            opObj.currentOpDetails.past.map(x=> tabularData(x.row, x.cmp_name)) ): data;


        if(opObj.currentOpDetails.current.isPresent === true){
            header = header+ ` CurrentComponent : ${opObj.currentOpDetails.current.cmp_name}`;
        }

    }

    // console.log(header);
    // console.log(data);
    return (
            <Panel header={header} eventKey={assemblyid}  >
                {data}
            </Panel>
    );
}

class AssemblyAccordinPanel extends React.Component{

    constructor(props){
        super(props);
    }



    render(){

        var id = this.props.data.id;
        var updatedAssemblyObj = this.props.assemblies.filter(x=> x.id === id);
        // console.log("assembly accordin");
        // console.log(updatedAssemblyObj);
        var operationRows = this.props.data.operationDetails.map(x=>OperationRow(x,updatedAssemblyObj,this.props.data.id));

        return (
            <Table>
                <tbody>
                <tr>
                    <td>
                        AssemblyName
                    </td>
                    <td>
                        {this.props.data.name}
                    </td>
                </tr>
                <tr>
                    <td colSpan="2">
                        Assembly Operations
                    </td>

                </tr>
                <tr>
                    <td colSpan="2">
                        <Accordion >
                            {operationRows}
                        </Accordion>
                    </td>
                </tr>

                </tbody>
            </Table>
        );
    }

}
function mapStateToProps(state) {
    return {
         assemblies: state.simulation.assemblies
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AssemblyAccordinPanel);


// export default AssemblyAccordinPanel;