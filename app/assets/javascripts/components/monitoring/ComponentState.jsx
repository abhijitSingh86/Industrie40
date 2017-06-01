

import React from "react"

class ComponentState extends React.Component{
    constructor(props){
        super(props);
        this.state =  { openAccordinIndex: -1 };

    }
    buildSections(sectionList){
        var sections = sectionList.map(this.buildSection)
        return sections;
    }
    buildSection(section, index){
        var openStatus = (index === this.state.openAccordinIndex);
        return <AccordinData key={index} data={section} toggleOne={this.toggleOne} open={openStatus} />
    }

    toggleOne(id){
        if(this.state.openAccordinIndex === id){
            this.setState({openAccordinIndex: -1});
        } else {
            this.setState({openAccordinIndex: id});
        }
    }

    render(){
        var sections = this.buildSections(this.props.data);
        return (
            <div className="container">
                {sections}
            </div>
        );
    }

}

module.exports = ComponentState