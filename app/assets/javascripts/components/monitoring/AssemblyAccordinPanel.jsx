import React from 'react';
import {Table ,Panel,Accordion} from 'react-bootstrap';


const OperationRow = (operation)=>{

    var header = `${operation.name} status:${operation.id} `;
    var data = 'No Processing Details Found';


    return (
            <Panel header={header} eventKey={operation.id} >
                {data}
            </Panel>
    );
}

class AssemblyAccordinPanel extends React.Component{

    constructor(props){
        super(props);

    }

    render(){

        var operationRows = this.props.data.operationDetails.map(x=>OperationRow(x));

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
                    <td colspan="2">
                        Assembly Operations
                    </td>

                </tr>
                <tr>
                    <td colspan="2">
                        <Accordion>
                            {operationRows}
                        </Accordion>
                    </td>
                </tr>

                </tbody>
            </Table>
        );
    }

}



export default AssemblyAccordinPanel;