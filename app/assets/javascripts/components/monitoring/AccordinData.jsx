import React from "react"

class AccordinData extends React.Component{
    constructor(props){
        super(props);
        this.toggleContent =this.toggleContent.bind(this);
        this.getHeight = this.getHeight.bind(this);
    }

    toggleContent(){
    this.props.toggleOne(this.props.key)
    }

    getHeight(){
        if(this.props.open){
            return "3em"
        } else {
            return "0"
        }
    }
    render(){
        var style = { height: this.getHeight() }
        return (
            <div className={"section section" + this.props.key}>
                <h2 className="sectionTitle" onClick={this.toggleContent} >{this.props.data.title}</h2>
                <p className="sectionContent" style={style} >{this.props.data.content}</p>
            </div>
        );
    }
}

module.exports = AccordinData