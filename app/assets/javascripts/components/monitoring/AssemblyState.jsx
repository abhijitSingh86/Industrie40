import React from "react"
import AssemblyAccordinPanel from './AssemblyAccordinPanel'
import {Panel, PanelGroup, Accordion, Tab, Row, Col, Nav, NavItem} from 'react-bootstrap'

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as Actions from '../redux/actions';


class AssemblyState extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            openAccordinIndex: -1,
            data: this.props.data,
            activeKey: -1,
            completed: []
        };

        // this.updateComponentState = this.updateComponentState.bind(this);
        // this.startTimer = this.startTimer.bind(this);
        // this.stopTimer = this.stopTimer.bind(this);
        this.getActiveKey = this.getActiveKey.bind(this);
        this.buildCustomTabPanels = this.buildCustomTabPanels.bind(this);
        this.createTabPanes = this.createTabPanes.bind(this);
        this.createNavs = this.createNavs.bind(this);
    }

    setEnterId(id) {
        console.log("AssemblyState"+id);
        this.setState({
            activeKey: (id)
        });
    }

    removeEnterId(id) {
        console.log("AssemblyState removving"+id);

        this.setState({
            activeKey: -1
        });
    }


    getActiveKey() {
        return this.state.activeKey;
    }

    // updateComponentState() {
    //     if(-1 != this.state.activeKey)
    //         this.props.actions.getAssemblyRunningStatus(this.state.activeKey, this.props.simulationId);
    // }
    //
    // componentDidMount(){
    //     this.startTimer();
    // }
    // componentWillUnmount(){
    //     this.stopTimer();
    // }
    // startTimer() {
    //     clearInterval(this.timer);
    //     this.updateComponentState();
    //     this.timer = setInterval(this.updateComponentState.bind(this), 7000)
    // }
    //
    // stopTimer() {
    //     clearInterval(this.timer)
    // }


    createTabPanes(ele, index) {
        let bindedFunction = this.setEnterId.bind(this, ele.id);
        let removeFun = this.removeEnterId.bind(this, ele.id);
        return (
            <Tab.Pane eventKey={index + 1} onEnter={bindedFunction} onExit={removeFun}>
                                <pre> {JSON.stringify(ele)}</pre>
                <AssemblyAccordinPanel data={ele} simulationId={this.props.simulationId}/>
            </Tab.Pane>
        );
    }

    createNavs(ele, index) {
        return (<NavItem eventKey={index + 1}>
            {ele.name}
        </NavItem>);
    }

    buildCustomTabPanels() {
        var colNav = []
        var colData = []

        var navs = this.props.data.map(this.createNavs)
        var tabpanes = this.props.data.map(this.createTabPanes)
        colNav.push(<Col sm={4}>
            <Nav bsStyle="pills" stacked>
                {navs}
            </Nav>
        </Col>);

        colData.push(<Col sm={8}>
            <Tab.Content animation>
                {tabpanes}
            </Tab.Content>
        </Col>);

        var cols = [];
        cols.push(colNav);
        cols.push(colData);
        return cols;
    }

    render() {
        return (
            <Tab.Container id="left-tabs-example" defaultActiveKey="first">
                <Row className="clearfix">
                    { this.buildCustomTabPanels()}
                </Row>
            </Tab.Container>
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

export default connect(mapStateToProps, mapDispatchToProps)(AssemblyState);
// module.exports = AssemblyState