import React from "react"
import AccordinData from './AccordinData'
import {Panel, PanelGroup, Accordion, Tab, Row, Col, Nav, NavItem} from 'react-bootstrap'


class ComponentState extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            openAccordinIndex: -1,
            activeKey: '1'
        };

        this.getActiveKey = this.getActiveKey.bind(this);
        this.buildCustomTabPanels = this.buildCustomTabPanels.bind(this);
        this.createTabPanes = this.createTabPanes.bind(this);
        this.createNavs = this.createNavs.bind(this);
    }

    setEnterId(id) {
        this.setState({
            activeKey: (id + 1)
        });
    }

    removeEnterId(id) {
        // console.log("remomving" + id);
        this.setState({
            activeKey: -1
        });
    }

    getActiveKey() {
        return this.state.activeKey;
    }

    // componentWillReceiveProps(nextProps) {
    //     this.setState({
    //         activeKey:nextProps.activeKey
    //     })
    // }

    getPanelTitle(ele) {
        var st = this.props.completedComponents;
        for (var i = 0; i < st.length; i++) {
            if (st[i].cmpId === ele.id) {
                return ele.name + "  :COMPLETED:  Time Taken" + st[i].time;
            }
        }
        return ele.name;
    }

    createTabPanes(ele, index) {
        let bindedFunction = this.setEnterId.bind(this, index);
        let removeFun = this.removeEnterId.bind(this, index);
        return (
            <Tab.Pane eventKey={index + 1} onEnter={bindedFunction} onExit={removeFun}>
                <AccordinData data={ele} simulationId={this.props.simulationId} index={index + 1}
                              activeKey={this.state.activeKey}
                              />
            </Tab.Pane>
        );
    }

    createNavs(ele, index) {
        return (<NavItem eventKey={index + 1}>
            {this.getPanelTitle(ele)}
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


export default ComponentState;
