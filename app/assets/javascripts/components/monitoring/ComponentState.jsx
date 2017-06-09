import React from "react"
import AccordinData from './AccordinData'
import {Panel, PanelGroup,Accordion} from 'react-bootstrap'

class ComponentState extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            openAccordinIndex: -1,
            data: this.props.data,
            activeKey: '1'
        };

        // this.toggleOne = this.toggleOne.bind(this);
        this.buildAccosrdin = this.buildAccosrdin.bind(this);
        this.buildSections = this.buildSections.bind(this);
        this.handleSelect = this.handleSelect.bind(this);
        this.getStyle = this.getStyle.bind(this);
        this.getActiveKey = this.getActiveKey.bind(this);
        this.updateComponentCompletionState = this.updateComponentCompletionState.bind(this);
    }

    handleSelect(activeKey) {
        this.setState({activeKey});
    }

    buildSections(sectionList) {
        var sections = sectionList.map(this.buildAccosrdin)
        return sections;
    }

    getStyle(){
        return "danger"
    }


    setEnterId(id){
        console.log(id);
        // console.log(this.state);
        this.setState({
            activeKey:(id+1)
        });
    }

    removeEnterId(id){
        console.log("remomving"+ id);
        this.setState({
            activeKey:-1
        });
    }

    getActiveKey(){
        return this.state.activeKey;
    }

    updateComponentCompletionState(componentId,totalCompletionTime){
        if(this.state.completed == undefined){
            this.setState({completed:[]});
        }else {

            var st = this.state.completed
            st.push({
                cmpId: componentId,
                time: totalCompletionTime
            })
            this.setState({
                completed: st
            })
        }
    }

    getPanelTitle(ele){
        if(this.state.completed == undefined) {
            this.setState({completed: []});
        }
         else{   var st = this.state.completed;

            for (var i = 0; i < st.length; i++) {
                if (st[i].cmpId === ele.id) {
                    return ele.name + "  :COMPLETED:  Time Taken" + st[i].time;
                }
            }
        }
        return ele.name;

    }

    buildAccosrdin(ele, index) {
        let bindedFunction = this.setEnterId.bind(this,index);
        let removeFun = this.removeEnterId.bind(this,index);
        return (<Panel header = {this.getPanelTitle(ele)} eventKey={index+1} bsStyle={this.getStyle()} onEnter={bindedFunction} onExit={removeFun}>
                    <AccordinData data={ele} simulationId={this.props.simulationId} index={index+1} activeKey={this.state.activeKey}
                    onComplete={this.updateComponentCompletionState}/>
               </Panel>);
    }


    render() {
        var sections = this.buildSections(this.props.data);
        return (

                <Accordion >
                    {sections}
                </Accordion>
            
        );
    }

}

module.exports = ComponentState