import React from 'react'
import ReactDom from 'react-dom'

class TransportTime extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            json:this.props.fieldValues,
            assemblyTT:this.props.fieldValues.assemblyTT,
            componentTT:this.props.fieldValues.componentTT
        }
        this.nextStep = this.nextStep.bind(this);
        this.handleTransportInputChangeAssembly = this.handleTransportInputChangeAssembly.bind(this);
        this.handleTransportInputChangeComponent = this.handleTransportInputChangeComponent.bind(this);
    }

    handleTransportInputChangeAssembly(assembyId1,assemblyId2,e){
        if(!isNaN(e.target.value) && e.target.value != ""){
            var json = {assembly1: assembyId1, assembly2:assemblyId2 ,transportTime:parseInt(e.target.value)};
            var assemblyTT = this.state.assemblyTT
            var tempTT = [];
            var flag=false;
            var i=0;
            for(var i=0;i<assemblyTT.length;i++){
                if(assemblyTT[i].assembly1 === assembyId1){
                    tempTT[i] = json
                    flag=true;
                }else{
                    tempTT[i]= assemblyTT[i];
                }
            }

            if(!flag){
                tempTT[i] = json
            }

            this.setState({
                assemblyTT:tempTT
            });

        }else{
            e.target.value = 0
        }
        console.log("e"+e.target.value);
    }

    handleTransportInputChangeComponent(assembyId,componentId,e){
        if(!isNaN(e.target.value) && e.target.value != ""){
            var json = {assembly: assembyId , component:componentId ,transportTime:parseInt(e.target.value)};
            var assemblyTT = this.state.componentTT
            var tempTT = [];
            var flag=false;
            var i=0;
            for(i=0;i<assemblyTT.length;i++){
                if(assemblyTT[i].assembly === assembyId){
                        tempTT[i] = json;
                    flag=true;
                }else{
                    tempTT[i]= assemblyTT[i];
                }
            }

            if(!flag){
                tempTT[i] = json
            }

            this.setState({
                componentTT:tempTT
            });

        }else{
            e.target.value = 0
        }



        console.log("e"+e.target.value);
        console.log("assem"+assembyId);
        console.log("comps"+componentId);


    }

    createAssemblyAssemblyTable() {
        var _this=this
        var i=0;
        var assem = this.state.json.assemblies.map(function(s,j){
            var __this = _this;
            var temp = _this.state.json.assemblies.map(function(comp,i){

                if(i >= j){
                    return <td></td>;
                }
                let change = __this.handleTransportInputChangeAssembly.bind("",s.id,comp.id)

                return <td><input id="{s.id}{comp.id}" name="{comp.name}" onChange={change} defaultValue="0" style={{width:50+'px'}}/></td>
            })
            return <tr><td>{s.name}</td>{temp}</tr>;
        });

        var data = this.state.json.assemblies.map(function(s){
            return <th>{s.name}</th>;
        });
        var head =<table><thead><th></th>{data}</thead><tbody>{assem}</tbody></table>
        return head;
    }

    createComponentAssemblyTable() {
        var _this=this
        var assem = this.state.json.assemblies.map(function(s){
            var __this = _this;
            var temp = _this.state.json.components.map(function(comp){
                let change = __this.handleTransportInputChangeComponent.bind("",s.id,comp.id)

                return <td><input id="{s.id}{comp.id}" name="{comp.name}" onChange={change} defaultValue="0" style={{width:50+'px'}}/></td>
            })
            return <tr><td>{s.name}</td>{temp}</tr>;
        });

        var data = this.state.json.components.map(function(s){
            return <th>{s.name}</th>;
        });
        var head =<table><thead><th></th>{data}</thead><tbody>{assem}</tbody></table>
        return head;
    }

    nextStep(){
        //save logic
        var data = {
            assemblyTT : this.state.assemblyTT
            ,componentTT:this.state.componentTT
        }
        this.props.saveValues(data)
        this.props.nextStep()
    }

    render(){
        var dataA2C = this.createComponentAssemblyTable();
        var dataA2A = this.createAssemblyAssemblyTable();
        return (
            <div>
                {dataA2C}
                {dataA2A}

                <li className="form-footer">
                    <button className="btn -default pull-center" onClick={this.props.previousStep}>Back</button>
                    <button className="btn -primary pull-right" onClick={this.nextStep}>Save &amp; Continue</button>
                </li>
            </div>
        )
    }
}

module.exports = TransportTime