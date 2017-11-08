

import React from 'react';
import SimulationForm from './SimulationForm'
import Components  from  './Components'
import Assembly from './Assembly'
import Success       from './Success'
import OperationForm  from './OperationForm'
import TransportTime from './TransportTime'
import {connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as Actions from './redux/actions';

class Registration extends React.Component{

  constructor(props){
    super(props);
  }




  showStep() {
    switch (this.props.step) {
      case 0:
        return <SimulationForm />
      case 1:
        return <OperationForm />
      case 2:
        return <Components />
      case 3:
        return <Assembly />
        case 4:
          return <TransportTime />
        case 5:
        return <Success/>
    }
  }

  render() {
    var style = {
      width : (this.props.step / 5 * 100) + '%'
    }

    return (
      <div>
        <span className="progress-step">Step {this.props.step}</span>
        <progress className="progress" style={style}></progress>
        {this.showStep()}
      </div>
    )
  }
}


function mapStateToProps(state) {
    return {
       step:state.registration.step
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(Actions, dispatch)
    };
}


export default connect(mapStateToProps,mapDispatchToProps)(Registration);
