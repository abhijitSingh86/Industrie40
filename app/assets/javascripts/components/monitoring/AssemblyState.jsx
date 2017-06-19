import React from "react"
import AssemblyAccordinPanel from './AssemblyAccordinPanel'
import {Panel, PanelGroup, Accordion, Tab, Row, Col, Nav, NavItem} from 'react-bootstrap'

class AssemblyState extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            openAccordinIndex: -1,
            data: this.props.data,
            activeKey: '1',
            completed: []
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
        this.setState({
            activeKey: -1
        });
    }

    getActiveKey() {
        return this.state.activeKey;
    }




    createTabPanes(ele, index) {
        let bindedFunction = this.setEnterId.bind(this, index);
        let removeFun = this.removeEnterId.bind(this, index);
        return (
            <Tab.Pane eventKey={index + 1} onEnter={bindedFunction} onExit={removeFun}>
                                <pre> {JSON.stringify(ele)}</pre>
                <AssemblyAccordinPanel data={ele}/>
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

module.exports = AssemblyState